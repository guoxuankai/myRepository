package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.tx.annotation.TxTransaction;
import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.CompleteSaleCall;
import com.ebay.soap.eBLBaseComponents.*;
import com.google.gson.JsonObject;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.enums.*;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.model.vo.freight.*;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.common.ExportUtil;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.Amazon.AmazonDelivery;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.entity.*;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrder;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.order.entity.aliexpress.ShipmentDTO;
import com.rondaful.cloud.order.entity.aliexpress.TradeListDTO;
import com.rondaful.cloud.order.entity.commodity.CodeAndValueVo;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.entity.erpentity.ERPOrder;
import com.rondaful.cloud.order.entity.erpentity.ERPOrderDetail;
import com.rondaful.cloud.order.entity.erpentity.ERPShipping;
import com.rondaful.cloud.order.entity.erpentity.WareHouseDeliverCallBack;
import com.rondaful.cloud.order.entity.finance.OrderItemVo;
import com.rondaful.cloud.order.entity.finance.OrderRequestVo;
import com.rondaful.cloud.order.entity.goodcang.GoodCangOrder;
import com.rondaful.cloud.order.entity.goodcang.GoodCangOrderItem;
import com.rondaful.cloud.order.entity.supplier.DeliveryDetail;
import com.rondaful.cloud.order.entity.supplier.DeliveryRecord;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.system.*;
import com.rondaful.cloud.order.enums.*;
import com.rondaful.cloud.order.mapper.*;
import com.rondaful.cloud.order.mapper.aliexpress.AliexpressOrderChildMapper;
import com.rondaful.cloud.order.mapper.aliexpress.AliexpressOrderMapper;
import com.rondaful.cloud.order.model.dto.remoteErp.GetOrderSpeedInfoVO;
import com.rondaful.cloud.order.model.dto.syncorder.SplitPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.dto.sysorder.DeliveryPackageDTO;
import com.rondaful.cloud.order.model.dto.wms.WmsOrderDTO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsResultVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsSkuVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsSupplierVO;
import com.rondaful.cloud.order.model.vo.sysorder.EmpowerRequestVo;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.*;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.service.*;
import com.rondaful.cloud.order.service.aliexpress.IAliexpressOrderService;
import com.rondaful.cloud.order.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

@Service
public class SystemOrderServiceImpl implements ISystemOrderService {
    @Autowired
    private ISysOrderService sysOrderService;
    @Autowired
    private ISystemOrderService systemOrderService;
    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;
    @Autowired
    private SysOrderMapper sysOrderMapper;
    @Autowired
    private SysOrderDetailMapper sysOrderDetailMapper;
    @Autowired
    private ISysOrderLogService sysOrderLogService;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    @Autowired
    private RemoteFinanceService remoteFinanceService;
    @Autowired
    private RemoteErpService remoteErpService;
    @Autowired
    private OrderMessageSender orderMessageSender;
    @Autowired
    private RemoteCmsService remoteCmsService;
    @Autowired
    private RemoteSellerService remoteSellerService;
    @Autowired
    private ISyncEbayOrderService syncEbayOrderService;
    @Autowired
    private EbayOrderDetailMapper ebayOrderDetailMapper;
    @Autowired
    private AmazonOrderDetailMapper amazonOrderDetailMapper;
    @Autowired
    private AmazonDeliveryMapper amazonDeliveryMapper;
    @Autowired
    private GetLoginUserInformationByToken loginUserInfo;
    @Autowired
    private IAliexpressOrderService aliexpressOrderService;
    @Autowired
    private AliexpressOrderMapper aliexpressOrderMapper;
    @Autowired
    private AliexpressOrderChildMapper aliexpressOrderChildMapper;
    @Autowired
    private RemoteLogisticsService remoteLogisticsService;
    @Autowired
    private RemoteAliexpressService remoteAliexpressService;
    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private IGoodCangService goodCangService;
    @Autowired
    private EbayOrderMapper ebayOrderMapper;
    @Autowired
    private AmazonOrderMapper amazonOrderMapper;
    @Autowired
    private RateUtil rateUtil;
    @Autowired
    private RedissLockUtil redissLockUtil;
    @Autowired
    private Environment env;

    @Autowired
    private IGoodCangService iGoodCangService;

    @Autowired
    private ISystemOrderService iSystemOrderService;

    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;

    @Autowired
    private SysOrderPackageDetailMapper sysOrderPackageDetailMapper;

    @Autowired
    private SysOrderNewMapper sysOrderNewMapper;

    @Autowired
    private SysOrderReceiveAddressMapper sysOrderReceiveAddressMapper;

    @Autowired
    private ISkuSalesRecordService skuSalesRecordService;

    @Autowired
    private IWmsService wmsService;

    @Autowired
    private IDeliverCallBackService deliverCallBackService;

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private ISysOrderExceptionHandelService sysOrderExceptionHandelService;

    @Value("${wsdl.url}")
    private String goodCangUrl;

    private static String rateCurrency = PropertyUtil.getProperty("rateCurrency");

    private static String eBayServerUrl = PropertyUtil.getProperty("eBayServerUrl");

    public static String appid = "Qingdaow-rondaful-PRD-760c321e5-1c3e761b";
    public static String developer = "41de149d-3a20-4b8f-8f16-941f7a14ceac";
    public static String cert = "PRD-60c321e5bc1d-daed-487d-9e96-f903";
    public static String ruName = "Qingdaowangushi-Qingdaow-rondaf-eunonvhdg";

    private final static Logger _log = LoggerFactory.getLogger(SystemOrderServiceImpl.class);

