package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.enums.OrderHandleEnum;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListSku;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.MappingShopDTO;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.QuerySkuMapForOrderVo;
import com.rondaful.cloud.order.entity.SkuMapBind;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.commodity.CommodityBase;
import com.rondaful.cloud.order.entity.commodity.CommoditySpec;
import com.rondaful.cloud.order.entity.commodity.SkuInventoryVo;
import com.rondaful.cloud.order.entity.orderRule.OrderRule;
import com.rondaful.cloud.order.entity.orderRule.OrderRuleWithBLOBs;
import com.rondaful.cloud.order.entity.orderRule.SellerSkuMap;
import com.rondaful.cloud.order.entity.orderRule.SkuMapRule;
import com.rondaful.cloud.order.entity.supplier.OrderInvDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.enums.ConvertSysStatusEnum;
import com.rondaful.cloud.order.enums.LogisticsStrategyCovertToLogisticsLogisticsType;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.enums.PlatformRuleCovertToUserServicePlatformEnum;
import com.rondaful.cloud.order.enums.SkuBindEnum;
import com.rondaful.cloud.order.enums.SkuEnmus;
import com.rondaful.cloud.order.mapper.OrderRuleMailMapper;
import com.rondaful.cloud.order.mapper.OrderRuleWarehouseMapper;
import com.rondaful.cloud.order.mapper.SellerSkuMapMapper;
import com.rondaful.cloud.order.model.dto.remoteUser.GetSupplyChainByUserIdDTO;
import com.rondaful.cloud.order.model.dto.remoteseller.GetByplatformSkuAndSiteDTO;
import com.rondaful.cloud.order.model.dto.seller.GetResultPublishListingVO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderReceiveAddressDTO;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.IOrderRuleService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.SkuMapRuleService;
import com.rondaful.cloud.order.utils.CheckOrderUtils;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.MappingOrderRuleUtil;
import com.rondaful.cloud.order.utils.OrderUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Service
public class SkuMapServiceImpl implements ISkuMapService {
    @Autowired
    private OrderRuleServiceImpl orderRuleServiceImpl;
    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private IOrderRuleService orderRuleService;
    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;
    @Autowired
    private RemoteSellerService remoteSellerService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private SkuMapRuleService skuMapRuleService;
    @Autowired
    private SellerSkuMapMapper sellerSkuMapMapper;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    @Autowired
    OrderRuleWarehouseMapper orderRuleWarehouseMapper;
    @Autowired
    private OrderRuleMailMapper orderRuleMailMapper;

    private static Logger LOGGER = LoggerFactory.getLogger(SkuMapServiceImpl.class);

