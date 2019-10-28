package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.tx.annotation.TxTransaction;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.SearchLogisticsListSku;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.AreaCode;
import com.rondaful.cloud.order.entity.QuerySkuMapForOrderVo;
import com.rondaful.cloud.order.entity.SkuMapBind;
import com.rondaful.cloud.order.entity.SupplyChainCompany;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.commodity.CommodityBase;
import com.rondaful.cloud.order.entity.commodity.CommoditySpec;
import com.rondaful.cloud.order.entity.commodity.SkuInventoryVo;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.OrderInvDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.enums.LogisticsStrategyCovertToLogisticsLogisticsType;
import com.rondaful.cloud.order.enums.OrderCodeEnum;
import com.rondaful.cloud.order.enums.OrderDeliveryStatusNewEnum;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.enums.OrderPackageHandleEnum;
import com.rondaful.cloud.order.enums.OrderPackageStatusEnum;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.SellerSkuMapMapper;
import com.rondaful.cloud.order.mapper.SysOrderDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderLogMapper;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.mapper.SysOrderNewMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.mapper.SysOrderReceiveAddressMapper;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderReceiveAddressDTO;
import com.rondaful.cloud.order.model.xingShang.response.OrderDetailXS;
import com.rondaful.cloud.order.model.xingShang.response.SysOrderPackageDetailXS;
import com.rondaful.cloud.order.model.xingShang.response.SysOrderPackageXS;
import com.rondaful.cloud.order.model.xingShang.response.SysOrderReceiveAddressXS;
import com.rondaful.cloud.order.model.xingShang.response.SysOrderXS;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteFinanceService;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.ICountryCodeService;
import com.rondaful.cloud.order.service.IDistributionOrderService;
import com.rondaful.cloud.order.service.IOrderRuleService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.ISystemOrderService;
import com.rondaful.cloud.order.utils.BeanConvertor;
import com.rondaful.cloud.order.utils.CheckOrderUtils;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: zhangjinglei
 * @description: 星商订单相关接口
 * @date: 2019/5/4
 */
