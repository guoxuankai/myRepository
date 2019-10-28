package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.rondaful.cloud.common.enums.MessageEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.granary.GranaryUtils;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostEnum;
import com.rondaful.cloud.common.model.vo.freight.LogisticsCostVo;
import com.rondaful.cloud.common.model.vo.freight.LogisticsDetailVo;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListDTO;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListSku;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.SupplyChainCompany;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.commodity.CommodityBase;
import com.rondaful.cloud.order.entity.finance.OrderRequestVo;
import com.rondaful.cloud.order.entity.supplier.FreightTrial;
import com.rondaful.cloud.order.entity.supplier.FreightTrialDTO;
import com.rondaful.cloud.order.entity.supplier.InventoryDTO;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.OrderInvDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.system.OrderProfitCalculation;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.entity.user.ThirdAppDTO;
import com.rondaful.cloud.order.enums.LogisticsStrategyCovertToLogisticsLogisticsType;
import com.rondaful.cloud.order.enums.OrderDeliveryStatusNewEnum;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.enums.OrderSourceCovertToLogisticsServicePlatformEnum;
import com.rondaful.cloud.order.enums.OrderSourceCovertToUserServicePlatformEnum;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.enums.PlatformCommissionEnum;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.model.dto.remoteCommodity.GetCommodityBySkuListDTO;
import com.rondaful.cloud.order.model.dto.remoteUser.GetSupplyChainByUserIdDTO;
import com.rondaful.cloud.order.model.dto.remoteUser.UserXieRequest;
import com.rondaful.cloud.order.model.dto.remoteseller.GetByplatformSkuAndSiteDTO;
import com.rondaful.cloud.order.model.dto.remoteseller.GetByplatformSkuAndSiteVO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderReceiveAddressDTO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsResultVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsSkuVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsSupplierVO;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteFinanceService;
import com.rondaful.cloud.order.remote.RemoteLogisticsService;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.ISystemOrderService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.MappingOrderRuleUtil;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.RateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SystemOrderCommonServiceImpl implements ISystemOrderCommonService {
    @Autowired
    ISystemOrderCommonService systemOrderCommonService;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    @Autowired
    private RemoteSellerService remoteSellerService;
    @Autowired
    private ISysOrderLogService sysOrderLogService;
    @Autowired
    private SysOrderMapper sysOrderMapper;
    @Autowired
    private OrderMessageSender orderMessageSender;
    @Autowired
    private ISystemOrderService systemOrderService;
    @Autowired
    GranaryUtils granaryUtils;
    @Autowired
    private RateUtil rateUtil;
    @Autowired
    private RemoteLogisticsService remoteLogisticsService;
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private RemoteCommodityService remoteCommodityService;

    @Autowired
    private RemoteFinanceService remoteFinanceService;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Value("${wsdl.url}")
    public String url;
    @Value("${wsdl.AppToken}")
    public String appToken;
    @Value("${wsdl.AppKey}")
    public String appKey;

    private static String rateCurrency = PropertyUtil.getProperty("rateCurrency");

    private final String SERVICE_NAME = "getProductInventory";

    private final static Logger _log = LoggerFactory.getLogger(SystemOrderCommonServiceImpl.class);

    @Override
    public String calculateLogisticFeeBySKUS(SysOrder sysOrder) throws Exception {
        Boolean isERPOrder = true;
        FreightTrial freightTrial = new FreightTrial();
//        freightTrial.setSysOrderId(sysOrder.getSysOrderId());
        String warehouseCode = sysOrder.getDeliveryWarehouseCode();
        freightTrial.setCountryCode(sysOrder.getShipToCountry());
        freightTrial.setPostCode(sysOrder.getShipToPostalCode());
        freightTrial.setLogisticsCode(sysOrder.getDeliveryMethodCode());
        Byte orderSource = sysOrder.getOrderSource();
        if (orderSource == (byte) 4) freightTrial.setPlatformType("1");//eBay
        else if (orderSource == (byte) 5) freightTrial.setPlatformType("2");//Amazon
        else if (orderSource == (byte) 6) freightTrial.setPlatformType("4");//AliExpress
        else if (orderSource == (byte) 7) freightTrial.setPlatformType("3");//Wish
        else if (orderSource == (byte) 8) freightTrial.setPlatformType("5");//星商
        else freightTrial.setPlatformType("6");//other

        List<SysOrderDetail> detailList = sysOrder.getSysOrderDetails();

        boolean isGoodCangOrder = this.isGoodCangWarehouse(sysOrder.getDeliveryWarehouseId());
        // 废弃
        /*if (isGoodCangOrder) {
            //谷仓的订单
            List<String> gcSkuList = new ArrayList<>();
//            freightTrial.setWarehouseCode(sysOrder.getDeliveryWarehouseCode());
            freightTrial.setWarehouseId(Integer.valueOf(sysOrder.getDeliveryWarehouseId()));//TODO 仓库CODE 改为仓库ID
            freightTrial.setCallPlatform(1);
            for (SysOrderDetail detail : detailList)
                gcSkuList.add(detail.getSku() + ":" + detail.getSkuQuantity());
            freightTrial.setGcSKUList(gcSkuList);
        } else {
            // erp订单
            List<Map<String, Object>> erpSKUList = new ArrayList<>();
//            freightTrial.setWarehouseCode(sysOrder.getDeliveryWarehouseCode());
            freightTrial.setWarehouseId(sysOrder.getDeliveryWarehouseId());
            freightTrial.setCallPlatform(0);
            for (SysOrderDetail detail : detailList) {
                Map<String, Object> map = new HashMap<>();
                map.put("sku", detail.getSupplierSku());
                map.put("num", detail.getSkuQuantity());
                erpSKUList.add(map);
            }
            freightTrial.setErpSKUList(erpSKUList);
        }*/

        String result = remoteSupplierService.getFreightTrial(freightTrial);
        _log.error("___________供应商运费试算返回结果___________{}____________", result);
        String data = Utils.returnRemoteResultDataString(result, "运费试算调用供应商微服务异常 。。。");
        List<FreightTrialDTO> trialList = JSONObject.parseArray(data, FreightTrialDTO.class);
        if (CollectionUtils.isEmpty(trialList))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "邮寄方式不支持。。。");
        Double totalCost = new Double(0);
        String currency = "";
        for (FreightTrialDTO dto : trialList) {
            if (!dto.getTotalCost().equals(new Double(0))) {
                totalCost = dto.getTotalCost();
                currency = dto.getCurrency();
                break;
            }
        }
        if (totalCost.equals(new Double(0)))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "邮寄方式不支持。。。");
        if ("RMB".equalsIgnoreCase(currency))
            currency = "CNY";
        if (!rateCurrency.equalsIgnoreCase(currency)) {
            String rate = rateUtil.remoteExchangeRateByCurrencyCode(currency, rateCurrency);
            return OrderUtils.calculateMoney(new BigDecimal(totalCost).multiply(new BigDecimal(rate)), false).toString();
//            return new BigDecimal(totalCost).multiply(new BigDecimal(rate)).toString();
        } else
            return totalCost + "";
    }

    @Override
    public BigDecimal calculatePackageLogisticFeeBySKUS(SysOrderPackageDTO sysOrderPackageDTO,
                                                        SysOrderReceiveAddressDTO sysOrderReceiveAddressDTO,
                                                        Integer orderSource, Integer storeId, Integer handOrder) throws Exception {
        String sysOrderId = sysOrderPackageDTO.getSysOrderId();
        String orderTrackId = sysOrderPackageDTO.getOrderTrackId();
        CalculateLogisticsResultVO calculateLogisticsResultVO = this.calculateLogisticsFee(sysOrderPackageDTO,
                sysOrderReceiveAddressDTO, orderSource, storeId, handOrder);
        if (null == calculateLogisticsResultVO) {
            _log.info("订单{}, 包裹{} 预估物流费计算错误，不进行设值", sysOrderId, orderTrackId);
            return BigDecimal.ZERO;
        }

        List<SysOrderPackageDetailDTO> packageDetailDTOList = sysOrderPackageDTO.getSysOrderPackageDetailList();
        List<CalculateLogisticsSupplierVO> sellers = calculateLogisticsResultVO.getSellerList();
        List<CalculateLogisticsSupplierVO> suppliers = calculateLogisticsResultVO.getSupplierList();
        // 卖家预估物流费
        CalculateLogisticsSupplierVO sellerFeeVO = sellers.get(0);
        BigDecimal sellerEstimateShipCost = sellerFeeVO.getLogisticsFee();
        this.setPackageDetailEstimateShipCost(packageDetailDTOList,
                sellerFeeVO.getSkuList(), LogisticsCostEnum.sellers);
        sysOrderPackageDTO.setAdditionalFreightRate(new BigDecimal(Constants.ADDITIONAL_FREIGHT_RATE));
        sysOrderPackageDTO.setEstimateShipCost(sellerEstimateShipCost);
        sysOrderPackageDTO.setCalculateFeeInfo(FastJsonUtils.toJsonString(calculateLogisticsResultVO.getLogisticsCostData()));

        // 供应商预估物流费
        this.setPackageDetailEstimateShipCost(packageDetailDTOList, suppliers.get(0).getSkuList(), LogisticsCostEnum.supplier);

        return sellerEstimateShipCost;
    }

    private void setPackageDetailEstimateShipCost(List<SysOrderPackageDetailDTO> packageDetailDTOList,
                                                  List<CalculateLogisticsSkuVO> skuList, LogisticsCostEnum logisticsCostType) {
        for (CalculateLogisticsSkuVO skuVO : skuList) {
            for (SysOrderPackageDetailDTO orderPackageDetail : packageDetailDTOList) {
                if (Objects.equals(skuVO.getSku(), orderPackageDetail.getSku())) {
                    BigDecimal skuPerCost = skuVO.getSkuPerCost();
                    if (logisticsCostType == LogisticsCostEnum.sellers) {
                        orderPackageDetail.setSellerShipFee(skuPerCost);
                    } else if (logisticsCostType == LogisticsCostEnum.supplier) {
                        orderPackageDetail.setSupplierShipFee(skuPerCost);
                    } else if (logisticsCostType == LogisticsCostEnum.logistics) {
                        orderPackageDetail.setLogisticCompanyShipFee(skuPerCost);
                    }
                }
            }
        }
    }

    private CalculateLogisticsResultVO calculateLogisticsFee(SysOrderPackageDTO sysOrderPackageDTO,
                                                             SysOrderReceiveAddressDTO sysOrderReceiveAddressDTO,
                                                             Integer orderSource, Integer storeId, Integer handOrder) {
        String sysOrderId = sysOrderPackageDTO.getSysOrderId();
        String orderTrackId = sysOrderPackageDTO.getOrderTrackId();

        Integer warehouseId = sysOrderPackageDTO.getDeliveryWarehouseId();
        if (null == warehouseId) {
            _log.info("订单{}, 包裹{} 没有仓库，不进行预估物流费计算", sysOrderId, orderTrackId);
            return null;
        }

        String logisticsStrategy = sysOrderPackageDTO.getLogisticsStrategy();
        String deliveryMethodCode = sysOrderPackageDTO.getDeliveryMethodCode();

        if (StringUtils.isBlank(logisticsStrategy) && StringUtils.isBlank(deliveryMethodCode)) {
            _log.info("订单{}, 包裹{} 没有物流类型和物流方式，不进行预估物流费计算", sysOrderId, orderTrackId);
            return null;
        }

        CalculateLogisticsResultVO calculateLogisticsResultVO = null;
        String platformType = String.valueOf(
                OrderSourceCovertToLogisticsServicePlatformEnum.getLogisticsPlatformCode(orderSource));

        if (StringUtils.isNotBlank(logisticsStrategy) && StringUtils.isBlank(deliveryMethodCode)) {
            _log.info("订单{}, 包裹{} 有物流类型，没有物流方式，需要先获得物流方式再进行预估物流费计算", sysOrderId, orderTrackId);
            // 如果有 物流类型 没有物流方式，则需要先获得一个物流方式，再进行预估物流费计算
            SearchLogisticsListDTO searchLogisticsListDTO = new SearchLogisticsListDTO();
            searchLogisticsListDTO.setCity(sysOrderReceiveAddressDTO.getShipToCity());
            searchLogisticsListDTO.setCountryCode(sysOrderReceiveAddressDTO.getShipToCountry());
            searchLogisticsListDTO.setPlatformType(platformType);
            searchLogisticsListDTO.setPostCode(sysOrderReceiveAddressDTO.getShipToPostalCode());
            searchLogisticsListDTO.setWarehouseId(String.valueOf(sysOrderPackageDTO.getDeliveryWarehouseId()));
            searchLogisticsListDTO.setSearchType(LogisticsStrategyCovertToLogisticsLogisticsType
                    .getLogisticsTypeByLogisticsStrategy(logisticsStrategy));

            List<SearchLogisticsListSku> skuList = new ArrayList<>();
            for (SysOrderPackageDetailDTO sysOrderPackageDetailDTO : sysOrderPackageDTO.getSysOrderPackageDetailList()) {
                SearchLogisticsListSku searchLogisticsListSku = new SearchLogisticsListSku();
                searchLogisticsListSku.setSku(sysOrderPackageDetailDTO.getSku());
                searchLogisticsListSku.setSkuNumber(sysOrderPackageDetailDTO.getSkuQuantity());
                skuList.add(searchLogisticsListSku);
            }
            searchLogisticsListDTO.setSearchLogisticsListSkuList(skuList);
            searchLogisticsListDTO.setHandOrder(handOrder);
            searchLogisticsListDTO.setStoreId(storeId);

            List<LogisticsDetailVo> list = systemOrderService.getSuitLogisticsByType(searchLogisticsListDTO);
            if (CollectionUtils.isEmpty(list)) {
                _log.info("订单{}, 包裹{} 没有获得物流方式，停止预估物流费计算", sysOrderId, orderTrackId);
                return null;
            }
            LogisticsDetailVo logisticsDetailVo = list.get(0);
            calculateLogisticsResultVO = systemOrderService.calculateEstimateFreight(sysOrderPackageDTO, platformType,
                    String.valueOf(sysOrderPackageDTO.getDeliveryWarehouseId()), sysOrderReceiveAddressDTO.getShipToCountry(),
                    sysOrderReceiveAddressDTO.getShipToPostalCode(), null, logisticsDetailVo.getLogisticsCode(), sysOrderReceiveAddressDTO.getShipToCity(), storeId, handOrder);
            sysOrderPackageDTO.setDeliveryMethodCode(logisticsDetailVo.getLogisticsCode());
            sysOrderPackageDTO.setDeliveryMethod(logisticsDetailVo.getLogisticsName());
        } else {
            // 如果有 物流方式 没有物流类型，则直接进行预估物流费计算
            _log.info("订单{}, 包裹{} 没有物流类型，有物流方式，用匹配到的物流方式进行预估物流费计算", sysOrderId, orderTrackId);
            calculateLogisticsResultVO = systemOrderService.calculateEstimateFreight(sysOrderPackageDTO, platformType,
                    String.valueOf(sysOrderPackageDTO.getDeliveryWarehouseId()), sysOrderReceiveAddressDTO.getShipToCountry(),
                    sysOrderReceiveAddressDTO.getShipToPostalCode(), null, deliveryMethodCode, sysOrderReceiveAddressDTO.getShipToCity(), storeId, handOrder);
        }

        return calculateLogisticsResultVO;
    }

    /**
     * 计算预估物流费
     *
     * @param freightTrial {@link FreightTrial}
     * @return {@link List<FreightTrialDTO>}
     */
    private List<FreightTrialDTO> getFreightTrial(FreightTrial freightTrial) {
        String result = remoteSupplierService.getFreightTrial(freightTrial);
        String data = Utils.returnRemoteResultDataString(result, "运费试算调用供应商微服务异常 。。。");
        return JSONObject.parseArray(data, FreightTrialDTO.class);
    }

    @Override
    public boolean isGoodCangWarehouse(String warehouseId) {
        List<Integer> goodCangWarehouseIdList = this.getGoodCangWarehouseIdList();
        for (Integer goodCangWarehouseId : goodCangWarehouseIdList) {
            if (Objects.equals(goodCangWarehouseId, Integer.valueOf(warehouseId))) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> getWarehouseIdList(String warehouseType) {
        String str = Utils.returnRemoteResultDataString(remoteSupplierService.getsByType(warehouseType),
                "供应商服务异常");
        _log.error("调用供应商服务返回的谷仓仓库id列表为：{}", str);
        return JSONObject.parseArray(str, Integer.class);
    }

    @Override
    public List<Integer> getErpWarehouseIdList() {
        return this.getWarehouseIdList(Constants.Warehouse.RONDAFUL);
    }

    @Override
    public List<Integer> getWmsWarehouseIdList() {
        return this.getWarehouseIdList(Constants.Warehouse.WMS);
    }

    @Override
    public List<Integer> getGoodCangWarehouseIdList() {
        return this.getWarehouseIdList(Constants.Warehouse.GOODCANG);
    }

    @Override
    public List<Integer> getsWarehouseIdList(String firmCode) {
        return this.getWarehouseIdList(firmCode);
    }

    @Override
    public void judgeWareHouseIsEnough(SysOrder sysOrder, String username, boolean isFromDeliver) throws JSONException, IOException {
        String deliveryWarehouseCode = sysOrder.getDeliveryWarehouseCode();
        String sysOrderId = sysOrder.getSysOrderId();
        List<String> list = new ArrayList<>();
        sysOrder.getSysOrderDetails().forEach(item -> list.add(item.getSku()));
        _log.info("调用供应商服务 根据sku列表获取库存 参数：{},{}", sysOrder.getDeliveryWarehouseId(), FastJsonUtils.toJsonString(list));
        String result = remoteSupplierService.getsBySku(Integer.valueOf(sysOrder.getDeliveryWarehouseId()), FastJsonUtils.toJsonString(list));
        String data = Utils.returnRemoteResultDataString(result, "查询库存是否足够调用供应商服务异常。。。");
        if (StringUtils.isBlank(data)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "供应商服务异常");
        }
        List<InventoryDTO> InventoryDTOList = JSONObject.parseArray(data, InventoryDTO.class);
        if (Objects.equals(sysOrder.getSysOrderDetails().size(), InventoryDTOList.size())) {    //TODO 所有SKU都能找到对应的仓库信息
            if (StringUtils.isBlank(sysOrder.getDeliveryWarehouse())) {
                sysOrder.setDeliveryWarehouse(InventoryDTOList.get(0).getWarehouseName());
            }
        } else {   //TODO 全部或部分的SKU找不到对应的仓库信息
            if ("2_ZSW".equals(deliveryWarehouseCode) || "6_JHW".equals(deliveryWarehouseCode)) {
                _log.error("___________此订单:{}  此订单中有商品在仓库库存不足,卖家仍要发货___________", sysOrderId);
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PUSH_FAIL_2.pushFail_2(sysOrderId),
                        OrderHandleLogEnum.OrderStatus.STATUS_2.getMsg(), username));
                orderMessageSender.sendOrderStockOut(sysOrder.getSellerPlAccount(), sysOrder.getSysOrderId(),
                        MessageEnum.ORDER_OUT_OF_STOCK_NOTICE, null);
                if (!isFromDeliver) {
                    sysOrder.setOrderDeliveryStatus(OrderDeliveryStatusNewEnum.STOCKOUT.getValue());
                }
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "所选仓库库存不足，不支持发货。。。");
            }
        }
    }

    @Override
    public void judgeWareHouseIsEnough(SysOrderPackage sysOrderPackage, String username, String sellerPlAccount,
                                       boolean isFromDeliver) throws JSONException, IOException {
        String deliveryWarehouseCode = sysOrderPackage.getDeliveryWarehouseCode();
        String sysOrderId = sysOrderPackage.getSysOrderId();
        List<String> list = new ArrayList<>();
        sysOrderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> list.add(sysOrderPackageDetail.getSku()));

        List<InventoryDTO> InventoryDTOList = this.getAvailableInventory(sysOrderPackage.getDeliveryWarehouseId(), list);
        //TODO 所有SKU都能找到对应的仓库信息，如果所有的sku都在该仓库有库存，则会返回相应的条数
        if (Objects.equals(sysOrderPackage.getSysOrderPackageDetailList().size(), InventoryDTOList.size())) {
            for (InventoryDTO inventoryDTO : InventoryDTOList) {
                for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                    if (inventoryDTO.getPinlianSku().equals(sysOrderPackageDetail.getSku())) {
                        if (inventoryDTO.getLocalAvailableQty() < sysOrderPackageDetail.getSkuQuantity()) {
                            if ("2_ZSW".equals(deliveryWarehouseCode) || "6_JHW".equals(deliveryWarehouseCode)) {
                                _log.error("___________此订单:{}  此订单中有商品在仓库库存不足,卖家仍要发货___________", sysOrderId);
                                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PUSH_FAIL_2.pushFail_2(sysOrderId),
                                        OrderHandleLogEnum.OrderStatus.STATUS_2.getMsg(), username));
                                orderMessageSender.sendOrderStockOut(sellerPlAccount, sysOrderPackage.getSysOrderId(),
                                        MessageEnum.ORDER_OUT_OF_STOCK_NOTICE, null);
                                if (!isFromDeliver) {
                                    sysOrderPackage.setPackageDeliverStatus(OrderDeliveryStatusNewEnum.STOCKOUT.getValue());
                                }
                            } else {
                                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "所选仓库库存不足或停用，不支持发货。。。");
                            }
                        }
                    }
                }
            }
            if (StringUtils.isBlank(sysOrderPackage.getDeliveryWarehouse())) {
                sysOrderPackage.setDeliveryWarehouse(InventoryDTOList.get(0).getWarehouseName());
            }
        } else {   //TODO 全部或部分的SKU找不到对应的仓库信息
            if ("2_ZSW".equals(deliveryWarehouseCode) || "6_JHW".equals(deliveryWarehouseCode)) {
                _log.error("___________此订单:{}  此订单中有商品在仓库库存不足,卖家仍要发货___________", sysOrderId);
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PUSH_FAIL_2.pushFail_2(sysOrderId),
                        OrderHandleLogEnum.OrderStatus.STATUS_2.getMsg(), username));
                orderMessageSender.sendOrderStockOut(sellerPlAccount, sysOrderPackage.getSysOrderId(),
                        MessageEnum.ORDER_OUT_OF_STOCK_NOTICE, null);
                if (!isFromDeliver) {
                    sysOrderPackage.setPackageDeliverStatus(OrderDeliveryStatusNewEnum.STOCKOUT.getValue());
                }
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "所选仓库库存不足或停用，不支持发货。。。");
            }
        }
    }

    public List<InventoryDTO> getAvailableInventory(Integer warehouseId, List<String> skuList) {
        _log.info("调用供应商服务 根据sku列表获取库存 参数：{},{}", warehouseId, FastJsonUtils.toJsonString(skuList));
        String result = remoteSupplierService.getsBySku(warehouseId, FastJsonUtils.toJsonString(skuList));
        String data = Utils.returnRemoteResultDataString(result, "查询库存是否足够调用供应商服务异常。。。");
        if (StringUtils.isBlank(data)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "供应商服务异常");
        }
        return JSONObject.parseArray(data, InventoryDTO.class);
    }

    @Override
    public Boolean judgeSupportDeliverMethod(SysOrder sysOrder) throws Exception {
        String sysOrderId = sysOrder.getSysOrderId();
        String estimateShipCostStr = systemOrderCommonService.calculateLogisticFeeBySKUS(sysOrder);
        _log.error("___________________订单{}_________预估物流费为：{}__________", sysOrderId, estimateShipCostStr);
        if (StringUtils.isBlank(estimateShipCostStr)) {
            return false;
        }
        return true;
    }

    @Override
    public void updatePackageDeliverException(String orderTrackId, String warehouseShipExceptionMsg, String packageStatus,
                                              String modifier) {
        SysOrderPackage sysOrderPackage = new SysOrderPackage();
        sysOrderPackage.setOrderTrackId(orderTrackId);
        sysOrderPackage.setWarehouseShipException(warehouseShipExceptionMsg);
        sysOrderPackage.setPackageStatus(packageStatus);
        sysOrderPackage.setModifier(modifier);
        sysOrderPackage.setModifiedTime(new Date());
        sysOrderPackageMapper.updateByOrderTrackIdSelective(sysOrderPackage);
    }

    /**
     * 更新异常发货信息，发货状态，付款方式方法
     *
     * @param sysOrder            订单对象
     * @param msg                 异常发货信息
     * @param orderDeliveryStatus 订单发货状态
     * @param payStatus           付款方式
     */
    @Override
    public void updateDeliverExceptionInfo(SysOrder sysOrder, String msg, Byte orderDeliveryStatus, Byte payStatus, String operator) {
        String sysOrderId = sysOrder.getSysOrderId();
        try {
            sysOrder.setWarehouseShipException(msg);
            sysOrder.setOrderDeliveryStatus(orderDeliveryStatus);
            sysOrder.setPayStatus(payStatus);
            sysOrder.setUpdateBy(operator);
            sysOrderMapper.updateStatusAndShipExceptionBySysOrderId(sysOrder);
        } catch (Exception e) {
            _log.error("___________订单号:{} 发货异常更新异常发货信息出错________{}_________", sysOrderId, e);
        }
    }

    @Override
    public Empower queryAuthorizationFromSellerBySellerAccount(String platformSellerAccount, Integer platform) {
        return this.queryAuthorization(null, null, 1, platformSellerAccount, platform);
    }

    @Override
    public Empower queryAuthorizationFromSeller(SysOrder sysOrder) {
        //4eBay订单,5Amazon订单,6AliExpress订单,7Wish订单,8星商订单
        Byte orderSource = sysOrder.getOrderSource();
        Integer platform = OrderSourceCovertToUserServicePlatformEnum.getOtherPlatformCode(orderSource);
        return this.queryAuthorization(sysOrder.getPlatformShopId(), sysOrder.getSellerPlAccount(), 1, null, platform);
    }

    @Override
    public Empower queryAuthorizationByShopID(Integer empowerID) {
        return this.queryAuthorization(empowerID, null, 1, null, null);
    }

    /**
     * 远程获取卖家授权信息
     *
     * @param empowerId      用户ID
     * @param pinlianAccount 品连账号
     * @param status         状态
     * @param account        账号
     * @param platform       平台
     * @return {@link Empower}
     */
    private Empower queryAuthorization(Integer empowerId, String pinlianAccount, Integer status,
                                       String account, Integer platform) {
        String result = remoteSellerService.selectObjectByAccount(empowerId, pinlianAccount, status, account, platform);
        _log.error("___________查询卖家授权返回结果___________{}____________", result);
        String data = Utils.returnRemoteResultDataString(result, "获取授权调用卖家微服务异常 。。。");
        List<Empower> empowers = JSONObject.parseArray(data, Empower.class);
        if (CollectionUtils.isEmpty(empowers)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据登录人信息查出卖家授权信息为空。。。");
        }
        return empowers.get(0);
    }