    @Override
    @Transactional
    public void saveSplittedSysOrder(List<SysOrder> sysOrders) throws Exception {
        String orderId = sysOrders.stream().map(x -> x.getSysOrderId()).distinct().collect(Collectors.toList()).get(0);
        RLock lock = redissLockUtil.lock(orderId, 20);
        this.validateMustData(sysOrders);
        SysOrder oldSysOrder = this.judgeIsCanBeSplitted(sysOrders);
        if ((byte) 4 == oldSysOrder.getOrderSource()) {
            this.setEBaySYSOrderDeadline(oldSysOrder);
        }
        StringBuilder childIds = new StringBuilder();
        String fatherSysOrderId = oldSysOrder.getSysOrderId();
        _log.info("______________申请拆分的订单号为：{}______________", fatherSysOrderId);
        String[] childIdArray = new String[sysOrders.size()];//插入操作日志用
        int i = 0;//用于记录拆分出的子订单个数
        String username = loginUserInfo.getUserInfo().getUser().getUsername();
        for (SysOrder sysOrder : sysOrders) {
            sysOrder = this.mapSysOrder(sysOrder, oldSysOrder);//sysOrder为接收前端拆分的订单，oldSysOrder为被拆分的原始订单
            sysOrder = this.setBaseData(sysOrder, username);
            sysOrder.setMainOrderId(fatherSysOrderId);
            String sysOrderId = OrderUtils.getPLOrderNumber();
            childIdArray[i++] = sysOrderId;
            sysOrder.setId(null);
            sysOrder.setSysOrderId(sysOrderId);
            sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());
            sysOrder.setRecordNumber(oldSysOrder.getRecordNumber());
            sysOrder.setSplittedOrMerged(OrderHandleEnum.SplittedOrMerged.SPLITTED.getValue());
            sysOrder.setCreateDate(null);
            sysOrder.setCreateBy(username);
            sysOrder.setUpdateDate(null);
            sysOrder.setUpdateBy(username);
            systemOrderCommonService.setGrossMarginAndProfitMargin(sysOrder);
            sysOrderMapper.insertSelective(sysOrder);
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrder.getSysOrderId(), OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrder.getSysOrderId()),
                    OrderHandleLogEnum.OrderStatus.STATUS_10.getMsg(), sysOrder.getSellerPlAccount()));
            orderMessageSender.sendOrderStockOut(sysOrder.getSellerPlAccount(), sysOrder.getSysOrderId(), MessageEnum.ORDER_NEW_NOTICE, null);
            for (SysOrderDetail x : sysOrder.getSysOrderDetails()) {
                x.setSysOrderId(sysOrderId);
                x.setId(null);
                x.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
                String sourceOrderId = x.getSourceOrderId();
                if (StringUtils.isNotBlank(sourceOrderId))
                    x.setSourceOrderId(sourceOrderId);
                String sourceOrderLineItemId = x.getSourceOrderLineItemId();
                if (StringUtils.isNotBlank(sourceOrderLineItemId))
                    x.setSourceOrderLineItemId(sourceOrderLineItemId);
                x.setCreateDate(null);
                x.setCreateBy(username);
                x.setUpdateDate(null);
                x.setUpdateBy(username);
                sysOrderDetailMapper.insertSelective(x);
            }
            childIds.append(sysOrderId).append("#");
            if (childIds.length() > 500) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并订单数超过限制。。。");
            }
        }
        sysOrderLogService.insertSelective(new SysOrderLog(oldSysOrder.getSysOrderId(), OrderHandleLogEnum.Content.SPLIT_1.split_1(oldSysOrder.getSysOrderId()),
                OrderHandleLogEnum.OrderStatus.STATUS_10.getMsg(), oldSysOrder.getSellerPlAccount()));
        //更新父系统订单的状态
        SysOrder vo = new SysOrder();
        vo.setSysOrderId(fatherSysOrderId);
        vo.setOrderDeliveryStatus((byte) 0);
        vo.setSplittedOrMerged(OrderHandleEnum.SplittedOrMerged.SPLITTED.getValue());
        vo.setChildIds(childIds.toString());
        vo.setUpdateDate(null);
        vo.setUpdateBy(username);
        sysOrderMapper.updateBySysOrderIdSelective(vo);
        _log.info("________________拆分订单操作后保存子订单集合操作成功___________________");
        redissLockUtil.unlock(lock);
    }

    /**
     * 拆分包裹
     *
     * @param splitPackageDTO
     * @throws Exception
     */
    @Override
    public void saveSplittedSysPackage(SplitPackageDTO splitPackageDTO) throws Exception {
        _log.info("________________拆分订单参数splitPackageDTO：{}___________________" + FastJsonUtils.toJsonString(splitPackageDTO));
        //验证数据
        this.validateSplittedSysPackageData(splitPackageDTO);
        //组装包裹数据
        String sysOrderId = splitPackageDTO.getSysOrderId();
        List<SysOrderPackage> sysOrderPackages = sysOrderPackageMapper.selectPackageByOderId(sysOrderId);
        SysOrderPackage oldSysOrderPackage = sysOrderPackages.get(0);
        String orderTrackId = oldSysOrderPackage.getOrderTrackId();

        List<SysOrderPackageDTO> sysOrderPackageDTOList = splitPackageDTO.getSysOrderPackageDTOList();
        sysOrderPackageDTOList.forEach(sysOrderPackageDTO -> {
            SysOrderPackage sysOrderPackage = new SysOrderPackage();
            String trackId = OrderUtils.getPLTrackNumber();
            BeanUtils.copyProperties(sysOrderPackageDTO, sysOrderPackage);
            sysOrderPackage.setSysOrderId(sysOrderId);
            sysOrderPackage.setOrderTrackId(trackId);
            sysOrderPackage.setOperateStatus(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue());
            sysOrderPackage.setOperateOrderTrackId(orderTrackId);

            _log.info("________________sysOrderPackage：{}___________________" + FastJsonUtils.toJsonString(sysOrderPackage));
            sysOrderPackageMapper.insertSelective(sysOrderPackage);

            //组装包裹明细数据
            List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOs = sysOrderPackageDTO.getSysOrderPackageDetailList();
            sysOrderPackageDetailDTOs.forEach(sysOrderPackageDetailDTO -> {
                SysOrderPackageDetail sysOrderPackageDetail = new SysOrderPackageDetail();
                BeanUtils.copyProperties(sysOrderPackageDetailDTO, sysOrderPackageDetail);
                sysOrderPackageDetail.setOrderTrackId(trackId);

                _log.info("________________sysOrderPackageDetail：{}___________________" + FastJsonUtils.toJsonString(sysOrderPackageDetail));
                sysOrderPackageDetailMapper.insertSelective(sysOrderPackageDetail);
            });
        });

        //修改原来包裹状态为不显示
        SysOrderPackage sysOrderPackage = new SysOrderPackage();
        sysOrderPackage.setId(oldSysOrderPackage.getId());
        sysOrderPackage.setIsShow(OrderPackageHandleEnum.IsShowEnum.NO_SHOW.getValue());
        sysOrderPackageMapper.updateByPrimaryKeySelective(sysOrderPackage);

        //修改订单状态
        SysOrderNew sysOrderNew = new SysOrderNew();
        sysOrderNew.setSysOrderId(sysOrderId);
        sysOrderNew.setSplittedOrMerged(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue());
        sysOrderNewMapper.updateByPrimaryKeySelective(sysOrderNew);
        _log.info("________________拆分包裹 {} 操作成功________________", sysOrderId);

    }

    /**
     * 撤销拆分包裹
     *
     * @param sysOrderId
     */
    @Override
    public void cancelSplittedSysPackage(String sysOrderId) {
        RLock lock = redissLockUtil.lock(sysOrderId, 15);
        List<SysOrderPackage> sysOrderPackages = sysOrderPackageMapper.queryOrderPackageByOrderId(sysOrderId);

        if (CollectionUtils.isEmpty(sysOrderPackages) || sysOrderPackages.size() == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "根据订单号查询不到包裹。。。");
        }
        //一、判断是否符合撤销拆包条件
        SysOrderNew oldSysOrder = sysOrderNewMapper.queryOrderByOrderId(sysOrderId);
        Byte orderDeliveryStatus = oldSysOrder.getOrderDeliveryStatus();
        if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.INTERCEPTED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.DELIVERED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.RECEIVED.equals(orderDeliveryStatus)
                || OrderHandleEnum.OrderDeliveryStatus.CANCELLED.equals(orderDeliveryStatus)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "处于配货中/已拦截/已发货/已收货/已作废状态的订单不能撤销拆分。。。");
        }

        //三、修改旧包状态
        String operateOrderTrackId = sysOrderPackages.get(0).getOperateOrderTrackId();
        SysOrderPackage Package = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(operateOrderTrackId);
        SysOrderPackage sysOrderPackage = new SysOrderPackage();
        sysOrderPackage.setId(Package.getId());
        sysOrderPackage.setIsShow(OrderPackageHandleEnum.IsShowEnum.SHOW.getValue());
        sysOrderPackageMapper.updateByPrimaryKeySelective(sysOrderPackage);

        //二、删除拆分后的包裹、包裹详情
        List<String> collect = sysOrderPackages.stream().map(x -> x.getOrderTrackId()).collect(Collectors.toList());
        sysOrderPackageDetailMapper.deleteBatchBySysOrderTrackId(collect);
        sysOrderPackageMapper.deletePackageBySplitSysOrderId(sysOrderId);

        //修改订单状态
        SysOrderNew sysOrderNew = new SysOrderNew();
        sysOrderNew.setSysOrderId(sysOrderId);
        sysOrderNew.setSplittedOrMerged(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue());
        sysOrderNewMapper.updateByPrimaryKeySelective(sysOrderNew);

        _log.info("________________撤销已拆分系统订单 {} 操作成功________________", sysOrderId);
        redissLockUtil.unlock(lock);
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
                if (StringUtils.isBlank(detail.getOrderTrackId()) || StringUtils.isBlank(detail.getSku()) || detail.getSkuQuantity() <= 0) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统跟踪号,SKU和购买数量数量不能为空。。。");
                }
            }
        }

        SysOrderNew oldSysOrder = sysOrderNewMapper.queryOrderByOrderId(sysOrderId);

        if (oldSysOrder == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据父订单号查询出的系统订单实体类为空。。。");
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
            if (skuCount.get(0) < 2) {
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
            }
        }
        Boolean flag = this.compareMap(oldMap, splitMap);
        if (!flag) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分后SKU和对应SKU购买数量匹配不上。。。");
        }
    }

    private void setEBaySYSOrderDeadline(SysOrder order) {
        String deadline = "";
        for (SysOrderDetail detail : order.getSysOrderDetails()) {
            String deliverDeadline = detail.getDeliverDeadline();
            if (StringUtils.isNotBlank(deliverDeadline)) {
                if (StringUtils.isBlank(deadline)) {
                    deadline = deliverDeadline;
                    continue;
                }
                if (TimeUtil.stringToDate(deadline).compareTo(TimeUtil.stringToDate(deliverDeadline)) > 0)
                    deadline = deliverDeadline;
            }
        }
        if (StringUtils.isNotBlank(deadline))
            order.setDeliverDeadline(deadline);
    }

    private void validateMustData(List<SysOrder> sysOrders) {
        if (CollectionUtils.isEmpty(sysOrders) || sysOrders.size() == 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分的子订单数不符合条件。。。");
        }
        for (SysOrder newSysOrder : sysOrders) {
            if (StringUtils.isBlank(newSysOrder.getSysOrderId())
                    || StringUtils.isBlank(newSysOrder.getDeliveryWarehouseId())
//                    || StringUtils.isBlank(newSysOrder.getDeliveryWarehouseCode())
                    || StringUtils.isBlank(newSysOrder.getDeliveryWarehouse())
                    || StringUtils.isBlank(newSysOrder.getShippingCarrierUsedCode())
                    || StringUtils.isBlank(newSysOrder.getShippingCarrierUsed())
                    || StringUtils.isBlank(newSysOrder.getDeliveryMethodCode())
                    || StringUtils.isBlank(newSysOrder.getDeliveryMethod())) {
//                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统订单号,仓库CODE/仓库名称,物流商CODE/物流商名称,邮寄方式CODE/邮寄方式名称不能为空。。。");
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统订单号,仓库ID/仓库名称,物流商CODE/物流商名称,邮寄方式CODE/邮寄方式名称不能为空。。。");
            }
            for (SysOrderDetail detail : newSysOrder.getSysOrderDetails()) {
                if (StringUtils.isBlank(detail.getSysOrderId()) || StringUtils.isBlank(detail.getSku()) || detail.getSkuQuantity() <= 0) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "系统订单号,SKU和购买数量数量不能为空。。。");
                }
            }
        }
    }

    private SysOrder mapSysOrder(SysOrder newSysOrder, SysOrder oldSysOrder) {
        SysOrder copySysOrder;
        String[] ignoreProperties = {"deliveryWarehouseCode", "deliveryWarehouse", "shippingCarrierUsedCode", "shippingCarrierUsed"};
        copySysOrder = BeanConvertor.copy(oldSysOrder, SysOrder.class, ignoreProperties);
        String deadline = oldSysOrder.getDeliverDeadline();
        if (StringUtils.isNotBlank(deadline)) {
            copySysOrder.setDeliverDeadline(deadline);
        }
        copySysOrder.setDeliveryWarehouseId(newSysOrder.getDeliveryWarehouseId());  //TODO  增加仓库ID
        String result = remoteSupplierService.getWarehouseByIds(newSysOrder.getDeliveryWarehouseId());
        String data = Utils.returnRemoteResultDataString(result, "供应商服务异常");
        if (StringUtils.isBlank(data)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "供应商服务异常");
        }
        WarehouseDTO warehouseDTO = JSONObject.parseObject(data, WarehouseDTO.class);
        copySysOrder.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());
        copySysOrder.setDeliveryWarehouse(newSysOrder.getDeliveryWarehouse());
        copySysOrder.setShippingCarrierUsedCode(newSysOrder.getShippingCarrierUsedCode());
        copySysOrder.setShippingCarrierUsed(newSysOrder.getShippingCarrierUsed());
        copySysOrder.setDeliveryMethod(newSysOrder.getDeliveryMethod());
        copySysOrder.setDeliveryMethodCode(newSysOrder.getDeliveryMethodCode());
        copySysOrder.setAmazonCarrierName(newSysOrder.getAmazonCarrierName());//设置映射后的Amazon物流方式
        copySysOrder.setAmazonShippingMethod(newSysOrder.getAmazonShippingMethod());//设置映射后的Amazon物流方式
        copySysOrder.setEbayCarrierName(newSysOrder.getEbayCarrierName());//设置映射后的ebay物流方式
        Map<String, Integer> skuAndQuantityMap = newSysOrder.getSysOrderDetails().stream()
                .collect(Collectors.groupingBy(SysOrderDetail::getSku,
                        Collectors.summingInt(SysOrderDetail::getSkuQuantity)));
        Set<String> newSkuSet = skuAndQuantityMap.keySet();
        List<SysOrderDetail> detailList = new ArrayList<>();
        BigDecimal orderAmountTemp = new BigDecimal(0);
        for (SysOrderDetail detail : oldSysOrder.getSysOrderDetails()) {
            if (newSkuSet.contains(detail.getSku())) {
                detail.setSkuQuantity(skuAndQuantityMap.get(detail.getSku()));
                orderAmountTemp = orderAmountTemp.add(detail.getItemPrice().multiply(new BigDecimal(detail.getSkuQuantity())));
                detailList.add(detail);
            }
        }
        BigDecimal commoditiesAmount = oldSysOrder.getCommoditiesAmount();
        BigDecimal shippingServiceCost = oldSysOrder.getShippingServiceCost();
        BigDecimal orderAmount = oldSysOrder.getOrderAmount();
        BigDecimal apportion = orderAmountTemp.divide(orderAmount, 4, BigDecimal.ROUND_HALF_UP);
        if (commoditiesAmount != null) {
            copySysOrder.setCommoditiesAmount(commoditiesAmount.multiply(apportion));
        }
        if (shippingServiceCost != null) {
            copySysOrder.setShippingServiceCost(shippingServiceCost.multiply(apportion));
        }
        copySysOrder.setSysOrderDetails(detailList);
        return copySysOrder;
    }

    /**
     * 拆分合并订单设置基础数据
     *
     * @param newSysOrder
     * @return
     */
    private SysOrder setBaseData(SysOrder newSysOrder, String username) throws Exception {
        newSysOrder.setItemNum(0L);
        newSysOrder.setTotal(BigDecimal.valueOf(0));//订单总售价:预估物流费+商品金额(未加预估物流费)
        newSysOrder.setOrderAmount(BigDecimal.valueOf(0));//系统订单总价
        newSysOrder.setTotalBulk(BigDecimal.valueOf(0));
        newSysOrder.setTotalWeight(BigDecimal.valueOf(0));
        newSysOrder.setEstimateShipCost(BigDecimal.valueOf(0));//预估物流费
        newSysOrder.setProductCost(BigDecimal.valueOf(0));//产品成本
        for (SysOrderDetail detail : newSysOrder.getSysOrderDetails()) {
            newSysOrder.setItemNum(newSysOrder.getItemNum() + detail.getSkuQuantity());
            newSysOrder.setOrderAmount(newSysOrder.getOrderAmount().add(this.totalBigDecimal(detail.getSkuQuantity(), detail.getItemPrice())));
            newSysOrder.setTotalBulk(newSysOrder.getTotalBulk().add(detail.getBulk().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));
            newSysOrder.setTotalWeight(newSysOrder.getTotalWeight().add(detail.getWeight().multiply(BigDecimal.valueOf(detail.getSkuQuantity()))));
            newSysOrder.setProductCost(newSysOrder.getProductCost().add(this.totalBigDecimal(detail.getSkuQuantity(), detail.getItemCost())));
        }
        String str = systemOrderCommonService.calculateLogisticFeeBySKUS(newSysOrder);
        newSysOrder.setEstimateShipCost(new BigDecimal(str));
        _log.info("_______________订单{}_________预估物流费为：{}__________", newSysOrder.getSysOrderId(), str);
        newSysOrder.setTotal(newSysOrder.getOrderAmount().add(new BigDecimal(str)));
        String warehouseCode = newSysOrder.getDeliveryWarehouseCode();
        if (StringUtils.isNotBlank(warehouseCode)) {
            systemOrderCommonService.judgeWareHouseIsEnough(newSysOrder, username, false);
        }
        return newSysOrder;
    }

    private BigDecimal totalBigDecimal(Integer skuNum, BigDecimal itemPrice) {
        if (itemPrice == null || skuNum == null || skuNum == 0) {
            return BigDecimal.valueOf(0);
        }
        return itemPrice.multiply(BigDecimal.valueOf(skuNum));
    }

    /**
     * 判断SKU种类和数量是否支持拆分
     * <p>
     * 1、拆分订单只能勾选1条系统订单
     * 2、系统订单要求SKU种类数量大于等于2或单个SKU购买数量大于等于2；
     * 3、拆分订单所有的子订单数量产品数量之和务必等于主订单的产品数量才可保存，保存后编辑支持调整；
     * 4、发生过拆分或者合并的订单不能再进行拆分或合并；
     *
     * @param sysOrders
     * @return
     */
    private SysOrder judgeIsCanBeSplitted(List<SysOrder> sysOrders) {
        if (sysOrders == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分订单子订单集合传参不能为空。。。");
        List<String> sysOrderIds = sysOrders.stream().map(x -> x.getSysOrderId()).distinct().collect(Collectors.toList());
        if (sysOrderIds.size() != 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分子订单的主订单号必须一致。。。");
        }
        String fatherSysOrderId = sysOrderIds.get(0);
        SysOrder oldSysOrder = sysOrderMapper.selectSysOrderBySysOrderId(fatherSysOrderId);
        for (SysOrder sysOrder : sysOrders) {
            if ("4".equals(oldSysOrder.getOrderSource())) {
                if (StringUtils.isBlank(sysOrder.getEbayCarrierName())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的Ebay物流商名称不能为空。。。");
                }
            } else if ("5".equals(oldSysOrder.getOrderSource())) {
                if (StringUtils.isBlank(sysOrder.getAmazonCarrierName()) || StringUtils.isBlank(sysOrder.getAmazonShippingMethod())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "映射后的Amazon物流商名称且映射后的Amazon配送方式不能为空。。。");
                }
            }
        }
        if (oldSysOrder == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据父订单号查询出的系统订单实体类为空。。。");
        }
        if (oldSysOrder.getSplittedOrMerged() != 0) {
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
            if (skuCount.get(0) < 2) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "此订单只有一种商品且数量小于2，不能拆分。。。");
            }
        }
        //取出原始系统订单表中SKU和此SKU对应数量
        Map<String, Integer> splitMap = new HashMap<>();
        //拆分后SKU数量，种类等校验
        for (SysOrder order : sysOrders) {
            List<SysOrderDetail> sysDetails = order.getSysOrderDetails();
            for (SysOrderDetail detail : sysDetails) {
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
            }
        }
        Boolean flag = this.compareMap(oldMap, splitMap);
        if (!flag) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "拆分后SKU和对应SKU购买数量匹配不上。。。");
        }
        return oldSysOrder;
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
     * 1、拆分后的订单要进行撤销拆分后恢复成原始订单后再进行合并;
     * 2、撤销拆分的前提条件是所有的子订单的状态均为待发货或缺货状态;
     *
     * @param sysOrderId
     */
    @Override
    @Transactional
    public void cancelSplittedSysOrder(String sysOrderId) {
        RLock lock = redissLockUtil.lock(sysOrderId, 15);
        List<SysOrder> childSysOrders = sysOrderMapper.selectDeliveryStatusAndSysIdByMainOrderId(sysOrderId);
        if (CollectionUtils.isEmpty(childSysOrders) || childSysOrders.size() == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "根据订单号查询不到子订单。。。");
        }
        for (SysOrder order : childSysOrders) {
            Byte deliveryStatus = order.getOrderDeliveryStatus();
            if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.getValue() == deliveryStatus
                    || OrderHandleEnum.OrderDeliveryStatus.INTERCEPTED.getValue() == deliveryStatus
                    || OrderHandleEnum.OrderDeliveryStatus.DELIVERED.getValue() == deliveryStatus
                    || OrderHandleEnum.OrderDeliveryStatus.RECEIVED.getValue() == deliveryStatus
                    || OrderHandleEnum.OrderDeliveryStatus.CANCELLED.getValue() == deliveryStatus) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "子订单状态为配货中/已拦截/已发货/已收货/已作废，不能撤销。。。");
            }
        }
        String username = loginUserInfo.getUserInfo().getUser().getUsername();
        List<String> collect = childSysOrders.stream().map(x -> x.getSysOrderId()).collect(Collectors.toList());
        sysOrderDetailMapper.deleteBatchBySysOrderId(collect);
        sysOrderMapper.deleteBySysOrderId(sysOrderId);
        SysOrder sysOrder = new SysOrder();
        sysOrder.setSysOrderId(sysOrderId);
        sysOrder.setChildIds("");
        sysOrder.setOrderDeliveryStatus((byte) 1);
        sysOrder.setSplittedOrMerged(OrderHandleEnum.SplittedOrMerged.DEFAULT.getValue());//设置为默认状态
        sysOrder.setUpdateBy(username);
        sysOrderMapper.updateBySysOrderIdSelective(sysOrder);
        _log.info("________________撤销已拆分系统订单 {} 操作成功________________", sysOrderId);
        redissLockUtil.unlock(lock);
    }

    /**
     * 1、合并订单勾选系统订单数需大于等于2，且收货信息必须相同；
     * 2、发货仓库ID，名称和邮寄方式ID和名称信息必须相同；
     * 3、合并订单后新生成的主订单对应的商品数需等于被合并的原始订单的产品数量之和；
     * 4、不同来源的订单不能合并，不同卖家的不能合并。
     * 5、订单订单来源状态为"1手工创建,2批量导入"可以与"3第三方平台API推送,4eBay平台订单转入,5Amazon平台订单转入,6AliExpress订单,7Wish订单,8星商订单" 合并，3,4,5,6,7,8之间不能合并
     * 6、合并订单限定阈值:10个订单50个订单项。
     *
     * @param sysOrderIds
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public String saveMergedSysOrder(List<String> sysOrderIds) throws Exception {
        _log.info("_____________申请合并的订单为___________{}___________", sysOrderIds.toString());
        List<RLock> lockList = new ArrayList<>();
        for (String orderId : sysOrderIds) {
            RLock lock = redissLockUtil.lock(orderId, 20);
            lockList.add(lock);
        }
        if (CollectionUtils.isEmpty(sysOrderIds) || sysOrderIds.size() < 2) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并订单请求合并订单数不符合要求。。。");
        }
        List<SysOrder> sysOrders = this.judgeIsCanBeMerged(sysOrderIds);
        _log.info("_________________可以进行合并__________________");
        StringBuilder sourceOrderId = new StringBuilder();
        List<SysOrderDetail> detailList = new ArrayList<>();
        for (SysOrder sysOrder : sysOrders) {
            if (StringUtils.isNotBlank(sysOrder.getSourceOrderId())) {
                sourceOrderId.append(sysOrder.getSourceOrderId()).append("#");
            }
            for (SysOrderDetail detail : sysOrder.getSysOrderDetails()) {
                detailList.add(detail);
            }
        }
        if (CollectionUtils.isEmpty(detailList) && detailList.size() == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "合并所有订单项集合出异常。。。");
        }
        if (detailList.size() > 50) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并订单所有订单项数需小于等于50。。。");
        }
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
                    String distinctSourceId = distinct.getSourceOrderId();
                    String distinctSourceItemID = distinct.getSourceOrderLineItemId();
                    if (StringUtils.isNotBlank(detailSourceID) && StringUtils.isNotBlank(detailSourceItemID)) {
                        if (StringUtils.isNotBlank(distinctSourceId) && StringUtils.isNotBlank(distinctSourceItemID)) {
                            if (!distinctSourceId.contains(detailSourceID) && !distinctSourceItemID.contains(detailSourceItemID)) {
                                distinct.setSourceOrderId(distinctSourceId + "#" + detailSourceID);
                                distinct.setSourceOrderLineItemId(distinctSourceItemID + "#" + detailSourceItemID);
                            }
                        } else {
                            distinct.setSourceOrderId(detailSourceID);
                            distinct.setSourceOrderLineItemId(detailSourceItemID);
                        }
                    }
                }
            }
        }
        String sysOrderId = OrderUtils.getPLOrderNumber();
        String deliverDeadline = this.getDeliverDeadLine(sysOrders);
        String username = loginUserInfo.getUserInfo().getUser().getUsername();
        for (SysOrderDetail x : distinctDetailList) {
            x.setId(null);
            x.setSysOrderId(sysOrderId);
            x.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
            x.setCreateDate(null);
            x.setCreateBy(username);
            x.setUpdateDate(null);
            x.setUpdateBy(username);
            sysOrderDetailMapper.insertSelective(x);
        }
        StringBuilder childIds = new StringBuilder();
        for (SysOrder order : sysOrders) {
            order.setMainOrderId(sysOrderId);
            order.setOrderDeliveryStatus((byte) 0);
            order.setSplittedOrMerged(OrderHandleEnum.SplittedOrMerged.MERGED.getValue());
            order.setUpdateDate(null);
            order.setUpdateBy(username);
            sysOrderMapper.updateBySysOrderIdSelective(order);
            childIds.append(order.getSysOrderId()).append("#");
        }
        SysOrder sysOrder = this.setSysOrderProperty(sysOrders, sysOrderId);
        sysOrder.setSourceOrderId(StringUtils.isBlank(sourceOrderId) ? null : sourceOrderId.toString());
        sysOrder.setChildIds(childIds.toString());
        sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());
        sysOrder.setDeliverDeadline(deliverDeadline);
        sysOrder.setSysOrderDetails(distinctDetailList);
        if (childIds.length() > 500) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并订单数超过限制。。。");
        }
        sysOrder = this.setBaseData(sysOrder, username);//TODO 根据仓库和物流方式计算预估物流费
        sysOrder.setCreateDate(null);
        sysOrder.setCreateBy(username);
        sysOrder.setUpdateDate(null);
        sysOrder.setUpdateBy(username);
        systemOrderCommonService.setGrossMarginAndProfitMargin(sysOrder);
        sysOrderMapper.insertSelective(sysOrder);
        sysOrderLogService.insertSelective(new SysOrderLog(sysOrder.getSysOrderId(), OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrder.getSysOrderId()),
                OrderHandleLogEnum.OrderStatus.STATUS_11.getMsg(), sysOrder.getSellerPlAccount()));
        sysOrderLogService.insertSelective(new SysOrderLog(sysOrder.getSysOrderId(), OrderHandleLogEnum.Content.MERGE_SUCCESS.mergeSuccess(sysOrder.getSysOrderId()),
                OrderHandleLogEnum.OrderStatus.STATUS_11.getMsg(), sysOrder.getSellerPlAccount()));
        orderMessageSender.sendOrderStockOut(sysOrder.getSellerPlAccount(), sysOrder.getSysOrderId(), MessageEnum.ORDER_NEW_NOTICE, null);
        _log.info("_____________合并订单操作成功，新生成的主订单号为______________{}______________", sysOrderId);
        for (RLock lock : lockList) {
            redissLockUtil.unlock(lock);
        }
        return sysOrderId;
    }

    private String getDeliverDeadLine(List<SysOrder> sysOrders) {
        List<Integer> deadLineIntegerList = new ArrayList<>();
        for (SysOrder sysOrder : sysOrders) {
            String deadlineTime = sysOrder.getDeliverDeadline();
            if (StringUtils.isNotBlank(deadlineTime)) {
                deadLineIntegerList.add(TimeUtil.StringToTimestamp(deadlineTime));
            }
        }
        Integer minDeadLine = deadLineIntegerList.stream().reduce(Integer::min).get();
        Calendar c = Calendar.getInstance();
        long millions = new Long(minDeadLine).longValue() * 1000;
        c.setTimeInMillis(millions);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(c.getTime());
    }

    private SysOrder setSysOrderProperty(List<SysOrder> sysOrders, String sysOrderId) {
        SysOrder sysOrder = sysOrders.get(0);
        sysOrder.setSysOrderId(sysOrderId);
        sysOrder.setMainOrderId("");
        sysOrder.setSplittedOrMerged(OrderHandleEnum.SplittedOrMerged.MERGED.getValue());
        sysOrder.setOrderDeliveryStatus(OrderHandleEnum.OrderDeliveryStatus.PENDING_DELIVERY.getValue());
        sysOrder.setSellerPlId(sysOrder.getSellerPlId());
        sysOrder.setPlatformShopId(sysOrder.getPlatformShopId());
        sysOrder.setSellerPlAccount(sysOrder.getSellerPlAccount());
        List<SysOrder> commoditiesAmountList = new ArrayList<>(sysOrders.size());
        List<SysOrder> shippingServiceCostList = new ArrayList<>(sysOrders.size());
        for (SysOrder order : sysOrders) {
            if (order.getCommoditiesAmount() != null) {
                commoditiesAmountList.add(order);
            }
            if (order.getShippingServiceCost() != null) {
                shippingServiceCostList.add(order);
            }
        }
        if (CollectionUtils.isNotEmpty(commoditiesAmountList)) {
            sysOrder.setCommoditiesAmount(commoditiesAmountList.stream().map(SysOrder::getCommoditiesAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        if (CollectionUtils.isNotEmpty(shippingServiceCostList)) {
            sysOrder.setShippingServiceCost(shippingServiceCostList.stream().map(SysOrder::getShippingServiceCost).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return sysOrder;
    }

    /**
     * 判断能否合并订单并返回被合并的订单对象集合
     *
     * @param sysOrderIds
     * @return
     */
    private List<SysOrder> judgeIsCanBeMerged(List<String> sysOrderIds) {
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

        List<SysOrder> sysOrders = sysOrderMapper.selectBatchSysOrderListBySysOrderId(sysOrderIds);
        if (CollectionUtils.isEmpty(sysOrders) || sysOrders.size() != size) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "查询不到请求合并的订单。。。");
        }
        for (SysOrder sysOrder : sysOrders) {
            if (OrderHandleEnum.SplittedOrMerged.SPLITTED.equals(sysOrder.getSplittedOrMerged()) || OrderHandleEnum.SplittedOrMerged.MERGED.equals(sysOrder.getSplittedOrMerged())) {
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
        SysOrder bean = sysOrders.get(0);
        String deliveryWarehouseCode = bean.getDeliveryWarehouseCode();
        String deliveryWarehouseId = bean.getDeliveryWarehouseId();
        String deliveryWarehouse = bean.getDeliveryWarehouse();
        String shippingCarrierUsedCode = bean.getShippingCarrierUsedCode();
        String shippingCarrierUsed = bean.getShippingCarrierUsed();
        String deliveryMethod = bean.getDeliveryMethod();
        String deliveryMethodCode = bean.getDeliveryMethodCode();
        String amazonCarrierName = bean.getAmazonCarrierName();
        String amazonShippingMethod = bean.getAmazonShippingMethod();
        String ebayCarrierName = bean.getEbayCarrierName();
        if (StringUtils.isNotBlank(deliveryWarehouseId) && StringUtils.isNotBlank(deliveryWarehouse) &&
                StringUtils.isNotBlank(shippingCarrierUsedCode) && StringUtils.isNotBlank(shippingCarrierUsed)) {
            for (SysOrder order : sysOrders) {
                if ((!deliveryWarehouseId.equals(order.getDeliveryWarehouseId()))
                        || (!deliveryWarehouse.equals(order.getDeliveryWarehouse()))
                        || (!shippingCarrierUsedCode.equals(order.getShippingCarrierUsedCode()))
                        || (!shippingCarrierUsed.equals(order.getShippingCarrierUsed())
                        || (!deliveryMethod.equals(order.getDeliveryMethod()))
                        || (!deliveryMethodCode.equals(order.getDeliveryMethodCode())))) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "发货仓库ID和名称，物流商CODE和名称，邮寄方式CODE和名称信息必须一致。。。");
                }
                Byte orderSource = order.getOrderSource();
                if (orderSource == (byte) 4) {
                    if (!ebayCarrierName.equals(order.getEbayCarrierName())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "子订单物流商ebayCarrierName不一致，不能合并。。。");
                    }
                }
                if (orderSource == (byte) 5) {
                    if (!amazonCarrierName.equals(order.getAmazonCarrierName()) || !amazonShippingMethod.equals(order.getAmazonShippingMethod())) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "子订单物流商amazonCarrierName或amazonShippingMethod不一致，不能合并。。。");
                    }
                }
                order.setDeliveryWarehouseId(deliveryWarehouseId);
                order.setDeliveryWarehouseCode(deliveryWarehouseCode);
                order.setDeliveryWarehouse(deliveryWarehouse);
                order.setShippingCarrierUsedCode(shippingCarrierUsedCode);
                order.setShippingCarrierUsed(shippingCarrierUsed);
                order.setDeliveryMethod(deliveryMethod);
                order.setDeliveryMethodCode(deliveryMethodCode);
                order.setAmazonCarrierName(amazonCarrierName);
                order.setAmazonShippingMethod(amazonShippingMethod);
                order.setEbayCarrierName(ebayCarrierName);
            }
        }
        List<ShippingAddress> shippingAddressList = BeanConvertor.copyList(sysOrders, ShippingAddress.class, null);
        for (ShippingAddress address : shippingAddressList) {
            if (!DomainEquals.domainEquals(address, shippingAddressList.get(0))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并系统订单收货人信息不一致，不能合并。。。");
            }
        }
        return sysOrders;
    }

    /**
     * 1、合并后的订单需进行撤销合并后恢复成原始订单才能进行拆分;
     * 2、撤销合并的前提条件是合并的主订单的状态为待发货或缺货状态;
     *
     * @param sysOrderId
     */
    @Override
    @Transactional
    public void cancelMergedSysOrder(String sysOrderId) {
        RLock lock = redissLockUtil.lock(sysOrderId, 15);
        SysOrder sysOrder = sysOrderMapper.selectSysOrderBySysOrderId(sysOrderId);
        if (sysOrder == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据系统订单号查询不到订单数据。。。");
        }
        Byte orderDeliveryStatus = sysOrder.getOrderDeliveryStatus();
        Byte splittedOrMerged = sysOrder.getSplittedOrMerged();
        if (splittedOrMerged != (byte) 2) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "此订单未发生过合并。。。");
        }
        if (StringUtils.isNotBlank(sysOrder.getMainOrderId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "撤销合并请传主订单号。。。");
        }
        if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.getValue() == orderDeliveryStatus
                || OrderHandleEnum.OrderDeliveryStatus.INTERCEPTED.getValue() == orderDeliveryStatus
                || OrderHandleEnum.OrderDeliveryStatus.DELIVERED.getValue() == orderDeliveryStatus
                || OrderHandleEnum.OrderDeliveryStatus.RECEIVED.getValue() == orderDeliveryStatus
                || OrderHandleEnum.OrderDeliveryStatus.CANCELLED.getValue() == orderDeliveryStatus) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "主订单状态需为待发货或缺货才能撤销合并。。。");
        }
        List<String> list = new ArrayList<>();
        list.add(sysOrderId);
        sysOrderDetailMapper.deleteBatchBySysOrderId(list);
        sysOrderMapper.deleteSysOrderBySysOrderId(sysOrderId);
        String username = loginUserInfo.getUserInfo().getUser().getUsername();
        List<SysOrder> sysOrderList = sysOrderMapper.selectSysOrderByMainOrderId(sysOrderId);
        for (SysOrder order : sysOrderList) {
            order.setMainOrderId("");
            order.setOrderDeliveryStatus((byte) 1);
            order.setSplittedOrMerged((byte) 0);
            order.setUpdateBy(username);
            sysOrderMapper.updateSysOrder(order);
        }
        _log.info("_____________撤销合并订单成功，操作的系统订单号为{}___________", sysOrderId);
        lock.unlock();
    }

    @Override
    public void deliverGoodSingleNew(String sysOrderId, boolean isAutoDeliveryPackage) throws Exception {
        long startTime = System.currentTimeMillis();
        _log.info("申请发货的订单号为: {}", sysOrderId);

        DeliveryPackageDTO deliveryPackageDTO = this.getOrderListFromSysOrderId(sysOrderId);
        // 校验订单数据
        List<SysOrderNew> sysOrderNewList = deliveryPackageDTO.getSysOrderNewList();
        this.validateSysOrders(sysOrderNewList, deliveryPackageDTO);

        // 构造财务的请求数据
        List<OrderRequestVo> orderRequestVoList = this.constructOrderRequestVoDataNew(sysOrderNewList, isAutoDeliveryPackage);

        // 包裹发货
        try {
            systemOrderService.deliveryPackage(deliveryPackageDTO, orderRequestVoList, isAutoDeliveryPackage);
        } catch (Exception e) {
            _log.error("{}订单发货异常，{}",sysOrderId, e.getMessage());
            //财务解冻先
            sysOrderNewList.forEach(sysOrderNew -> {
                remoteFinanceService.cancel(sysOrderNew.getSysOrderId());
            });

            if (!deliveryPackageDTO.getMergedPackageOrder()) {
                // 发货异常，需要回滚, 只有拆分
                sysOrderService.InterceptSplitPackageOrderByDelievryUse(sysOrderNewList.get(0));
            }
            throw e;
        }
        long endTime = System.currentTimeMillis();
        _log.info("订单{}发货总耗时为{}ms", sysOrderId, endTime - startTime);
    }

    @Override
    @Transactional
    //@TxTransaction(isStart = true)
    public void deliveryPackage(DeliveryPackageDTO deliveryPackageDTO, List<OrderRequestVo> orderRequestVoList, boolean isAutoDeliveryPackage) throws Exception {
        List<SysOrderPackage> deliveryPackageList = this.preDeliveryPackage(deliveryPackageDTO);
        List<SysOrderNew> deliverySysOrderList = deliveryPackageDTO.getSysOrderNewList();
        String username = "";
        if (isAutoDeliveryPackage) {
            username = SysOrderLogEnum.Operator.SYSTEM.getMsg();
        } else {
            username = loginUserInfo.getUserInfo().getUser().getUsername();
        }

        // 不管是 普通包裹订单 拆分包裹订单 合并包裹订单 ， 都必须先冻结，后发货
        for (SysOrderNew deliveryOrder : deliverySysOrderList) {
            String sysOrderId = deliveryOrder.getSysOrderId();
            SysOrderNew updateOrder = new SysOrderNew();
            updateOrder.setSysOrderId(sysOrderId);

            OrderRequestVo orderRequestVo = this.getOrderRequestVo(orderRequestVoList, sysOrderId);
            _log.info("订单{}发货调用财务冻结金额发送内容为: {}", sysOrderId, JSONObject.toJSONString(orderRequestVo));
            Map<String, String> financeReturnMap = systemOrderCommonService.financeGenerate(orderRequestVo);
            _log.info("订单{}发货调用财务冻结金额返回结果为: {}", sysOrderId, financeReturnMap);
            String serialNo = financeReturnMap.get("serialNo");
            String payType = financeReturnMap.get("payType");
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.FREEZE_SUCCESS.freezeSuccess(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), username));
            _log.info("冻结卖家账户订单金额成功，冻结订单号为 {}", sysOrderId);
            updateOrder.setPayId(serialNo);
            updateOrder.setPayStatus(PayInfoEnum.PayStatusEnum.FREEZE_SUCCESS.getValue());
            updateOrder.setPayMethod(PayInfoEnum.PayMethodEnum.getPayMethod(payType));
            updateOrder.setSysOrderId(sysOrderId);
            updateOrder.setOrderDeliveryStatus(OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.getValue());
            updateOrder.setUpdateBy(username);
            // TODO: 需要更新冻结信息
            sysOrderNewMapper.updateBySysOrderIdSelective(updateOrder);
        }

        List<SysOrderDetail> deliverySysOrderDetail = new ArrayList<>();
        for (SysOrderNew order : deliverySysOrderList) {
            deliverySysOrderDetail.addAll(order.getSysOrderDetails());
        }

        for (SysOrderPackage deliveryPackage : deliveryPackageList) {
            // TODO: 构造数据给ERP的时候，需要订单号，合并包裹是否可以订单号合并后传给ERP？
            SysOrderNew deliveryOrder = deliverySysOrderList.get(0);
            Integer warehouseId = deliveryPackage.getDeliveryWarehouseId();
            String sysOrderId = deliveryOrder.getSysOrderId();
            WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(String.valueOf(warehouseId));

            String orderTrackId = deliveryPackage.getOrderTrackId();
            // 需要更新包裹的跟踪信息
            SysOrderPackage updatePackage = new SysOrderPackage();
            updatePackage.setOrderTrackId(orderTrackId);
            //谷仓
            if (warehouseDTO.getFirmCode().equals(Constants.Warehouse.GOODCANG)) {
                GoodCangOrder goodCangOrder = this.constructGoodCangOrderDataNew(deliveryOrder, deliveryPackage);
                String referenceId = goodCangService.deliverGoodToGoodCang(goodCangOrder);
                updatePackage.setReferenceId(referenceId);
            } else if ((warehouseDTO.getFirmCode().equals(Constants.Warehouse.RONDAFUL))) {
                //ERP
                Map<String, Object> erpOrderMap = this.constructERPOrderDataNew(deliveryOrder, deliveryPackage, deliverySysOrderDetail);
                remoteErpService.orderReceive(erpOrderMap);
            } else if ((warehouseDTO.getFirmCode().equals(Constants.Warehouse.WMS))) {
                //WMS
                WmsOrderDTO wmsOrderDTO = wmsService.assembleWmsOrderDate(deliveryOrder, deliveryPackage, deliverySysOrderDetail);
                wmsService.createWmsOrder(wmsOrderDTO, String.valueOf(warehouseId));
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "错误的仓库类型");
            }
            for (SysOrderPackageDetail item : deliveryPackage.getSysOrderPackageDetailList()) { // 发货成功，推送发货数量到仓库
                String sku = item.getSku();
                Integer skuQuantity = item.getSkuQuantity();
                _log.info("推送发货数量到仓库，请求参数：仓库id:{},sku:{},sku数量：{}", deliveryPackage.getDeliveryWarehouseId(), sku, skuQuantity);
                String result = remoteSupplierService.updateLocalShipping(deliveryPackage.getDeliveryWarehouseId(), sku, skuQuantity);
                _log.info("推送发货数量到仓库，返回：{}", result);
            }
            updatePackage.setPackageStatus(OrderPackageStatusEnum.WAIT_DELIVER.getValue());
            _log.debug("开始更新发货包裹{} 的状态,内容是: {}", orderTrackId, FastJsonUtils.toJsonString(updatePackage));
            sysOrderPackageMapper.updateByOrderTrackIdSelective(updatePackage);
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PICKING_CARGO
                    .pickingCargo(sysOrderId, orderTrackId), OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), username));
        }
    }

    private List<SysOrderPackage> preDeliveryPackage(DeliveryPackageDTO deliveryPackageDTO) {
        List<SysOrderPackage> deliveryPackageList = new ArrayList<>();
        if (deliveryPackageDTO.getMergedPackageOrder()) {
            // 合并包裹有多个订单
            deliveryPackageList.add(deliveryPackageDTO.getSysOrderPackage());
        } else {
            // 普通包裹 和 拆分包裹 只有一个订单
            deliveryPackageList.addAll(deliveryPackageDTO.getSysOrderNewList().get(0).getSysOrderPackageList());
        }

        return deliveryPackageList;
    }

    private OrderRequestVo getOrderRequestVo(List<OrderRequestVo> orderRequestVoList, String sysOrderId) {
        for (OrderRequestVo orderRequestVo : orderRequestVoList) {
            if (Objects.equals(sysOrderId, orderRequestVo.getOrderNo())) {
                return orderRequestVo;
            }
        }

        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "获取不到构造的发货财务参数");
    }

    private List<OrderRequestVo> constructOrderRequestVoDataNew(List<SysOrderNew> sysOrderNewList, boolean isAutoDeliveryPackage) {
        Integer userId = null;
        if (isAutoDeliveryPackage) {   //TODO 普通发货取当前操作人ID， 自动发货取订单的品连ID（主账号ID）
            userId = sysOrderNewList.get(0).getSellerPlId();
        } else {
            userId = loginUserInfo.getUserDTO().getUserId();
        }
        List<OrderRequestVo> orderRequestVoList = new ArrayList<>();
        for (SysOrderNew sysOrderNew : sysOrderNewList) {
            OrderRequestVo orderRequestVo = this.assembleOrderRequestVoData(sysOrderNew, userId);
            orderRequestVoList.add(orderRequestVo);
        }
        return orderRequestVoList;
    }

    private OrderRequestVo assembleOrderRequestVoData(SysOrderNew sysOrderNew, Integer userId) {
        OrderRequestVo vo = new OrderRequestVo();
        vo.setSellerName(sysOrderNew.getSellerPlAccount());
        vo.setProductAmount(sysOrderNew.getOrderAmount());
        vo.setLogisticsFare(sysOrderNew.getEstimateShipCost());
        vo.setSellerId(sysOrderNew.getSellerPlId());
        vo.setOrderNo(sysOrderNew.getSysOrderId());
        vo.setSellerAccount(sysOrderNew.getSellerPlAccount());

        List<SysOrderPackage> sysOrderPackageList = sysOrderNew.getSysOrderPackageList();
        // 同个订单的包裹目的地肯定是一样的
        SysOrderPackage addressPackage = sysOrderPackageList.get(0);

        vo.setLogisticsId(addressPackage.getShippingCarrierUsedCode());
        vo.setLogisticsName(addressPackage.getShippingCarrierUsed() == null ? "——" : addressPackage.getShippingCarrierUsed());
        vo.setStorageId(addressPackage.getDeliveryWarehouseCode());
        vo.setStorageName(addressPackage.getDeliveryWarehouse() == null ? "——" : addressPackage.getDeliveryWarehouse());
        vo.setSupplyCompanyId(sysOrderNew.getSupplyChainCompanyId());
        vo.setSupplyCompanyName(sysOrderNew.getSupplyChainCompanyName());
        vo.setShopId(sysOrderNew.getPlatformShopId());
        vo.setShopType(sysOrderNew.getShopType());
        vo.setCurrency(rateCurrency);
        vo.setPlatformTotal(sysOrderNew.getCommoditiesAmount() == null ? new BigDecimal(0) : sysOrderNew.getCommoditiesAmount());//TODO
        vo.setOperateUserId(userId);

        List<OrderItemVo> orderItems = new ArrayList<>();
        boolean isContainsFreeFreight = false;
        for (SysOrderDetail sysOrderDetail : sysOrderNew.getSysOrderDetails()) {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(sysOrderDetail.getSysOrderId());
            orderItemVo.setItemNo(sysOrderDetail.getSupplierSku());
            orderItemVo.setSellerId(sysOrderNew.getSellerPlId());//1
            orderItemVo.setSupplierId(sysOrderDetail.getSupplierId().intValue());
            orderItemVo.setSupplierName(sysOrderDetail.getSupplierName());
            orderItemVo.setProductAmount(sysOrderDetail.getItemPrice().multiply(BigDecimal.valueOf(sysOrderDetail.getSkuQuantity())));
            orderItemVo.setSupplyCompanyId(sysOrderDetail.getSupplyChainCompanyId());
            orderItemVo.setSupplyCompanyName(sysOrderDetail.getSupplyChainCompanyName());
            orderItemVo.setProductName(sysOrderDetail.getSkuTitle());
            String fareTypeAmount = sysOrderDetail.getFareTypeAmount();
            if (StringUtils.isNotBlank(fareTypeAmount)) {
                String[] split = StringUtils.split(fareTypeAmount, Constants.SplitSymbol.HASH_TAG);
                if ("1".equals(split[0])) {
                    orderItemVo.setFareType("固定金额");
                    orderItemVo.setFare(new BigDecimal(split[1]));
                } else {
                    orderItemVo.setFareType("百分比");
                    orderItemVo.setFare(new BigDecimal(split[1]));
                }
            }
            orderItemVo.setSkuQuantity(sysOrderDetail.getSkuQuantity());
            orderItemVo.setItemPrice(sysOrderDetail.getItemPrice());
            orderItems.add(orderItemVo);
            if (Objects.equals(sysOrderDetail.getFreeFreight(), Constants.SysOrder.FREE_FREIGHT)) {
                isContainsFreeFreight = true;
            }
        }
        vo.setOrderItems(orderItems);


        // 包邮，或者部分包邮都传true。 不包邮传false
        vo.setFreeShipping(isContainsFreeFreight);
        return vo;
    }

    /**
     * 校验订单数据
     *
     * @param sysOrderNewList    {@link List<SysOrderNew>}
     * @param deliveryPackageDTO {@link DeliveryPackageDTO}
     */
    private void validateSysOrders(List<SysOrderNew> sysOrderNewList,
                                   DeliveryPackageDTO deliveryPackageDTO) throws Exception {
        String username = loginUserInfo.getUserInfo().getUser().getUsername();

        for (SysOrderNew sysOrderNew : sysOrderNewList) {
            if (null == sysOrderNew) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "未识别的系统订单号。。。");
            }

            Byte orderDeliveryStatus = sysOrderNew.getOrderDeliveryStatus();

            if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.getValue() == orderDeliveryStatus ||
                    OrderHandleEnum.OrderDeliveryStatus.DELIVERED.getValue() == orderDeliveryStatus ||
                    OrderHandleEnum.OrderDeliveryStatus.RECEIVED.getValue() == orderDeliveryStatus ||
                    OrderHandleEnum.OrderDeliveryStatus.CANCELLED.getValue() == orderDeliveryStatus) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "此状态下不支持继续发货。。。");
            }

            // 校验发货数据
            List<SysOrderPackage> sysOrderPackageList = new ArrayList<>();
            if (deliveryPackageDTO.getMergedPackageOrder()) {
                sysOrderPackageList.add(deliveryPackageDTO.getSysOrderPackage());
            } else {
                sysOrderPackageList.addAll(sysOrderNew.getSysOrderPackageList());
            }

            for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {
                CheckOrderUtils.validateSysOrderDataForDeliverGood(sysOrderNew,
                        sysOrderPackage, sysOrderNew.getSysOrderReceiveAddress());

                // 判断库存,是否缺货
                systemOrderCommonService.judgeWareHouseIsEnough(sysOrderPackage, username, sysOrderNew.getSellerPlAccount(), true);

            }
        }
    }

    /**
     * 根据订单号获取订单列表
     *
     * @param sysOrderId 订单号
     * @return {@link List<DeliveryPackageDTO>}
     */
    private DeliveryPackageDTO getOrderListFromSysOrderId(String sysOrderId) {
        SysOrderNew sysOrderNew = this.getSysOrderNew(sysOrderId);
        if (null == sysOrderNew) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单不存在");
        }

        return this.getOrderListFromSysOrder(sysOrderNew);
    }

    /**
     * 根据订单获取订单列表。
     * 因为包裹合并的关系，需要操作2个订单
     *
     * @param sysOrderNew {@link SysOrderNew}
     * @return {@link DeliveryPackageDTO}
     */
    private DeliveryPackageDTO getOrderListFromSysOrder(SysOrderNew sysOrderNew) {
        String sysOrderId = sysOrderNew.getSysOrderId();
        List<SysOrderNew> sysOrderNewList = new ArrayList<>();
        String operateStatus = sysOrderNew.getSplittedOrMerged();

        DeliveryPackageDTO deliveryPackageDTO = new DeliveryPackageDTO();
        SysOrderPackage orderPackage = sysOrderNew.getSysOrderPackageList().get(0);
        if (Objects.equals(operateStatus, OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
            String mergedOrderTrackId = orderPackage.getOperateOrderTrackId();
            _log.info("订单{} 是合并包裹订单， 合并的包裹是: {}", sysOrderId, mergedOrderTrackId);
            SysOrderPackage mergedPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(mergedOrderTrackId);
            List<SysOrderPackageDetail> mergedPackageDetailList = sysOrderPackageDetailMapper
                    .queryOrderPackageDetails(mergedPackage.getOrderTrackId());
            mergedPackage.setSysOrderPackageDetailList(mergedPackageDetailList);

            String operateSysOrderId = mergedPackage.getOperateSysOrderId();
            String[] mergedSysOrderIds = StringUtils.split(operateSysOrderId, Constants.SplitSymbol.HASH_TAG);
            for (String mergedSysOrderId : mergedSysOrderIds) {
                SysOrderNew mergedSysOrderNew = this.getSysOrderNew(mergedSysOrderId);
                sysOrderNewList.add(mergedSysOrderNew);
            }

            deliveryPackageDTO.setMergedPackageOrder(true);
            deliveryPackageDTO.setSysOrderPackage(mergedPackage);
        } else if (Objects.equals(operateStatus, OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
            _log.info("订单{} 是拆分包裹订单", sysOrderId);
            sysOrderNewList.add(sysOrderNew);
            deliveryPackageDTO.setMergedPackageOrder(false);
            deliveryPackageDTO.setSysOrderPackage(orderPackage);
        } else {
            _log.info("订单{} 是普通包裹订单", sysOrderId);
            sysOrderNewList.add(sysOrderNew);
            deliveryPackageDTO.setSysOrderPackage(orderPackage);
            deliveryPackageDTO.setMergedPackageOrder(false);
        }
        deliveryPackageDTO.setSysOrderNewList(sysOrderNewList);

        return deliveryPackageDTO;
    }

    @Override
    public void deliverGoodSingle(String sysOrderId) throws Exception {
        long startTime = System.currentTimeMillis();
        _log.info("申请发货的订单号为: {}", sysOrderId);
        List<SysOrder> sysOrders = sysOrderMapper.selectBatchSysOrderListBySysOrderId(new ArrayList<String>() {{
            add(sysOrderId);
        }});

        if (CollectionUtils.isEmpty(sysOrders)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "未识别的系统订单号。。。");
        }

        String username = loginUserInfo.getUserInfo().getUser().getUsername();
        SysOrder sysOrder = sysOrders.get(0);
        this.validateRequiredField(sysOrders);
        RLock lock = redissLockUtil.lock(sysOrderId, 20);
        Byte orderDeliveryStatus = sysOrder.getOrderDeliveryStatus();

        if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.getValue() == orderDeliveryStatus ||
                OrderHandleEnum.OrderDeliveryStatus.DELIVERED.getValue() == orderDeliveryStatus ||
                OrderHandleEnum.OrderDeliveryStatus.RECEIVED.getValue() == orderDeliveryStatus ||
                OrderHandleEnum.OrderDeliveryStatus.CANCELLED.getValue() == orderDeliveryStatus) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "此状态下不支持继续发货。。。");
        }

        // 判断拆、合单
        boolean flag = this.judgeSplittedOrMergedCanDeliverGood(sysOrder);
        if (!flag) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "合并过的订单,仅主订单能发货;拆分过的订单,仅子订单能发货。。。");
        }

        // 判断库存
        systemOrderCommonService.judgeWareHouseIsEnough(sysOrder, username, true);
        boolean isSupportDeliverMethod = systemOrderCommonService.judgeSupportDeliverMethod(sysOrder);
        if (!isSupportDeliverMethod) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单中商品不支持此邮寄方式。。。");
        }

        try {
            OrderRequestVo orderRequestVo = this.constructOrderRequestVoData(sysOrder);//构造财务冻结订单数据

            boolean isGoodCangOrder = systemOrderCommonService.isGoodCangWarehouse(sysOrder.getDeliveryWarehouseId());
            _log.info("订单{} 是否谷仓订单的标识是：{}", sysOrderId, isGoodCangOrder);

            if (isGoodCangOrder) {
                GoodCangOrder goodCangOrder = this.constructGoodCangOrderData(sysOrder);//构造谷仓发货数据
                systemOrderService.transactionFrozenAmountAndDeliverGoodGoodCang(sysOrder, orderRequestVo, goodCangOrder, username);
            } else {
                Map<String, Object> erpOrderMap = this.constructERPOrderData(sysOrder);//构造ERP发货数据
                systemOrderService.transactionFrozenAmountAndDeliverGoodERP(sysOrder, orderRequestVo, erpOrderMap, username);
            }
        } catch (Exception e) {
            _log.error("________________订单:{}冻结金额发货事务执行出错_________{}_______", sysOrderId, e);
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.FREEZE_FAILURE.freezeFailure(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), sysOrder.getSellerPlAccount()));
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
        }
        _log.info("_____________________推送发货订单 {} 至ERP成功_____________________", sysOrderId);
        lock.unlock();
        long endTime = System.currentTimeMillis();
        _log.info("订单{}发货总耗时为{}ms", sysOrderId, startTime - endTime);
    }

    @Override
    public Integer canBeDeliveredPlatformOrder(String sysOrderId) {
        Integer result = Constants.CanBeDelivered.CAN_BE_DELIVERRED;
        SysOrderNew sysOrder = sysOrderNewMapper.queryOrderByOrderId(sysOrderId);
        if (null == sysOrder) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "未识别的系统订单号。。。");
        }
        DeliveryPackageDTO deliveryPackageDTO = this.getOrderListFromSysOrderId(sysOrderId);
        List<SysOrderNew> sysOrderNewList = deliveryPackageDTO.getSysOrderNewList();
        for (SysOrderNew sysOrderNew : sysOrderNewList) {
            // 校验发货数据
            List<SysOrderPackage> sysOrderPackageList = new ArrayList<>();
            if (deliveryPackageDTO.getMergedPackageOrder()) {
                sysOrderPackageList.add(deliveryPackageDTO.getSysOrderPackage());
            } else {
                sysOrderPackageList.addAll(sysOrderNew.getSysOrderPackageList());
            }

            for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {
                //仓库已禁用
                WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(String.valueOf(sysOrderPackage.getDeliveryWarehouseId()));
                if (warehouseDTO.getStatus() == Constants.WareHousAbleb.DISABLED) {
                    return Constants.CanBeDelivered.WAREHOUSE_DISABLED;
                }

                //判断物流方式是否停用
                String deliveryMethodCode = sysOrderPackage.getDeliveryMethodCode();
                Integer deliveryWarehouseId = sysOrderPackage.getDeliveryWarehouseId();
                LogisticsDTO logisticsDTO = systemOrderCommonService.queryLogisticsByCode(deliveryMethodCode, deliveryWarehouseId);
                if (null == logisticsDTO || logisticsDTO.getStatus() == Constants.LogisticsAbleb.DISABLED) {
                    return Constants.CanBeDelivered.LOGISTICS_DISABLED;
                }
            }
        }

        //是否缴纳押金

        Byte orderSource = sysOrder.getOrderSource();
        String sourceOrderId = sysOrder.getSourceOrderId();
        if (StringUtils.isNotBlank(sourceOrderId)) {
            List<String> list = new ArrayList<>();
            if (sourceOrderId.contains("#")) {
                list = Arrays.asList(sourceOrderId.split("#"));
            } else {
                list.add(sourceOrderId);
            }
            long count = 0;
            if (Objects.equals(orderSource, OrderSourceEnum.CONVER_FROM_EBAY.getValue())) {
                List<EbayOrder> ebayList = ebayOrderMapper.selectBatchSysOrderByOrderId(list);
                count = ebayList.stream().filter(x -> StringUtils.isNotEmpty(x.getShippedTime())).count();
            } else if (Objects.equals(orderSource, OrderSourceEnum.CONVER_FROM_AMAZON.getValue())) {
                List<AmazonOrder> amazonList = amazonOrderMapper.selectAmazonOrderByOrderListId(list);
                count = amazonList.stream().filter(x -> "部分发货".equals(x.getOrderStatus()) || "均已发货".equals(x.getOrderStatus())
                        || "均已发货、未寄发票".equals(x.getOrderStatus())).count();
            } else if (Objects.equals(orderSource, OrderSourceEnum.CONVER_FROM_ALIEXPRESS.getValue())) {
                List<AliexpressOrder> aliList = aliexpressOrderMapper.getsByOrderId(list);
                count = aliList.stream().filter(x -> "SELLER_PART_SEND_GOODS".equals(x.getOrderStatus())).count();
            }
            if (count != 0) {
                return Constants.CanBeDelivered.HAS_MARK;
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cmsMarkSYSCompleted(List<String> sysIDList) {
        _log.info("售后改订单状态为已完成，参数：{}", FastJsonUtils.toJsonString(sysIDList));
        if (CollectionUtils.isEmpty(sysIDList)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求集合为空。。。");
        }
        String updateBy = SysOrderLogEnum.Operator.SYSTEM.getMsg();
        UserAll userInfo = loginUserInfo.getUserInfo();
        if (userInfo != null && userInfo.getUser() != null && StringUtils.isNotBlank(userInfo.getUser().getUsername())) {
            updateBy = userInfo.getUser().getUsername();
        }
        sysOrderNewMapper.updateOrderCompleteStatus((byte) 8, sysIDList, updateBy);
        for (String sysOrderId : sysIDList) {
            updateOrderCompleteStatus(sysOrderId);
        }
    }


    public void updateOrderCompleteStatus(String sysOrderId) {
        sysOrderNewMapper.updateOrdersStatus(sysOrderId, OrderDeliveryStatusNewEnum.COMPLETED.getValue());
        //添加操作日志
        sysOrderLogService.insertSelective(
                new SysOrderLog(sysOrderId,
                        OrderHandleLogEnum.Content.COMPLETED.completed(sysOrderId),
                        OrderHandleLogEnum.OrderStatus.STATUS_13.getMsg(),
                        OrderHandleLogEnum.Operator.SYSTEM.getMsg()));
    }


    @Override
    public List<SysOrder> deliverGoodBatch(List<String> sysOrderIds) throws Exception {
        List<SysOrder> exceptionDeliverSysOrderList = new ArrayList<>();
        List<SysOrder> sysOrders = sysOrderMapper.selectBatchSysOrderListBySysOrderId(sysOrderIds);
        if (CollectionUtils.isEmpty(sysOrders) || sysOrders.size() == 0) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "未识别的系统订单号。。。");
        }
        if (sysOrders.stream().map(x -> x.getSellerPlAccount()).distinct().count() != 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "批量发货的订单必须属于同一品连卖家。。。");
        }
        this.validateRequiredField(sysOrders);
        String username = loginUserInfo.getUserInfo().getUser().getUsername();
        for (SysOrder sysOrder : sysOrders) {
            Empower empower = systemOrderCommonService.queryAuthorizationFromSeller(sysOrder);
            Integer rentstatus = empower.getRentstatus();
            sysOrder.setShopType(rentstatus == 0 ? "PERSONAL" : "RENT");
            Byte orderDeliveryStatus = sysOrder.getOrderDeliveryStatus();
            String sysOrderId = sysOrder.getSysOrderId();
            RLock lock = redissLockUtil.lock(sysOrderId, 20);
            if (OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.getValue() == orderDeliveryStatus ||
                    OrderHandleEnum.OrderDeliveryStatus.DELIVERED.getValue() == orderDeliveryStatus ||
                    OrderHandleEnum.OrderDeliveryStatus.RECEIVED.getValue() == orderDeliveryStatus ||
                    OrderHandleEnum.OrderDeliveryStatus.CANCELLED.getValue() == orderDeliveryStatus) {
                _log.info("_________________此订单 {}  发货状态不支持继续发货_________________", sysOrderId);
                String exceptionString = "此订单发货状态不支持继续发货。。。";
                sysOrder.setWarehouseShipException(Utils.translation(exceptionString));
                exceptionDeliverSysOrderList.add(sysOrder);
                continue;
            }
            boolean flag = this.judgeSplittedOrMergedCanDeliverGood(sysOrder);
            if (!flag) {
                _log.info("____________此订单:{} 合并过的订单,仅主订单能发货;拆分过的订单,仅子订单能发货____________", sysOrderId);
                String exceptionString = "合并过的订单,仅主订单能发货;拆分过的订单,仅子订单能发货。。。";
                systemOrderCommonService.updateDeliverExceptionInfo(sysOrder, exceptionString, null, null, username);
                sysOrder.setWarehouseShipException(Utils.translation(exceptionString));
                exceptionDeliverSysOrderList.add(sysOrder);
                continue;
            }
            try {
                systemOrderCommonService.judgeWareHouseIsEnough(sysOrder, username, true);
            } catch (JSONException e) {
                _log.error("___________此订单:{} 调用供应商校验库存异常________{}________", sysOrderId, e);
                systemOrderCommonService.updateDeliverExceptionInfo(sysOrder, e.getMessage(), null, null, username);
                continue;
            }
            try {
                Boolean isSupportDeliverMethod = systemOrderCommonService.judgeSupportDeliverMethod(sysOrder);
                if (!isSupportDeliverMethod) {
                    _log.info("____________此订单 {} 发货订单中商品不支持此邮寄方式_____________", sysOrderId);
                    String exceptionString = "发货订单中商品不支持此邮寄方式。。。";
                    systemOrderCommonService.updateDeliverExceptionInfo(sysOrder, exceptionString, null, null, username);
                    sysOrder.setWarehouseShipException(Utils.translation(exceptionString));
                    exceptionDeliverSysOrderList.add(sysOrder);
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PUSH_FAIL_1.pushFail_1(sysOrderId),
                            OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), sysOrder.getSellerPlAccount()));
                    continue;
                }
            } catch (Exception e) {
                _log.error("_______________此订单 {} 发货验证邮寄方式供应商微服务异常_________{}________", sysOrderId, e);
                String exceptionString = "验证邮寄方式供应商微服务异常," + e.getMessage();
                systemOrderCommonService.updateDeliverExceptionInfo(sysOrder, exceptionString, null, null, username);
                sysOrder.setWarehouseShipException(Utils.translation(exceptionString));
                exceptionDeliverSysOrderList.add(sysOrder);
                continue;
            }