    @Override
    public List<SysOrder> orderMapByOrderList(String platform, List<SysOrder> orders) {
        LOGGER.info(platform + "平台____________开始订单转入：" + FastJsonUtils.toJsonString(orders));
        HashSet<String> plSKUSet = new HashSet<>();

        this.orderMap(platform, orders, plSKUSet);//TODO

        if (plSKUSet.size() == 0) {
            LOGGER.error(platform + "转单日志 平台订单转入映射完全失败，id为：" + this.getOrderIds(orders).toString());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "ORDER_MAP_ERROR");
        }
        List<CommodityBase> commodityBaseList;
        try {
            String result = remoteCommodityService.getCommodityListBySystemSKU(new ArrayList<>(plSKUSet));
            String data = Utils.returnRemoteResultDataString(result, "转单日志 查询sku列表对应商品信息时 商品服务异常");
            if (StringUtils.isBlank(data)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "COMMODITY_ERROR");
            }
            commodityBaseList = JSONObject.parseArray(data, CommodityBase.class);
        } catch (Exception e) {
            LOGGER.error(platform + " 转单日志 平台订单转入查询商品完全失败，id为： " + this.getOrderIds(orders).toString()
                    + "映射完成的品连sku为： " + this.getOrderSku(plSKUSet), e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "ORDER_MAP_ERROR");
        }
        try {
            String str = null;
            for (SysOrder order : orders) {
                try {
                    if (order.getSkus() == null || order.getSkus().size() == 0)   //订单中没有匹配成功的，不用匹配直接循环下一个
                    {
                        continue;
                    }
                    List<SysOrderDetail> list = new ArrayList<>();
                    List<SysOrderDetail> detailList = order.getSysOrderDetails().stream().filter(x -> x.getConverSysDetailStatus() == (byte) 1).collect(Collectors.toList());
                    List<SysOrderDetail> detailFailureList = order.getSysOrderDetails().stream().filter(x -> x.getConverSysDetailStatus() == (byte) 2).collect(Collectors.toList());
                    //分组统计key:Sku,value:SkuQty
                    Map<String, Integer> map = detailList.stream().collect(Collectors.groupingBy(SysOrderDetail::getSku, Collectors.summingInt(SysOrderDetail::getSkuQuantity)));
                    Set<String> keySet = map.keySet();
                    //根据对象的属性去重
                    List<SysOrderDetail> distinctDetailList = detailList.stream().collect(
                            Collectors.collectingAndThen(toCollection(() ->
                                    new TreeSet<>(Comparator.comparing(SysOrderDetail::getSku))), ArrayList::new));
                    //将相同Sku合并，SkuQty相加
                    distinctDetailList = distinctDetailList.stream().map(x -> {
                        if (keySet.contains(x.getSku())) {
                            x.setSkuQuantity(map.get(x.getSku()));
                        }
                        return x;
                    }).collect(Collectors.toList());
                    //将具有相同SKU的订单项ID拼接
                    for (SysOrderDetail distinct : distinctDetailList) {
                        String distinctSKU = distinct.getSku();
                        for (SysOrderDetail detail : detailList) {
                            String detailSourceID = detail.getSourceOrderId();
                            String detailSourceItemID = detail.getSourceOrderLineItemId();
                            String sku = detail.getSku();
                            if (distinctSKU.equals(sku)) {
                                String distinctSourceID = distinct.getSourceOrderId();
                                String distinctSourceItemID = distinct.getSourceOrderLineItemId();
                                if (StringUtils.isNotBlank(detailSourceID) && StringUtils.isNotBlank(detailSourceItemID)) {
                                    if (StringUtils.isNotBlank(distinctSourceID) && StringUtils.isNotBlank(distinctSourceItemID)) {
                                        if (!distinctSourceItemID.contains(detailSourceItemID))
                                            distinct.setSourceOrderLineItemId(distinctSourceItemID + "#" + detailSourceItemID);
                                    }
                                }
                            }
                        }
                    }
                    list.addAll(distinctDetailList);
                    list.addAll(detailFailureList);
                    order.setSysOrderDetails(list);

                    this.setOrderBaseData(order, commodityBaseList);//TODO

                    // 订单规则匹配，设置仓库和发货方式
                    orderRuleService.mappingOrderRuleNew(platform, order);
                    if (StringUtils.isNotBlank(order.getDeliveryWarehouseCode())
                            && StringUtils.isNotBlank(order.getShippingCarrierUsedCode())) {
                        try {
                            LOGGER.info("yaomingyaomingyaoming + 订单id：" + order.getSourceOrderId() + "*仓库code*" + order.getDeliveryWarehouseCode() + "*国家双字母简写*" + order.getShipToCountry()
                                    + "*物流方式列表*" + order.getDeliveryMethodCode() + "*重量 g*" + order.getTotalWeight().intValue());

                            // TODO : 判断SKU在该仓库是否缺货，如果缺货则调用采购模块的添加采购单接口
                            List<SysOrderDetail> sysOrderDetails = order.getSysOrderDetails();
                            String deliveryWarehouseCode = order.getDeliveryWarehouseCode();
                            this.notifyPurchasingSystem(sysOrderDetails, deliveryWarehouseCode);

                            //TODO
                            str = systemOrderCommonService.calculateLogisticFeeBySKUS(order);
                            BigDecimal cny_amount = new BigDecimal(str);
                            if (cny_amount != null) {
                                order.setEstimateShipCost(cny_amount);
                                order.setTotal(order.getOrderAmount().add(order.getEstimateShipCost()));//订单总售价:预估物流费+商品金额
                            } else
                                LOGGER.info("转单日志 系统 " + platform + " id为：" + order.getSourceOrderId() +
                                        " 的订单 转入调用邮费试算没有值，参数：仓库：" + order.getDeliveryMethodCode() + " 发货方式："
                                        + order.getDeliveryMethodCode() + " 国家：" + order.getShipToCountry() + " 重量：" + order.getTotalWeight().intValue());
                        } catch (Exception e) {
                            LOGGER.error("转单日志 系统 " + platform + " id为：" + order.getSourceOrderId() + " 的订单 转入调用邮费试算异常 erp返回值：" + str, e);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("转单日志 系统 " + platform + " id为：" + order.getSourceOrderId() + " 的订单转入设置基本信息异常", e);
                }
            }

            LOGGER.info(platform + "平台 *****************************************************    ____________   结束转入位置1：" + FastJsonUtils.toJsonString(orders));
            return orders;
        } catch (Exception e) {
            LOGGER.error("转单日志 订单设置参数异常", e);
            LOGGER.info(platform + "平台 *****************************************************    ____________   结束转入位置2：" + FastJsonUtils.toJsonString(orders));
            return orders;
        }
    }

    public List<SysOrderDTO> orderMapByOrderListNew(String platform, List<SysOrderDTO> sysOrderDTOList) {

        LOGGER.info("平台{} 订单开始进行转入，数据为: {}", platform, FastJsonUtils.toJsonString(sysOrderDTOList));

        this.orderMapNew(platform, sysOrderDTOList);

        judgeSkuMapResult(sysOrderDTOList);

        List<SysOrderDTO> exceptionSysOrderList = new ArrayList<>(sysOrderDTOList.size());
        for (SysOrderDTO sysOrderDTO : sysOrderDTOList) {
            this.orderDetailMap(platform, sysOrderDTO, exceptionSysOrderList);
        }

        if (exceptionSysOrderList.size() == sysOrderDTOList.size()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "ORDER_MAP_ERROR");
        }

        return sysOrderDTOList;
    }

    public void judgeSkuMapResult(List<SysOrderDTO> sysOrderDTOList) {
        if (CollectionUtils.isNotEmpty(sysOrderDTOList)) {
            boolean flag = false;
            for (SysOrderDTO sysOrderDTO : sysOrderDTOList) {
                if (sysOrderDTO.getConverSysStatus() != ConvertSysStatusEnum.CONVERT_FAILURE.getValue()) {
                    flag = true;
                }
            }
            if (!flag) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "ORDER_MAP_ERROR");
            }

        }
    }

    /**
     * 填充订单数据详情
     *
     * @param platform              平台
     * @param sysOrderDTO           {@link SysOrderDTO}
     * @param exceptionSysOrderList {@link List<SysOrderDTO>}
     */
    private void orderDetailMap(String platform, SysOrderDTO sysOrderDTO, List<SysOrderDTO> exceptionSysOrderList) {
        String sourceOrderId = sysOrderDTO.getSourceOrderId();
        try {
            List<String> plSkus = sysOrderDTO.getSkus();
            if (CollectionUtils.isEmpty(plSkus)) {
                LOGGER.error("平台{} 订单{} 没有匹配到对应的品连sku, 跳过该订单", platform, sourceOrderId);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单" + sourceOrderId + "没有匹配到对应的品连sku, 跳过该订单");
            }
            // 调用商品服务，获取品连商品信息
            Integer sellerPlId = sysOrderDTO.getSellerPlId();
            LOGGER.error("请求商品的参数sellerId：{},plSkus:{}", sellerPlId, plSkus);
            List<CommodityBase> commodityBaseList = systemOrderCommonService.getCommodityBySkuList(sellerPlId, plSkus);
            LOGGER.error("判断是否可售、下架，调用商品服务返回的数据：{}", FastJsonUtils.toJsonString(commodityBaseList));
            if (CollectionUtils.isEmpty(commodityBaseList)) {
                sysOrderDTO.setConverSysStatus(ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
                sysOrderDTO.getSysOrderDetailList().forEach(sysOrderDetailDTO ->
                        sysOrderDetailDTO.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_FAILURE.getValue()));
            }
            LOGGER.error("原始的数据为: {}", FastJsonUtils.toJsonString(sysOrderDTO));

            boolean judgeSkuResult = CheckOrderUtils.judgeSkuState(commodityBaseList);
            if (!judgeSkuResult) {
                sysOrderDTO.setConverSysStatus(ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
                sysOrderDTO.getSysOrderDetailList().forEach(sysOrderDetailDTO ->
                        sysOrderDetailDTO.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_FAILURE.getValue()));
                LOGGER.error("平台{} 订单{} 判断sku是否可售，是否上架状态不通过, 跳过该订单", platform, sourceOrderId);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单" + sourceOrderId + "SKU不可售或已下架");
            }

       /*     // 去重  TODO 转单取消去重操作，未来可能恢复
            this.distinctDetail(sysOrderDTO);
            LOGGER.debug("去重后的数据为: {}", FastJsonUtils.toJsonString(sysOrderDTO));*/

            // 设置商品数据
            this.setOrderBaseDataNew(sysOrderDTO, commodityBaseList);
            LOGGER.error("设置商品数据后的数据为: {}", FastJsonUtils.toJsonString(sysOrderDTO));

            // 设置包裹信息
            this.setPackageData(sysOrderDTO);
            LOGGER.error("设置包裹数据后的数据为: {}", FastJsonUtils.toJsonString(sysOrderDTO));

            // 计算费用信息  todo  针对转入成功的订单才进行运费计算  OK
            if (sysOrderDTO.getConverSysStatus() == ConvertSysStatusEnum.CONVERT_SUCCESS.getValue()) {
                this.calculateFee(sysOrderDTO);
                LOGGER.error("计算费用信息后的数据为: {}", FastJsonUtils.toJsonString(sysOrderDTO));
            }
        } catch (Exception e) {
            LOGGER.error("平台{} 订单商品映射异常，订单为: {}", platform, sourceOrderId, e);
            sysOrderDTO.setConverSysStatus(ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
            sysOrderDTO.getSysOrderDetailList().forEach(sysOrderDetailDTO -> {
                sysOrderDetailDTO.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
            });
            exceptionSysOrderList.add(sysOrderDTO);
        }
    }

    /**
     * 设置包裹信息 - 转单默认一个包裹
     *
     * @param sysOrderDTO {@link SysOrderDTO}
     */
    private void setPackageData(SysOrderDTO sysOrderDTO) {
        for (SysOrderDetailDTO sysOrderDetailDTO : sysOrderDTO.getSysOrderDetailList()) {
            if (sysOrderDetailDTO.getConverSysDetailStatus() == ConvertSysStatusEnum.CONVERT_FAILURE.getValue()) {
                sysOrderDetailDTO.setBindStatus(SkuBindEnum.UNBIND.getValue());
            }
        }
        List<String> skus = new ArrayList<>();
        List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList = new ArrayList<>();
        sysOrderDTO.getSysOrderDetailList().forEach(sysOrderDetailDTO -> {
//            if ((int) sysOrderDetailDTO.getConverSysDetailStatus() == ConvertSysStatusEnum.CONVERT_SUCCESS.getValue()) {
            // 转换成功的sku才进行包裹打包
            SysOrderPackageDetailDTO sysOrderPackageDetailDTO = new SysOrderPackageDetailDTO();
            BeanUtils.copyProperties(sysOrderDetailDTO, sysOrderPackageDetailDTO);
            sysOrderPackageDetailDTO.setWeight(sysOrderDetailDTO.getWeight());
            sysOrderPackageDetailDTO.setSkuCost(sysOrderDetailDTO.getItemCost());
            sysOrderPackageDetailDTO.setSkuUrl(sysOrderDetailDTO.getItemUrl());
            sysOrderPackageDetailDTO.setSkuName(sysOrderDetailDTO.getItemName());
            sysOrderPackageDetailDTO.setSkuNameEn(sysOrderDetailDTO.getItemNameEn());
            sysOrderPackageDetailDTO.setSkuAttr(sysOrderDetailDTO.getItemAttr());
            sysOrderPackageDetailDTO.setSkuPrice(sysOrderDetailDTO.getItemPrice());
            sysOrderPackageDetailDTO.setSupplierId(sysOrderDetailDTO.getSupplierId());
            sysOrderPackageDetailDTO.setSupplierName(sysOrderDetailDTO.getSupplierName());
            sysOrderPackageDetailDTO.setSourceSku(sysOrderDetailDTO.getSourceSku());
            sysOrderPackageDetailDTO.setBindStatus(sysOrderDetailDTO.getBindStatus());
            String sku = sysOrderPackageDetailDTO.getSku();
            skus.add(sku);
            sysOrderPackageDetailDTOList.add(sysOrderPackageDetailDTO);
//            }
        });
        List<SysOrderPackageDTO> sysOrderPackageDTOList = sysOrderDTO.getSysOrderPackageList();
        // 转换订单，默认一个包裹
        SysOrderPackageDTO sysOrderPackageDTO = sysOrderPackageDTOList.get(0);
        sysOrderPackageDTO.setSourceOrderId(sysOrderDTO.getSourceOrderId());
        sysOrderPackageDTO.setSellerPlAccount(sysOrderDTO.getSellerPlAccount());
        sysOrderPackageDTO.setShipToCountry(sysOrderDTO.getSysOrderReceiveAddress().getShipToCountry());
        sysOrderPackageDTO.setShipToPostalCode(sysOrderDTO.getSysOrderReceiveAddress().getShipToPostalCode());
        sysOrderPackageDTO.setSysOrderPackageDetailList(sysOrderPackageDetailDTOList);
        sysOrderPackageDTO.setSkus(skus);
        sysOrderPackageDTO.setOrderTrackId(OrderUtils.getPLTrackNumber());
        sysOrderPackageDTO.setSysOrderId(OrderUtils.getPLOrderNumber());
        sysOrderPackageDTO.setEmpowerId(sysOrderDTO.getEmpowerId());
    }

    /**
     * 计算金额
     *
     * @param sysOrderDTO {@link SysOrderDTO}
     */
    private void calculateFee(SysOrderDTO sysOrderDTO) {
        Integer handOrder = 0;//是否手工单，0 否 1是
        List<SysOrderPackageDTO> sysOrderPackageDTOList = sysOrderDTO.getSysOrderPackageList();
        String sysOrderId = sysOrderDTO.getSysOrderId();
        BigDecimal totalEstimateShipCost = null;
        SysOrderReceiveAddressDTO sysOrderReceiveAddressDTO = sysOrderDTO.getSysOrderReceiveAddress();
        for (SysOrderPackageDTO sysOrderPackageDTO : sysOrderPackageDTOList) {

            String packageNumber = sysOrderPackageDTO.getOrderTrackId();

           /* // 包裹匹配仓库物流规则
            orderRuleService.mappingPackageOrderRule(platform, sysOrderPackageDTO, sysOrderDTO.getMarketplaceId(), platformShopId);*/

            if (null == sysOrderPackageDTO.getDeliveryWarehouseId()
                    || -1 == sysOrderPackageDTO.getDeliveryWarehouseId()) {
                continue;
            }
            LOGGER.info("订单id: {}, 包裹号: {}, 仓库为: {}, 国家为: {}, 物流方式为: {}",
                    sysOrderId, packageNumber, sysOrderPackageDTO.getDeliveryWarehouse(),
                    sysOrderReceiveAddressDTO.getShipToCountry(), sysOrderPackageDTO.getDeliveryMethod());
            //计算预估物流费之前非空判断
            if (sysOrderPackageDTO.getDeliveryWarehouseId() == null || sysOrderPackageDTO.getDeliveryMethodCode() == null) {
                return;
            }
            try {
                // 计算预估物流费
                Integer orderSource = sysOrderDTO.getOrderSource();
                BigDecimal estimateFee = systemOrderCommonService.calculatePackageLogisticFeeBySKUS(sysOrderPackageDTO,
                        sysOrderReceiveAddressDTO, orderSource, sysOrderDTO.getPlatformShopId(), handOrder);
                sysOrderPackageDTO.setEstimateShipCost(estimateFee);
                if (totalEstimateShipCost != null) {
                    totalEstimateShipCost = totalEstimateShipCost.add(estimateFee);
                } else {
                    totalEstimateShipCost = BigDecimal.ZERO.add(estimateFee);
                }
            } catch (Exception e) {
                LOGGER.info("订单id: {}, 包裹号: {}, 计算预估物流费异常", sysOrderId, packageNumber, e);
            }
        }

        //订单总售价:预估物流费+商品金额
        sysOrderDTO.setEstimateShipCost(totalEstimateShipCost);
        if (totalEstimateShipCost != null) {
            sysOrderDTO.setTotal(sysOrderDTO.getOrderAmount().add(totalEstimateShipCost));
        }
    }

    /**
     * 匹配物流规则
     *
     * @param platform    平台
     * @param sysOrderDTO {@link SysOrderDTO}
     */
    private void mappingOrderRule(String platform, SysOrderDTO sysOrderDTO) {
        // copy数据到SysOrder，主要是为了匹配仓库
        SysOrder sysOrder = new SysOrder();
        BeanUtils.copyProperties(sysOrderDTO, sysOrder);
        List<SysOrderDetailDTO> sysOrderDetailDTOList = sysOrderDTO.getSysOrderDetailList();
        for (SysOrderDetailDTO sysOrderDetailDTO : sysOrderDetailDTOList) {
            SysOrderDetail sysOrderDetail = new SysOrderDetail();
            BeanUtils.copyProperties(sysOrderDetailDTO, sysOrderDetail);
        }

        // 订单规则匹配，设置仓库和发货方式
        orderRuleService.mappingOrderRuleNew(platform, sysOrder);

        sysOrderDTO.getSysOrderPackageList().forEach(sysOrderDetailDTO -> {
            BeanUtils.copyProperties(sysOrder, sysOrderDetailDTO);
        });
    }

    /**
     * 去重，并设置回订单
     *
     * @param sysOrderDTO {@link SysOrderDTO}
     */
    private void distinctDetail(SysOrderDTO sysOrderDTO) {
        List<SysOrderDetailDTO> list = new ArrayList<>();
        // 转换成功的详情列表
        List<SysOrderDetailDTO> detailSuccessList = sysOrderDTO.getSysOrderDetailList().stream().filter(x ->
                x.getConverSysDetailStatus() == ConvertSysStatusEnum.CONVERT_SUCCESS.getValue()).collect(Collectors.toList());
        // 转换失败的详情列表
        List<SysOrderDetailDTO> detailFailureList = sysOrderDTO.getSysOrderDetailList().stream().filter(x ->
                x.getConverSysDetailStatus() == ConvertSysStatusEnum.CONVERT_FAILURE.getValue()).collect(Collectors.toList());
        //分组统计key:Sku,value:SkuQty
        Map<String, Integer> skuQuantityMap = detailSuccessList.stream().collect(
                Collectors.groupingBy(SysOrderDetailDTO::getSku,
                        Collectors.summingInt(SysOrderDetailDTO::getSkuQuantity)));
        //转换后根据品连sku去重
        List<SysOrderDetailDTO> distinctDetailList = detailSuccessList.stream().collect(
                Collectors.collectingAndThen(toCollection(() ->
                        new TreeSet<>(Comparator.comparing(SysOrderDetailDTO::getSku))), ArrayList::new));
        //将相同Sku合并，SkuQty相加
        Set<String> skuSet = skuQuantityMap.keySet();
        distinctDetailList = distinctDetailList.stream().map(x -> {
            String sku = x.getSku();
            if (skuSet.contains(sku)) {
                x.setSkuQuantity(skuQuantityMap.get(sku));
            }
            return x;
        }).collect(Collectors.toList());
        //将具有相同SKU的订单项ID拼接
        for (SysOrderDetailDTO distinct : distinctDetailList) {
            String distinctSKU = distinct.getSku();
            for (SysOrderDetailDTO detail : detailSuccessList) {
                String detailSourceID = detail.getSourceOrderId();
                String detailSourceItemID = detail.getSourceOrderLineItemId();
                String sku = detail.getSku();
                if (distinctSKU.equals(sku)) {
                    String distinctSourceID = distinct.getSourceOrderId();
                    String distinctSourceItemID = distinct.getSourceOrderLineItemId();
                    if (StringUtils.isNotBlank(detailSourceID) && StringUtils.isNotBlank(detailSourceItemID)) {
                        if (StringUtils.isNotBlank(distinctSourceID) && StringUtils.isNotBlank(distinctSourceItemID)) {
                            if (!distinctSourceItemID.contains(detailSourceItemID))
                                distinct.setSourceOrderLineItemId(distinctSourceItemID +
                                        Constants.SplitSymbol.HASH_TAG + detailSourceItemID);
                        }
                    }
                }
            }
        }
        list.addAll(distinctDetailList);
        list.addAll(detailFailureList);
        sysOrderDTO.setSysOrderDetailList(list);
    }

    @Override
    public void notifyPurchasingSystem(List<SysOrderDetail> sysOrderDetails, String deliveryWarehouseCode) {
        LOGGER.info("采购业务2.4版本暂时不上，先注释掉代码");
        /*for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
            String sysOrderId = sysOrderDetail.getSysOrderId();
            String sku = sysOrderDetail.getSku();
            String result = remoteSupplierService.getInventoryByParam(deliveryWarehouseCode, sku);
            LOGGER.info("订单={}的sku={}调用仓库服务的 getInventoryByParam 接口返回的信息是:{}",sysOrderId, sku, result);
            if (null == result) {
                LOGGER.error("订单={}的sku={} 没有从仓库{}获取到对应的库存信息",sysOrderId, sku, deliveryWarehouseCode);
                continue;
            }

            JSONObject InventoryResult = JSON.parseObject(result);

            Boolean success = InventoryResult.getBoolean("success");
            if (null == success || !success) {
                String msg = InventoryResult.getString("msg");
                String errorCode = InventoryResult.getString("errorCode");
                LOGGER.error("订单={}调用仓库接口失败，失败编码是：{}, 失败信息是：{}",sysOrderId, msg, errorCode);
                continue;
            }

            Integer inventory = InventoryResult.getInteger("data");
            if (null == inventory) {
                LOGGER.error("订单={}的sku={} 没有从仓库{}获取到对应的库存信息",sysOrderId, sku, deliveryWarehouseCode);
                continue;
            }

            Integer skuQuantity = sysOrderDetail.getSkuQuantity();
            if (inventory < skuQuantity) {
                Integer stockoutNumber = skuQuantity - inventory;
                LOGGER.info("订单={}的sku={} 在仓库{} 缺货，所需数量为{}, 仓库数量为{}, 缺货数量为{}",sysOrderId, sku, deliveryWarehouseCode,
                        skuQuantity, inventory, stockoutNumber);
                String suggestResult = remoteSupplierService.addSuggest(sku, sysOrderDetail.getSysOrderId(), deliveryWarehouseCode, stockoutNumber);
                LOGGER.info("订单={}的sku={}添加采购单返回的结果是:{}",sysOrderId, sku, suggestResult);
            }
        }*/
    }

    /**
     * 设置订单列表的订单映射结果
     * 区分出各个订单的转换结果，成功---失败---部分成功
     *
     * @param platform 平台
     * @param orders   订单列表
     */
    private void orderMapNew(String platform, List<SysOrderDTO> orders) {

        mappingSku(platform, orders);  //匹配单SKU、n数量

        for (SysOrderDTO order : orders) {
            boolean successFlag = false;
            boolean failFlag = false;
            HashSet<String> skus = new HashSet<>();//品连SKU集合
            List<SearchLogisticsListSku> plSkuList = new LinkedList<>();//品连SKU对象（SKU、数量）集合
            HashSet<String> platformSkuList = new HashSet<>(); //平台SKU集合
            for (SysOrderDetailDTO detail : order.getSysOrderDetailList()) {
                try {
                    String plSku = detail.getSku();
                    platformSkuList.add(detail.getPlatformSKU());
                    if (StringUtils.isNotBlank(plSku)) {
                        detail.setSku(plSku);
                        successFlag = true;
                        detail.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_SUCCESS.getValue());
                        skus.add(plSku);
                        SearchLogisticsListSku searchLogisticsListSku = new SearchLogisticsListSku();
                        searchLogisticsListSku.setSku(plSku);
                        searchLogisticsListSku.setSkuNumber(detail.getSkuQuantity());
                        plSkuList.add(searchLogisticsListSku);
                    } else {
                        failFlag = true;
                        detail.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
                    }
                } catch (Exception e) {
                    LOGGER.error("平台{} 转单匹配sku异常, 平台订单id为: {}, 平台sku为: {}", platform, order.getSourceOrderId(),
                            detail.getPlatformSKU(), e);
                    failFlag = true;
                    detail.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
                }
            }

            setConverSysStatus(order, successFlag, failFlag);

            if (CollectionUtils.isEmpty(skus)) {
                continue;
            } else {
                order.setSkus(new ArrayList<>(skus));
            }

            if (setSupplyChainInfo(platform, order, order.getSourceOrderId())) {
                continue;
            }

            if (order.getConverSysStatus() == ConvertSysStatusEnum.CONVERT_SUCCESS.getValue()) {
                order.setHandOrder(0);//设置是否手工单
                mappingLogisticsRule(platform, order.getEmpower(), order, plSkuList, new ArrayList<>(platformSkuList), true);
            } else {
                order.getSysOrderPackageList().get(0).setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_ORDER_RULE_FAILURE.mappingOrderRuleFailure());
            }
        }
    }


    private void setConverSysStatus(SysOrderDTO order, boolean successFlag, boolean failFlag) {
        if (successFlag && failFlag) {
            order.setConverSysStatus(ConvertSysStatusEnum.CONVERT_PORTION_SUCCESS.getValue());
        } else if (successFlag) {
            order.setConverSysStatus(ConvertSysStatusEnum.CONVERT_SUCCESS.getValue());
        } else if (failFlag) {
            order.setConverSysStatus(ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
        }
    }

    private boolean setSupplyChainInfo(String platform, SysOrderDTO order, String sourceOrderId) {
        Integer sellerPlId = order.getSellerPlId();
        GetSupplyChainByUserIdDTO getSupplyChainByUserIdDTO = systemOrderCommonService.getSupplyChinByUserId(
                sellerPlId, Constants.System.PLATFORM_TYPE_SELLER);

        LOGGER.info("平台{}, 平台订单id为: {}, 查询到的供应商公司为: {}", platform,
                sourceOrderId, FastJsonUtils.toJsonString(getSupplyChainByUserIdDTO));

        if (null == getSupplyChainByUserIdDTO || null == getSupplyChainByUserIdDTO.getSupplyId()
                || StringUtils.isBlank(getSupplyChainByUserIdDTO.getSupplyChainCompanyName())) {
            LOGGER.info("平台{} 转单没有查询到对应的供应链公司, 平台订单id为: {}, sellerPlId为: {}", platform,
                    sourceOrderId, sellerPlId);
            return true;
        }
        order.setSupplyChainCompanyId(getSupplyChainByUserIdDTO.getSupplyId());
        order.setSupplyChainCompanyName(getSupplyChainByUserIdDTO.getSupplyChainCompanyName());
        return false;
    }

    private void mappingSku(String platform, List<SysOrderDTO> orders) {
        try {
            List<QuerySkuMapForOrderVo> list = new ArrayList<>();
            for (SysOrderDTO order : orders) {  //设置数据后，调用商品服务的SKU映射
                getEmpower(platform, order, order.getSourceOrderId());//获取授权信息
                if (order.getEmpower() == null) {
                    continue;
                }
                for (SysOrderDetailDTO sysOrderDetailDTO : order.getSysOrderDetailList()) {
                    QuerySkuMapForOrderVo vo = new QuerySkuMapForOrderVo();
                    vo.setAuthorizationId(String.valueOf(order.getEmpowerId()));
                    vo.setPlatform(platform);
                    vo.setPlatformSku(sysOrderDetailDTO.getPlatformSKU());
                    vo.setSourceOrderId(sysOrderDetailDTO.getSourceOrderId());
                    vo.setSourceOrderLineItemId(sysOrderDetailDTO.getSourceOrderLineItemId());
                    vo.setSellerId(String.valueOf(order.getSellerPlId()));
                    list.add(vo);
                }
            }
            String result = remoteCommodityService.getSkuMapForOrder(list);
            String data = Utils.returnRemoteResultDataString(result, "商品服务异常");
            List<QuerySkuMapForOrderVo> mapForOrderVos = JSONObject.parseArray(data, QuerySkuMapForOrderVo.class);
            Map<String, List<QuerySkuMapForOrderVo>> map = mapForOrderVos.stream().collect(Collectors.groupingBy(vo -> vo.getSourceOrderLineItemId()));
            for (SysOrderDTO order : orders) {
                for (SysOrderDetailDTO dto : order.getSysOrderDetailList()) {
                    String sourceOrderLineItemId = dto.getSourceOrderLineItemId();
                    if (MapUtils.isNotEmpty(map) && CollectionUtils.isNotEmpty(map.get(sourceOrderLineItemId)) && map.get(sourceOrderLineItemId).get(0) != null) {
                        List<SkuMapBind> skuBinds = map.get(sourceOrderLineItemId).get(0).getSkuBinds();
                        if (CollectionUtils.isNotEmpty(skuBinds)) {
                            dto.setSku(skuBinds.get(0).getSystemSku());
                            BigDecimal skuNum = new BigDecimal(skuBinds.get(0).getSkuNum()).multiply(new BigDecimal(dto.getSkuQuantity()));
                            dto.setSkuQuantity(Integer.valueOf(String.valueOf(skuNum)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("匹配SKU出错，全部订单置为转入失败：{}", e);
            for (SysOrderDTO order : orders) {
                order.setConverSysStatus(ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
                order.getSysOrderDetailList().forEach(dto -> dto.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_FAILURE.getValue()));
            }
        }
    }

    private void getEmpower(String platform, SysOrderDTO order, String sourceOrderId) {
        Empower empower = null;
        try {
            if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())) {
                empower = systemOrderCommonService.findOneEmpowerByAccount(
                        PlatformRuleCovertToUserServicePlatformEnum.getOtherPlatformCode(platform),
                        null, order.getSite(), String.valueOf(order.getEmpowerId()));

            } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())) {
                empower = systemOrderCommonService.findOneEmpowerByAccount(
                        PlatformRuleCovertToUserServicePlatformEnum.getOtherPlatformCode(platform),
                        null, order.getSite(), String.valueOf(order.getEmpowerId()));

            } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())) {
                if (order.getEmpowerId() != null && order.getSellerPlId() != null && StringUtils.isNotBlank(order.getSellerPlAccount())) {
                    empower = new Empower() {{
                        setEmpowerid(order.getEmpowerId());
                        setPinlianid(order.getSellerPlId());
                        setPinlianaccount(order.getSellerPlAccount());
                        setRentstatus(Integer.valueOf(order.getShopType()));
                    }};
                } else {
                    LOGGER.error("速卖通转单授权信息传入不全");
                }
            }
            if (empower == null) {
                order.setConverSysStatus(ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
                order.getSysOrderDetailList().forEach(x -> x.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_FAILURE.getValue()));
                return;
            }

            LOGGER.debug("平台: {}, 平台订单id为: {}, 匹配到的授权信息为: {}", platform, sourceOrderId, FastJsonUtils.toJsonString(empower));
            order.setEmpower(empower);
            order.setSellerPlAccount(empower.getPinlianaccount());
            order.setSellerPlId(empower.getPinlianid());
            order.setEmpowerId(empower.getEmpowerid());
            order.setShopType(empower.getRentstatus() == 0 ? "PERSONAL" : "RENT");
            order.setPlatformSellerId(empower.getThirdpartyname());
        } catch (Exception e) {
            LOGGER.error("平台{} 转单异常, 订单ID为: {}", platform, sourceOrderId, e);
            order.setConverSysStatus(ConvertSysStatusEnum.CONVERT_FAILURE.getValue());
            order.getSysOrderDetailList().forEach(x -> x.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_FAILURE.getValue()));
        }
    }

    /**
     * 物流规则匹配(SKU刊登的物流规则、本地仓库规则（卖家->管理后台）、本地物流规则（卖家->管理后台）)
     *
     * @param platform             平台来源                 amazon  ebay   wish
     * @param empower              授权信息
     * @param order                订单对象
     * @param plSkuList            品连SKU、数量 对象集合   获取可用仓库、可用物流信息使用（不需要去重）
     * @param platformSkuList      平台SKU集合              匹配刊登仓库使用（需要去重）
     * @param isMappingPublishRule 是否匹配刊登规则
     */
    public void mappingLogisticsRule(String platform, Empower empower, SysOrderDTO order, List<SearchLogisticsListSku> plSkuList,
                                     List<String> platformSkuList, boolean isMappingPublishRule) {

        if (CollectionUtils.isEmpty(order.getSkus())) {
            //设置转入成功的SKU   去重
            order.setSkus(new ArrayList<>(plSkuList.stream().collect(Collectors.groupingBy(SearchLogisticsListSku::getSku)).keySet()));
        }
        HashMap<Object, Object> warehouseMap = new HashMap<>(); //查出SKU集合的可用仓库并添加进该Map
        if (getUserfulWarehouseBySkus(plSkuList, warehouseMap)) {
            LOGGER.error("平台订单ID：{}的SKU集合查不到任何可用仓库，直接返回", order.getSourceOrderId());
            return; //根据SKU查出可用的仓库，没有则跳过该订单的仓库规则匹配
        }
        //TODO 匹配刊登SKU物流规则START-------------------------------------------------------------------------------------
        LOGGER.error("平台订单ID：{}开始订单规则匹配", order.getSourceOrderId());
        if (CollectionUtils.isNotEmpty(order.getSysOrderPackageList())) {
            if (isMappingPublishRule) {
                List<GetResultPublishListingVO> publishListingVOS = findLogisticsAndWarehousingFormPlaceService(empower.getEmpowerid(), platform, platformSkuList);
                LOGGER.error("平台订单ID：{}开始进行刊登SKU规则匹配，匹配结果：{}", order.getSourceOrderId(), FastJsonUtils.toJsonString(publishListingVOS));
                if (CollectionUtils.isNotEmpty(publishListingVOS)) {
                    GetResultPublishListingVO getResultPublishListingVO = publishListingVOS.get(0);
                    Integer logisticsType = getResultPublishListingVO.getLogisticsType();
                    Integer warehouseId = getResultPublishListingVO.getWarehouseId();
                    List<LogisticsDetailVo> logisticsDetailVos = null;
                    WarehouseDTO warehouseDTO = null;
                    if (warehouseMap.containsKey(warehouseId)) {
                        warehouseDTO = getWarehouseDTO(warehouseId);
                        //物流信息集合
                        logisticsDetailVos = getLogisticsDetailVos(platform, order, plSkuList, logisticsType, warehouseId);
                    }
                    //仓库信息、物流信息均不为空时候才匹配，否则继续后面的物流规则匹配
                    if (CollectionUtils.isNotEmpty(logisticsDetailVos) && warehouseDTO != null) {
                        setPackageInfo(order, logisticsDetailVos, getResultPublishListingVO, logisticsType, warehouseDTO);
                        return;
                    }
                }
                LOGGER.error("平台订单ID：{}，匹配不到刊登SKU规则，继续往下进行仓库、物流规则的匹配！", order.getSourceOrderId());
            }
            try {
                // TODO 匹配仓库规则START-------------------------------------------------------------------------------------
                setTotalWeightAndBulk(order);
                Integer warehouseId = null;//仓库ID
                OrderRule orderRule = new OrderRule() {{
                    setSellerId(String.valueOf(order.getSellerPlId()));
                    setStatus(1);
                    setSellerAccount(order.getSellerPlAccount());
                    setPlatformMark("S");
                }};
                List<OrderRuleWithBLOBs> orderRuleWarehouseList = orderRuleWarehouseMapper.findAll(orderRule);//卖家仓库规则
                List<OrderRuleWithBLOBs> orderRuleMailList = orderRuleMailMapper.findAll(orderRule);  //查询卖家物流规则
                if (CollectionUtils.isNotEmpty(orderRuleWarehouseList)) {
                    OrderRuleWithBLOBs warehouseRule = filterRule(platform, order, orderRuleWarehouseList, warehouseMap, null, true);
                    warehouseId = getWarehouseId(warehouseRule);
                    if (warehouseId == null) {
                        warehouseId = getOrderRuleByCMS(platform, order, orderRule, warehouseMap);
                    } else {
                        //TODO 设置匹配仓库规则的日志
                        order.getSysOrderPackageList().get(0).setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_WAREHOUSE_RULE_SUCCESS.mappingWarehouseRuleSuccess(warehouseRule.getRuleName()));
                    }
                } else {
                    warehouseId = getOrderRuleByCMS(platform, order, orderRule, warehouseMap);
                }
                if (warehouseId == null) {
                    //TODO 设置匹配订单规则失败的日志
                    order.getSysOrderPackageList().get(0).setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_ORDER_RULE_FAILURE.mappingOrderRuleFailure());
                    return; //如果卖家、管理后台都匹配不到仓库ID就继续进行下一个订单的仓库规则匹配
                }
                if (setWarehouseInfo(order, warehouseId)) {
                    return;//设置仓库数据，根据仓库ID查不到仓库信息就继续进行下一个订单的仓库规则匹配
                }
                //TODO 匹配物流方式规则START-------------------------------------------------------------------------------------
                //根据仓库ID、综合优先的物流类型查询有效的物流信息
                List<LogisticsDetailVo> logisticsDetailVos = getLogisticsDetailVos(platform, order, plSkuList, warehouseId);
                if (CollectionUtils.isEmpty(logisticsDetailVos)) {
                    return;
                }
                Map<String, List<LogisticsDetailVo>> logisticsMap = logisticsDetailVos.stream().collect(Collectors.groupingBy(vo -> vo.getLogisticsCode()));
                if (CollectionUtils.isNotEmpty(orderRuleMailList)) {
                    OrderRuleWithBLOBs logisticsRule = filterRule(platform, order, orderRuleMailList, null, logisticsMap, false);
                    if (logisticsRule != null) {
                        getDeliveryLogisticsInfo(order, logisticsMap, logisticsRule);
                        if (StringUtils.isBlank(order.getSysOrderPackageList().get(0).getDeliveryMethod())) {
                            mappingCMSLogisticsRule(platform, order, orderRule, logisticsMap);
                        }
                    } else {
                        mappingCMSLogisticsRule(platform, order, orderRule, logisticsMap);
                    }
                } else {
                    mappingCMSLogisticsRule(platform, order, orderRule, logisticsMap);
                }
            } catch (Exception e) {
                LOGGER.error("转单异常日志：{}", e);
                throw e;
            } finally {
//                setMappingOrderRuleLog(order);// 插入匹配订单物流规则日志
            }
        }
    }

