package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.OrderHandleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.ShippingAddress;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.commodity.CommoditySpec;
import com.rondaful.cloud.order.entity.commodity.SkuInventoryVo;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.enums.*;
import com.rondaful.cloud.order.mapper.SysOrderNewMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.mapper.SysOrderReceiveAddressMapper;
import com.rondaful.cloud.order.model.dto.syncorder.SplitPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.service.ISplitPackgeService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.utils.DomainEquals;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.OrderUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Service
public class SplitPackgeServiceImpl implements ISplitPackgeService {
    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private SysOrderNewMapper sysOrderNewMapper;

    @Autowired
    private SysOrderReceiveAddressMapper sysOrderReceiveAddressMapper;

    @Autowired
    private SysOrderPackageDetailMapper sysOrderPackageDetailMapper;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;

    @Autowired
    private ISysOrderLogService sysOrderLogService;

    private final static Logger _log = LoggerFactory.getLogger(SystemOrderServiceImpl.class);

    @Autowired
    private RedissLockUtil redissLockUtil;

    @Autowired
    private GetLoginUserInformationByToken loginUserInfo;

    @Autowired
    private ISysOrderService sysOrderService;

    @Autowired
    private SysOrderServiceImpl sysOrderServiceImpl;

    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;

    @Autowired
    private RemoteSupplierService remoteSupplierService;