//            String warehouseCode = sysOrder.getDeliveryWarehouseCode();
            try {
                OrderRequestVo orderRequestVo = this.constructOrderRequestVoData(sysOrder);//构造财务冻结订单数据
//                if (warehouseCode.startsWith("GC_")) {
//                    GoodCangOrder goodCangOrder = this.constructGoodCangOrderData(sysOrder);//构造谷仓发货数据
//                    systemOrderService.transactionFrozenAmountAndDeliverGoodGoodCang(sysOrder, orderRequestVo, goodCangOrder, username);
//                }

                boolean isGoodCangOrder = systemOrderCommonService.isGoodCangWarehouse(sysOrder.getDeliveryWarehouseId());

                if (isGoodCangOrder) {
                    GoodCangOrder goodCangOrder = this.constructGoodCangOrderData(sysOrder);//构造谷仓发货数据
                    systemOrderService.transactionFrozenAmountAndDeliverGoodGoodCang(sysOrder, orderRequestVo, goodCangOrder, username);
                } else {
                    Map<String, Object> erpOrderMap = this.constructERPOrderData(sysOrder);//构造ERP发货数据
                    systemOrderService.transactionFrozenAmountAndDeliverGoodERP(sysOrder, orderRequestVo, erpOrderMap, username);
                }
            } catch (Exception e) {
                _log.error("________________此订单 {} 执行冻结订单金额发货事务失败________{}_______", sysOrderId, e);
                String exceptionString = "执行冻结订单金额发货事务失败," + e.getMessage();
                systemOrderCommonService.updateDeliverExceptionInfo(sysOrder, exceptionString, null, (byte) 10, username);
                sysOrder.setWarehouseShipException(Utils.translation(exceptionString));
                exceptionDeliverSysOrderList.add(sysOrder);
                continue;
            }
            _log.info("____________________推送发货订单 {} 至ERP成功____________________", sysOrderId);
            lock.unlock();
        }
        return exceptionDeliverSysOrderList;
    }

    private boolean judgeSplittedOrMergedCanDeliverGood(SysOrder sysOrder) {
        String mainOrderId = sysOrder.getMainOrderId();
        byte splittedOrMerged = sysOrder.getSplittedOrMerged();
        if (StringUtils.isNotEmpty(mainOrderId)) {//子订单
            if (splittedOrMerged == 2) {
                return false;
            }
            return true;
        } else {
            if (splittedOrMerged == 1) {
                return false;
            }
            return true;
        }
    }

    @Override
    public String replenishDeliverGood(SysOrder sysOrder) throws Exception {
        this.validateRequiredField(new ArrayList<SysOrder>() {{
            add(sysOrder);
        }});
        boolean isSupportDeliverMethod = systemOrderCommonService.judgeSupportDeliverMethod(sysOrder);
        if (!isSupportDeliverMethod) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单中商品不支持此邮寄方式。。。");
        }
//        String warehouseCode = sysOrder.getDeliveryWarehouseCode();
//        if (warehouseCode.startsWith("GC_")) {
//            GoodCangOrder goodCangOrder = this.constructGoodCangOrderData(sysOrder);//构造谷仓发货数据
//            String referenceId = goodCangService.deliverGoodToGoodCang(goodCangOrder);
//            return referenceId;
//        }
        boolean isGoodCangOrder = systemOrderCommonService.isGoodCangWarehouse(sysOrder.getDeliveryWarehouseId());

        if (isGoodCangOrder) {
            GoodCangOrder goodCangOrder = this.constructGoodCangOrderData(sysOrder);//构造谷仓发货数据
            String referenceId = goodCangService.deliverGoodToGoodCang(goodCangOrder);
            return referenceId;
        } else {
            Map<String, Object> erpOrderMap = this.constructERPOrderData(sysOrder);
            remoteErpService.orderReceive(erpOrderMap);
            return "SUCCESS";
        }
    }

    private void validateRequiredField(List<SysOrder> sysOrders) {
        for (SysOrder sysOrder : sysOrders) {
            //todo 订单对象改变了
            //CheckOrderUtils.validateSysOrderDataForDeliverGood(sysOrder);
        }
    }

    @Override
    public SysOrderNew getSysOrderNew(String sysOrderId) {
        SysOrderNew sysOrderNew = sysOrderNewMapper.queryOrderByOrderId(sysOrderId);

        if (null == sysOrderNew) {
            return null;
        }

        List<SysOrderDetail> sysOrderDetailList = sysOrderDetailMapper.querySysOrderDetailByOrderId(sysOrderId);
        List<SysOrderDetail> sysOrderDetails = new ArrayList<>();
        for (SysOrderDetail sysOrderDetail: sysOrderDetailList){
            if (StringUtils.isNotBlank(sysOrderDetail.getSku())){
                sysOrderDetails.add(sysOrderDetail);
            }
        }
        sysOrderNew.setSysOrderDetails(sysOrderDetails);

        String operateStatus = sysOrderNew.getSplittedOrMerged();
        List<SysOrderPackage> sysOrderPackageList = new ArrayList<>();
        if (Objects.equals(operateStatus, OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
            // 合并包裹订单，需要用no_show 获取合并之前的包裹
            sysOrderPackageList = sysOrderPackageMapper.queryOrderNoShowPackageByOrderId(sysOrderId);
        } else {
            // 普通订单 和 拆分包裹订单，需要用show获取包裹
            sysOrderPackageList = sysOrderPackageMapper.queryOrderPackageByOrderId(sysOrderId);
        }

        for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {
            List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackageDetailMapper
                    .queryOrderPackageDetails(sysOrderPackage.getOrderTrackId());
            List<SysOrderPackageDetail> sysOrderPackageDetails = new ArrayList<>();
            for (SysOrderPackageDetail sysOrderPackageDetail: sysOrderPackageDetailList){
                if (StringUtils.isNotBlank(sysOrderPackageDetail.getSku())){
                    sysOrderPackageDetails.add(sysOrderPackageDetail);
                }
            }
            sysOrderPackage.setSysOrderPackageDetailList(sysOrderPackageDetails);
        }

        sysOrderNew.setSysOrderPackageList(sysOrderPackageList);
        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderReceiveAddressMapper.queryAddressByOrderId(sysOrderId);
        sysOrderNew.setSysOrderReceiveAddress(sysOrderReceiveAddress);
        return sysOrderNew;
    }

    /**
     * 构造erp订单数据
     *
     * @param sysOrderNew        订单
     * @param sysOrderPackage    包裹
     * @param sysOrderDetailList 所有的商品列表
     * @return
     * @throws Exception
     */
    public Map<String, Object> constructERPOrderDataNew(SysOrderNew sysOrderNew,
                                                        SysOrderPackage sysOrderPackage,
                                                        List<SysOrderDetail> sysOrderDetailList) throws Exception {
        Map<String, Object> map = new HashMap<>(3);
        ERPOrder erpOrder = new ERPOrder();
        List<ERPOrderDetail> erpOrderDetails = new ArrayList<>();
        ERPShipping erpShipping = new ERPShipping();

        erpOrder.setOrder_number(sysOrderNew.getSysOrderId());
        erpOrder.setBuyer(sysOrderNew.getBuyerName() == null ? sysOrderNew.getSellerPlAccount() : sysOrderNew.getBuyerName());
        erpOrder.setBuyer_id(sysOrderNew.getBuyerUserId() == null ? sysOrderNew.getSellerPlAccount() : sysOrderNew.getBuyerUserId());

        // 收货地址
        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderNew.getSysOrderReceiveAddress();
        erpOrder.setConsignee(sysOrderReceiveAddress.getShipToName());
        erpOrder.setCountry_code(sysOrderReceiveAddress.getShipToCountry());
        erpOrder.setCity(sysOrderReceiveAddress.getShipToCity());
        erpOrder.setProvince(StringUtils.isBlank(sysOrderReceiveAddress.getShipToState()) ?
                sysOrderReceiveAddress.getShipToCity() : sysOrderReceiveAddress.getShipToState());
        erpOrder.setAddress(sysOrderReceiveAddress.getShipToAddrStreet1());
        erpOrder.setAddress2(sysOrderReceiveAddress.getShipToAddrStreet2() == null ? "" : sysOrderReceiveAddress.getShipToAddrStreet2());
        erpOrder.setZipcode(sysOrderReceiveAddress.getShipToPostalCode());
        erpOrder.setTel(sysOrderReceiveAddress.getShipToPhone() == null ? "" : sysOrderReceiveAddress.getShipToPhone());
        erpOrder.setMobile(sysOrderReceiveAddress.getShipToPhone() == null ? "" : sysOrderReceiveAddress.getShipToPhone());
        erpOrder.setEmail(sysOrderReceiveAddress.getShipToEmail() == null ? "" : sysOrderReceiveAddress.getShipToEmail());

        //交易号，财务模块实际付款返回的。可不传
        erpOrder.setPay_id("");
        erpOrder.setPay_fee(sysOrderNew.getTotal().doubleValue());//默认币种为CNY
        String deliverDeadline = sysOrderNew.getDeliverDeadline();
        if (StringUtils.isNotBlank(deliverDeadline)) {
            erpOrder.setUploaded_deadline(Integer.valueOf(String.valueOf(
                    TimeUtil.stringToDate(sysOrderNew.getDeliverDeadline(), "yyyy-MM-dd HH:mm:ss").getTime() / 1000)));
        } else {
            erpOrder.setUploaded_deadline(Integer.valueOf(String.valueOf(new Date().getTime() / 1000)));
        }
        //系统订单总价
        erpOrder.setOrder_amount(sysOrderNew.getOrderAmount().doubleValue());

        String orderTime = sysOrderNew.getOrderTime();
        if (StringUtils.isNotBlank(orderTime)) {
            //下单时间
            erpOrder.setOrder_time(Integer.valueOf(String.valueOf(
                    TimeUtil.stringToDate(orderTime, "yyyy-MM-dd HH:mm:ss").getTime() / 1000)));
        }
        erpOrder.setCreate_time(Integer.valueOf(String.valueOf(new Date().getTime() / 1000)));//记录创建时间,取当前时间
        erpOrder.setPay_time(Integer.valueOf(String.valueOf(new Date().getTime() / 1000)));//支付时间
        erpOrder.setMessage(sysOrderNew.getBuyerCheckoutMessage() == null ? "" : sysOrderNew.getBuyerCheckoutMessage());
        erpOrder.setChannel_id(31);//渠道ID（固定传31）
        erpOrder.setChannel_account_id(sysOrderNew.getSellerPlId());//渠道账号ID（就是分销商ID）
        erpOrder.setChannel_account_code("brandslink");//渠道账号简称
        erpOrder.setChannel_order_id(sysOrderNew.getSellerPlId() == null ? 1 : sysOrderNew.getSellerPlId());//渠道订单ID,取卖家品连ID
        erpOrder.setChannel_order_number(sysOrderPackage.getOrderTrackId());//order_track_id订单跟踪号//每次推送都新生成一个，在系统订单表里保存
        erpOrder.setRelated_order_id(sysOrderNew.getSellerPlId() == null ? 1 : sysOrderNew.getSellerPlId());//关联订单ID,取来源订单ID,取卖家品连ID
        erpOrder.setIs_wish_express(1);//Wish订单是否海外仓（0-是 1-否）
        erpOrder.setCurrency_code(rateCurrency);
        erpOrder.setChannel_shipping_free(sysOrderNew.getEstimateShipCost().doubleValue());//渠道运费,取系统预估物流费
        erpOrder.setChannel_shipping_discount(sysOrderNew.getEstimateShipCost().doubleValue());//渠道折扣运费,取系统预估物流费
        erpOrder.setChannel_cost(0.00);//渠道手续费
        erpOrder.setBuyer_selected_logistics(sysOrderPackage.getDeliveryMethodCode());//买家选择的邮寄方式
        erpOrder.setRequires_delivery_confirmation(0);//Wish订单是否妥投 1-是 0-否
        erpOrder.setSite_code(sysOrderNew.getMarketplaceId());//站点简称
        erpOrder.setGoods_amount(sysOrderNew.getOrderAmount().doubleValue());//商品总额:商品单价x数量再取和

        List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackage.getSysOrderPackageDetailList();
        for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetailList) {
            ERPOrderDetail detail = new ERPOrderDetail();
            SysOrderDetail sysOrderDetail = null;

            String plSku = sysOrderPackageDetail.getSku();

            for (SysOrderDetail orderDetail : sysOrderDetailList) {
                if (Objects.equals(plSku, orderDetail.getSku())) {
                    sysOrderDetail = orderDetail;
                    break;
                }
            }

            if (null == sysOrderDetail) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹商品信息错误");
            }

            //渠道产品ID,传系统订单项商品ID:各平台分别设置
            String channel_item_id = "";
            if (Integer.valueOf(sysOrderNew.getOrderSource()) == OrderSourceCovertToGoodCandPlatformEnum.EBAY.getOrderSource()) {
                if (null != sysOrderPackageDetail.getSourceOrderLineItemId()) {
                    List<String> list = Arrays.asList(sysOrderPackageDetail.getSourceOrderLineItemId().split(Constants.SplitSymbol.HASH_TAG));
                    List<EbayOrderDetail> ebayOrderDetails = ebayOrderDetailMapper.queryBatchEbayOrderDetailByOrderLineItemId(list);
                    if (null != ebayOrderDetails && ebayOrderDetails.size() > 0) {
                        channel_item_id = ebayOrderDetails.get(0).getItemId();
                    }
                }
            } else if (Integer.valueOf(sysOrderNew.getOrderSource()) == OrderSourceCovertToGoodCandPlatformEnum.AMAZON.getOrderSource()) {
                if (null != sysOrderPackageDetail.getSourceOrderLineItemId()) {
                    List<String> list = Arrays.asList(sysOrderPackageDetail.getSourceOrderLineItemId().split(Constants.SplitSymbol.HASH_TAG));
                    List<AmazonOrderDetail> amazonOrderDetails = amazonOrderDetailMapper.queryBatchAmazonOrderDetailByOrderLineItemId(list);
                    if (null != amazonOrderDetails && amazonOrderDetails.size() > 0) {
                        channel_item_id = amazonOrderDetails.get(0).getAsin();
                    }
                }
            } else if (Integer.valueOf(sysOrderNew.getOrderSource()) == OrderSourceCovertToGoodCandPlatformEnum.ALIEXPRESS.getOrderSource()) {
                if (null != sysOrderPackageDetail.getSourceOrderLineItemId()) {
                    List<String> list = Arrays.asList(sysOrderPackageDetail.getSourceOrderLineItemId().split(Constants.SplitSymbol.HASH_TAG));
                    List<AliexpressOrderChild> aliexpressOrderChildren = aliexpressOrderChildMapper.queryBatchAliexpressDetailByOrderItemId(list);
                    if (null != aliexpressOrderChildren && aliexpressOrderChildren.size() > 0) {
                        channel_item_id = ObjectUtils.isEmpty(aliexpressOrderChildren.get(0).getProductId()) ? null : aliexpressOrderChildren.get(0).getProductId().toString();
                    }
                }
            }
            detail.setChannel_item_id(channel_item_id);//渠道产品ID,传系统订单项商品ID

            detail.setChannel_sku(sysOrderDetail.getSupplierSku());//渠道sku,传供应商SKU
            detail.setChannel_sku_title(sysOrderDetail.getSupplierSkuTitle());//渠道sku标题.传供应商的商品名称：就是系统商品名称
            detail.setChannel_sku_price((sysOrderDetail.getSupplierSkuPrice() == null ?
                    new BigDecimal(0) : sysOrderDetail.getSupplierSkuPrice()).toString());//渠道sku价格,传供应商的商品单价
            detail.setSku_quantity(sysOrderPackageDetail.getSkuQuantity());//渠道sku数量
            String skuUrl = "";
            if (StringUtils.isNotBlank(sysOrderPackageDetail.getSkuUrl())){
                skuUrl = sysOrderPackageDetail.getSkuUrl();
                if (skuUrl.contains("|")){
                    skuUrl = skuUrl.substring(0, skuUrl.indexOf("|"));
                }
            }
            detail.setChannel_item_link(skuUrl);//渠道产品链接
            detail.setChanel_currency_code(rateCurrency);//渠道价格货币简写
            detail.setChannel_sku_shipping_free(0.00);//渠道sku运费
            detail.setColor("");//渠道产品颜色（只限于wish）
            detail.setSize("");//渠道产品尺寸（只限于wish）
            //渠道交易号,目前只有eBay有
            if (Integer.valueOf(sysOrderNew.getOrderSource()) == OrderSourceCovertToGoodCandPlatformEnum.EBAY.getOrderSource()) {
                List<String> orderLineItemIdList = Arrays.asList(sysOrderDetail.getSourceOrderLineItemId().split(Constants.SplitSymbol.HASH_TAG));
                List<EbayOrderDetail> ebayOrderDetails = ebayOrderDetailMapper.queryBatchEbayOrderDetailByOrderLineItemId(orderLineItemIdList);
                if (null != ebayOrderDetails && ebayOrderDetails.size() > 0) {
                    detail.setTransaction_id(ebayOrderDetails.get(0).getTransactionId());
                }
            } else {
                detail.setTransaction_id("0");
            }

            erpOrderDetails.add(detail);
        }

        erpShipping.setWarehouse_code(sysOrderPackage.getDeliveryWarehouseCode());//发货仓库CODE
        erpShipping.setShipping_code(sysOrderPackage.getDeliveryMethodCode());//发货邮寄方式CODE
        map.put("order", erpOrder);
        map.put("order_detail", erpOrderDetails);
        map.put("shipping", erpShipping);
        return map;
    }

    public Map<String, Object> constructERPOrderData(SysOrder sysOrder) throws Exception {
        Map<String, Object> map = new HashMap<>(3);
        ERPOrder erpOrder = new ERPOrder();
        List<ERPOrderDetail> erpOrderDetails = new ArrayList<>();
        ERPShipping erpShipping = new ERPShipping();
//        String exchangeRate = rateUtil.remoteExchangeRateByCurrencyCode("USD", "CNY");
//        _log.error("__________获取USD------>RMB的汇率为__________{}__________", exchangeRate);

        erpOrder.setOrder_number(sysOrder.getSysOrderId());
        erpOrder.setBuyer(sysOrder.getBuyerName() == null ? sysOrder.getSellerPlAccount() : sysOrder.getBuyerName());
        erpOrder.setBuyer_id(sysOrder.getBuyerUserId() == null ? sysOrder.getSellerPlAccount() : sysOrder.getBuyerUserId());
        erpOrder.setConsignee(sysOrder.getShipToName());
        erpOrder.setCountry_code(sysOrder.getShipToCountry());
        erpOrder.setCity(sysOrder.getShipToCity());
        erpOrder.setProvince(StringUtils.isBlank(sysOrder.getShipToState()) ? sysOrder.getShipToCity() : sysOrder.getShipToState());
        erpOrder.setAddress(sysOrder.getShipToAddrStreet1());
        erpOrder.setAddress2(sysOrder.getShipToAddrStreet2() == null ? "" : sysOrder.getShipToAddrStreet2());
        erpOrder.setZipcode(sysOrder.getShipToPostalCode());
        erpOrder.setTel(sysOrder.getShipToPhone() == null ? "" : sysOrder.getShipToPhone());
        erpOrder.setMobile(sysOrder.getShipToPhone() == null ? "" : sysOrder.getShipToPhone());
        erpOrder.setEmail(sysOrder.getShipToEmail() == null ? "" : sysOrder.getShipToEmail());
        erpOrder.setPay_id("");//交易号，财务模块实际付款返回的。可不传
        erpOrder.setPay_fee(Double.valueOf(sysOrder.getTotal().doubleValue()));//默认币种为CNY
        String deliverDeadline = sysOrder.getDeliverDeadline();
        if (StringUtils.isNotBlank(deliverDeadline)) {
            erpOrder.setUploaded_deadline(Integer.valueOf(String.valueOf(TimeUtil.stringToDate(sysOrder.getDeliverDeadline(), "yyyy-MM-dd HH:mm:ss").getTime() / 1000)));
        } else {
            erpOrder.setUploaded_deadline(Integer.valueOf(String.valueOf(new Date().getTime() / 1000)));
        }
        erpOrder.setOrder_amount(Double.valueOf(sysOrder.getOrderAmount().doubleValue()));//系统订单总价

        String orderTime = sysOrder.getOrderTime();
        if (StringUtils.isNotBlank(orderTime)) {
            //下单时间
            erpOrder.setOrder_time(Integer.valueOf(String.valueOf(TimeUtil.stringToDate(orderTime, "yyyy-MM-dd HH:mm:ss").getTime() / 1000)));
        }
        erpOrder.setCreate_time(Integer.valueOf(String.valueOf(new Date().getTime() / 1000)));//记录创建时间,取当前时间
        erpOrder.setPay_time(Integer.valueOf(String.valueOf(new Date().getTime() / 1000)));//支付时间
        erpOrder.setMessage(sysOrder.getBuyerCheckoutMessage() == null ? "" : sysOrder.getBuyerCheckoutMessage());
        erpOrder.setChannel_id(31);//渠道ID（固定传31）
        erpOrder.setChannel_account_id(sysOrder.getSellerPlId());//渠道账号ID（就是分销商ID）
        erpOrder.setChannel_account_code("brandslink");//渠道账号简称
        erpOrder.setChannel_order_id(sysOrder.getSellerPlId() == null ? 1 : sysOrder.getSellerPlId());//渠道订单ID,取卖家品连ID
        erpOrder.setChannel_order_number(sysOrder.getOrderTrackId());//order_track_id订单跟踪号//每次推送都新生成一个，在系统订单表里保存
        erpOrder.setRelated_order_id(sysOrder.getSellerPlId() == null ? 1 : sysOrder.getSellerPlId());//关联订单ID,取来源订单ID,取卖家品连ID
        erpOrder.setIs_wish_express(1);//Wish订单是否海外仓（0-是 1-否）
        erpOrder.setCurrency_code(rateCurrency);
        erpOrder.setChannel_shipping_free(Double.valueOf(sysOrder.getEstimateShipCost().doubleValue()));//渠道运费,取系统预估物流费
        erpOrder.setChannel_shipping_discount(Double.valueOf(sysOrder.getEstimateShipCost().doubleValue()));//渠道折扣运费,取系统预估物流费
        erpOrder.setChannel_cost(0.00);//渠道手续费
        erpOrder.setBuyer_selected_logistics(sysOrder.getDeliveryMethodCode());//买家选择的邮寄方式
        erpOrder.setRequires_delivery_confirmation(0);//Wish订单是否妥投 1-是 0-否
        erpOrder.setSite_code(sysOrder.getMarketplaceId());//站点简称
        erpOrder.setGoods_amount(Double.valueOf(sysOrder.getOrderAmount().doubleValue()));//商品总额:商品单价x数量再取和

        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            ERPOrderDetail detail = new ERPOrderDetail();
            detail.setChannel_item_id(sysOrderDetail.getOrderLineItemId());//渠道产品ID,传系统订单项商品ID
            detail.setChannel_sku(sysOrderDetail.getSupplierSku());//渠道sku,传供应商SKU
            detail.setChannel_sku_title(sysOrderDetail.getSupplierSkuTitle());//渠道sku标题.传供应商的商品名称：就是系统商品名称
            detail.setChannel_sku_price((sysOrderDetail.getSupplierSkuPrice() == null ? new BigDecimal(0) : sysOrderDetail.getSupplierSkuPrice()).toString());//渠道sku价格,传供应商的商品单价
            detail.setSku_quantity(sysOrderDetail.getSkuQuantity());//渠道sku数量
            detail.setChannel_item_link(sysOrderDetail.getItemUrl() == null ? "" : sysOrderDetail.getItemUrl());//渠道产品链接
            detail.setChanel_currency_code(rateCurrency);//渠道价格货币简写
            detail.setChannel_sku_shipping_free(0.00);//渠道sku运费
            detail.setColor("");//渠道产品颜色（只限于wish）
            detail.setSize("");//渠道产品尺寸（只限于wish）
            erpOrderDetails.add(detail);
        }

        erpShipping.setWarehouse_code(sysOrder.getDeliveryWarehouseCode());//发货仓库CODE
        erpShipping.setShipping_code(sysOrder.getDeliveryMethodCode());//发货邮寄方式CODE
        map.put("order", erpOrder);
        map.put("order_detail", erpOrderDetails);
        map.put("shipping", erpShipping);
        return map;
    }

    public GoodCangOrder constructGoodCangOrderDataNew(SysOrderNew sysOrderNew,
                                                       SysOrderPackage sysOrderPackage) {
        GoodCangOrder order = new GoodCangOrder();
        order.setWarehouseId(String.valueOf(sysOrderPackage.getDeliveryWarehouseId()));
        order.setReference_no(sysOrderPackage.getOrderTrackId());
        Byte orderSource = sysOrderNew.getOrderSource();
        order.setPlatform(OrderSourceCovertToGoodCandPlatformEnum.getGoodCangPlatformCode(orderSource));
        order.setShipping_method(sysOrderPackage.getDeliveryMethodCode());
        order.setWarehouse_code(sysOrderPackage.getDeliveryWarehouseCode());

        SysOrderReceiveAddress sysOrderReceiveAddress = sysOrderNew.getSysOrderReceiveAddress();

        order.setCountry_code(sysOrderReceiveAddress.getShipToCountry());
        order.setProvince(StringUtils.isBlank(sysOrderReceiveAddress.getShipToState()) ?
                sysOrderReceiveAddress.getShipToCity() : sysOrderReceiveAddress.getShipToState());
        order.setCity(sysOrderReceiveAddress.getShipToCity());
        order.setCompany("");
        order.setAddress1(sysOrderReceiveAddress.getShipToAddrStreet1());
        order.setAddress2(sysOrderReceiveAddress.getShipToAddrStreet2());
        order.setZipcode(sysOrderReceiveAddress.getShipToPostalCode());
        order.setDoorplate("");
        order.setName(sysOrderReceiveAddress.getShipToName());
        order.setCell_phone("");
        order.setPhone(sysOrderReceiveAddress.getShipToPhone());
        order.setEmail(sysOrderReceiveAddress.getShipToEmail());
        order.setOrder_desc(sysOrderNew.getBuyerCheckoutMessage());
        order.setVerify(1);
        order.setIs_shipping_method_not_allow_update(1);
        order.setIs_signature(0);
        order.setIs_insurance(0);
        order.setInsurance_value(0);
        order.setFba_shipment_id("");
        order.setFba_shipment_id_create_time("");
        order.setIs_change_label(0);
        order.setAge_detection(0);
        List<GoodCangOrderItem> items = new ArrayList<>();

        List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackage.getSysOrderPackageDetailList();
        for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetailList) {
            GoodCangOrderItem item = new GoodCangOrderItem();
            item.setProduct_sku(sysOrderPackageDetail.getSku());
            item.setQuantity(sysOrderPackageDetail.getSkuQuantity());
            item.setTransaction_id("");
            item.setItem_id("");
            item.setFba_product_code("");
            items.add(item);
        }
        order.setItems(items);
        return order;
    }

    public GoodCangOrder constructGoodCangOrderData(SysOrder sysOrder) {
        GoodCangOrder order = new GoodCangOrder();
        order.setWarehouseId(sysOrder.getDeliveryWarehouseId());
        order.setReference_no(sysOrder.getOrderTrackId());
        Byte orderSource = sysOrder.getOrderSource();
        switch (orderSource) {
            case (byte) 4:
                order.setPlatform("EBAY");
                break;
            case (byte) 5:
                order.setPlatform("AMAZON");
                break;
            case (byte) 6:
                order.setPlatform("ALIEXPRESS");
                break;
            case (byte) 7:
                order.setPlatform("WISH");
                break;
            default:
                order.setPlatform("OTHER");
        }
        order.setShipping_method(sysOrder.getDeliveryMethodCode());
        order.setWarehouse_code(sysOrder.getDeliveryWarehouseCode());
        order.setCountry_code(sysOrder.getShipToCountry());
        order.setProvince(StringUtils.isBlank(sysOrder.getShipToState()) ? sysOrder.getShipToCity() : sysOrder.getShipToState());
        order.setCity(sysOrder.getShipToCity());
        order.setCompany("");
        order.setAddress1(sysOrder.getShipToAddrStreet1());
        order.setAddress2(sysOrder.getShipToAddrStreet2());
        order.setZipcode(sysOrder.getShipToPostalCode());
        order.setDoorplate("");
        order.setName(sysOrder.getShipToName());
        order.setCell_phone("");
        order.setPhone(sysOrder.getShipToPhone());
        order.setEmail(sysOrder.getShipToEmail());
        order.setOrder_desc(sysOrder.getBuyerCheckoutMessage());
        order.setVerify(1);
        order.setIs_shipping_method_not_allow_update(1);
        order.setIs_signature(0);
        order.setIs_insurance(0);
        order.setInsurance_value(0);
        order.setFba_shipment_id("");
        order.setFba_shipment_id_create_time("");
        order.setIs_change_label(0);
        order.setAge_detection(0);
        List<GoodCangOrderItem> items = new ArrayList<>();
        for (SysOrderDetail detail : sysOrder.getSysOrderDetails()) {
            GoodCangOrderItem item = new GoodCangOrderItem();
            item.setProduct_sku(detail.getSku());
            item.setQuantity(detail.getSkuQuantity());
            item.setTransaction_id(detail.getOrderLineItemId());
            item.setItem_id(detail.getItemId() + "");
            item.setFba_product_code("");
            items.add(item);
        }
        order.setItems(items);
        return order;
    }

    public OrderRequestVo constructOrderRequestVoData(SysOrder sysOrder) {
        OrderRequestVo vo = new OrderRequestVo();
        vo.setSellerName(sysOrder.getSellerPlAccount());
        vo.setProductAmount(sysOrder.getOrderAmount());
        vo.setLogisticsFare(sysOrder.getEstimateShipCost());
        vo.setSellerId(sysOrder.getSellerPlId());//1
        vo.setOrderNo(sysOrder.getSysOrderId());
        vo.setSellerAccount(sysOrder.getSellerPlAccount());
        vo.setLogisticsId(sysOrder.getShippingCarrierUsedCode());
        vo.setLogisticsName(sysOrder.getShippingCarrierUsed() == null ? "——" : sysOrder.getShippingCarrierUsed());
        vo.setStorageId(sysOrder.getDeliveryWarehouseCode());
        vo.setStorageName(sysOrder.getDeliveryWarehouse() == null ? "——" : sysOrder.getDeliveryWarehouse());
        vo.setSupplyCompanyId(sysOrder.getSupplyChainCompanyId());
        vo.setSupplyCompanyName(sysOrder.getSupplyChainCompanyName());
        vo.setShopId(sysOrder.getPlatformShopId());
        vo.setShopType(sysOrder.getShopType());
        vo.setCurrency(rateCurrency);
        vo.setPlatformTotal(sysOrder.getCommoditiesAmount() == null ? new BigDecimal(0) : sysOrder.getCommoditiesAmount());//TODO

        List<OrderItemVo> orderItems = new ArrayList<>();
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(sysOrderDetail.getSysOrderId());
            orderItemVo.setItemNo(sysOrderDetail.getSupplierSku());
            orderItemVo.setSellerId(sysOrder.getSellerPlId());//1
            orderItemVo.setSupplierId(sysOrderDetail.getSupplierId().intValue());
            orderItemVo.setSupplierName(sysOrderDetail.getSupplierName());
            orderItemVo.setProductAmount(sysOrderDetail.getItemPrice().multiply(BigDecimal.valueOf(sysOrderDetail.getSkuQuantity())));
            orderItemVo.setSupplyCompanyId(sysOrderDetail.getSupplyChainCompanyId());
            orderItemVo.setSupplyCompanyName(sysOrderDetail.getSupplyChainCompanyName());
            orderItemVo.setProductName(sysOrderDetail.getSkuTitle());
            String fareTypeAmount = sysOrderDetail.getFareTypeAmount();
            if (StringUtils.isNotBlank(fareTypeAmount)) {
                String[] split = fareTypeAmount.split("#");
                if ("1".equals(split[0])) {
                    orderItemVo.setFareType("固定金额");
                    orderItemVo.setFare(new BigDecimal(split[1]));
                } else {
                    orderItemVo.setFareType("百分比");
                    orderItemVo.setFare(new BigDecimal(split[1]));
                }
            }
            orderItemVo.setSkuQuantity(sysOrderDetail.getSkuQuantity());
            orderItemVo.setItemPrice(sysOrderDetail.getItemPrice());
            orderItems.add(orderItemVo);
        }
        vo.setOrderItems(orderItems);
        // TODO: 应财务要求，需要先整理业务逻辑，先默认false传给财务
        if (Constants.SysOrder.FREE_FREIGHT.equals(sysOrder.getFreeFreightType()) ||
                Constants.SysOrder.PART_FREE_FREIGHT.equals(sysOrder.getFreeFreightType())) {
            vo.setFreeShipping(true);
        } else {
            vo.setFreeShipping(false);
        }
        return vo;
    }

    @Override
    @Transactional
    @TxTransaction(isStart = true)
    public void transactionFrozenAmountAndDeliverGoodERP(SysOrder sysOrder, OrderRequestVo orderRequestVo, Map<String, Object> erpOrderMap, String username) throws Exception {
        String sysOrderId = sysOrder.getSysOrderId();
        if (sysOrder.getOrderDeliveryStatus() != (byte) 4) {//已拦截的订单发货时不用冻结订单款项
            String result = remoteFinanceService.generate(orderRequestVo);
            _log.info("订单发货调用财务冻结金额返回结果为: {}", result);
            String data = Utils.returnRemoteResultDataString(result, "发货调用财务服务出错。。。");
            JSONObject object = JSONObject.parseObject(data);
            String serialNo = (String) object.get("serialNo");
            String payType = (String) object.get("payType");
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.FREEZE_SUCCESS.freezeSuccess(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), username));
            _log.info("冻结卖家账户订单金额成功，冻结订单号为: {}", sysOrderId);
            byte payMethod = 0;
            if ("USD账户".equals(payType)) {
                payMethod = 1;
            } else if ("微信".equals(payType)) {
                payMethod = 2;
            } else if ("支付宝".equals(payType)) {
                payMethod = 3;
            } else if ("线下支付".equals(payType)) {
                payMethod = 4;
            }
            sysOrder.setPayId(serialNo);
            sysOrder.setPayStatus((byte) 11);
            sysOrder.setPayMethod(payMethod);
        }
        sysOrder.setSysOrderId(sysOrderId);
        sysOrder.setOrderDeliveryStatus(OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.getValue());
        sysOrder.setUpdateBy(username);
        sysOrderMapper.updateDataBySysOrderIdSelective(sysOrder);
        remoteErpService.orderReceive(erpOrderMap);
