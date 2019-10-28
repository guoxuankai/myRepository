package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.PlatformAccount;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.orderRule.OrderRule;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleSort;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleWithBLOBs;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.OrderInvDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseInventory;
import com.rondaful.cloud.order.enums.LogisticsStrategyCovertToLogisticsLogisticsType;
import com.rondaful.cloud.order.mapper.OrderRuleMailMapper;
import com.rondaful.cloud.order.mapper.OrderRuleWarehouseMapper;
import com.rondaful.cloud.order.model.dto.remoteseller.GetByplatformSkuAndSiteVO;
import com.rondaful.cloud.order.model.dto.syncorder.OrderWarehouseMappingRules;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.RemoteLogisticsService;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.service.IOrderRuleService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.MappingOrderRuleUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderRuleServiceImpl implements IOrderRuleService {

    private final OrderRuleMailMapper orderRuleMailMapper;

    private final OrderRuleWarehouseMapper orderRuleWarehouseMapper;

    private final RemoteSupplierService remoteSupplierService;

    private final OrderMessageSender orderMessageSender;

    private final RemoteLogisticsService remoteLogisticsService;

    private final ISysOrderLogService sysOrderLogService;

    private final Logger logger = LoggerFactory.getLogger(OrderRuleServiceImpl.class);

    private ISystemOrderCommonService systemOrderCommonService;

    private static final String ORDER_RULE_PUBLIC = "public";
    private static final String ORDER_RULE_SELLER = "seller";

    @Autowired
    public OrderRuleServiceImpl(OrderRuleMailMapper orderRuleMailMapper, OrderRuleWarehouseMapper orderRuleWarehouseMapper,
                                RemoteSupplierService remoteSupplierService, OrderMessageSender orderMessageSender,
                                RemoteLogisticsService remoteLogisticsService, ISysOrderLogService sysOrderLogService,
                                ISystemOrderCommonService systemOrderCommonService) {
        this.orderRuleMailMapper = orderRuleMailMapper;
        this.orderRuleWarehouseMapper = orderRuleWarehouseMapper;
        this.remoteSupplierService = remoteSupplierService;
        this.orderMessageSender = orderMessageSender;
        this.remoteLogisticsService = remoteLogisticsService;
        this.sysOrderLogService = sysOrderLogService;
        this.systemOrderCommonService = systemOrderCommonService;
    }


    @Override
    public String insertMail(OrderRuleWithBLOBs rule) {
        objectToJsonString(rule);
        rule.setId(null);
        rule.setPriority(orderRuleMailMapper.selectCount(rule) + 1);
        orderRuleMailMapper.insertSelective(rule);
        return String.valueOf(rule.getId());
    }

    @Override
    public String insertWarehouse(OrderRuleWithBLOBs rule) {
        objectToJsonString(rule);
        rule.setId(null);
        rule.setPriority(orderRuleWarehouseMapper.selectCount(rule) + 1);
        orderRuleWarehouseMapper.insertSelective(rule);
        return String.valueOf(rule.getId());
    }

    @Override
    public void updateMail(OrderRuleWithBLOBs rule) {
        objectToJsonString(rule);
        if (rule.getId() != null)
            orderRuleMailMapper.updateByPrimaryKeySelective2(rule);
    }

    @Override
    public void updateWarehouse(OrderRuleWithBLOBs rule) {
        objectToJsonString(rule);
        if (rule.getId() != null)
            orderRuleWarehouseMapper.updateByPrimaryKeySelective2(rule);
    }

    @Override
    public void updateMailStatus(OrderRuleWithBLOBs rule) {
        if (rule.getId() != null)
            orderRuleMailMapper.updateByPrimaryKeySelective(rule);
    }

    @Override
    public void updateWarehouseStatus(OrderRuleWithBLOBs rule) {
        if (rule.getId() != null)
            orderRuleWarehouseMapper.updateByPrimaryKeySelective(rule);
    }

    @Override
    public void swopPriority(OrderRuleSort swop, String type) {
        if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())) {
            this.swops(orderRuleMailMapper, swop);
        } else if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
            this.swops(orderRuleWarehouseMapper, swop);
        }
    }

    private boolean setParamOrderRule(OrderRuleWithBLOBs orderRuleWithBLOBs, OrderRule orderRule) {
        if (orderRuleWithBLOBs.getPlatformMark().equalsIgnoreCase(OrderRuleEnum.platformMark.CMS.getWay())) {
            orderRule.setPlatformMark(OrderRuleEnum.platformMark.CMS.getWay());
        } else if (orderRuleWithBLOBs.getPlatformMark().equalsIgnoreCase(OrderRuleEnum.platformMark.SELLER.getWay())) {
            orderRule.setPlatformMark(OrderRuleEnum.platformMark.SELLER.getWay());
            orderRule.setSellerId(orderRuleWithBLOBs.getSellerId());
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void topOrTailPriority(String type, OrderRuleSort sort) {
        OrderRule orderRule = new OrderRule();
        if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
            OrderRuleWithBLOBs orderRuleWithBLOBs = orderRuleWarehouseMapper.selectByPrimaryKey(sort.getId());
            if (this.setParamOrderRule(orderRuleWithBLOBs, orderRule)) {
                this.topOrTail(sort, orderRuleWarehouseMapper, this.warehouseList(orderRule));
            }
        } else if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())) {
            OrderRuleWithBLOBs orderRuleWithBLOBs = orderRuleMailMapper.selectByPrimaryKey(sort.getId());
            if (this.setParamOrderRule(orderRuleWithBLOBs, orderRule)) {
                this.topOrTail(sort, orderRuleMailMapper, this.mailList(orderRule));
            }
        }
    }

    @Override
    public void upOrDownPriority(String type, OrderRuleSort sort) {
        OrderRule orderRule = new OrderRule();
        if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
            OrderRuleWithBLOBs orderRuleWithBLOBs = orderRuleWarehouseMapper.selectByPrimaryKey(sort.getId());
            if (this.setParamOrderRule(orderRuleWithBLOBs, orderRule)) {
                this.upOrDown(sort, orderRuleWarehouseMapper, this.warehouseList(orderRule));
            }
        } else if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())) {
            OrderRuleWithBLOBs orderRuleWithBLOBs = orderRuleMailMapper.selectByPrimaryKey(sort.getId());
            if (this.setParamOrderRule(orderRuleWithBLOBs, orderRule)) {
                this.upOrDown(sort, orderRuleMailMapper, this.mailList(orderRule));
            }
        }
    }

    /**
     * 上移或者下移优先级
     *
     * @param sort       排序对象
     * @param baseMapper 使用mapper
     * @param ruleList   数据列表
     */
    private void upOrDown(OrderRuleSort sort, BaseMapper<OrderRuleWithBLOBs> baseMapper,
                          List<OrderRuleWithBLOBs> ruleList) {
        int index = 0;
        for (OrderRuleWithBLOBs rule : ruleList) {          //检索id的排序
            if (rule.getId().equals(sort.getId())) {
                break;
            }
            index++;
        }
        if (sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TOP.getWay())) {   //上移
            if (index == 0)
                return;
            int priority = 1;
            OrderRuleWithBLOBs orderRuleWithBLOBs = new OrderRuleWithBLOBs();
            for (int i = 0; i < ruleList.size(); i++, priority++) {
                orderRuleWithBLOBs.setId(ruleList.get(i).getId());
                if (i == (index - 1)) {
                    orderRuleWithBLOBs.setPriority(priority + 1);
                } else if (i == index) {
                    orderRuleWithBLOBs.setPriority(priority - 1);
                } else {
                    orderRuleWithBLOBs.setPriority(priority);
                }
                baseMapper.updateByPrimaryKeySelective(orderRuleWithBLOBs);
            }
        } else if (sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TAIL.getWay())) {  //下移
            if (index == (ruleList.size() - 1))
                return;
            int priority = 1;
            OrderRuleWithBLOBs orderRuleWithBLOBs = new OrderRuleWithBLOBs();
            for (int i = 0; i < ruleList.size(); i++, priority++) {
                orderRuleWithBLOBs.setId(ruleList.get(i).getId());
                if (i == (index + 1)) {
                    orderRuleWithBLOBs.setPriority(priority - 1);
                } else if (i == index) {
                    orderRuleWithBLOBs.setPriority(priority + 1);
                } else {
                    orderRuleWithBLOBs.setPriority(priority);
                }
                baseMapper.updateByPrimaryKeySelective(orderRuleWithBLOBs);
            }
        }
    }

    /**
     * 置顶或者置底的逻辑操作
     *
     * @param sort       排序对象
     * @param baseMapper mapper
     */
    private void topOrTail(OrderRuleSort sort, BaseMapper<OrderRuleWithBLOBs> baseMapper,
                           List<OrderRuleWithBLOBs> ruleList) {
        OrderRuleWithBLOBs orderRuleWithBLOBs = new OrderRuleWithBLOBs();
        if (sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TOP.getWay())) {
            Integer num = 1;
            orderRuleWithBLOBs.setId(sort.getId());
            orderRuleWithBLOBs.setPriority(num++);
            baseMapper.updateByPrimaryKeySelective(orderRuleWithBLOBs);
            this.changePriority(baseMapper, ruleList, sort.getId(), num);
        } else if (sort.getWay().equalsIgnoreCase(OrderRuleEnum.prioritySortWay.TO_TAIL.getWay())) {
            Integer num = 1;
            num = this.changePriority(baseMapper, ruleList, sort.getId(), num);
            orderRuleWithBLOBs.setId(sort.getId());
            orderRuleWithBLOBs.setPriority(num);
            baseMapper.updateByPrimaryKeySelective(orderRuleWithBLOBs);
        }
    }

    /**
     * 排序的数据库轮训更新操作
     *
     * @param baseMapper mapper
     * @param ruleList   规则列表
     * @param id         被置顶或者置底的id
     * @param num        初始排序值
     * @return 结束后的排序值
     */
    private Integer changePriority(BaseMapper<OrderRuleWithBLOBs> baseMapper,
                                   List<OrderRuleWithBLOBs> ruleList, Long id, Integer num) {
        OrderRuleWithBLOBs orderRuleWithBLOBs = new OrderRuleWithBLOBs();
        for (OrderRuleWithBLOBs r : ruleList) {
            if (!r.getId().equals(id)) {
                orderRuleWithBLOBs.setId(r.getId());
                orderRuleWithBLOBs.setPriority(num++);
                baseMapper.updateByPrimaryKeySelective(orderRuleWithBLOBs);
            }
        }
        return num;
    }

    /**
     * 交换两个规则的优先级的数据库操作
     *
     * @param baseMapper mapper
     * @param swop       派逊对象
     */
    private void swops(BaseMapper<OrderRuleWithBLOBs> baseMapper, OrderRuleSort swop) {
        OrderRuleWithBLOBs rule = new OrderRuleWithBLOBs();
        rule.setId(swop.getId1());
        rule.setPriority(swop.getPriority1());
        baseMapper.updateByPrimaryKeySelective(rule);
        rule.setId(swop.getId2());
        rule.setPriority(swop.getPriority2());
        baseMapper.updateByPrimaryKeySelective(rule);
    }

    @Override
    public List<OrderRuleWithBLOBs> mailList(OrderRule rule) {
        List<OrderRuleWithBLOBs> all = orderRuleMailMapper.findAll(rule);
        this.checkEffectiveEndTimeByList(all, orderRuleMailMapper);
        return all;
    }

    @Override
    public List<OrderRuleWithBLOBs> warehouseList(OrderRule rule) {
        List<OrderRuleWithBLOBs> all = orderRuleWarehouseMapper.findAll(rule);
        this.checkEffectiveEndTimeByList(all, orderRuleWarehouseMapper);
        return all;
    }

    @Override
    public void deleteRule(Long id, String type) {
        if (id != null && StringUtils.isNotBlank(type)) {
            if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType()))
                orderRuleMailMapper.deleteByPrimaryKey(id);
            else if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType()))
                orderRuleWarehouseMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public OrderRuleWithBLOBs selectByPrimaryKey(Long id, String type) {
        if (id != null && StringUtils.isNotBlank(type)) {
            OrderRuleWithBLOBs rule = null;
            if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())) {
                rule = orderRuleMailMapper.selectByPrimaryKey(id);
                this.checkEffectiveEndTime(rule, orderRuleMailMapper);
            } else if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
                rule = orderRuleWarehouseMapper.selectByPrimaryKey(id);
                this.checkEffectiveEndTime(rule, orderRuleWarehouseMapper);
            }
            jsonStringToObject(rule);
            return rule;
        }
        return null;
    }

    /**
     * 当存在有效时间下限时  此时如果时间已经过期 并且规则还是正常状态 便将状态改为停用并且更新数据库
     *
     * @param rules      规则列表
     * @param baseMapper 操作mapper
     */
    private void checkEffectiveEndTimeByList(List<OrderRuleWithBLOBs> rules, BaseMapper<OrderRuleWithBLOBs> baseMapper) {
        rules.forEach(rule -> this.checkEffectiveEndTime(rule, baseMapper));
    }


    /**
     * 当存在有效时间下限时  此时如果时间已经过期 并且规则还是正常状态 便将状态改为停用并且更新数据库
     *
     * @param rule       规则对象
     * @param baseMapper 操作的mapper
     */
    private void checkEffectiveEndTime(OrderRuleWithBLOBs rule, BaseMapper<OrderRuleWithBLOBs> baseMapper) {
        if (StringUtils.isNotBlank(rule.getEffectiveEndTime())) {
            if (rule.getStatus() == 1) {
                if (new Date().after(DateUtils.strToDate(rule.getEffectiveEndTime(), DateUtils.FORMAT_2))) {
                    rule.setStatus(2);
                    baseMapper.updateByPrimaryKeySelective(new OrderRuleWithBLOBs() {{
                        setId(rule.getId());
                        setStatus(2);
                    }});
                }
            }
        }
    }


    /**
     * 将订单规则中的 代表条件的 list对象转换为 json字符串
     *
     * @param rule 订单规则对象
     */
    private void objectToJsonString(OrderRuleWithBLOBs rule) {
        if (rule == null)
            return;
        if (rule.getDeliveryWarehouseIds() != null && rule.getDeliveryWarehouseIds().size() > 0) {
            rule.setDeliveryWarehouseIdList(FastJsonUtils.toJsonString(rule.getDeliveryWarehouseIds()));
        }else{
            rule.setDeliveryWarehouseIdList("");
        }
        boolean flag = false;
        for (PlatformAccount account : rule.getPlatformAccounts()) {
            if (CollectionUtils.isNotEmpty(account.getAccounts())) {
                flag=true;
            }
        }
        if (flag) {
            rule.setSellerAccountList(FastJsonUtils.toJsonString(rule.getPlatformAccounts()));
        }else{
            rule.setSellerAccountList("");
        }
        if (rule.getReceiveGoodsCountrys() != null && rule.getReceiveGoodsCountrys().size() > 0) {
            rule.setReceiveGoodsCountryList(FastJsonUtils.toJsonString(rule.getReceiveGoodsCountrys()));
        }else{
            rule.setReceiveGoodsCountryList("");
        }
        if (rule.getReceiveGoodsZipCodes() != null && rule.getReceiveGoodsZipCodes().size() > 0) {
            rule.setReceiveGoodsZipCodeList(FastJsonUtils.toJsonString(rule.getReceiveGoodsZipCodes()));
        }else{
            rule.setReceiveGoodsZipCodeList("");
        }
        if (rule.getPlSkus() != null && rule.getPlSkus().size() > 0) {
            rule.setPlSkuList(FastJsonUtils.toJsonString(rule.getPlSkus()));
        }else{
            rule.setPlSkuList("");
        }
    }

    /**
     * 将订单规则中的 json字符串 转换成代表条件的list对象
     *
     * @param rule 订单规则对象
     */
    public void jsonStringToObject(OrderRuleWithBLOBs rule) {
        if (rule == null)
            return;
        if (StringUtils.isNotBlank(rule.getDeliveryWarehouseIdList())) {
            rule.setDeliveryWarehouseIds(JSONObject.parseArray(rule.getDeliveryWarehouseIdList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getDeliveryWarehouseCodeList())) {
            rule.setDeliveryWarehouseIds(JSONObject.parseArray(rule.getDeliveryWarehouseCodeList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getSellerAccountList())) {
            rule.setPlatformAccounts(JSONObject.parseArray(rule.getSellerAccountList(), PlatformAccount.class));
        }
        if (StringUtils.isNotBlank(rule.getReceiveGoodsCountryList())) {
            rule.setReceiveGoodsCountrys(JSONObject.parseArray(rule.getReceiveGoodsCountryList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getReceiveGoodsZipCodeList())) {
            rule.setReceiveGoodsZipCodes(JSONObject.parseArray(rule.getReceiveGoodsZipCodeList(), String.class));
        }
        if (StringUtils.isNotBlank(rule.getPlSkuList())) {
            rule.setPlSkus(JSONObject.parseArray(rule.getPlSkuList(), String.class));
        }
    }

    @Override
    public void discardOrderRule(String deliveryWarehouseId, String mailTypeId) {
        if (StringUtils.isNotBlank(deliveryWarehouseId) && StringUtils.isNotBlank(mailTypeId)) {
            this.discardOrderRuleByWarehouseIdAndMailId(deliveryWarehouseId, mailTypeId);
        } else if (StringUtils.isNotBlank(deliveryWarehouseId)) {
            this.discardOrderRuleByWarehouseId(deliveryWarehouseId);
        } else if (StringUtils.isNotBlank(mailTypeId))
            this.discardOrderRuleByMailTypeId(mailTypeId);
    }

    /**
     * 废弃以指定发货方式作为结果 以指定仓库作为条件的订单规则
     *
     * @param deliveryWarehouseId 发货仓库id
     * @param mailTypeId          发货方式id
     */
    private void discardOrderRuleByWarehouseIdAndMailId(String deliveryWarehouseId, String mailTypeId) {
        OrderRule orderRule = new OrderRule();
        orderRule.setMailTypeCode(mailTypeId);
        orderRule.setStatus(1);
        List<OrderRuleWithBLOBs> orderRuleWithBLOBs = this.mailList(orderRule);
        OrderRuleWithBLOBs updateRule = new OrderRuleWithBLOBs();
        for (OrderRuleWithBLOBs rule : orderRuleWithBLOBs) {
            this.jsonStringToObject(rule);
            for (String wId : rule.getDeliveryWarehouseIds()) {
                if (wId.equalsIgnoreCase(deliveryWarehouseId)) {
                    updateRule.setId(rule.getId());
                    updateRule.setStatus(2);
                    this.updateMail(updateRule);
                }
            }
        }
    }

    /**
     * 根据发货仓库id废弃相关规则
     *
     * @param deliveryWarehouseId 相关仓库id
     */
    private void discardOrderRuleByWarehouseId(String deliveryWarehouseId) {
        OrderRule orderRule = new OrderRule();
        orderRule.setDeliveryWarehouseCode(deliveryWarehouseId);
        orderRule.setStatus(1);
        List<OrderRuleWithBLOBs> orderRuleWithBLOBs = this.warehouseList(orderRule);
        OrderRuleWithBLOBs updateRule = new OrderRuleWithBLOBs();
        orderRuleWithBLOBs.forEach(r -> {
            updateRule.setId(r.getId());
            updateRule.setStatus(2);
            this.updateWarehouse(updateRule);
        });
        orderRuleWithBLOBs = this.mailList(orderRule);
        orderRuleWithBLOBs.forEach(r -> {
            this.jsonStringToObject(r);
            r.getDeliveryWarehouseIds().forEach(d -> {
                if (d.equalsIgnoreCase(deliveryWarehouseId)) {
                    updateRule.setId(r.getId());
                    updateRule.setStatus(2);
                    this.updateMail(updateRule);
                }
            });
        });
    }

    /**
     * 根据邮寄方式id废弃相关规则
     *
     * @param mailTypeId 邮寄方式id
     */
    private void discardOrderRuleByMailTypeId(String mailTypeId) {
        OrderRule orderRule = new OrderRule();
        orderRule.setMailTypeCode(mailTypeId);
        orderRule.setStatus(1);
        List<OrderRuleWithBLOBs> orderRuleWithBLOBs = this.mailList(orderRule);
        OrderRuleWithBLOBs updateRule = new OrderRuleWithBLOBs();
        orderRuleWithBLOBs.forEach(r -> {
            updateRule.setId(r.getId());
            updateRule.setStatus(2);
            this.updateMail(updateRule);
        });
    }

    /**
     * 包裹匹配仓库物流规则
     *
     * @param platform           平台
     * @param sysOrderPackageDTO {@link SysOrderPackageDTO}
     * @param site               亚马逊平台才有，其余的传null
     */
    @Override
    public void mappingPackageOrderRule(String platform, SysOrderPackageDTO sysOrderPackageDTO, String site, Integer empowerId) {
        String sysOrderId = sysOrderPackageDTO.getSysOrderId();
        String packageNumber = sysOrderPackageDTO.getOrderTrackId();
        logger.info("订单{}规则匹配设置仓库和发货方式匹配开始=======传入参数:platform={} , sysOrderPackageDTO={}",
                sysOrderId, platform, FastJsonUtils.toJsonString(sysOrderPackageDTO));

        // 判断订单sku 是否有在同一个仓库
        List<OrderInvDTO> orderInventoryList;
        try {
            orderInventoryList = systemOrderCommonService.getMappingWarehouseBySkuList(sysOrderPackageDTO.getSkus());
            if (CollectionUtils.isEmpty(orderInventoryList)) {
                logger.info("订单{}, 包裹{} 中的商品没有匹配到仓库", sysOrderId, packageNumber);
                return;
            }
            logger.info("订单{}, 包裹{} 中的商品匹配到的仓库数量为{}", sysOrderId, packageNumber, orderInventoryList.size());

        } catch (Exception e) {
            logger.error("订单{}, 包裹{} 中的商品匹配仓库数据异常", sysOrderId, packageNumber, e);
            return;
        }
        List<GetByplatformSkuAndSiteVO> getByplatformSkuAndSiteVOList = new ArrayList<>();

        // 目前不支持other  和 wish订单调用该接口
        if (!Objects.equals(platform, OrderRuleEnum.platformEnm.OTHER.getPlatform())
                && !Objects.equals(platform, OrderRuleEnum.platformEnm.WISH.getPlatform())) {
            List<String> sourceSkus = sysOrderPackageDTO.getSysOrderPackageDetailList().stream()
                    .map(SysOrderPackageDetailDTO::getSourceSku).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(sourceSkus)) {
                logger.info("订单{}, 包裹{} 来源sku不为空，查询卖家的刊登信息", sysOrderId, packageNumber);

                boolean callSellerApi = true;
                for (String sourceSku : sourceSkus) {
                    if (StringUtils.isBlank(sourceSku)) {
                        callSellerApi = false;
                        logger.info("订单{}, 包裹{} 含有来源sku为空的信息，不查询卖家的刊登信息", sysOrderId, packageNumber);
                        break;
                    }
                }

                try {
                    if (callSellerApi) {
                        getByplatformSkuAndSiteVOList = systemOrderCommonService
                                .getByplatformSkuAndSite(sourceSkus, site, platform.toUpperCase(), empowerId);
                    }
                } catch (Exception e) {
                    logger.info("订单{}, 包裹{} 调用卖家，查询卖家刊登信息异常", sysOrderId, packageNumber, e);
                }
            }else {
                logger.info("订单{}, 包裹{} 来源sku为空，不查询卖家的刊登信息", sysOrderId, packageNumber);
            }

        }

        boolean mappingSeller = this.isMappingSeller(getByplatformSkuAndSiteVOList, orderInventoryList);

        if (mappingSeller) {
            this.mappingSeller(platform, sysOrderPackageDTO, getByplatformSkuAndSiteVOList, orderInventoryList);
        } else {
            this.mappingOrderRule(platform, sysOrderPackageDTO, orderInventoryList);
        }
    }



    private void mappingSeller(String platform, SysOrderPackageDTO sysOrderPackageDTO,
                               List<GetByplatformSkuAndSiteVO> getByplatformSkuAndSiteVOList,
                               List<OrderInvDTO> orderInventoryList) {
        // 匹配刊登   有仓库 校验该仓库是否有sku 无类型 默认 综合最优 调用物流接口
        //            有仓库 校验该仓库是否有sku 有类型 类型一致？ 否则不搞
        Set<Integer> warehouseIds = getByplatformSkuAndSiteVOList.stream().map(GetByplatformSkuAndSiteVO::getWarehouseId)
                .collect(Collectors.toSet());
        Integer[] temp = new Integer[warehouseIds.size()];
        Integer warehouseId = warehouseIds.toArray(temp)[0];

        // 仓库信息
        OrderInvDTO warehouse = null;
        for (OrderInvDTO orderInvDTO : orderInventoryList) {
            if (orderInvDTO.getWarehouseId().intValue() == warehouseId.intValue()) {
                warehouse = orderInvDTO;
                break;
            }
        }

        if (null == warehouse) {
            logger.info("没有匹配到刊登填写的仓库");
            return;
        }

        sysOrderPackageDTO.setDeliveryWarehouseId(warehouseId);
        sysOrderPackageDTO.setDeliveryWarehouse(warehouse.getWarehouseName());
        sysOrderPackageDTO.setDeliveryWarehouseCode(warehouse.getWarehouseCode());

        // 物流类型信息
        Set<Integer> logisticsTypes = getByplatformSkuAndSiteVOList.stream().map(GetByplatformSkuAndSiteVO::getLogisticsType)
                .collect(Collectors.toSet());

        if (logisticsTypes.size() == 1) {
            Integer[] logisticsTemp = new Integer[warehouseIds.size()];
            sysOrderPackageDTO.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType
                    .getLogisticsStrategyByLogisticsType(logisticsTypes.toArray(logisticsTemp)[0]));
        } else {
            sysOrderPackageDTO.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType
                    .INTEGRATED_OPTIMAL.getLogisticsStrategy());
        }
    }

    /**
     * 判断是否走刊登的仓库
     *
     * @param getByplatformSkuAndSiteVOList
     * @param orderInventoryList            sku所在的仓库列表
     * @return boolean
     */
    private boolean isMappingSeller(List<GetByplatformSkuAndSiteVO> getByplatformSkuAndSiteVOList,
                                    List<OrderInvDTO> orderInventoryList) {
        if (CollectionUtils.isEmpty(getByplatformSkuAndSiteVOList)) {
            return false;
        }

        Set<Integer> warehouseIds = getByplatformSkuAndSiteVOList.stream().map(GetByplatformSkuAndSiteVO::getWarehouseId)
                .collect(Collectors.toSet());
        if (warehouseIds.size() != 1) {
            return false;
        }
        Integer[] temp = new Integer[warehouseIds.size()];
        for (OrderInvDTO orderInvDTO : orderInventoryList) {
            if (orderInvDTO.getWarehouseId().intValue() == warehouseIds.toArray(temp)[0].intValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 匹配仓库物流匹配规则
     *
     * @param platform           平台
     * @param sysOrderPackageDTO 包裹信息
     * @param orderInventoryList sku所在仓库列表信息
     */
    private void mappingOrderRule(String platform, SysOrderPackageDTO sysOrderPackageDTO, List<OrderInvDTO> orderInventoryList) {
        String sysOrderId = sysOrderPackageDTO.getSysOrderId();
        String packageNumber = sysOrderPackageDTO.getOrderTrackId();
        logger.info("订单{}, 包裹{} 开始匹配物流仓库匹配规则", sysOrderId, packageNumber);
        //卖家平台
        String sellerPlAccount = sysOrderPackageDTO.getSellerPlAccount();
        OrderWarehouseMappingRules orderWarehouseMappingRules = this.getOrderWarehouseMappingRules(sellerPlAccount);
        List<OrderRuleWithBLOBs> sellerWarehouseRuleList = orderWarehouseMappingRules.getSellerWarehouseRuleList();
        List<OrderRuleWithBLOBs> sellerMailRuleList = orderWarehouseMappingRules.getSellerMailRuleList();
        logger.info("订单{}查询获得的卖家平台===仓库规则数量:{}, 邮寄规则数量:{}", sysOrderId,
                sellerWarehouseRuleList.size(), sellerMailRuleList.size());

        //管理后台
        //仓库规则列表
        List<OrderRuleWithBLOBs> cmsWarehouseRuleList = orderWarehouseMappingRules.getCmsWarehouseRuleList();
        //邮寄方式列表
        List<OrderRuleWithBLOBs> cmsMailRuleList = orderWarehouseMappingRules.getCmsMailRuleList();
        logger.info("订单{}查询获得的管理后台===仓库规则数量:{}, 邮寄规则数量:{}", sysOrderId,
                cmsWarehouseRuleList.size(), cmsMailRuleList.size());

        if (CollectionUtils.isEmpty(sellerWarehouseRuleList) && CollectionUtils.isEmpty(cmsWarehouseRuleList)) {
            logger.info("订单{}在卖家平台和管理后台均无仓库匹配规则", sysOrderId);
            return;
        }

        // 如果有同一个仓库， 则先匹配卖家仓库规则，后匹配管理后台仓库规则
        Map<String, String> mappedWarehouseMap = this.mappingWarehouseNew(sysOrderPackageDTO, platform, sellerWarehouseRuleList,
                cmsWarehouseRuleList, orderInventoryList);

        if (null == mappedWarehouseMap) {
            logger.info("订单{}, 包裹{} 无匹配到对应的仓库信息", sysOrderId, packageNumber);
            return;
        }

        String warehouseId = mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_ID);
        String warehouseType = mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_TYPE);
        this.setOrderPackageData(sysOrderPackageDTO, platform, mappedWarehouseMap, null);

        // 如果有匹配到仓库，则先匹配卖家邮寄方式规则，后匹配管理后台邮寄方式规则
        Map<String, String> mappedMailMap = this.mappingMailTypeNew(sysOrderPackageDTO, platform, sellerMailRuleList,
                cmsMailRuleList, warehouseId, warehouseType);
        if (null == mappedMailMap) {
            logger.info("订单{}无匹配到对应的物流方式信息", sysOrderId);
            return;
        }

        this.setOrderPackageData(sysOrderPackageDTO, platform, null, mappedMailMap);
    }

    /**
     * 获取仓库物流匹配规则列表
     *
     * @param sellerPlAccount sellerPlAccount
     * @return {@link OrderWarehouseMappingRules}
     */
    private OrderWarehouseMappingRules getOrderWarehouseMappingRules(String sellerPlAccount) {
        //卖家平台
        List<OrderRuleWithBLOBs> sellerWarehouseRuleList = new ArrayList<>();
        List<OrderRuleWithBLOBs> sellerMailRuleList = new ArrayList<>();
        if (StringUtils.isNotBlank(sellerPlAccount)) {
            OrderRule sellerParam = new OrderRule();
            sellerParam.setSellerAccount(sellerPlAccount);
            sellerParam.setStatus(1);
            sellerParam.setCreateTime(new Date());
            sellerParam.setPlatformMark("S");
            //仓库规则列表
            sellerWarehouseRuleList = orderRuleWarehouseMapper.findAll(sellerParam);
            //邮寄方式列表
            sellerMailRuleList = orderRuleMailMapper.findAll(sellerParam);
        }
        //管理后台
        OrderRule cmsParam = new OrderRule();
        cmsParam.setStatus(1);
        cmsParam.setCreateTime(new Date());
        cmsParam.setPlatformMark("G");
        //仓库规则列表
        List<OrderRuleWithBLOBs> cmsWarehouseRuleList = orderRuleWarehouseMapper.findAll(cmsParam);
        //邮寄方式列表
        List<OrderRuleWithBLOBs> cmsMailRuleList = orderRuleMailMapper.findAll(cmsParam);

        OrderWarehouseMappingRules orderWarehouseMappingRules = new OrderWarehouseMappingRules();
        orderWarehouseMappingRules.setSellerWarehouseRuleList(sellerWarehouseRuleList);
        orderWarehouseMappingRules.setSellerMailRuleList(sellerMailRuleList);
        orderWarehouseMappingRules.setCmsWarehouseRuleList(cmsWarehouseRuleList);
        orderWarehouseMappingRules.setCmsMailRuleList(cmsMailRuleList);

        return orderWarehouseMappingRules;
    }

    public void mappingOrderRuleNew(String platform, SysOrder order) {
        String sysOrderId = order.getSysOrderId();
        logger.info("订单{}规则匹配设置仓库和发货方式匹配开始=======传入参数:platform={} , order={}", sysOrderId, platform, order);
        // 1、判断订单sku 是否有在同一个仓库
        List<OrderInvDTO> orderInventoryList = new ArrayList<>();
        try {
            orderInventoryList = systemOrderCommonService.getMappingWarehouseBySkuList(order.getSkus());
            if (CollectionUtils.isEmpty(orderInventoryList)) {
                logger.info("订单{}商品没有匹配到仓库", sysOrderId);
                return;
            }
            logger.info("订单{}商品匹配到的仓库数量为{}", sysOrderId, orderInventoryList.size());

        } catch (Exception e) {
            logger.error("订单{}商品匹配仓库数据异常", sysOrderId, e);
            return;
        }
        // 2、如果有同一个仓库， 则先匹配卖家仓库规则，后匹配管理后台仓库规则
        //卖家平台
        List<OrderRuleWithBLOBs> sellerWarehouseRuleList = new ArrayList<>();
        List<OrderRuleWithBLOBs> sellerMailRuleList = new ArrayList<>();
        if (StringUtils.isNotBlank(order.getSellerPlAccount())) {
            OrderRule sellerParam = new OrderRule();
            sellerParam.setSellerAccount(order.getSellerPlAccount());
            sellerParam.setStatus(1);
            sellerParam.setCreateTime(new Date());
            sellerParam.setPlatformMark("S");
            //仓库规则列表
            sellerWarehouseRuleList = orderRuleWarehouseMapper.findAll(sellerParam);
            //邮寄方式列表
            sellerMailRuleList = orderRuleMailMapper.findAll(sellerParam);
            logger.info("订单{}查询获得的卖家平台===仓库规则数量:{}, 邮寄规则数量:{}", sysOrderId,
                    sellerWarehouseRuleList.size(), sellerMailRuleList.size());
        }
        //管理后台
        OrderRule cmsParam = new OrderRule();
        cmsParam.setStatus(1);
        cmsParam.setCreateTime(new Date());
        cmsParam.setPlatformMark("G");
        //仓库规则列表
        List<OrderRuleWithBLOBs> cmsWarehouseRuleList = orderRuleWarehouseMapper.findAll(cmsParam);
        //邮寄方式列表
        List<OrderRuleWithBLOBs> cmsMailRuleList = orderRuleMailMapper.findAll(cmsParam);
        logger.info("订单{}查询获得的管理后台===仓库规则数量:{}, 邮寄规则数量:{}", sysOrderId,
                cmsWarehouseRuleList.size(), cmsMailRuleList.size());

        if (CollectionUtils.isEmpty(sellerWarehouseRuleList) && CollectionUtils.isEmpty(cmsWarehouseRuleList)) {
            logger.info("订单{}在卖家平台和管理后台均无仓库匹配规则", sysOrderId);
        }

        Map<String, String> mappedWarehouseMap = this.mappingWarehouse(order, platform, sellerWarehouseRuleList,
                cmsWarehouseRuleList, orderInventoryList);

        if (null == mappedWarehouseMap) {
            logger.info("订单{}无匹配到对应的仓库信息", sysOrderId);
            return;
        }

        String warehouseId = mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_ID);
        String warehouseType = mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_TYPE);
        this.setOrderData(order, platform, mappedWarehouseMap, null);

        // 3、如果有匹配到仓库，则先匹配卖家邮寄方式规则，后匹配管理后台邮寄方式规则
        Map<String, String> mappedMailMap = this.mappingMailType(order, platform, sellerMailRuleList,
                cmsMailRuleList, warehouseId, warehouseType);
        if (null == mappedMailMap) {
            logger.info("订单{}无匹配到对应的物流方式信息", sysOrderId);
            return;
        }

        this.setOrderData(order, platform, null, mappedMailMap);
    }

    private void setOrderPackageData(SysOrderPackageDTO sysOrderPackageDTO, String platform,
                                     Map<String, String> mappedWarehouseMap, Map<String, String> mappedMailMap) {
        // 如果匹配到仓库规则
        if (null != mappedWarehouseMap && !mappedWarehouseMap.isEmpty()) {
            sysOrderPackageDTO.setDeliveryWarehouseCode(mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_CODE));
            sysOrderPackageDTO.setDeliveryWarehouse(mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_NAME));
            sysOrderPackageDTO.setDeliveryWarehouseId(Integer.valueOf(mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_ID)));
        }

        // 如果匹配到物流方式规则
        if (null != mappedMailMap && !mappedMailMap.isEmpty()) {
            sysOrderPackageDTO.setShippingCarrierUsedCode(mappedMailMap.get(MappingOrderRuleUtil.CARRIER_CODE));
            sysOrderPackageDTO.setShippingCarrierUsed(mappedMailMap.get(MappingOrderRuleUtil.CARRIER_NAME));
            sysOrderPackageDTO.setDeliveryMethod(mappedMailMap.get(MappingOrderRuleUtil.SHORT_NAME));
            sysOrderPackageDTO.setDeliveryMethodCode(mappedMailMap.get(MappingOrderRuleUtil.CODE));
            // 默认设置综合最优
            sysOrderPackageDTO.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType
                    .INTEGRATED_OPTIMAL.getLogisticsStrategy());
            if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())) {
                sysOrderPackageDTO.setAmazonCarrierName(mappedMailMap.get(MappingOrderRuleUtil.AMAZON_CARRIER));
                sysOrderPackageDTO.setAmazonShippingMethod(mappedMailMap.get(MappingOrderRuleUtil.AMAZON_CODE));
            } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())) {
                sysOrderPackageDTO.setEbayCarrierName(mappedMailMap.get(MappingOrderRuleUtil.EBAY_CARRIER));
            } else {
                sysOrderPackageDTO.setAmazonCarrierName(mappedMailMap.get(""));
                sysOrderPackageDTO.setAmazonShippingMethod(mappedMailMap.get(""));
                sysOrderPackageDTO.setEbayCarrierName(mappedMailMap.get(""));
            }
        }
    }

    private void setOrderData(SysOrder order, String platform, Map<String, String> mappedWarehouseMap,
                              Map<String, String> mappedMailMap) {
        // 如果匹配到仓库规则
        if (null != mappedWarehouseMap && !mappedWarehouseMap.isEmpty()) {
            order.setDeliveryWarehouseCode(mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_CODE));
            order.setDeliveryWarehouse(mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_NAME));
            order.setDeliveryWarehouseId(mappedWarehouseMap.get(MappingOrderRuleUtil.WAREHOUSE_ID));
        }

        // 如果匹配到物流方式规则
        if (null != mappedMailMap && !mappedMailMap.isEmpty()) {
            order.setShippingCarrierUsedCode(mappedMailMap.get(MappingOrderRuleUtil.CARRIER_CODE));
            order.setShippingCarrierUsed(mappedMailMap.get(MappingOrderRuleUtil.CARRIER_NAME));
            order.setDeliveryMethod(mappedMailMap.get(MappingOrderRuleUtil.SHORT_NAME));
            order.setDeliveryMethodCode(mappedMailMap.get(MappingOrderRuleUtil.CODE));

            if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())) {
                order.setAmazonCarrierName(mappedMailMap.get(MappingOrderRuleUtil.AMAZON_CARRIER));
                order.setAmazonShippingMethod(mappedMailMap.get(MappingOrderRuleUtil.AMAZON_CODE));
            } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())) {
                order.setEbayCarrierName(mappedMailMap.get(MappingOrderRuleUtil.EBAY_CARRIER));
            } else {
                order.setAmazonCarrierName(mappedMailMap.get(""));
                order.setAmazonShippingMethod(mappedMailMap.get(""));
                order.setEbayCarrierName(mappedMailMap.get(""));
            }
        }
    }

    /**
     * 匹配仓库
     *
     * @param sysOrderPackageDTO      {@link SysOrderPackageDTO} 包裹
     * @param platform                平台
     * @param sellerWarehouseRuleList {@link List<OrderRuleWithBLOBs>} 卖家仓库匹配规则
     * @param cmsWarehouseRuleList    {@link List<OrderRuleWithBLOBs>} 管理后台仓库匹配规则
     * @param orderInventoryList      {@link List<OrderInvDTO>} sku在同个仓库的列表
     * @return {@link Map<String, String>} 匹配的仓库信息
     */
    private Map<String, String> mappingWarehouseNew(SysOrderPackageDTO sysOrderPackageDTO, String platform,
                                                    List<OrderRuleWithBLOBs> sellerWarehouseRuleList,
                                                    List<OrderRuleWithBLOBs> cmsWarehouseRuleList,
                                                    List<OrderInvDTO> orderInventoryList) {
        String sysOrderId = sysOrderPackageDTO.getSysOrderId();
        String packageNumber = sysOrderPackageDTO.getOrderTrackId();
        logger.info("订单{}, 包裹{}, 开始进行仓库匹配", sysOrderId, packageNumber);
        Map<String, String> sellerWarehouseMap = MappingOrderRuleUtil.warehouseMatchingMethods(
                sysOrderPackageDTO, platform, orderInventoryList, sellerWarehouseRuleList);
        if (sellerWarehouseMap != null && !sellerWarehouseMap.isEmpty()
                && StringUtils.isNotBlank(sellerWarehouseMap.get(MappingOrderRuleUtil.RULE_OBJECT))) {
            logger.info("订单{}, 包裹{}, 根据卖家仓库规则匹配到的仓库为{}", sysOrderId, packageNumber, sellerWarehouseMap);
            return sellerWarehouseMap;
        }

        Map<String, String> cmsWarehouseMap = MappingOrderRuleUtil.warehouseMatchingMethods(
                sysOrderPackageDTO, platform, orderInventoryList, cmsWarehouseRuleList);

        if (cmsWarehouseMap != null && !cmsWarehouseMap.isEmpty()
                && StringUtils.isNotBlank(cmsWarehouseMap.get(MappingOrderRuleUtil.RULE_OBJECT))) {
            logger.info("订单{}, 包裹{}, 根据管理后台仓库规则匹配到的仓库为{}", sysOrderId, packageNumber, cmsWarehouseMap);
            return cmsWarehouseMap;
        }

        return null;
    }

    /**
     * 匹配仓库
     *
     * @param sysOrder                {@link SysOrder} 系统订单
     * @param platform                平台
     * @param sellerWarehouseRuleList {@link List<OrderRuleWithBLOBs>} 卖家仓库匹配规则
     * @param cmsWarehouseRuleList    {@link List<OrderRuleWithBLOBs>} 管理后台仓库匹配规则
     * @param orderInventoryList      {@link List<OrderInvDTO>} sku在同个仓库的列表
     * @return {@link Map<String, String>} 匹配的仓库信息
     */
    private Map<String, String> mappingWarehouse(SysOrder sysOrder, String platform,
                                                 List<OrderRuleWithBLOBs> sellerWarehouseRuleList,
                                                 List<OrderRuleWithBLOBs> cmsWarehouseRuleList,
                                                 List<OrderInvDTO> orderInventoryList) {
        String sysOrderId = sysOrder.getSysOrderId();
        logger.info("订单{}开始进行仓库匹配", sysOrderId);
        Map<String, String> sellerWarehouseMap = MappingOrderRuleUtil.warehouseMatchingMethodsNew(
                sysOrder, platform, orderInventoryList, sellerWarehouseRuleList);
        if (sellerWarehouseMap != null && !sellerWarehouseMap.isEmpty()
                && StringUtils.isNotBlank(sellerWarehouseMap.get(MappingOrderRuleUtil.RULE_OBJECT))) {
            logger.info("订单{}根据卖家仓库规则匹配到的仓库为{}", sysOrderId, sellerWarehouseMap);
            return sellerWarehouseMap;
        }

        Map<String, String> cmsWarehouseMap = MappingOrderRuleUtil.warehouseMatchingMethodsNew(
                sysOrder, platform, orderInventoryList, cmsWarehouseRuleList);

        if (cmsWarehouseMap != null && !cmsWarehouseMap.isEmpty()
                && StringUtils.isNotBlank(cmsWarehouseMap.get(MappingOrderRuleUtil.RULE_OBJECT))) {
            logger.info("订单{}根据管理后台仓库规则匹配到的仓库为{}", sysOrderId, cmsWarehouseMap);
            return cmsWarehouseMap;
        }

        return null;
    }

    /**
     * @param sysOrderPackageDTO  {@link SysOrderPackageDTO} 系统订单
     * @param platform            平台
     * @param sellerMailRuleList  {@link List<OrderRuleWithBLOBs>} 卖家物流方式匹配规则
     * @param cmsMailRuleList     {@link List<OrderRuleWithBLOBs>} 管理后台物流方式匹配规则
     * @param warehouseId         仓库ID
     * @param warehouseSourceType {@link Constants.Warehouse}仓库类型
     * @return {@link Map<String, String>} 匹配的物流方式信息
     */
    private Map<String, String> mappingMailTypeNew(SysOrderPackageDTO sysOrderPackageDTO, String platform,
                                                   List<OrderRuleWithBLOBs> sellerMailRuleList,
                                                   List<OrderRuleWithBLOBs> cmsMailRuleList,
                                                   String warehouseId, String warehouseSourceType) {
        String sysOrderId = sysOrderPackageDTO.getSysOrderId();
        logger.info("订单{}开始进行邮寄方式匹配", sysOrderId);
        String packageNumber = sysOrderPackageDTO.getOrderTrackId();
        List<LogisticsDTO> orderLogisticsList = systemOrderCommonService.matchOrderLogistics(warehouseSourceType,
                sysOrderPackageDTO, platform, Integer.valueOf(warehouseId));

        if (CollectionUtils.isEmpty(orderLogisticsList)) {
            logger.info("订单{}, 包裹{}, 在仓库{}下无合适的邮寄方式", sysOrderId, packageNumber, warehouseId);
            return null;
        }

        Map<String, String> sellerMailMap = MappingOrderRuleUtil.mailMatchingMethods(sysOrderPackageDTO, platform,
                orderLogisticsList, sellerMailRuleList, warehouseId);
        if (null != sellerMailMap && StringUtils.isNotBlank(sellerMailMap.get(MappingOrderRuleUtil.RULE_OBJECT))) {
            logger.info("订单{}, 包裹{}, 在仓库{}下根据卖家规则匹配到的邮寄方式是{}", sysOrderId, packageNumber, warehouseId, sellerMailMap);
            return sellerMailMap;
        }

        Map<String, String> cmsMailMap = MappingOrderRuleUtil.mailMatchingMethods(sysOrderPackageDTO, platform,
                orderLogisticsList, cmsMailRuleList, warehouseId);
        if (null != cmsMailMap && StringUtils.isNotBlank(cmsMailMap.get(MappingOrderRuleUtil.RULE_OBJECT))) {
            logger.info("订单{}, 包裹{}, 在仓库{}下根据管理后台规则匹配到的邮寄方式是{}", sysOrderId, packageNumber, warehouseId, cmsMailMap);
            return cmsMailMap;
        }

        return null;
    }

    /**
     * @param sysOrder            {@link SysOrder} 系统订单
     * @param platform            平台
     * @param sellerMailRuleList  {@link List<OrderRuleWithBLOBs>} 卖家物流方式匹配规则
     * @param cmsMailRuleList     {@link List<OrderRuleWithBLOBs>} 管理后台物流方式匹配规则
     * @param warehouseId         仓库ID
     * @param warehouseSourceType {@link Constants.Warehouse}仓库类型
     * @return {@link Map<String, String>} 匹配的物流方式信息
     */
    private Map<String, String> mappingMailType(SysOrder sysOrder, String platform,
                                                List<OrderRuleWithBLOBs> sellerMailRuleList,
                                                List<OrderRuleWithBLOBs> cmsMailRuleList,
                                                String warehouseId, String warehouseSourceType) {
        String sysOrderId = sysOrder.getSysOrderId();
        logger.info("订单{}开始进行邮寄方式匹配", sysOrderId);
        // 已废弃，后续该方法会删掉
//        List<LogisticsDTO> orderLogisticsList = systemOrderCommonService.matchOrderLogistics(warehouseSourceType,
//                sysOrder, platform, warehouseId);
        List<LogisticsDTO> orderLogisticsList = new ArrayList<>();

        if (CollectionUtils.isEmpty(orderLogisticsList)) {
            logger.info("订单{}在仓库{}下无合适的邮寄方式", sysOrderId, warehouseId);
            return null;
        }

        Map<String, String> sellerMailMap = MappingOrderRuleUtil.mailMatchingMethodsNew(sysOrder, platform,
                orderLogisticsList, sellerMailRuleList);
        if (null != sellerMailMap && StringUtils.isNotBlank(sellerMailMap.get(MappingOrderRuleUtil.RULE_OBJECT))) {
            logger.info("订单{}在仓库{}下根据卖家规则匹配到的邮寄方式是{}", sysOrderId, warehouseId, sellerMailMap);
            return sellerMailMap;
        }

        Map<String, String> cmsMailMap = MappingOrderRuleUtil.mailMatchingMethodsNew(sysOrder, platform,
                orderLogisticsList, cmsMailRuleList);
        if (null != cmsMailMap && StringUtils.isNotBlank(cmsMailMap.get(MappingOrderRuleUtil.RULE_OBJECT))) {
            logger.info("订单{}在仓库{}下根据管理后台规则匹配到的邮寄方式是{}", sysOrderId, warehouseId, cmsMailMap);
            return cmsMailMap;
        }

        return null;
    }


    /**
     * 判断库存
     *
     * @param warehouse            仓库code
     * @param order                订单
     * @param warehouseInventories 所有仓库的库存列表
     * @return 成功为true 否则为false
     */
    private boolean checkInventory(String warehouse, SysOrder order, List<WarehouseInventory> warehouseInventories) {
        warehouseInventories = warehouseInventories.stream().filter(w -> w.getWarehouseCode().equalsIgnoreCase(warehouse)).collect(Collectors.toList());
        WarehouseInventory inventory = null;
        for (SysOrderDetail detail : order.getSysOrderDetails()) {
            for (WarehouseInventory w : warehouseInventories) {
                if (w.getPinlianSku().equalsIgnoreCase(detail.getSku()))
                    inventory = w;
            }
            if (inventory == null || inventory.getAvailableQty() == null || inventory.getAvailableQty() < detail.getSkuQuantity())
                return false;
        }
        return true;
    }