/*    public void setMappingOrderRuleLog(SysOrderDTO order) {
        for (SysOrderPackageDTO orderPackageDTO : order.getSysOrderPackageList()) {
            String deliveryWarehouse = orderPackageDTO.getDeliveryWarehouse();
            String deliveryMethod = orderPackageDTO.getDeliveryMethod();
            if (StringUtils.isBlank(orderPackageDTO.getMappingOrderRuleLog())) {
                if (StringUtils.isNotBlank(deliveryWarehouse) && StringUtils.isNotBlank(deliveryMethod)) {
                    orderPackageDTO.setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_ORDER_RULE_SUCCESS.mappingOrderRuleSuccess(deliveryWarehouse, deliveryMethod));
                } else if (StringUtils.isNotBlank(deliveryWarehouse)) {
                    orderPackageDTO.setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_ORDER_RULE_PARTLY_SUCCESS.mappingOrderRulePartlySuccess(deliveryWarehouse));
                } else {
                    orderPackageDTO.setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_ORDER_RULE_FAILURE.mappingOrderRuleFailure());
                }
            }
        }
    }*/

    public boolean setWarehouseInfo(SysOrderDTO order, Integer warehouseId) {
        WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(String.valueOf(warehouseId));
        if (warehouseDTO != null) {
            order.getSysOrderPackageList().stream().forEach(p -> {
                p.setDeliveryWarehouse(warehouseDTO.getWarehouseName());
                p.setDeliveryWarehouseId(warehouseDTO.getWarehouseId());
                p.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());
            });
        } else {
            return true;
        }
        return false;
    }

    public void setTotalWeightAndBulk(SysOrderDTO order) {
        Map<String, List<SysOrderDetailDTO>> map = order.getSysOrderDetailList().stream().collect(Collectors.groupingBy(sysOrderDetailDTO -> sysOrderDetailDTO.getSku()));
        BigDecimal totalWeight = BigDecimal.ZERO;//总重量
        BigDecimal totalBulk = BigDecimal.ZERO;//总体积
        if (MapUtils.isNotEmpty(map)) {
            List<CommodityBase> commodityBaseList = systemOrderCommonService.getCommodityBySkuList(order.getSellerPlId(), order.getSkus());
            for (CommodityBase commodityBase : commodityBaseList) {
                for (CommoditySpec commoditySpec : commodityBase.getCommoditySpecList()) {
                    if (commoditySpec.getPackingHeight() != null && commoditySpec.getPackingLength() != null
                            && commoditySpec.getPackingWidth() != null) {
                        totalBulk = totalBulk.add(commoditySpec.getPackingHeight().multiply(commoditySpec.getPackingLength()).multiply(commoditySpec.getPackingWidth().multiply(new BigDecimal(map.get(commoditySpec.getSystemSku()).get(0).getSkuQuantity()))));
                    }
                    totalWeight = totalWeight.add(commoditySpec.getPackingWeight() == null ? BigDecimal.valueOf(0) : commoditySpec.getPackingWeight().multiply(new BigDecimal(map.get(commoditySpec.getSystemSku()).get(0).getSkuQuantity())));
                }
            }
        } else {
            LOGGER.error("转单日志：转入的订单SKU为空，默认设置订单总体积和总重量为0，订单json串：{}", FastJsonUtils.toJsonString(order));
        }
        order.setTotalBulk(totalBulk);
        order.setTotalWeight(totalWeight);
    }

    /**
     * 过滤规则
     *
     * @param platform              来源平台
     * @param orderDTO              订单对象
     * @param orderRuleList         订单规则集合
     * @param warehouseMap          仓库规则MAP
     * @param logisticsMap          物流规则MAP
     * @param isFilterWarehouseRule 是否过滤仓库规则  true/false
     * @return
     */
    private OrderRuleWithBLOBs filterRule(String platform, SysOrderDTO orderDTO, List<OrderRuleWithBLOBs> orderRuleList, HashMap<Object, Object> warehouseMap,
                                          Map<String, List<LogisticsDetailVo>> logisticsMap, boolean isFilterWarehouseRule) {
        if (isFilterWarehouseRule) {
            orderRuleList = orderRuleList.stream().filter(rule -> warehouseMap.containsKey(rule.getDeliveryWarehouseId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(orderRuleList)) {
                return null;
            }
        } else {
            //进行卖家物流方式规则匹配,筛选出符合仓库ID的物流规则
            orderRuleList = orderRuleList.stream().filter(rule -> rule.getDeliveryWarehouseIdList().contains(String.valueOf(orderDTO.getSysOrderPackageList().get(0).getDeliveryWarehouseId()))).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(orderRuleList)) {
                return null;
            }
        }
        Collections.sort(orderRuleList, new Comparator<OrderRuleWithBLOBs>() {
            @Override
            public int compare(OrderRuleWithBLOBs o1, OrderRuleWithBLOBs o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
        SysOrderReceiveAddressDTO sysOrderReceiveAddress = orderDTO.getSysOrderReceiveAddress();
        String shipToCountry = sysOrderReceiveAddress.getShipToCountry();//国家代码
        String shipToPostalCode = sysOrderReceiveAddress.getShipToPostalCode();//邮编
        BigDecimal TotalPrice = orderDTO.getCommoditiesAmount(); //平台总售价

        List<String> skus = orderDTO.getSkus();  //转入成功的SKU集合
        for (OrderRuleWithBLOBs rule : orderRuleList) {
            if (!isFilterWarehouseRule) {
                if (!logisticsMap.containsKey(rule.getMailTypeCode())) {
                    continue;
                }
            }
            orderRuleServiceImpl.jsonStringToObject(rule);
            if (checkSourceAndShop(platform, orderDTO, rule))
                if (checkCountryCode(shipToCountry, rule))
                    if (checkShipToPostalCode(shipToPostalCode, rule))
                        if (checkPlSku(rule, skus))
                            if (MappingOrderRuleUtil.checkBigDecimal(rule.getPriceMin(), rule.getPriceMax(), TotalPrice))
                                if (MappingOrderRuleUtil.checkBigDecimal(rule.getWeightMin(), rule.getWeightMax(), orderDTO.getTotalWeight()))
                                    if (MappingOrderRuleUtil.checkBigDecimal(rule.getVolumeMin(), rule.getVolumeMax(), orderDTO.getTotalBulk())) {
                                        printRuleLog(orderDTO, isFilterWarehouseRule, rule);
                                        return rule;
                                    }
        }
        return null;
    }

    private void printRuleLog(SysOrderDTO orderDTO, boolean isFilterWarehouseRule, OrderRuleWithBLOBs rule) {
        if (isFilterWarehouseRule) {
            LOGGER.info("平台订单ID：{}，匹配到仓库规则：{}", orderDTO.getSourceOrderId(), FastJsonUtils.toJsonString(rule));
        } else {
            LOGGER.info("平台订单ID：{}，匹配到物流规则：{}", orderDTO.getSourceOrderId(), FastJsonUtils.toJsonString(rule));
        }
    }

    private boolean checkShipToPostalCode(String shipToPostalCode, OrderRuleWithBLOBs rule) {
        List<String> receiveGoodsZipCodes = rule.getReceiveGoodsZipCodes();
        if (CollectionUtils.isNotEmpty(receiveGoodsZipCodes)) {
            if (!receiveGoodsZipCodes.contains(shipToPostalCode)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPlSku(OrderRuleWithBLOBs rule, List<String> skus) {
        List<String> plSkus = rule.getPlSkus();
        if (CollectionUtils.isNotEmpty(plSkus)) {
            for (String sku : skus) {
                if (!plSkus.contains(sku)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkCountryCode(String shipToCountry, OrderRuleWithBLOBs rule) {
        List<String> receiveGoodsCountrys = rule.getReceiveGoodsCountrys();
        if (CollectionUtils.isNotEmpty(receiveGoodsCountrys)) {
            if (!receiveGoodsCountrys.contains(shipToCountry)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkSourceAndShop(String platform, SysOrderDTO orderDTO, OrderRuleWithBLOBs rule) {
        String sellerAccountList = rule.getSellerAccountList();
        if (StringUtils.isNotBlank(sellerAccountList)) {
            List<MappingShopDTO> list = JSONObject.parseArray(sellerAccountList, MappingShopDTO.class);
            boolean flag = false;
            for (MappingShopDTO dto : list) {
                List<String> accounts = dto.getAccounts();
                if (CollectionUtils.isNotEmpty(accounts)) {
                    flag = true;
                }
            }
            if (!flag) {
                return true;
            }
            for (MappingShopDTO dto : list) {
                if (dto.getPlatform().equalsIgnoreCase(platform)) {
                    if (dto.getAccounts().toString().contains(String.valueOf(orderDTO.getPlatformShopId()))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private void setPackageInfo(SysOrderDTO order, List<LogisticsDetailVo> logisticsDetailVos, GetResultPublishListingVO getResultPublishListingVO, Integer logisticsType, WarehouseDTO warehouseDTO) {
        LogisticsDetailVo logisticsDetailVo = logisticsDetailVos.get(0);
        order.getSysOrderPackageList().forEach(orderPackageDTO -> {
            if (logisticsDetailVo != null) {
                orderPackageDTO.setDeliveryWarehouseId(getResultPublishListingVO.getWarehouseId());
                orderPackageDTO.setDeliveryMethod(logisticsDetailVo.getLogisticsName());
                orderPackageDTO.setShippingCarrierUsed(logisticsDetailVo.getCarrierName());
                orderPackageDTO.setShippingCarrierUsedCode(logisticsDetailVo.getCarrierCode());
                orderPackageDTO.setAmazonCarrierName(logisticsDetailVo.getAmazonCarrier());
                orderPackageDTO.setAmazonShippingMethod(logisticsDetailVo.getAmazonCode());
                orderPackageDTO.setEbayCarrierName(logisticsDetailVo.getEbayCarrier());
                orderPackageDTO.setDeliveryMethodCode(logisticsDetailVo.getLogisticsCode());
                orderPackageDTO.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.getLogisticsStrategyByLogisticsType(logisticsType));
            }
            if (warehouseDTO != null) {
                orderPackageDTO.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());
                orderPackageDTO.setDeliveryWarehouse(warehouseDTO.getWarehouseName());
            }
            //添加匹配上刊登物流规则成功日志
            orderPackageDTO.setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_PUBLISH_RULE_SUCCESS.mappingPublishRuleSuccess());
        });
    }

    private WarehouseDTO getWarehouseDTO(Integer warehouseId) {
        String warehouseStr = remoteSupplierService.getWarehouseById(warehouseId);
        String data = Utils.returnRemoteResultDataString(warehouseStr, "调用供应商服务发生异常");
        return JSON.parseObject(data, WarehouseDTO.class);
    }

    private List<LogisticsDetailVo> getLogisticsDetailVos(String platform, SysOrderDTO order, List<SearchLogisticsListSku> plSkuList, Integer logisticsType, Integer warehouseId) {
        SearchLogisticsListDTO searchLogisticsListDTO = new SearchLogisticsListDTO();
        searchLogisticsListDTO.setCountryCode(order.getSysOrderReceiveAddress().getShipToCountry());
        searchLogisticsListDTO.setPlatformType(getPlatformType(platform));
        searchLogisticsListDTO.setSearchLogisticsListSkuList(plSkuList);
        searchLogisticsListDTO.setPostCode(order.getSysOrderReceiveAddress().getShipToPostalCode());
        searchLogisticsListDTO.setWarehouseId(String.valueOf(warehouseId));
        searchLogisticsListDTO.setSearchType(logisticsType);
        searchLogisticsListDTO.setHandOrder(order.getHandOrder());
        searchLogisticsListDTO.setStoreId(order.getPlatformShopId());
        String suitLogisticsStr = remoteSupplierService.getSuitLogisticsByType(searchLogisticsListDTO);
        LOGGER.error("请求供应商服务请求参数：{}，返回结果：{}", FastJsonUtils.toJsonString(searchLogisticsListDTO), suitLogisticsStr);
        String data = Utils.returnRemoteResultDataString(suitLogisticsStr, "调用供应商服务发生异常");
        return JSON.parseArray(data, LogisticsDetailVo.class);
    }

    /**
     * 管理后台物流规则匹配
     *
     * @param platform
     * @param order
     * @param orderRule
     * @param logisticsMap
     */
    private void mappingCMSLogisticsRule(String platform, SysOrderDTO order, OrderRule orderRule, Map<String, List<LogisticsDetailVo>> logisticsMap) {
        List<OrderRuleWithBLOBs> orderRuleMailList;
        orderRule.setSellerId(null);
        orderRule.setSellerAccount(null);
        orderRule.setPlatformMark("G");
        orderRuleMailList = orderRuleMailMapper.findAll(orderRule);
        if (CollectionUtils.isNotEmpty(orderRuleMailList)) {
            OrderRuleWithBLOBs logisticsRule = filterRule(platform, order, orderRuleMailList, null, logisticsMap, false);
            if (logisticsRule != null) {
                getDeliveryLogisticsInfo(order, logisticsMap, logisticsRule);
            }
        }
    }

    /**
     * 筛选仓库ID后的卖家物流规则不为空则遍历物流规则筛选可用的设置物流信息
     *
     * @param order
     * @param logisticsMap
     * @param logisticsRule
     */
    private void getDeliveryLogisticsInfo(SysOrderDTO order, Map<String, List<LogisticsDetailVo>> logisticsMap, OrderRuleWithBLOBs logisticsRule) {
        //TODO 设置匹配物流规则成功的日志
        String mappingOrderRuleLog = order.getSysOrderPackageList().get(0).getMappingOrderRuleLog();//前面匹配仓库规则的日志
        order.getSysOrderPackageList().get(0).setMappingOrderRuleLog(mappingOrderRuleLog+"#"+OrderHandleLogEnum.Content.MAPPING_LOGISTICS_RULE_SUCCESS.mappingLogisticsRuleSuccess(logisticsRule.getRuleName()));
        for (LogisticsDetailVo logisticsVo : logisticsMap.get(logisticsRule.getMailTypeCode())) {
            order.getSysOrderPackageList().stream().forEach(p -> {
                p.setDeliveryMethod(logisticsVo.getLogisticsName());
                p.setShippingCarrierUsed(logisticsVo.getCarrierName());
                p.setShippingCarrierUsedCode(logisticsVo.getCarrierCode());
                p.setAmazonCarrierName(logisticsVo.getAmazonCarrier());
                p.setAmazonShippingMethod(logisticsVo.getAmazonCode());
                p.setEbayCarrierName(logisticsVo.getEbayCarrier());
                p.setDeliveryMethodCode(logisticsVo.getLogisticsCode());
            });
            break;
        }
//        List<LogisticsDetailVo> logisticsDetail = logisticsDetailVos.stream().filter(detail -> detail.getLogisticsCode().equalsIgnoreCase(orderRuleMailList.getMailTypeCode())).collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(logisticsDetail)) {
//            for (LogisticsDetailVo logisticsDetailVo : logisticsDetail) {
//                order.getSysOrderPackageList().stream().forEach(p -> {
//                    p.setDeliveryMethod(logisticsDetailVo.getLogisticsName());
//                    p.setShippingCarrierUsed(logisticsDetailVo.getCarrierName());
//                    p.setShippingCarrierUsedCode(logisticsDetailVo.getCarrierCode());
//                    p.setAmazonCarrierName(logisticsDetailVo.getAmazonCarrier());
//                    p.setAmazonShippingMethod(logisticsDetailVo.getAmazonCode());
//                    p.setEbayCarrierName(logisticsDetailVo.getEbayCarrier());
//                    p.setDeliveryMethodCode(logisticsDetailVo.getLogisticsCode());
//                });
//                break;
//            }
//        }
    }

    private List<LogisticsDetailVo> getLogisticsDetailVos(String platform, SysOrderDTO order, List<SearchLogisticsListSku> plSkuList, Integer warehouseId) {
        SearchLogisticsListDTO searchLogisticsListDTO;
        List<LogisticsDetailVo> logisticsDetailVos;
        searchLogisticsListDTO = new SearchLogisticsListDTO();
        searchLogisticsListDTO.setCountryCode(order.getSysOrderReceiveAddress().getShipToCountry());
        searchLogisticsListDTO.setPlatformType(getPlatformType(platform));
        searchLogisticsListDTO.setSearchLogisticsListSkuList(plSkuList);
        searchLogisticsListDTO.setPostCode(order.getSysOrderReceiveAddress().getShipToPostalCode());
        searchLogisticsListDTO.setWarehouseId(String.valueOf(warehouseId));
        //搜索条件 1 价格最低  2 综合排序   3 物流速度最快
        searchLogisticsListDTO.setSearchType(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsType());
        searchLogisticsListDTO.setStoreId(order.getPlatformShopId());
        searchLogisticsListDTO.setHandOrder(order.getHandOrder());
        LOGGER.error("请求小寂寞参数：{}", FastJsonUtils.toJsonString(searchLogisticsListDTO));
        String suitLogisticsStr = remoteSupplierService.getSuitLogisticsByType(searchLogisticsListDTO);
        LOGGER.error("小寂寞返回参数：{}", suitLogisticsStr);
        String data = Utils.returnRemoteResultDataString(suitLogisticsStr, "请求供应商服务失败");
        logisticsDetailVos = JSON.parseArray(data, LogisticsDetailVo.class);
        return logisticsDetailVos;
    }

    private Integer getOrderRuleByCMS(String plstform, SysOrderDTO orderDTO, OrderRule orderRule, HashMap<Object, Object> warehouseMap) {
        List<OrderRuleWithBLOBs> orderRuleWarehouseList;
        orderRule.setSellerId(null);
        orderRule.setSellerAccount(null);
        orderRule.setPlatformMark("G");
        orderRuleWarehouseList = orderRuleWarehouseMapper.findAll(orderRule);
        if (CollectionUtils.isEmpty(orderRuleWarehouseList)) {
            return null;
        } else {
            OrderRuleWithBLOBs warehouseRule = filterRule(plstform, orderDTO, orderRuleWarehouseList, warehouseMap, null, true);
            Integer warehouseId = getWarehouseId(warehouseRule);
            if (warehouseId != null) {
                //TODO 设置匹配仓库规则的日志
                orderDTO.getSysOrderPackageList().get(0).setMappingOrderRuleLog(OrderHandleLogEnum.Content.MAPPING_WAREHOUSE_RULE_SUCCESS.mappingWarehouseRuleSuccess(warehouseRule.getRuleName()));
            }
            return warehouseId;
        }
    }

    private Integer getWarehouseId(OrderRuleWithBLOBs warehouseRule) {
        if (warehouseRule == null) {
            return null;
        }
        return warehouseRule.getDeliveryWarehouseId();
    }

    private boolean getUserfulWarehouseBySkus(List<SearchLogisticsListSku> plSkuList, HashMap<Object, Object> map) {
        List<String> skus = new ArrayList<>();
        for (SearchLogisticsListSku sku : plSkuList) {
            skus.add(sku.getSku());
        }
        List<OrderInvDTO> warehouseList = systemOrderCommonService.getMappingWarehouseBySkuList(skus);
        if (CollectionUtils.isEmpty(warehouseList)) {
            return true;
        } else {
            warehouseList.forEach(orderInvDTO -> {
                map.put(orderInvDTO.getWarehouseId(), orderInvDTO);
            });
        }
        return false;
    }

    private List<SearchLogisticsListSku> getSearchLogisticsListSkuList(List<String> skuList) {
        List<SearchLogisticsListSku> list = new ArrayList<>();
        if (skuList == null && skuList.isEmpty()) {
            return list;
        }
        for (String s : skuList) {
            SearchLogisticsListSku sku = new SearchLogisticsListSku();
            sku.setSku(s);
            list.add(sku);
        }

        return list;
    }

    /**
     * 设置订单列表的订单映射结果
     *
     * @param platform 平台
     * @param orders   订单列表
     * @param plSKUSet 用来返回
     */
    private void orderMap(String platform, List<SysOrder> orders, HashSet<String> plSKUSet) {
        boolean successFlag;
        boolean failFlag;
        List<String> skus;
        Empower empower;
        String oneEmpowByAccount;
        String dataString;
        String plSku;
        for (SysOrder order : orders) {
            empower = null;
            successFlag = false;
            failFlag = false;
            skus = new ArrayList<>();
            try {
                if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())) {
                    oneEmpowByAccount = remoteSellerService.findOneEmpowByAccount(this.getPlatform(platform),
                            order.getPlatformSellerId(), order.getSite(), null);
                    dataString = Utils.returnRemoteResultDataString(oneEmpowByAccount, "转单日志 卖家服务异常");
                    empower = JSONObject.parseObject(dataString, Empower.class);
                } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())) {
                    oneEmpowByAccount = remoteSellerService.findOneEmpowByAccount(this.getPlatform(platform),
                            order.getPlatformSellerAccount(), order.getSite(), null);
                    dataString = Utils.returnRemoteResultDataString(oneEmpowByAccount, "转单日志 卖家服务异常");
                    empower = JSONObject.parseObject(dataString, Empower.class);
                } else if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform())) {
                    if (order.getEmpowerId() != null && order.getSellerPlId() != null && StringUtils.isNotBlank(order.getSellerPlAccount())) {
                        empower = new Empower() {{
                            setEmpowerid(order.getEmpowerId());
                            setPinlianid(order.getSellerPlId());
                            setPinlianaccount(order.getSellerPlAccount());
                            setRentstatus(Integer.valueOf(order.getShopType()));
                        }};
                    } else {
                        LOGGER.error("速卖通转单授权信息传入不全");
                    }
                }
                if (empower == null) {
                    order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                    order.getSysOrderDetails().forEach(x -> x.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue()));
                    continue;
                }
                Integer rentstatus = empower.getRentstatus();
                order.setShopType(rentstatus == 0 ? "PERSONAL" : "RENT");
            } catch (Exception e) {
                LOGGER.error(platform + " 转单日志 查询卖家服务授权信息异常,平台订单id ：" + order.getSourceOrderId(), e);
                order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                order.getSysOrderDetails().forEach(x -> x.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue()));
                continue;
            }
            for (SysOrderDetail detail : order.getSysOrderDetails()) {
                try {
                    plSku = this.queryPlSku(platform, empower.getEmpowerid().toString(), detail.getPlatformSKU(), String.valueOf(empower.getPinlianid()));//TODO
                    if (StringUtils.isNotBlank(plSku)) {
                        plSKUSet.add(plSku);
                        detail.setSku(plSku);
                        successFlag = true;
                        detail.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue());
                        skus.add(plSku);
                        order.setSellerPlAccount(empower.getPinlianaccount());
                        order.setSellerPlId(empower.getPinlianid());
                        order.setEmpowerId(empower.getEmpowerid());
                    } else {
                        failFlag = true;
                        detail.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                        LOGGER.info(platform + " 转单日志 没有匹配到sku,平台订单id ：" + order.getSourceOrderId() + "平台sku：" + detail.getPlatformSKU());
                    }
                } catch (Exception e) {
                    LOGGER.error(platform + " 转单日志 设置sku异常,平台订单id ：" + order.getSourceOrderId() + "平台sku：" + detail.getPlatformSKU(), e);
                    failFlag = true;
                    detail.setConverSysDetailStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
                }
            }
            if (successFlag && failFlag) {
                order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_PORTION_SUCCESS.getValue());
            } else if (successFlag) {
                order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_SUCCESS.getValue());
            } else if (failFlag) {
                order.setConverSysStatus(OrderHandleEnum.ConverSysStatus.CONVER_FAILURE.getValue());
            }
            order.setSkus(skus);
            String supplyChainStr = remoteUserService.getSupplyChainByUserId("1", new ArrayList<Integer>() {{
                this.add(order.getSellerPlId());
            }});
            String resultString = Utils.returnRemoteResultDataString(supplyChainStr, "用户服务异常");
            if (StringUtils.isBlank(resultString)) {
                continue;
            }
            JSONArray jsonArray = JSONObject.parseArray(resultString);
            if (CollectionUtils.isEmpty(jsonArray)) {
                continue;
            }
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            if (StringUtils.isBlank(jsonObject.getString("supplyId")) || StringUtils.isEmpty(jsonObject.getString("supplyChainCompanyName"))) {
                continue;
            }

            order.setSupplyChainCompanyId(Integer.valueOf(jsonObject.getString("supplyId")));
            order.setSupplyChainCompanyName(jsonObject.getString("supplyChainCompanyName"));


        }
    }

    private String getPlatformType(String platform) {
        //所属平台 1(eBay) 2(Amazon) 3(Wish) 4(AliExpress)
        if (platform.equalsIgnoreCase("eBay")) {
            return String.valueOf(1);
        } else if (platform.equalsIgnoreCase("Amazon")) {
            return String.valueOf(2);
        } else if (platform.equalsIgnoreCase("Wish")) {
            return String.valueOf(3);
        } else if (platform.equalsIgnoreCase("AliExpress")) {
            return String.valueOf(4);
        }
        return String.valueOf("");
    }

    /**
     * 获取刊登SKU的物流信息,全部SKU物流信息必须不为空、信息全部一致，否则都返回Null
     *
     * @param empowerId
     * @param platform
     * @param platformSkuList
     * @return
     */
    private List<GetResultPublishListingVO> findLogisticsAndWarehousingFormPlaceService(Integer empowerId, String platform, List<String> platformSkuList) {
        GetByplatformSkuAndSiteDTO dto = new GetByplatformSkuAndSiteDTO();
        dto.setEmpowerId(empowerId);
        dto.setType(platform.toUpperCase());
        dto.setPlatformSku(platformSkuList);
        LOGGER.error("匹配刊登接口的参数:{}", JSON.toJSONString(dto));
        String byplatformSkuAndSite = remoteSellerService.getByplatformSkuAndSite(dto);
        LOGGER.error("匹配刊登接口返回的数据:{}", JSON.toJSONString(byplatformSkuAndSite));
        Map map = JSON.parseObject(byplatformSkuAndSite, Map.class);
        if (map.containsKey("data")) {
            String data = String.valueOf(map.get("data"));
            List<GetResultPublishListingVO> getResultPublishListingVOS = JSON.parseArray(data, GetResultPublishListingVO.class);
            if (CollectionUtils.isEmpty(getResultPublishListingVOS)) {
                return null;
            } else {
                for (GetResultPublishListingVO vo : getResultPublishListingVOS) {
                    if (vo.getWarehouseId() == null || vo.getLogisticsType() == null) {
                        return null;
                    }
                }
                HashSet hashSet = new HashSet();
                getResultPublishListingVOS.forEach(vo -> hashSet.add(FastJsonUtils.toJsonString(vo)));
                if (hashSet.size() == 1) {
                    return getResultPublishListingVOS;
                }
            }
        }
        return null;
    }

    private StringBuilder getOrderIdsNew(List<SysOrderDTO> orders) {
        StringBuilder sb = new StringBuilder();
        if (orders != null && orders.size() > 0) {
            orders.forEach(o -> sb.append(o.getSourceOrderId()).append(","));
        }
        return sb;
    }

    private StringBuilder getOrderIds(List<SysOrder> orders) {
        StringBuilder sb = new StringBuilder();
        if (orders != null && orders.size() > 0) {
            orders.forEach(o -> sb.append(o.getSourceOrderId()).append(","));
        }
        return sb;
    }

    private StringBuilder getOrderSku(Set<String> skus) {
        StringBuilder sb = new StringBuilder();
        if (skus != null && skus.size() > 0) {
            skus.forEach(sku -> sb.append(sku).append(","));
        }
        return sb;
    }

    /**
     * 设置订单列表的基础参数
     * 体积，金额之类的，主要是用来匹配仓库物流方式
     *
     * @param order             订单
     * @param commodityBaseList 商品列表
     */
    private void setOrderBaseDataNew(SysOrderDTO order, List<CommodityBase> commodityBaseList) {
        //订单总售价:预估物流费+商品金额(未加预估物流费)
        order.setTotal(BigDecimal.valueOf(0));
        //系统订单总价
        order.setOrderAmount(BigDecimal.valueOf(0));
        //产品成本
        order.setProductCost(BigDecimal.valueOf(0));

        // 转单只有一个包裹
        SysOrderPackageDTO sysOrderPackageDTO = new SysOrderPackageDTO();
        SysOrderPackageDTO dto = order.getSysOrderPackageList().get(0);
        if (StringUtils.isBlank(dto.getLogisticsStrategy())) {
            dto.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsStrategy());
        }
        BeanUtils.copyProperties(dto, sysOrderPackageDTO);
        sysOrderPackageDTO.setItemNum(0L);
        sysOrderPackageDTO.setTotalBulk(BigDecimal.ZERO);
        sysOrderPackageDTO.setTotalWeight(BigDecimal.ZERO);
        sysOrderPackageDTO.setSkus(order.getSkus());
        sysOrderPackageDTO.setEmpowerId(order.getEmpowerId());
        sysOrderPackageDTO.setSellerPlAccount(order.getSellerPlAccount());
        // 收货信息
        SysOrderReceiveAddressDTO sysOrderReceiveAddressDTO = order.getSysOrderReceiveAddress();
        if (null != sysOrderReceiveAddressDTO) {
            sysOrderPackageDTO.setShipToCountry(sysOrderReceiveAddressDTO.getShipToCountry());
            sysOrderPackageDTO.setShipToCountryName(sysOrderReceiveAddressDTO.getShipToCountryName());
            sysOrderPackageDTO.setShipToPostalCode(sysOrderReceiveAddressDTO.getShipToPostalCode());
        }
        for (SysOrderDetailDTO detail : order.getSysOrderDetailList()) {
            try {
                this.setDetailNew(detail, commodityBaseList, order.getSysOrderPackageList().get(0));  //TODO sku存在判断

                if (StringUtils.isNotBlank(detail.getSku())) {
                    sysOrderPackageDTO.setItemNum(sysOrderPackageDTO.getItemNum() + detail.getSkuQuantity());
                    order.setOrderAmount(order.getOrderAmount().add(this.totalBigDecimal(detail.getSkuQuantity(), detail.getItemPrice())));
                    sysOrderPackageDTO.setTotalBulk(sysOrderPackageDTO.getTotalBulk().add(detail.getBulk().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));
                    //todo 重量这块还有单位处理
                    sysOrderPackageDTO.setTotalWeight(sysOrderPackageDTO.getTotalWeight().add(detail.getWeight().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));
                }
            } catch (Exception e) {
                LOGGER.error("转单日志 设置订单项商品参数异常,平台id： " + order.getSourceOrderId(), e);
            }
        }
        sysOrderPackageDTO.setTotal(order.getOrderAmount());
        List<SysOrderPackageDTO> sysOrderPackageDTOList = new ArrayList<>();
        sysOrderPackageDTOList.add(sysOrderPackageDTO);
        order.setSysOrderPackageList(sysOrderPackageDTOList);
        order.setTotal(order.getOrderAmount());
    }

    /**
     * 设置订单项参数
     *
     * @param detail            订单项对象
     * @param commodityBaseList 商品列表
     */
    private void setDetailNew(SysOrderDetailDTO detail, List<CommodityBase> commodityBaseList, SysOrderPackageDTO packageDTO) {
        if (StringUtils.isNotBlank(detail.getSku())) {
            detail.setSourceSku(detail.getPlatformSKU()); //设置来源SKU
            commodityBaseList.forEach(commodityBase -> {
                if (commodityBase.getCommoditySpecList() != null && commodityBase.getCommoditySpecList().size() > 0) {
                    Integer canSale = commodityBase.getCanSale();
                    if (canSale.equals(Constants.CommodityBase.CANT_SALE)) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "含有不可售商品");
                    }
                    //commodityBase.

                    commodityBase.getCommoditySpecList().forEach(commoditySpec -> {
                        if (commoditySpec.getSystemSku().equalsIgnoreCase(detail.getSku())) {
                            //
                            detail.setItemId(commoditySpec.getId());
                            //TODO 修改为分仓定价
                            detail.setItemPrice(commoditySpec.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityPriceUs());
                            List<SkuInventoryVo> inventoryList = commoditySpec.getInventoryList();
                            if (packageDTO != null && packageDTO.getDeliveryWarehouseId() != null) {
                                inventoryList = inventoryList.stream().filter(inventory -> String.valueOf(inventory.getWarehouseId()).equals(String.valueOf(packageDTO.getDeliveryWarehouseId()))).collect(Collectors.toList());
                                if (!inventoryList.isEmpty()) {
                                    BigDecimal price = new BigDecimal(inventoryList.get(0).getWarehousePrice());
                                    detail.setItemPrice(price);
                                }

                            }


                            if (commoditySpec.getPackingHeight() != null && commoditySpec.getPackingLength() != null
                                    && commoditySpec.getPackingWidth() != null) {
                                detail.setBulk(commoditySpec.getPackingHeight().multiply(commoditySpec.getPackingLength()).multiply(commoditySpec.getPackingWidth()));
                            } else {
                                detail.setBulk(BigDecimal.valueOf(0));
                            }
                            detail.setWeight(commoditySpec.getCommodityWeight() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityWeight());
                            detail.setItemCost(commoditySpec.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityPriceUs());
                            detail.setItemAttr(commoditySpec.getCommoditySpec());
                            //若主图有多个URL，只取第一个
                            detail.setItemUrl(StringUtils.split(commoditySpec.getMasterPicture(), "|")[0]);
                            detail.setItemName(commoditySpec.getCommodityNameCn());
                            detail.setItemNameEn(commoditySpec.getCommodityNameEn());
                            detail.setSkuTitle(commoditySpec.getCommodityNameCn());
                            detail.setSupplierSkuTitle(commoditySpec.getCommodityNameCn());
                            detail.setSupplierId(commodityBase.getSupplierId().intValue());
                            detail.setSupplierName(commoditySpec.getSupplierName());
                            detail.setSupplyChainCompanyId(commoditySpec.getSupChainCompanyId());
                            detail.setSupplyChainCompanyName(commoditySpec.getSupChainCompanyName());
                            detail.setFareTypeAmount(commoditySpec.getFeePriceUs() == null ? (commoditySpec.getFeeRate() == null ? null : "2#" + commoditySpec.getFeeRate()) : "1#" + commoditySpec.getFeePriceUs());
                            detail.setSupplierSku(commoditySpec.getSupplierSku());
                            detail.setSupplierSkuPrice(commoditySpec.getCommodityPriceUs() == null ? null : commoditySpec.getCommodityPriceUs());
                            detail.setFreeFreight(commoditySpec.getFreeFreight());
                        }
                    });
                }
            });
        }
    }

    /**
     * 设置订单列表的基础参数
     *
     * @param order             订单
     * @param commodityBaseList 商品列表
     */
    private void setOrderBaseData(SysOrder order, List<CommodityBase> commodityBaseList) {
        order.setItemNum(0L);
        order.setTotal(BigDecimal.valueOf(0));//订单总售价:预估物流费+商品金额(未加预估物流费)
        order.setOrderAmount(BigDecimal.valueOf(0));//系统订单总价
        order.setTotalBulk(BigDecimal.valueOf(0));
        order.setTotalWeight(BigDecimal.valueOf(0));
        order.setEstimateShipCost(BigDecimal.valueOf(0));//预估物流费
        order.setProductCost(BigDecimal.valueOf(0));//产品成本
        for (SysOrderDetail detail : order.getSysOrderDetails()) {
            try {
                //TODO sku存在判断
                this.setDetail(detail, commodityBaseList);

                if (StringUtils.isNotBlank(detail.getSku())) {
                    order.setItemNum(order.getItemNum() + detail.getSkuQuantity());
                    order.setOrderAmount(order.getOrderAmount().add(this.totalBigDecimal(detail.getSkuQuantity(), detail.getItemPrice())));
                    order.setTotalBulk(order.getTotalBulk().add(detail.getBulk().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));
                    order.setTotalWeight(order.getTotalWeight().add(detail.getWeight().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));   //todo 重量这块还有单位处理
                }
            } catch (Exception e) {
                LOGGER.error("转单日志 设置订单项商品参数异常,平台id： " + order.getSourceOrderId(), e);
            }
        }
        order.setTotal(order.getOrderAmount());
    }

    @Override
    public String queryPlSku(String platform, String authorizationId, String platformSku, String sellerId) {
        if (StringUtils.isBlank(platform) || StringUtils.isBlank(authorizationId) || StringUtils.isBlank(platformSku)) {
            LOGGER.error("转单日志 sku匹配传入参数不全 platform:　" + platform + "　authorizationId：" + authorizationId + "　platformSku：" + platformSku);
            return null;
        }
        SellerSkuMap m = this.getSellerSkuMapByPlatformSku(platform, authorizationId, platformSku, sellerId);//TODO 查询本地映射表中映射数据
        if (m == null || StringUtils.isBlank(m.getPlSku())) {//TODO 用品连SKU作为接口参数去查数据，解决卖家直接使用品连SKU去第三方平台登录的场景
            m = new SellerSkuMap();
            try {
                SkuMapRule skuMapRule = skuMapRuleService.selectBySellerId(new SkuMapRule() {{
                    setSellerId(sellerId);
                }});
                if (skuMapRule == null || skuMapRule.getStatus() == 1) {
                    return null;
                }
                String skuModel = this.createSkuModel(skuMapRule, platformSku);
                if (StringUtils.isBlank(skuModel)) {
                    return null;
                }
                String result = remoteCommodityService.getCommoditySpecBySku(skuModel);

                String data = Utils.returnRemoteResultDataString(result, "商品服务异常");
                if (StringUtils.isBlank(data)) {
                    return null;
                }
                String sku = JSONObject.parseObject(data).getString("systemSku");
                if (StringUtils.isNotBlank(sku)) {
                    m.setPlSku(sku);
                }
            } catch (Exception e) {
                LOGGER.error(platform + "平台 转单日志 调用商品服务远程匹配sku异常 对应平台sku" + platformSku, e);
                m.setPlSku(null);
            }
        }
        return m.getPlSku();
    }

    @Override
    public Integer getPlatform(String platform) {
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform()))
            return 2;
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform()))
            return 1;
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.ALIEXPRESS.getPlatform()))
            return 3;
        return null;
    }

    /**
     * 设置订单项参数
     *
     * @param detail            订单项对象
     * @param commodityBaseList 商品列表
     */
    private void setDetail(SysOrderDetail detail, List<CommodityBase> commodityBaseList) {
        if (StringUtils.isNotBlank(detail.getSku())) {
            commodityBaseList.forEach(c -> {
                if (c.getCommoditySpecList() != null && c.getCommoditySpecList().size() > 0) {
                    c.getCommoditySpecList().forEach(s -> {
                        if (s.getSystemSku().equalsIgnoreCase(detail.getSku())) {
                            detail.setItemId(s.getId());
                            detail.setItemPrice(s.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : s.getCommodityPriceUs());
                            if (s.getPackingHeight() != null && s.getPackingLength() != null
                                    && s.getPackingWidth() != null) {
                                detail.setBulk(s.getPackingHeight().multiply(s.getPackingLength()).multiply(s.getPackingWidth()));
                            } else {
                                detail.setBulk(BigDecimal.valueOf(0));
                            }
                            detail.setWeight(s.getCommodityWeight() == null ? BigDecimal.valueOf(0) : s.getCommodityWeight());
                            detail.setItemCost(s.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : s.getCommodityPriceUs());
                            detail.setItemAttr(s.getCommoditySpec());
                            detail.setItemUrl(s.getMasterPicture().split("\\|")[0]);//若主图有多个URL，只取第一个
                            detail.setItemName(s.getCommodityNameCn());
                            detail.setItemNameEn(s.getCommodityNameEn());
                            detail.setSkuTitle(s.getCommodityNameCn());
                            detail.setSupplierSkuTitle(s.getCommodityNameCn());
                            detail.setSupplierId(c.getSupplierId());
                            detail.setSupplierName(s.getSupplierName());
                            detail.setSupplyChainCompanyId(s.getSupChainCompanyId());
                            detail.setSupplyChainCompanyName(s.getSupChainCompanyName());
                            detail.setFareTypeAmount(s.getFeePriceUs() == null ? (s.getFeeRate() == null ? null : "2#" + s.getFeeRate()) : "1#" + s.getFeePriceUs());
                            detail.setSupplierSku(s.getSupplierSku());
                            detail.setSupplierSkuPrice(s.getCommodityPriceUs() == null ? null : s.getCommodityPriceUs());
                        }
                    });
                }
            });
        }
    }

    /**
     * 将金额转换为人民币并计算总金额
     *
     * @param skuNum    商品数量
     * @param itemPrice 商品单价：0.0000#CNY#1.00　　金额＃币种＃汇率
     * @return 总金额
     */
    private BigDecimal totalBigDecimal(Integer skuNum, BigDecimal itemPrice) {
        if (itemPrice == null || skuNum == null || skuNum == 0)
            return BigDecimal.valueOf(0);
        return itemPrice.multiply(BigDecimal.valueOf(skuNum));
    }

    @Override
    public SellerSkuMap getSellerSkuMapByPlatformSku(String platform, String authorizationId, String
            platformSku, String sellerId) {
        SellerSkuMap m = new SellerSkuMap();
        m.setPlatform(platform);
        m.setAuthorizationId(authorizationId);
        m.setPlatformSku(platformSku);
        m.setStatus(1);
        return this.selectByEntry(m);
    }

    /**
     * 根据规则生成一个可能的sku
     *
     * @param skuMapRule  规则
     * @param platformSku 平台sku
     * @return 品连sku
     */
    private String createSkuModel(SkuMapRule skuMapRule, String platformSku) {
        try {
            String sku;
            if (skuMapRule.getRuleType().equalsIgnoreCase(SkuEnmus.ruleType.splitByNum.getType())) {
                if (StringUtils.isBlank(skuMapRule.getRule1()) || StringUtils.isBlank(skuMapRule.getRule2()))
                    return null;
                return platformSku.substring(Integer.valueOf(skuMapRule.getRule1()) - 1, Integer.valueOf(skuMapRule.getRule1()) + Integer.valueOf(skuMapRule.getRule2()) - 1);

            } else if (skuMapRule.getRuleType().equalsIgnoreCase(SkuEnmus.ruleType.spliteByChar.getType())) {
                if (StringUtils.isBlank(skuMapRule.getRule1()) && StringUtils.isBlank(skuMapRule.getRule2()))
                    return null;
                if (StringUtils.isNotBlank(skuMapRule.getRule1())) {
                    int i = platformSku.indexOf(skuMapRule.getRule1());
                    if (i == -1)
                        return null;
                    sku = platformSku.substring(i + 1);
                } else {
                    sku = platformSku;
                }
                if (StringUtils.isNotBlank(skuMapRule.getRule2()) && StringUtils.isNotBlank(sku)) {
                    int i = sku.lastIndexOf(skuMapRule.getRule2());
                    if (i <= 0)
                        return null;
                    return sku.substring(0, i);
                }
                return sku;
            }
        } catch (Exception e) {
            LOGGER.error("sku映射规则ID为：" + skuMapRule.getId() + " 映射sku : " + platformSku + "异常", e);
            return null;
        }
        return null;
    }

    @Override
    public SellerSkuMap selectByEntry(SellerSkuMap map) {
        return sellerSkuMapMapper.selectByEntry(map);
    }
}