    /**
     * 拆分包裹
     *
     * @param splitPackageDTO
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void saveSplittedSysPackage(SplitPackageDTO splitPackageDTO) throws Exception {
        for (SysOrderPackageDTO sysOrderPackageDTO: splitPackageDTO.getSysOrderPackageDTOList()){
            for (int i = sysOrderPackageDTO.getSysOrderPackageDetailList().size() -1; i >= 0; i--){
                SysOrderPackageDetailDTO sysOrderPackageDetailDTO = sysOrderPackageDTO.getSysOrderPackageDetailList().get(i);
                if (StringUtils.isBlank(sysOrderPackageDetailDTO.getSku())){
                    sysOrderPackageDTO.getSysOrderPackageDetailList().remove(sysOrderPackageDetailDTO);
                }
            }
        }
        _log.info("________________拆分订单参数splitPackageDTO：{}___________________" + FastJsonUtils.toJsonString(splitPackageDTO));
        //RLock lock = redissLockUtil.lock(splitPackageDTO.getSysOrderId(), 20);
        if (!redissLockUtil.tryLock(splitPackageDTO.getSysOrderId(), 10, 5)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请求频繁，请稍后尝试！");
        }
        //验证数据
        this.validateSplittedSysPackageData(splitPackageDTO);
        //组装包裹数据
        String sysOrderId = splitPackageDTO.getSysOrderId();
        SysOrderNew sysOrderNew = sysOrderService.getSysOrderDetailByPlOrderId(sysOrderId);
        List<SysOrderPackage> newSysOrderPackages = new ArrayList<>();
        List<SysOrderPackage> sysOrderPackages = sysOrderPackageMapper.selectPackageByOderId(sysOrderId);
        SysOrderPackage oldSysOrderPackage = sysOrderPackages.get(0);
        String orderTrackId = oldSysOrderPackage.getOrderTrackId();

        List<SysOrderPackageDTO> sysOrderPackageDTOList = splitPackageDTO.getSysOrderPackageDTOList();
        StringBuilder operateOrderTrackId = new StringBuilder();
        BigDecimal orderAmount = BigDecimal.ZERO;
        for (SysOrderPackageDTO sysOrderPackageDTO : sysOrderPackageDTOList) {
            SysOrderPackage sysOrderPackage = new SysOrderPackage();
            String trackId = OrderUtils.getPLTrackNumber();
            operateOrderTrackId.append(trackId).append(Constants.SplitSymbol.HASH_TAG);
            BeanUtils.copyProperties(sysOrderPackageDTO, sysOrderPackage);
            sysOrderPackage.setId(null);
            sysOrderPackage.setSysOrderId(sysOrderId);
            sysOrderPackage.setOrderTrackId(trackId);
            sysOrderPackage.setOperateStatus(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue());
            sysOrderPackage.setOperateOrderTrackId(orderTrackId);
            sysOrderPackage.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.getLogisticsStrategyByLogisticsType(Integer.valueOf(sysOrderPackage.getLogisticsStrategy())));

            _log.info("________________sysOrderPackage：{}___________________" + FastJsonUtils.toJsonString(sysOrderPackage));
            //组装包裹明细数据
            List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOs = sysOrderPackageDTO.getSysOrderPackageDetailList();
            List<SysOrderPackageDetail> sysOrderPackageDetails = new ArrayList<>();
            for (SysOrderPackageDetailDTO sysOrderPackageDetailDTO : sysOrderPackageDetailDTOs) {
                SysOrderPackageDetail sysOrderPackageDetail = new SysOrderPackageDetail();
                BeanUtils.copyProperties(sysOrderPackageDetailDTO, sysOrderPackageDetail);
                sysOrderPackageDetail.setId(null);
                sysOrderPackageDetail.setOrderTrackId(trackId);
                //TODO 分仓定价业务
                orderAmount = reSetItemMoney(orderAmount, sysOrderPackage, sysOrderPackageDetail);
                _log.info("________________sysOrderPackageDetail：{}___________________" + FastJsonUtils.toJsonString(sysOrderPackageDetail));
                sysOrderPackageDetails.add(sysOrderPackageDetail);
            }
            sysOrderPackage.setSysOrderPackageDetailList(sysOrderPackageDetails);
            newSysOrderPackages.add(sysOrderPackage);
        }
        sysOrderNew.setSysOrderPackageList(newSysOrderPackages);
        sysOrderNew.setOrderAmount(orderAmount);//TODO 重新设置订单商品成本
        //物流费计算
        _log.info("订单物流费计算之前：{}___________________" + FastJsonUtils.toJsonString(sysOrderNew));
        sysOrderServiceImpl.setShipFee(sysOrderNew);
        _log.info("订单物流费计算后：{}___________________" + FastJsonUtils.toJsonString(sysOrderNew));
        //插入包裹信息
        String loginName = getLoginUserInformationByToken.getUserDTO().getLoginName();
        sysOrderNew.getSysOrderPackageList().forEach(sysOrderPackage -> {
            sysOrderPackage.setCreater(loginName);
            sysOrderPackage.setModifier(loginName);

            WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(sysOrderPackage.getDeliveryWarehouseId().toString());
            if (null == warehouseDTO) {
                _log.error("根据仓库ID：{}找不到仓库信息，供应商服务返回Null", sysOrderPackage.getDeliveryWarehouseId());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据仓库ID查不到仓库信息");
            }
            sysOrderPackage.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());

            String str = remoteSupplierService.queryLogisticsByCode(sysOrderPackage.getDeliveryMethodCode(), Integer.valueOf(sysOrderPackage.getDeliveryWarehouseId()));
            String dataString = Utils.returnRemoteResultDataString(str, "供应商服务异常");
            if (StringUtils.isNotBlank(dataString)) {
                LogisticsDTO logisticsDTO = JSONObject.parseObject(dataString, LogisticsDTO.class);
                sysOrderPackage.setAmazonCarrierName(logisticsDTO.getAmazonCarrier());
                sysOrderPackage.setAmazonShippingMethod(logisticsDTO.getAmazonCode());
                sysOrderPackage.setEbayCarrierName(logisticsDTO.getEbayCarrier());
            }

            sysOrderPackageMapper.insertSelective(sysOrderPackage);
            sysOrderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> {
                sysOrderPackageDetail.setCreater(loginName);
                sysOrderPackageDetail.setModifier(loginName);
                sysOrderPackageDetailMapper.insertSelective(sysOrderPackageDetail);
            });
        });

        //修改原来包裹状态为不显示
        SysOrderPackage sysOrderPackage = new SysOrderPackage();
        sysOrderPackage.setId(oldSysOrderPackage.getId());
        sysOrderPackage.setIsShow(OrderPackageHandleEnum.IsShowEnum.NO_SHOW.getValue());
        sysOrderPackage.setOperateStatus(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue());
        String operateOrderTrackIdnew = StringUtils.isBlank(operateOrderTrackId) ? null : operateOrderTrackId.toString().substring(0, operateOrderTrackId.lastIndexOf(Constants.SplitSymbol.HASH_TAG));
        sysOrderPackage.setOperateOrderTrackId(operateOrderTrackIdnew);

        sysOrderPackageMapper.updateByPrimaryKeySelective(sysOrderPackage);

        //修改订单
        //设置预估利润、利润率
        systemOrderCommonService.setGrossMarginAndProfitMarginAndTotal(sysOrderNew);
        if (sysOrderNew.getIsConvertOrder().equalsIgnoreCase(Constants.isConvertOrder.NO)) {
            sysOrderNew.setGrossMargin(null);
            sysOrderNew.setProfitMargin(null);
        }
        sysOrderNew.setSplittedOrMerged(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue());
        sysOrderNewMapper.updateOrder(sysOrderNew);

        _log.info("________________拆分包裹 {} 操作成功________________", sysOrderId);
        sysOrderLogService.insertSelective(        //添加订单操作日志
                new SysOrderLog(sysOrderNew.getSysOrderId(),
                        OrderHandleLogEnum.Content.PACKAGE_SPLIT.packageSplit(sysOrderId, operateOrderTrackIdnew.split(Constants.SplitSymbol.HASH_TAG)),
                        OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                        loginUserInfo.getUserDTO().getUserName()));
        redissLockUtil.unlock(splitPackageDTO.getSysOrderId());
    }

    public BigDecimal reSetItemMoney(BigDecimal orderAmount, SysOrderPackage sysOrderPackage, SysOrderPackageDetail sysOrderPackageDetail) {
        String result = remoteCommodityService.test("1", "1", null, null, null, null,
                sysOrderPackageDetail.getSku(), null, null);
        String data = Utils.returnRemoteResultDataString(result, "调用商品服务异常");
        JSONObject parse1 = (JSONObject) JSONObject.parse(data);
        String pageInfo = parse1.getString("pageInfo");
        JSONObject parse2 = (JSONObject) JSONObject.parse(pageInfo);
        JSONArray list1 = parse2.getJSONArray("list");
        List<CommoditySpec> commodityDetails = list1.toJavaList(CommoditySpec.class);
        for (CommoditySpec commodityDetail : commodityDetails) {
            List<SkuInventoryVo> inventoryList = commodityDetail.getInventoryList();
            int count = 0;
            if (CollectionUtils.isNotEmpty(inventoryList)) {  //TODO 仓库分仓定价业务  匹配到相同仓库ID的则取仓库商品价
                for (SkuInventoryVo skuInventoryVo : inventoryList) {
                    if (String.valueOf(skuInventoryVo.getWarehouseId()).equals(String.valueOf(sysOrderPackage.getDeliveryWarehouseId()))) {
                        count++;
                        //商品系统单价
                        sysOrderPackageDetail.setSkuCost(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                        sysOrderPackageDetail.setSkuPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                    }
                }
                if (count == 0) {
                    //商品系统单价
                    sysOrderPackageDetail.setSkuCost(commodityDetail.getCommodityPriceUs());
                    sysOrderPackageDetail.setSkuPrice(commodityDetail.getCommodityPriceUs());
                }
            } else {
                //商品系统单价
                sysOrderPackageDetail.setSkuCost(commodityDetail.getCommodityPriceUs());
                sysOrderPackageDetail.setSkuPrice(commodityDetail.getCommodityPriceUs());
            }
            orderAmount = orderAmount.add(sysOrderPackageDetail.getSkuPrice().multiply(BigDecimal.valueOf(sysOrderPackageDetail.getSkuQuantity())));
        }
        return orderAmount;
    }

    /**
     * 撤销拆分包裹
     *
     * @param sysOrderId
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void cancelSplittedSysPackage(String sysOrderId) {
        if (!redissLockUtil.tryLock(sysOrderId, 10, 5)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请求频繁，请稍后尝试！");
        }
        List<SysOrderPackage> sysOrderPackages = sysOrderPackageMapper.queryOrderPackageByOrderId(sysOrderId);

        if (CollectionUtils.isEmpty(sysOrderPackages) || sysOrderPackages.size() == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "根据订单号查询不到包裹。。。");
        }
        //一、判断是否符合撤销拆包条件
        SysOrderNew oldSysOrder = sysOrderService.getSysOrderDetailByPlOrderId(sysOrderId);

        if (oldSysOrder.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "该订单是普通订单。。。");
        }

        Byte orderDeliveryStatus = oldSysOrder.getOrderDeliveryStatus();
        if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.INTERCEPTED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.DELIVERED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.RECEIVED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.CANCELLED.equals(orderDeliveryStatus)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "处于配货中/已拦截/已发货/已收货/已作废状态的订单不能撤销拆分。。。");
        }

        //二、删除拆分后的包裹、包裹详情
        List<String> collect = sysOrderPackages.stream().map(x -> x.getOrderTrackId()).collect(Collectors.toList());
        sysOrderPackageDetailMapper.deleteBatchBySysOrderTrackId(collect);
        sysOrderPackageMapper.deletePackageBySplitSysOrderId(sysOrderId);

        //三、修改旧包状态
        String operateOrderTrackId = sysOrderPackages.get(0).getOperateOrderTrackId();
        SysOrderPackage oldSysOrderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(operateOrderTrackId);

        List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDetailMapper.queryOrderPackageDetails(oldSysOrderPackage.getOrderTrackId());

        oldSysOrderPackage.setSysOrderPackageDetailList(sysOrderPackageDetails);

        oldSysOrder.setSysOrderPackageList(Arrays.asList(oldSysOrderPackage));
        //物流费计算
        _log.info("订单物流费计算之前：{}___________________" + FastJsonUtils.toJsonString(oldSysOrder));
        sysOrderServiceImpl.setShipFee(oldSysOrder);
        _log.info("订单物流费计算后：{}___________________" + FastJsonUtils.toJsonString(oldSysOrder));
        //如果没有选择物流方式，把运费设置为0
        if (null == oldSysOrder.getSysOrderPackageList().get(0).getDeliveryMethodCode() || StringUtils.equalsIgnoreCase("-1", oldSysOrder.getSysOrderPackageList().get(0).getDeliveryMethodCode()) || StringUtils.isEmpty(oldSysOrder.getSysOrderPackageList().get(0).getDeliveryMethodCode())) {
            oldSysOrder.getSysOrderPackageList().get(0).setEstimateShipCost(BigDecimal.ZERO);
            oldSysOrder.setEstimateShipCost(BigDecimal.ZERO);
        }

        BigDecimal orderAmount = BigDecimal.ZERO;
        for (SysOrderPackage sysOrderPackage1: oldSysOrder.getSysOrderPackageList()){
            SysOrderPackage sysOrderPackage = new SysOrderPackage();
            sysOrderPackage.setId(sysOrderPackage1.getId());
            sysOrderPackage.setIsShow(OrderPackageHandleEnum.IsShowEnum.SHOW.getValue());
            sysOrderPackage.setOperateStatus(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue());
            sysOrderPackage.setOperateOrderTrackId("");
            sysOrderPackage.setEstimateShipCost(sysOrderPackage1.getEstimateShipCost());
            sysOrderPackageMapper.updateByPrimaryKeySelective(sysOrderPackage);

            for (SysOrderPackageDetail sysOrderPackageDetail: sysOrderPackage1.getSysOrderPackageDetailList()){
                SysOrderPackageDetail newSysOrderPackageDetail = new SysOrderPackageDetail();
                newSysOrderPackageDetail.setId(sysOrderPackageDetail.getId());
                newSysOrderPackageDetail.setSellerShipFee(sysOrderPackageDetail.getSellerShipFee());
                newSysOrderPackageDetail.setSupplierShipFee(sysOrderPackageDetail.getSupplierShipFee());
                if (null == oldSysOrder.getSysOrderPackageList().get(0).getDeliveryMethodCode() || StringUtils.equalsIgnoreCase("-1", oldSysOrder.getSysOrderPackageList().get(0).getDeliveryMethodCode()) || StringUtils.isEmpty(oldSysOrder.getSysOrderPackageList().get(0).getDeliveryMethodCode())) {
                    newSysOrderPackageDetail.setSellerShipFee(BigDecimal.ZERO);
                    newSysOrderPackageDetail.setSupplierShipFee(BigDecimal.ZERO);
                }
                //TODO 分仓定价业务
                sysOrderPackageDetail.setSellerShipFee(newSysOrderPackageDetail.getSellerShipFee());
                sysOrderPackageDetail.setSupplierShipFee(newSysOrderPackageDetail.getSupplierShipFee());
                newSysOrderPackageDetail.setSkuQuantity(sysOrderPackageDetail.getSkuQuantity());
                newSysOrderPackageDetail.setSku(sysOrderPackageDetail.getSku());
                orderAmount = reSetItemMoney(orderAmount, sysOrderPackage1, newSysOrderPackageDetail);
                sysOrderPackageDetailMapper.updateByPrimaryKeySelective(newSysOrderPackageDetail);

            }
        }
        //重新计算订单商品总价
        oldSysOrder.setOrderAmount(orderAmount);

        //修改订单
        //设置预估利润、利润率
        systemOrderCommonService.setGrossMarginAndProfitMarginAndTotal(oldSysOrder);
        if (oldSysOrder.getIsConvertOrder().equalsIgnoreCase(Constants.isConvertOrder.NO)) {
            oldSysOrder.setGrossMargin(null);
            oldSysOrder.setProfitMargin(null);
        }
        oldSysOrder.setSysOrderId(sysOrderId);
        oldSysOrder.setSplittedOrMerged(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue());
        sysOrderNewMapper.updateOrder(oldSysOrder);

        _log.info("________________撤销已拆分系统订单 {} 操作成功________________", sysOrderId);
        sysOrderLogService.insertSelective(        //添加订单操作日志
                new SysOrderLog(sysOrderId,
                        OrderHandleLogEnum.Content.REVOCATION_PACKAGE_SPLIT.revocationPackageSplit(sysOrderId),
                        OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                        loginUserInfo.getUserDTO().getUserName()));
        redissLockUtil.unlock(sysOrderId);
    }

    /**
     * 检查订单是否能够拆分
     *
     * @param splitPackageDTO
     */
    private void validateSplittedSysPackageData(SplitPackageDTO splitPackageDTO) {
        String sysOrderId = splitPackageDTO.getSysOrderId();

        List<SysOrderPackageDTO> sysOrderPackageDTOList = splitPackageDTO.getSysOrderPackageDTOList();
        if (CollectionUtils.isEmpty(sysOrderPackageDTOList) || sysOrderPackageDTOList.size() <= 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分的包裹数不符合条件。。。");
        }
        for (SysOrderPackageDTO sysOrderPackageDTO : sysOrderPackageDTOList) {
            if (StringUtils.isBlank(sysOrderPackageDTO.getSysOrderId())
                    || ObjectUtils.isEmpty(sysOrderPackageDTO.getDeliveryWarehouseId())
                    || StringUtils.isBlank(sysOrderPackageDTO.getDeliveryWarehouse())
                    || StringUtils.isBlank(sysOrderPackageDTO.getShippingCarrierUsedCode())
                    || StringUtils.isBlank(sysOrderPackageDTO.getShippingCarrierUsed())
                    || StringUtils.isBlank(sysOrderPackageDTO.getDeliveryMethodCode())
                    || StringUtils.isBlank(sysOrderPackageDTO.getDeliveryMethod())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统订单号,仓库ID/仓库名称,物流商CODE/物流商名称,邮寄方式CODE/邮寄方式名称不能为空。。。");
            }
            for (SysOrderPackageDetailDTO detail : sysOrderPackageDTO.getSysOrderPackageDetailList()) {
                if (StringUtils.isBlank(detail.getOrderTrackId())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹号不能为空。。。");
                } else if (StringUtils.isBlank(detail.getSku())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "SKU不能为空。。。");
                } else if (detail.getSkuQuantity() <= 0) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "SKU购买数量不能为空。。。");
                } else if (detail.getBindStatus().equalsIgnoreCase(SkuBindEnum.UNBIND.getValue())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "SKU未绑定。。。");
                }
            }
        }

        SysOrderNew oldSysOrder = sysOrderService.getSysOrderDetailByPlOrderId(sysOrderId);

        if (oldSysOrder == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据订单号查询出的系统订单实体类为空。。。");
        }
        if (!oldSysOrder.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分过或合并过的订单不能再进行拆分或合并。。。");
        }
        Byte orderDeliveryStatus = oldSysOrder.getOrderDeliveryStatus();
        if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.INTERCEPTED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.DELIVERED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.RECEIVED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.CANCELLED.equals(orderDeliveryStatus)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "处于配货中/已拦截/已发货/已收货/已作废状态的订单不能拆分。。。");
        }
        //将原始订单的SKU和SkuNum装进Map
        List<SysOrderDetail> oldSysOrderDetails = oldSysOrder.getSysOrderDetails();
        Map<String, Integer> oldMap = oldSysOrderDetails.stream()
                .collect(Collectors.groupingBy(SysOrderDetail::getSku,
                        Collectors.summingInt(SysOrderDetail::getSkuQuantity)));
        Set<String> oldSkuSet = oldMap.keySet();
        //判断原始SKU种类是否支持拆分
        int size = oldSysOrderDetails.stream().map(x -> x.getSku()).collect(Collectors.toSet()).size();
        List<Integer> skuCount = oldSysOrderDetails.stream().map(x -> x.getSkuQuantity()).collect(Collectors.toList());
        //判断sku数量是否支持拆分
        if (size == 1) {
            if (skuCount.get(0) < 1) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "此订单只有一种商品且数量小于2，不能拆分。。。");
            }
        }
        //取出原始系统订单表中SKU和此SKU对应数量
        Map<String, Integer> splitMap = new HashMap<>();
        //拆分后SKU数量，种类等校验
        for (SysOrderPackageDTO sysOrderPackageDTO : sysOrderPackageDTOList) {
            List<SysOrderPackageDetailDTO> sysDetails = sysOrderPackageDTO.getSysOrderPackageDetailList();
            for (SysOrderPackageDetailDTO detail : sysDetails) {
                String sysSku = detail.getSku();
                if (!oldSkuSet.contains(sysSku)) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分后此SKU不在原始订单中。。。");
                }
                Integer skuNum = detail.getSkuQuantity();
                if (skuNum <= 0) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分后此SKU购买数量不能为0或者为负数。。。");
                }
                if (oldMap.get(sysSku).compareTo(skuNum) == -1) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分后此SKU购买数量不能比原始订单多。。。");
                }
                splitMap.put(sysSku, (int) (splitMap.get(sysSku) == null ? 0 : splitMap.get(sysSku)) + (int) skuNum);

                //补全商品信息
                for (SysOrderDetail sysOrderDetail : oldSysOrderDetails) {
                    if (detail.getSku().equals(sysOrderDetail.getSku())) {
                        detail.setSkuUrl(sysOrderDetail.getItemUrl());
                        detail.setSkuName(sysOrderDetail.getItemName());
                        detail.setSkuNameEn(sysOrderDetail.getItemNameEn());
                        detail.setSkuAttr(sysOrderDetail.getItemAttr());
                        detail.setSkuPrice(sysOrderDetail.getItemPrice());
                    }
                }
            }
        }
        Boolean flag = this.compareMap(oldMap, splitMap);
        if (!flag) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分后SKU和对应SKU购买数量匹配不上。。。");
        }
    }

    private Boolean compareMap(Map<String, Integer> oldMap, Map<String, Integer> splitMap) {
        try {
            for (Map.Entry<String, Integer> entry1 : oldMap.entrySet()) {
                Integer m1value = entry1.getValue() == 0 ? 0 : entry1.getValue();
                Integer m2value = splitMap.get(entry1.getKey()) == 0 ? 0 : splitMap.get(entry1.getKey());
                if (!m1value.equals(m2value)) {//若两个map中相同key对应的value不相等
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 保存合并后的包裹
     *
     * @param sysOrderIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void saveMergedSysPackage(List<String> sysOrderIds) throws Exception {
        _log.info("_____________申请合并的订单为___________{}___________", sysOrderIds.toString());
        //List<RLock> lockList = new ArrayList<>();
        for (String orderId : sysOrderIds) {
            //RLock lock = redissLockUtil.lock(orderId, 20);
            if (!redissLockUtil.tryLock(orderId, 10, 5)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请求频繁，请稍后尝试！");
            }
            //lockList.add(lock);
        }
        if (CollectionUtils.isEmpty(sysOrderIds) || sysOrderIds.size() < 2) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并订单请求合并订单数不符合要求。。。");
        }
        //一、判断是否符合合包条件
        List<SysOrderNew> sysOrders = this.judgePackageCanBeMerged(sysOrderIds);
        _log.info("_________________可以进行合并__________________");
        List<SysOrderPackage> packages = new ArrayList<>();
        StringBuilder operateSysOrderId = new StringBuilder();
        sysOrders.forEach(sysOrderNew -> {
            packages.addAll(sysOrderNew.getSysOrderPackageList());
            operateSysOrderId.append(sysOrderNew.getSysOrderId()).append(Constants.SplitSymbol.HASH_TAG);

        });
        //获取所有包裹号，查询包裹详情
        List<String> collect = packages.stream().map(x -> x.getOrderTrackId()).collect(Collectors.toList());
        List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackageDetailMapper.queryBatchOrderPackageDetails(collect);

        //分组统计key:Sku,value:SkuQty
        Map<String, Integer> map = sysOrderPackageDetailList.stream().collect(Collectors.groupingBy(SysOrderPackageDetail::getSku, Collectors.summingInt(SysOrderPackageDetail::getSkuQuantity)));
        Set<String> keySet = map.keySet();

        //根据对象的属性去重
        List<SysOrderPackageDetail> distinctDetailList = sysOrderPackageDetailList.stream().collect(
                Collectors.collectingAndThen(toCollection(() ->
                        new TreeSet<>(Comparator.comparing(SysOrderPackageDetail::getSku))), ArrayList::new));
        //将相同Sku合并，SkuQty相加
        distinctDetailList = distinctDetailList.stream().map(x -> {
            if (keySet.contains(x.getSku())) {
                x.setSkuQuantity(map.get(x.getSku()));
            }
            return x;
        }).collect(Collectors.toList());

        //物流费计算
        List<SysOrderPackage> sysOrderPackages = new ArrayList<>();
        SysOrderPackage newSysOrderPackage = new SysOrderPackage();
        BeanUtils.copyProperties(packages.get(0), newSysOrderPackage);


        newSysOrderPackage.setSysOrderPackageDetailList(distinctDetailList);
        sysOrderPackages.add(newSysOrderPackage);
        //   用新的包裹计算物流费
        SysOrderNew sysOrderNew = sysOrders.get(0);
        sysOrderNew.setSysOrderPackageList(sysOrderPackages);
        _log.info("订单物流费计算之前：{}___________________" + FastJsonUtils.toJsonString(sysOrderNew));
        sysOrderServiceImpl.setShipFee(sysOrderNew);
        _log.info("订单物流费计算后：{}___________________" + FastJsonUtils.toJsonString(sysOrderNew));
        List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderNew.getSysOrderPackageList().get(0).getSysOrderPackageDetailList();


        //二、修改旧包裹&旧订单
        String trackId = OrderUtils.getPLTrackNumber();
        StringBuilder operateOrderTrackId = new StringBuilder();
        packages.forEach(sysOrderPackage -> {
            operateOrderTrackId.append(sysOrderPackage.getOrderTrackId()).append(Constants.SplitSymbol.HASH_TAG);
            SysOrderPackage oldSysOrderPackage = new SysOrderPackage();
            oldSysOrderPackage.setId(sysOrderPackage.getId());
            oldSysOrderPackage.setOperateOrderTrackId(trackId);
            oldSysOrderPackage.setOperateStatus(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue());
            oldSysOrderPackage.setIsShow(OrderPackageHandleEnum.IsShowEnum.NO_SHOW.getValue());
            List<String> sysOrderIdList = this.removeSpecifiedElement(sysOrderIds, sysOrderPackage.getSysOrderId());
            StringBuilder builderSysOrderId = new StringBuilder();
            sysOrderIdList.forEach(s -> {
                builderSysOrderId.append(s).append(Constants.SplitSymbol.HASH_TAG);
            });
            oldSysOrderPackage.setOperateSysOrderId(StringUtils.isBlank(builderSysOrderId) ? null : builderSysOrderId.toString().substring(0, builderSysOrderId.lastIndexOf(Constants.SplitSymbol.HASH_TAG)));

            List<SysOrderPackageDetail> oldSysOrderPackageDetails = sysOrderPackage.getSysOrderPackageDetailList();
            BigDecimal estimateShipCostTotal = new BigDecimal(0);

            for (SysOrderPackageDetail sysOrderPackageDetail : oldSysOrderPackageDetails) {
                Integer skuQuantity = sysOrderPackageDetail.getSkuQuantity();
                for (SysOrderPackageDetail sysOrderPackageDetail1 : sysOrderPackageDetails) {
                    if (sysOrderPackageDetail.getSku().equals(sysOrderPackageDetail1.getSku())) {
                        sysOrderPackageDetail.setSellerShipFee(sysOrderPackageDetail1.getSellerShipFee());
                        sysOrderPackageDetail.setSupplierShipFee(sysOrderPackageDetail1.getSupplierShipFee());

                        //运费只计算卖家，考虑包邮不包邮的情况
                        BigDecimal sellerShipFee = sysOrderPackageDetail1.getSellerShipFee();
                        if (sysOrderPackageDetail1.getFreeFreight().equals(Constants.SysOrder.FREE_FREIGHT)) {
                            sellerShipFee = BigDecimal.ZERO;
                        }
                        BigDecimal estimateShipCost = sellerShipFee.multiply(BigDecimal.valueOf(skuQuantity));
                        estimateShipCostTotal = estimateShipCostTotal.add(estimateShipCost);
                    }
                }
                sysOrderPackageDetailMapper.updateByPrimaryKeySelective(sysOrderPackageDetail);
            }

            oldSysOrderPackage.setEstimateShipCost(estimateShipCostTotal);
            sysOrderPackageMapper.updateByPrimaryKeySelective(oldSysOrderPackage);

            //修改订单
            //设置预估利润、利润率
            for (SysOrderNew sysOrder : sysOrders) {
                if (sysOrder.getSysOrderId().equals(sysOrderPackage.getSysOrderId())) {
                    sysOrder.setEstimateShipCost(estimateShipCostTotal);
                    systemOrderCommonService.setGrossMarginAndProfitMarginAndTotal(sysOrder);
                    if (sysOrder.getIsConvertOrder().equalsIgnoreCase(Constants.isConvertOrder.NO)) {
                        sysOrder.setGrossMargin(null);
                        sysOrder.setProfitMargin(null);
                    }
                    sysOrder.setSysOrderId(sysOrderPackage.getSysOrderId());
                    sysOrder.setSplittedOrMerged(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue());
                    sysOrderNewMapper.updateOrder(sysOrder);
                }
            }

        });


        //三、插入新包记录
        String username = loginUserInfo.getUserInfo().getUser().getUsername();
        SysOrderPackage sysOrderPackage = sysOrderNew.getSysOrderPackageList().get(0);
        sysOrderPackage.setId(null);
        sysOrderPackage.setSysOrderId("");
        sysOrderPackage.setOrderTrackId(trackId);
        sysOrderPackage.setOperateStatus(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue());
        sysOrderPackage.setOperateOrderTrackId(StringUtils.isBlank(operateOrderTrackId) ? null : operateOrderTrackId.toString().substring(0, operateOrderTrackId.lastIndexOf(Constants.SplitSymbol.HASH_TAG)));
        String operateSysOrderIdNew = StringUtils.isBlank(operateSysOrderId) ? null : operateSysOrderId.toString().substring(0, operateSysOrderId.lastIndexOf(Constants.SplitSymbol.HASH_TAG));
        sysOrderPackage.setOperateSysOrderId(operateSysOrderIdNew);
        sysOrderPackage.setIsShow(OrderPackageHandleEnum.IsShowEnum.SHOW.getValue());
        sysOrderPackage.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.getLogisticsStrategyByLogisticsType(Integer.valueOf(sysOrderPackage.getLogisticsStrategy())));
        sysOrderPackageMapper.insertSelective(sysOrderPackage);

        //新增包裹详情

        sysOrderPackageDetails.forEach(sysOrderPackageDetail -> {
            sysOrderPackageDetail.setId(null);
            sysOrderPackageDetail.setOrderTrackId(trackId);
            sysOrderPackageDetail.setCreater(username);
            sysOrderPackageDetail.setModifier(username);
            sysOrderPackageDetailMapper.insertSelective(sysOrderPackageDetail);
        });

        _log.info("_________________合并包裹成功，新的包裹号：{}__________________" + trackId);

        for (String id : sysOrderIds) {
            sysOrderLogService.insertSelective(        //添加订单操作日志
                    new SysOrderLog(id,
                            OrderHandleLogEnum.Content.PACKAGE_MERGE.packageMerge(trackId, sysOrderIds.toArray(new String[sysOrderIds.size()])),
                            OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                            loginUserInfo.getUserDTO().getUserName()));
        }

        for (String s : sysOrderIds) {
            redissLockUtil.unlock(s);
        }

    }

    /**
     * 判断能否合并订单包裹并返回被合并的订单对象集合
     *
     * @param sysOrderIds
     * @return
     */
    private List<SysOrderNew> judgePackageCanBeMerged(List<String> sysOrderIds) {
        if (CollectionUtils.isEmpty(sysOrderIds) || sysOrderIds.size() == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并订单传参系统订单集合为空。。。");
        }
        int size = sysOrderIds.size();
        if (size < 2) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并订单勾选系统订单数需大于等于2。。。");
        }
        if (size > 10) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并订单勾选系统订单数需小于等于10。。。");
        }

        List<SysOrderNew> sysOrders = new ArrayList<>();
        sysOrderIds.forEach(s -> {
            sysOrders.add(sysOrderService.getSysOrderDetailByPlOrderId(s));
        });

        if (CollectionUtils.isEmpty(sysOrders) || sysOrders.size() != size) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询不到请求合并的订单。。。");
        }
        for (SysOrderNew sysOrder : sysOrders) {
            if (!sysOrder.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分过或合并过的订单不能再拆分或合并。。。");
            }
            Byte orderDeliveryStatus = sysOrder.getOrderDeliveryStatus();
            if (orderDeliveryStatus == (byte) 3 || orderDeliveryStatus == (byte) 4 || orderDeliveryStatus == (byte) 5 || orderDeliveryStatus == (byte) 6 || orderDeliveryStatus == (byte) 7) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "处于配货中/已拦截/已发货/已收货/已作废的订单不能合并。。。");
            }
        }
        long sellerCount = sysOrders.stream().map(x -> x.getPlatformShopId()).distinct().count();
        if (sellerCount != 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并系统订单集合中不属于同一店铺不能合并订单。。。");
        }

        //订单订单来源状态为"1手工创建,2批量导入"可以与"3第三方平台API推送,4eBay平台订单转入,5Amazon平台订单转入,6AliExpress订单,7Wish订单,8星商订单" 合并，3,4,5,6,7,8之间不能合并
        long thirdCount = sysOrders.stream().map(a -> a.getOrderSource()).filter(x -> x == (byte) 3).distinct().count();
        long ebayCount = sysOrders.stream().map(a -> a.getOrderSource()).filter(x -> x == (byte) 4).distinct().count();
        long amazonCount = sysOrders.stream().map(a -> a.getOrderSource()).filter(x -> x == (byte) 5).distinct().count();
        long aliCount = sysOrders.stream().map(a -> a.getOrderSource()).filter(x -> x == (byte) 6).distinct().count();
        long wishCount = sysOrders.stream().map(a -> a.getOrderSource()).filter(x -> x == (byte) 7).distinct().count();
        long xsCount = sysOrders.stream().map(a -> a.getOrderSource()).filter(x -> x == (byte) 8).distinct().count();
        long num = thirdCount + ebayCount + amazonCount + aliCount + wishCount + xsCount;
        if (num >= 2) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "不同平台订单不能合并。。。");
        }
        //不同仓库不同邮寄方式不能合并，映射后的物流商和物流方式不相同不能合并
        SysOrderPackage bean = sysOrders.get(0).getSysOrderPackageList().get(0);
        Integer deliveryWarehouseId = bean.getDeliveryWarehouseId();
        String deliveryWarehouse = bean.getDeliveryWarehouse();
        String shippingCarrierUsedCode = bean.getShippingCarrierUsedCode();
        String shippingCarrierUsed = bean.getShippingCarrierUsed();
        String deliveryMethod = bean.getDeliveryMethod();
        String deliveryMethodCode = bean.getDeliveryMethodCode();
        String amazonCarrierName = bean.getAmazonCarrierName();
        String amazonShippingMethod = bean.getAmazonShippingMethod();
        String ebayCarrierName = bean.getEbayCarrierName();
        if (!ObjectUtils.isEmpty(deliveryWarehouseId) && StringUtils.isNotBlank(deliveryWarehouse) &&
                StringUtils.isNotBlank(shippingCarrierUsedCode) && StringUtils.isNotBlank(shippingCarrierUsed)
                && StringUtils.isNotBlank(deliveryMethod) && StringUtils.isNotBlank(deliveryMethodCode)) {
            for (SysOrderNew order : sysOrders) {
                for (SysOrderPackage sysOrderPackage: order.getSysOrderPackageList()){
                    for (SysOrderPackageDetail sysOrderPackageDetail: sysOrderPackage.getSysOrderPackageDetailList()){
                        if (sysOrderPackageDetail.getBindStatus().equalsIgnoreCase(SkuBindEnum.UNBIND.getValue())){
                            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "存在未绑定的sku，不能合并。。。");
                        }
                    }

                }
                SysOrderPackage sysOrderPackage = order.getSysOrderPackageList().get(0);
                if ((!deliveryWarehouseId.equals(order.getSysOrderPackageList().get(0).getDeliveryWarehouseId()))
                        || (!deliveryWarehouse.equals(sysOrderPackage.getDeliveryWarehouse()))
                        || (!shippingCarrierUsedCode.equals(sysOrderPackage.getShippingCarrierUsedCode()))
                        || (!shippingCarrierUsed.equals(sysOrderPackage.getShippingCarrierUsed())
                        || (!deliveryMethod.equals(sysOrderPackage.getDeliveryMethod()))
                        || (!deliveryMethodCode.equals(sysOrderPackage.getDeliveryMethodCode())))) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库ID和名称，物流商CODE和名称，邮寄方式CODE和名称信息必须一致。。。");
                }
                Byte orderSource = order.getOrderSource();
                if (orderSource == (byte) 4) {
                    if (!ebayCarrierName.equals(sysOrderPackage.getEbayCarrierName())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "子订单物流商ebayCarrierName不一致，不能合并。。。");
                    }
                }
                if (orderSource == (byte) 5) {
                    if (!amazonCarrierName.equals(sysOrderPackage.getAmazonCarrierName()) || !amazonShippingMethod.equals(sysOrderPackage.getAmazonShippingMethod())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "子订单物流商amazonCarrierName或amazonShippingMethod不一致，不能合并。。。");
                    }
                }
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库ID和名称，物流商CODE和名称，邮寄方式CODE不能为空。。。");
        }
        List<ShippingAddress> shippingAddressList = sysOrderReceiveAddressMapper.queryBatchAddressByOrderId(sysOrderIds);
        for (ShippingAddress address : shippingAddressList) {
            if (!DomainEquals.domainEquals(address, shippingAddressList.get(0))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并系统订单收货人信息不一致，不能合并。。。");
            }
        }
        return sysOrders;
    }

    /**
     * 删除指定元素
     *
     * @param stringList
     * @param element
     * @return
     */
    private List<String> removeSpecifiedElement(List<String> stringList, String element) {
        List<String> list = new ArrayList<>();
        stringList.forEach(s -> {
            if (!s.equals(element)) {
                list.add(s);
            }
        });
        return list;
    }

    /**
     * 取消合并包裹
     *
     * @param sysOrderIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void cancelMergedSysPackage(List<String> sysOrderIds) {
        //List<RLock> lockList = new ArrayList<>();
        for (String orderId : sysOrderIds) {
            //RLock lock = redissLockUtil.lock(orderId, 20);
            if (!redissLockUtil.tryLock(orderId, 10, 5)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请求频繁，请稍后尝试！");
            }
            //lockList.add(lock);
        }
        if (CollectionUtils.isEmpty(sysOrderIds) || sysOrderIds.size() < 2) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单ID参数不正确。。。");
        }
        //一、判断是否符合撤销拆包条件
        List<SysOrderNew> oldSysOrders = sysOrderNewMapper.selectBatchSysOrderListBySysOrderId(sysOrderIds);
        oldSysOrders.forEach(sysOrderNew -> {
            if (sysOrderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "该订单是普通订单。。。");
            }

            Byte orderDeliveryStatus = sysOrderNew.getOrderDeliveryStatus();
            if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.equals(orderDeliveryStatus)
                    || OrderHandleEnum.OrderDeliveryStatus.INTERCEPTED.equals(orderDeliveryStatus)
                    || OrderHandleEnum.OrderDeliveryStatus.DELIVERED.equals(orderDeliveryStatus)
                    || OrderHandleEnum.OrderDeliveryStatus.RECEIVED.equals(orderDeliveryStatus)
                    || OrderHandleEnum.OrderDeliveryStatus.CANCELLED.equals(orderDeliveryStatus)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "处于配货中/已拦截/已发货/已收货/已作废状态的订单不能撤销拆分。。。");
            }
        });

        List<SysOrderPackage> sysOrderPackages = sysOrderPackageMapper.queryBatchOrderPackageByOrderId(sysOrderIds);

        //二、删除合并后的包裹、包裹详情
        String orderTrackId = sysOrderPackages.get(0).getOperateOrderTrackId();
        List<String> collect = Arrays.asList(orderTrackId);
        sysOrderPackageDetailMapper.deleteBatchBySysOrderTrackId(collect);
        sysOrderPackageMapper.deletePackageByOrderTrackId(orderTrackId);

        //三、修改旧包状态
        sysOrderPackages.forEach(oldSysOrderPackage -> {
            SysOrderPackage sysOrderPackage = new SysOrderPackage();
            sysOrderPackage.setId(oldSysOrderPackage.getId());
            sysOrderPackage.setIsShow(OrderPackageHandleEnum.IsShowEnum.SHOW.getValue());
            sysOrderPackage.setOperateStatus(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue());
            sysOrderPackage.setOperateOrderTrackId("");
            sysOrderPackage.setOperateSysOrderId("");
            sysOrderPackageMapper.updateByPrimaryKeySelective(sysOrderPackage);
        });

        //修改订单状态
        sysOrderIds.forEach(s -> {
            BigDecimal orderAmount = BigDecimal.ZERO;
            sysOrderNewMapper.updateOrdersSplittedOrMerged(s, OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue());
            SysOrderNew sysOrderNew = sysOrderService.getSysOrderDetailByPlOrderId(s);
            //根据订单ID查询包裹详情
            List<SysOrderPackage> sysOrderPackageList = sysOrderPackageMapper.queryBatchOrderPackageByOrderId(Arrays.asList(s));
            sysOrderNew.setSysOrderPackageList(sysOrderPackageList);
            //物流费计算
            _log.info("订单物流费计算之前：{}___________________" + FastJsonUtils.toJsonString(sysOrderNew));
            sysOrderServiceImpl.setShipFee(sysOrderNew);
            _log.info("订单物流费计算后：{}___________________" + FastJsonUtils.toJsonString(sysOrderNew));
            sysOrderNew.setSplittedOrMerged(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue());
            List<SysOrderPackage> packages = sysOrderNew.getSysOrderPackageList();
            for (SysOrderPackage sysOrderPackage : packages) {
                SysOrderPackage sysOrderPackageNew = new SysOrderPackage();
                sysOrderPackageNew.setId(sysOrderPackage.getId());
                sysOrderPackageNew.setEstimateShipCost(sysOrderPackage.getEstimateShipCost());
                sysOrderPackageMapper.updateByPrimaryKeySelective(sysOrderPackageNew);
                for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                    SysOrderPackageDetail sysOrderPackageDetailNew = new SysOrderPackageDetail();
                    sysOrderPackageDetailNew.setSellerShipFee(sysOrderPackageDetail.getSellerShipFee());
                    sysOrderPackageDetailNew.setSupplierShipFee(sysOrderPackageDetail.getSupplierShipFee());
                    sysOrderPackageDetailNew.setId(sysOrderPackageDetail.getId());
                    // 分仓定价业务  重新设置商品价格 订单价格 根据仓库ID
                    orderAmount = reSetItemPrice(orderAmount, sysOrderPackage, sysOrderPackageDetail, sysOrderPackageDetailNew);
                    sysOrderPackageDetailMapper.updateByPrimaryKeySelective(sysOrderPackageDetailNew);
                }
            }
            sysOrderNew.setOrderAmount(orderAmount); // 重新计算订单成本价
            //设置预估利润、利润率
            systemOrderCommonService.setGrossMarginAndProfitMarginAndTotal(sysOrderNew);
            if (sysOrderNew.getIsConvertOrder().equalsIgnoreCase(Constants.isConvertOrder.NO)) {
                sysOrderNew.setGrossMargin(null);
                sysOrderNew.setProfitMargin(null);
            }
            sysOrderNewMapper.updateOrder(sysOrderNew);
        });

        _log.info("________________撤销合并系统订单 {} 操作成功________________", sysOrderIds);
        for (String id : sysOrderIds) {
            sysOrderLogService.insertSelective(        //添加订单操作日志
                    new SysOrderLog(id,
                            OrderHandleLogEnum.Content.REVOCATION_PACKAGE_MERGE.revocationPackageMerge(id),
                            OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                            loginUserInfo.getUserDTO().getUserName()));
        }
        for (String s : sysOrderIds) {
            redissLockUtil.unlock(s);
        }
    }

    public BigDecimal reSetItemPrice(BigDecimal orderAmount, SysOrderPackage sysOrderPackage, SysOrderPackageDetail sysOrderPackageDetail, SysOrderPackageDetail sysOrderPackageDetailNew) {
        String result = remoteCommodityService.test("1", "1", null, null, null, null,
                sysOrderPackageDetail.getSku(), null, null);
        String data = Utils.returnRemoteResultDataString(result, "调用商品服务异常");
        JSONObject parse1 = (JSONObject) JSONObject.parse(data);
        String pageInfo = parse1.getString("pageInfo");
        JSONObject parse2 = (JSONObject) JSONObject.parse(pageInfo);
        JSONArray list1 = parse2.getJSONArray("list");
        List<CommoditySpec> commodityDetails = list1.toJavaList(CommoditySpec.class);
        for (CommoditySpec commodityDetail : commodityDetails) {
            List<SkuInventoryVo> inventoryList = commodityDetail.getInventoryList();
            int count = 0;
            if (CollectionUtils.isNotEmpty(inventoryList)) {  //TODO 仓库分仓定价业务  匹配到相同仓库ID的则取仓库商品价
                for (SkuInventoryVo skuInventoryVo : inventoryList) {
                    if (String.valueOf(skuInventoryVo.getWarehouseId()).equals(sysOrderPackage.getDeliveryWarehouseId())) {
                        count++;
                        //商品系统成本价
                        sysOrderPackageDetailNew.setSkuCost(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                        //商品系统单价
                        sysOrderPackageDetailNew.setSkuPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                    }
                }
                if (count == 0) {
                    //商品系统成本价
                    sysOrderPackageDetailNew.setSkuCost(commodityDetail.getCommodityPriceUs());
                    //商品系统单价
                    sysOrderPackageDetailNew.setSkuPrice(commodityDetail.getCommodityPriceUs());
                }

            } else {
                //商品系统成本价
                sysOrderPackageDetailNew.setSkuCost(commodityDetail.getCommodityPriceUs());
                //商品系统单价
                sysOrderPackageDetailNew.setSkuPrice(commodityDetail.getCommodityPriceUs());
            }
            orderAmount = orderAmount.add(sysOrderPackageDetailNew.getSkuPrice().multiply(BigDecimal.valueOf(sysOrderPackageDetail.getSkuQuantity())));
        }
        return orderAmount;
    }

    public void reSetItemPriceByWarehouse(SysOrderPackage sysOrderPackage, SysOrderPackageDetail sysOrderPackageDetail, SysOrderPackageDetail sysOrderPackageDetailNew) {
        String result = remoteCommodityService.test("1", "1", null, null, null, null,
                sysOrderPackageDetail.getSku(), null, null);
        String data = Utils.returnRemoteResultDataString(result, "调用商品服务异常");
        JSONObject parse1 = (JSONObject) JSONObject.parse(data);
        String pageInfo = parse1.getString("pageInfo");
        JSONObject parse2 = (JSONObject) JSONObject.parse(pageInfo);
        JSONArray list1 = parse2.getJSONArray("list");
        List<CommoditySpec> commodityDetails = list1.toJavaList(CommoditySpec.class);
        for (CommoditySpec commodityDetail : commodityDetails) {
            List<SkuInventoryVo> inventoryList = commodityDetail.getInventoryList();
            int count = 0;
            if (CollectionUtils.isNotEmpty(inventoryList)) {  //TODO 仓库分仓定价业务  匹配到相同仓库ID的则取仓库商品价
                for (SkuInventoryVo skuInventoryVo : inventoryList) {
                    if (String.valueOf(skuInventoryVo.getWarehouseId()).equals(sysOrderPackage.getDeliveryWarehouseId())) {
                        count++;
                        //商品系统单价
                        sysOrderPackageDetailNew.setSkuCost(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                    }
                }
            } else {
                if (count == 0) {
                    //商品系统单价
                    sysOrderPackageDetailNew.setSkuCost(commodityDetail.getCommodityPriceUs());
                }
            }
        }
    }

    /**
     * 预估物流费存在时设置毛利和利润率
     *
     * @param sysOrder
     */
    public void setGrossMarginAndProfitMargin(SysOrderNew sysOrder) {
        BigDecimal commoditiesAmount = sysOrder.getCommoditiesAmount() == null ? new BigDecimal(0) : sysOrder.getCommoditiesAmount();//订单货款
        BigDecimal shippingServiceCost = sysOrder.getShippingServiceCost() == null ? new BigDecimal(0) : sysOrder.getShippingServiceCost();//平台运费（卖家填的）
        BigDecimal buyerActualPaidAmount = commoditiesAmount.add(shippingServiceCost);//买家实付款

        BigDecimal platformCommission = new BigDecimal("0");   //平台佣金
        if (commoditiesAmount.compareTo(new BigDecimal("0")) != 0) {
            if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
                platformCommission = (commoditiesAmount.add(shippingServiceCost)).multiply(PlatformCommissionEnum.AMAZON.getValue()).setScale(2, BigDecimal.ROUND_DOWN);
            } else if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
                platformCommission = (commoditiesAmount.add(shippingServiceCost)).multiply(PlatformCommissionEnum.EBAY.getValue()).setScale(2, BigDecimal.ROUND_DOWN);
            } else if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
                platformCommission = (commoditiesAmount.add(shippingServiceCost)).multiply(PlatformCommissionEnum.WISH.getValue()).setScale(2, BigDecimal.ROUND_DOWN);
            }
        }
        BigDecimal orderAmount = sysOrder.getOrderAmount() == null ? new BigDecimal(0) : sysOrder.getOrderAmount();//商品成本
        BigDecimal estimateShipCost = sysOrder.getEstimateShipCost() == null ? new BigDecimal(0) : sysOrder.getEstimateShipCost();//预估物流费
        BigDecimal interest = sysOrder.getInterest() == null ? new BigDecimal(0) : sysOrder.getInterest();//支付提现利息
        BigDecimal cost = orderAmount.add(estimateShipCost).add(interest).add(platformCommission.setScale(2, BigDecimal.ROUND_DOWN));//成本
        BigDecimal grossMargin = null;//毛利可为负值
        BigDecimal profitMargin = null;

        _log.info("计算利润:平台订单金额（商品总额{} + 订单运费{}）- 成本支出（平台佣金{} + 商品成本{} + 运费{}）", commoditiesAmount, shippingServiceCost, platformCommission, orderAmount, estimateShipCost);
        grossMargin = buyerActualPaidAmount.subtract(cost);//毛利可为负值
        if (new BigDecimal(0).compareTo(buyerActualPaidAmount) != 0) {
            profitMargin = grossMargin.divide(buyerActualPaidAmount, 2, BigDecimal.ROUND_DOWN);//利润率可为负
        }
        //存在未关联的平台SKU、未选中仓库、物流等信息不全情况下不展示预估利润、运费、商品价格等
        sysOrder.setGrossMargin(grossMargin);
        sysOrder.setProfitMargin(profitMargin);
        sysOrder.setTotal(sysOrder.getOrderAmount().add(sysOrder.getEstimateShipCost())); //设置总售价'
    }
}