//        orderMessageSender.sendOrderStockOut(sysOrder.getSellerPlAccount(), sysOrderId, MessageEnum.ORDER_DELIVERY_NOTICE, null);
        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PICKING_CARGO.pickingCargo(sysOrderId, null),
                OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), username));
    }

    @Override
    @Transactional
    @TxTransaction(isStart = true)
    public void transactionFrozenAmountAndDeliverGoodGoodCang(SysOrder sysOrder, OrderRequestVo orderRequestVo, GoodCangOrder goodCangOrder, String username) throws Exception {
        String sysOrderId = sysOrder.getSysOrderId();
        if (sysOrder.getOrderDeliveryStatus() != (byte) 4) {//已拦截的订单发货时不用冻结订单款项
            String result = remoteFinanceService.generate(orderRequestVo);
            _log.info("____________订单发货调用财务冻结金额返回结果为_________{}____________", result);
            String data = Utils.returnRemoteResultDataString(result, "发货调用财务服务出错。。。");
            JSONObject object = JSONObject.parseObject(data);
            String serialNo = (String) object.get("serialNo");
            String payType = (String) object.get("payType");
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.FREEZE_SUCCESS.freezeSuccess(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), username));
            _log.info("___________冻结卖家账户订单金额成功，冻结订单号为 {}_____________", sysOrderId);
            byte payMethod = 0;
            if ("USD账户".equals(payType)) {
                payMethod = 1;
            } else if ("微信".equals(payType)) {
                payMethod = 2;
            } else if ("支付宝".equals(payType)) {
                payMethod = 3;
            } else if ("线下支付".equals(payType)) {
                payMethod = 4;
            }
            sysOrder.setPayId(serialNo);
            sysOrder.setPayStatus((byte) 11);
            sysOrder.setPayMethod(payMethod);
        }
        sysOrder.setSysOrderId(sysOrderId);
        sysOrder.setOrderDeliveryStatus(OrderHandleEnum.OrderDeliveryStatus.PICKING_CARGO.getValue());
        sysOrder.setUpdateBy(username);
        String orderTrackId = sysOrder.getOrderTrackId();
        if (orderTrackId.startsWith(Constants.DistributionType.DistributionType_QT)) {
            sysOrderMapper.insertSelective(sysOrder);
            List<SysOrderDetail> detailList = sysOrder.getSysOrderDetails();
            for (SysOrderDetail detail : detailList) {
                sysOrderDetailMapper.insertSelective(detail);
            }
        } else {
            sysOrderMapper.updateDataBySysOrderIdSelective(sysOrder);
        }
        String referenceId = goodCangService.deliverGoodToGoodCang(goodCangOrder);
        SysOrder order = new SysOrder();
        order.setSysOrderId(sysOrderId);
        order.setReferenceId(referenceId);
        sysOrderMapper.updateBySysOrderIdSelective(order);