@Service
public class DistributionOrderServiceImpl implements IDistributionOrderService {
    @Autowired
    private SysOrderLogMapper sysOrderLogMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private SkuMapServiceImpl skuMapService;
    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private IDistributionOrderService distributionOrderService;
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(DistributionOrderServiceImpl.class);
    @Autowired
    private RedissLockUtil redissLockUtil;
    @Autowired
    private SysOrderServiceImpl sysOrderServiceImpl;
    @Autowired
    private ISysOrderService sysOrderService;
    @Autowired
    private ISystemOrderService systemOrderService;
    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;
    @Autowired
    private RemoteFinanceService remoteFinanceService;
    @Autowired
    private ISysOrderLogService sysOrderLogService;
    @Autowired
    private IOrderRuleService orderRuleService;
    @Autowired
    private SysOrderMapper sysOrderMapper;
    @Autowired
    private SysOrderDetailMapper sysOrderDetailMapper;
    @Autowired
    private SellerSkuMapMapper sellerSkuMapMapper;
    @Autowired
    private ICountryCodeService countryCodeService;
    @Autowired
    private SysOrderNewMapper sysOrderNewMapper;
    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;
    @Autowired
    private SysOrderPackageDetailMapper sysOrderPackageDetailMapper;
    @Autowired
    private SysOrderReceiveAddressMapper sysOrderReceiveAddressMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributionOrderServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> createSysOrderForXSNew(HttpServletRequest request, SysOrder sysOrder) throws Exception {
        LOGGER.info("______________对接商家创建订单_____________{}____________", FastJsonUtils.toJsonString(sysOrder));

        Integer handOrder = 0;//是否手工单，0 否 1是
        sysOrder.setHandOrder(handOrder);

        //星商传的参数的相关校验
        CheckOrderUtils.distributionCreateSysOrder(sysOrder);

        //判断来源订单号是否存在重复推送
        if (CollectionUtils.isNotEmpty(sysOrderNewMapper.queryPlOrderIdBySourceOrderId(sysOrder.getSourceOrderId()))) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300137);
        }

        // 店铺账号
        Empower empower = this.validateThirdAccount(sysOrder.getPlatformShopId());

        String sysOrderId = OrderUtils.getPLOrderNumber();
        sysOrder.setSysOrderId(sysOrderId);
        LOGGER.error("对接商家创建订单生成的系统订单号为: {}", sysOrderId);

        // SKU映射
        this.fillDeliverGoodMustData(sysOrder, empower);

        //设置国家信息
        setCountryName(sysOrder);

        // 填充商品信息
        this.commodityMap(sysOrder);

        // 填充包裹数据
        SysOrderPackageDTO sysOrderPackageDTO = this.constructPackageDTO(sysOrder);

        // 匹配发货仓库和邮寄方式，计算运费
        this.mappingDeliveryWarehouse(empower, sysOrder, sysOrderPackageDTO, handOrder);

        //插入数据库
        insertDb(sysOrder, sysOrderId, sysOrderPackageDTO);

        //订单创建成功查询出来
        SysOrderNew orderNew = sysOrderNewMapper.queryOrderByOrderId(sysOrderId);

        //自动发货
        autoDeliver(orderNew);

        //订单创建成功返回订单号
        return returnCreateOrderSuccessInfo(orderNew);
    }

    public Map<String, String> returnCreateOrderSuccessInfo(SysOrderNew orderNew) {
        HashMap<String, String> map = new HashMap<>();
        map.put("plId", orderNew.getSysOrderId());
        return map;
    }

    public void insertDb(SysOrder sysOrder, String sysOrderId, SysOrderPackageDTO sysOrderPackageDTO) {
        try {
            LOGGER.debug("第三方订单{}插入的数据为：{}", sysOrderId, FastJsonUtils.toJsonString(sysOrder));
            SysOrderNew sysOrderNew = new SysOrderNew();
            BeanUtils.copyProperties(sysOrder, sysOrderNew);
            sysOrderNew.setIsConvertOrder(Constants.isConvertOrder.YES);
            //插入系统订单
            sysOrderNewMapper.insertSelective(sysOrderNew);
            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
                //插入系统订单项
                sysOrderDetailMapper.insertSelective(sysOrderDetail);
            }
            // 插入包裹表
            SysOrderPackage sysOrderPackage = this.getSysOrderPackageFromSysOrderPackageDTO(sysOrderPackageDTO);
            sysOrderLogMapper.insertSelective(new SysOrderLog(
                    sysOrderPackageDTO.getSysOrderId(),
                    sysOrderPackageDTO.getMappingOrderRuleLog(),
                    OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                    OrderHandleLogEnum.Operator.SYSTEM.getMsg()
            ));
            sysOrderPackageMapper.insertSelective(sysOrderPackage);
            for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                // 插入包裹明细表
                sysOrderPackageDetailMapper.insertSelective(sysOrderPackageDetail);
            }

            // 收货地址
            SysOrderReceiveAddress sysOrderReceiveAddress = new SysOrderReceiveAddress();
            BeanUtils.copyProperties(sysOrder, sysOrderReceiveAddress);
            sysOrderReceiveAddressMapper.insertSelective(sysOrderReceiveAddress);

            //添加操作日志
            sysOrderLogService.insertSelective(
                    new SysOrderLog(sysOrder.getSysOrderId(),
                            OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrder.getSysOrderId()),
                            OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                            sysOrder.getSellerPlAccount()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300132);
        }
    }

    public void setCountryName(SysOrder sysOrder) {
        String countryName = "";
        try {
            String result = remoteUserService.getArea("", sysOrder.getShipToCountry());
            String data = Utils.returnRemoteResultDataString(result, "用户服务异常");
            AreaCode areaCode = JSONObject.parseObject(data, AreaCode.class);
            countryName = areaCode.getName();
        } catch (Exception e) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300132);
        }
        sysOrder.setShipToCountryName(countryName);
    }

    public void autoDeliver(SysOrderNew orderNew) {
        if (orderNew.getGrossMargin() != null) {
        try {
            if (Boolean.valueOf(Utils.returnRemoteResultDataString(remoteFinanceService.autopayVerify(orderNew.getSellerPlId()), "财务服务异常"))
            &&systemOrderCommonService.getThreshold(orderNew.getSellerPlId(), orderNew.getPlatformShopId(), orderNew.getProfitMargin())) {
                try {
                    logger.error("自动发货订单号：{}", orderNew.getSysOrderId());
                    systemOrderService.deliverGoodSingleNew(orderNew.getSysOrderId(), true);
                } catch (Exception e) {
                    logger.error("自动发货失败，订单号：{},异常信息：{}", orderNew.getSysOrderId(), e.toString());
                }
            }
        } catch (Exception e) {
            logger.error("校验是否可以自动支付，调用财务服务异常返回Null，用户ID：{}", orderNew.getSellerPlId());
        }
        }
    }

    private void mappingDeliveryWarehouse(Empower empower, SysOrder sysOrder, SysOrderPackageDTO orderPackageDTO, Integer handOrder) throws Exception {


        String deliveryWarehouseId = null == orderPackageDTO.getDeliveryWarehouseId() ? null
                : String.valueOf(orderPackageDTO.getDeliveryWarehouseId());
        String deliveryMethodCode = orderPackageDTO.getDeliveryMethodCode();

        OrderInvDTO orderInv = null;
        if (!StringUtils.isBlank(deliveryWarehouseId)) {
            WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfoByCode(deliveryWarehouseId);
            if (null == warehouseDTO) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300125);
            }
            orderPackageDTO.setDeliveryWarehouse(warehouseDTO.getWarehouseName());
            orderPackageDTO.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());

            List<OrderInvDTO> list = systemOrderCommonService.getMappingWarehouseBySkuList(sysOrder.getSkus());
            if (CollectionUtils.isEmpty(list)) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300126);
            }

            boolean mappingFlag = false;
            for (OrderInvDTO orderInvDTO : list) {
                if (String.valueOf(orderInvDTO.getWarehouseId()).equals(deliveryWarehouseId)) {
                    orderInv = orderInvDTO;
                    mappingFlag = true;
                    break;
                }
            }

            if (!mappingFlag) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300126);
            }
        }

        if (!StringUtils.isBlank(deliveryMethodCode)) {
            LogisticsDTO logisticsDTO = systemOrderCommonService.queryLogisticsByCode(deliveryMethodCode,
                    orderPackageDTO.getDeliveryWarehouseId());
            if (null == logisticsDTO) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300127);
            }
            sysOrder.setDeliveryMethod(logisticsDTO.getShortName());
            sysOrder.setShippingCarrierUsed(logisticsDTO.getCarrierName());
            sysOrder.setShippingCarrierUsedCode(logisticsDTO.getCarrierCode());
            sysOrder.setEbayCarrierName(logisticsDTO.getEbayCarrier());
            sysOrder.setAmazonCarrierName(logisticsDTO.getAmazonCarrier());
            sysOrder.setAmazonShippingMethod(logisticsDTO.getAmazonCode());
            orderPackageDTO.setShippingCarrierUsed(logisticsDTO.getCarrierName());
            orderPackageDTO.setShippingCarrierUsedCode(logisticsDTO.getCarrierCode());
            orderPackageDTO.setDeliveryMethod(logisticsDTO.getShortName());
            orderPackageDTO.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsStrategy());
            orderPackageDTO.setAmazonCarrierName(logisticsDTO.getAmazonCarrier());
            orderPackageDTO.setAmazonShippingMethod(logisticsDTO.getAmazonCode());
            orderPackageDTO.setEbayCarrierName(logisticsDTO.getEbayCarrier());
        }
        if (sysOrder.getAppointDeliveryWay() == 1) {
            // 指定物流方式，需要增加一个判断
            this.judgeLogistics(orderInv, orderPackageDTO);
        }

        //TODO -----------------自动匹配物流规则
        if (StringUtils.isBlank(deliveryWarehouseId) && StringUtils.isBlank(deliveryMethodCode)) {
            SysOrderDTO sysOrderDTO = new SysOrderDTO();
            BeanUtils.copyProperties(sysOrder, sysOrderDTO);
            sysOrderDTO.setSysOrderPackageList(new ArrayList<SysOrderPackageDTO>() {{
                add(orderPackageDTO);
            }});
            List<SearchLogisticsListSku> plSkuList = new ArrayList<>();
            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
                plSkuList.add(new SearchLogisticsListSku() {{
                    setSku(sysOrderDetail.getSku());
                    setSkuNumber(sysOrderDetail.getSkuQuantity());
                }});
            }
            SysOrderReceiveAddress sysOrderReceiveAddress = new SysOrderReceiveAddress();
            SysOrderReceiveAddressDTO addressDTO = new SysOrderReceiveAddressDTO();
            BeanUtils.copyProperties(sysOrder, sysOrderReceiveAddress);
            BeanUtils.copyProperties(sysOrderReceiveAddress, addressDTO);
            sysOrderDTO.setSysOrderReceiveAddress(addressDTO);
            skuMapService.mappingLogisticsRule("Other", empower, sysOrderDTO, plSkuList, null, false);
            SysOrderPackageDTO sysOrderPackageDTO = sysOrderDTO.getSysOrderPackageList().get(0);
            BeanUtils.copyProperties(sysOrderPackageDTO, orderPackageDTO);
        }

        // 计算包裹的预估物流费
        SysOrderReceiveAddressDTO sysOrderReceiveAddressDTO = new SysOrderReceiveAddressDTO();
        BeanUtils.copyProperties(sysOrder, sysOrderReceiveAddressDTO);
        Integer orderSource = Integer.valueOf(sysOrder.getOrderSource());
        if (StringUtils.isBlank(orderPackageDTO.getDeliveryMethodCode())) {
            orderPackageDTO.setLogisticsStrategy(null);
        }
        BigDecimal estimateFee = systemOrderCommonService.calculatePackageLogisticFeeBySKUS(orderPackageDTO,
                sysOrderReceiveAddressDTO, orderSource, sysOrder.getPlatformShopId(), handOrder);
        if (StringUtils.isBlank(orderPackageDTO.getLogisticsStrategy())) {
            orderPackageDTO.setLogisticsStrategy(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsStrategy());
        }

        sysOrder.setEstimateShipCost(estimateFee);
        sysOrder.setTotal(sysOrder.getOrderAmount().add(sysOrder.getEstimateShipCost()));
        //设置预估利润、利润率
        systemOrderCommonService.setGrossMarginAndProfitMarginAndTotal(sysOrder);
    }

    private SysOrderPackage getSysOrderPackageFromSysOrderPackageDTO(SysOrderPackageDTO orderPackageDTO) {
        SysOrderPackage sysOrderPackage = new SysOrderPackage();
        List<SysOrderPackageDetailDTO> orderPackageDetailDTOList = orderPackageDTO.getSysOrderPackageDetailList();
        List<SysOrderPackageDetail> sysOrderPackageDetailList = new ArrayList<>();
        BeanUtils.copyProperties(orderPackageDTO, sysOrderPackage);
        for (SysOrderPackageDetailDTO orderPackageDetail : orderPackageDetailDTOList) {
            SysOrderPackageDetail sysOrderPackageDetail = new SysOrderPackageDetail();
            BeanUtils.copyProperties(orderPackageDetail, sysOrderPackageDetail);
            sysOrderPackageDetailList.add(sysOrderPackageDetail);
        }
        sysOrderPackage.setSysOrderPackageDetailList(sysOrderPackageDetailList);

        return sysOrderPackage;
    }

    /**
     * 封装包裹数据
     *
     * @param sysOrder 星商传输过来的数据
     * @return SysOrderPackage 封装为包裹
     */
    private SysOrderPackageDTO constructPackageDTO(SysOrder sysOrder) {
        SysOrderPackageDTO sysOrderPackageDTO = new SysOrderPackageDTO();
        String orderTrackId = OrderUtils.getPLTrackNumber();
        BeanUtils.copyProperties(sysOrder, sysOrderPackageDTO);
        sysOrderPackageDTO.setOrderTrackId(orderTrackId);
        List<SysOrderPackageDetailDTO> packageDetailList = new ArrayList<>();

        List<SysOrderDetail> orderDetailList = sysOrder.getSysOrderDetails();
        for (SysOrderDetail orderDetail : orderDetailList) {
            if (StringUtils.isBlank(orderDetail.getSourceSku())) {
                orderDetail.setSourceSku(orderDetail.getSku());
            }
            SysOrderPackageDetailDTO sysOrderPackageDetailDTO = new SysOrderPackageDetailDTO();
            BeanUtils.copyProperties(orderDetail, sysOrderPackageDetailDTO);
            sysOrderPackageDetailDTO.setSkuCost(orderDetail.getItemCost());
            sysOrderPackageDetailDTO.setSkuPrice(orderDetail.getItemPrice());
            sysOrderPackageDetailDTO.setSkuUrl(orderDetail.getItemUrl());
            sysOrderPackageDetailDTO.setSkuName(orderDetail.getItemName());
            sysOrderPackageDetailDTO.setSkuNameEn(orderDetail.getItemNameEn());
            sysOrderPackageDetailDTO.setSkuAttr(orderDetail.getItemAttr());
            sysOrderPackageDetailDTO.setOrderTrackId(orderTrackId);
            sysOrderPackageDetailDTO.setSupplierId(orderDetail.getSupplierId().intValue());
            sysOrderPackageDetailDTO.setSupplyChainCompanyId(sysOrder.getSupplyChainCompanyId());
            sysOrderPackageDetailDTO.setSupplyChainCompanyName(sysOrder.getSupplyChainCompanyName());
            sysOrderPackageDetailDTO.setModifier(sysOrder.getPlatformSellerAccount());
            packageDetailList.add(sysOrderPackageDetailDTO);
        }
        sysOrderPackageDTO.setDeliveryWarehouseId(sysOrder.getDeliveryWarehouseCode() == null ? null : Integer.valueOf(sysOrder.getDeliveryWarehouseCode()));
        sysOrderPackageDTO.setDeliveryWarehouseCode("");
        sysOrderPackageDTO.setSysOrderPackageDetailList(packageDetailList);
        return sysOrderPackageDTO;
    }

    /**
     * 判断该仓库和渠道 是否可以发该订单SKU
     *
     * @param orderInvDTO        仓库信息
     * @param sysOrderPackageDTO 包裹
     * @throws Exception 异常
     */
    private void judgeLogistics(OrderInvDTO orderInvDTO, SysOrderPackageDTO sysOrderPackageDTO) throws Exception {
        if (null == orderInvDTO) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300127);
        }

        //仓库下的邮寄方式列表
        List<LogisticsDTO> logisticsList = systemOrderCommonService.matchOrderLogistics(orderInvDTO.getServiceCode(),
                sysOrderPackageDTO, null, sysOrderPackageDTO.getDeliveryWarehouseId());

        if (CollectionUtils.isEmpty(logisticsList)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300127);
        }

        String deliveryMethodCode = sysOrderPackageDTO.getDeliveryMethodCode();
        for (LogisticsDTO logisticsDTO : logisticsList) {
            if (Objects.equals(logisticsDTO.getCode(), deliveryMethodCode)) {
                return;
            }
        }
        throw new GlobalException(OrderCodeEnum.RETURN_CODE_300127);
    }

    @Override
    public SysOrderXS queryDistributionSysOrderByID(HttpServletRequest request, Integer platformShopId, String sysOrderId, String sourceOrderId) {
        if (StringUtils.isBlank(sourceOrderId)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300100);
        }
        if (StringUtils.isBlank(sysOrderId)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300122);
        }
        //使用订单来源ID和品连订单ID去查询
        SysOrderNew sysOrder = sysOrderNewMapper.selectSysOrderBySysOrderIdAndSourceOrderId(sysOrderId, sourceOrderId);
        if (sysOrder == null) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300123);
        }
        String distribution = (String) request.getAttribute("distribution");

        if (Constants.DistributionType.DistributionType_QT.equals(distribution)) {
            if (sysOrder.getOrderSource() != OrderSourceEnum.THIRD_API_PUSH.getValue()) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300124);
            }
        }
        if (!platformShopId.equals(sysOrder.getPlatformShopId())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300124);
        }
        SysOrderXS sysOrderXS = BeanConvertor.copy(sysOrder, SysOrderXS.class);
        getOrderDetail(sysOrderXS);
        sysOrderXS.setPlatformShopAccount(sysOrder.getPlatformSellerAccount());
        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderReceiveAddressMapper.queryAddressByOrderId(sysOrderId);
        SysOrderReceiveAddressXS sysOrderReceiveAddressXS = new SysOrderReceiveAddressXS();
        BeanUtils.copyProperties(sysOrderReceiveAddress, sysOrderReceiveAddressXS);
        sysOrderXS.setSysOrderReceiveAddress(sysOrderReceiveAddressXS);

        return sysOrderXS;
    }


    private void getOrderDetail(SysOrderXS sysOrder) {

        List<SysOrderDetail> sysOrderDetails = sysOrderDetailMapper.selectOrderDetailBySysOrderId(sysOrder.getSysOrderId());
        final List<OrderDetailXS> orderDetailsXS = BeanConvertor.copyList(sysOrderDetails, OrderDetailXS.class);
        sysOrder.setSysOrderDetails(orderDetailsXS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @TxTransaction(isStart = true)
    public String cancelDistributionOrder(HttpServletRequest request, Integer platformShopId, String sysOrderId, String sourceOrderId, String cancelReason) throws Exception {
        SysOrderNew orderNew = queryOrderByOther(sysOrderId, platformShopId);
        Byte orderDeliveryStatus = orderNew.getOrderDeliveryStatus();
        if (orderDeliveryStatus == OrderDeliveryStatusNewEnum.WAIT_PAY.getValue() | orderDeliveryStatus == OrderDeliveryStatusNewEnum.INTERCEPTED.getValue()) {
            judgeOrderStatusIsHandleAndLock(orderNew);//判断包裹状态是否符合要求->上锁
            distributionOrderService.updateOrderCancelStatus(sysOrderId, orderNew);//更改作废状态
        } else if (orderDeliveryStatus == OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue()) {
            try {
                //拦截（拦截后退钱）+改为已作废状态  TODO  拦截ERP  还有称重的  要抛异常  捕获异常(826版本需求)
                judgePackageStatusAndLock(orderNew);    //判断包裹状态是否符合要求->上锁
                //TODO 普通订单或者合并包裹的订单（只有一个包裹）
                if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue()) |
                        orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
                    String deliveryWarehouseId = String.valueOf(orderNew.getSysOrderPackageList().get(0).getDeliveryWarehouseId());
                    String orderTrackId = orderNew.getSysOrderPackageList().get(0).getOrderTrackId();
                    String warehouseId = String.valueOf(orderNew.getSysOrderPackageList().get(0).getDeliveryWarehouseId());
                    String warehouse = sysOrderService.judgeWarehouseByWarehouseId(warehouseId);
                    if (Objects.equals(warehouse, Constants.Warehouse.GOODCANG)) {
                        String msg = sysOrderService.cancelGoodCangOrder(sysOrderId, orderNew, deliveryWarehouseId, orderTrackId);//拦截谷仓订单
                        distributionOrderService.unfreezeAndUpdateOrderStatus(sysOrderId, orderNew, msg); //解冻并且更改成作废状态
                    } else if (Objects.equals(warehouse, Constants.Warehouse.WMS)) {
                        String msg = sysOrderService.cancelWmsOrder(sysOrderId, orderNew, orderTrackId, warehouseId);//拦截WMS订单
                        distributionOrderService.unfreezeAndUpdateOrderStatus(sysOrderId, orderNew, msg);//解冻并且更改成作废状态`
                    } else if (Objects.equals(warehouse, Constants.Warehouse.RONDAFUL)) {
                        String msg = sysOrderService.cancelErpOrder(sysOrderId, orderNew, orderTrackId);//拦截ERP订单
                        distributionOrderService.unfreezeAndUpdateOrderStatus(sysOrderId, orderNew, msg);//解冻并且更改成作废状态
                    } else {
                        throw new GlobalException(OrderCodeEnum.RETURN_CODE_300134);
                    }
                }
                // TODO 拆分包裹的订单（多个包裹）
                else if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                    boolean flag = true; // 订单包裹拦截标记，默认全部拦截成功
                    flag = dealSplitPackage(sysOrderId, orderNew, flag);
                    if (flag) {
                        String msg = sysOrderService.returnInterceptSuccessful(sysOrderId, orderNew);// 订单全部包裹拦截成功
                        distributionOrderService.unfreezeAndUpdateOrderStatus(sysOrderId, orderNew, msg);//解冻并且更改成作废状态
                    } else {
                        String msg = sysOrderService.returnSplitPackageInterceptFailure(sysOrderId, orderNew);// 订单包裹部分拦截失败或全部拦截失败
                        if (msg.contains(Constants.Intercept.INTERCEPT_PART_FAIL)) {
                            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300138);
                        } else {
                            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300135);
                        }
                    }
                } else {
                    throw new GlobalException(OrderCodeEnum.RETURN_CODE_300130);
                }
            } catch (GlobalException e) {
                if (e.toString().contains("包裹拦截成功，订单取消冻结失败")) {
                    return Constants.InterceptResponse.RESPONSE_1;
                } else {
                    throw e;
                }
            }
        } else {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300129);
        }
        redissLockUtil.unlock(sysOrderId);
        return "取消成功";
    }


    @Override
    public void unfreezeAndUpdateOrderStatus(String sysOrderId, SysOrderNew orderNew, String msg) {
        distributionOrderService.unfreezeMoney(sysOrderId, orderNew, msg);//解冻
        distributionOrderService.updateOrderCancelStatus(sysOrderId, orderNew);//更改作废状态
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderCancelStatus(String sysOrderId, SysOrderNew orderNew) {
        if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {  //TODO 合包订单
            List<String> list = Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"));
            String orderTrackId = orderNew.getSysOrderPackageList().get(0).getOrderTrackId();
            sysOrderPackageMapper.updatePackageStatusByOrderTrackId(orderTrackId, OrderPackageStatusEnum.WAIT_PUSH.getValue());   //更改合并后的订单包裹状态
            for (String orderId : list) { //更改合并前的订单状态和包裹状态
                sysOrderNewMapper.updateOrdersStatus(orderId, OrderDeliveryStatusNewEnum.CANCELLED.getValue());
                sysOrderPackageMapper.updatePackageStatus(orderId, OrderPackageStatusEnum.WAIT_PUSH.getValue());
                sysOrderServiceImpl.sendMsgAndAddLogByOther(orderNew, orderId);
            }
        } else if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())
                || orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {   //TODO 普通订单或者拆包订单
            sysOrderNewMapper.updateOrdersStatus(sysOrderId, OrderDeliveryStatusNewEnum.CANCELLED.getValue());
            sysOrderPackageMapper.updatePackageStatus(sysOrderId, OrderPackageStatusEnum.WAIT_PUSH.getValue());
            sysOrderServiceImpl.sendMsgAndAddLogByOther(orderNew, orderNew.getSysOrderId());
        } else {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300130);
        }
    }

    @Override
    public void unfreezeMoney(String sysOrderId, SysOrderNew orderNew, String msg) {
        if (msg.contains(Constants.Intercept.INTERCEPT_SUCCESS)) {
            if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
                for (String orderId : Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"))) {
                    distributionOrderService.cancelMoney(orderId);
                }
            } else {
                distributionOrderService.cancelMoney(sysOrderId);
            }
        }
    }

    @Override
    public void cancelMoney(String orderId) {
        try {
            sysOrderService.cancelMoney(orderId);
        } catch (Exception e) {
            updateErrorOrderInfo(orderId);//拦截成功，解冻失败，将订单置为异常订单，添加包裹异常信息。
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "包裹拦截成功，订单取消冻结失败");
        }
    }

    public void updateErrorOrderInfo(String orderId) {
        SysOrderNew orderNew = sysOrderService.queryOrderByOther(orderId);
        if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())
                || orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {
            sysOrderNewMapper.updateToExceptionOrder(orderId); //变成异常订单
            for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {
                //所有包裹添加异常信息：包裹拦截成功，订单取消冻结失败，请联系客服进行人工处理
                sysOrderPackageMapper.updatePackageErrorInfo(orderPackage.getOrderTrackId());
            }
        } else {   //合包的订单
            List<String> list = Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"));
            list.forEach(id -> sysOrderNewMapper.updateToExceptionOrder(id));
            String orderTrackId = orderNew.getSysOrderPackageList().get(0).getOrderTrackId();
            //所有包裹添加异常信息：包裹拦截成功，订单取消冻结失败，请联系客服进行人工处理
            sysOrderPackageMapper.updatePackageErrorInfo(orderTrackId);
        }
    }

    public boolean dealSplitPackage(String sysOrderId, SysOrderNew orderNew, boolean flag) {
        try {
            flag = sysOrderService.dealSplitPackage(sysOrderId, orderNew, flag);
        } catch (Exception e) {
            if (e.toString().contains("未知的仓库名")) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300134);
            }
        }
        return flag;
    }

    public void judgePackageStatusAndLock(SysOrderNew orderNew) {
        try {
            sysOrderServiceImpl.judgePackageStatusAndLock(orderNew);
        } catch (Exception e) {
            if (e.toString().contains("包裹状态非推送失败或待推送")) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300133);
            } else {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300132);
            }
        }
    }

    public void judgeOrderStatusIsHandleAndLock(SysOrderNew orderNew) {
        try {
            sysOrderService.judgeOrderStatusIsHandleAndLock(orderNew);
        } catch (Exception e) {
            if (e.toString().contains("包裹状态有误")) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300131);
            } else {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300132);
            }
        }
    }

    public SysOrderNew queryOrderByOther(String sysOrderId, Integer platformShopId) {
        SysOrderNew orderNew;
        try {
            orderNew = sysOrderService.queryOrderByOther(sysOrderId);
        } catch (Exception e) {
            if (e.toString().contains("请求参数有误")) {
                logger.error("查询第三方导入订单异常：{}", e.toString());
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300122);
            } else {
                logger.error("查询第三方导入订单异常：{}", e.toString());
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300123);
            }
        }
        if (orderNew.getOrderSource() != OrderSourceEnum.THIRD_API_PUSH.getValue() || !platformShopId.equals(orderNew.getPlatformShopId())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300128);
        }
        return orderNew;
    }

    private void fillDeliverGoodMustData(SysOrder sysOrder, Empower empower) {
        Date now = new Date();
        sysOrder.setOrderTime(TimeUtil.DateToString2(now));
//        sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());

        // 设置卖家账号信息
        sysOrder.setPlatformShopId(empower.getEmpowerid());
        sysOrder.setPlatformSellerId(String.valueOf(empower.getThirdpartyname()));
        sysOrder.setPlatformSellerAccount(empower.getAccount());
        sysOrder.setSellerPlId(empower.getPinlianid());
        sysOrder.setSellerPlAccount(empower.getPinlianaccount());

        String account = empower.getAccount();
        Integer rentstatus = empower.getRentstatus();
        sysOrder.setShopType(rentstatus == 0 ? "PERSONAL" : "RENT");
        sysOrder.setCreateBy(account);
        sysOrder.setUpdateBy(account);
        //TODO sku映射
        mappingOtherPlatformSku2PlSku(sysOrder);
        List<SysOrderDetail> detailList = sysOrder.getSysOrderDetails();
        ArrayList<String> skus = new ArrayList<>();
        for (SysOrderDetail detail : detailList) {
            detail.setSourceOrderId(sysOrder.getSourceOrderId());
            detail.setSysOrderId(sysOrder.getSysOrderId());

            //此处为何？
            detail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
            detail.setCreateBy(account);
            detail.setUpdateBy(account);
            skus.add(detail.getSku());
        }
        sysOrder.setSkus(skus);

        List<Integer> sellerIds = new ArrayList<>();
        sellerIds.add(sysOrder.getSellerPlId());
        List<SupplyChainCompany> supplyChainCompanies = systemOrderCommonService.getSupplyChainByUserId(
                String.valueOf(Constants.System.PLATFORM_TYPE_SELLER), sellerIds);

        if (CollectionUtils.isNotEmpty(supplyChainCompanies)) {
            SupplyChainCompany supplyChainCompany = supplyChainCompanies.get(0);
            sysOrder.setSupplyChainCompanyId(Integer.valueOf(supplyChainCompany.getSupplyId()));
            sysOrder.setSupplyChainCompanyName(supplyChainCompany.getSupplyChainCompanyName());
        }

        //最晚发货时间默认2天
        sysOrder.setDeliverDeadline(TimeUtil.DateToString2(TimeUtil.dateAddSubtract(now, 60 * 24 * 2)));
        sysOrder.setOrderDeliveryStatus(OrderDeliveryStatusNewEnum.WAIT_PAY.getValue());
    }

    /**
     * 将渠道订单中品连SKU映射品连信息
     *
     * @param sysOrder
     * @return
     */
    private void commodityMap(SysOrder sysOrder) {

        List<String> systemSkuList = sysOrder.getSysOrderDetails().stream().map(x -> x.getSku()).collect(Collectors.toList());
        int distintSkuSize = systemSkuList.stream().collect(Collectors.groupingBy(String::toString)).size();
        Integer sellerPlId = sysOrder.getSellerPlId();
        List<CommodityBase> commodityBaseList = systemOrderCommonService.getCommodityBySkuList(sellerPlId, systemSkuList);
        int count = 0;
        for (CommodityBase commodityBase : commodityBaseList) {
            count += commodityBase.getCommoditySpecList().size();
        }
        if (distintSkuSize != count) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300119);
        }
        //不是品连的SKU商品报错
        this.setOrderBaseData(sysOrder, commodityBaseList);
    }

    private void mappingOtherPlatformSku2PlSku(SysOrder sysOrder) {

        List<SysOrderDetail> sysOrderDetails = sysOrder.getSysOrderDetails();
        //标志位
//        boolean falg = false;
        //其他平台的sku映射成PL的sku
        for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
            sysOrderDetail.setSourceSku(sysOrderDetail.getSku());//保存来源SKU
            List<QuerySkuMapForOrderVo> list =new ArrayList<>();
            QuerySkuMapForOrderVo vo =new QuerySkuMapForOrderVo();
            vo.setAuthorizationId(String.valueOf(sysOrder.getPlatformShopId()));
            vo.setPlatform("other");
            vo.setPlatformSku(sysOrderDetail.getSku());
            vo.setSellerId(String.valueOf(sysOrder.getSellerPlId()));
            list.add(vo);
            String result = remoteCommodityService.getSkuMapForOrder(list);
            String data = Utils.returnRemoteResultDataString(result, "商品服务异常");
            List<QuerySkuMapForOrderVo> mapForOrderVoList = JSONObject.parseArray(data, QuerySkuMapForOrderVo.class);
            if (CollectionUtils.isNotEmpty(mapForOrderVoList)) {
                for (QuerySkuMapForOrderVo querySkuMapForOrderVo : mapForOrderVoList) {
                    List<SkuMapBind> skuBinds = querySkuMapForOrderVo.getSkuBinds();
                    if (CollectionUtils.isNotEmpty(skuBinds)) {
                        sysOrderDetail.setSku(skuBinds.get(0).getSystemSku());
                        BigDecimal skuNum = new BigDecimal(skuBinds.get(0).getSkuNum()).multiply(new BigDecimal(sysOrderDetail.getSkuQuantity()));
                        sysOrderDetail.setSkuQuantity(Integer.valueOf(String.valueOf(skuNum)));
//                        falg = true;
                    }
                }
            }
           /* sysOrderDetail.setSourceSku(sysOrderDetail.getSku());
            SellerSkuMap plSku = sellerSkuMapMapper.getSellerSkuMapByOtherPlatformSku("other", sysOrderDetail.getSku(), 1, sysOrder.getPlatformShopId());
            if (plSku != null && StringUtils.isNotBlank(plSku.getPlSku())) {
                sysOrderDetail.setSku(plSku.getPlSku());
                falg = true;
            }*/
        }
     /*   if (falg) {
            HashMap<String, Integer> newMapSkuAndCount = new HashMap<>();
            //合并可能相同的sku
            sysOrderDetails.forEach(sysOrderDetail -> {

                boolean containsSku = newMapSkuAndCount.containsKey(sysOrderDetail.getSku());
                if (containsSku) {
                    int newCount = newMapSkuAndCount.get(sysOrderDetail.getSku()) + sysOrderDetail.getSkuQuantity();
                    newMapSkuAndCount.put(sysOrderDetail.getSku(), newCount);

                } else {
                    newMapSkuAndCount.put(sysOrderDetail.getSku(), sysOrderDetail.getSkuQuantity());
                }
            });
            //重新加入到订单详情中
            List<SysOrderDetail> newSysOrderDetails = new ArrayList<>();
            newMapSkuAndCount.forEach((key, value) -> {

                SysOrderDetail sysOrderDetail = new SysOrderDetail();
                sysOrderDetail.setSku(key);
                sysOrderDetail.setSkuQuantity(value);

                //将星商传过来的自己的sku保存
                for (SysOrderDetail orderDetail : sysOrderDetails) {
                    if (key.equals(orderDetail.getSku())) {
                        if (StringUtils.isNotBlank(orderDetail.getSourceSku())) {

                            if (StringUtils.isNotBlank(sysOrderDetail.getSourceSku())) {
                                String sourceSKU = sysOrderDetail.getSourceSku();
                                String newSourceSKU = sourceSKU + "#" + orderDetail.getSourceSku();
                                sysOrderDetail.setSourceSku(newSourceSKU);
                            } else {
                                sysOrderDetail.setSourceSku(orderDetail.getSourceSku());
                            }
                        }

                    }
                }

                newSysOrderDetails.add(sysOrderDetail);

            });

            sysOrder.setSysOrderDetails(newSysOrderDetails);
        }*/
    }

    /**
     * 设置订单列表的基础参数
     *
     * @param order             订单
     * @param commodityBaseList 商品列表
     */
    private void setOrderBaseData(SysOrder order, List<CommodityBase> commodityBaseList) {
        BigDecimal orderAmountIn = order.getOrderAmount();
        order.setOrderSource(OrderSourceEnum.THIRD_API_PUSH.getValue());//星商订单
        order.setOrderAmount(BigDecimal.valueOf(0));//系统订单总价
        order.setItemNum(0L);
        order.setTotalBulk(BigDecimal.valueOf(0));
        order.setTotalWeight(BigDecimal.valueOf(0));
        List<SysOrderDetail> sysOrderDetailList = order.getSysOrderDetails();
        for (SysOrderDetail detail : sysOrderDetailList) {
            this.setDetail(detail, commodityBaseList);  //TODO sku存在判断
            detail.setSysOrderId(order.getSysOrderId());
            resetSkuPriceByWarehouseId(order, detail);//TODO 分仓定价：如果有仓库ID重新设置商品价格
            if (StringUtils.isNotBlank(detail.getSku())) {
                order.setItemNum(order.getItemNum() + detail.getSkuQuantity());
                order.setTotalBulk(order.getTotalBulk().add(detail.getBulk().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));
                order.setTotalWeight(order.getTotalWeight().add(detail.getWeight().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));   //todo 重量这块还有单位处理
                order.setOrderAmount(order.getOrderAmount().add(this.totalBigDecimal(detail.getSkuQuantity(), detail.getItemPrice())));
            }
        }
        // 设置整个订单的包邮状态
        order.setFreeFreightType(SysOrderServiceImpl.getSysOrderFreeFreightType(sysOrderDetailList));

        BigDecimal orderAmount = order.getOrderAmount();
        LOGGER.info("__________分销商传进来的系统商品总价为 {}___________进行SKU匹配后计算出的系统商品总价(商品单价X数量)为 {}___________", orderAmountIn, orderAmount);
    }

    private void resetSkuPriceByWarehouseId(SysOrder order, SysOrderDetail detail) {
        String result = remoteCommodityService.test("1", "1", null, null, null, null,
                detail.getSku(), null, null);
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
                    if (String.valueOf(skuInventoryVo.getWarehouseId()).equals(order.getDeliveryWarehouseId())) {
                        count++;
                        //品连单个商品成本价
                        detail.setItemCost(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                        //商品系统单价
                        detail.setItemPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                        //供应商sku单价
                        detail.setSupplierSkuPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                    }
                }
                if (count == 0) {
                    //品连单个商品成本价
                    detail.setItemCost(commodityDetail.getCommodityPriceUs());
                    //商品系统单价
                    detail.setItemPrice(commodityDetail.getCommodityPriceUs());
                    //供应商sku单价
                    detail.setSupplierSkuPrice(commodityDetail.getCommodityPriceUs());
                }
            } else {
                //品连单个商品成本价
                detail.setItemCost(commodityDetail.getCommodityPriceUs());
                //商品系统单价
                detail.setItemPrice(commodityDetail.getCommodityPriceUs());
                //供应商sku单价
                detail.setSupplierSkuPrice(commodityDetail.getCommodityPriceUs());
            }
        }
    }

    /**
     * 设置订单项参数
     *
     * @param detail            订单项对象
     * @param commodityBaseList 商品列表
     */
    private void setDetail(SysOrderDetail detail, List<CommodityBase> commodityBaseList) {

        CheckOrderUtils.judgeSkuState(commodityBaseList);

        if (StringUtils.isNotBlank(detail.getSku())) {
            for (CommodityBase commodityBase : commodityBaseList) {
                List<CommoditySpec> commoditySpecList = commodityBase.getCommoditySpecList();
                if (!CollectionUtils.isEmpty(commoditySpecList)) {
                    for (CommoditySpec commoditySpec : commodityBase.getCommoditySpecList()) {
                        if (commoditySpec.getSystemSku().equalsIgnoreCase(detail.getSku())) {
                            detail.setItemId(commoditySpec.getId());
                            detail.setItemPrice(commoditySpec.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityPriceUs());
                            if (commoditySpec.getPackingHeight() != null && commoditySpec.getPackingLength() != null && commoditySpec.getPackingWidth() != null) {
                                detail.setBulk(commoditySpec.getPackingHeight().multiply(commoditySpec.getPackingLength()).multiply(commoditySpec.getPackingWidth()));
                            } else {
                                detail.setBulk(BigDecimal.valueOf(0));
                            }
                            detail.setWeight(commoditySpec.getCommodityWeight() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityWeight());
                            detail.setItemCost(commoditySpec.getCommodityPriceUs() == null ? BigDecimal.valueOf(0) : commoditySpec.getCommodityPriceUs());
                            detail.setItemAttr(commoditySpec.getCommoditySpec());
                            //新加属性
                            detail.setItemUrl(commoditySpec.getMasterPicture().split("\\|")[0]);//若主图有多个URL，只取第一个
                            detail.setItemName(commoditySpec.getCommodityNameCn());
                            detail.setItemNameEn(commoditySpec.getCommodityNameEn());
                            detail.setSkuTitle(commoditySpec.getCommodityNameCn());
                            detail.setSupplierSkuTitle(commoditySpec.getCommodityNameCn());
                            detail.setSupplierId(commodityBase.getSupplierId());
                            detail.setSupplierName(commoditySpec.getSupplierName());
                            detail.setSupplyChainCompanyId(commoditySpec.getSupChainCompanyId());
                            detail.setSupplyChainCompanyName(commoditySpec.getSupChainCompanyName());
                            detail.setFareTypeAmount(commoditySpec.getFeePriceUs() == null ? (commoditySpec.getFeeRate() == null ?
                                    null : "2#" + commoditySpec.getFeeRate()) : "1#" + commoditySpec.getFeePriceUs());
                            detail.setSupplierSku(commoditySpec.getSupplierSku());
                            detail.setSupplierSkuPrice(commoditySpec.getCommodityPriceUs());

                            Integer freeFreight = commoditySpec.getFreeFreight();
                            detail.setFreeFreight(freeFreight);
                        }
                    }
                }
            }
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

    /**
     * 校验第三方账号是否有权请求
     *
     * @param platformShopId 店铺账号
     * @return {@link Empower}
     */
    private Empower validateThirdAccount(Integer platformShopId) {

        //查询相关授权
        Empower empower = null;
        try {
            empower = systemOrderCommonService.queryAuthorizationByShopID(platformShopId);
        } catch (Exception e) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300117);
        }
        return empower;
    }

    @Override
    public List<SysOrderPackageXS> queryDistributionSysOrderPackageByID(HttpServletRequest request, Integer platformShopId,
                                                                        String sysOrderId, String sourceOrderId) {
        if (StringUtils.isBlank(sourceOrderId)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300100);
        }
        if (StringUtils.isBlank(sysOrderId)) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300122);
        }
        //使用订单来源ID和品连订单ID去查询
        SysOrderNew sysOrder = sysOrderNewMapper.selectSysOrderBySysOrderIdAndSourceOrderId(sysOrderId, sourceOrderId);
        if (sysOrder == null) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300123);
        }
        String distribution = (String) request.getAttribute("distribution");

        if (Constants.DistributionType.DistributionType_QT.equals(distribution)) {
            if (sysOrder.getOrderSource() != OrderSourceEnum.THIRD_API_PUSH.getValue()) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300124);
            }
        }
        if (!platformShopId.equals(sysOrder.getPlatformShopId())) {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300124);
        }

        List<SysOrderPackage> sysOrderPackageList = sysOrderPackageMapper.queryOrderPackage(sysOrderId);
        List<SysOrderPackageXS> sysOrderPackageXSList = new ArrayList<>();
        for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {
            List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackageDetailMapper
                    .queryOrderPackageDetails(sysOrderPackage.getOrderTrackId());
            sysOrderPackage.setSysOrderPackageDetailList(sysOrderPackageDetailList);

            SysOrderPackageXS sysOrderPackageXS = new SysOrderPackageXS();
            BeanUtils.copyProperties(sysOrderPackage, sysOrderPackageXS);
            List<SysOrderPackageDetailXS> sysOrderPackageDetailXSList = new ArrayList<>();
            for (SysOrderPackageDetail orderPackageDetail : sysOrderPackageDetailList) {
                SysOrderPackageDetailXS sysOrderPackageDetailXS = new SysOrderPackageDetailXS();
                BeanUtils.copyProperties(orderPackageDetail, sysOrderPackageDetailXS);
                sysOrderPackageDetailXSList.add(sysOrderPackageDetailXS);
            }
            sysOrderPackageXS.setSysOrderPackageDetailList(sysOrderPackageDetailXSList);
            sysOrderPackageXSList.add(sysOrderPackageXS);
        }
        return sysOrderPackageXSList;
    }
}