//    @Override
//    public void cancelWareHouseOrder(SysOrder sysOrder, String cancelReason) throws Exception {
//        String warehouseCode = sysOrder.getDeliveryWarehouseCode();
//        if (warehouseCode.startsWith("GC"))
//            goodCangService.cancelGoodCangOrder(sysOrder, cancelReason);
//        else
//            remoteErpService.orderCannel(sysOrder.getOrderTrackId());
//    }

    /**
     * 根据仓库ID获取谷仓授权信息
     *
     * @param warehouseIds 仓库ID
     * @return
     */
    @Override
    public Map<String, WarehouseDTO> getGCAuthorizeByWarehouseId(List<String> warehouseIds) {
        String result = remoteSupplierService.getWarehouseByIds(JSONObject.toJSON(warehouseIds).toString());
        if (StringUtils.isBlank(result)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "供应商服务异常");
        }
        String data = Utils.returnRemoteResultDataString(result, "获取谷仓账号数据调用供应商微服务异常 。。。");
        List<WarehouseDTO> warehouseDTOList = JSONObject.parseArray(data, WarehouseDTO.class);
        Map<String, WarehouseDTO> returnMap = new HashMap<>();
        for (WarehouseDTO warehouseDTO : warehouseDTOList) {
            returnMap.put(String.valueOf(warehouseDTO.getWarehouseId()), warehouseDTO);
        }
        return returnMap;
    }



   /* public Map<String, AuthorizeDTO> getGCAuthorizeByCompanyCode(Set<String> set) {
        if (CollectionUtils.isEmpty(set))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求集合为空。。。");
        Set<String> uniqueSet = set.stream().map(x -> x.split("_")[1]).collect(Collectors.toSet());
        int size = uniqueSet.size();
        _log.error("__________获取谷仓账号数据需要返回的授权个数为________{}_________", size);
        Map<String, AuthorizeDTO> returnMap = new HashMap<>();
        Map<String, AuthorizeDTO> map = (HashMap<String, AuthorizeDTO>) redisUtils.get(RedisKey.GC_ACCOUNT_MAP);
        if (map == null || map.size() == 0 || map.isEmpty()) {
            String result = remoteSupplierService.getAuthorizeByCompanyCodeList(new ArrayList<>(uniqueSet));
            _log.error("___________根据谷仓账号代码查询账号返回结果___________{}____________", result);
            String data = Utils.returnRemoteResultDataString(result, "获取谷仓账号数据调用供应商微服务异常 。。。");
            List<AuthorizeDTO> dtoList = JSONObject.parseArray(data, AuthorizeDTO.class);
            if (CollectionUtils.isEmpty(dtoList))
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据谷仓账号代码查询仓库服务商数据为空。。。");
            for (AuthorizeDTO dto : dtoList) {
                returnMap.put(dto.getCompanyCode(), dto);
            }
            redisUtils.set(RedisKey.GC_ACCOUNT_MAP, returnMap);
            return returnMap;
        } else {
            Set<String> filterSet = new HashSet<>();
            for (String code : uniqueSet) {
                for (Map.Entry<String, AuthorizeDTO> entry : map.entrySet()) {
                    if (code.equals(entry.getKey())) {
                        returnMap.put(code, map.get(code));
                        filterSet.add(code);
                        break;
                    }
                }
            }
            if (returnMap.size() == size)
                return returnMap;
            if (filterSet.size() != 0)
                uniqueSet.removeAll(filterSet);
            String result = remoteSupplierService.getAuthorizeByCompanyCodeList(new ArrayList<>(uniqueSet));
            String data = Utils.returnRemoteResultDataString(result, "获取谷仓账号数据调用供应商微服务异常 。。。");
            List<AuthorizeDTO> dtoList = JSONObject.parseArray(data, AuthorizeDTO.class);
            if (CollectionUtils.isEmpty(dtoList) || dtoList.size() == 0)
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据谷仓账号代码查询仓库服务商数据为空。。。");
            for (AuthorizeDTO dto : dtoList) {
                returnMap.put(dto.getCompanyCode(), dto);
                map.put(dto.getCompanyCode(), dto);
            }
            redisUtils.set(RedisKey.GC_ACCOUNT_MAP, map);
        }
        return returnMap;
    }*/

    @Override
    public void setGrossMarginAndProfitMargin(SysOrder sysOrder) {
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
        BigDecimal firstCarriage = sysOrder.getFirstCarriage() == null ? new BigDecimal(0) : sysOrder.getFirstCarriage();//头程运费
        BigDecimal packingExpense = sysOrder.getPackingExpense() == null ? new BigDecimal(0) : sysOrder.getPackingExpense();//包装费用
//        BigDecimal actualShipCost = sysOrder.getActualShipCost() == null ? new BigDecimal(0) : sysOrder.getActualShipCost();//实际物流费
        BigDecimal estimateShipCost = sysOrder.getEstimateShipCost() == null ? new BigDecimal(0) : sysOrder.getEstimateShipCost();//预估物流费
        BigDecimal interest = sysOrder.getInterest() == null ? new BigDecimal(0) : sysOrder.getInterest();//支付提现利息
        BigDecimal cost = orderAmount.add(firstCarriage).add(packingExpense).add(estimateShipCost).add(interest).add(platformCommission.setScale(2, BigDecimal.ROUND_DOWN));//成本
        BigDecimal grossMargin = null;//毛利可为负值
        BigDecimal profitMargin = null;
        grossMargin = buyerActualPaidAmount.subtract(cost);//毛利可为负值
        if (new BigDecimal(0).compareTo(buyerActualPaidAmount) != 0) {
            profitMargin = grossMargin.divide(buyerActualPaidAmount, 2, BigDecimal.ROUND_DOWN);//利润率可为负
        }
        sysOrder.setGrossMargin(grossMargin);
        sysOrder.setProfitMargin(profitMargin);
    }

    @Override
    public void setGrossMarginAndProfitMarginAndTotal(OrderProfitCalculation orderProfitCalculation) {
        SysOrder sysOrder = orderProfitCalculation instanceof SysOrder ? ((SysOrder) orderProfitCalculation) : null;
        SysOrderNew sysOrderNew = orderProfitCalculation instanceof SysOrderNew ? ((SysOrderNew) orderProfitCalculation) : null;
        SysOrderDTO sysOrderDTO = orderProfitCalculation instanceof SysOrderDTO ? ((SysOrderDTO) orderProfitCalculation) : null;
        if (sysOrder != null) {
            BigDecimal buyerActualPaidAmount = sysOrder.getPlatformTotalPrice() == null ? new BigDecimal(0) : sysOrder.getPlatformTotalPrice();//平台订单总价
            BigDecimal orderAmount = sysOrder.getOrderAmount();//商品成本
            BigDecimal estimateShipCost = sysOrder.getEstimateShipCost();//预估物流费
            BigDecimal platformCommission = BigDecimal.ZERO;   //平台佣金
            BigDecimal grossMargin = null;//毛利
            BigDecimal profitMargin = null;//利润率
            if (orderAmount == null || estimateShipCost == null) {
                sysOrder.setTotal(null);
                sysOrder.setGrossMargin(null);
                sysOrder.setProfitMargin(null);
                sysOrder.setEstimateShipCost(null);
            } else {
                if (buyerActualPaidAmount.compareTo(new BigDecimal("0")) != 0) {
                    if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.AMAZON.getValue()), true);
                    } else if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.EBAY.getValue()), true);
                    } else if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.WISH.getValue()), true);
                    }
                }
                //毛利润=平台订单总价-（商品成本+预估物流费+平台佣金）
                grossMargin = buyerActualPaidAmount.subtract(orderAmount.add(estimateShipCost).add(platformCommission));
                if (BigDecimal.ZERO.compareTo(buyerActualPaidAmount) != 0) {
                    //利润率=毛利润/平台订单总价
                    profitMargin = OrderUtils.calculateMoney(grossMargin.divide(buyerActualPaidAmount, 2, BigDecimal.ROUND_DOWN), true);
                }
                sysOrder.setGrossMargin(grossMargin);
                sysOrder.setProfitMargin(profitMargin);
                sysOrder.setTotal(OrderUtils.calculateMoney(estimateShipCost.add(orderAmount), false));
            }
        }
        if (sysOrderNew != null) {
            BigDecimal buyerActualPaidAmount = sysOrderNew.getPlatformTotalPrice() == null ? new BigDecimal(0) : sysOrderNew.getPlatformTotalPrice();//平台订单总价
            BigDecimal orderAmount = sysOrderNew.getOrderAmount();//商品成本
            BigDecimal estimateShipCost = sysOrderNew.getEstimateShipCost();//预估物流费
            BigDecimal platformCommission = new BigDecimal("0");   //平台佣金
            BigDecimal grossMargin = null;//毛利
            BigDecimal profitMargin = null;//利润率
            if (orderAmount == null || estimateShipCost == null) {
                sysOrderNew.setTotal(null);
                sysOrderNew.setGrossMargin(null);
                sysOrderNew.setProfitMargin(null);
                sysOrderNew.setEstimateShipCost(null);
            } else {
                if (buyerActualPaidAmount.compareTo(new BigDecimal("0")) != 0) {
                    if (sysOrderNew.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.AMAZON.getValue()), true);
                    } else if (sysOrderNew.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.EBAY.getValue()), true);
                    } else if (sysOrderNew.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.WISH.getValue()), true);
                    }
                }
                //毛利润=平台订单总价-（商品成本+预估物流费+平台佣金）
                grossMargin = buyerActualPaidAmount.subtract(orderAmount.add(estimateShipCost).add(platformCommission));
                if (BigDecimal.ZERO.compareTo(buyerActualPaidAmount) != 0) {
                    //利润率=毛利润/平台订单总价
                    profitMargin = OrderUtils.calculateMoney(grossMargin.divide(buyerActualPaidAmount, 2, BigDecimal.ROUND_DOWN), true);
                }
                sysOrderNew.setGrossMargin(grossMargin);
                sysOrderNew.setProfitMargin(profitMargin);
                sysOrderNew.setTotal(OrderUtils.calculateMoney(estimateShipCost.add(orderAmount), false));
            }
        }

        if (sysOrderDTO != null) {
            BigDecimal buyerActualPaidAmount = sysOrderDTO.getPlatformTotalPrice() == null ? new BigDecimal(0) : sysOrderDTO.getPlatformTotalPrice();//平台订单总价
            BigDecimal orderAmount = sysOrderDTO.getOrderAmount();//商品成本
            BigDecimal estimateShipCost = sysOrderDTO.getEstimateShipCost();//预估物流费
            BigDecimal platformCommission = new BigDecimal("0");   //平台佣金
            BigDecimal grossMargin = null;//毛利
            BigDecimal profitMargin = null;//利润率
            if (orderAmount == null || estimateShipCost == null) {
                sysOrderDTO.setTotal(null);
                sysOrderDTO.setGrossMargin(null);
                sysOrderDTO.setProfitMargin(null);
                sysOrderDTO.setEstimateShipCost(null);
            } else {
                if (buyerActualPaidAmount.compareTo(new BigDecimal("0")) != 0) {
                    if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.AMAZON.getValue()), true);
                    } else if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.EBAY.getValue()), true);
                    } else if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
                        platformCommission = OrderUtils.calculateMoney(buyerActualPaidAmount.multiply(PlatformCommissionEnum.WISH.getValue()), true);
                    }
                }
                //毛利润=平台订单总价-（商品成本+预估物流费+平台佣金）
                grossMargin = buyerActualPaidAmount.subtract(orderAmount.add(estimateShipCost).add(platformCommission));
                if (BigDecimal.ZERO.compareTo(buyerActualPaidAmount) != 0) {
                    //利润率=毛利润/平台订单总价
                    profitMargin = OrderUtils.calculateMoney(grossMargin.divide(buyerActualPaidAmount, 2, BigDecimal.ROUND_DOWN), true);
                }
                sysOrderDTO.setGrossMargin(grossMargin);
                sysOrderDTO.setProfitMargin(profitMargin);
                sysOrderDTO.setTotal(OrderUtils.calculateMoney(estimateShipCost.add(orderAmount), false));
            }
        }
    }

    @Autowired
    private AmazonUploadDataServiceImpl amazonUploadDataService;

    @Override
    public String test() throws Exception {
//        List<String> skuList = new ArrayList<>();
//        skuList.add("BI0096100|56C9483E7B");
//        skuList.add("EN0215001|5C9248A341");
//        skuList.add("BI0096100|56C9483E7B");
//        skuList.add("60787712001");
//        skuList.add("HC0150100|1652128908");
//        skuList.add("HH0015502|2302135336");
//        List<SysOrder> listByCondition = amazonOrderItemService.getPendingConverAmazonListByCondition(skuList);
//        return listByCondition.toString();
        amazonUploadDataService.queryAmazonUploadDataAndDeal();
        return null;
    }

    @Override
    public void deliverAmazonSYSOrder(String sysOrderId) {
        List<SysOrder> sysOrders = sysOrderMapper.selectBatchSysOrderListBySysOrderId(new ArrayList<String>() {{
            add(sysOrderId);
        }});
//        systemOrderService.sendAmazonOrderDeliverInfo(sysOrders.get(0));
        systemOrderService.constructEbayOrderDeliverInfo(sysOrders.get(0));
    }

    @Override
    public WarehouseDTO getWarehouseInfoByCode(String warehouseId) {
        return this.getWarehouseInfo(warehouseId);
    }

    @Override
    public WarehouseDTO getWarehouseInfo(String warehouseId) {
        String result = remoteSupplierService.getWarehouseById(Integer.valueOf(warehouseId));
        String dataString = Utils.returnRemoteResultDataString(result, "查询库存调用供应商服务异常。。。");
        JSONObject dataJsonObject = JSONObject.parseObject(dataString);
        if (null == dataJsonObject) {
            return null;
        }

        return JSONObject.toJavaObject(dataJsonObject, WarehouseDTO.class);
    }

    @Override
    public LogisticsDTO queryLogisticsByCode(String logisticsCode, Integer warehouseId) {
        String result = remoteLogisticsService.queryLogisticsByCode(logisticsCode, warehouseId);
        String dataString = Utils.returnRemoteResultDataString(result, "查询物流商服务异常。。。");
        JSONObject dataJsonObject = JSONObject.parseObject(dataString);
        if (null == dataJsonObject) {
            return null;
        }

        return JSONObject.toJavaObject(dataJsonObject, LogisticsDTO.class);
    }

    @Override
    public List<OrderInvDTO> getMappingWarehouseBySkuList(List<String> skuList) {
        _log.debug("调用根据sku数据获取仓库列表的参数为: {}", FastJsonUtils.toJsonString(skuList));
        String result = remoteSupplierService.getsInvBySku(JSONObject.toJSON(skuList).toString());
        _log.debug("调用根据sku数据获取仓库列表返回的数据为: {}", result);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        String data = Utils.returnRemoteResultDataString(result, "供应商服务异常");

        return JSONObject.parseArray(data, OrderInvDTO.class);
    }

    @Override
    public GetSupplyChainByUserIdDTO getSupplyChinByUserId(Integer userId, Integer platformType) {
        return this.getSupplyChinByUserIdOrUsername(userId, null, platformType);
    }

    @Override
    public GetSupplyChainByUserIdDTO getSupplyChinByUserIdOrUsername(Integer userId, String userName, Integer platformType) {
        String result = remoteUserService.getSupplyChinByUserIdOrUsername(userId, userName, platformType);
        if (StringUtils.isBlank(result)) {
            return null;
        }
        String dataString = Utils.returnRemoteResultDataString(result, "查询用户调用用户服务异常。。。");
        JSONObject dataJsonObject = JSONObject.parseObject(dataString);
        if (null == dataJsonObject) {
            return null;
        }
        return JSONObject.toJavaObject(dataJsonObject, GetSupplyChainByUserIdDTO.class);
    }

    @Override
    public List<UserXieRequest> getChildAccountFromSeller(Integer sellerId) {
        return this.getChildAccount(sellerId, null, Constants.System.PLATFORM_TYPE_SELLER.toString());
    }

    private List<UserXieRequest> getChildAccount(Integer userId, String userName, String platformType) {
        String result = remoteUserService.getChildAccount(userId, userName, platformType);
        String dataString = Utils.returnRemoteResultDataString(result, "查询用户调用用户服务异常。。。");
        return JSONArray.parseArray(dataString, UserXieRequest.class);
    }

    @Override
    public ThirdAppDTO getByAppKey(String appKey) {
        String result = remoteUserService.getByAppKey(appKey);
        String dataString = Utils.returnRemoteResultDataString(result, "查询用户调用用户服务异常。。。");
        JSONObject dataJsonObject = JSONObject.parseObject(dataString);
        if (null == dataJsonObject) {
            return null;
        }

        return JSONObject.toJavaObject(dataJsonObject, ThirdAppDTO.class);
    }

    @Override
    public List<LogisticsDTO> matchOrderLogistics(String warehouseSourceType, SysOrderPackageDTO sysOrderPackageDTO, String platform,
                                                  Integer deliveryWarehouseId) {
        FreightTrial freightTrial = MappingOrderRuleUtil.getFreightTrialObjectNew(warehouseSourceType, sysOrderPackageDTO,
                platform, deliveryWarehouseId);
        _log.debug("请求匹配物流方式的参数为: {}", FastJsonUtils.toJsonString(freightTrial));
        String result = remoteSupplierService.matchLogistics(freightTrial);
        _log.debug("请求匹配物流方式的结果为: {}", result);
        String dataString = Utils.returnRemoteResultDataString(result, "查询用户调用用户服务异常。。。");
        //仓库下的邮寄方式列表
        return JSONObject.parseArray(dataString, LogisticsDTO.class);
    }

    @Override
    public Empower findOneEmpowerByAccount(Integer platform, String account, String webName, String empowerId) {

        String result = remoteSellerService.findOneEmpowByAccount(platform, account, webName, empowerId);
        String dataString = Utils.returnRemoteResultDataString(result, "查询卖家服务异常");
        return JSONObject.parseObject(dataString, Empower.class);
    }

    @Override
    public List<CommodityBase> getCommodityBySkuList(Integer sellerPlId, List<String> plSkuList) {
        GetCommodityBySkuListDTO getCommodityBySkuListDTO = new GetCommodityBySkuListDTO();
        getCommodityBySkuListDTO.setSellerId(sellerPlId);
        getCommodityBySkuListDTO.setSystemSkuList(plSkuList);
        String result = remoteCommodityService.getCommodityBySkuList(getCommodityBySkuListDTO);
        String data = Utils.returnRemoteResultDataString(result, "查询sku列表对应商品信息时，商品服务异常");
        if (StringUtils.isBlank(data)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统查询不到商品。。。");
        }
        return JSONObject.parseArray(data, CommodityBase.class);
    }

    @Override
    public LogisticsCostVo getEstimateFreight(LogisticsCostVo logisticsCostVo) {
        _log.error("获取预估物流费请求的参数为: {}", FastJsonUtils.toJsonString(logisticsCostVo));
        String result = remoteSupplierService.queryFreightByLogisticsCode(logisticsCostVo);
        _log.error("获取预估物流费返回的数据为: {}", result);
        String data = Utils.returnRemoteResultDataString(result, "获取预估物流费异常");
        if (StringUtils.isBlank(data)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取不到预估物流费");
        }
        return JSON.parseObject(data, new TypeReference<LogisticsCostVo>() {
        });
    }

    @Override
    public Map<String, String> financeGenerate(OrderRequestVo orderRequestVo) {
        _log.debug("订单发货调用财务冻结金额请求参数为: {}", FastJsonUtils.toJsonString(orderRequestVo));
        String result = remoteFinanceService.generate(orderRequestVo);
        _log.debug("订单发货调用财务冻结金额返回结果为: {}", result);
        String data = Utils.returnRemoteResultDataString(result, "发货调用财务服务出错。。。");
        return JSONObject.parseObject(data, new TypeReference<HashMap<String, String>>() {
        });
    }

    @Override
    public List<LogisticsDetailVo> getSuitLogisticsByType(SearchLogisticsListDTO searchLogisticsListDTO) {
        _log.debug("获取合适的物流方式请求的参数为: {}", FastJsonUtils.toJsonString(searchLogisticsListDTO));
        String result = remoteSupplierService.getSuitLogisticsByType(searchLogisticsListDTO);
        _log.debug("获取合适的物流方式返回的数据为: {}", result);
        String data = Utils.returnRemoteResultDataString(result, "获取合适的物流方式异常");
        if (StringUtils.isBlank(data)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "获取不到合适的物流方式");
        }
        return JSONObject.parseArray(data, LogisticsDetailVo.class);
    }

    @Override
    public List<SupplyChainCompany> getSupplyChainByUserId(String platformType, List<Integer> userIds) {
        _log.debug("请求获取供应链公司的信息为: platformType={}, list={}", platformType, FastJsonUtils.toJsonString(userIds));
        String result = remoteUserService.getSupplyChainByUserId(platformType, userIds);
        _log.debug("请求获取供应链公司返回的信息为: result={}", result);
        String string = com.rondaful.cloud.common.utils.Utils.returnRemoteResultDataString(result,
                "获取供应链信息调用用户服务异常");
        if (StringUtils.isBlank(string)) {
            _log.error("调用用户服务查询供应链公司信息异常！");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
        }
        return JSONObject.parseArray(string, SupplyChainCompany.class);
    }

    @Override
    public List<GetByplatformSkuAndSiteVO> getByplatformSkuAndSite(List<String> platformSkuList, String site,
                                                                   String type, Integer empowerId) {
        GetByplatformSkuAndSiteDTO getByplatformSkuAndSiteDTO = new GetByplatformSkuAndSiteDTO();
        getByplatformSkuAndSiteDTO.setEmpowerId(empowerId);
        getByplatformSkuAndSiteDTO.setPlatformSku(platformSkuList);
        getByplatformSkuAndSiteDTO.setSite(site);
        getByplatformSkuAndSiteDTO.setType(type);
        _log.debug("请求获取刊登平台sku信息的参数为: {}", FastJsonUtils.toJsonString(getByplatformSkuAndSiteDTO));
        String result = remoteSellerService.getByplatformSkuAndSite(getByplatformSkuAndSiteDTO);
        _log.debug("请求获取刊登平台sku信息的返回结果为: {}", FastJsonUtils.toJsonString(result));

        String string = com.rondaful.cloud.common.utils.Utils.returnRemoteResultDataString(result,
                "获取刊登平台sku信息调用卖家服务异常");
        if (StringUtils.isBlank(string)) {
            _log.error("获取刊登平台sku信息调用卖家服务异常！");
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常");
        }
        return JSONObject.parseArray(string, GetByplatformSkuAndSiteVO.class);
    }

    /**
     * 判断订单利润阈值,true利润大于阈值，false利润小于阈值
     *
     * @param sellerId 卖家账户
     * @param storeId  店铺ID
     * @param profitMargin 利润率
     * @return
     */
    @Override
    public Boolean getThreshold(Integer sellerId, Integer storeId, BigDecimal profitMargin) {
        String result = remoteUserService.getThreshold(sellerId, storeId);
        _log.error("用户ID：{}，店铺ID：{}调用用户模块返回店铺利润阈值: {}", sellerId, storeId, result);
        String data = Utils.returnRemoteResultDataString(result, "调用用户服务返回店铺利润阈值出错。。。");
        if (data != null && profitMargin != null) {
            BigDecimal storeGrossMargin = new BigDecimal(data);
            storeGrossMargin = storeGrossMargin.divide(new BigDecimal(100));
            if (profitMargin.compareTo(storeGrossMargin) == -1) {
                return false;
            }
        }
        return true;
    }
}