//        orderMessageSender.sendOrderStockOut(sysOrder.getSellerPlAccount(), sysOrderId, MessageEnum.ORDER_DELIVERY_NOTICE, null);
        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PICKING_CARGO.pickingCargo(sysOrderId, null),
                OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), username));
    }

    @Override
    public void wareHouseDeliverCallBackNew(WareHouseDeliverCallBack deliverCallBack) throws Exception {
        _log.info("仓库回调数据为: {}", FastJsonUtils.toJsonString(deliverCallBack));
        String warehouseType = deliverCallBack.getWarehouseType();
        String orderTrackId = deliverCallBack.getOrderTrackId();
        String warehouseShipException = deliverCallBack.getWarehouseShipException();
        String speed = deliverCallBack.getSpeed();
        if (orderTrackId.startsWith("SH") || orderTrackId.startsWith("sh")) {
            _log.info("包裹{} 是售后包裹", orderTrackId);
            this.pushCMSOrderDeliverInfo(deliverCallBack);
            return;
        }

        // TODO【作废、推送失败】订单处理
        if (deliverCallBack.getSpeed().equals(OrderPackageStatusEnum.PUSH_FAIL.getValue())){
            _log.error("erp返回作废、创建失败订单============={}", JSONObject.toJSONString(deliverCallBack));
            try {
                sysOrderExceptionHandelService.cancellationOrderHandel(deliverCallBack.getOrderTrackId());
            } catch (Exception e) {
                _log.error("erp返回作废、创建失败订单处理失败============={}", deliverCallBack.getOrderTrackId());
            }
            return;
        }

        SysOrderPackage deliveredPackage = sysOrderPackageMapper.queryOrderPackageByPk(orderTrackId);
        if (null == deliveredPackage) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹号查询不到数据");
        }

        String packageStatus = deliveredPackage.getPackageStatus();
        if (Objects.equals(packageStatus, OrderPackageStatusEnum.WAIT_PUSH.getValue())
                || Objects.equals(packageStatus, OrderPackageStatusEnum.PUSH_FAIL.getValue())
                || Objects.equals(packageStatus, OrderPackageStatusEnum.DELIVERED.getValue())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "只有已发货的包裹才能更新发货状态");
        }

        List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackageDetailMapper.queryOrderPackageDetails(orderTrackId);
        deliveredPackage.setSysOrderPackageDetailList(sysOrderPackageDetailList);

        List<SysOrderNew> sysOrderNewList = this.getSysOrderNewListByPackage(deliveredPackage);
        if (StringUtils.isNotEmpty(warehouseShipException)) {
            this.dealPackageDeliverException(warehouseShipException, warehouseType, speed, sysOrderNewList, deliveredPackage);
            return;
        }

        try {
            if (Objects.equals(warehouseType, Constants.WarehouseType.ERP)) {
                this.rondafulERPWareHouseDeliverCallBackNew(deliverCallBack, sysOrderNewList, deliveredPackage);
            } else if (Objects.equals(warehouseType, Constants.WarehouseType.GC)) {
                this.goodCangWareHouseDeliverCallBackNew(deliverCallBack, sysOrderNewList, deliveredPackage);
            } else if (Objects.equals(warehouseType, Constants.WarehouseType.WMS)) {
                this.rondafulWMSWareHouseDeliverCallBackNew(deliverCallBack, sysOrderNewList, deliveredPackage);
            }
            //通知第三方包裹已发货 todo
            //deliverCallBackService.sendDelivery(orderTrackId);
        } catch (Exception e) {
            _log.error("包裹{} 发货异常", orderTrackId, e);
            this.updateException(sysOrderNewList, orderTrackId, e);
            //通知第三方包裹有异常 todo
            //deliverCallBackService.sendDelivery(orderTrackId);
        }
    }

    public void updateLocalShipping(List<SysOrderNew> sysOrderNewList) {
        for (SysOrderNew orderNew : sysOrderNewList) {
            for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {
                Integer deliveryWarehouseId = orderPackage.getDeliveryWarehouseId();
                for (SysOrderPackageDetail item : orderPackage.getSysOrderPackageDetailList()) {
                    String sku = item.getSku();
                    Integer skuQuantity = item.getSkuQuantity();
                    _log.info("推送发货数量到仓库，请求参数：仓库id:{},sku:{},sku数量：{}", deliveryWarehouseId, sku, -skuQuantity);
                    String result = remoteSupplierService.updateLocalShipping(deliveryWarehouseId, sku, -skuQuantity);
                    _log.info("推送发货数量到仓库，返回：{}", result);
                }
            }
        }
    }

    private void updateException(List<SysOrderNew> sysOrderNewList, String orderTrackId, Exception e) {
        for (SysOrderNew sysOrderNew : sysOrderNewList) {
            String sysOrderId = sysOrderNew.getSysOrderId();
            SysOrderPackage updatePackage = new SysOrderPackage();
            updatePackage.setOrderTrackId(orderTrackId);
            String exceptionMsg = e.toString();
            updatePackage.setWarehouseShipException(exceptionMsg.length() > 2000 ?
                    StringUtils.substring(exceptionMsg, 0, 2000) : exceptionMsg);
            updatePackage.setModifier(Constants.DefaultUser.SYSTEM);
            updatePackage.setModifiedTime(new Date());
            sysOrderPackageMapper.updateByOrderTrackIdSelective(updatePackage);

            SysOrderNew updateOrder = new SysOrderNew();
            updateOrder.setSysOrderId(sysOrderId);
            updateOrder.setIsErrorOrder(Constants.SysOrder.ERROR_ORDER_YES);
            updateOrder.setUpdateBy(Constants.DefaultUser.SYSTEM);
            updateOrder.setUpdateDate(new Date());
            sysOrderNewMapper.updateBySysOrderIdSelective(updateOrder);
        }
    }

    private List<SysOrderNew> getSysOrderNewListByPackage(SysOrderPackage sysOrderPackage) {
        String operateStatus = sysOrderPackage.getOperateStatus();
        List<SysOrderNew> sysOrderNewList = new ArrayList<>();
        if (Objects.equals(operateStatus, OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
            // 合并包裹，需要拆分订单
            String orderIdStr = sysOrderPackage.getOperateSysOrderId();
            String[] orderIds = StringUtils.split(orderIdStr, Constants.SplitSymbol.HASH_TAG);
            for (String orderId : orderIds) {
                SysOrderNew sysOrderNew = this.getSysOrderNew(orderId);
                sysOrderNewList.add(sysOrderNew);
            }
        } else {
            // 普通包裹 和 拆分包裹  只有一个订单
            sysOrderNewList.add(this.getSysOrderNew(sysOrderPackage.getSysOrderId()));
        }

        return sysOrderNewList;
    }

    public void dealPackageDeliverException(String warehouseShipException, String warehouseType, String speed,
                                            List<SysOrderNew> sysOrderNewList, SysOrderPackage sysOrderPackage) throws Exception {
        for (SysOrderNew sysOrder : sysOrderNewList) {
            String sysOrderId = sysOrder.getSysOrderId();
            String orderTrackId = sysOrderPackage.getOrderTrackId();
            String deliveryException;
            if (warehouseShipException.contains("{")) {
                deliveryException = warehouseShipException.length() > 500 ? warehouseShipException.substring(0, 500) + "}" : warehouseShipException;
                if ("ERP".equals(warehouseType)) {
                    try {
                        JSONObject object = JSONObject.parseObject(deliveryException);
                        deliveryException = object.getString("ExceptionInfo");
                    } catch (Exception e) {
                        _log.error("解析ERP返回的信息异常{}", deliveryException);
                        deliveryException = warehouseShipException;
                    }
                }
            } else {
                deliveryException = warehouseShipException;
            }
            orderMessageSender.sendOrderStockOut(sysOrder.getSellerPlAccount(), sysOrderId,
                    MessageEnum.ORDER_EXCEPTION_NOTICE, deliveryException);
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, deliveryException,
                    OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), warehouseType + "DeliverGoods", speed));
            systemOrderCommonService.updatePackageDeliverException(orderTrackId, "当前进度" + speed + "发生异常，错误信息：" + deliveryException,
                    null, warehouseType + "DeliverGoods");
            SysOrderNew updateOrder = new SysOrderNew();
            updateOrder.setSysOrderId(sysOrderId);
            updateOrder.setIsErrorOrder(Constants.SysOrder.ERROR_ORDER_YES);
            sysOrderNewMapper.updateBySysOrderIdSelective(updateOrder);
            _log.info("订单 {} 包裹{} 仓库发货失败，更新接收异常信息，保存成功", sysOrderId, orderTrackId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void goodCangWareHouseDeliverCallBackNew(WareHouseDeliverCallBack deliverCallBack, List<SysOrderNew> sysOrderNewList,
                                                    SysOrderPackage deliveredPackage) throws Exception {
        String warehouseType = deliverCallBack.getWarehouseType();
        String speed = deliverCallBack.getSpeed();
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.info("包裹{} , 是谷仓订单，进入处理谷仓回调流程", orderTrackId);
        BigDecimal actualLogisticFare = OrderUtils.calculateMoney(deliverCallBack.getActualShipCost(), false);
        if (!"delivered".equals(speed)) {
            this.updateGoodCangDeliverySysOrderLogs(sysOrderNewList, speed, shipTrackNumber, orderTrackId, warehouseType,
                    deliverCallBack, deliveredPackage, shipOrderId);
        } else {
            _log.info("包裹{} , 是谷仓订单，进入处理谷仓发货流程", orderTrackId);
            String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
            for (SysOrderNew sysOrderNew : sysOrderNewList) {
                String sysOrderId = sysOrderNew.getSysOrderId();
                _log.info("订单{} 包裹{} , 插入发货日志", sysOrderId, orderTrackId);
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId,
                        OrderHandleLogEnum.Content.SHIPPED.shipped(sysOrderId, trackNumber, orderTrackId),
                        OrderHandleLogEnum.OrderStatus.STATUS_5.getMsg(), SysOrderLogEnum.Operator.SYSTEM.getMsg(), speed));
            }

            DeliveryPackageDTO deliveryPackageDTO = new DeliveryPackageDTO();
            deliveryPackageDTO.setSysOrderPackage(deliveredPackage);
            deliveryPackageDTO.setSysOrderNewList(sysOrderNewList);
            deliveryPackageDTO.setMergedPackageOrder(Objects.equals(deliveredPackage.getOperateStatus(),
                    OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue()));

            deliveredPackage.setActualShipCost(actualLogisticFare);
            deliveredPackage.setModifier(warehouseType + "DeliverGoods");
            Date now = new Date();
            deliveredPackage.setModifiedTime(now);
            deliveredPackage.setDeliveryTime(now);
            // 计算实际物流费
            this.calculateAndUpdatePackageActualLogisticsFee(deliveredPackage, actualLogisticFare, deliveryPackageDTO);

            // 推送SKU发货数量到仓库
            updateLocalShipping(sysOrderNewList);

            // 更新订单状态及回标
            this.otherOperationMethodsNew(deliverCallBack, sysOrderNewList, deliveredPackage, deliveryPackageDTO);

            // 财务扣款
            this.confirmFee(deliveryPackageDTO, sysOrderNewList, deliveredPackage);
        }
    }

    private void updateGoodCangDeliverySysOrderLogs(List<SysOrderNew> sysOrderNewList, String speed, String shipTrackNumber,
                                                    String orderTrackId, String warehouseType, WareHouseDeliverCallBack deliverCallBack,
                                                    SysOrderPackage deliveredPackage, String shipOrderId) {
        for (SysOrderNew sysOrderNew : sysOrderNewList) {
            String sysOrderId = sysOrderNew.getSysOrderId();
            _log.info("订单{} 更新谷仓回传的信息, 回传的状态为{}", sysOrderId, speed);
            if (GCDeliverStatus.PACKAGE_UPLOAD_STATUS.getProcess().equals(speed)) {
                String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId,
                        GCDeliverStatus.PACKAGE_UPLOAD_STATUS.packageUploadStatus(sysOrderId, trackNumber, orderTrackId),
                        OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), warehouseType + "DeliverGoods", speed));
                this.deliverInfoCallBackPlatformNew(deliverCallBack, sysOrderNew, deliveredPackage);
            } else {
                String message = this.constructGoodCangDeliveryMsg(speed, sysOrderId, orderTrackId);
                SysOrderLog sysOrderLog = sysOrderLogService.findSysOrderLogByMessage(sysOrderId, message);
                if (null == sysOrderLog) {
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, message, OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(),
                            warehouseType + "DeliverGoods", speed));
                }
            }
        }
    }

    /**
     * 计算并更新包裹的实际物流费
     *
     * @param deliveredPackage   {@link SysOrderPackage} 已经获取实际物流费的包裹
     * @param actualLogisticsFee 实际物流费
     * @param deliveryPackageDTO {@link DeliveryPackageDTO} 实际物流费
     */
    public void calculateAndUpdatePackageActualLogisticsFee(SysOrderPackage deliveredPackage,
                                                            BigDecimal actualLogisticsFee,
                                                            DeliveryPackageDTO deliveryPackageDTO) throws Exception {
        String sysOrderId = deliveredPackage.getSysOrderId();
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.error("订单{} , 包裹{} , 进入计算实际物流流程，实际物流费：{}", sysOrderId, orderTrackId, actualLogisticsFee);
        CalculateLogisticsResultVO calculateLogisticsResult = this.getActualLogisticsFeeResult(deliveredPackage, actualLogisticsFee);
        _log.error("订单{} , 包裹{} , 计算实际物流费后的结果为: {}", sysOrderId, orderTrackId, FastJsonUtils.toJsonString(
                calculateLogisticsResult));
        if (null == calculateLogisticsResult) {
            return;
        }

        // 更新发货包裹实际物流费信息
        this.updatePackageActualLogisticsFee(deliveredPackage, actualLogisticsFee, calculateLogisticsResult);

        // 更新 相关订单 或者相关包裹 的实际物流费
        this.updateRelatedFee(deliveredPackage, actualLogisticsFee, calculateLogisticsResult, deliveryPackageDTO);
    }

    /**
     * 更新相关包裹的实际物流费
     *
     * @param deliveredPackage         已发货包裹
     * @param actualLogisticsFee       实际物流费
     * @param calculateLogisticsResult 计算后的物流费
     * @param deliveryPackageDTO       已发货包裹的相关信息
     */
    public void updateRelatedFee(SysOrderPackage deliveredPackage, BigDecimal actualLogisticsFee,
                                 CalculateLogisticsResultVO calculateLogisticsResult, DeliveryPackageDTO deliveryPackageDTO) {
        String orderTrackId = deliveredPackage.getOrderTrackId();
        List<SysOrderNew> orderNewList = deliveryPackageDTO.getSysOrderNewList();
        List<CalculateLogisticsSkuVO> skuList = calculateLogisticsResult.getLogisticsList().get(0).getSkuList();
        if (deliveryPackageDTO.getMergedPackageOrder()) {
            _log.error("包裹{} , 是合并包裹，进入计算子包裹的物流费流程", orderTrackId);
            // 合并包裹订单
            for (SysOrderNew orderNew : orderNewList) {
                List<SysOrderPackage> updatePackageList = orderNew.getSysOrderPackageList();
                for (SysOrderPackage updatePackage : updatePackageList) {
                    String updateOrderId = updatePackage.getSysOrderId();
                    String updateOrderTrackId = updatePackage.getOrderTrackId();
                    _log.error("更新合并订单{} , 包裹{} 的物流费", updateOrderId, updateOrderTrackId);
                    BigDecimal packageActualLogisticsFee = new BigDecimal(0);
                    List<SysOrderPackageDetail> updatePackageDetailList = updatePackage.getSysOrderPackageDetailList();
                    for (SysOrderPackageDetail updatePackageDetail : updatePackageDetailList) {
                        for (CalculateLogisticsSkuVO skuVO : skuList) {
                            if (Objects.equals(skuVO.getSku(), updatePackageDetail.getSku())) {
                                BigDecimal skuPlCost = skuVO.getSkuPerCost();
                                Integer skuQuantity = updatePackageDetail.getSkuQuantity();
                                BigDecimal skuShipFee = skuPlCost.multiply(new BigDecimal(skuQuantity));
                                updatePackageDetail.setLogisticCompanyShipFee(skuPlCost);
                                packageActualLogisticsFee = packageActualLogisticsFee.add(skuShipFee);
                            }
                        }
                        sysOrderPackageDetailMapper.updateByPrimaryKeySelective(updatePackageDetail);
                    }
                    BigDecimal packageActualShipCost = updatePackage.getActualShipCost().add(packageActualLogisticsFee);
                    updatePackage.setActualShipCost(packageActualShipCost);
                    updatePackage.setPackageStatus(OrderPackageStatusEnum.DELIVERED.getValue());
                    sysOrderPackageMapper.updateByPrimaryKeySelective(updatePackage);
                }
            }
        }
    }

    /**
     * 更新发货包裹相关的费用
     *
     * @param deliveredPackage         已发货包裹
     * @param actualLogisticsFee       实际物流费
     * @param calculateLogisticsResult 计算后的物流费
     */
    public void updatePackageActualLogisticsFee(SysOrderPackage deliveredPackage, BigDecimal actualLogisticsFee,
                                                CalculateLogisticsResultVO calculateLogisticsResult) {
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.error("包裹{} , 更新实际物流费:{}", orderTrackId, actualLogisticsFee);
        deliveredPackage.setActualShipCost(actualLogisticsFee);
        deliveredPackage.setWarehouseShipException("");
        List<SysOrderPackageDetail> orderPackageDetails = deliveredPackage.getSysOrderPackageDetailList();

        // 物流商只有一个， 并且包含整个包裹的sku
        List<CalculateLogisticsSkuVO> skuLogisticsFeeList = calculateLogisticsResult.getLogisticsList().get(0).getSkuList();

        // 计算并更新包裹详情的sku物流商费用信息
        for (SysOrderPackageDetail orderPackageDetail : orderPackageDetails) {
            for (CalculateLogisticsSkuVO skuVO : skuLogisticsFeeList) {
                if (Objects.equals(orderPackageDetail.getSku(), skuVO.getSku())) {
                    orderPackageDetail.setLogisticCompanyShipFee(skuVO.getSkuPerCost());
                    sysOrderPackageDetailMapper.updateByPrimaryKeySelective(orderPackageDetail);
                }
            }
        }

        // 更新包裹的实际物流费
        sysOrderPackageMapper.updateByPrimaryKeySelective(deliveredPackage);
    }

    private CalculateLogisticsResultVO getActualLogisticsFeeResult(SysOrderPackage deliveredPackage,
                                                                   BigDecimal actualLogisticsFee) {
        String calculateFeeInfoString = deliveredPackage.getCalculateFeeInfo();
        if (StringUtils.isBlank(calculateFeeInfoString)) {
            _log.info("计算实际物流费的基础信息不存在");
            return null;
        }

        LogisticsCostVo logisticsCostVo = JSON.parseObject(calculateFeeInfoString, LogisticsCostVo.class);
        if (null == logisticsCostVo) {
            _log.info("计算实际物流费的基础信息不存在");
            return null;
        }
        _log.error("计算实际物流费的基础信息是: {}", FastJsonUtils.toJsonString(logisticsCostVo));
        // 物流商列表，有且只有一个
//        List<SupplierGroupVo> logistics = logisticsCostVo.getLogistics();
        // 物流商的商品数据和卖家是一样的
//        logistics.addAll(logisticsCostVo.getSellers());
        // 设置物流商的实际物流费
//        logistics.get(0).setSupplierCost(actualLogisticsFee);
//        logisticsCostVo.setLogistics(logistics);
        List<SupplierGroupVo> logistics = new ArrayList<>();
        SupplierGroupVo supplierGroupVo = new SupplierGroupVo();
        supplierGroupVo.setSupplierCost(actualLogisticsFee);
        List<SkuGroupVo> skuGroupVoList = new ArrayList<>();
        for (SkuGroupVo skuGroupVo : logisticsCostVo.getSellers().get(0).getItems()) {
            SkuGroupVo sku = new SkuGroupVo(skuGroupVo.getSupplierId(), skuGroupVo.getSku(), skuGroupVo.getSkuNumber(),
                    skuGroupVo.getSkuCost(), skuGroupVo.getFreeFreight());
            skuGroupVoList.add(sku);
            supplierGroupVo.setItems(skuGroupVoList);
        }
        logistics.add(supplierGroupVo);
        logisticsCostVo.setLogistics(logistics);
        // 计算并设置回数据
        _log.error("包裹{} 计算前的实际物流费用信息为: {}", deliveredPackage.getOrderTrackId(),
                FastJsonUtils.toJsonString(logisticsCostVo));
        OrderUtils.calcLogisticsCost(logisticsCostVo);
        _log.error("包裹{} 根据不同类型计算前的实际物流费用信息为: {}", deliveredPackage.getOrderTrackId(),
                FastJsonUtils.toJsonString(logisticsCostVo));

        // 包裹计算信息增加
        deliveredPackage.setCalculateFeeInfo(FastJsonUtils.toJsonString(logisticsCostVo));
        CalculateLogisticsResultVO calculateLogisticsResult = new CalculateLogisticsResultVO();

        calculateLogisticsResult = this.calculateLogisticsFee(logisticsCostVo, calculateLogisticsResult, LogisticsCostEnum.logistics);
        _log.error("包裹{} 计算后的实际物流费用信息为: {}", deliveredPackage.getOrderTrackId(),
                FastJsonUtils.toJsonString(calculateLogisticsResult));
        return calculateLogisticsResult;
    }

    /**
     * 构造谷仓发货的信息
     *
     * @param speed        发货节点
     * @param sysOrderId   系统订单ID
     * @param orderTrackId 包裹号
     * @return String message
     */
    private String constructGoodCangDeliveryMsg(String speed, String sysOrderId, String orderTrackId) {
        String message = "";
        if (Objects.equals(GCDeliverStatus.PENDING_CHECK.getProcess(), speed)) {
            message = GCDeliverStatus.PENDING_CHECK.pendingCheck(sysOrderId, orderTrackId);
        } else if (Objects.equals(GCDeliverStatus.PENDING_DELIVER.getProcess(), speed)) {
            message = GCDeliverStatus.PENDING_DELIVER.pendingDeliver(sysOrderId, orderTrackId);
        } else if (Objects.equals(GCDeliverStatus.TEMPORARILY_SAVE.getProcess(), speed)) {
            message = GCDeliverStatus.TEMPORARILY_SAVE.temporarilySave(sysOrderId, orderTrackId);
        } else if (Objects.equals(GCDeliverStatus.ABNORMAL_ORDER.getProcess(), speed)) {
            message = GCDeliverStatus.ABNORMAL_ORDER.abnormalOrder(sysOrderId, orderTrackId);
        } else if (Objects.equals(GCDeliverStatus.PROBLEM_SHIPMENT.getProcess(), speed)) {
            message = GCDeliverStatus.PROBLEM_SHIPMENT.problemShipment(sysOrderId, orderTrackId);
        } else if (Objects.equals(GCDeliverStatus.DISCARD.getProcess(), speed)) {
            message = GCDeliverStatus.DISCARD.discard(sysOrderId, orderTrackId);
        }
        return message;
    }

    @Transactional(rollbackFor = Exception.class)
    public void rondafulERPWareHouseDeliverCallBackNew(WareHouseDeliverCallBack deliverCallBack, List<SysOrderNew> sysOrderNewList,
                                                       SysOrderPackage deliveredPackage) throws Exception {
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.error("包裹{} , 是ERP订单，进入处理ERP回调流程", orderTrackId);
        String warehouseType = deliverCallBack.getWarehouseType();
        String speed = deliverCallBack.getSpeed();
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        BigDecimal actualLogisticFare = deliverCallBack.getActualShipCost();
        String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
        if (!Objects.equals(ERPDeliverProcess.SHIPPING_TIME.getProcess(), speed)) {
            // 不是发货流程
            this.updateERPDeliverySysOrderLogs(sysOrderNewList, speed, shipOrderId, shipTrackNumber, orderTrackId, warehouseType,
                    deliverCallBack, deliveredPackage);

        } else {
            _log.error("包裹{} , 是ERP订单，进入处理ERP发货流程{}", orderTrackId, JSONObject.toJSONString(deliverCallBack));
            // 发货流程
            for (SysOrderNew sysOrderNew : sysOrderNewList) {
                String sysOrderId = sysOrderNew.getSysOrderId();
                _log.info("订单{} 包裹{} , 插入发货日志", sysOrderId, orderTrackId);
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId,
                        OrderHandleLogEnum.Content.SHIPPED.shipped(sysOrderId, trackNumber, orderTrackId),
                        OrderHandleLogEnum.OrderStatus.STATUS_5.getMsg(), SysOrderLogEnum.Operator.SYSTEM.getMsg(), speed));
            }
            DeliveryPackageDTO deliveryPackageDTO = new DeliveryPackageDTO();
            deliveryPackageDTO.setSysOrderPackage(deliveredPackage);
            deliveryPackageDTO.setSysOrderNewList(sysOrderNewList);
            deliveryPackageDTO.setMergedPackageOrder(Objects.equals(deliveredPackage.getOperateStatus(),
                    OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue()));

            String exchangeRate = rateUtil.remoteExchangeRateByCurrencyCode("CNY", rateCurrency);
            _log.error("包裹{} 获取RMB------>USD的汇率为: {}", orderTrackId, exchangeRate);
            BigDecimal actualLogisticFareUSD = OrderUtils.calculateMoney(actualLogisticFare.multiply(new BigDecimal(exchangeRate)), true);
            _log.error("包裹{} 发货后的实际物流费为: {} USD", orderTrackId, actualLogisticFareUSD);

            //判断是否是线上物流
            if (this.isEdisAcount(deliveredPackage, sysOrderNewList.get(0))){
                actualLogisticFareUSD = BigDecimal.ZERO;
                _log.error("包裹{} 走的是edis物流发货后的实际物流费为: {} USD", orderTrackId, actualLogisticFareUSD);
            }

            deliveredPackage.setActualShipCost(actualLogisticFareUSD);

            // 计算实际物流费
            this.calculateAndUpdatePackageActualLogisticsFee(deliveredPackage, actualLogisticFareUSD, deliveryPackageDTO);

            // 更新订单状态及回标
            this.otherOperationMethodsNew(deliverCallBack, sysOrderNewList, deliveredPackage, deliveryPackageDTO);

            // 推送SKU发货数量到仓库
            updateLocalShipping(sysOrderNewList);

            // 财务扣款
            this.confirmFee(deliveryPackageDTO, sysOrderNewList, deliveredPackage);
        }
    }

    /**
     * 判断是否是线上物流及edis账户
     * @param deliveredPackage
     * @param sysOrderNew
     * @return
     */
    public Boolean isEdisAcount(SysOrderPackage deliveredPackage, SysOrderNew sysOrderNew){
        //判断是否是线上物流
        _log.error("包裹{} , 走的物流方式为:{}", deliveredPackage.getOrderTrackId(), deliveredPackage.getDeliveryMethodCode());
        String str = remoteSupplierService.queryLogisticsByCode(deliveredPackage.getDeliveryMethodCode(), Integer.valueOf(deliveredPackage.getDeliveryWarehouseId()));
        String dataString = Utils.returnRemoteResultDataString(str, "调用查询物流方式异常");
        if (StringUtils.isNotBlank(dataString)) {
            LogisticsDTO logisticsDTO = JSONObject.parseObject(dataString, LogisticsDTO.class);
            if (logisticsDTO.getOnlineLogistics().equalsIgnoreCase(Constants.OnlineLogistics.EDIS)){
                _log.error("包裹{} , 走的是线上物流:{}", deliveredPackage.getOrderTrackId(), deliveredPackage.getDeliveryMethodCode());
                //判断是否为edis账户
                EmpowerRequestVo empowerRequestVo = new EmpowerRequestVo();
                empowerRequestVo.setPlatform(OrderSourceCovertToUserServicePlatformEnum.getOtherPlatformCode(sysOrderNew.getOrderSource()));
                empowerRequestVo.setBindCode(Arrays.asList(sysOrderNew.getPlatformShopId()));
                empowerRequestVo.setDataType("10");
                empowerRequestVo.setPage(1);
                empowerRequestVo.setRow(100);
                String object = remoteSellerService.getEmpowerSearchVO(empowerRequestVo);
                String empower = Utils.returnRemoteResultDataString(object, "调用查询店铺授权异常");
                if (StringUtils.isNotBlank(empower)){
                    List<Empower> empowerVO = JSONObject.parseArray(empower, Empower.class);
                    for (Empower empower1: empowerVO){
                        if (empower1.getEbayEdis().intValue() == Constants.EbayEdis.BIND_PL || empower1.getEbayEdis().intValue() == Constants.EbayEdis.AUTORIZATION_PL){
                            _log.error("包裹{} , 账户绑定了edis账户bindCode:{}", deliveredPackage.getOrderTrackId(), sysOrderNew.getPlatformShopId());
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * WMS回调处理
     *
     * @param deliverCallBack
     * @param sysOrderNewList
     * @param deliveredPackage
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void rondafulWMSWareHouseDeliverCallBackNew(WareHouseDeliverCallBack deliverCallBack, List<SysOrderNew> sysOrderNewList,
                                                       SysOrderPackage deliveredPackage) throws Exception {
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.info("包裹{} , 是WMS订单，进入处理WMS回调流程", orderTrackId);
        String warehouseType = deliverCallBack.getWarehouseType();
        String speed = deliverCallBack.getSpeed();
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        BigDecimal actualLogisticFare = deliverCallBack.getActualShipCost();
        String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
        if (!Objects.equals(ERPDeliverProcess.SHIPPING_TIME.getProcess(), speed)) {
            // 不是发货流程
            this.updateERPDeliverySysOrderLogs(sysOrderNewList, speed, shipOrderId, shipTrackNumber, orderTrackId, warehouseType,
                    deliverCallBack, deliveredPackage);

        } else {
            _log.info("包裹{} , 是WMS订单，进入处理WMS发货流程", orderTrackId);
            // 发货流程
            for (SysOrderNew sysOrderNew : sysOrderNewList) {
                String sysOrderId = sysOrderNew.getSysOrderId();
                _log.info("订单{} 包裹{} , 插入发货日志", sysOrderId, orderTrackId);
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId,
                        OrderHandleLogEnum.Content.SHIPPED.shipped(sysOrderId, trackNumber, orderTrackId),
                        OrderHandleLogEnum.OrderStatus.STATUS_5.getMsg(), SysOrderLogEnum.Operator.SYSTEM.getMsg(), speed));
            }
            DeliveryPackageDTO deliveryPackageDTO = new DeliveryPackageDTO();
            deliveryPackageDTO.setSysOrderPackage(deliveredPackage);
            deliveryPackageDTO.setSysOrderNewList(sysOrderNewList);
            deliveryPackageDTO.setMergedPackageOrder(Objects.equals(deliveredPackage.getOperateStatus(),
                    OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue()));

            deliveredPackage.setActualShipCost(actualLogisticFare);

            // 计算实际物流费
            this.calculateAndUpdatePackageActualLogisticsFee(deliveredPackage, actualLogisticFare, deliveryPackageDTO);

            // 后续操作处理
            this.otherOperationMethodsNew(deliverCallBack, sysOrderNewList, deliveredPackage, deliveryPackageDTO);

            // 推送SKU发货数量到仓库
            updateLocalShipping(sysOrderNewList);

            // 财务扣款
            this.confirmFee(deliveryPackageDTO, sysOrderNewList, deliveredPackage);
        }
    }

    private void updateERPDeliverySysOrderLogs(List<SysOrderNew> sysOrderNewList, String speed, String shipOrderId, String trackNumber,
                                               String orderTrackId, String warehouseType, WareHouseDeliverCallBack deliverCallBack,
                                               SysOrderPackage deliveredPackage) {
        for (SysOrderNew sysOrder : sysOrderNewList) {
            String sysOrderId = sysOrder.getSysOrderId();
            _log.info("订单{} 更新ERP回传的信息, 回传的状态为{}", sysOrderId, speed);
            if (Objects.equals(ERPDeliverProcess.PACKAGE_UPLOAD_STATUS.getProcess(), speed)) {
                SysOrderLog sysOrderLog = new SysOrderLog();
                //日志以跟踪号为主
                if (StringUtils.isNotBlank(trackNumber)) {
                    sysOrderLog = new SysOrderLog(sysOrderId,
                            ERPDeliverProcess.PACKAGE_UPLOAD_STATUS.packageUploadStatus(sysOrderId, trackNumber, orderTrackId),
                            OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), warehouseType + "DeliverGoods", speed);
                } else {
                    sysOrderLog = new SysOrderLog(sysOrderId,
                            ERPDeliverProcess.PACKAGE_UPLOAD_STATUS.packageUploadStatus2(sysOrderId, shipOrderId, orderTrackId),
                            OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), warehouseType + "DeliverGoods", speed);
                }
                sysOrderLogService.insertSelective(sysOrderLog);
                this.deliverInfoCallBackPlatformNew(deliverCallBack, sysOrder, deliveredPackage);
            } else {
                String message = "";
                if (Objects.equals(ERPDeliverProcess.ORDER_PUSH.getProcess(), speed)) {
                    message = ERPDeliverProcess.ORDER_PUSH.orderPush(sysOrderId);
                } else if (Objects.equals(ERPDeliverProcess.DISTRIBUTION.getProcess(), speed)) {
                    message = ERPDeliverProcess.DISTRIBUTION.distribution(sysOrderId);
                } else if (Objects.equals(ERPDeliverProcess.PACKAGE_CONFIRM_STATUS.getProcess(), speed)) {
                    //日志以跟踪号为主
                    if (StringUtils.isNotBlank(trackNumber)) {
                        message = ERPDeliverProcess.PACKAGE_CONFIRM_STATUS.packageConfirmStatus(sysOrderId, trackNumber, orderTrackId);
                    } else {
                        message = ERPDeliverProcess.PACKAGE_CONFIRM_STATUS.packageConfirmStatus2(sysOrderId, shipOrderId, orderTrackId);
                    }
                    this.deliverInfoCallBackPlatformNew(deliverCallBack, sysOrder, deliveredPackage);
                } else if (Objects.equals(ERPDeliverProcess.PACKING_TIME.getProcess(), speed)) {
                    //日志以跟踪号为主
                    if (StringUtils.isNotBlank(trackNumber)) {
                        message = ERPDeliverProcess.PACKING_TIME.packingTime(sysOrderId, trackNumber, orderTrackId);
                    } else {
                        message = ERPDeliverProcess.PACKING_TIME.packingTime2(sysOrderId, shipOrderId, orderTrackId);
                    }
                    this.deliverInfoCallBackPlatformNew(deliverCallBack, sysOrder, deliveredPackage);
                }

                SysOrderLog sysOrderLog = sysOrderLogService.findSysOrderLogByMessage(sysOrderId, message);
                if (null == sysOrderLog) {
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, message, OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(),
                            warehouseType + "DeliverGoods", speed));
                }
            }
        }
    }

    /**
     * 更新本地表
     * 推送出库记录
     * 推送商品销售统计
     * 如果没有回传平台发货数据则回传
     *
     * @param deliverCallBack    {@link WareHouseDeliverCallBack} 仓库回掉的参数
     * @param deliveredPackage   {@link SysOrderPackage} 已发货的包裹
     * @param deliveryPackageDTO {@link DeliveryPackageDTO} 发货包裹的一些数据
     */
    public void otherOperationMethodsNew(WareHouseDeliverCallBack deliverCallBack, List<SysOrderNew> sysOrderNewList,
                                         SysOrderPackage deliveredPackage, DeliveryPackageDTO deliveryPackageDTO) {
        String orderTrackId = deliveredPackage.getOrderTrackId();
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        if (StringUtils.isBlank(shipTrackNumber) && StringUtils.isBlank(shipOrderId)) {
            _log.error("跟踪单号，物流商单号不能为空");
            return;
        }
        String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
        String trackNumDB = deliveredPackage.getShipTrackNumber();
        String shipOrderIdDB = deliveredPackage.getShipOrderId();
        String trackNumberDB = trackNumDB == null ? shipOrderIdDB : trackNumDB;
        String warehouseType = deliverCallBack.getWarehouseType();

        // 更新包裹相关的信息
        this.updateRelatePackageStatus(sysOrderNewList, deliveredPackage, orderTrackId,
                shipTrackNumber, shipOrderId, warehouseType);

        // 更新订单状态
        this.updateSysOrderDeliveryStatus(deliveredPackage, deliveryPackageDTO, deliverCallBack);

        //sku 销售记录
        this.insertBatchSkuSalesRecord(deliveredPackage);

        //推送商品销售统计
        systemOrderService.pushCommoditySalesRecord(deliveredPackage);

        // 回传
        if (StringUtils.isBlank(trackNumberDB) || (StringUtils.isNotBlank(trackNumberDB) && !trackNumberDB.equals(trackNumber))) {
            for (SysOrderNew sysOrderNew : sysOrderNewList) {
                _log.error("订单{} , 包裹{} , 需要把订单发货信息回传其他平台", sysOrderNew.getSysOrderId(), orderTrackId);
                // TODO: 回传方法需要重构
                deliveredPackage.setShipTrackNumber(shipTrackNumber);
                deliveredPackage.setShipOrderId(shipOrderId);
                this.deliverInfoCallBackNew(sysOrderNew, deliveredPackage);
            }
        }
    }

    public void updateRelatePackageStatus(List<SysOrderNew> sysOrderNewList, SysOrderPackage deliveredPackage,
                                          String orderTrackId, String shipTrackNumber, String shipOrderId,
                                          String warehouseType) {
        String operateStatus = deliveredPackage.getOperateStatus();
        // 更新包裹的发货信息
        SysOrderPackage updatePackage = new SysOrderPackage();
        updatePackage.setOrderTrackId(orderTrackId);
        updatePackage.setShipTrackNumber(shipTrackNumber);
        updatePackage.setShipOrderId(shipOrderId);
        Date now = new Date();
        updatePackage.setDeliveryTime(now);
        updatePackage.setModifiedTime(now);
        updatePackage.setModifier(warehouseType + "DeliverGoods");
        sysOrderPackageMapper.updateByOrderTrackIdSelective(updatePackage);
        if (Objects.equals(operateStatus, OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
            _log.info("包裹{} 是合并包裹，需要同时更新原始数据", orderTrackId);
            for (SysOrderNew sysOrderNew : sysOrderNewList) {
                List<SysOrderPackage> sysOrderPackageList = sysOrderNew.getSysOrderPackageList();
                for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {
                    SysOrderPackage updateRelatePackage = new SysOrderPackage();
                    updateRelatePackage.setOrderTrackId(sysOrderPackage.getOrderTrackId());
                    updateRelatePackage.setShipTrackNumber(shipTrackNumber);
                    updateRelatePackage.setShipOrderId(shipOrderId);
                    updateRelatePackage.setDeliveryTime(now);
                    updateRelatePackage.setModifiedTime(now);
                    updateRelatePackage.setModifier(warehouseType + "DeliverGoods");
                    sysOrderPackageMapper.updateByOrderTrackIdSelective(updateRelatePackage);
                }
            }
        }
    }

    /**
     * 入库sku销售记录
     *
     * @param deliveredPackage 发货包裹
     */
    public void insertBatchSkuSalesRecord(SysOrderPackage deliveredPackage) {
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.info("包裹{} 推送数据给sku销售记录", orderTrackId);
        List<SkuSalesRecord> insertSkuSalesRecordList = new ArrayList<>();

        boolean isMergedPackage = Objects.equals(deliveredPackage.getOperateStatus(),
                OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue());

        // 获得所有订单的订单商品信息
        List<SysOrderNew> sysOrderNewList = this.getSysOrderNewListByPackage(deliveredPackage);
        for (SysOrderNew orderNew : sysOrderNewList) {
            String sysOrderId = orderNew.getSysOrderId();
            String sellerPlAccount = orderNew.getSellerPlAccount();
            Integer sellerPlId = orderNew.getSellerPlId();
            _log.info("订单{} 包裹{} 组装数据，并插入到sku销售记录表", sysOrderId, orderTrackId);
            // 组装插入sku销售记录表的信息

            List<SysOrderPackage> sysOrderPackageList = orderNew.getSysOrderPackageList();
            for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {

                // 不是合并包裹的，需要正确判断发货包裹是哪一个
                if (!isMergedPackage) {
                    if (!Objects.equals(orderTrackId, sysOrderPackage.getOrderTrackId())) {
                        continue;
                    }
                }

                List<SysOrderPackageDetail> sysOrderPackageDetailList = deliveredPackage.getSysOrderPackageDetailList();
                for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetailList) {
                    if (StringUtils.isBlank(sysOrderPackageDetail.getSku())){
                        continue;
                    }
                    SkuSalesRecord skuSalesRecord = new SkuSalesRecord();
                    Date now = new Date();
                    skuSalesRecord.setOrderTrackId(orderTrackId);
                    skuSalesRecord.setSysOrderId(sysOrderId);
                    // SKU信息
                    skuSalesRecord.setSku(sysOrderPackageDetail.getSku());
                    Integer skuQuantity = sysOrderPackageDetail.getSkuQuantity();
                    skuSalesRecord.setSkuQuantity(skuQuantity);
                    BigDecimal skuQuantityBigDecimal = new BigDecimal(skuQuantity);
                    skuSalesRecord.setSkuTitle(sysOrderPackageDetail.getSkuName());
                    BigDecimal skuPrice = sysOrderPackageDetail.getSkuPrice();
                    skuSalesRecord.setSkuPrice(skuPrice);
                    skuSalesRecord.setItemURL(sysOrderPackageDetail.getSkuUrl());
                    skuSalesRecord.setItemAttr(sysOrderPackageDetail.getSkuAttr());
                    skuSalesRecord.setSkuTitleEn(sysOrderPackageDetail.getSkuNameEn());
                    skuSalesRecord.setTotalAmount(sysOrderPackageDetail.getSkuPrice().multiply(skuQuantityBigDecimal));

                    // 卖家 物流商 供应商 物流费
                    BigDecimal sellerShipFee = sysOrderPackageDetail.getSellerShipFee();
                    skuSalesRecord.setSellerShipFee(sellerShipFee.multiply(skuQuantityBigDecimal));
                    BigDecimal supplierShipFee = sysOrderPackageDetail.getSupplierShipFee();
                    skuSalesRecord.setSupplierShipFee(supplierShipFee.multiply(skuQuantityBigDecimal));
                    BigDecimal logisticCompanyShipFee = sysOrderPackageDetail.getLogisticCompanyShipFee();
                    skuSalesRecord.setLogisticCompanyShipFee(logisticCompanyShipFee.multiply(skuQuantityBigDecimal));

                    skuSalesRecord.setSellerSkuPerShipFee(sellerShipFee);
                    skuSalesRecord.setSupplierSkuPerShipFee(supplierShipFee);
                    skuSalesRecord.setLogisticCompanySkuPerShipFee(logisticCompanyShipFee);

                    skuSalesRecord.setDeliveryTime(now);
                    skuSalesRecord.setDeliveryWarehouseId(sysOrderPackage.getDeliveryWarehouseId());
                    skuSalesRecord.setDeliveryWarehouseName(sysOrderPackage.getDeliveryWarehouse());
                    skuSalesRecord.setFreeFreight(sysOrderPackageDetail.getFreeFreight());
                    skuSalesRecord.setServiceCharge(this.calculateSkuServiceCost(sysOrderPackageDetail.getFareTypeAmount(),
                            skuQuantityBigDecimal, skuPrice));
                    skuSalesRecord.setSupplierId(sysOrderPackageDetail.getSupplierId());
                    skuSalesRecord.setSupplierName(sysOrderPackageDetail.getSupplierName());
                    skuSalesRecord.setSellerName(sellerPlAccount);
                    skuSalesRecord.setSellerId(sellerPlId);

                    skuSalesRecord.setCreater(Constants.DefaultUser.SYSTEM);
                    skuSalesRecord.setCreateTime(now);
                    skuSalesRecord.setModifier(Constants.DefaultUser.SYSTEM);
                    skuSalesRecord.setModifiedTime(now);
                    insertSkuSalesRecordList.add(skuSalesRecord);
                }
            }


        }
//        _log.debug("insertSkuSalesRecordList= {}", FastJsonUtils.toJsonString(insertSkuSalesRecordList));
        skuSalesRecordService.insertBatchSkuSalesRecord(insertSkuSalesRecordList);
    }

    private BigDecimal calculateSkuServiceCost(String fareTypeAmount, BigDecimal quantity, BigDecimal skuPrice) {
        String[] split = StringUtils.split(fareTypeAmount, Constants.SplitSymbol.HASH_TAG);
        int length = split.length;
        if (length == 0) {
            return BigDecimal.ZERO;
        }

        if (length == 1) {
            BigDecimal price = new BigDecimal(split[0]);
            return price.multiply(quantity);
        }

        BigDecimal base = new BigDecimal(split[1]);
        String type = split[0];
        if (Objects.equals(type, Constants.Commodity.FARE_TYPE_FIXED)) {
            // 固定抽取服务费
            return base.multiply(quantity);
        } else {
            // 百分比抽取服务费
            return OrderUtils.calculateMoney(
                    base.multiply(skuPrice).multiply(quantity)
                            .divide(new BigDecimal("100"), 8, BigDecimal.ROUND_HALF_UP), false);
        }
    }

    public void updateSysOrderDeliveryStatus(SysOrderPackage deliveredPackage, DeliveryPackageDTO deliveryPackageDTO,
                                              WareHouseDeliverCallBack deliverCallBack) {
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.error("包裹{} 进入更新订单发货状态流程", orderTrackId);
        List<SysOrderNew> orderNewList = deliveryPackageDTO.getSysOrderNewList();
        Date now = new Date();
        if (deliveryPackageDTO.getMergedPackageOrder()) {
            _log.error("包裹{} , 是合并包裹，需要对所有的订单进行发货状态更新", orderTrackId);
            // 合并包裹
            for (SysOrderNew order : orderNewList) {
                SysOrderNew updateOrder = new SysOrderNew();
                updateOrder.setSysOrderId(order.getSysOrderId());
                updateOrder.setOrderDeliveryStatus(OrderDeliveryStatusNewEnum.DELIVERED.getValue());
                updateOrder.setDeliveryTime(DateUtils.dateToString(now, DateUtils.FORMAT_2));
                updateOrder.setIsErrorOrder(Constants.SysOrder.ERROR_ORDER_NO);
                updateOrder.setUpdateBy(deliverCallBack.getWarehouseType() + "DeliverGoods");
                updateOrder.setUpdateDate(now);
                sysOrderNewMapper.updateBySysOrderIdSelective(updateOrder);
            }
        } else {
            // 普通包裹 拆分包裹
            _log.error("包裹{} , 不是合并包裹，判断所有包裹状态后更新订单状态", orderTrackId);
            SysOrderNew order = orderNewList.get(0);
            List<SysOrderPackage> packageList = new ArrayList<>();
            for (SysOrderPackage orderPackage : order.getSysOrderPackageList()) {
                // 判断订单状态需要修改该包裹是已发货状态
                if (Objects.equals(orderPackage.getOrderTrackId(), orderTrackId)) {
                    orderPackage.setPackageStatus(OrderPackageStatusEnum.DELIVERED.getValue());
                }
                packageList.add(orderPackage);
            }
            SysOrderNew updateOrder = new SysOrderNew();
            String updateSysOrderId = order.getSysOrderId();
            updateOrder.setSysOrderId(updateSysOrderId);
            byte orderDeliveryStatus = this.getSysOrderDeliveryStatus(packageList);
            _log.error("订单{} 经过包裹计算的发货状态为: {}", updateSysOrderId, orderDeliveryStatus);
            updateOrder.setOrderDeliveryStatus(orderDeliveryStatus);

            if (Objects.equals(OrderDeliveryStatusNewEnum.DELIVERED.getValue(), orderDeliveryStatus)) {
                _log.error("订单{} 所有的包裹均已发货。", updateSysOrderId);
                updateOrder.setDeliveryTime(DateUtils.dateToString(now, DateUtils.FORMAT_2));
            }

            updateOrder.setIsErrorOrder(Constants.SysOrder.ERROR_ORDER_NO);
            updateOrder.setUpdateBy(deliverCallBack.getWarehouseType() + "DeliverGoods");
            updateOrder.setUpdateDate(now);
            sysOrderNewMapper.updateBySysOrderIdSelective(updateOrder);
        }
    }

    /**
     * 获取发货的状态， 只会返回在  待发货 、 已发货、 部分发货 中的其中一个状态
     *
     * @param sysOrderPackageList {@link List<SysOrderPackage>} 订单包裹列表
     * @return byte
     */
    public byte getSysOrderDeliveryStatus(List<SysOrderPackage> sysOrderPackageList) {
        _log.error("获取发货状态参数为: {}", JSONObject.toJSONString(sysOrderPackageList));
        Set<Boolean> resultSet = new HashSet<>(2);

        if (CollectionUtils.isEmpty(sysOrderPackageList)) {
            return OrderDeliveryStatusNewEnum.DELIVERED.getValue();
        }

        for (SysOrderPackage orderPackage : sysOrderPackageList) {
            boolean isDelivered = false;
            if (Objects.equals(OrderPackageStatusEnum.DELIVERED.getValue(), orderPackage.getPackageStatus())) {
                isDelivered = true;
            }
            resultSet.add(isDelivered);
        }

        if (resultSet.contains(true) && resultSet.contains(false)) {
            return OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getValue();
        }

        if (resultSet.contains(true) && !resultSet.contains(false)) {
            return OrderDeliveryStatusNewEnum.DELIVERED.getValue();
        }

        return OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue();
    }

    /**
     * 财务扣款
     *
     * @param deliveryPackageDTO {@link DeliveryPackageDTO} 发货包裹的一些参数
     * @param sysOrderNewList    {@link List<SysOrderNew>} 发货包裹对应的订单
     * @param deliveredPackage   {@link SysOrderPackage} 发货包裹
     */
    private void confirmFee(DeliveryPackageDTO deliveryPackageDTO, List<SysOrderNew> sysOrderNewList,
                            SysOrderPackage deliveredPackage) throws Exception {
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.info("包裹{} 进入通知财务扣款流程", orderTrackId);
        if (deliveryPackageDTO.getMergedPackageOrder()) {
            // 合并包裹， 需要把所有的订单通知财务扣款
            _log.info("包裹{} 是合并包裹，需要对所有的订单通知财务扣款", orderTrackId);
            for (SysOrderNew confirmFeeOrder : sysOrderNewList) {
                _log.info("订单{} 通知财务扣款", confirmFeeOrder.getSysOrderId());
                this.confirmFeeToFinance(confirmFeeOrder);
            }
        } else {
            // 其余的包裹， 需要查看其余包裹是否已经发货，如果全部都发货，则需要通知财务扣款
            SysOrderNew sysOrderNew = sysOrderNewList.get(0);
            List<SysOrderPackage> packageList = sysOrderNew.getSysOrderPackageList();
            boolean confirmFee = true;
            for (SysOrderPackage orderPackage : packageList) {
                if (!Objects.equals(orderTrackId, orderPackage.getOrderTrackId())
                        && !Objects.equals(OrderPackageStatusEnum.DELIVERED.getValue(), orderPackage.getPackageStatus())) {
                    confirmFee = false;
                }
            }

            if (confirmFee) {
                _log.info("订单{} , 包裹{} 不是合并包裹，并且校验完所有包裹均已发货，通知财务扣款",
                        sysOrderNew.getSysOrderId(), orderTrackId);
                this.confirmFeeToFinance(sysOrderNew);
            }
        }

        // 更新包裹发货状态
        SysOrderPackage updatePackage = new SysOrderPackage();
        updatePackage.setOrderTrackId(deliveredPackage.getOrderTrackId());
        updatePackage.setPackageStatus(OrderPackageStatusEnum.DELIVERED.getValue());
        sysOrderPackageMapper.updateByOrderTrackIdSelective(updatePackage);

        deliveredPackage.setPackageStatus(OrderPackageStatusEnum.DELIVERED.getValue());
    }

    /**
     * 通知财务扣款
     */
    private void confirmFeeToFinance(SysOrderNew sysOrderNew) throws Exception {
        String sysOrderId = sysOrderNew.getSysOrderId();
        SysOrderNew updateOrder = new SysOrderNew();
        updateOrder.setSysOrderId(sysOrderNew.getSysOrderId());
        // 财务需要的是实际物流费
        BigDecimal confirmFee = new BigDecimal("0");

        List<SysOrderPackage> sysOrderPackageList = sysOrderPackageMapper.queryOrderPackageByOrderId(sysOrderId);

        for (SysOrderPackage orderPackage : sysOrderPackageList) {
            confirmFee = confirmFee.add(orderPackage.getActualShipCost());
        }

        try {
            _log.error("订单{} 发货财务付款金额为: {}", sysOrderId, confirmFee);
            String result = remoteFinanceService.confirm(sysOrderId, confirmFee);
            _log.error("订单{} 发货财务付款返回结果为: {}", sysOrderId, result);
            String msg = Utils.returnRemoteResultDataString(result, "发货确认扣款调用财务微服务出错。。。");
            updateOrder.setPayStatus(this.getPayStatusForDeliveryPackage(msg));
            _log.info("订单{} 所有包裹均发货成功，确认财务付款结果 {}, 扣除的费用为：{}", sysOrderId, msg, confirmFee);
        } catch (Exception e) {
            _log.error("订单{}发货财务确认扣款失败,财务返回错误信息为: {},使用MQ推送扣减订单金额数据,扣除的费用为：{}",
                    sysOrderId, e.getMessage(), confirmFee, e);
            orderMessageSender.sendFinanceOrderConfirmMQ(sysOrderId, confirmFee.toString());
            updateOrder.setPayStatus((byte) 20);
        }
        sysOrderNewMapper.updateBySysOrderIdSelective(updateOrder);
    }

    private byte getPayStatusForDeliveryPackage(String financeReturnMsg) {
        if (Objects.equals(PayInfoEnum.PayStatusEnum.PAYMENT_SUCCESS.getMsg(), financeReturnMsg)) {
            return PayInfoEnum.PayStatusEnum.PAYMENT_SUCCESS.getValue();
        } else if (Objects.equals(PayInfoEnum.PayStatusEnum.GENERATION_OF_FILLING_MONEY.getMsg(), financeReturnMsg)) {
            return PayInfoEnum.PayStatusEnum.GENERATION_OF_FILLING_MONEY.getValue();
        } else {
            return PayInfoEnum.PayStatusEnum.IN_THE_PAYMENT.getValue();
        }
    }

    @Override
    public void wareHouseDeliverCallBack(WareHouseDeliverCallBack deliverCallBack) throws JSONException {
        _log.info("仓库回调数据为: {}", FastJsonUtils.toJsonString(deliverCallBack));
        String warehouseType = deliverCallBack.getWarehouseType();
        String orderTrackId = deliverCallBack.getOrderTrackId();
        String warehouseShipException = deliverCallBack.getWarehouseShipException();
        String speed = deliverCallBack.getSpeed();
        if (orderTrackId.startsWith("SH") || orderTrackId.startsWith("sh")) {
            _log.info("包裹{} 是售后包裹", orderTrackId);
            this.pushCMSOrderDeliverInfo(deliverCallBack);
            return;
        }
        SysOrder sysOrder = sysOrderMapper.selectSysOrderByOrderTrackId(orderTrackId);
        if (sysOrder == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "根据订单跟踪号查询不到数据。。。");
        }
        String sysOrderId = sysOrder.getSysOrderId();
        Byte deliveryStatus = sysOrder.getOrderDeliveryStatus();
        if ((byte) 1 == deliveryStatus || (byte) 2 == deliveryStatus || (byte) 4 == deliveryStatus
                || (byte) 5 == deliveryStatus || (byte) 6 == deliveryStatus || (byte) 7 == deliveryStatus) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "只有配货中状态的订单才能更新发货信息。。。");
        }
        if (StringUtils.isNotEmpty(warehouseShipException)) {
            String deliveryException;
            if (warehouseShipException.contains("{")) {
                deliveryException = warehouseShipException.length() > 500 ? warehouseShipException.substring(0, 500) + "}" : warehouseShipException;
                if ("ERP".equals(warehouseType)) {
                    try {
                        JSONObject object = JSONObject.parseObject(deliveryException);
                        deliveryException = object.getString("ExceptionInfo");
                    } catch (Exception e) {
                        _log.error("解析ERP返回的信息异常{}", deliveryException);
                        deliveryException = warehouseShipException;
                    }
                }
            } else {
                deliveryException = warehouseShipException;
            }
            orderMessageSender.sendOrderStockOut(sysOrder.getSellerPlAccount(), sysOrderId, MessageEnum.ORDER_EXCEPTION_NOTICE, deliveryException);
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, deliveryException,
                    OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), warehouseType + "DeliverGoods", speed));
            systemOrderCommonService.updateDeliverExceptionInfo(sysOrder, "当前进度" + speed + "发生异常，错误信息：" + deliveryException,
                    null, null, warehouseType + "DeliverGoods");
            _log.info("________________订单 {} 仓库发货失败，更新接收异常信息，保存成功________________", sysOrderId);
            return;
        }
        if ("ERP".equals(warehouseType)) {
            this.rondafulERPWareHouseDeliverCallBack(deliverCallBack, sysOrder);
        } else if ("GC".equals(warehouseType)) {
            this.goodCangWareHouseDeliverCallBack(deliverCallBack, sysOrder);
        }
    }

    private void rondafulERPWareHouseDeliverCallBack(WareHouseDeliverCallBack deliverCallBack, SysOrder sysOrder) throws JSONException {
        String warehouseType = deliverCallBack.getWarehouseType();
        String sysOrderId = sysOrder.getSysOrderId();
        String speed = deliverCallBack.getSpeed();
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        BigDecimal actualLogisticFare = deliverCallBack.getActualShipCost();
        String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
        if (!"shipping_time".equals(speed)) {
            if (ERPDeliverProcess.PACKAGE_UPLOAD_STATUS.getProcess().equals(speed)) {
                //日志以跟踪号为主
                SysOrderLog sysOrderLog = new SysOrderLog();
                if (StringUtils.isNotBlank(trackNumber)) {
                    sysOrderLog = new SysOrderLog(sysOrderId, ERPDeliverProcess.PACKAGE_UPLOAD_STATUS.packageUploadStatus(sysOrderId, shipTrackNumber, null),
                            OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), warehouseType + "DeliverGoods", speed);
                } else {
                    sysOrderLog = new SysOrderLog(sysOrderId, ERPDeliverProcess.PACKAGE_UPLOAD_STATUS.packageUploadStatus2(sysOrderId, shipOrderId, null),
                            OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), warehouseType + "DeliverGoods", speed);
                }
                sysOrderLogService.insertSelective(sysOrderLog);
                this.deliverInfoCallBackPlatform(deliverCallBack, sysOrder);
            } else {
                String message = "";
                if ("order_push".equals(speed)) {
                    message = ERPDeliverProcess.ORDER_PUSH.orderPush(sysOrderId);
                } else if ("distribution".equals(speed)) {
                    message = ERPDeliverProcess.DISTRIBUTION.distribution(sysOrderId);
                } else if ("package_confirm_status".equals(speed)) {
                    if (StringUtils.isNotBlank(trackNumber)) {
                        message = ERPDeliverProcess.PACKAGE_CONFIRM_STATUS.packageConfirmStatus(sysOrderId, trackNumber, null);
                    } else {
                        message = ERPDeliverProcess.PACKAGE_CONFIRM_STATUS.packageConfirmStatus(sysOrderId, shipOrderId, null);
                    }
                    this.deliverInfoCallBackPlatform(deliverCallBack, sysOrder);
                } else if ("packing_time".equals(speed)) {
                    if (StringUtils.isNotBlank(trackNumber)) {
                        message = ERPDeliverProcess.PACKING_TIME.packingTime(sysOrderId, trackNumber, null);
                    } else {
                        message = ERPDeliverProcess.PACKING_TIME.packingTime2(sysOrderId, shipOrderId, null);
                    }
                    this.deliverInfoCallBackPlatform(deliverCallBack, sysOrder);
                }

                SysOrderLog sysOrderLog = sysOrderLogService.findSysOrderLogByMessage(sysOrderId, message);
                if (null == sysOrderLog) {
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, message, OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(),
                            warehouseType + "DeliverGoods", speed));
                }
            }
        } else {
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.SHIPPED.shipped(sysOrderId, trackNumber, null),
                    OrderHandleLogEnum.OrderStatus.STATUS_5.getMsg(), warehouseType + "DeliverGoods", speed));
            String exchangeRate = rateUtil.remoteExchangeRateByCurrencyCode("CNY", rateCurrency);
            _log.info("__________获取RMB------>USD的汇率为__________{}_________", exchangeRate);