//    @Override
//    public boolean checkOrderInventory(SysOrder order) {
//        ArrayList<String> strings = new ArrayList<>();
//        order.getSysOrderDetails().forEach(d -> strings.add(d.getSku()));
//        order.setSkus(strings);
//        String res = remoteSupplierService.getInventoryAvailableQty(order.getSkus());
//        String dataString = Utils.returnRemoteResultDataString(res, "供应商服务异常");     //查询库存，当没有库存或者查询库存失败返回
//        try {
//            List<WarehouseInventory> warehouseInventories = JSONObject.parseArray(dataString, WarehouseInventory.class);
//            if (warehouseInventories == null || warehouseInventories.size() == 0
//                    || !checkInventory(order.getDeliveryWarehouseCode(), order, warehouseInventories)) {
//                try {
////                    orderMessageSender.sendOrderStockOut(order.getSellerPlAccount(), order.getSysOrderId(),
////                            MessageEnum.ORDER_OUT_OF_STOCK_NOTICE, null);
//                } catch (Exception e) {
//                    logger.error("发送订单缺货消息错误，订单id为：" + order.getSysOrderId(), e);
//                }
//                return false;
//            }
//            return true;
//        } catch (Exception e) {
//            logger.error("匹配库存异常", e);
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "库存匹配异常");
//        }
//    }

    /**
     * 针对新需求中的公共规则与卖家规则进行区分处理
     * @param ruleType 规则类型 公共类型：public 卖家类型seller
     * @param rule 参数承载体
     * @param type 类型(仓库，物流)
     * @return
     */
    @Override
    public List<OrderRuleWithBLOBs> queryRuleList(String ruleType, String type,OrderRule rule) {

        List<OrderRuleWithBLOBs> resultList=Collections.emptyList();
        if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.WAREHOUSE.getType())) {
            resultList= this.warehouseList(ruleType,rule);
        } else if (type.equalsIgnoreCase(OrderRuleEnum.RuleType.MAIL_TYPE.getType())){
            resultList= this.mailList(ruleType,rule);
        }
        Collections.sort(resultList, new Comparator<OrderRuleWithBLOBs>() {
            @Override
            public int compare(OrderRuleWithBLOBs o1, OrderRuleWithBLOBs o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
        return resultList;
    }

    /**
     * 根据ruleType进行 物流信息规则查询
     * @param ruleType
     * @param rule
     * @return
     */
    private List<OrderRuleWithBLOBs> mailList(String ruleType, OrderRule rule) {
        List<OrderRuleWithBLOBs> all = orderRuleMailMapper.findByRuleType(ruleType,rule);
        this.checkEffectiveEndTimeByList(all, orderRuleMailMapper);
        return all;
    }

    /**
     * 根据ruleType 进行仓库查询
     * @param ruleType
     * @param rule
     * @return
     */
    private List<OrderRuleWithBLOBs> warehouseList(String ruleType, OrderRule rule) {
        List<OrderRuleWithBLOBs> all = orderRuleWarehouseMapper.findListByRuleType(ruleType,rule);
        this.checkEffectiveEndTimeByList(all, orderRuleWarehouseMapper);
        return all;
    }

}