//            BigDecimal actualLogisticFareUSD = actualLogisticFare.multiply(new BigDecimal(exchangeRate));
            BigDecimal actualLogisticFareUSD = OrderUtils.calculateMoney(actualLogisticFare.multiply(new BigDecimal(exchangeRate)), false);
            sysOrder.setActualShipCost(actualLogisticFareUSD);
            try {
                String result = remoteFinanceService.confirm(sysOrderId, actualLogisticFareUSD);
                _log.info("____________ERP仓库发货财务付款返回结果为_________{}____________", result);
                String msg = Utils.returnRemoteResultDataString(result, "发货确认扣款调用财务微服务出错。。。");
                if ("付款成功".equals(msg)) {
                    sysOrder.setPayStatus((byte) 21);
                } else if ("待补款".equals(msg)) {
                    sysOrder.setPayStatus((byte) 30);
                } else {
                    //付款中
                    sysOrder.setPayStatus((byte) 20);
                }
                _log.info("_________ERP发货成功，确认订单 {}_________财务付款结果 {}________实际物流费为：{}________", sysOrderId, msg, actualLogisticFare);
            } catch (Exception e) {
                _log.error("______ERP发货财务确认扣款失败______财务返回错误信息为:{}______使用MQ推送扣减订单金额数据______实际物流费为：{}______", e.getMessage(), actualLogisticFareUSD);
                orderMessageSender.sendFinanceOrderConfirmMQ(sysOrderId, actualLogisticFareUSD.toString());
                sysOrder.setPayStatus((byte) 20);
            }
            this.otherOperationMethods(deliverCallBack, sysOrder);
        }
    }

    private void goodCangWareHouseDeliverCallBack(WareHouseDeliverCallBack deliverCallBack, SysOrder sysOrder) throws JSONException {
        String warehouseType = deliverCallBack.getWarehouseType();
        String sysOrderId = sysOrder.getSysOrderId();
        String speed = deliverCallBack.getSpeed();
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        BigDecimal actualLogisticFare = OrderUtils.calculateMoney(deliverCallBack.getActualShipCost(), false);
//        BigDecimal actualLogisticFare = deliverCallBack.getActualShipCost();
        if (!"delivered".equals(speed)) {
            if (GCDeliverStatus.PACKAGE_UPLOAD_STATUS.getProcess().equals(speed)) {
                String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, GCDeliverStatus.PACKAGE_UPLOAD_STATUS.packageUploadStatus(sysOrderId, trackNumber, null),
                        OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), warehouseType + "DeliverGoods", speed));
                this.deliverInfoCallBackPlatform(deliverCallBack, sysOrder);
            } else {
                String message = "";
                if ("pending_check".equals(speed)) {
                    message = GCDeliverStatus.PENDING_CHECK.pendingCheck(sysOrderId, null);
                } else if ("pending_deliver".equals(speed)) {
                    message = GCDeliverStatus.PENDING_DELIVER.pendingDeliver(sysOrderId, null);
                } else if ("temporarily_save".equals(speed)) {
                    message = GCDeliverStatus.TEMPORARILY_SAVE.temporarilySave(sysOrderId, null);
                } else if ("abnormal_order".equals(speed)) {
                    message = GCDeliverStatus.ABNORMAL_ORDER.abnormalOrder(sysOrderId, null);
                } else if ("problem_shipment".equals(speed)) {
                    message = GCDeliverStatus.PROBLEM_SHIPMENT.problemShipment(sysOrderId, null);
                } else if ("discard".equals(speed)) {
                    message = GCDeliverStatus.DISCARD.discard(sysOrderId, null);
                }

                SysOrderLog sysOrderLog = sysOrderLogService.findSysOrderLogByMessage(sysOrderId, message);
                if (null == sysOrderLog) {
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, message, OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(),
                            warehouseType + "DeliverGoods", speed));
                }

            }
        } else {
            String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.SHIPPED.shipped(sysOrderId, trackNumber, null),
                    OrderHandleLogEnum.OrderStatus.STATUS_5.getMsg(), warehouseType + "DeliverGoods", speed));
            sysOrder.setActualShipCost(actualLogisticFare);
            try {
                String result = remoteFinanceService.confirm(sysOrderId, actualLogisticFare);
                _log.info("____________谷仓发货财务付款返回结果为_________{}____________", result);
                String msg = Utils.returnRemoteResultDataString(result, "发货确认扣款调用财务微服务出错。。。");
                if ("付款成功".equals(msg)) {
                    sysOrder.setPayStatus((byte) 21);
                } else if ("待补款".equals(msg)) {
                    sysOrder.setPayStatus((byte) 30);
                } else {
                    //付款中
                    sysOrder.setPayStatus((byte) 20);
                }
                _log.info("_________谷仓发货成功，确认订单 {}_________财务付款结果 {}________实际物流费为：{}________", sysOrderId, msg, actualLogisticFare);
            } catch (Exception e) {
                _log.error("______谷仓发货财务确认扣款失败______财务返回错误信息为:{}______使用MQ推送扣减订单金额数据______实际物流费为：{}______", e.getMessage(), actualLogisticFare);
                orderMessageSender.sendFinanceOrderConfirmMQ(sysOrderId, actualLogisticFare.toString());
                sysOrder.setPayStatus((byte) 20);
            }
            this.otherOperationMethods(deliverCallBack, sysOrder);
        }
    }

    private void deliverInfoCallBackPlatformNew(WareHouseDeliverCallBack deliverCallBack, SysOrderNew sysOrder,
                                                SysOrderPackage sysOrderPackage) {
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        if (StringUtils.isBlank(shipTrackNumber) && StringUtils.isBlank(shipOrderId)) {
            return;
        }
        String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
        String trackNumDB = sysOrderPackage.getShipTrackNumber();
        String shipOrderIdDB = sysOrderPackage.getShipOrderId();
        String trackNumberDB = trackNumDB == null ? shipOrderIdDB : trackNumDB;
        Date now = new Date();
        sysOrderPackage.setDeliveryTime(now);
        if (StringUtils.isBlank(trackNumberDB) || (StringUtils.isNotBlank(trackNumberDB) && !trackNumberDB.equals(trackNumber))) {
            // 更新包裹发货信息
            SysOrderPackage updatePackage = new SysOrderPackage();
            updatePackage.setOrderTrackId(sysOrderPackage.getOrderTrackId());
            updatePackage.setShipTrackNumber(shipTrackNumber);
            updatePackage.setShipOrderId(shipOrderId);
            updatePackage.setModifier(deliverCallBack.getWarehouseType() + "DeliverGoods");
            updatePackage.setModifiedTime(now);
            sysOrderPackageMapper.updateByOrderTrackIdSelective(updatePackage);

            sysOrderPackage.setShipTrackNumber(shipTrackNumber);
            sysOrderPackage.setShipOrderId(shipOrderId);
            // 回标
            this.deliverInfoCallBackNew(sysOrder, sysOrderPackage);
        }
    }

    /**
     * 回传平台发货信息 -- 待编写
     *
     * @param sysOrder
     */
    public void deliverInfoCallBackNew(SysOrderNew sysOrder, SysOrderPackage deliveredPackage) {
        String sysOrderId = sysOrder.getSysOrderId();
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.error("回传平台的系统订单号为: {}，包裹为: {}", sysOrderId, orderTrackId);

        // 判断是否手工创建的订单
        if (sysOrder.getIsConvertOrder().equalsIgnoreCase(Constants.isConvertOrder.NO)) {
            _log.error("订单{} 是手工创建的订单，不进行回标操作", sysOrderId);
            return;
        }
        // 测试环境是否开启回标
        if (!this.isOpenTest()) {
            return;
        }
        Byte orderSource = sysOrder.getOrderSource();
        // TODO: 回传需要重写
        try {
            deliveredPackage.setDeliveryTime(new Date());
            _log.info("回标的包裹信息为; {}", FastJsonUtils.toJsonString(deliveredPackage));
            if (orderSource == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
                List<EbayOrder> list = systemOrderService.constructEbayOrderDeliverInfoNew(deliveredPackage);
                for (EbayOrder order : list) {
                    _log.info("____________回传eBay平台订单发货数据对象__________{}_________", order);
                }
                systemOrderService.sendEbayOrderDeliverInfoNew(list, sysOrder, deliveredPackage);
            } else if (orderSource == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
                List<AmazonOrderDetail> detailList = systemOrderService.sendAmazonOrderDeliverInfoNew(sysOrder, deliveredPackage);
                for (AmazonOrderDetail order : detailList) {
                    _log.info("____________回传Amazon平台订单发货数据对象__________{}_________", order);
                }
            } else if (orderSource == OrderSourceEnum.CONVER_FROM_ALIEXPRESS.getValue()) {
                _log.info("_________速卖通订单：orderId={}", sysOrder.getSourceOrderId());
                // TODO: 速卖通回标逻辑有问题- 有时间做
                systemOrderService.sendAliexpressDeliverInfoNew(sysOrder, deliveredPackage);
            } else {
                _log.info("未找到回传信息所对应的的订单信息");
            }
        } catch (Exception e) {
            _log.error("____________回传平台订单发货数据出错__________", e);
        }
    }

    /**
     * 回传平台发货数据
     *
     * @param deliverCallBack
     * @param sysOrder
     */
    private void deliverInfoCallBackPlatform(WareHouseDeliverCallBack deliverCallBack, SysOrder sysOrder) {
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        if (StringUtils.isBlank(shipTrackNumber) && StringUtils.isBlank(shipOrderId)) {
            return;
        }
        String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
        String trackNumDB = sysOrder.getShipTrackNumber();
        String shipOrderIdDB = sysOrder.getShipOrderId();
        String trackNumberDB = trackNumDB == null ? shipOrderIdDB : trackNumDB;
        if (StringUtils.isBlank(trackNumberDB) || (StringUtils.isNotBlank(trackNumberDB) && !trackNumberDB.equals(trackNumber))) {
            sysOrder.setShipTrackNumber(shipTrackNumber);
            sysOrder.setShipOrderId(shipOrderId);
            sysOrder.setUpdateBy(deliverCallBack.getWarehouseType() + "DeliverGoods");
            sysOrderMapper.updateBySysOrderIdSelective(sysOrder);
            this.deliverInfoCallBack(sysOrder);
        }
    }

    /**
     * 更新本地表
     * 推送出库记录
     * 推送商品销售统计
     * 如果没有回传平台发货数据则回传
     *
     * @param deliverCallBack
     * @param sysOrder
     */
    private void otherOperationMethods(WareHouseDeliverCallBack deliverCallBack, SysOrder sysOrder) {
        //计算毛利和利润率
//        systemOrderCommonService.setGrossMarginAndProfitMargin(sysOrder);
        String shipTrackNumber = deliverCallBack.getShipTrackNumber();
        String shipOrderId = deliverCallBack.getShipOrderId();
        if (StringUtils.isBlank(shipTrackNumber) && StringUtils.isBlank(shipOrderId)) {
            return;
        }
        String trackNumber = shipTrackNumber == null ? shipOrderId : shipTrackNumber;
        String trackNumDB = sysOrder.getShipTrackNumber();
        String shipOrderIdDB = sysOrder.getShipOrderId();
        String trackNumberDB = trackNumDB == null ? shipOrderIdDB : trackNumDB;
        sysOrder.setShipTrackNumber(shipTrackNumber);
        sysOrder.setShipOrderId(shipOrderId);
        if (StringUtils.isBlank(trackNumberDB) || (StringUtils.isNotBlank(trackNumberDB) && !trackNumberDB.equals(trackNumber))) {
            this.deliverInfoCallBack(sysOrder);
        }
        sysOrder.setDeliveryTime(TimeUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        sysOrder.setOrderDeliveryStatus(OrderHandleEnum.OrderDeliveryStatus.DELIVERED.getValue());
        sysOrder.setUpdateBy(deliverCallBack.getWarehouseType() + "DeliverGoods");
        sysOrderMapper.updateBySysOrderIdSelective(sysOrder);
        //推送出库信息
        systemOrderService.pushDeliverInfoToWareHouse(deliverCallBack.getOrderTrackId());
        //推送商品销售统计
        systemOrderService.pushCommoditySalesRecord(sysOrder);
    }

    private void pushCMSOrderDeliverInfo(WareHouseDeliverCallBack deliverCallBack) {
        CallBackVO callBackVO = new CallBackVO();
        String orderTrackId = deliverCallBack.getOrderTrackId();
        String warehouseShipException = deliverCallBack.getWarehouseShipException();
        callBackVO.setTrackingId(orderTrackId);
        callBackVO.setWarehouseShipException(warehouseShipException);
        callBackVO.setShipNumber(deliverCallBack.getShipTrackNumber() == null ? deliverCallBack.getShipOrderId() : deliverCallBack.getShipTrackNumber());
        try {
            String result;
            if (StringUtils.isEmpty(warehouseShipException)) {
                if (OrderHandleEnum.ERPOrderHandleStatus.SHIPPING_TIME.getValue().equals(deliverCallBack.getSpeed())) {
                    BigDecimal actualShipCost = deliverCallBack.getActualShipCost();
                    if (deliverCallBack.getActualShipCost() != null && "ERP".equals(deliverCallBack.getWarehouseType())) {
                        String exchangeRate = rateUtil.remoteExchangeRateByCurrencyCode("CNY", rateCurrency);
                        _log.info("__________获取RMB------>USD的汇率为__________{}_________", exchangeRate);
                        actualShipCost = actualShipCost.multiply(new BigDecimal(exchangeRate));
                    }
                    callBackVO.setLogisticsCost(actualShipCost + "");
                    callBackVO.setStatus("22");//发货成功
                    _log.info("推送售后补发货订单发货信息参数为: {}", FastJsonUtils.toJsonString(callBackVO));
                    result = remoteCmsService.updateOrderCallBack(callBackVO);
                    Utils.returnRemoteResultDataString(result, "推送售后补发货订单发货信息调用售后微服务出错。。。");
                }
            } else {
                result = remoteCmsService.updateOrderCallBack(callBackVO);
                Utils.returnRemoteResultDataString(result, "推送售后补发货订单发货信息调用售后微服务出错。。。");
            }
            _log.info("______________ERP发货推送售后补发货订单  {}  发货信息调用售后微服务成功______________", orderTrackId);
        } catch (Exception e) {
            _log.error("______________推送售后补发货订单 {} 发货信息调用售后微服务出错________{}_______", orderTrackId, e);
        }
    }

    /**
     * 回传平台发货信息
     *
     * @param sysOrder
     */
    public void deliverInfoCallBack(SysOrder sysOrder) {
        _log.info("____________回传平台的系统订单号为__________{}_________", sysOrder.getSysOrderId());
        Byte orderSource = sysOrder.getOrderSource();
        long count = sysOrder.getSysOrderDetails().stream().filter(x -> StringUtils.isBlank(x.getSourceOrderLineItemId())).count();
        if (count >= 1) {
            return;
        }
        if (!this.isOpenTest()) {
            return;
        }
        try {
            switch (orderSource) {
                case 4:
                    List<EbayOrder> list = systemOrderService.constructEbayOrderDeliverInfo(sysOrder);
                    for (EbayOrder order : list) {
                        _log.info("____________回传eBay平台订单发货数据对象__________{}_________", order);
                    }
                    systemOrderService.sendEbayOrderDeliverInfo(list, sysOrder);
                    break;
                case 5:
                    List<AmazonOrderDetail> detailList = systemOrderService.sendAmazonOrderDeliverInfo(sysOrder);
                    for (AmazonOrderDetail order : detailList) {
                        _log.info("____________回传Amazon平台订单发货数据对象__________{}_________", order);
                    }
                    break;
                case 6:
                    _log.info("_________速卖通订单：orderId={}", sysOrder.getSourceOrderId());
                    systemOrderService.sendAliexpressDeliverInfo(sysOrder);
                    break;
                default:
                    _log.info("未找到回传信息所对应的的订单信息");
            }
        } catch (Exception e) {
            _log.error("____________回传平台订单发货数据出错__________", e);
        }
    }

    private boolean isOpenTest() {
        Boolean result = true;
        String[] activeProfiles = env.getActiveProfiles();
        List<String> list = Arrays.asList(activeProfiles);

        String value = Constants.SysConfig.OPEN_TEST_BACK_MARK_YES;
        List<SysConfig> sysConfigs = sysConfigMapper.querySysConfigKey(Constants.SysConfig.OPEN_TEST_BACK_MARK);
        if (null != sysConfigs && sysConfigs.size() > 0) {
            value = sysConfigs.get(0).getValue();
        }
        //如果是测试环境，判断是否开启
        if (!list.contains(Constants.SysConfig.SYS_PROD)) {
            if (value.equals(Constants.SysConfig.OPEN_TEST_BACK_MARK_NO)) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public List<EbayOrder> constructEbayOrderDeliverInfoNew(SysOrderPackage deliveredPackage) {
        String sysOrderId = deliveredPackage.getSysOrderId();
        String orderTrackId = deliveredPackage.getOrderTrackId();
        _log.info("订单{}, 包裹{}, 准备组装回传数据", sysOrderId, orderTrackId);
        List<SysOrderPackageDetail> deliveredPackageDetail = deliveredPackage.getSysOrderPackageDetailList();
        List<EbayOrderDetail> ebayOrderDetailList = ebayOrderDetailMapper.queryBatchEbayOrderDetailByOrderLineItemId(
                this.getSourceOrderLineItemIdListNew(deliveredPackageDetail));
        if (CollectionUtils.isEmpty(ebayOrderDetailList)) {
            _log.info("包裹{} 根据平台订单项ID查出平台订单项实体类--无数据", orderTrackId);
            return null;
        }
        String operateStatus = deliveredPackage.getOperateStatus();
        // 获取需要回标的ebay订单号
        Set<String> ebayOrderIdSet = ebayOrderDetailList.stream().map(x -> x.getOrderId()).collect(Collectors.toSet());
        List<EbayOrder> ebayOrderList = new ArrayList<>();
        for (String ebayOrderId : ebayOrderIdSet) {
            EbayOrder ebayOrder = new EbayOrder();
            ebayOrder.setOrderId(ebayOrderId);
            List<EbayOrderDetail> list = new ArrayList<>();
            for (EbayOrderDetail detail : ebayOrderDetailList) {
                String orderItemID = detail.getOrderLineItemId();
                Integer qty = detail.getQuantityPurchased();
                String alreadyMark = detail.getRemark();
                Map<String, Integer> alreadyMarkMap = new HashMap<>();
                if (ebayOrderId.equals(detail.getOrderId())) {
                    //TODO 回传平台发货信息设置具体数量：已拆分订单商品数量取系统订单发货商品数量，其他取平台订单项商品数量
                    if (Objects.equals(operateStatus, OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                        for (SysOrderPackageDetail sysOrderPackageDetail : deliveredPackageDetail) {
                            Integer skuQuantity = sysOrderPackageDetail.getSkuQuantity();
                            if (skuQuantity == 0) {
                                continue;
                            }
                            String sysSourceItemID = sysOrderPackageDetail.getSourceOrderLineItemId();
                            if (sysSourceItemID.contains("#")) {//多对一情况
                                if (sysSourceItemID.contains(orderItemID)) {
                                    if (StringUtils.isNotBlank(alreadyMark)) {
                                        alreadyMarkMap = JSONObject.parseObject(alreadyMark, Map.class);
                                        int sum = alreadyMarkMap.values().stream().mapToInt(Integer::intValue).sum();
                                        if (sum + skuQuantity > qty) {
                                            detail.setQuantityPurchased(qty - sum);
                                            alreadyMarkMap.put(sysOrderId, qty - sum);
                                            sysOrderPackageDetail.setSkuQuantity(sum + skuQuantity - qty);
                                            list.add(detail);
                                        } else {
                                            detail.setQuantityPurchased(skuQuantity);
                                            alreadyMarkMap.put(sysOrderId, skuQuantity);
                                            sysOrderPackageDetail.setSkuQuantity(0);
                                            list.add(detail);
                                        }
                                    } else {
                                        if (skuQuantity.compareTo(qty) > 0) {
                                            detail.setQuantityPurchased(qty);
                                            sysOrderPackageDetail.setSkuQuantity(skuQuantity - qty);
                                            alreadyMarkMap.put(sysOrderId, qty);
                                            list.add(detail);
                                        } else {
                                            detail.setQuantityPurchased(skuQuantity);
                                            sysOrderPackageDetail.setSkuQuantity(0);
                                            alreadyMarkMap.put(sysOrderId, skuQuantity);
                                            list.add(detail);
                                        }
                                    }
                                }
                            } else if (sysOrderPackageDetail.getSourceOrderLineItemId().equals(detail.getOrderLineItemId())) {
                                detail.setQuantityPurchased(skuQuantity);
                            }
                        }
                        detail.setRemark(FastJsonUtils.toJsonString(alreadyMarkMap));
                    } else {
                        list.add(detail);
                    }
                    ebayOrder.setEbayOrderDetails(list);
                }
            }
            ebayOrderList.add(ebayOrder);
        }
        return ebayOrderList;
    }

    @Override
    public List<EbayOrder> constructEbayOrderDeliverInfo(SysOrder sysOrder) {
        String sysOrderId = sysOrder.getSysOrderId();
        List<EbayOrderDetail> ebayOrderDetailList = ebayOrderDetailMapper.queryBatchEbayOrderDetailByOrderLineItemId(this.getSourceOrderLineItemIdList(sysOrder));
        if (CollectionUtils.isEmpty(ebayOrderDetailList) || ebayOrderDetailList.size() == 0) {
            _log.info("______________根据平台订单项ID查出平台订单项实体类出错_______________系统订单号为：{}_______________", sysOrderId);
            return null;
        }
        Byte splittedOrMerged = sysOrder.getSplittedOrMerged();
        Set<String> ebayOrderIdSet = ebayOrderDetailList.stream().map(x -> x.getOrderId()).collect(Collectors.toSet());
        List<EbayOrder> ebayOrderList = new ArrayList<>();
        for (String ebayOrderId : ebayOrderIdSet) {
            EbayOrder ebayOrder = new EbayOrder();
            ebayOrder.setOrderId(ebayOrderId);
            List<EbayOrderDetail> list = new ArrayList<>();
            for (EbayOrderDetail detail : ebayOrderDetailList) {
                String orderItemID = detail.getOrderLineItemId();
                Integer qty = detail.getQuantityPurchased();
                String alreadyMark = detail.getRemark();
                Map<String, Integer> alreadyMarkMap = new HashMap<>();
                if (ebayOrderId.equals(detail.getOrderId())) {
                    //TODO 回传平台发货信息设置具体数量：已拆分订单商品数量取系统订单发货商品数量，其他取平台订单项商品数量
                    if ((byte) 1 == splittedOrMerged) {
                        for (SysOrderDetail sysDetail : sysOrder.getSysOrderDetails()) {
                            Integer skuQuantity = sysDetail.getSkuQuantity();
                            if (skuQuantity == 0) continue;
                            String sysSourceItemID = sysDetail.getSourceOrderLineItemId();
                            if (sysSourceItemID.contains("#")) {//多对一情况
                                if (sysSourceItemID.contains(orderItemID)) {
                                    if (StringUtils.isNotBlank(alreadyMark)) {
                                        alreadyMarkMap = JSONObject.parseObject(alreadyMark, Map.class);
                                        int sum = alreadyMarkMap.values().stream().mapToInt(Integer::intValue).sum();
                                        if (sum + skuQuantity > qty) {
                                            detail.setQuantityPurchased(qty - sum);
                                            alreadyMarkMap.put(sysOrderId, qty - sum);
                                            sysDetail.setSkuQuantity(sum + skuQuantity - qty);
                                            list.add(detail);
                                        } else {
                                            detail.setQuantityPurchased(skuQuantity);
                                            alreadyMarkMap.put(sysOrderId, skuQuantity);
                                            sysDetail.setSkuQuantity(0);
                                            list.add(detail);
                                        }
                                    } else {
                                        if (skuQuantity.compareTo(qty) > 0) {
                                            detail.setQuantityPurchased(qty);
                                            sysDetail.setSkuQuantity(skuQuantity - qty);
                                            alreadyMarkMap.put(sysOrderId, qty);
                                            list.add(detail);
                                        } else {
                                            detail.setQuantityPurchased(skuQuantity);
                                            sysDetail.setSkuQuantity(0);
                                            alreadyMarkMap.put(sysOrderId, skuQuantity);
                                            list.add(detail);
                                        }
                                    }
                                }
                            } else if (sysDetail.getSourceOrderLineItemId().equals(detail.getOrderLineItemId()))
                                detail.setQuantityPurchased(skuQuantity);
                        }
                        detail.setRemark(FastJsonUtils.toJsonString(alreadyMarkMap));
                    } else
                        list.add(detail);
                    ebayOrder.setEbayOrderDetails(list);
                }
            }
            ebayOrderList.add(ebayOrder);
        }
        return ebayOrderList;
    }

    @Override
    public void sendEbayOrderDeliverInfoNew(List<EbayOrder> list, SysOrderNew sysOrder, SysOrderPackage deliveredPackage) {
        Integer warehouseId = deliveredPackage.getDeliveryWarehouseId();
        boolean isGoodCangOrder = systemOrderCommonService.isGoodCangWarehouse(String.valueOf(warehouseId));
        String warehouseType = isGoodCangOrder ? "GC" : "ERP";
        String shipTrackNumber = deliveredPackage.getShipTrackNumber();
        String shipOrderId = deliveredPackage.getShipOrderId();
        String shippingCarrierUsed = deliveredPackage.getShippingCarrierUsed();
        for (EbayOrder ebayOrder : list) {
            List<EbayOrderDetail> ebayOrderDetails = new ArrayList<>();
            try {
                ApiContext apiContext = this.getApiContextForSendBackDeliverInfoNew(sysOrder.getPlatformSellerAccount());
                CompleteSaleCall completeSaleCall = new CompleteSaleCall(apiContext);
                CompleteSaleRequestType completeSaleRequestType = new CompleteSaleRequestType();
                ShipmentType shipmentType = new ShipmentType();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(deliveredPackage.getDeliveryTime());
                shipmentType.setShippedTime(calendar);
                ebayOrderDetails = ebayOrder.getEbayOrderDetails();
                int size = ebayOrderDetails.size();
                LineItemType[] arrayLineItemType = new LineItemType[size];
                for (int i = 0; i < size; i++) {
                    LineItemType lineItemType = new LineItemType();
                    lineItemType.setItemID(ebayOrderDetails.get(i).getItemId());
                    lineItemType.setTransactionID(ebayOrderDetails.get(i).getTransactionId());
                    lineItemType.setQuantity(ebayOrderDetails.get(i).getQuantityPurchased());
                    arrayLineItemType[i] = lineItemType;
                }
                ShipmentLineItemType shipmentLineItemType = new ShipmentLineItemType();
                shipmentLineItemType.setLineItem(arrayLineItemType);
                ShipmentTrackingDetailsType[] arrayShipmentTrackingDetailsType = new ShipmentTrackingDetailsType[1];
                ShipmentTrackingDetailsType shipmentTrackingDetailsType = new ShipmentTrackingDetailsType();
                shipmentTrackingDetailsType.setShipmentTrackingNumber(StringUtils.isBlank(shipTrackNumber) ?
                        shipOrderId : shipTrackNumber);
                shipmentTrackingDetailsType.setShippingCarrierUsed(deliveredPackage.getEbayCarrierName());
                shipmentTrackingDetailsType.setShipmentLineItem(shipmentLineItemType);
                arrayShipmentTrackingDetailsType[0] = shipmentTrackingDetailsType;
                shipmentType.setShipmentTrackingDetails(arrayShipmentTrackingDetailsType);
                completeSaleRequestType.setOrderID(ebayOrder.getOrderId());
                completeSaleRequestType.setShipment(shipmentType);
                completeSaleRequestType.setShipped(true);
                _log.info("上传ebay平台订单发货的参数为: {}", FastJsonUtils.toJsonString(completeSaleRequestType));
                AbstractResponseType response = completeSaleCall.execute(completeSaleRequestType);
                _log.info("______________上传ebay平台订单:{} 的发货数据完成________________", ebayOrder.getOrderId());
                AckCodeType ack = response.getAck();
                for (EbayOrderDetail detail : ebayOrderDetails) {
                    detail.setShipmentTrackingNumber(StringUtils.isBlank(shipTrackNumber) ?
                            deliveredPackage.getShipOrderId() : shipTrackNumber);
                    detail.setDeliveryTime(TimeUtil.dateToString(deliveredPackage.getDeliveryTime(), "yyyy-MM-dd HH:mm:ss"));
                    detail.setShippingCarrierUsed(shippingCarrierUsed);
                    if (OrderHandleEnum.SuccessOrFailure.SUCCESS.getTypeName().equalsIgnoreCase(ack.value())) {
                        detail.setMarkDeliverStatus(OrderHandleEnum.MarkDeliverStatus.MARK_SUCCESS.getValue());
                    }
                    if (OrderHandleEnum.SuccessOrFailure.FAILURE.getTypeName().equalsIgnoreCase(ack.value())) {
                        detail.setMarkDeliverStatus(OrderHandleEnum.MarkDeliverStatus.MARK_FAILURE.getValue());
                    }
                }
                for (EbayOrderDetail detail : ebayOrderDetails) {
                    detail.setUpdateBy(warehouseType + "DeliverGoods");
                    ebayOrderDetailMapper.updateMarkDeliverStatusByOrderItemId(detail);
                    _log.info("_______________更新ebay订单项表  标记发货状态  成功________标记值为{}________", detail.getMarkDeliverStatus());
                }
            } catch (Exception e) {
                _log.error("_____________上传ebay平台订单:{} 发货数据出错________{}________", sysOrder.getSourceOrderId(), e);
                for (EbayOrderDetail detail : ebayOrderDetails) {
                    detail.setShipmentTrackingNumber(StringUtils.isBlank(shipTrackNumber) ?
                            shipOrderId : shipTrackNumber);
                    detail.setDeliveryTime(TimeUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    detail.setShippingCarrierUsed(shippingCarrierUsed);
                    detail.setMarkDeliverStatus((byte) 2);
                }
                for (EbayOrderDetail detail : ebayOrderDetails) {
                    detail.setUpdateBy(warehouseType + "DeliverGoods");
                    ebayOrderDetailMapper.updateMarkDeliverStatusByOrderItemId(detail);
                }
                _log.info("_______________________发货更新ebay订单项表  标记发货状态  成功_______________________");
            }
        }
    }

    /**
     * 回传ebay平台订单发货信息
     *
     * @param list
     * @param sysOrder
     */
    @Override
    public void sendEbayOrderDeliverInfo(List<EbayOrder> list, SysOrder sysOrder) {
        String warehouseCode = sysOrder.getDeliveryWarehouseCode();
        String warehouseType = warehouseCode.startsWith("GC_") ? "GC" : "ERP";
        for (EbayOrder ebayOrder : list) {
            List<EbayOrderDetail> ebayOrderDetails = new ArrayList<>();
            try {
                ApiContext apiContext = this.getApiContextForSendBackDeliverInfo(sysOrder);
                CompleteSaleCall completeSaleCall = new CompleteSaleCall(apiContext);
                CompleteSaleRequestType completeSaleRequestType = new CompleteSaleRequestType();
                ShipmentType shipmentType = new ShipmentType();
                shipmentType.setShippedTime(TimeUtil.strToCalendar(sysOrder.getDeliveryTime()));
                ebayOrderDetails = ebayOrder.getEbayOrderDetails();
                int size = ebayOrderDetails.size();
                LineItemType[] arrayLineItemType = new LineItemType[size];
                for (int i = 0; i < size; i++) {
                    LineItemType lineItemType = new LineItemType();
                    lineItemType.setItemID(ebayOrderDetails.get(i).getItemId());
                    lineItemType.setTransactionID(ebayOrderDetails.get(i).getTransactionId());
                    lineItemType.setQuantity(ebayOrderDetails.get(i).getQuantityPurchased());
                    arrayLineItemType[i] = lineItemType;
                }
                ShipmentLineItemType shipmentLineItemType = new ShipmentLineItemType();
                shipmentLineItemType.setLineItem(arrayLineItemType);
                ShipmentTrackingDetailsType[] arrayShipmentTrackingDetailsType = new ShipmentTrackingDetailsType[1];
                ShipmentTrackingDetailsType shipmentTrackingDetailsType = new ShipmentTrackingDetailsType();
                shipmentTrackingDetailsType.setShipmentTrackingNumber(sysOrder.getShipTrackNumber());
                shipmentTrackingDetailsType.setShippingCarrierUsed(sysOrder.getEbayCarrierName());
                shipmentTrackingDetailsType.setShipmentLineItem(shipmentLineItemType);
                arrayShipmentTrackingDetailsType[0] = shipmentTrackingDetailsType;
                shipmentType.setShipmentTrackingDetails(arrayShipmentTrackingDetailsType);
                completeSaleRequestType.setOrderID(ebayOrder.getOrderId());
                completeSaleRequestType.setShipment(shipmentType);
                completeSaleRequestType.setShipped(true);
                AbstractResponseType response = completeSaleCall.execute(completeSaleRequestType);
                _log.info("______________上传ebay平台订单:{} 的发货数据完成________________", ebayOrder.getOrderId());
                AckCodeType ack = response.getAck();
                for (EbayOrderDetail detail : ebayOrderDetails) {
                    detail.setShipmentTrackingNumber(StringUtils.isBlank(sysOrder.getShipTrackNumber()) ? sysOrder.getShipOrderId() : sysOrder.getShipTrackNumber());
                    detail.setDeliveryTime(TimeUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    detail.setShippingCarrierUsed(sysOrder.getShippingCarrierUsed());
                    if (OrderHandleEnum.SuccessOrFailure.SUCCESS.getTypeName().equalsIgnoreCase(ack.value()))
                        detail.setMarkDeliverStatus(OrderHandleEnum.MarkDeliverStatus.MARK_SUCCESS.getValue());
                    if (OrderHandleEnum.SuccessOrFailure.FAILURE.getTypeName().equalsIgnoreCase(ack.value()))
                        detail.setMarkDeliverStatus(OrderHandleEnum.MarkDeliverStatus.MARK_FAILURE.getValue());
                }
                for (EbayOrderDetail detail : ebayOrderDetails) {
                    detail.setUpdateBy(warehouseType + "DeliverGoods");
                    ebayOrderDetailMapper.updateMarkDeliverStatusByOrderItemId(detail);
                    _log.info("_______________更新ebay订单项表  标记发货状态  成功________标记值为{}________", detail.getMarkDeliverStatus());
                }
            } catch (Exception e) {
                _log.error("_____________上传ebay平台订单:{} 发货数据出错________{}________", sysOrder.getSourceOrderId(), e);
                for (EbayOrderDetail detail : ebayOrderDetails) {
                    detail.setShipmentTrackingNumber(sysOrder.getShipTrackNumber() == null ? sysOrder.getShipOrderId() : sysOrder.getShipTrackNumber());
                    detail.setDeliveryTime(TimeUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    detail.setShippingCarrierUsed(sysOrder.getShippingCarrierUsed());
                    detail.setMarkDeliverStatus((byte) 2);
                }
                for (EbayOrderDetail detail : ebayOrderDetails) {
                    detail.setUpdateBy(warehouseType + "DeliverGoods");
                    ebayOrderDetailMapper.updateMarkDeliverStatusByOrderItemId(detail);
                }
                _log.info("_______________________发货更新ebay订单项表  标记发货状态  成功_______________________");
            }
        }
    }

    /**
     * 获取ebay apiContext内容
     *
     * @param platformSellerAccount 卖家品连店铺账号
     * @return {@link ApiContext}
     */
    private ApiContext getApiContextForSendBackDeliverInfoNew(String platformSellerAccount) {
        Empower empower = systemOrderCommonService.findOneEmpowerByAccount(PlatformRuleCovertToUserServicePlatformEnum
                .E_BAY.getUserServicePlatform(), platformSellerAccount, null, null);

        ApiContext apiContext = new ApiContext();
        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(appid);
        apiAccount.setDeveloper(developer);
        apiAccount.setCertificate(cert);
        apiContext.getApiCredential().setApiAccount(apiAccount);
        apiContext.getApiCredential().seteBayToken(empower.getToken());
        apiContext.setTimeout(30000);//毫秒
        apiContext.setApiServerUrl(eBayServerUrl);
        return apiContext;
    }

    private ApiContext getApiContextForSendBackDeliverInfo(SysOrder sysOrder) {
        String oneEmpowByAccount = remoteSellerService.findOneEmpowByAccount(this.getPlatform("eBay"),
                sysOrder.getPlatformSellerAccount(), null, null);
        String dataString = Utils.returnRemoteResultDataString(oneEmpowByAccount, "卖家服务异常");
        Empower empower = JSONObject.parseObject(dataString, Empower.class);
        ApiContext apiContext = new ApiContext();
        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setApplication(appid);
        apiAccount.setDeveloper(developer);
        apiAccount.setCertificate(cert);
        apiContext.getApiCredential().setApiAccount(apiAccount);
        apiContext.getApiCredential().seteBayToken(empower.getToken());
        apiContext.setTimeout(30000);//毫秒
        apiContext.setApiServerUrl(eBayServerUrl);
        return apiContext;
    }

    /**
     * 调用授权服务是转换平台格式
     *
     * @param platform 平台字符串格式
     * @return 平台Integer格式
     */
    private Integer getPlatform(String platform) {
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.AMAZON.getPlatform())) {
            return 2;
        }
        if (platform.equalsIgnoreCase(OrderRuleEnum.platformEnm.E_BAU.getPlatform())) {
            return 1;
        }
        return null;
    }

    @Override
    public List<AmazonOrderDetail> sendAmazonOrderDeliverInfoNew(SysOrderNew sysOrder, SysOrderPackage deliveredPackage) {
        try {
            String sysOrderId = sysOrder.getSysOrderId();
            String orderTrackId = deliveredPackage.getOrderTrackId();
            _log.error("进入亚马逊回标,系统订单号为：{}, 包裹为: {}", sysOrderId, orderTrackId);
            List<SysOrderPackageDetail> packageDetailList = deliveredPackage.getSysOrderPackageDetailList();
            List<AmazonOrderDetail> amazonOrderDetailList = amazonOrderDetailMapper.
                    queryBatchAmazonOrderDetailByOrderLineItemId(this.getSourceOrderLineItemIdListNew(packageDetailList));
            if (CollectionUtils.isEmpty(amazonOrderDetailList)) {
                _log.error("根据平台订单项ID查出平台订单项实体类出错,系统订单号为：{}, 包裹为: {}", sysOrderId, orderTrackId);
                return null;
            }
            String operateStatus = deliveredPackage.getOperateStatus();
            Set<String> amazonOrderIdSet = amazonOrderDetailList.stream().map(x -> x.getOrderId()).collect(Collectors.toSet());
            List<AmazonOrderDetail> amazonOrderDetails = new ArrayList<>();
            String shipTrackNumber = deliveredPackage.getShipTrackNumber();
            String shipOrderId = deliveredPackage.getShipOrderId();
            String deliveryTime = TimeUtil.dateToString(deliveredPackage.getDeliveryTime());
            String amazonCarrierName = deliveredPackage.getAmazonCarrierName();
            String amazonShippingMethod = deliveredPackage.getAmazonShippingMethod();
            for (String amazonOrderId : amazonOrderIdSet) {
                AmazonDelivery amazonDelivery = new AmazonDelivery();
                amazonDelivery.setAmazonShopName(sysOrder.getPlatformSellerAccount());
                amazonDelivery.setPlSellerAccount(sysOrder.getSellerPlAccount());
                amazonDelivery.setAmazonSellerAccount(sysOrder.getPlatformSellerId());
                amazonDelivery.setMarketplaceId(sysOrder.getMarketplaceId());
                List<AmazonOrderDetail> list = new ArrayList<>();

                for (AmazonOrderDetail detail : amazonOrderDetailList) {
                    String orderItemID = detail.getAmazonOrderitemId();
                    Integer qty = detail.getQuantity();
                    String alreadyMark = detail.getRemark();
                    Map<String, Integer> alreadyMarkMap = new HashMap<>();
                    if (amazonOrderId.equals(detail.getOrderId())) {
                        detail.setShipmentTrackingNumber(StringUtils.isBlank(shipTrackNumber) ? shipOrderId : shipTrackNumber);//跟踪单号
                        detail.setMarkDeliverStatus(OrderHandleEnum.MarkDeliverStatus.MARK_SUCCESS.getValue());
                        detail.setDeliveryTime(deliveryTime);
                        detail.setCarrierName(amazonCarrierName);//映射后的Amazon物流商名称
                        detail.setShippingMethod(amazonShippingMethod);//映射后的Amazon配送方式
                        if (Objects.equals(operateStatus, OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                            for (SysOrderDetail sysDetail : sysOrder.getSysOrderDetails()) {
                                Integer skuQuantity = sysDetail.getSkuQuantity();
                                if (skuQuantity == 0) {
                                    continue;
                                }
                                String sysSourceItemID = sysDetail.getSourceOrderLineItemId();
                                if (sysSourceItemID.contains("#")) {//多对一情况
                                    if (sysSourceItemID.contains(orderItemID)) {
                                        if (StringUtils.isNotBlank(alreadyMark)) {
                                            alreadyMarkMap = JSONObject.parseObject(alreadyMark, Map.class);
                                            int sum = alreadyMarkMap.values().stream().mapToInt(Integer::intValue).sum();
                                            if (sum + skuQuantity > qty) {
                                                detail.setQuantity(qty - sum);
                                                alreadyMarkMap.put(sysOrderId, qty - sum);
                                                sysDetail.setSkuQuantity(sum + skuQuantity - qty);
                                                list.add(detail);
                                                amazonOrderDetails.add(detail);
                                            } else {
                                                detail.setQuantity(skuQuantity);
                                                alreadyMarkMap.put(sysOrderId, skuQuantity);
                                                sysDetail.setSkuQuantity(0);
                                                list.add(detail);
                                                amazonOrderDetails.add(detail);
                                            }
                                        } else {
                                            if (skuQuantity.compareTo(qty) > 0) {
                                                detail.setQuantity(qty);
                                                sysDetail.setSkuQuantity(skuQuantity - qty);
                                                alreadyMarkMap.put(sysOrderId, qty);
                                                list.add(detail);
                                                amazonOrderDetails.add(detail);
                                            } else {
                                                detail.setQuantity(skuQuantity);
                                                sysDetail.setSkuQuantity(0);
                                                alreadyMarkMap.put(sysOrderId, skuQuantity);
                                                list.add(detail);
                                                amazonOrderDetails.add(detail);
                                            }
                                        }
                                    }
                                } else if (sysDetail.getSourceOrderLineItemId().equals(detail.getAmazonOrderitemId())) {
                                    detail.setQuantity(sysDetail.getSkuQuantity());
                                }
                            }
                            detail.setRemark(FastJsonUtils.toJsonString(alreadyMarkMap));
                        } else {
                            amazonOrderDetails.add(detail);
                        }
                        amazonDelivery.setAmazonOrderDetailList(list);
                        amazonOrderDetailMapper.updateDeliverInfoByAmazonOrderItemId(detail);
                    }
                }
                amazonDelivery.setAmazonOrderDetails(FastJsonUtils.toJsonString(amazonOrderDetails));
                _log.error("___________Amazon订单发货插入发货数据_______{}_______", amazonDelivery);
                amazonDeliveryMapper.insertSelective(amazonDelivery);
            }
            return amazonOrderDetails;
        } catch (Exception e) {
            _log.error("Amazon订单发货插入发货数据到回传平台发货数据表格出错", e);
        }
        return null;
    }

    @Override
    public List<AmazonOrderDetail> sendAmazonOrderDeliverInfo(SysOrder sysOrder) {
        try {
            String sysOrderId = sysOrder.getSysOrderId();
            List<AmazonOrderDetail> amazonOrderDetailList = amazonOrderDetailMapper.
                    queryBatchAmazonOrderDetailByOrderLineItemId(this.getSourceOrderLineItemIdList(sysOrder));
            if (CollectionUtils.isEmpty(amazonOrderDetailList) || amazonOrderDetailList.size() == 0) {
                _log.info("__________根据平台订单项ID查出平台订单项实体类出错___________系统订单号为：{}____________", sysOrderId);
                return null;
            }
            Byte splittedOrMerged = sysOrder.getSplittedOrMerged();
            Set<String> amazonOrderIdSet = amazonOrderDetailList.stream().map(x -> x.getOrderId()).collect(Collectors.toSet());
            List<AmazonOrderDetail> amazonOrderDetails = new ArrayList<>();
            for (String amazonOrderId : amazonOrderIdSet) {
                AmazonDelivery amazonDelivery = new AmazonDelivery();
                amazonDelivery.setAmazonShopName(sysOrder.getPlatformSellerAccount());
                amazonDelivery.setPlSellerAccount(sysOrder.getSellerPlAccount());
                amazonDelivery.setAmazonSellerAccount(sysOrder.getPlatformSellerId());
                amazonDelivery.setMarketplaceId(sysOrder.getMarketplaceId());
                List<AmazonOrderDetail> list = new ArrayList<>();
                for (AmazonOrderDetail detail : amazonOrderDetailList) {
                    String orderItemID = detail.getAmazonOrderitemId();
                    Integer qty = detail.getQuantity();
                    String alreadyMark = detail.getRemark();
                    Map<String, Integer> alreadyMarkMap = new HashMap<>();
                    if (amazonOrderId.equals(detail.getOrderId())) {
                        detail.setShipmentTrackingNumber(StringUtils.isBlank(sysOrder.getShipTrackNumber()) ? sysOrder.getShipOrderId() : sysOrder.getShipTrackNumber());//跟踪单号
                        detail.setMarkDeliverStatus(OrderHandleEnum.MarkDeliverStatus.MARK_SUCCESS.getValue());
                        detail.setDeliveryTime(sysOrder.getDeliveryTime());
                        detail.setCarrierName(sysOrder.getAmazonCarrierName());//映射后的Amazon物流商名称
                        detail.setShippingMethod(sysOrder.getAmazonShippingMethod());//映射后的Amazon配送方式
                        if ((byte) 1 == splittedOrMerged) {
                            for (SysOrderDetail sysDetail : sysOrder.getSysOrderDetails()) {
                                Integer skuQuantity = sysDetail.getSkuQuantity();
                                if (skuQuantity == 0) continue;
                                String sysSourceItemID = sysDetail.getSourceOrderLineItemId();
                                if (sysSourceItemID.contains("#")) {//多对一情况
                                    if (sysSourceItemID.contains(orderItemID)) {
                                        if (StringUtils.isNotBlank(alreadyMark)) {
                                            alreadyMarkMap = JSONObject.parseObject(alreadyMark, Map.class);
                                            int sum = alreadyMarkMap.values().stream().mapToInt(Integer::intValue).sum();
                                            if (sum + skuQuantity > qty) {
                                                detail.setQuantity(qty - sum);
                                                alreadyMarkMap.put(sysOrderId, qty - sum);
                                                sysDetail.setSkuQuantity(sum + skuQuantity - qty);
                                                list.add(detail);
                                                amazonOrderDetails.add(detail);
                                            } else {
                                                detail.setQuantity(skuQuantity);
                                                alreadyMarkMap.put(sysOrderId, skuQuantity);
                                                sysDetail.setSkuQuantity(0);
                                                list.add(detail);
                                                amazonOrderDetails.add(detail);
                                            }
                                        } else {
                                            if (skuQuantity.compareTo(qty) > 0) {
                                                detail.setQuantity(qty);
                                                sysDetail.setSkuQuantity(skuQuantity - qty);
                                                alreadyMarkMap.put(sysOrderId, qty);
                                                list.add(detail);
                                                amazonOrderDetails.add(detail);
                                            } else {
                                                detail.setQuantity(skuQuantity);
                                                sysDetail.setSkuQuantity(0);
                                                alreadyMarkMap.put(sysOrderId, skuQuantity);
                                                list.add(detail);
                                                amazonOrderDetails.add(detail);
                                            }
                                        }
                                    }
                                } else if (sysDetail.getSourceOrderLineItemId().equals(detail.getAmazonOrderitemId()))
                                    detail.setQuantity(sysDetail.getSkuQuantity());
                            }
                            detail.setRemark(FastJsonUtils.toJsonString(alreadyMarkMap));
                        } else
                            amazonOrderDetails.add(detail);
                        amazonDelivery.setAmazonOrderDetailList(list);
                        amazonOrderDetailMapper.updateDeliverInfoByAmazonOrderItemId(detail);
                    }
                }
                amazonDelivery.setAmazonOrderDetails(FastJsonUtils.toJsonString(amazonOrderDetails));
                _log.info("___________Amazon订单发货插入发货数据_______{}_______", amazonDelivery);
                amazonDeliveryMapper.insertSelective(amazonDelivery);
            }
            return amazonOrderDetails;
        } catch (Exception e) {
            _log.error("___________Amazon订单发货插入发货数据到回传平台发货数据表格出错_______{}_______", e);
        }
        return null;
    }

    private List<String> getSourceOrderLineItemIdListNew(List<SysOrderPackageDetail> packageDetails) {
        Set<String> sourceOrderLineItemIdSet = new HashSet<>();
        for (SysOrderPackageDetail detail : packageDetails) {
            String sourceOrderLineItemId = detail.getSourceOrderLineItemId();
            if (sourceOrderLineItemId.contains("#")) {
                String[] sourceOrderLineItemIdSplit = sourceOrderLineItemId.split("#");
                sourceOrderLineItemIdSet.addAll(Arrays.asList(sourceOrderLineItemIdSplit));
            } else sourceOrderLineItemIdSet.add(sourceOrderLineItemId);
        }
        return new ArrayList<>(sourceOrderLineItemIdSet);
    }

    private List<String> getSourceOrderLineItemIdList(SysOrder sysOrder) {
        Set<String> sourceOrderLineItemIdSet = new HashSet<>();
        for (SysOrderDetail detail : sysOrder.getSysOrderDetails()) {
            String sourceOrderLineItemId = detail.getSourceOrderLineItemId();
            if (sourceOrderLineItemId.contains("#")) {
                String[] sourceOrderLineItemIdSplit = sourceOrderLineItemId.split("#");
                sourceOrderLineItemIdSet.addAll(Arrays.asList(sourceOrderLineItemIdSplit));
            } else sourceOrderLineItemIdSet.add(sourceOrderLineItemId);
        }
        List<String> sourceOrderLineItemIdList = new ArrayList<>(sourceOrderLineItemIdSet);
        return sourceOrderLineItemIdList;
    }

    @Override
    public void pushDeliverInfoToWareHouse(String orderTrackId) {
        SysOrder sysOrder = sysOrderMapper.selectSysOrderByOrderTrackId(orderTrackId);
        DeliveryRecord deliveryRecord = BeanConvertor.convertBean(sysOrder, DeliveryRecord.class);
        deliveryRecord.setCreateDate(null);
        Byte orderDeliveryStatus = sysOrder.getOrderDeliveryStatus();
        if (orderDeliveryStatus == 5) {
            deliveryRecord.setOrderStatus(1);
        }
        if (orderDeliveryStatus == 6) {
            deliveryRecord.setOrderStatus(2);
        }
        List<DeliveryDetail> deliveryDetailList = new ArrayList<>();
        sysOrder.getSysOrderDetails().forEach(x -> {
            deliveryDetailList.add(BeanConvertor.convertBean(x, DeliveryDetail.class));
        });
        deliveryRecord.setDeliveryDetailList(deliveryDetailList);
        try {
            String data = remoteSupplierService.syncDeliveryRecord(new ArrayList<DeliveryRecord>() {{
                add(deliveryRecord);
            }});
            _log.info("___直接远程调用出库记录返回结果：{},请求参数为：{}", data, new ArrayList<DeliveryRecord>() {{
                add(deliveryRecord);
            }}.toString());
            JSONObject json = JSONObject.parseObject(data);
            if (!json.getString("errorCode").equalsIgnoreCase(ResponseCodeEnum.RETURN_CODE_100200.getCode())) {
                String message = StringUtils.isNotEmpty(json.getString("msg")) ? json.getString("msg") : "生成出库记录异常";
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, message);
            }
        } catch (Exception e) {
            _log.error("____________生成出库记录出错_____________使用MQ推送出库记录_____________", e);
            orderMessageSender.sendDeliverInfoToWareHouseMQ(deliveryRecord);
            _log.error("_____使用MQ推送出库记录，请求参数为：{}", deliveryRecord.toString());
        }
        _log.info("_____________推送订单:{} 出库信息成功____________", sysOrder.getSysOrderId());
    }

    @Override
    public void exportEbayOrderListExcel(HashMap hashMap) {
        String title = "ebay平台订单列表";
        String[] rowsName = new String[]{Utils.translation("平台订单号"), Utils.translation("卖家店铺账号"), Utils.translation("卖家品连账号"), Utils.translation("订单创建时间"),
                Utils.translation("商品SKU"), Utils.translation("商品单价"), Utils.translation("商品数量"), Utils.translation("总售价"), Utils.translation("订单状态"),
                Utils.translation("订单转化状态"), Utils.translation("收货人"), Utils.translation("联系电话"), Utils.translation("国家"), Utils.translation("省/州"), Utils.translation("城市"),
                Utils.translation("地址"), Utils.translation("邮编")};
        List<Object[]> dataList = new ArrayList<>();
        try {
            List<EbayOrder> ebayOrderList = syncEbayOrderService.queryEbayOrderList(hashMap);
            Object[] objs;
            for (EbayOrder x : ebayOrderList) {
                objs = new Object[rowsName.length];
                objs[0] = x.getOrderId();
                objs[1] = x.getSellerPlShopAccount();
                objs[2] = x.getSellerPlAccount();
                objs[3] = x.getCreatedTime();
                String skuStr = "";
                String itemPrice = "";
                String quantityPurchased = "";//购买此SKU数量
                for (EbayOrderDetail detail : x.getEbayOrderDetails()) {
                    String variationSku = detail.getVariationSku();
                    if(StringUtils.isNotBlank(variationSku)){
                        skuStr += variationSku + "#";
                    }else{
                        skuStr += detail.getSku() + "#";
                    }
                    itemPrice += this.getAmountPrice(detail.getTransactionPrice()).setScale(2, BigDecimal.ROUND_HALF_UP) + "#";
                    quantityPurchased += detail.getQuantityPurchased() + "#";
                }
                objs[4] = skuStr;
                objs[5] = itemPrice;
                objs[6] = quantityPurchased;

                String[] split = x.getTotal().split("#");
                if (split.length > 2) {
                    objs[7] = split[0] + "_" + split[1];
                } else {
                    objs[7] = "";
                }


                switch (x.getOrderStatus()) {
                    case "Active":
                        objs[8] = Utils.translation("未完成");
                        break;
                    case "Completed":
                        objs[8] = Utils.translation("已付款");
                        break;
                    case "Cancelled":
                        objs[8] = Utils.translation("已取消");
                        break;
                    case "CancelPending":
                        objs[8] = Utils.translation("买家请求取消");
                        break;
                    case "Inactive":
                        objs[8] = Utils.translation("不活动的订单");
                        break;
                    case "InProcess":
                        objs[8] = Utils.translation("处理中");
                        break;
                }
                switch (x.getConverSysStatus()) {
                    case 0:
                        objs[9] = Utils.translation("待处理");
                        break;
                    case 1:
                        objs[9] = Utils.translation("转入成功");
                        break;
                    case 2:
                        objs[9] = Utils.translation("转入失败");
                        break;
                    case 3:
                        objs[9] = Utils.translation("部分转入成功");
                        break;
                }
                objs[10] = x.getName();
                objs[11] = x.getPhone();
                objs[12] = x.getCountryName();
                objs[13] = x.getStateOrProvince();
                objs[14] = x.getCityName();
                objs[15] = x.getStreet1();
                objs[16] = x.getPostalCode();
                dataList.add(objs);
            }
            ExportUtil ex = new ExportUtil(title, rowsName, Utils.translation("ebay平台订单列表导出"), dataList, (HttpServletResponse) hashMap.get("response"));
            ex.export();
        } catch (Exception e) {
            _log.error("_____________ebay平台订单列表导出出错_________{}________", e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "ebay平台订单列表导出出错。。。");
        }
    }

    private BigDecimal getAmountPrice(String price) {
        if (StringUtils.isBlank(price))
            return BigDecimal.valueOf(0);
        String[] split = StringUtils.split(price, "#");
        if (split.length < 3) {
            return new BigDecimal(split[0]);
        } else {
            return new BigDecimal(split[0]).multiply(new BigDecimal(split[2]));
        }
    }

    @Override
    public Map<Object, Object> querySysOrderERPSpeedInfo(HashMap<String, String> hashMap) throws Exception {
        String sysOrderId = hashMap.get("sysOrderId");
        String orderTrackId = hashMap.get("orderTrackId");
        if (StringUtils.isBlank(sysOrderId) && StringUtils.isBlank(orderTrackId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请输入要查询的订单ID或订单跟踪号。。。");
        }
        if (!StringUtils.isBlank(sysOrderId) && !"{sysOrderId}".equals(sysOrderId)) {
            orderTrackId = sysOrderMapper.selectOrderTrackIdByOrderId(sysOrderId);
        }
        if (StringUtils.isBlank(orderTrackId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "不存在此订单。。。");
        }
        String result = remoteErpService.orderSpeedInfo(orderTrackId);
        _log.info("____________获取ERP订单物流相关信息返回结果___________{}__________", result);
        return JSONObject.parseArray(result, Map.class).get(0);
    }

    /**
     * erp订单信息_xd
     *
     * @throws Exception
     */
    @Override
    public void getSysOrderERPSpeedInfo() throws Exception {
        _log.info("批量拉取erp订单状态开始");//   TODO
        List<Integer> erpWarehouseIdList = systemOrderCommonService.getErpWarehouseIdList();
        //1,查询出ERP所有配货中的包裹
        List<SysOrderPackage> erpPackageList = sysOrderPackageMapper.getPackageByWarehouseId(erpWarehouseIdList);
        String orderTrackIds = FastJsonUtils.toJsonString(erpPackageList.stream().map(SysOrderPackage::getOrderTrackId)
                .collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(erpPackageList)) {
            return;
        }
        _log.error("批量拉取erp订单传入的跟踪号列表为: {}", orderTrackIds);
        //调用erp返回结果
        String result = remoteErpService.getOrderSpeedInfo(orderTrackIds);
        _log.error("批量拉取erp返回的包裹列表原为: {}", result);

        List<GetOrderSpeedInfoVO> orderSpeedInfoList = JSONArray.parseArray(result, GetOrderSpeedInfoVO.class);
        if (CollectionUtils.isEmpty(orderSpeedInfoList)) {
            _log.error("获取不到ERP 订单数据");
            return;
        }

        _log.error("批量拉取erp返回的包裹列表转为: {}", JSONObject.toJSONString(orderSpeedInfoList));
        for (GetOrderSpeedInfoVO orderSpeedInfo : orderSpeedInfoList) {
            String orderTrackId = null;
            try {
                orderTrackId = orderSpeedInfo.getChannel_order_number();
                String speed = orderSpeedInfo.getSpeed();
                String warehouseShipException = orderSpeedInfo.getError();
                String channelOrderNumber = orderTrackId;
                String shipTrackNumber = orderSpeedInfo.getShipping_number();
                String shipOrderId = orderSpeedInfo.getProcess_code();
                BigDecimal actualShipCost = orderSpeedInfo.getShipping_fee();

                //组装调用订单回传参数
                WareHouseDeliverCallBack deliverCallBack = new WareHouseDeliverCallBack();
                deliverCallBack.setSpeed(speed);
                deliverCallBack.setWarehouseShipException(warehouseShipException);
                deliverCallBack.setOrderTrackId(channelOrderNumber);
                deliverCallBack.setWarehouseType(Constants.WarehouseType.ERP);
                deliverCallBack.setShipTrackNumber(shipTrackNumber);
                deliverCallBack.setShipOrderId(shipOrderId);
                deliverCallBack.setActualShipCost(actualShipCost);

                //作废处理
                if (orderSpeedInfo.getOrder_status().equals(ERPOrderStatusEnum.CANCELLED.getProcess())){
                    _log.error("erp返回作废订单============={}", JSONObject.toJSONString(deliverCallBack));
                    try {
                        sysOrderExceptionHandelService.cancellationOrderHandel(deliverCallBack.getOrderTrackId());
                    } catch (Exception e) {
                        _log.error("erp返回作废订单处理失败============={}", deliverCallBack.getOrderTrackId());
                    }
                    continue;
                }

                boolean isCallWarehouseDeliver = iGoodCangService.isUpdateSysLog(channelOrderNumber, speed,
                        shipTrackNumber, shipOrderId, Constants.WarehouseType.ERP);

                if (isCallWarehouseDeliver) {
                    _log.info("(定时任务批量获取ERP订单状态)包裹{} 调用订单回传方法,内容为 {}", orderTrackId, deliverCallBack);
                    iSystemOrderService.wareHouseDeliverCallBackNew(deliverCallBack);
                } else {
                    _log.info("(定时任务批量获取ERP订单状态)包裹{} 无需调用订单回传方法,内容为 {}", orderTrackId, deliverCallBack);
                }
            } catch (Exception e) {
                _log.error("包裹{} 更新ERP的状态异常", orderTrackId);
            }
        }
        _log.info("批量拉取erp订单状态结束");
    }

    @Override
    public void updateEbayDetailDeliverStatus(String orderLineItemId) {
        List<EbayOrderDetail> detailList = ebayOrderDetailMapper.queryBatchEbayOrderDetailByOrderLineItemId(new ArrayList<String>() {{
            this.add(orderLineItemId);
        }});
        if (CollectionUtils.isEmpty(detailList))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "根据ebay订单项号查询不到数据。。。");
        EbayOrderDetail detail = detailList.get(0);
        if (OrderHandleEnum.MarkDeliverStatus.MARK_FAILURE.getValue() != detail.getMarkDeliverStatus())
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "此ebay订单项平台发货状态不能修改。。。");
        detail.setMarkDeliverStatus(OrderHandleEnum.MarkDeliverStatus.MARK_SUCCESS.getValue());
        ebayOrderDetailMapper.updateMarkDeliverStatusByOrderItemId(detail);
    }

    @Override
    public void updatePayStatus(String sysOrderId, String payStatus) {
        _log.info("__________财务修改订单付款状态传递过来的数据__________sysOrderId {}_______payStatus {}__________", sysOrderId, payStatus);
        if (StringUtils.isBlank(sysOrderId) || StringUtils.isBlank(payStatus))
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数错误。。。");
        SysOrder order = sysOrderMapper.selectSysOrderByPrimaryKey(sysOrderId);
        byte payStatusDB = order.getPayStatus();
        byte orderDeliveryStatus = order.getOrderDeliveryStatus();
        if (orderDeliveryStatus == (byte) 1 || orderDeliveryStatus == (byte) 2 || orderDeliveryStatus == (byte) 3 || orderDeliveryStatus == (byte) 4 || orderDeliveryStatus == (byte) 7)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "仅已发货已收货的订单可修改付款状态。。。");
        if (payStatusDB == (byte) 0 || payStatusDB == (byte) 10 || payStatusDB == (byte) 11 || payStatusDB == (byte) 21)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "当前订单付款状态不支持更改。。。");
        SysOrder sysOrder = new SysOrder();
        sysOrder.setSysOrderId(sysOrderId);
        switch (payStatus) {
            case "付款成功":
                sysOrder.setPayStatus((byte) 21);
                break;
            case "待补款":
                sysOrder.setPayStatus((byte) 30);
                break;
        }
        sysOrderMapper.updateStatusAndShipExceptionBySysOrderId(sysOrder);
    }

    @Override
    public void sendAliexpressDeliverInfoNew(SysOrderNew sysOrder, SysOrderPackage deliveredPackage) {
        _log.error("sendAliexpressDeliverInfo:orderId={}", sysOrder.getSourceOrderId());
        String sysOrderId = sysOrder.getSysOrderId();
        String deliveryMethodCode = deliveredPackage.getDeliveryMethodCode();
        Integer deliveryWarehouseId = deliveredPackage.getDeliveryWarehouseId();
        LogisticsDTO logisticsDTO = systemOrderCommonService.queryLogisticsByCode(deliveryMethodCode, deliveryWarehouseId);
        if (null == logisticsDTO) {
            _log.error("_____________没有找到物流商名称:deliveryWarehouseId={},deliveryMethodCode={}",
                    deliveryWarehouseId, deliveryMethodCode);
            return;
        }
        _log.error("获取到的仓库信息为:={}", FastJsonUtils.toJsonString(logisticsDTO));
        String serviceName = logisticsDTO.getAliexpressCode();
        String shipTrackNumber = deliveredPackage.getShipTrackNumber();
        String shipOrderId = deliveredPackage.getShipOrderId();
        String logisticsOrderId = StringUtils.isBlank(shipTrackNumber) ? shipOrderId : shipTrackNumber;
        String operateStatus = deliveredPackage.getOperateStatus();

        if (Objects.equals(operateStatus, OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
            // 拆单：现在标记部分合包，都发货之后再标记全部发货（可能覆盖物流单号）
            List<SysOrderPackage> packageList = sysOrderPackageMapper.queryOrderPackageByOrderId(sysOrder.getSysOrderId());
            byte orderDeliveryStatus = this.getSysOrderDeliveryStatus(packageList);
            String sendType = "part";
            if (orderDeliveryStatus == OrderDeliveryStatusNewEnum.DELIVERED.getValue()) {
                sendType = "all";
            } else if (orderDeliveryStatus == OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getValue()) {
                sendType = "part";
            }
            try {
                AliexpressOrder aliexpressOrder = this.aliexpressOrderMapper.getByOrderId(sysOrder.getSourceOrderId());
                if (aliexpressOrder == null) {
                    return;
                }
                Empower empower = systemOrderCommonService.findOneEmpowerByAccount(OrderSourceCovertToUserServicePlatformEnum
                                .CONVER_FROM_ALIEXPRESS.getOtherPlatform(), null, null,
                        String.valueOf(aliexpressOrder.getEmpowerId()));
                if (empower == null) {
                    _log.error("查不到速卖通卖家的授权信息:empowerId={}", aliexpressOrder.getEmpowerId().toString());
                    return;
                }
                this.aliexpressOrderService.callBack(logisticsOrderId, serviceName, sysOrder.getSourceOrderId(),
                        empower.getToken(), sendType);
            } catch (Exception e) {
                _log.error("速卖通订单回传修改信息异常:", e);
            }
        } else {
            // 普通包裹
            try {
                AliexpressOrder aliexpressOrder = this.aliexpressOrderMapper.getByOrderId(sysOrder.getSourceOrderId());
                if (aliexpressOrder == null) {
                    return;
                }
                Empower empower = systemOrderCommonService.findOneEmpowerByAccount(OrderSourceCovertToUserServicePlatformEnum
                                .CONVER_FROM_ALIEXPRESS.getOtherPlatform(), null, null,
                        String.valueOf(aliexpressOrder.getEmpowerId()));
                if (empower == null) {
                    _log.error("查不到速卖通卖家的授权信息:empowerId={}", aliexpressOrder.getEmpowerId().toString());
                    return;
                }
                this.aliexpressOrderService.callBack(logisticsOrderId, serviceName, sysOrder.getSourceOrderId(),
                        empower.getToken(), "all");
            } catch (Exception e) {
                _log.error("速卖通订单回传修改信息异常:", e);
            }
        }
    }

    /**
     * 回传Aliexpress订单发货信息到Aliexpress平台
     *
     * @param sysOrder
     */
    @Override
    public void sendAliexpressDeliverInfo(SysOrder sysOrder) {
        _log.info("sendAliexpressDeliverInfo:orderId={}", sysOrder.getSourceOrderId());
        String sysOrderId = sysOrder.getSysOrderId();
        String remote = this.remoteLogisticsService.queryLogisticsByCode(sysOrder.getDeliveryMethodCode(),
                Integer.valueOf(sysOrder.getDeliveryWarehouseId()));
        if (StringUtils.isEmpty(remote)) {
            _log.info("_____________没有找到物流商名称:code={},wareh={}", sysOrder.getDeliveryMethodCode(), sysOrder.getDeliveryWarehouseCode());
            return;
        }
        _log.info("hehehehe:={}", remote);
        JSONObject data = JSONObject.parseObject(remote).getJSONObject("data");
        String serviceName = data.getString("aliexpressCode");
        String logisticsOrderId = StringUtils.isBlank(sysOrder.getShipTrackNumber()) ? sysOrder.getShipOrderId() : sysOrder.getShipTrackNumber();//g
        Byte splittedOrMerged = sysOrder.getSplittedOrMerged();
        if ((byte) 0 == splittedOrMerged) {
            try {
                AliexpressOrder aliexpressOrder = this.aliexpressOrderMapper.getByOrderId(sysOrder.getSourceOrderId());
                if (aliexpressOrder == null)
                    return;
                String allRemote = this.remoteSellerService.findOneEmpowByAccount(3, null, null, aliexpressOrder.getEmpowerId().toString());
                JSONObject remoteJson = JSONObject.parseObject(allRemote).getJSONObject("data");
                if (remoteJson == null || remoteJson.size() < 1) {
                    _log.info("查不到速卖通卖家的授权信息:empowerId={}", aliexpressOrder.getEmpowerId().toString());
                    return;
                }
                this.aliexpressOrderService.callBack(logisticsOrderId, serviceName, sysOrder.getSourceOrderId(), (String) remoteJson.get("token"), "all");
            } catch (Exception e) {
                _log.error("_____________速卖通订单回传修改信息异常:", e.getMessage(), e);
            }
        } else {
            List<AliexpressOrderChild> aliexpressOrderChildList = aliexpressOrderChildMapper.queryBatchAliexpressDetailByOrderItemId(this.getSourceOrderLineItemIdList(sysOrder));
            if (CollectionUtils.isEmpty(aliexpressOrderChildList) || aliexpressOrderChildList.size() == 0) {
                _log.info("_____________根据平台订单项ID查出平台订单项实体类出错_______________系统订单号为：{}_______________", sysOrderId);
                return;
            }
            String parentOrderId = aliexpressOrderChildList.get(0).getParentOrderId();
            AliexpressOrder aliexpressOrder = this.aliexpressOrderMapper.getByOrderId(parentOrderId);
            String allRemote = this.remoteSellerService.findOneEmpowByAccount(3, null, null, aliexpressOrder.getEmpowerId().toString());
            JSONObject remoteJson = JSONObject.parseObject(allRemote).getJSONObject("data");
            if (remoteJson == null || remoteJson.size() < 1) {
                _log.info("查不到速卖通卖家的授权信息:empowerId={}", aliexpressOrder.getEmpowerId().toString());
                return;
            }
            String token = (String) remoteJson.get("token");
            Set<String> parentIdSet = aliexpressOrderChildList.stream().map(x -> x.getParentOrderId()).collect(Collectors.toSet());
            for (String parentId : parentIdSet) {
                AliexpressOrder aliOrder = new AliexpressOrder();
                aliOrder.setOrderId(parentId);
                List<TradeListDTO> list = new ArrayList<>();
                for (AliexpressOrderChild child : aliexpressOrderChildList) {
                    String tradeOrderId = child.getParentOrderId();
                    if (tradeOrderId.equals(parentId)) {
                        String orderId = child.getOrderId();
                        Integer count = child.getProductCount();
                        TradeListDTO tradeListDTO = new TradeListDTO();
                        List<ShipmentDTO> shipmentList = new ArrayList<>();
                        Integer sortId = child.getOrderSortId();
                        for (SysOrderDetail detail : sysOrder.getSysOrderDetails()) {
                            if (detail.getSourceOrderLineItemId().contains(orderId)) {
                                ShipmentDTO shipmentDTO = new ShipmentDTO();
                                if (count.intValue() == detail.getSkuQuantity()) {
                                    tradeListDTO.setSendType("all");
                                } else {
                                    tradeListDTO.setSendType("part");
                                }
                                tradeListDTO.setOrderIndex(sortId);
                                shipmentDTO.setLogisticsNo(logisticsOrderId);
                                shipmentDTO.setServiceName(serviceName);
                                shipmentList.add(shipmentDTO);
                            }
                        }
                    }
                }
                remoteAliexpressService.shipChild(FastJsonUtils.toJsonString(list), token, parentId);
            }
        }
    }

    @Override
    public void pushCommoditySalesRecord(SysOrderPackage deliveredPackage) {
        String sysOrderId = deliveredPackage.getSysOrderId();
        String orderTrackId = deliveredPackage.getOrderTrackId();
        List<CodeAndValueVo> list = new ArrayList<>();
        for (SysOrderPackageDetail detail : deliveredPackage.getSysOrderPackageDetailList()) {
            CodeAndValueVo vo = new CodeAndValueVo();
            vo.setCode(detail.getSku());
            vo.setValue(detail.getSkuQuantity() + "");
            list.add(vo);
        }
        remoteCommodityService.updateSkuSaleNum(list);
        _log.error("订单{} , 包裹{} 推送商品销售记录", sysOrderId, orderTrackId);
    }

    @Override
    public void pushCommoditySalesRecord(SysOrder sysOrder) {
        List<CodeAndValueVo> list = new ArrayList<>();
        for (SysOrderDetail detail : sysOrder.getSysOrderDetails()) {
            CodeAndValueVo vo = new CodeAndValueVo();
            vo.setCode(detail.getSku());
            vo.setValue(detail.getSkuQuantity() + "");
            list.add(vo);
        }
        remoteCommodityService.updateSkuSaleNum(list);
        _log.info("_________推送商品销售统计________订单为:{}__________", sysOrder.getSysOrderId());
    }

    @Override
    public CalculateLogisticsResultVO calculateLogisticsFee(LogisticsCostVo logisticsCostVo,
                                                            CalculateLogisticsResultVO logisticsResultVO,
                                                            LogisticsCostEnum calculateType) {
        List<SupplierGroupVo> baseList = new ArrayList<>();
        if (LogisticsCostEnum.sellers == calculateType) {
            baseList = logisticsCostVo.getSellers();
        } else if (LogisticsCostEnum.supplier == calculateType) {
            baseList = logisticsCostVo.getSupplier();
        } else {
            baseList = logisticsCostVo.getLogistics();
        }

        List<CalculateLogisticsSupplierVO> supplierFeeList = new ArrayList<>();

        for (SupplierGroupVo supplierGroupVo : baseList) {
            CalculateLogisticsSupplierVO supplier = new CalculateLogisticsSupplierVO();
            BigDecimal supplierLogisticsFee = new BigDecimal(0);
            List<CalculateLogisticsSkuVO> skuList = new ArrayList<>();
            List<SkuGroupVo> supplierItemList = supplierGroupVo.getItems();
            for (SkuGroupVo item : supplierItemList) {
                CalculateLogisticsSkuVO sku = new CalculateLogisticsSkuVO();
                BigDecimal upwardCost = item.getSkuPlCost();

                if (LogisticsCostEnum.logistics != calculateType) {
                    // 物流商价格不上浮
                    upwardCost = upwardCost.add(upwardCost.multiply(new BigDecimal(Constants.ADDITIONAL_FREIGHT_RATE)));
                    upwardCost = OrderUtils.calculateMoney(upwardCost, false);
                }

                sku.setSku(item.getSku());
                sku.setSkuNumber(item.getSkuNumber());
                sku.setSkuPerCost(upwardCost);
                _log.error("_________计算最终物流费，skuPlCost{}", upwardCost);
                // 计算包裹的运费
                BigDecimal zero = new BigDecimal("0");
                BigDecimal shipFee = upwardCost;
                if (LogisticsCostEnum.sellers == calculateType) {
                    if (item.getFreeFreight().equals(Constants.SysOrder.FREE_FREIGHT)) {
                        shipFee = zero;
                    }
                } else if (LogisticsCostEnum.supplier == calculateType) {
                    if (item.getFreeFreight().equals(Constants.SysOrder.NOT_FREE_FREIGHT)) {
                        shipFee = zero;
                    }
                }

                _log.error("_________计算最终物流费，supplierLogisticsFee{}+shipFee{}*getSkuNumber{}", supplierLogisticsFee, shipFee, item.getSkuNumber());
                supplierLogisticsFee = supplierLogisticsFee.add(shipFee
                        .multiply(new BigDecimal(item.getSkuNumber())));
                skuList.add(sku);
            }
            supplier.setSkuList(skuList);
            if (LogisticsCostEnum.logistics != calculateType) {
                supplier.setLogisticsFee(supplierLogisticsFee);
            }
            supplierFeeList.add(supplier);
        }

        if (LogisticsCostEnum.sellers == calculateType) {
            logisticsResultVO.setSellerList(supplierFeeList);
        } else if (LogisticsCostEnum.supplier == calculateType) {
            logisticsResultVO.setSupplierList(supplierFeeList);
        } else {
            logisticsResultVO.setLogisticsList(supplierFeeList);
        }

        return logisticsResultVO;
    }

    @Override
    public CalculateLogisticsResultVO calculateEstimateFreight(SysOrderPackageDTO sysOrderPackageDTO,
                                                               String platformType, String warehouseId, String countryCode,
                                                               String postCode, Integer searchType, String logisticsCode, String city, Integer storeId, Integer handOrder) {
        LogisticsCostVo logisticsCostVo = this.assembleGetEstimateFreightParam(sysOrderPackageDTO.getSysOrderPackageDetailList(), platformType,
                warehouseId, countryCode, postCode, searchType, logisticsCode, city, storeId, handOrder);
        logisticsCostVo = this.calculateEstimateFreight(logisticsCostVo);
        CalculateLogisticsResultVO calculateLogisticsResultVO = new CalculateLogisticsResultVO();
        this.calculateLogisticsFee(logisticsCostVo, calculateLogisticsResultVO, LogisticsCostEnum.sellers);
        this.calculateLogisticsFee(logisticsCostVo, calculateLogisticsResultVO, LogisticsCostEnum.supplier);
        calculateLogisticsResultVO.setLogisticsCostData(logisticsCostVo);
        _log.error("计算之后的数据为: {}", FastJsonUtils.toJsonString(calculateLogisticsResultVO));
        return calculateLogisticsResultVO;
    }

    /**
     * 组装请求预估物流费的参数
     *
     * @param sysOrderPackageDetailDTOList {@link List<SysOrderPackageDetailDTO>} 包裹详情信息
     * @param platformType                 所属平台 1(eBay) 2(Amazon) 3(Wish) 4(AliExpress)
     * @param warehouseId                  仓库id
     * @param countryCode                  国家简码
     * @param postCode                     收货地址邮编
     * @param searchType                   搜索条件 1 价格最低  2 综合排序   3 物流速度最快
     * @param logisticsCode                物流方式code
     * @param handOrder                    是否手工单，0 否 1是
     */
    private LogisticsCostVo assembleGetEstimateFreightParam(List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList,
                                                            String platformType, String warehouseId, String countryCode,
                                                            String postCode, Integer searchType, String logisticsCode, String city, Integer storeId, Integer handOrder) {
        if (null != searchType && null != logisticsCode) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "物流方式code 和 搜索条件 不能同时存在");
        }
        List<SkuGroupVo> skuGroupVoList = new ArrayList<>();
        for (SysOrderPackageDetailDTO sysOrderPackageDetailDTO : sysOrderPackageDetailDTOList) {
            SkuGroupVo skuGroupVo = new SkuGroupVo(Long.valueOf(sysOrderPackageDetailDTO.getSupplierId()),
                    sysOrderPackageDetailDTO.getSku(), sysOrderPackageDetailDTO.getSkuQuantity(),
                    BigDecimal.ZERO, sysOrderPackageDetailDTO.getFreeFreight());
            skuGroupVoList.add(skuGroupVo);
        }
        LogisticsCostVo logisticsCostVo = OrderUtils.getLogisticsBySkuGroup(skuGroupVoList);
        logisticsCostVo.setPlatformType(platformType);
        logisticsCostVo.setWarehouseId(warehouseId);
        logisticsCostVo.setCountryCode(countryCode);
        logisticsCostVo.setPostCode(postCode);
        logisticsCostVo.setSearchType(searchType);
        logisticsCostVo.setLogisticsCode(logisticsCode);
        logisticsCostVo.setCity(city);
        logisticsCostVo.setStoreId(storeId);
        logisticsCostVo.setHandOrder(handOrder);
        return logisticsCostVo;
    }

    /**
     * 计算预估物流费
     *
     * @param logisticsCostVo
     */
    private LogisticsCostVo calculateEstimateFreight(LogisticsCostVo logisticsCostVo) {
        LogisticsCostVo logisticsCostVo1 = systemOrderCommonService.getEstimateFreight(logisticsCostVo);
        _log.error("请求获得的计算预估物流费的信息为: {}", FastJsonUtils.toJsonString(logisticsCostVo1));
        OrderUtils.calcLogisticsCost(logisticsCostVo1);
        _log.error("经过OrderUtils计算之后的值为: {}", FastJsonUtils.toJsonString(logisticsCostVo1));
        return logisticsCostVo1;
    }

    @Override
    public List<LogisticsDetailVo> getSuitLogisticsByType(SearchLogisticsListDTO searchLogisticsListDTO) {
        return systemOrderCommonService.getSuitLogisticsByType(searchLogisticsListDTO);
    }
}
