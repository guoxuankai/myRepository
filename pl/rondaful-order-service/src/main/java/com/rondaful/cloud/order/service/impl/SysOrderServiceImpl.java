package com.rondaful.cloud.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonservices.mws.uploadData.common.mws.UploadData;
import com.amazonservices.mws.uploadData.common.mws.model.FeedSubmissionInfo;
import com.amazonservices.mws.uploadData.common.mws.model.SubmitFeedResponse;
import com.amazonservices.mws.uploadData.common.mws.model.SubmitFeedResult;
import com.amazonservices.mws.uploadData.common.mws.samples.SubmitFeedSample;
import com.codingapi.tx.annotation.TxTransaction;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.MessageEnum;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.enums.SysOrderLogEnum;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.ERPUtils;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.common.utils.UserUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.Amazon.AmazonDelivery;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.entity.Amazon.AmazonUploadData;
import com.rondaful.cloud.order.entity.BuyerCountAndCountryCode;
import com.rondaful.cloud.order.entity.JudgeAfterSaleDTO;
import com.rondaful.cloud.order.entity.OrderAfterSalesModel;
import com.rondaful.cloud.order.entity.OrderInfoVO;
import com.rondaful.cloud.order.entity.OrderRecord;
import com.rondaful.cloud.order.entity.PLOrderInfoDTO;
import com.rondaful.cloud.order.entity.PayOrderInfo;
import com.rondaful.cloud.order.entity.PlatformOrderItemInfo;
import com.rondaful.cloud.order.entity.ProviderUserDTO;
import com.rondaful.cloud.order.entity.SupplyChainCompany;
import com.rondaful.cloud.order.entity.SysOrder;
import com.rondaful.cloud.order.entity.SysOrderDetail;
import com.rondaful.cloud.order.entity.SysOrderLog;
import com.rondaful.cloud.order.entity.SystemExport;
import com.rondaful.cloud.order.entity.TheMonthOrderCount;
import com.rondaful.cloud.order.entity.TheMonthOrderSaleAndProfit;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrder;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderChild;
import com.rondaful.cloud.order.entity.cms.OrderAfterSalesCommodityOrderDetail;
import com.rondaful.cloud.order.entity.cms.OrderAfterSalesOrderDetailsModel;
import com.rondaful.cloud.order.entity.cms.OrderAfterVo;
import com.rondaful.cloud.order.entity.commodity.CommoditySpec;
import com.rondaful.cloud.order.entity.commodity.SkuInventoryVo;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.entity.supplier.InventoryDTO;
import com.rondaful.cloud.order.entity.supplier.LogisticsDTO;
import com.rondaful.cloud.order.entity.supplier.WarehouseDTO;
import com.rondaful.cloud.order.entity.system.SysOrderNew;
import com.rondaful.cloud.order.entity.system.SysOrderPackage;
import com.rondaful.cloud.order.entity.system.SysOrderPackageDetail;
import com.rondaful.cloud.order.entity.system.SysOrderReceiveAddress;
import com.rondaful.cloud.order.enums.ConvertSysStatusEnum;
import com.rondaful.cloud.order.enums.LogisticsStrategyCovertToLogisticsLogisticsType;
import com.rondaful.cloud.order.enums.OrderCodeEnum;
import com.rondaful.cloud.order.enums.OrderDeliveryStatusNewEnum;
import com.rondaful.cloud.order.enums.OrderHandleLogEnum;
import com.rondaful.cloud.order.enums.OrderPackageHandleEnum;
import com.rondaful.cloud.order.enums.OrderPackageStatusEnum;
import com.rondaful.cloud.order.enums.OrderSourceCovertToLogisticsServicePlatformEnum;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.enums.PayInfoEnum;
import com.rondaful.cloud.order.enums.PlatformCommissionEnum;
import com.rondaful.cloud.order.enums.SkuBindEnum;
import com.rondaful.cloud.order.mapper.AmazonDeliveryMapper;
import com.rondaful.cloud.order.mapper.AmazonOrderDetailMapper;
import com.rondaful.cloud.order.mapper.AmazonOrderMapper;
import com.rondaful.cloud.order.mapper.AmazonUploadDataMapper;
import com.rondaful.cloud.order.mapper.EbayOrderDetailMapper;
import com.rondaful.cloud.order.mapper.EbayOrderMapper;
import com.rondaful.cloud.order.mapper.EbayOrderStatusMapper;
import com.rondaful.cloud.order.mapper.SysOrderDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderInvoiceMapper;
import com.rondaful.cloud.order.mapper.SysOrderLogMapper;
import com.rondaful.cloud.order.mapper.SysOrderMapper;
import com.rondaful.cloud.order.mapper.SysOrderNewMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageDetailMapper;
import com.rondaful.cloud.order.mapper.SysOrderPackageMapper;
import com.rondaful.cloud.order.mapper.SysOrderReceiveAddressMapper;
import com.rondaful.cloud.order.mapper.aliexpress.AliexpressOrderChildMapper;
import com.rondaful.cloud.order.mapper.aliexpress.AliexpressOrderMapper;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderPackageDetailDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderReceiveAddressDTO;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderTransferInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDTO;
import com.rondaful.cloud.order.model.dto.syncorder.UpdateSourceOrderDetailDTO;
import com.rondaful.cloud.order.model.dto.sysOrderInvoice.SysOrderInvoiceInsertOrUpdateDTO;
import com.rondaful.cloud.order.model.dto.sysorder.SkuDTO;
import com.rondaful.cloud.order.model.vo.sysOrderInvoice.SysOrderInvoiceVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsResultVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsSkuVO;
import com.rondaful.cloud.order.model.vo.sysorder.CalculateLogisticsSupplierVO;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.RemoteCmsService;
import com.rondaful.cloud.order.remote.RemoteCommodityService;
import com.rondaful.cloud.order.remote.RemoteFinanceService;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.remote.RemoteSupplierService;
import com.rondaful.cloud.order.remote.RemoteUserService;
import com.rondaful.cloud.order.service.IAmazonEmpowerService;
import com.rondaful.cloud.order.service.IEbayOrderService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.service.ISysOrderInvoiceService;
import com.rondaful.cloud.order.service.ISysOrderLogService;
import com.rondaful.cloud.order.service.ISysOrderService;
import com.rondaful.cloud.order.service.ISysOrderUpdateService;
import com.rondaful.cloud.order.service.ISystemOrderCommonService;
import com.rondaful.cloud.order.service.ISystemOrderService;
import com.rondaful.cloud.order.utils.CheckOrderUtils;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import com.rondaful.cloud.order.utils.GetAccountAndShopInfoUtils;
import com.rondaful.cloud.order.utils.JudgeAuthorityUtils;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.SysOrderUtils;
import com.rondaful.cloud.order.utils.ThreadPoolUtil;
import com.rondaful.cloud.order.utils.TimeUtil;
import com.rondaful.cloud.order.utils.WmsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2018-12-20 14:57
 * 包名: com.rondaful.cloud.order.service.impl
 * 描述: 系统订单逻辑实现类
 */
@Service
public class SysOrderServiceImpl extends BaseServiceImpl<SysOrder> implements ISysOrderService {
    @Autowired
    private SysOrderLogMapper sysOrderLogMapper;
    @Autowired
    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private IEbayOrderService ebayOrderService;
    @Autowired
    private WmsUtils wmsUtils;
    @Autowired
    private SystemOrderServiceImpl systemOrderServiceImpl;
    @Autowired
    private ISysOrderService sysOrderService;
    @Autowired
    private ISystemOrderService systemOrderService;
    @Autowired
    private EbayOrderStatusMapper ebayOrderStatusMapper;
    @Autowired
    private SysOrderNewMapper sysOrderNewMapper;
    @Autowired
    private SysOrderPackageMapper sysOrderPackageMapper;
    @Autowired
    private SysOrderPackageDetailMapper sysOrderPackageDetailMapper;
    @Autowired
    private SysOrderReceiveAddressMapper sysOrderReceiveAddressMapper;
    @Autowired
    private RedissLockUtil redissLockUtil;
    @Autowired
    private ISkuMapService skuMapService;
    @Autowired
    private ISysOrderInvoiceService sysOrderInvoiceService;
    @Autowired
    private RemoteCmsService remoteCmsService;
    @Autowired
    private RemoteSupplierService remoteSupplierService;
    @Autowired
    private AliexpressOrderChildMapper aliexpressOrderChildMapper;
    @Autowired
    private AmazonOrderDetailMapper amazonOrderDetailMapper;
    @Autowired
    private EbayOrderDetailMapper ebayOrderDetailMapper;
    @Autowired
    private AliexpressOrderMapper aliexpressOrderMapper;
    @Autowired
    private AmazonOrderMapper amazonOrderMapper;
    @Autowired
    private EbayOrderMapper ebayOrderMapper;
    @Autowired
    private SystemOrderCommonServiceImpl orderCommonService;
    @Autowired
    private JudgeAuthorityUtils judgeAuthorityUtils;
    @Autowired
    private GetAccountAndShopInfoUtils getAccountAndShopInfoUtils;
    @Autowired
    private GetLoginUserInformationByToken getLoginUserInformationByToken;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private SysOrderMapper sysOrderMapper;
    @Autowired
    private SysOrderDetailMapper sysOrderDetailMapper;
    @Autowired
    private ERPUtils erpUtils;
    @Autowired
    private RemoteSellerService remoteSellerService;
    @Autowired
    private RemoteFinanceService remoteFinanceService;

    @Autowired
    private ISysOrderUpdateService sysOrderUpdateService;

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(SysOrderServiceImpl.class);

    //    @Autowired
//    private RemoteCommodityService remoteCommodityService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private ISysOrderLogService sysOrderLogService;
    @Autowired
    private OrderMessageSender orderMessageSender;
    @Autowired
    private IAmazonEmpowerService amazonEmpowerService;
    @Autowired
    private AmazonUploadDataMapper amazonUploadDataMapper;
    @Autowired
    private AmazonDeliveryMapper amazonDeliveryMapper;
    @Autowired
    private ISystemOrderCommonService systemOrderCommonService;
    @Autowired
    private GetLoginUserInformationByToken getUserInfo;
    @Autowired
    private GoodCangOrderInterceptService goodCangOrderInterceptService;

    @Autowired
    private SysOrderInvoiceMapper sysOrderInvoiceMapper;

    public void test() {
//        List<AmazonOrder> list = new ArrayList<>();
//        list.add(amazonOrderMapper.selectAmazonOrderByOrderId("402-3742972-5700339"));
//        list.add(amazonOrderMapper.selectAmazonOrderByOrderId("402-2551013-5376332"));
//        String str = UploadData.setUploadDatasXML(list);
//        System.out.println(str);
//        AmazonUploadData amazonUploadData = new AmazonUploadData();
//        amazonUploadData.setResultDescription("88");
//        amazonUploadDataMapper.insertSelective(amazonUploadData);
    }

    @Override
    public List<SystemExport> getExportResults(String sku, boolean isSeller, Byte errorOrder, Byte payStatus, Byte orderSource,
                                               String recordNumber, String orderTrackId, String isAfterSaleOrder1, String sourceOrderId, String isLogisticsAbnormal,
                                               String splittedOrMerged, String platformSellerAccount, String sellerPlAccount, String sysOrderId, String orderStatus,
                                               String startDate, String endDate, String startTime, String endTime) throws Exception {
        sellerPlAccount = getSellerPlAccount(sellerPlAccount);//如果是卖家平台则返回主账号
        Integer plSellerId = null;
        plSellerId = getAccountAndShopInfoUtils.getPlSellerIdIfNotNull(sellerPlAccount, plSellerId);//存在则返回品连账号ID
        List<Integer> userIds = new ArrayList<>();  //账号ID集合
        List<Integer> empIds = new ArrayList<>();  //店铺ID集合
        if (StringUtils.isNotBlank(platformSellerAccount)) {
            List<String> platformSellerAccountList = Arrays.asList(platformSellerAccount.split("#"));
            for (String account : platformSellerAccountList) {
                Integer shopIdIfNotNull = getAccountAndShopInfoUtils.getShopIdIfNotNull(account, sellerPlAccount);//存在则返回店铺ID
                if (shopIdIfNotNull != null) {
                    empIds.add(shopIdIfNotNull);
                }
            }
        }
        if (judgeAuthorityUtils.judgeUserAuthorityAndSetDataToList(plSellerId, getLoginUserInformationByToken.getUserDTO(), userIds, empIds)) {
            return null;
        }
        Byte isAfterSaleOrder = getIsAfterSaleOrder(isAfterSaleOrder1);//获取是否提供售后入口状态值
        Byte plOrderStatus = getPlOrderStatus(orderStatus);//获取品连订单发货状态
        List<String> sysOrderIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(sysOrderId)) {
            sysOrderIdList.add(sysOrderId);
        }
        if (StringUtils.isNotBlank(sku)) {
            //根据SKU查出订单ID集合
            sysOrderIdList.addAll(sysOrderDetailMapper.querySysOrderIdListBySku(sku));
        }
        if (StringUtils.isNotBlank(orderTrackId)) { //包裹号不为空 先查出包裹号 再反推出订单、从而查出订单详情再塞进集合
            SysOrderPackage orderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(orderTrackId);
            if (orderPackage == null) {
                logger.error("根据包裹号找不到包裹，包裹号：{}", orderTrackId);
                return null;
            }
            String orderId = orderPackage.getSysOrderId();
            if (StringUtils.isNotBlank(orderId)) {
                sysOrderIdList.add(orderId);
            } else {
                List<String> stringList = Arrays.asList(orderPackage.getOperateSysOrderId().split("\\#"));
                sysOrderIdList.addAll(stringList);
            }
        }
        int count = sysOrderNewMapper.getSysOrderNewCount(errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder, sourceOrderId, isLogisticsAbnormal, splittedOrMerged, empIds, userIds, sysOrderIdList, plOrderStatus, startDate, endDate, startTime, endTime);
        List<SystemExport> result = new ArrayList<>();//返回结果
        if (count == 0) {
            return null;
        }
        int num = 1000;//每次查询的条数
        //需要查询的次数
        int times = count / num;
        if (count % num != 0) {
            times = times + 1;
        }
        //开始查询的行数
        int bindex = 0;
        List<Callable<List<SystemExport>>> tasks = new ArrayList<Callable<List<SystemExport>>>();//添加任务
        for (int i = 0; i < times; i++) {
            Callable<List<SystemExport>> qfe = new SysOrderThredQuery(this, isSeller, errorOrder, payStatus, orderSource, recordNumber, orderTrackId, sourceOrderId, isLogisticsAbnormal, sysOrderIdList, startDate, endDate, startTime, endTime, userIds, empIds, isAfterSaleOrder, splittedOrMerged, plOrderStatus, bindex, num);
            tasks.add(qfe);
            bindex = bindex + num;
        }
        ThreadPoolExecutor execservice = ThreadPoolUtil.getInstance();
        List<Future<List<SystemExport>>> futures = null;
        futures = execservice.invokeAll(tasks);
        // 处理线程返回结果
        if (futures != null && futures.size() > 0) {
            for (Future<List<SystemExport>> future : futures) {
                result.addAll(future.get());
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getGrossMargin(SysOrder sysOrder) {
        Map<String, Object> map = new HashMap<>();
        Integer handOrder = sysOrder.getHandOrder();//是否手工创建
        String platformType = String.valueOf(
                OrderSourceCovertToLogisticsServicePlatformEnum.getLogisticsPlatformCode(sysOrder.getOrderSource()));

        CalculateLogisticsResultVO resultVO = systemOrderService.calculateEstimateFreight(new SysOrderPackageDTO() {{
            setSysOrderPackageDetailList(sysOrder.getSkuList());
        }}, platformType, sysOrder.getDeliveryWarehouseId(), sysOrder.getShipToCountry(), sysOrder.getShipToPostalCode(), null, sysOrder.getDeliveryMethodCode(), sysOrder.getShipToCity(), sysOrder.getPlatformShopId(), handOrder);
        BigDecimal logisticsFee = resultVO.getSellerList().get(0).getLogisticsFee();
        List<SysOrderPackageDetailDTO> skuList = sysOrder.getSkuList();
        List<CalculateLogisticsSupplierVO> sellerList = resultVO.getSellerList();
        List<CalculateLogisticsSupplierVO> supplierList = resultVO.getSupplierList();
        if (CollectionUtils.isNotEmpty(sellerList)) {
            for (CalculateLogisticsSupplierVO supplierVO : sellerList) {
                for (CalculateLogisticsSkuVO skuVO : supplierVO.getSkuList()) {
                    for (SysOrderPackageDetailDTO dto : skuList) {
                        if (Objects.equals(dto.getSku(), skuVO.getSku())) {
                            dto.setSellerShipFee(skuVO.getSkuPerCost());//设置卖家物流费
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(supplierList)) {
            for (CalculateLogisticsSupplierVO supplierVO : supplierList) {
                for (CalculateLogisticsSkuVO skuVO : supplierVO.getSkuList()) {
                    for (SysOrderPackageDetailDTO dto : skuList) {
                        if (Objects.equals(dto.getSku(), skuVO.getSku())) {
                            dto.setSupplierShipFee(skuVO.getSkuPerCost());//设置供应商物流费
                        }
                    }
                }
            }
        }
        if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 1) {
            for (SysOrderPackageDetailDTO dto : skuList) {
                if (dto.getFreeFreight() == 1) {
                    dto.setSellerShipFee(BigDecimal.ZERO);
                }
            }
        }
        Map<String, Object> objectMap = new HashMap<>();
        for (SysOrderPackageDetailDTO dto : skuList) {
            SkuDTO skuDTO = new SkuDTO();
            BeanUtils.copyProperties(dto, skuDTO);
            objectMap.put(dto.getSku(), skuDTO);
        }
        map.put("skuFeeDetail", objectMap);
        map.put("estimateShipCost", logisticsFee.setScale(2, BigDecimal.ROUND_DOWN));
        sysOrder.setEstimateShipCost(logisticsFee);

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
        if (sysOrder.getGrossMargin().signum() == -1) {
            map.put("grossMargin", null);
        } else {
            map.put("grossMargin", sysOrder.getGrossMargin().setScale(2, BigDecimal.ROUND_DOWN));
        }
        return map;
    }


    @Override
    /**
     * description: 亚马逊发货（被定时任务调用）
     * @Param:
     * @return void
     * create by wujiachuang
     */
    public void amazonDelivery() {
        logger.error("开始上传亚马逊数据");
        //取出需要上传发货信息回亚马逊的数据集合
        List<AmazonDelivery> amazonOrderLists = amazonDeliveryMapper.queryUploadData();
        if (amazonOrderLists == null || amazonOrderLists.size() == 0) {
            return;
        }
        for (AmazonDelivery amazonDelivery : amazonOrderLists) {
            List<AmazonOrderDetail> list = JSONObject.parseArray(amazonDelivery.getAmazonOrderDetails()).toJavaList(AmazonOrderDetail.class);
            amazonDelivery.setAmazonOrderDetailList(list);
        }
        List<AmazonDelivery> reverse = Lists.reverse(amazonOrderLists);
        Map<String, List<AmazonDelivery>> collect = reverse.stream().collect(Collectors.groupingBy(AmazonDelivery::getAmazonSellerAccount));
        for (String key : collect.keySet()) {
            List<AmazonDelivery> list = collect.get(key); //相同的店铺订单、不同的站点
            List<AmazonDelivery> reverse1 = Lists.reverse(list);
            Map<String, List<AmazonDelivery>> collect1 = reverse1.stream().collect(Collectors.groupingBy(AmazonDelivery::getMarketplaceId));
            for (String s : collect1.keySet()) {
                List<AmazonDelivery> amazonOrderList = collect1.get(s);//相同的店铺订单、相同的站点
                String sellerId = amazonOrderList.get(0).getAmazonSellerAccount(); //亚马逊SellerID
                String marketplaceId = amazonOrderList.get(0).getMarketplaceId();//亚马逊站点ID
                //找出卖家在该站点下的授权token
                List<String> list1 = amazonEmpowerService.selectMWSTokenBySellerId(sellerId, marketplaceId);
                if (list1.size() == 0) {
                    logger.error("异常：查询不到该卖家的授权信息，无法上传数据，卖家SellerID为：" + sellerId + "站点ID为：" + marketplaceId);
                    break;
                }
                String mwsToken = list1.get(0);//亚马逊授权token
                String xmlString = UploadData.setUploadDatasXML(amazonOrderList);
                try {
                    //上传数据到亚马逊并存入回传信息数据库、更改亚马逊发货状态
                    getUploadResultAndInsertAndUpdate(amazonOrderList, sellerId, marketplaceId, mwsToken, xmlString);
                } catch (Exception e) {
                    //亚马逊上传数据异常
                    logger.error("异常：亚马逊数据上传异常:SellerID:" + sellerId + "站点ID：" + marketplaceId + "品连账号：" + amazonOrderList.get(0).getPlSellerAccount(), e);
                }
            }
        }
    }

    @Override
    public void updateSysOrderStatus() {
        List<SysOrderNew> sysOrderNews = sysOrderNewMapper.selectShippedSysOrder();
        if (CollectionUtils.isNotEmpty(sysOrderNews)) {
            for (SysOrderNew sysOrderNew : sysOrderNews) {
                if (sysOrderNew.getIsAfterSaleOrder() == 0) {
                    logger.info("修改未进行过售后的订单为已完成状态，参数：{}", FastJsonUtils.toJsonString(sysOrderNew));
                    updateOrderCompleteStatus(sysOrderNew);
                } else {//TODO 有在进行售后的订单，需要调用售后接口查询是否还有SKU在进行售后，没有的话该订单改为已完成状态
                    Boolean flag = JSON.parseObject(remoteCmsService.judgeAfterSalesFinishByOrderId(new ArrayList<String>() {{
                        add(sysOrderNew.getSysOrderId());
                    }})).getBoolean("data");
                    logger.info("flag:{]", flag);
                    if (flag) {
                        logger.info("修改未进行过售后的订单为已完成状态，参数：{}", FastJsonUtils.toJsonString(sysOrderNew));
                        updateOrderCompleteStatus(sysOrderNew);
                    }
                }
            }
        } else {
            return;
        }
    }

    public void updateOrderCompleteStatus(SysOrderNew sysOrderNew) {
        sysOrderNewMapper.updateOrdersStatus(sysOrderNew.getSysOrderId(), OrderDeliveryStatusNewEnum.COMPLETED.getValue());
        //添加操作日志
        sysOrderLogService.insertSelective(
                new SysOrderLog(sysOrderNew.getSysOrderId(),
                        OrderHandleLogEnum.Content.COMPLETED.completed(sysOrderNew.getSysOrderId()),
                        OrderHandleLogEnum.OrderStatus.STATUS_13.getMsg(),
                        OrderHandleLogEnum.Operator.SYSTEM.getMsg()));
    }

    @Override
    /**
     * description: 获取亚马逊上传数据结果更新数据库
     * @Param: amazonOrderList  亚马逊订单集合
     * @Param: sellerId        亚马逊SellerID
     * @Param: marketplaceId   站点ID
     * @Param: mwsToken        授权Token
     * @Param: xmlString       上传数据模板XML字符串
     * @return void
     * create by wujiachuang
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void getUploadResultAndInsertAndUpdate(List<AmazonDelivery> amazonOrderList, String sellerId, String marketplaceId, String mwsToken, String
            xmlString) throws ParseException {
        if (StringUtils.isBlank(xmlString)) {
            logger.error("回传的xml字符串为空，回传失败！sellerId:" + sellerId + "marketplaceId" + marketplaceId);
            return;
        }
        SubmitFeedResponse submitFeedSample = SubmitFeedSample.getSubmitFeedSample(marketplaceId, mwsToken, sellerId, xmlString);
        if (submitFeedSample == null) {
            logger.error("submitFeedSample为null，请求参数：sellerId:{},marketplaceID:{},token:{},xmlString:{}", sellerId, marketplaceId, mwsToken, xmlString);
            return;
        }
        SubmitFeedResult submitFeedResult = submitFeedSample.getSubmitFeedResult();
        FeedSubmissionInfo feedSubmissionInfo = submitFeedResult.getFeedSubmissionInfo();
        //上传数据后回传的sessionId
        String feedSubmissionId = feedSubmissionInfo.getFeedSubmissionId();
        //获得上传的数据类型
        String feedType = feedSubmissionInfo.getFeedType();
        //获得上传数据的提交时间
        String submittedDate = feedSubmissionInfo.getSubmittedDate();
        String submitTime = TimeUtil.DateToString2(OrderUtils.getTimeToDate(submittedDate));   //将格林尼治时间转为中国时间
        //上传数据的处理状态
        String feedProcessingStatus = feedSubmissionInfo.getFeedProcessingStatus();
        int messageId = 1;
        for (AmazonDelivery amazonDelivery : amazonOrderList) {
            //插入DB
            for (AmazonOrderDetail amazonOrderDetail : amazonDelivery.getAmazonOrderDetailList()) {
                AmazonUploadData amazonUploadData = new AmazonUploadData();
                amazonUploadData.setPlSellerAccount(amazonDelivery.getPlSellerAccount());
                amazonUploadData.setAmazonSellerAccount(amazonDelivery.getAmazonSellerAccount());
                amazonUploadData.setMarketplaceId(amazonDelivery.getMarketplaceId());
                amazonUploadData.setOrderId(amazonOrderDetail.getOrderId());
                amazonUploadData.setOrderitemId(amazonOrderDetail.getAmazonOrderitemId());
                amazonUploadData.setFeedType(feedType);
                amazonUploadData.setFeedSubmissionId(feedSubmissionId);
                amazonUploadData.setMessageId(messageId);
                amazonUploadData.setSubmittedDate(submitTime);
                amazonUploadData.setFeedProcessingStatus(feedProcessingStatus);
                amazonUploadDataMapper.insertSelective(amazonUploadData);        //插入亚马逊上传数据记录表
            }
            messageId++;
            amazonDeliveryMapper.updateUploadStatus(amazonDelivery.getId());  //更改亚马逊发货状态为成功
        }
    }

    @Override
    /**
     * description: 通过卖家平台账号（店铺名）查询系统订单
     * @Param: Status 卖家平台账号
     * @return com.rondaful.cloud.common.entity.Page<com.rondaful.cloud.order.entity.SysOrder>  系统订单集合
     * create by wujiachuang
     */
    public Page<SysOrder> selectSysOrdersByPlatformSellerAccount(String platformSellerAccount) {
        String sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        Integer shopId = getAccountAndShopInfoUtils.GetSellerShopIdByShopName(platformSellerAccount, sellerPlAccount);
        List<SysOrder> list;
        list = sysOrderMapper.selectSysOrdersByPlatformSellerAccount(shopId);
        if (list == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
        PageInfo pageInfo = new PageInfo(list);
        return new Page<>(pageInfo);
    }

    @Override
    /**
     * description: 查询用户毛利
     * @Param: userName  品连用户名
     * @Param: type     Y当月、N上月、X全部
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * create by wujiachuang
     */
    public Map<String, Object> findUserGrossMargin(String userName, String type) {
        return sysOrderMapper.findUserGrossMargin(userName, type);
    }


    @Override
    /**
     * description: 手工新建系统订单
     * //TODO 缺调运费试算接口
     * //TODO 设置商品的卖家和供应商物流费还有订单级别的物流费，
     * //TODO 设置订单是否包邮还是不包邮还是部分包邮
     * @Param: sysOrder 系统订单
     * @return java.lang.String  创建结果
     * create by wujiachuang
     */
    @Transactional(rollbackFor = Exception.class)
    public String addSysOrder(SysOrder sysOrder) {
        if (!redissLockUtil.tryLock(sysOrder.getSellerPlId().toString(), 10, 3)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请求繁忙，请稍后尝试！");
        }
        reSetLogisticsStrategy(sysOrder);
        if (Objects.equals("success", CheckOrderUtils.checkSysOrder(sysOrder))) {//校验订单必填项
            sysOrder.setPlatformSellerAccount(getAccountAndShopInfoUtils.GetSellerShopNameByShopId(sysOrder.getPlatformShopId(), null));//设置店铺名
            getSellerPlAccountAndId(sysOrder);//获取并设置卖家的品连主账号和ID
            if (!CheckOrderUtils.judgeSkusAbleSaleAndIsPutAway(sysOrder.getSellerPlId(), sysOrder.getSysOrderDetails().stream().map(x -> x.getSku()).collect(Collectors.toList()))) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "含有不可售商品或已下架商品");
            }
            setWareHouseInfo(sysOrder);//调用供应商服务设置仓库信息
            setSysOrderItemInfo(sysOrder);//设置订单商品信息
            setOrderData(sysOrder);//设置订单相关数据
            String loginName = getLoginUserInformationByToken.getUserDTO().getLoginName();
            SysOrderNew orderNew = new SysOrderNew();
            setOrderPackageInfo(sysOrder, loginName, orderNew, new SysOrderReceiveAddress(), new SysOrderPackage(), new ArrayList<SysOrderPackageDetail>());//设置订单、包裹、包裹详情数据
            setShipFee(orderNew);//物流费计算(设置每个SKU的卖家和供应商物流费还有订单级别的卖家物流费)
            setOrderDataAgain(sysOrder, orderNew); //再次设置订单数据（物流费、利润）
            insertBulk(sysOrder, orderNew);//开启事务将订单、订单详情、包裹、包裹详情插入数据库并添加操作日志
        }
        redissLockUtil.unlock(sysOrder.getSellerPlId().toString());
        return "新增采购订单【" + sysOrder.getSysOrderId() + "】成功！";
    }

    public void reSetLogisticsStrategy(SysOrder sysOrder) {
        if (sysOrder.getLogisticsStrategy().equals(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsType()))) {
            sysOrder.setLogisticsStrategy("integrated_optimal");
        } else if (sysOrder.getLogisticsStrategy().equals(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.FASTEST.getLogisticsType()))) {
            sysOrder.setLogisticsStrategy("fastest");
        } else if (sysOrder.getLogisticsStrategy().equals(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.CHEAPEST.getLogisticsType()))) {
            sysOrder.setLogisticsStrategy("cheapest");
        }
    }

    public void reSetLogisticsStrategy(SysOrderPackage orderPackage) {
        if (orderPackage.getLogisticsStrategy().equals(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsType()))) {
            orderPackage.setLogisticsStrategy("integrated_optimal");
        } else if (orderPackage.getLogisticsStrategy().equals(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.FASTEST.getLogisticsType()))) {
            orderPackage.setLogisticsStrategy("fastest");
        } else if (orderPackage.getLogisticsStrategy().equals(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.CHEAPEST.getLogisticsType()))) {
            orderPackage.setLogisticsStrategy("cheapest");
        }
    }

    public void setOrderDataAgain(SysOrder sysOrder, SysOrderNew orderNew) {
        //因为是手工新增 平台信息设置Null
        sysOrder.setEstimateShipCost(orderNew.getEstimateShipCost());//设置预估物流费
        sysOrder.setTotal(sysOrder.getOrderAmount().add(sysOrder.getEstimateShipCost()));//设置订单总售价：预估物流费+系统商品总金额---------
        orderNew.setTotal(sysOrder.getTotal());
      /*  //设置预估利润、利润率  TODO 产品需求：手工新建和复制的订单不需要计算利润还有利润率
        systemOrderCommonService.setGrossMarginAndProfitMargin(sysOrder);
        orderNew.setProfitMargin(sysOrder.getProfitMargin());
        orderNew.setGrossMargin(sysOrder.getGrossMargin());*/
    }

    /**
     * 普通订单和拆分订单可以使用此方法，合并订单需要将运费分摊回原来订单需要自己处理 参考setSoureMergeOrderInfo 返回原订单集合
     *
     * @param orderNew
     */
    public void setShipFee(SysOrderNew orderNew) {
        Integer handOrder = 1;//是否手工创建
        if (null != orderNew.getIsConvertOrder() && orderNew.getIsConvertOrder().equals(Constants.isConvertOrder.YES)) {
            handOrder = 0;
        }
        BigDecimal totalSellerShipFee = new BigDecimal("0");// TODO 订单级别卖家预估物流费
        SysOrderReceiveAddress ads = orderNew.getSysOrderReceiveAddress();
        String postalCode = ads.getShipToPostalCode();//邮编
        String countryCode = ads.getShipToCountry();//国家二字简码
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            String warehouseId = String.valueOf(sysOrderPackage.getDeliveryWarehouseId());//仓库id
            String logisticsCode = String.valueOf(sysOrderPackage.getDeliveryMethodCode());//邮寄方式code
            List<SysOrderPackageDetailDTO> skuList = new ArrayList<>();
            for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
                if (StringUtils.isNotBlank(item.getSku())) {
                    SysOrderPackageDetailDTO dto = new SysOrderPackageDetailDTO();
                    dto.setSku(item.getSku());
                    dto.setSkuQuantity(item.getSkuQuantity());
                    dto.setSupplierId(item.getSupplierId());
                    dto.setFreeFreight(item.getFreeFreight());
                    skuList.add(dto);
                }
            }
            //TODO 加入运费计算
            String platformType = String.valueOf(
                    OrderSourceCovertToLogisticsServicePlatformEnum.getLogisticsPlatformCode(orderNew.getOrderSource()));
            CalculateLogisticsResultVO resultVO = systemOrderService.calculateEstimateFreight(new SysOrderPackageDTO() {{
                setSysOrderPackageDetailList(skuList);
            }}, platformType, warehouseId, countryCode, postalCode, null, logisticsCode, ads.getShipToCity(), orderNew.getPlatformShopId(), handOrder);
            sysOrderPackage.setCalculateFeeInfo(FastJsonUtils.toJsonString(resultVO.getLogisticsCostData()));//TODO 添加计算公式
            //不包邮的
            if (CollectionUtils.isNotEmpty(resultVO.getSellerList())) {
                CalculateLogisticsSupplierVO vo = resultVO.getSellerList().get(0);
                BigDecimal logisticsFee = vo.getLogisticsFee();//此包裹卖家的物流费 需要累加全部包裹
                sysOrderPackage.setEstimateShipCost(logisticsFee);//设置包裹级别预估物流费
                totalSellerShipFee = totalSellerShipFee.add(logisticsFee);//累加包裹卖家物流费到订单级别的卖家预估物流费
                for (CalculateLogisticsSkuVO skuVO : vo.getSkuList()) {
                    String sku = skuVO.getSku();
                    for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
                        if (sku.equals(item.getSku())) {
                            item.setSellerShipFee(skuVO.getSkuPerCost()); //设置SKU卖家物流费
                        }
                    }
                }
            }
            //包邮的
            if (CollectionUtils.isNotEmpty(resultVO.getSupplierList())) {
                for (CalculateLogisticsSupplierVO vo : resultVO.getSupplierList()) {
                    for (CalculateLogisticsSkuVO skuVO : vo.getSkuList()) {
                        String sku = skuVO.getSku();
                        for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
                            if (sku.equals(item.getSku())) {
                                item.setSupplierShipFee(skuVO.getSkuPerCost()); //设置SKU供应商物流费
                            }
                        }
                    }
                }
            }
        }
        orderNew.setEstimateShipCost(totalSellerShipFee);
    }


//            Map<Integer, List<SysOrderPackageDetail>> collect = sysOrderPackage.getSysOrderPackageDetailList().stream().collect(Collectors.groupingBy(SysOrderPackageDetail::getFreeFreight));
//            List<SysOrderPackageDetail> sellerList = collect.get(0); //TODO 不包邮  调用物流费接口只取卖家的就行
//            if (CollectionUtils.isNotEmpty(sellerList)) {
//                List<SysOrderPackageDetailDTO> skuList = new ArrayList<>();
//                for (SysOrderPackageDetail item : sellerList) {
//                    SysOrderPackageDetailDTO dto = new SysOrderPackageDetailDTO();
//                    dto.setSku(item.getSku());
//                    dto.setSkuQuantity(item.getSkuQuantity());
//                    dto.setSupplierId(item.getSupplierId());
//                    dto.setFreeFreight(item.getFreeFreight());
//                    skuList.add(dto);
//                }
//                //TODO 加入运费计算
//                CalculateLogisticsResultVO resultVO = systemOrderService.calculateEstimateFreight(new SysOrderPackageDTO() {{
//                    setSysOrderPackageDetailList(skuList);
//                }}, paltformType, warehouseId, countryCode, postalCode, null, logisticsCode);
//                CalculateLogisticsSupplierVO vo = resultVO.getSellerList().get(0);
//                BigDecimal logisticsFee = vo.getLogisticsFee();//此包裹卖家的物流费 需要累加全部包裹
//                sysOrderPackage.setEstimateShipCost(logisticsFee);//设置包裹级别预估物流费
////                sysOrderPackage.setCalculateFeeInfo(FastJsonUtils.toJsonString(resultVO.getLogisticsCostData()));//TODO 添加计算公式
//                totalSellerShipFee = totalSellerShipFee.add(logisticsFee);//累加包裹卖家物流费到订单级别的卖家预估物流费
//                for (CalculateLogisticsSkuVO skuVO : vo.getSkuList()) {
//                    String sku = skuVO.getSku();
//                    for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
//                        if (sku.equals(item.getSku())) {
//                            item.setSellerShipFee(skuVO.getSkuPerCost()); //设置SKU卖家物流费，供应商和物流商默认为0
//                        }
//                    }
//                }
//            }
//            List<SysOrderPackageDetail> supplierSkuList = collect.get(1); //TODO 包邮  调用物流费接口只取供应商的就行
//            if (CollectionUtils.isNotEmpty(supplierSkuList)) {
//                Map<Integer, List<SysOrderPackageDetail>> listMap = supplierSkuList.stream().collect(Collectors.groupingBy(SysOrderPackageDetail::getSupplierId));
//                for (Integer supplierId : listMap.keySet()) {
//                    List<SysOrderPackageDetail> SkuList = listMap.get(supplierId);
//                    List<SysOrderPackageDetailDTO> skuList = new ArrayList<>();
//                    for (SysOrderPackageDetail item : SkuList) {
//                        SysOrderPackageDetailDTO dto = new SysOrderPackageDetailDTO();
//                        dto.setSku(item.getSku());
//                        dto.setSkuQuantity(item.getSkuQuantity());
//                        dto.setSupplierId(item.getSupplierId());
//                        dto.setFreeFreight(item.getFreeFreight());
//                        skuList.add(dto);
//                    }
//                    //TODO 加入运费计算
//                    CalculateLogisticsResultVO resultVO = systemOrderService.calculateEstimateFreight(new SysOrderPackageDTO() {{
//                        setSysOrderPackageDetailList(skuList);
//                    }}, paltformType, warehouseId, countryCode, postalCode, null, logisticsCode);
//                    sysOrderPackage.setCalculateFeeInfo(FastJsonUtils.toJsonString(resultVO.getLogisticsCostData()));//TODO 添加计算公式
//                    for (CalculateLogisticsSupplierVO vo : resultVO.getSupplierList()) {
//                        for (CalculateLogisticsSkuVO skuVO : vo.getSkuList()) {
//                            String sku = skuVO.getSku();
//                            for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
//                                if (sku.equals(item.getSku())) {
//                                    item.setSupplierShipFee(skuVO.getSkuPerCost()); //设置SKU供应商物流费，卖家和物流商默认为0
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
/*
    public void setShipFee(SysOrderNew orderNew) {
        String paltformType = "";//平台类型
        if (orderNew.getOrderSource().equals(OrderSourceEnum.CONVER_FROM_EBAY) ||
                orderNew.getOrderSource().equals(OrderSourceEnum.CONVER_FROM_AMAZON) ||
                orderNew.getOrderSource().equals(OrderSourceEnum.CONVER_FROM_ALIEXPRESS) ||
                orderNew.getOrderSource().equals(OrderSourceEnum.CONVER_FROM_WISH)) {
            paltformType = String.valueOf(orderNew.getOrderSource());
        }
        BigDecimal totalSellerShipFee = new BigDecimal("0");// TODO 订单级别卖家预估物流费
        SysOrderReceiveAddress ads = orderNew.getSysOrderReceiveAddress();
        String postalCode = ads.getShipToPostalCode();//邮编
        String countryCode = ads.getShipToCountry();//国家二字简码
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            String warehouseId = String.valueOf(sysOrderPackage.getDeliveryWarehouseId());//仓库id
            String logisticsCode = String.valueOf(sysOrderPackage.getDeliveryMethodCode());//邮寄方式code
            Map<Integer, List<SysOrderPackageDetail>> collect = sysOrderPackage.getSysOrderPackageDetailList().stream().collect(Collectors.groupingBy(SysOrderPackageDetail::getFreeFreight));
            List<SysOrderPackageDetail> sellerList = collect.get(0); //TODO 不包邮  调用物流费接口只取卖家的就行
            if (CollectionUtils.isNotEmpty(sellerList)) {
                List<SysOrderPackageDetailDTO> skuList = new ArrayList<>();
                for (SysOrderPackageDetail item : sellerList) {
                    SysOrderPackageDetailDTO dto = new SysOrderPackageDetailDTO();
                    dto.setSku(item.getSku());
                    dto.setSkuQuantity(item.getSkuQuantity());
                    dto.setSupplierId(item.getSupplierId());
                    dto.setFreeFreight(item.getFreeFreight());
                    skuList.add(dto);
                }
                //TODO 加入运费计算
                CalculateLogisticsResultVO resultVO = systemOrderService.calculateEstimateFreight(new SysOrderPackageDTO() {{
                    setSysOrderPackageDetailList(skuList);
                }}, paltformType, warehouseId, countryCode, postalCode, null, logisticsCode);
                CalculateLogisticsSupplierVO vo = resultVO.getSellerList().get(0);
                BigDecimal logisticsFee = vo.getLogisticsFee();//此包裹卖家的物流费 需要累加全部包裹
                sysOrderPackage.setEstimateShipCost(logisticsFee);//设置包裹级别预估物流费
                sysOrderPackage.setCalculateFeeInfo(FastJsonUtils.toJsonString(resultVO.getLogisticsCostData()));//TODO 添加计算公式
                totalSellerShipFee = totalSellerShipFee.add(logisticsFee);//累加包裹卖家物流费到订单级别的卖家预估物流费
                for (CalculateLogisticsSkuVO skuVO : vo.getSkuList()) {
                    String sku = skuVO.getSku();
                    for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
                        if (sku.equals(item.getSku())) {
                            item.setSellerShipFee(skuVO.getSkuPerCost()); //设置SKU卖家物流费，供应商和物流商默认为0
                        }
                    }
                }
            }
            List<SysOrderPackageDetail> supplierSkuList = collect.get(1); //TODO 包邮  调用物流费接口只取供应商的就行
            if (CollectionUtils.isNotEmpty(supplierSkuList)) {
                Map<Integer, List<SysOrderPackageDetail>> listMap = supplierSkuList.stream().collect(Collectors.groupingBy(SysOrderPackageDetail::getSupplierId));
                for (Integer supplierId : listMap.keySet()) {
                    List<SysOrderPackageDetail> SkuList = listMap.get(supplierId);
                    List<SysOrderPackageDetailDTO> skuList = new ArrayList<>();
                    for (SysOrderPackageDetail item : SkuList) {
                        SysOrderPackageDetailDTO dto = new SysOrderPackageDetailDTO();
                        dto.setSku(item.getSku());
                        dto.setSkuQuantity(item.getSkuQuantity());
                        dto.setSupplierId(item.getSupplierId());
                        dto.setFreeFreight(item.getFreeFreight());
                        skuList.add(dto);
                    }
                    //TODO 加入运费计算
                    CalculateLogisticsResultVO resultVO = systemOrderService.calculateEstimateFreight(new SysOrderPackageDTO() {{
                        setSysOrderPackageDetailList(skuList);
                    }}, paltformType, warehouseId, countryCode, postalCode, null, logisticsCode);
                    sysOrderPackage.setCalculateFeeInfo(FastJsonUtils.toJsonString(resultVO.getLogisticsCostData()));//TODO 添加计算公式
                    for (CalculateLogisticsSupplierVO vo : resultVO.getSupplierList()) {
                        for (CalculateLogisticsSkuVO skuVO : vo.getSkuList()) {
                            String sku = skuVO.getSku();
                            for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
                                if (sku.equals(item.getSku())) {
                                    item.setSupplierShipFee(skuVO.getSkuPerCost()); //设置SKU供应商物流费，卖家和物流商默认为0
                                }
                            }
                        }
                    }
                }
            }
        }
        orderNew.setEstimateShipCost(totalSellerShipFee);
    }
*/

    public void setOrderPackageInfo(SysOrder sysOrder, String loginName, SysOrderNew orderNew, SysOrderReceiveAddress address, SysOrderPackage orderPackage, List<SysOrderPackageDetail> orderPackageDetailList) {
        //设置平台佣金率
        if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
            orderNew.setPlatformCommissionRate(PlatformCommissionEnum.AMAZON.getValue());
        } else if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
            orderNew.setPlatformCommissionRate(PlatformCommissionEnum.EBAY.getValue());
        } else if (sysOrder.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
            orderNew.setPlatformCommissionRate(PlatformCommissionEnum.WISH.getValue());
        }
        //设置订单表数据
        orderNew.setFreeFreightType(Byte.valueOf(String.valueOf(sysOrder.getFreeFreightType())));
        orderNew.setSysOrderId(sysOrder.getSysOrderId());
        orderNew.setDeliverDeadline(sysOrder.getDeliverDeadline());
        orderNew.setOrderSource(sysOrder.getOrderSource());
        orderNew.setPlatformShopId(sysOrder.getPlatformShopId());
        orderNew.setPlatformSellerAccount(sysOrder.getPlatformSellerAccount());
        orderNew.setShopType(sysOrder.getShopType());
        orderNew.setPlatformSellerId(sysOrder.getPlatformSellerId());
        orderNew.setSellerPlId(sysOrder.getSellerPlId());
        orderNew.setSellerPlAccount(sysOrder.getSellerPlAccount());
        orderNew.setSupplyChainCompanyId(sysOrder.getSupplyChainCompanyId());
        orderNew.setSupplyChainCompanyName(sysOrder.getSupplyChainCompanyName());
//        orderNew.setTotal(sysOrder.getTotal());  // 后面物流费算出来后再补上
        orderNew.setOrderAmount(sysOrder.getOrderAmount());
//        orderNew.setEstimateShipCost(sysOrder.getEstimateShipCost());// 后面物流费算出来后再补上
        orderNew.setMarketplaceId(sysOrder.getMarketplaceId());
        orderNew.setOrderTime(sysOrder.getOrderTime());
        orderNew.setBuyerCheckoutMessage(sysOrder.getBuyerCheckoutMessage());
        orderNew.setCreateBy(loginName);
        orderNew.setUpdateBy(loginName);
        orderNew.setRecordNumber(sysOrder.getRecordNumber());
//        orderNew.setGrossMargin(sysOrder.getGrossMargin());// 后面物流费算出来后再补上
//        orderNew.setProfitMargin(sysOrder.getProfitMargin());// 后面物流费算出来后再补上
        //设置收货地址数据
        address.setSysOrderId(sysOrder.getSysOrderId());
        address.setShipToName(sysOrder.getShipToName());
        address.setShipToCountry(sysOrder.getShipToCountry());
        address.setShipToCountryName(sysOrder.getShipToCountryName());
        address.setShipToState(sysOrder.getShipToState());
        address.setShipToCity(sysOrder.getShipToCity());
        address.setShipToAddrStreet1(sysOrder.getShipToAddrStreet1());
        address.setShipToAddrStreet2(sysOrder.getShipToAddrStreet2());
        address.setShipToAddrStreet3(sysOrder.getShipToAddrStreet3());
        address.setShipToPostalCode(sysOrder.getShipToPostalCode());
        address.setShipToPostalCode(sysOrder.getShipToPostalCode());
        address.setShipToPhone(sysOrder.getShipToPhone());
        address.setShipToEmail(sysOrder.getShipToEmail());
        address.setCreater(loginName);
        address.setModifier(loginName);
        //设置包裹数据
        orderPackage.setSysOrderId(sysOrder.getSysOrderId());
        orderPackage.setOrderTrackId(sysOrder.getOrderTrackId());
        orderPackage.setDeliveryWarehouseId(Integer.valueOf(sysOrder.getDeliveryWarehouseId()));
        orderPackage.setDeliveryWarehouseCode(sysOrder.getDeliveryWarehouseCode());
        orderPackage.setDeliveryMethodCode(sysOrder.getDeliveryMethodCode());
        orderPackage.setDeliveryWarehouse(sysOrder.getDeliveryWarehouse());
        orderPackage.setShippingCarrierUsedCode(sysOrder.getShippingCarrierUsedCode());
        orderPackage.setShippingCarrierUsed(sysOrder.getShippingCarrierUsed());
        orderPackage.setLogisticsStrategy(sysOrder.getLogisticsStrategy());
        orderPackage.setDeliveryMethod(sysOrder.getDeliveryMethod());
        orderPackage.setDeliveryMethodCode(sysOrder.getDeliveryMethodCode());
        orderPackage.setEstimateShipCost(sysOrder.getEstimateShipCost());
        orderPackage.setAmazonShippingMethod(sysOrder.getAmazonShippingMethod());
        orderPackage.setAmazonCarrierName(sysOrder.getAmazonCarrierName());
        orderPackage.setEbayCarrierName(sysOrder.getEbayCarrierName());
        orderPackage.setCreater(loginName);
        orderPackage.setModifier(loginName);
        //设置包裹详情数据

        sysOrder.getSysOrderDetails().forEach(sysOrderDetail -> {
            SysOrderPackageDetail orderPackageDetail = new SysOrderPackageDetail();
            orderPackageDetail.setOrderTrackId(orderPackage.getOrderTrackId());
            orderPackageDetail.setSku(sysOrderDetail.getSku());
            orderPackageDetail.setSkuQuantity(sysOrderDetail.getSkuQuantity());
            orderPackageDetail.setSkuCost(sysOrderDetail.getItemCost());
            orderPackageDetail.setSkuUrl(sysOrderDetail.getItemUrl());
            orderPackageDetail.setSkuName(sysOrderDetail.getItemName());
            orderPackageDetail.setSkuNameEn(sysOrderDetail.getItemNameEn());
            orderPackageDetail.setSkuAttr(sysOrderDetail.getItemAttr());
            orderPackageDetail.setSkuPrice(sysOrderDetail.getItemPrice());
            orderPackageDetail.setBulk(sysOrderDetail.getBulk());
            orderPackageDetail.setWeight(sysOrderDetail.getWeight());
            orderPackageDetail.setBulk(sysOrderDetail.getBulk());
            orderPackageDetail.setSupplierId(Math.toIntExact(sysOrderDetail.getSupplierId()));
            orderPackageDetail.setSupplierName(sysOrderDetail.getSupplierName());
            orderPackageDetail.setSupplyChainCompanyId(sysOrderDetail.getSupplyChainCompanyId());
            orderPackageDetail.setSupplyChainCompanyName(sysOrderDetail.getSupplyChainCompanyName());
            orderPackageDetail.setFareTypeAmount(sysOrderDetail.getFareTypeAmount());
//            orderPackageDetail.setSellerShipFee(null);  // TODO 暂时设置为NULL
//            orderPackageDetail.setSupplierShipFee(null); // TODO 暂时设置为NULL
//            orderPackageDetail.setLogisticCompanyShipFee(null); // TODO 暂时设置为NULL
            orderPackageDetail.setFreeFreight(sysOrderDetail.getFreeFreight());
            orderPackageDetail.setCreater(loginName);
            orderPackageDetail.setModifier(loginName);
            orderPackageDetailList.add(orderPackageDetail);
        });
        orderPackage.setSysOrderPackageDetailList(orderPackageDetailList);
        orderNew.setSysOrderPackageList(new ArrayList<SysOrderPackage>() {{
            add(orderPackage);
        }});
        orderNew.setSysOrderReceiveAddress(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertBulk(SysOrder sysOrder, SysOrderNew sysOrderNew) {
        sysOrderNew.setSourceOrderId(sysOrder.getSourceOrderId());
        SysOrderPackage sysOrderPackage = sysOrderNew.getSysOrderPackageList().get(0);
        sysOrder.getSysOrderDetails().forEach(sysOrderDetail -> {  //遍历插入订单详情
            sysOrderDetailMapper.insertSelective(sysOrderDetail);
        });
        sysOrderNewMapper.insertSelective(sysOrderNew);  //插入订单
        sysOrderPackageMapper.insertSelective(sysOrderPackage);//插入包裹
        sysOrderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> {//遍历插入包裹详情
            sysOrderPackageDetailMapper.insertSelective(sysOrderPackageDetail);
        });
        sysOrderReceiveAddressMapper.insertSelective(sysOrderNew.getSysOrderReceiveAddress());  //插入地址
        sysOrderLogService.insertSelective(        //添加订单操作日志
                new SysOrderLog(sysOrderNew.getSysOrderId(),
                        OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrderNew.getSysOrderId()),
                        OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                        userUtils.getUser().getUsername()));
    }

    @Override
    public String addSysOrderNew(SysOrderNew sysOrderNew) {
        //1.校验订单必填项

        //2.调用供应商服务设置仓库信息
        //3.或许并设置卖家的品连主账号和ID
        //4.设置订单相关数据
        //5.插入数据库并添加操作日志
        return "新增系统订单【" + sysOrderNew.getSysOrderId() + "】成功！";
    }

    /**
     * 调用供应商服务设置仓库信息
     *
     * @param sysOrder
     */
    public void setWareHouseInfo(SysOrder sysOrder) {
        WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(sysOrder.getDeliveryWarehouseId());
        if (null == warehouseDTO) {
            logger.error("根据仓库ID：{}找不到仓库信息，供应商服务返回Null", sysOrder.getDeliveryWarehouseId());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据仓库ID查不到仓库信息");
        }
        sysOrder.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());
        sysOrder.setDeliveryWarehouse(warehouseDTO.getWarehouseName());

        String str = remoteSupplierService.queryLogisticsByCode(sysOrder.getDeliveryMethodCode(), Integer.valueOf(sysOrder.getDeliveryWarehouseId()));
        String dataString = Utils.returnRemoteResultDataString(str, "供应商服务异常");
        if (StringUtils.isNotBlank(dataString)) {
            LogisticsDTO logisticsDTO = JSONObject.parseObject(dataString, LogisticsDTO.class);
            String shortName = logisticsDTO.getShortName();
            sysOrder.setDeliveryMethod(shortName);
            sysOrder.setShippingCarrierUsed(logisticsDTO.getCarrierName());
            sysOrder.setShippingCarrierUsedCode(logisticsDTO.getCarrierCode());
            sysOrder.setAmazonCarrierName(logisticsDTO.getAmazonCarrier());
            sysOrder.setAmazonShippingMethod(logisticsDTO.getAmazonCode());
            sysOrder.setEbayCarrierName(logisticsDTO.getEbayCarrier());
        } else {
            logger.error("根据仓库ID：{}和邮寄方式code：{}找不到仓库相关信息，供应商服务返回Null", sysOrder.getDeliveryWarehouseId(), sysOrder.getDeliveryMethodCode());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据仓库ID和邮寄方式code查不到仓库信息");
        }
    }

    /**
     * 调用供应商服务设置仓库信息
     *
     * @param orderPackage
     */
    public void setWareHouseInfo(SysOrderPackage orderPackage) {
        WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(String.valueOf(orderPackage.getDeliveryWarehouseId()));
        if (null == warehouseDTO) {
            logger.error("根据仓库ID：{}找不到仓库信息，供应商服务返回Null", orderPackage.getDeliveryWarehouseId());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据仓库ID查不到仓库信息");
        }
        orderPackage.setDeliveryWarehouseCode(warehouseDTO.getWarehouseCode());
        orderPackage.setDeliveryWarehouse(warehouseDTO.getWarehouseName());

        String str = remoteSupplierService.queryLogisticsByCode(orderPackage.getDeliveryMethodCode(), Integer.valueOf(orderPackage.getDeliveryWarehouseId()));
        String dataString = Utils.returnRemoteResultDataString(str, "供应商服务异常");
        if (StringUtils.isNotBlank(dataString)) {
            LogisticsDTO logisticsDTO = JSONObject.parseObject(dataString, LogisticsDTO.class);
            orderPackage.setDeliveryMethod(logisticsDTO.getShortName());
            orderPackage.setShippingCarrierUsed(logisticsDTO.getCarrierName());
            orderPackage.setShippingCarrierUsedCode(logisticsDTO.getCarrierCode());
            orderPackage.setAmazonCarrierName(logisticsDTO.getAmazonCarrier());
            orderPackage.setAmazonShippingMethod(logisticsDTO.getAmazonCode());
            orderPackage.setEbayCarrierName(logisticsDTO.getEbayCarrier());
        } else {
            logger.error("根据仓库ID：{}和邮寄方式code：{}找不到仓库相关信息，供应商服务返回Null", orderPackage.getDeliveryWarehouseId(), orderPackage.getDeliveryMethodCode());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据仓库ID和邮寄方式code查不到仓库信息");
        }
    }

    /**
     * 设置订单商品信息
     *
     * @param sysOrder
     */
    private void setSysOrderItemInfo(SysOrder sysOrder) {
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            String result = remoteCommodityService.test("1", "1", null, null, null, null,
                    sysOrderDetail.getSku(), null, null);
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
                        if (String.valueOf(skuInventoryVo.getWarehouseId()).equals(sysOrder.getDeliveryWarehouseId())) {
                            count++;
                            //品连单个商品成本价
                            sysOrderDetail.setItemCost(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                            //商品系统单价
                            sysOrderDetail.setItemPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                            //供应商sku单价
                            sysOrderDetail.setSupplierSkuPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                        }
                    }
                    if (count == 0) {
                        //品连单个商品成本价
                        sysOrderDetail.setItemCost(commodityDetail.getCommodityPriceUs());
                        //商品系统单价
                        sysOrderDetail.setItemPrice(commodityDetail.getCommodityPriceUs());
                        //供应商sku单价
                        sysOrderDetail.setSupplierSkuPrice(commodityDetail.getCommodityPriceUs());
                    }
                } else {
                    //品连单个商品成本价
                    sysOrderDetail.setItemCost(commodityDetail.getCommodityPriceUs());
                    //商品系统单价
                    sysOrderDetail.setItemPrice(commodityDetail.getCommodityPriceUs());
                    //供应商sku单价
                    sysOrderDetail.setSupplierSkuPrice(commodityDetail.getCommodityPriceUs());
                }

                //体积
                sysOrderDetail.setBulk(commodityDetail.getPackingHeight().multiply(commodityDetail.getPackingWidth()).multiply(commodityDetail.getPackingLength()));
                //重量
                sysOrderDetail.setWeight(commodityDetail.getPackingWeight());
                //商品ID
                sysOrderDetail.setItemId(commodityDetail.getId());
                //商品URL
                sysOrderDetail.setItemUrl(commodityDetail.getMasterPicture());
                //商品名称
                sysOrderDetail.setItemName(commodityDetail.getCommodityNameCn());
                sysOrderDetail.setItemNameEn(commodityDetail.getCommodityNameEn());
                //商品属性
                sysOrderDetail.setItemAttr(commodityDetail.getCommoditySpec());
                //订单项SKU
                sysOrderDetail.setSku(commodityDetail.getSystemSku());
                //SKU标题
                sysOrderDetail.setSkuTitle(commodityDetail.getCommodityNameCn());
                //供应商ID
                sysOrderDetail.setSupplierId(Long.valueOf(commodityDetail.getSupplierId()));
                //供应商名称
                sysOrderDetail.setSupplierName(commodityDetail.getSupplierName());
                //供应商SKU
                sysOrderDetail.setSupplierSku(commodityDetail.getSupplierSku());
                //供应商SKU标题
                sysOrderDetail.setSupplierSkuTitle(commodityDetail.getCommodityNameCn());
                //服务费 优先取固定服务费
                sysOrderDetail.setFareTypeAmount(commodityDetail.getFeePriceUs() != null ? "1#" + commodityDetail.getFeePriceUs().toString() : "2#" + commodityDetail.getFeeRate().toString());
                //是否包邮
                sysOrderDetail.setFreeFreight(commodityDetail.getFreeFreight());
            }
        }
        //判断该订单是包邮还是不包邮还是部分包邮
        int count = 0;
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            count += sysOrderDetail.getFreeFreight();
        }
        if (count == 0) {
            sysOrder.setFreeFreightType(Constants.SysOrder.NOT_FREE_FREIGHT);
        } else if (count == sysOrder.getSysOrderDetails().size()) {
            sysOrder.setFreeFreightType(Constants.SysOrder.FREE_FREIGHT);
        } else {
            sysOrder.setFreeFreightType(Constants.SysOrder.PART_FREE_FREIGHT);
        }
    }

    public void insertDbAndAddLog(SysOrder sysOrder) {
        setSysOrderNull(sysOrder);//插入前：系统订单部分字段设置为Null
        try {
            sysOrderMapper.insertSelective(sysOrder);//6插入系统订单
            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
                setSysOrderItemNull(sysOrderDetail);//插入前：系统订单项部分字段设置为Null
                sysOrderDetailMapper.insertSelective(sysOrderDetail);   //7插入系统订单项
            }
            //添加操作日志
            sysOrderLogService.insertSelective(
                    new SysOrderLog(sysOrder.getSysOrderId(),
                            OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrder.getSysOrderId()),
                            OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                            userUtils.getUser().getUsername()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

    public void setSysOrderItemNull(SysOrderDetail sysOrderDetail) {
        sysOrderDetail.setSourceOrderId(null);
        sysOrderDetail.setSourceOrderLineItemId(null);
        sysOrderDetail.setRecordNumber(null);
        sysOrderDetail.setCreateDate(null);
        sysOrderDetail.setCreateBy(null);
        sysOrderDetail.setUpdateDate(null);
        sysOrderDetail.setUpdateBy(null);
        sysOrderDetail.setRemark(null);
        sysOrderDetail.setSourceSku(null);
    }

    public void setSysOrderNull(SysOrder sysOrder) {
        sysOrder.setRecordNumber(null);
        sysOrder.setSourceOrderId(null);
        sysOrder.setConverSysStatus(null);
        sysOrder.setOrderDeliveryStatus(null);
        sysOrder.setIsAfterSaleOrder(null);
        sysOrder.setMainOrderId(null);
        sysOrder.setSplittedOrMerged(null);
        sysOrder.setChildIds(null);
        sysOrder.setCommoditiesAmount(null);
        sysOrder.setCreatedTime(null);
//        sysOrder.setMarketplaceId(null);
        sysOrder.setPayId(null);
        sysOrder.setPayStatus(null);
        sysOrder.setPayMethod(null);
        sysOrder.setPayTime(null);
        sysOrder.setDeliveryTime(null);
        sysOrder.setShippingServiceCost(null);
        sysOrder.setActualShipCost(null);
        sysOrder.setShipTrackNumber(null);
        sysOrder.setShipOrderId(null);
        sysOrder.setGrossMargin(null);
        sysOrder.setProfitMargin(null);
        sysOrder.setReferenceId(null);
        sysOrder.setBuyerUserId(null);
        sysOrder.setBuyerName(null);
        sysOrder.setWarehouseShipException(null);
        sysOrder.setMarkException(null);
        sysOrder.setCreateDate(null);
        sysOrder.setCreateBy(null);
        sysOrder.setUpdateDate(null);
        sysOrder.setUpdateBy(null);
        sysOrder.setRemark(null);
        sysOrder.setIsAfterSaleOrder(null);
        sysOrder.getSysOrderDetails().forEach(x -> {
            x.setIsAfterSale(null);
        });
    }

    public void setOrderData(SysOrder sysOrder) {
        //设置店铺类型
        sysOrder.setShopType(orderCommonService.queryAuthorizationFromSeller(sysOrder).getRentstatus() == 0 ? "PERSONAL" : "RENT");
        //获取该用户的供应链公司信息，如果是新接入的订单来源则更改为具体的订单来源，否则默认都为手工新建
        resetOrderSourceIfExistNewSource(sysOrder);//TODO
        //远程调用用户服务获取供应链公司ID和名称并设置进系统订单
        setSupplyChainInfo(sysOrder, null, true);
        sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());//设置订单跟踪号
        String plOrderNumber = OrderUtils.getPLOrderNumber();
        sysOrder.setSysOrderId(plOrderNumber);//设置品连系统订单ID
        BigDecimal orderTotal = new BigDecimal(0);//系统商品总价格
        for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
            setSupplyChainInfo(null, sysOrderDetail, false);
            sysOrderDetail.setSysOrderId(plOrderNumber);  //设置品连系统订单ID
            sysOrderDetail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());//设置品连系统订单项ID
            //商品数量*价格
            orderTotal = orderTotal.add(sysOrderDetail.getItemPrice().multiply(new BigDecimal(sysOrderDetail.getSkuQuantity())));
        }
        //设置系统商品总价格
        sysOrder.setOrderAmount(orderTotal);
    }

    public void getSellerPlAccountAndId(SysOrder sysOrder) {
        if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 1) {  //卖家平台
            String topUserLoginName = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
            Integer topUserId = getLoginUserInformationByToken.getUserDTO().getTopUserId();
            sysOrder.setSellerPlAccount(topUserLoginName);
            sysOrder.setSellerPlId(topUserId);
        } else if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 2) {  //管理后台
            String result = remoteUserService.getSupplyChinByUserIdOrUsername(sysOrder.getSellerPlId(), null, UserEnum.platformType.SELLER.getPlatformType());
            String data = Utils.returnRemoteResultDataString(result, "用户服务异常");
            if (JSONObject.parseObject(data).get("loginName") == null) {
                logger.error(String.valueOf(sysOrder.getSellerPlId()) + "找不到品连账号");
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "根据品连账号ID找不到该品连账号");
            } else {
                sysOrder.setSellerPlAccount(JSONObject.parseObject(data).getString("loginName"));
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401);//其他平台无权限查看
        }
    }

    public void resetOrderSourceIfExistNewSource(SysOrder sysOrder) {
        if (sysOrder.getOrderSource() == null) sysOrder.setOrderSource((byte) 1);
        String result = remoteUserService.getSupplyChinByUserIdOrUsername(sysOrder.getSellerPlId(), null, 1);
        JSONObject data = (JSONObject) JSONObject.parse(Utils.returnRemoteResultDataString(result, "用户服务异常"));
        if (data != null) {
            String supplyUsername = (String) data.get("supplyChainCompanyName");
            if ("星商".equals(supplyUsername)) {  //星商订单来源
                sysOrder.setOrderSource((byte) 8);    //设置订单来源为星商订单来源
            }
        }
    }


    @Override
    /**
     * description: 不定条件查询系统订单
     * @Param: recordNumber         记录号（EBAY才有）
     * @Param: orderTrackId         订单跟踪号
     * @Param: isAfterSaleOrder1    是否售后单  0否 1是
     * @Param: sourceOrderId        来源平台订单ID
     * @Param: isLogisticsAbnormal  是否物流异常订单
     * @Param: splittedOrMerged     已拆分、已合并
     * @Param: platformSellerAccount卖家平台账号
     * @Param: sellerPlAccount      卖家品连账号
     * @Param: sysOrderId           系统订单ID
     * @Param: orderStatus          系统订单状态
     * @Param: startDate           订单创建起始时间
     * @Param: endDate             订单创建结束时间
     * @Param: startTime           订单发货起始时间
     * @Param: endTime             订单发货结束时间
     * @return com.github.pagehelper.PageInfo<com.rondaful.cloud.order.entity.SysOrder>   系统订单集合
     * create by wujiachuang
     */
    public PageInfo<SysOrderNew> selectSysOrderByMultiCondition(String sku, Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId,
                                                                String isAfterSaleOrder1, String sourceOrderId, String isLogisticsAbnormal, String splittedOrMerged,
                                                                String platformSellerAccount, String sellerPlAccount, String sysOrderId, String orderStatus, String startDate,
                                                                String endDate, String startTime, String endTime, Integer bindex, Integer num) {
        sellerPlAccount = getSellerPlAccount(sellerPlAccount);//如果是卖家平台则返回主账号
        Integer plSellerId = null;
        plSellerId = getAccountAndShopInfoUtils.getPlSellerIdIfNotNull(sellerPlAccount, plSellerId);//存在则返回品连账号ID
        List<Integer> userIds = new ArrayList<>();  //账号ID集合
        List<Integer> empIds = new ArrayList<>();  //店铺ID集合
        if (StringUtils.isNotBlank(platformSellerAccount)) {
            List<String> platformSellerAccountList = Arrays.asList(platformSellerAccount.split("#"));
            for (String account : platformSellerAccountList) {
                Integer shopIdIfNotNull = getAccountAndShopInfoUtils.getShopIdIfNotNull(account, sellerPlAccount);//存在则返回店铺ID
                if (shopIdIfNotNull != null) {
                    empIds.add(shopIdIfNotNull);
                }
            }
        }
        if (judgeAuthorityUtils.judgeUserAuthorityAndSetDataToList(plSellerId, getLoginUserInformationByToken.getUserDTO(), userIds, empIds))
            return new PageInfo<>(null);
        Byte isAfterSaleOrder = getIsAfterSaleOrder(isAfterSaleOrder1);//获取是否提供售后入口状态值
        Byte plOrderStatus = getPlOrderStatus(orderStatus);//获取品连订单发货状态
        return getSysOrders(sku, errorOrder, payStatus, orderSource, recordNumber, orderTrackId, sourceOrderId, isLogisticsAbnormal,
                sysOrderId, startDate, endDate, startTime, endTime, userIds, empIds, isAfterSaleOrder, splittedOrMerged, plOrderStatus, bindex, num);

    }

    public byte getIsAfterSaleOrder(String isAfterSaleOrder1) {
        byte isAfterSaleOrder;
        if (isAfterSaleOrder1 == null || "".equals(isAfterSaleOrder1)) {
            isAfterSaleOrder = 3;
        } else {
            if (isAfterSaleOrder1.equals("0") || isAfterSaleOrder1.equals("1") || isAfterSaleOrder1.equals("2")) {
                isAfterSaleOrder = Byte.parseByte(isAfterSaleOrder1);
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
            }
        }
        return isAfterSaleOrder;
    }

    public String getSellerPlAccount(String sellerPlAccount) {
        if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 1) {  //卖家平台
            sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        } else if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() == 2) {  //管理后台
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);//其他平台无权限查看
        }
        return sellerPlAccount;
    }

    public PageInfo<SysOrderNew> getSysOrders(String sku, Byte errorOrder, Byte payStatus, Byte orderSource, String recordNumber, String orderTrackId, String sourceOrderId,
                                              String isLogisticsAbnormal, String sysOrderId, String startDate, String endDate, String startTime, String endTime, List<Integer> userIds,
                                              List<Integer> empIds, byte isAfterSaleOrder, String splittedOrMerged, byte plOrderStatus, Integer bindex, Integer num) {
        List<String> sysOrderIdList = new ArrayList<>();
        List<SysOrderNew> list = new ArrayList<>();
        list = getSysOrderNews(sysOrderId, sku, errorOrder, payStatus, orderSource, recordNumber, orderTrackId, sourceOrderId, isLogisticsAbnormal,
                startDate, endDate, startTime, endTime, userIds, empIds, isAfterSaleOrder, splittedOrMerged, plOrderStatus, bindex, num, sysOrderIdList, list);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<SysOrderNew> newSysOrderList = new ArrayList<>();
        for (SysOrderNew orderNew : list) {
            queryOrderShowPackageAndDetailInfo(orderNew, true);//组装订单其余信息
            newSysOrderList.add(JSONObject.parseObject(JSON.toJSONString(orderNew)).toJavaObject(SysOrderNew.class));
        }
        List<SysOrderNew> newList = new ArrayList<>();
        for (SysOrderNew sysOrderNew : newSysOrderList) {
            translateSysOrderArgs(sysOrderNew);//翻译物流方式、仓库等
            setPlatformSkuAndInventoryInfo(sysOrderNew); //设置平台订单的发货状态、平台商品信息 判断库存、可用数量
            setErrorOrderExceptionInfo(sysOrderNew);//如果是异常订单，查出全部包裹的异常信息
            setPayOrderInfo(sysOrderNew);//设置支付订单的信息
            judgeOrder(sysOrderNew);//判断该订单信息是否完整足以发货，提供APP判断用
            queryAfterOrderInfo(sysOrderNew);//查询售后信息
            setIsLessThanThreshold(sysOrderNew);//判断是否低于利润阈值
            if (sysOrderNew.getOrderInfoIsIntact() == false) { //信息不完整
                sysOrderNew.setIsOOS(false);
                sysOrderNew.setIsLessThanThreshold(false);
            } else {
                if (sysOrderNew.getIsOOS() == true) { //缺货
                    sysOrderNew.setIsLessThanThreshold(false);
                }
            }
            newList.add(JSONObject.parseObject(JSON.toJSONString(sysOrderNew)).toJavaObject(SysOrderNew.class));
        }
        PageInfo<SysOrderNew> pageInfo = new PageInfo<>(list);
        pageInfo.setList(newList);
        return pageInfo;
    }

    public void judgeOrder(SysOrderNew sysOrderNew) {
        String str = JSON.toJSONString(sysOrderNew);
        SysOrderNew orderNew = JSONObject.parseObject(str).toJavaObject(SysOrderNew.class);
        SysOrderNew orderNew1 = judgeOrderIsFull(orderNew);
        sysOrderNew.setOrderInfoIsIntact(orderNew1.getOrderInfoIsIntact());
    }

    public void setIsLessThanThreshold(SysOrderNew sysOrderNew) {
        if (sysOrderNew.getGrossMargin() != null && sysOrderNew.getPayStatus().intValue() == 0) {
            if (!systemOrderCommonService.getThreshold(sysOrderNew.getSellerPlId(), sysOrderNew.getPlatformShopId(), sysOrderNew.getProfitMargin())) {
                sysOrderNew.setIsLessThanThreshold(true);
            }
        }
    }

    public List<SysOrderNew> getSysOrderNews(String sysOrderId, String sku, Byte errorOrder, Byte payStatus, Byte orderSource,
                                             String recordNumber, String orderTrackId, String sourceOrderId, String isLogisticsAbnormal,
                                             String startDate, String endDate, String startTime, String endTime, List<Integer> userIds, List<Integer> empIds,
                                             byte isAfterSaleOrder, String splittedOrMerged, byte plOrderStatus, Integer bindex, Integer num, List<String> sysOrderIdList,
                                             List<SysOrderNew> list) {
        if (StringUtils.isNotBlank(sysOrderId)) {
            sysOrderIdList.add(sysOrderId);
        }
        if (StringUtils.isNotBlank(sku)) {
            //根据SKU查出订单ID集合
            List<String> idList = sysOrderDetailMapper.querySysOrderIdListBySku(sku);
            if (CollectionUtils.isEmpty(idList)) {
                return null;
            } else {
                sysOrderIdList.addAll(idList);
            }
        }
        if (StringUtils.isNotBlank(orderTrackId)) { //包裹号不为空 先查出包裹号 再反推出订单、从而查出订单详情再塞进集合
            SysOrderPackage orderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(orderTrackId);
            if (orderPackage == null) {
                logger.error("根据包裹号找不到包裹，包裹ID为{}", orderTrackId);
                return null;
            }
            String packageSysOrderId = orderPackage.getSysOrderId();
            if (StringUtils.isNotBlank(packageSysOrderId)) {  //TODO 普通包裹或者拆分的包裹
                sysOrderIdList.add(packageSysOrderId);
                list = sysOrderNewMapper.selectSysOrderByMultiCondition(errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder,
                        sourceOrderId, isLogisticsAbnormal, splittedOrMerged, empIds, userIds, sysOrderIdList, plOrderStatus, startDate, endDate, startTime, endTime, bindex, num);
            } else {                          //TODO 合并包裹没有订单号，找到合并的订单号
                sysOrderIdList.addAll(Arrays.asList(orderPackage.getOperateSysOrderId().split("\\#")));
                list = sysOrderNewMapper.selectSysOrderByMultiCondition(errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder, sourceOrderId, isLogisticsAbnormal, splittedOrMerged, empIds, userIds, sysOrderIdList, plOrderStatus, startDate, endDate, startTime, endTime, bindex, num);
            }
        } else {
            list = sysOrderNewMapper.selectSysOrderByMultiCondition(errorOrder, payStatus, orderSource, recordNumber, orderTrackId, isAfterSaleOrder, sourceOrderId, isLogisticsAbnormal, splittedOrMerged, empIds, userIds, sysOrderIdList, plOrderStatus, startDate, endDate, startTime, endTime, bindex, num);
        }
        return list;
    }

    /**
     * 如果主订单中存在子订单，则查出子订单设置回主订单
     *
     * @param sysOrder
     * @param childIds
     */
    public void setChildOrderIfExist(SysOrder sysOrder, String childIds) {
        if (childIds.length() != 0) {
            String[] split = childIds.split("#");
            List<String> childsIds = new ArrayList<>();
            for (String s : split) {
                childsIds.add(s);
            }
            List<SysOrder> sysChildOrders = sysOrderMapper.selectSysChildOrdersByChildOrderIds(childsIds);
            for (SysOrder sysChildOrder : sysChildOrders) {
                dealOrder(new SysOrderNew());//设置售后信息、店铺名、翻译
            }
            sysOrder.setSysChildOrderList(sysChildOrders);   //设置子订单进主订单  没有就是Null
        }
    }

    /**
     * 设置 平台订单的发货状态、平台商品信息 判断库存、可用数量
     *
     * @param orderNew
     */
    public void setPlatformSkuAndInventoryInfo(SysOrderNew orderNew) {
        setPlatformOrderInfo(orderNew);  // 设置平台订单的发货状态、平台商品信息
        setInventoryInfo(orderNew);//判断库存、可用数量
    }

    /**
     * 设置售后信息、店铺名、翻译
     *
     * @param orderNew
     */
    public void dealOrder(SysOrderNew orderNew) {
        queryAfterOrderInfo(orderNew);//查询售后信息
        //只要订单包裹详情中有任何一个SKU进行过售后的，订单都要设置有售后的标记
//        checkIsAfterSaleOrder(orderNew);
     /*   String shopName = getAccountAndShopInfoUtils.GetSellerShopNameByShopId(sysOrder.getPlatformShopId(), sysOrder.getSellerPlAccount());
        sysOrder.setPlatformSellerAccount(shopName);*/
        translateSysOrderArgs(orderNew);//翻译
    }

    /**
     * 查出有售后标记订单的售后信息
     * i
     *
     * @param sysOrder
     */
    public SysOrderNew queryAfterOrderInfo(SysOrderNew sysOrder) {
        if (sysOrder.getSplittedOrMerged().equalsIgnoreCase(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
            Map<String, List<JudgeAfterSaleDTO>> map = new HashMap<>();
            List<String> list = Arrays.asList(sysOrder.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"));
            for (String orderId : list) {
                String result = remoteCmsService.findOrderAfterSaleByOrderId(orderId);
                if (StringUtils.isNotBlank(result)) {
                    String data = Utils.returnRemoteResultDataString(result, "售后服务异常！");
                    if (StringUtils.isNotBlank(data)) {
                        List<OrderAfterSalesOrderDetailsModel> list1 = JSONObject.parseArray(data, OrderAfterSalesOrderDetailsModel.class);
                        if (CollectionUtils.isNotEmpty(list1)) {
                            for (OrderAfterSalesOrderDetailsModel model : list1) {
                                String orderId1 = model.getOrderId();
                                for (OrderAfterSalesCommodityOrderDetail detail : model.getOrderAfterSalesCommodityOrderDetails()) {
                                    String sku = detail.getCommoditySku();
                                    Long count = detail.getCommodityNumber();
                                    String orderTrackId = detail.getOrderTrackId();
                                    Integer skuQuantity = sysOrderDetailMapper.querySysOrderDetailByOrderIdAndSku(orderId1, sku).getSkuQuantity();
                                    JudgeAfterSaleDTO dto = new JudgeAfterSaleDTO();
                                    dto.setAfterAount(count);
                                    dto.setCount(Long.valueOf(skuQuantity));
                                    dto.setPackageId(orderTrackId);
                                    dto.setSysOrderId(orderId1);
                                    dto.setSku(sku);
                                    dto.setAfterSale(true);
                                    if (map.containsKey(sku)) {
                                        List<JudgeAfterSaleDTO> list3 = map.get(sku);
                                        list3.add(dto);
                                        map.put(sku, list3);
                                    } else {
                                        List<JudgeAfterSaleDTO> list2 = new ArrayList<>();
                                        list2.add(dto);
                                        map.put(sku, list2);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    logger.error("订单ID：{}调用售后服务返回Null", orderId);
                }
            } //设置合并订单进行过售后的Map
            for (SysOrderPackage sysOrderPackage : sysOrder.getSysOrderPackageList()) {
                for (SysOrderPackageDetail item : sysOrderPackage.getSysOrderPackageDetailList()) {
                    if (item.getIsAfterSale() == Constants.IsAfterSale.YES) {
                        List<String> orderIdList = new ArrayList<>(); //含有该SKU的订单ID集合
                        for (String orderId : list) {
                            SysOrderDetail sysOrderDetail = sysOrderDetailMapper.querySysOrderDetailByOrderIdAndSku(orderId, item.getSku());
                            if (sysOrderDetail != null) {
                                orderIdList.add(orderId);
                            }
                        }
                        if (CollectionUtils.isNotEmpty(orderIdList)) {
                            for (String id : orderIdList) {
                                //一个SKU可能来自不同的订单去申请售后，因此需要判断用户所选的订单ID是否对该SKU进行过售后
                                if (id.equalsIgnoreCase(sysOrder.getSysOrderId()) && !JSONObject.toJSONString(map.get(item.getSku())).contains(id)) {
                                    item.setIsAfterSale(0);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
//            }
            //上面对售后标记做出了判断
            // 下面处理SKU可进行售后的数量上限
            for (SysOrderPackage orderPackage : sysOrder.getSysOrderPackageList()) {
                String orderTrackId = orderPackage.getOrderTrackId();
                for (SysOrderPackageDetail item : orderPackage.getSysOrderPackageDetailList()) {
                    List<JudgeAfterSaleDTO> judgeAfterSaleDTOS = new ArrayList<>();
                    String sku = item.getSku();
                    if (StringUtils.isNotBlank(sku)) {
                        for (String orderId : list) {
                            SysOrderDetail sysOrderDetail = sysOrderDetailMapper.querySysOrderDetailByOrderIdAndSku(orderId, sku);
                            if (sysOrderDetail != null) {  // 原来订单该SKU的数量不为空
                                Integer beforeMergeSkuQuantity = sysOrderDetail.getSkuQuantity();
                                List<JudgeAfterSaleDTO> dtoList = map.get(sku);
                                if (CollectionUtils.isNotEmpty(dtoList)) {
                                    //进行过售后
                                    for (JudgeAfterSaleDTO dto : dtoList) {
                                        if (dto.getSysOrderId().equalsIgnoreCase(orderId)) {
                                            judgeAfterSaleDTOS.add(dto);
                                        } else {
                                            JudgeAfterSaleDTO dto1 = new JudgeAfterSaleDTO();
                                            dto1.setAfterAount(Long.valueOf(0));
                                            dto1.setCount(Long.valueOf(beforeMergeSkuQuantity));
                                            dto1.setPackageId(orderTrackId);
                                            dto1.setSysOrderId(orderId);
                                            dto1.setSku(sku);
                                            dto1.setAfterSale(false);
                                            judgeAfterSaleDTOS.add(dto1);
                                        }
                                    }
                                } else {
                                    //没有进行过售后
                                    JudgeAfterSaleDTO dto = new JudgeAfterSaleDTO();
                                    dto.setAfterAount(Long.valueOf(0));
                                    dto.setCount(Long.valueOf(beforeMergeSkuQuantity));
                                    dto.setPackageId(orderTrackId);
                                    dto.setSysOrderId(orderId);
                                    dto.setSku(sku);
                                    dto.setAfterSale(false);
                                    judgeAfterSaleDTOS.add(dto);
                                }
                            }
                        }
                    }
                    item.setJudgeAfterSaleDTOList(judgeAfterSaleDTOS);
                }
            }
        }
        //TODO ----------------------------------------上面为专门给合并订单设置的标记
        String result = remoteCmsService.findOrderAfterSaleByOrderId(sysOrder.getSysOrderId());
        String data = Utils.returnRemoteResultDataString(result, "售后服务异常！");
        if (StringUtils.isNotBlank(data)) {
            List<OrderAfterVo> orderAfterVoList = new ArrayList<>();
            List<OrderAfterSalesOrderDetailsModel> list = JSONObject.parseArray(data, OrderAfterSalesOrderDetailsModel.class);
            for (OrderAfterSalesOrderDetailsModel model : list) {
                for (OrderAfterSalesCommodityOrderDetail item : model.getOrderAfterSalesCommodityOrderDetails()) {
                    OrderAfterVo orderAfterVo = new OrderAfterVo();
                    orderAfterVo.setAfterSalesType(model.getAfterSalesType());
                    orderAfterVo.setStatus(model.getStatus());
                    orderAfterVo.setCreateTime(model.getCreateTime());
                    orderAfterVo.setCommodityName(item.getCommodityName());
                    orderAfterVo.setCommodityNameEn(item.getCommodityNameEn());
                    orderAfterVo.setCommodityNumber(item.getCommodityNumber());
                    orderAfterVo.setCommodityRefundMoney(model.getRefundMoney());
                    orderAfterVo.setCommoditySku(item.getCommoditySku());
                    orderAfterVo.setOrderAfterSalesId(item.getOrderAfterSalesId());
                    orderAfterVoList.add(orderAfterVo);
                }
            }
            sysOrder.setOrderAfterVoList(orderAfterVoList);
        }

        return sysOrder;
    }

    /**
     * 设置主子订单的平台发货状态、平台商品SKU、平台商品货币类型、平台商品金额
     *
     * @param orderNew
     */
    public void setPlatformOrderInfo(SysOrderNew orderNew) {
        if (StringUtils.isNotBlank(orderNew.getSourceOrderId())) {
            setOrderInfo(orderNew);
        }
    }

    public void setOrderInfo(SysOrderNew orderNew) {
        String orderStatus = "";
        Byte source = orderNew.getOrderSource();
        if (StringUtils.isNotBlank(orderNew.getSourceOrderId())) {
            switch (source) {
                case 3://第三方推送的订单
                    orderNew.getSysOrderPackageList().forEach(orderPackage -> {
                        orderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> {
                            if (StringUtils.isNotBlank(sysOrderPackageDetail.getSourceSku())) {
                                sysOrderPackageDetail.setPlatformOrderItemInfo(new PlatformOrderItemInfo(sysOrderPackageDetail.getSourceSku(), null, null, null));
                            }
                        });
                    });
                    break;
                case 4://EBAY订单
                    EbayOrder ebayOrder = ebayOrderMapper.queryEbayOrderDetail(orderNew.getSourceOrderId());
                    if (ebayOrder != null) {
                        String createdTime = ebayOrder.getCreatedTime();
                        String time = ebayOrderStatusMapper.selectLastShippingTimeByOrderId(ebayOrder.getOrderId());
                        if (StringUtils.isNotBlank(createdTime)) {
                            orderNew.setPlatformOrderTime(TimeUtil.stringToDate(createdTime));//订单创建时间
                        }
                        if (StringUtils.isNotBlank(time)) {
                            orderNew.setLastShippingTime(TimeUtil.stringToDate(time)); //最迟发货时间
                        }
                        //缺最迟发货时间
                        //订单状态:Active(未完成),Completed(已付款),Cancelled(已取消),CancelPending(买家请求取消),Inactive(不活动的订单),InProcess(处理中)
                        switch (ebayOrder.getOrderStatus()) {
                            case "Active":
                                orderStatus = "未完成";
                                break;
                            case "Completed":
                                orderStatus = "已付款";
                                break;
                            case "Cancelled":
                                orderStatus = "已取消";
                                break;
                            case "CancelPending":
                                orderStatus = "买家请求取消";
                                break;
                            case "Inactive":
                                orderStatus = "不活动的订单";
                                break;
                            case "InProcess":
                                orderStatus = "处理中";
                                break;
                        }
                        //设置商品信息
                        orderNew.getSysOrderPackageList().forEach(sysOrderPackage -> {
                            sysOrderPackage.getSysOrderPackageDetailList().forEach(sysOrderDetail -> {
                                String sourceOrderLineItemId = sysOrderDetail.getSourceOrderLineItemId().split("\\#")[0];
                                if (StringUtils.isNotBlank(sourceOrderLineItemId)) {
                                    List<String> ebayOrderItemIds = new ArrayList<>();
                                    ebayOrderItemIds.add(sourceOrderLineItemId);
                                    List<EbayOrderDetail> ebayOrderDetails = ebayOrderDetailMapper.queryBatchEbayOrderDetailByOrderLineItemId(ebayOrderItemIds);
//                                    PlatformOrderItemInfo platformOrderItemInfo = new PlatformOrderItemInfo();
                                    if (CollectionUtils.isNotEmpty(ebayOrderDetails)) {
                                        for (EbayOrderDetail ebayOrderDetail : ebayOrderDetails) {
                                            Integer quantityPurchased = ebayOrderDetail.getQuantityPurchased();
                                            String price = ebayOrderDetail.getTransactionPrice().split("\\#")[0];
                                            String currencyCode = ebayOrderDetail.getTransactionPrice().split("\\#")[1];
                                            String sku = "";
                                            if (StringUtils.isNotBlank(ebayOrderDetail.getVariationSku())) {
                                                sku = ebayOrderDetail.getVariationSku();
                                            } else {
                                                sku = ebayOrderDetail.getSku();
                                            }
                                            sysOrderDetail.setPlatformOrderItemInfo(new PlatformOrderItemInfo(sku, price, quantityPurchased, currencyCode));
                                            sysOrderDetail.setSourceSku(sku);
                                        }
                                    }
                                }
                            });
                        });
                    } else {
                        logger.error("根据来源订单号：" + orderNew.getSourceOrderId() + "找不到EBAY平台订单，订单号：" + orderNew.getSysOrderId());
                    }
                    break;
                case 5://Amazon订单
                    AmazonOrder amazonOrder = amazonOrderMapper.selectAmazonOrderByOrderId(orderNew.getSourceOrderId());
                    if (amazonOrder != null) {
                        Date latestShipTime = amazonOrder.getLatestShipTime(); //最迟发货时间
                        Date paymentTime = amazonOrder.getPaymentTime(); //平台下单时间
                        orderNew.setLastShippingTime(latestShipTime);
                        orderNew.setPlatformOrderTime(paymentTime);
                        orderStatus = amazonOrder.getOrderStatus();//订单状态：待发货，部分发货，均已发货，均已发货、未寄发票，已取消',
                        //设置商品信息
                        orderNew.getSysOrderPackageList().forEach(sysOrderPackage -> {
                            sysOrderPackage.getSysOrderPackageDetailList().forEach(sysOrderDetail -> {
                                String sourceItemId = sysOrderDetail.getSourceOrderLineItemId().split("\\#")[0];
                                if (StringUtils.isNotBlank(sourceItemId)) {
                                    AmazonOrderDetail amazonOrderDetail = amazonOrderDetailMapper.selectAmazonOrderItem(sourceItemId);
                                    if (amazonOrderDetail != null) {
                                        String platformSku = amazonOrderDetail.getPlatformSku();
                                        String itemCurrencyCode = amazonOrderDetail.getItemCurrencyCode();
                                        String itemPrice = amazonOrderDetail.getItemPrice().toString();
                                        Integer quantity = amazonOrderDetail.getQuantity();
                                        sysOrderDetail.setPlatformOrderItemInfo(new PlatformOrderItemInfo(platformSku, itemPrice, quantity, itemCurrencyCode));
                                        sysOrderDetail.setSourceSku(platformSku);
                                    }
                                }
                            });
                        });
                    } else {
                        logger.error("根据来源订单号：" + orderNew.getSourceOrderId() + "找不到亚马逊平台订单，订单号：" + orderNew.getSysOrderId());
                    }
                    break;
                case 6://AliExpress订单
                    AliexpressOrder aliexpressOrder = aliexpressOrderMapper.getByOrderId(orderNew.getSourceOrderId());
                    if (aliexpressOrder != null) {
                        Date gmtCreate = aliexpressOrder.getGmtCreate();
                        Date overTimeLeft = aliexpressOrder.getOverTimeLeft();
                        orderNew.setPlatformOrderTime(gmtCreate);//
                        orderNew.setLastShippingTime(overTimeLeft);
                        //缺最迟发货时间
                        switch (aliexpressOrder.getOrderStatus()) {
                            case "WAIT_SELLER_SEND_GOODS":
                                orderStatus = "待发货";
                                break;
                            case "SELLER_PART_SEND_GOODS":
                                orderStatus = "部分发货";
                                break;
                            case "WAIT_BUYER_ACCEPT_GOODS":
                                orderStatus = "等待买家收货";
                                break;
                            case "FUND_PROCESSING":
                                orderStatus = "资金处理中";
                                break;
                            case "IN_ISSUE":
                                orderStatus = "纠纷中";
                                break;
                            case "IN_FROZEN":
                                orderStatus = "冻结中的订单";
                                break;
                            case "IN_CANCEL":
                                orderStatus = "买家申请取消";
                                break;
                        }
                        //设置商品信息
                        orderNew.getSysOrderPackageList().forEach(sysOrderPackage -> {
                            sysOrderPackage.getSysOrderPackageDetailList().forEach(sysOrderDetail -> {
                                String sourceItemId = sysOrderDetail.getSourceOrderLineItemId().split("\\#")[0];
                                if (StringUtils.isNotBlank(sourceItemId)) {
                                    AliexpressOrderChild aliexpressOrderChild = aliexpressOrderChildMapper.getByOrderId(sourceItemId);
                                    if (aliexpressOrderChild != null) {
                                        String platformSku = aliexpressOrderChild.getSkuCode();
                                        String itemCurrencyCode = aliexpressOrderChild.getCurrencyCode();
                                        String itemPrice = aliexpressOrderChild.getAmount().toString();
                                        Integer productCount = aliexpressOrderChild.getProductCount();
                                        sysOrderDetail.setPlatformOrderItemInfo(new PlatformOrderItemInfo(platformSku, itemPrice, productCount, itemCurrencyCode));
                                        sysOrderDetail.setSourceSku(platformSku);
                                    }
                                }
                            });
                        });
                    } else {
                        logger.error("根据来源订单号：" + orderNew.getSourceOrderId() + "找不到速卖通平台订单，订单号：" + orderNew.getSysOrderId());
                    }
                    break;
            }
            orderNew.setPlatformOrderStatus(orderStatus);
        }
    }

    public Byte getSplittedOrMergedStatus(String splittedOrMerged) {
        Byte splittedOrMergedStatus;
        if ("普通".equals(splittedOrMerged)) {
            splittedOrMergedStatus = 0;
        } else if ("已拆分".equals(splittedOrMerged)) {
            splittedOrMergedStatus = 1;
        } else {
            if (("已合并".equals(splittedOrMerged))) {
                splittedOrMergedStatus = 2;
            } else {
                splittedOrMergedStatus = 3;
            }
        }
        return splittedOrMergedStatus;
    }

    public byte getPlOrderStatus(String orderStatus) {
        //订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6已收货,7已作废'
        byte plOrderStatus;
        if (orderStatus == null || "null".equals(orderStatus) || "".equals(orderStatus)) {
            plOrderStatus = 0;   //全选
        } else {
            if (orderStatus.equals("待发货")) {
                plOrderStatus = OrderDeliveryStatusNewEnum.WAIT_PAY.getValue();
            } else if (orderStatus.equals(OrderDeliveryStatusNewEnum.STOCKOUT.getMsg())) {
                plOrderStatus = OrderDeliveryStatusNewEnum.STOCKOUT.getValue();
            } else if (orderStatus.equals("配货中")) {
                plOrderStatus = OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue();
            } else if (orderStatus.equals(OrderDeliveryStatusNewEnum.INTERCEPTED.getMsg())) {
                plOrderStatus = OrderDeliveryStatusNewEnum.INTERCEPTED.getValue();
            } else if (orderStatus.equals(OrderDeliveryStatusNewEnum.DELIVERED.getMsg())) {
                plOrderStatus = OrderDeliveryStatusNewEnum.DELIVERED.getValue();
            } else if (orderStatus.equals(OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getMsg())) {
                plOrderStatus = OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getValue();
            } else if (orderStatus.equals(OrderDeliveryStatusNewEnum.CANCELLED.getMsg()) || orderStatus.equals("已取消")) {
                plOrderStatus = OrderDeliveryStatusNewEnum.CANCELLED.getValue();
            } else {
                plOrderStatus = OrderDeliveryStatusNewEnum.COMPLETED.getValue();
            }
        }
        return plOrderStatus;
    }

    /**
     * 翻译仓库、物流方式
     *
     * @param orderNew 采购订单对象
     */
    public void translateSysOrderArgs(SysOrderNew orderNew) {
        //翻译仓库和物流名称和物流方式和物流异常信息
        orderNew.getSysOrderPackageList().forEach(sysOrderPackage -> {
            if (sysOrderPackage.getLogisticsStrategy().equals("cheapest")) {
                sysOrderPackage.setLogisticsStrategy(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.CHEAPEST.getLogisticsType()));
            }
            if (sysOrderPackage.getLogisticsStrategy().equals("integrated_optimal")) {
                sysOrderPackage.setLogisticsStrategy(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.INTEGRATED_OPTIMAL.getLogisticsType()));
            }
            if (sysOrderPackage.getLogisticsStrategy().equals("fastest")) {
                sysOrderPackage.setLogisticsStrategy(String.valueOf(LogisticsStrategyCovertToLogisticsLogisticsType.FASTEST.getLogisticsType()));
            }
            if (StringUtils.isNotBlank(sysOrderPackage.getDeliveryWarehouse())) {
                sysOrderPackage.setDeliveryWarehouse(com.rondaful.cloud.common.utils.Utils.translation(sysOrderPackage.getDeliveryWarehouse()));
            }
            if (StringUtils.isNotBlank(sysOrderPackage.getShippingCarrierUsed())) {
                sysOrderPackage.setShippingCarrierUsed(com.rondaful.cloud.common.utils.Utils.translation(sysOrderPackage.getShippingCarrierUsed()));
            }
            if (StringUtils.isNotBlank(sysOrderPackage.getDeliveryMethod())) {
                sysOrderPackage.setDeliveryMethod(com.rondaful.cloud.common.utils.Utils.translation(sysOrderPackage.getDeliveryMethod()));
            }
            if (StringUtils.isNotBlank(sysOrderPackage.getWarehouseShipException())) {
                sysOrderPackage.setWarehouseShipException(com.rondaful.cloud.common.utils.Utils.translation(sysOrderPackage.getWarehouseShipException()));
            }
        });
    }

    @Override
    /**
     * description: 通过卖家品连账号查询系统订单
     * @Param: sellerPlAccount   卖家品连账号
     * @return com.rondaful.cloud.common.entity.Page<com.rondaful.cloud.order.entity.SysOrder> 系统订单集合
     * create by wujiachuang
     */
    public Page<SysOrder> selectSysOrderBySellerPlAccount(String sellerPlAccount) {
        List<SysOrder> sysOrderList = null;
        sysOrderList = sysOrderMapper.selectSysOrderBySellerPlAccount(sellerPlAccount);
        if (sysOrderList == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
        PageInfo<SysOrder> pageInfo = new PageInfo<>(sysOrderList);
        return new Page<>(pageInfo);
    }

    @Override
    /**
     * description: 通过系统订单ID查询系统订单
     * @Param: orderId  订单ID
     * @return com.rondaful.cloud.order.entity.SysOrder 系统订单对象
     * create by wujiachuang
     */
    public SysOrderNew getSysOrderDetailByPlOrderId(String orderId) {
        SysOrderNew orderNew = queryOrder(orderId);//查询订单
        serMergeOrderDetail(orderNew);//设置合并订单的订单详情信息  sku物流费等信息 供PC端详情使用
        dealOrder(orderNew);//设置售后信息、店铺名、翻译
        setPlatformSkuAndInventoryInfo(orderNew); //设置平台订单的发货状态、平台商品信息 判断库存、可用数量
        setPlatformCommission(orderNew);//设置平台佣金
        setInvoiceInfo(orderId, orderNew);//设置发票信息
        setPayOrderInfo(orderNew);//设置支付订单的信息
//        setGrossMarginAndProfitMarginNullIfLessZero(orderNew);//预估利润小于0设置null
        judgeOrderIsFreeFreight(orderNew);  //判断该订单是包邮还是不包邮还是部分包邮   根据查出要展示的包裹详情来判断
        orderNew = judgeOrderIsFull(orderNew); //判断该订单信息是否完整足以发货，提供APP判断用
        setSupplierSku(orderNew);//设置包裹详情供应商SKU
        setErrorOrderExceptionInfo(orderNew);//如果是异常订单，查出全部包裹的异常信息
        return orderNew;
    }

    /**
     * description: 通过系统订单ID查询系统订单(包含所有的sku，只供订单详情查询使用)
     *
     * @return com.rondaful.cloud.order.entity.SysOrder 系统订单对象
     * create by lijiantao
     * @Param: orderId  订单ID
     */
    @Override
    public SysOrderNew getSysOrderContainAllSkuByOrderId(String orderId) {
        SysOrderNew orderNew = queryOrderForDetails(orderId);//查询订单
        serMergeOrderDetail(orderNew);//设置合并订单的订单详情信息  sku物流费等信息 供PC端详情使用
        dealOrder(orderNew);//设置售后信息、店铺名、翻译
        setPlatformSkuAndInventoryInfo(orderNew); //设置平台订单的发货状态、平台商品信息 判断库存、可用数量
        setPlatformCommission(orderNew);//设置平台佣金
        setInvoiceInfo(orderId, orderNew);//设置发票信息
        setPayOrderInfo(orderNew);//设置支付订单的信息
//        setGrossMarginAndProfitMarginNullIfLessZero(orderNew);//预估利润小于0设置null
        judgeOrderIsFreeFreight(orderNew);  //判断该订单是包邮还是不包邮还是部分包邮   根据查出要展示的包裹详情来判断
        orderNew = judgeOrderIsFull(orderNew); //判断该订单信息是否完整足以发货，提供APP判断用
        setSupplierSku(orderNew);//设置包裹详情供应商SKU
        setErrorOrderExceptionInfo(orderNew);//如果是异常订单，查出全部包裹的异常信息
        setIsLessThanThreshold(orderNew);//设置是否低于利润阈值
        reSetEbayRecordNumber(orderNew);//如果EBAY订单记录号为空,则取平台订单号（复制订单用到）
        return orderNew;
    }

    public void reSetEbayRecordNumber(SysOrderNew orderNew) {
        if (orderNew.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
            if (StringUtils.isBlank(orderNew.getRecordNumber())) {
                orderNew.setRecordNumber(orderNew.getSourceOrderId());
            }
        }
    }

    public void setErrorOrderExceptionInfo(SysOrderNew orderNew) {
        if (orderNew.getIsErrorOrder().equals("yes")) {
            if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
                orderNew.setWarehouseShipExceptionVoList(sysOrderPackageMapper.queryWarehouseShipExceptionByOrderTrackId(orderNew.getSysOrderPackageList().get(0).getOrderTrackId()));
            } else {
                orderNew.setWarehouseShipExceptionVoList(sysOrderPackageMapper.queryWarehouseShipExceptionByOrderId(orderNew.getSysOrderId()));
            }
        }
    }

    public void setSupplierSku(SysOrderNew orderNew) {
        orderNew.getSysOrderPackageList().forEach(orderPackage -> {
            orderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> {
                if (StringUtils.isNotBlank(sysOrderPackageDetail.getSku())) {
                    String result = remoteCommodityService.test("1", "1", null, null, null, null,
                            sysOrderPackageDetail.getSku(), null, null);
                    String data = Utils.returnRemoteResultDataString(result, "调用商品服务异常");
                    JSONObject parse1 = (JSONObject) JSONObject.parse(data);
                    String pageInfo = parse1.getString("pageInfo");
                    JSONObject parse2 = (JSONObject) JSONObject.parse(pageInfo);
                    JSONArray list1 = parse2.getJSONArray("list");
                    List<CommoditySpec> commodityDetails = list1.toJavaList(CommoditySpec.class);
                    for (CommoditySpec commodityDetail : commodityDetails) {
                        sysOrderPackageDetail.setSupplierSku(commodityDetail.getSupplierSku());
                    }
                }
            });
        });
    }

    public SysOrderNew judgeOrderIsFull(SysOrderNew orderNew) {
        List<SysOrderPackageDetail> list = new ArrayList<>();
        SysOrderNew order = new SysOrderNew();
        String str = JSON.toJSONString(orderNew);
        BeanUtils.copyProperties(orderNew, order);
        for (SysOrderPackage sysOrderPackage : order.getSysOrderPackageList()) {
            for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                if (!sysOrderPackageDetail.getBindStatus().equalsIgnoreCase(SkuBindEnum.REMOVE.getValue())) {
                    list.add(sysOrderPackageDetail);
                }
            }
            sysOrderPackage.setSysOrderPackageDetailList(list);
        }
        orderNew = JSONObject.parseObject(str).toJavaObject(SysOrderNew.class);
        try {
            for (SysOrderPackage orderPackage : order.getSysOrderPackageList()) {
                CheckOrderUtils.validateSysOrderDataForDeliverGood(orderNew, orderPackage, orderNew.getSysOrderReceiveAddress());
            }
        } catch (Exception e) {
            orderNew.setOrderInfoIsIntact(false);
            return orderNew;
        }
        orderNew.setOrderInfoIsIntact(true);
        return orderNew;
    }

    public void judgeOrderIsFreeFreight(SysOrderNew orderNew) {
        int count = 0;
        int packageDetailCount = 0;
        for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {
            for (SysOrderPackageDetail sysOrderPackageDetail : orderPackage.getSysOrderPackageDetailList()) {
                packageDetailCount++;
                count += sysOrderPackageDetail.getFreeFreight();
            }
        }
        if (count == 0) {
            orderNew.setFreeFreightType(Byte.valueOf(String.valueOf(Constants.SysOrder.NOT_FREE_FREIGHT)));
        } else if (count == packageDetailCount) {
            orderNew.setFreeFreightType(Byte.valueOf(String.valueOf(Constants.SysOrder.FREE_FREIGHT)));
        } else {
            orderNew.setFreeFreightType(Byte.valueOf(String.valueOf(Constants.SysOrder.PART_FREE_FREIGHT)));
        }
    }

    public void serMergeOrderDetail(SysOrderNew orderNew) {
        if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
            for (SysOrderDetail sysOrderDetail : orderNew.getSysOrderDetails()) {
                for (SysOrderPackageDetail packageDetail : orderNew.getSysOrderPackageList().get(0).getSysOrderPackageDetailList()) {
                    if (sysOrderDetail.getSku().equals(packageDetail.getSku())) {
                        sysOrderDetail.setSellerShipFee(packageDetail.getSellerShipFee());
                        sysOrderDetail.setSupplierShipFee(packageDetail.getSupplierShipFee());
                        sysOrderDetail.setLogisticCompanyShipFee(packageDetail.getLogisticCompanyShipFee());
                        sysOrderDetail.setPrice(sysOrderDetail.getItemPrice().multiply(new BigDecimal(sysOrderDetail.getSkuQuantity())).add(sysOrderDetail.getSellerShipFee().multiply(new BigDecimal(sysOrderDetail.getSkuQuantity())))); //设置卖家平台订单详情SKU总价
                        sysOrderDetail.setSkuTotalShipFee(sysOrderDetail.getSellerShipFee().multiply(BigDecimal.valueOf(sysOrderDetail.getSkuQuantity())));
                    }
                }
            }
        }
    }

//    public void setGrossMarginAndProfitMarginNullIfLessZero(SysOrderNew orderNew) {
//        if (StringUtils.isBlank(orderNew.getSourceOrderId())) {  //手工订单 利润为负的话设置null ，前端展示 --
//            if (orderNew.getGrossMargin().signum() == -1) {
//                orderNew.setGrossMargin(null);
//                orderNew.setProfitMargin(null);
//            }
//        }
//    }

    public void setPayOrderInfo(SysOrderNew orderNew) {
        a:
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            if (sysOrderPackage.getOperateSysOrderId().contains("#")) {
                List<PayOrderInfo> list = new ArrayList<>();
                List<String> idList = Arrays.asList(sysOrderPackage.getOperateSysOrderId().split("\\#"));
                for (String id : idList) {
                    PayOrderInfo info = new PayOrderInfo();
                    SysOrderNew order = sysOrderNewMapper.queryOrderByOrderId(id);
                    info.setSysOrderId(order.getSysOrderId());
                    info.setOrderAmount(order.getOrderAmount());
                    info.setEstimateShipCost(order.getEstimateShipCost());
                    info.setTotal(order.getTotal());
                    list.add(info);
                }
                orderNew.setPayOrderInfos(list);
                break a;
            } else {
                List<PayOrderInfo> list = new ArrayList<>();
                PayOrderInfo info = new PayOrderInfo();
                info.setSysOrderId(orderNew.getSysOrderId());
                info.setOrderAmount(orderNew.getOrderAmount());
                info.setEstimateShipCost(orderNew.getEstimateShipCost());
                info.setTotal(orderNew.getTotal());
                list.add(info);
                orderNew.setPayOrderInfos(list);
                break a;
            }
        }
    }

    public void setPlatformCommission(SysOrderNew orderNew) {
        BigDecimal platformTotalPrice = orderNew.getPlatformTotalPrice();//平台订单总价
        if (platformTotalPrice != null) {
            if (orderNew.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
                orderNew.setPlatformCommission(platformTotalPrice.multiply(PlatformCommissionEnum.AMAZON.getValue()).setScale(2, BigDecimal.ROUND_DOWN));
            } else if (orderNew.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
                orderNew.setPlatformCommission(platformTotalPrice.multiply(PlatformCommissionEnum.EBAY.getValue()).setScale(2, BigDecimal.ROUND_DOWN));
            } else if (orderNew.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
                orderNew.setPlatformCommission(platformTotalPrice.multiply(PlatformCommissionEnum.WISH.getValue()).setScale(2, BigDecimal.ROUND_DOWN));
            }
        }
    }

    public void setInvoiceInfo(String orderId, SysOrderNew orderNew) {
        SysOrderInvoiceVO vo = sysOrderInvoiceService.getSysOrderInvoiceBySysOrderId(orderId);
        if (vo != null) {
            SysOrderInvoiceInsertOrUpdateDTO dto = new SysOrderInvoiceInsertOrUpdateDTO();
            BeanUtils.copyProperties(vo, dto);
            orderNew.setSysOrderInvoiceInsertOrUpdateDTO(dto);//设置发票信息
        }
    }

    @Override
    public SysOrderNew queryOrder(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数有误！");
        }
        SysOrderNew orderNew = sysOrderNewMapper.queryOrderByOrderId(orderId);
        if (orderNew == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "未查询到结果！请确认订单号是否正确");
        } else {
            queryOrderShowPackageAndDetailInfo(orderNew, false);
        }
        return orderNew;
    }

    @Override
    public SysOrderNew queryOrderForDetails(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数有误！");
        }
        SysOrderNew orderNew = sysOrderNewMapper.queryOrderByOrderId(orderId);
        if (orderNew == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "未查询到结果！请确认订单号是否正确");
        } else {
            queryOrderShowPackageContainAllSku(orderNew, false);
        }
        return orderNew;
    }

    @Override
    public SysOrderNew queryOrderByOther(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "请求参数有误！");
        }
        SysOrderNew orderNew = sysOrderNewMapper.queryOrderByOrderId(orderId);
        if (orderNew == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "未查询到结果！请确认订单号是否正确");
        } else {
            queryOrderShowPackageAndDetailInfo(orderNew, true);
        }
        setPlatformSkuAndInventoryInfo(orderNew); //设置平台订单的发货状态、平台商品信息 判断库存、可用数量
        dealOrder(orderNew);//设置售后信息、店铺名、翻译
        //设置供应商SKU
        setSkuInfo(orderNew);
        return orderNew;
    }

    public void setSkuInfo(SysOrderNew orderNew) {
        if (orderNew.getSplittedOrMerged().equalsIgnoreCase(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
            List<String> list = Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("#"));
            List<SysOrderDetail> sysOrderDetails = new ArrayList<>();
            for (String id : list) {
                sysOrderDetails.addAll(sysOrderDetailMapper.querySysOrderDetailByOrderId(id));
            }
            Map<String, List<SysOrderDetail>> map = sysOrderDetails.stream().collect(Collectors.groupingBy(sysOrderDetail -> sysOrderDetail.getSku()));
            for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {
                for (SysOrderPackageDetail detail : orderPackage.getSysOrderPackageDetailList()) {
                    detail.setSupplierSku(map.get(detail.getSku()).get(0).getSupplierSku());
                }
            }
        } else {
            Map<String, List<SysOrderDetail>> map = orderNew.getSysOrderDetails().stream().collect(Collectors.groupingBy(sysOrderDetail -> sysOrderDetail.getSku()));
            for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {
                for (SysOrderPackageDetail detail : orderPackage.getSysOrderPackageDetailList()) {
                    detail.setSupplierSku(map.get(detail.getSku()).get(0).getSupplierSku());
                }
            }
        }
    }

    public SysOrderNew queryOrderAndNoShowPackage(String orderId) {
        SysOrderNew orderNew = sysOrderNewMapper.queryOrderByOrderId(orderId);
        if (orderNew == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "未查询到结果！请确认订单号是否正确");
        } else {
            queryOrderNoShowPackageAndDetailInfo(orderNew, false);
        }
        return orderNew;
    }

    public void queryOrderShowPackageAndDetailInfo(SysOrderNew orderNew, boolean isMultiQuery) {
        List<SysOrderDetail> sysOrderDetails = sysOrderDetailMapper.querySysOrderDetailByOrderId(orderNew.getSysOrderId());
        if (CollectionUtils.isNotEmpty(sysOrderDetails)) {
            orderNew.setSysOrderDetails(sysOrderDetails);  //TODO 设置订单详情
        } else {
            logger.error("根据订单ID：{}查不到订单详情", orderNew.getSysOrderId());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        SysOrderReceiveAddress address = sysOrderReceiveAddressMapper.queryAddressByOrderId(orderNew.getSysOrderId());
        if (address == null) {
            logger.error("根据订单ID：{}查不到收货地址", orderNew.getSysOrderId());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        } else {
            orderNew.setSysOrderReceiveAddress(address);//TODO 设置收货地址
            List<SysOrderPackage> sysOrderPackages = new ArrayList<>();
            if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
                String orderTrackId = sysOrderPackageMapper.queryOrderPackage(orderNew.getSysOrderId()).get(0).getOperateOrderTrackId();
                SysOrderPackage sysOrderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(orderTrackId);
                sysOrderPackages.add(sysOrderPackage);
            } else {//拆分或者普通订单
                sysOrderPackages = sysOrderPackageMapper.queryOrderPackageByOrderId(orderNew.getSysOrderId());
            }
            if (CollectionUtils.isNotEmpty(sysOrderPackages)) {
                sysOrderPackages.forEach(sysOrderPackage -> {
                    String orderTrackId = sysOrderPackage.getOrderTrackId();
                    List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDetailMapper.queryOrderPackageDetails(orderTrackId);
                    if (CollectionUtils.isEmpty(sysOrderPackageDetails)) {
                        logger.error("根据包裹号:{}找不到包裹详情", orderTrackId);
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
                    } else {
                        //对包裹详情进行排序
                        sysOrderPackageDetails = sysOrderPackageDetails.stream().sorted((o1, o2) ->
                                o1.getBindStatus().compareTo(o2.getBindStatus())
                        ).collect(Collectors.toList());
                        if (!isMultiQuery) {
                            if (!getLoginUserInformationByToken.getUserDTO().getPlatformType().equals(UserEnum.platformType.CMS.getPlatformType())) {
                                sysOrderPackageDetails.forEach(item -> {
                                    if (Objects.equals(item.getFreeFreight(), 1)) {   //此处代码为陈老板场景需求    包邮商品 卖家还有SKU物流费，因此展示出来需要设置为0
                                        item.setSellerShipFee(new BigDecimal("0"));
                                    } else {
                                        item.setSupplierShipFee(new BigDecimal("0"));   //此处代码为陈老板场景需求    不包邮商品 供应商SKU物流费，展示出来需要设置为0
                                    }
                                    item.setPrice(item.getSkuPrice().multiply(new BigDecimal(item.getSkuQuantity())).add(item.getSellerShipFee().multiply(new BigDecimal(item.getSkuQuantity())))); //设置卖家平台订单详情SKU总价
                                    item.setSkuTotalShipFee(item.getSellerShipFee().multiply(BigDecimal.valueOf(item.getSkuQuantity())));
                                });
                            }
                        }
                        sysOrderPackage.setSysOrderPackageDetailList(sysOrderPackageDetails);
                    }
                });
                orderNew.setSysOrderPackageList(sysOrderPackages);//TODO 设置订单包裹集合（包含包裹详情信息）
            } else {
                logger.error("根据订单ID:{}找不到包裹", orderNew.getSysOrderId());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
            }
        }
    }

    public void queryOrderShowPackageContainAllSku(SysOrderNew orderNew, boolean isMultiQuery) {
        List<SysOrderDetail> sysOrderDetails = sysOrderDetailMapper.querySysOrderDetailCoantainAllSkuByOrderId(orderNew.getSysOrderId());
        if (CollectionUtils.isNotEmpty(sysOrderDetails)) {
            orderNew.setSysOrderDetails(sysOrderDetails);  //TODO 设置订单详情
        } else {
            logger.error("根据订单ID：{}查不到订单详情", orderNew.getSysOrderId());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        SysOrderReceiveAddress address = sysOrderReceiveAddressMapper.queryAddressByOrderId(orderNew.getSysOrderId());
        if (address == null) {
            logger.error("根据订单ID：{}查不到收货地址", orderNew.getSysOrderId());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        } else {
            orderNew.setSysOrderReceiveAddress(address);//TODO 设置收货地址
            List<SysOrderPackage> sysOrderPackages = new ArrayList<>();
            if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
                String orderTrackId = sysOrderPackageMapper.queryOrderPackage(orderNew.getSysOrderId()).get(0).getOperateOrderTrackId();
                SysOrderPackage sysOrderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(orderTrackId);
                sysOrderPackages.add(sysOrderPackage);
            } else {//拆分或者普通订单
                sysOrderPackages = sysOrderPackageMapper.queryOrderPackageByOrderId(orderNew.getSysOrderId());
            }
            if (CollectionUtils.isNotEmpty(sysOrderPackages)) {
                sysOrderPackages.forEach(sysOrderPackage -> {
                    String orderTrackId = sysOrderPackage.getOrderTrackId();
                    List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDetailMapper.queryOrderPackageDetailsContainAllSku(orderTrackId);
                    if (CollectionUtils.isEmpty(sysOrderPackageDetails)) {
                        logger.error("根据包裹号:{}找不到包裹详情", orderTrackId);
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
                    } else {
                        if (!isMultiQuery) {
                            if (!getLoginUserInformationByToken.getUserDTO().getPlatformType().equals(UserEnum.platformType.CMS.getPlatformType())) {
                                sysOrderPackageDetails.forEach(item -> {
                                    if (Objects.equals(item.getFreeFreight(), 1)) {   //此处代码为陈老板场景需求    包邮商品 卖家还有SKU物流费，因此展示出来需要设置为0
                                        item.setSellerShipFee(new BigDecimal("0"));
                                    } else {
                                        item.setSupplierShipFee(new BigDecimal("0"));   //此处代码为陈老板场景需求    不包邮商品 供应商SKU物流费，展示出来需要设置为0
                                    }
                                    item.setPrice(item.getSkuPrice().multiply(new BigDecimal(item.getSkuQuantity())).add(item.getSellerShipFee().multiply(new BigDecimal(item.getSkuQuantity())))); //设置卖家平台订单详情SKU总价
                                    item.setSkuTotalShipFee(item.getSellerShipFee().multiply(BigDecimal.valueOf(item.getSkuQuantity())));
                                });
                            }
                        }
                        sysOrderPackage.setSysOrderPackageDetailList(sysOrderPackageDetails);
                    }
                });
                orderNew.setSysOrderPackageList(sysOrderPackages);//TODO 设置订单包裹集合（包含包裹详情信息）
            } else {
                logger.error("根据订单ID:{}找不到包裹", orderNew.getSysOrderId());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
            }
        }
    }

    public void queryOrderNoShowPackageAndDetailInfo(SysOrderNew orderNew, boolean isMultiQuery) {
        List<SysOrderDetail> sysOrderDetails = sysOrderDetailMapper.querySysOrderDetailByOrderId(orderNew.getSysOrderId());
        if (CollectionUtils.isNotEmpty(sysOrderDetails)) {
            orderNew.setSysOrderDetails(sysOrderDetails);  //TODO 设置订单详情
        } else {
            logger.error("根据订单ID：{}查不到订单详情", orderNew.getSysOrderId());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        SysOrderReceiveAddress address = sysOrderReceiveAddressMapper.queryAddressByOrderId(orderNew.getSysOrderId());
        if (address == null) {
            logger.error("根据订单ID：{}查不到收货地址", orderNew.getSysOrderId());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        } else {
            orderNew.setSysOrderReceiveAddress(address);//TODO 设置收货地址
            List<SysOrderPackage> sysOrderPackages = sysOrderPackageMapper.queryOrderNoShowPackageByOrderId(orderNew.getSysOrderId());
            if (CollectionUtils.isNotEmpty(sysOrderPackages)) {
                sysOrderPackages.forEach(sysOrderPackage -> {
                    String orderTrackId = sysOrderPackage.getOrderTrackId();
                    List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDetailMapper.queryOrderPackageDetails(orderTrackId);
                    if (CollectionUtils.isEmpty(sysOrderPackageDetails)) {
                        logger.error("根据包裹号:{}找不到包裹详情", orderTrackId);
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
                    } else {
                        if (!isMultiQuery) {
                            sysOrderPackageDetails.forEach(item -> {
                                item.setPrice(item.getSkuPrice().multiply(new BigDecimal(item.getSkuQuantity())).add(item.getSellerShipFee())); //设置卖家平台订单详情SKU总价
                            });
                        }
                        sysOrderPackage.setSysOrderPackageDetailList(sysOrderPackageDetails);
                    }
                });
                orderNew.setSysOrderPackageList(sysOrderPackages);//TODO 设置订单包裹集合（包含包裹详情信息）
            } else {
                logger.error("根据订单ID:{}找不到包裹", orderNew.getSysOrderId());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
            }
        }
    }

    /**
     * 设置包裹可用库存数以及判断是否缺货
     *
     * @param orderNew
     */
    public void setInventoryInfo(SysOrderNew orderNew) {
        if (orderNew.getOrderDeliveryStatus() == OrderDeliveryStatusNewEnum.WAIT_PAY.getValue()
                || orderNew.getOrderDeliveryStatus() == OrderDeliveryStatusNewEnum.INTERCEPTED.getValue()) {
            setInventory(orderNew);
        }
    }

    /**
     * 查询可用库存，符合条件判断是否缺货
     *
     * @param orderNew
     */
    public void setInventory(SysOrderNew orderNew) {
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            if (sysOrderPackage.getDeliveryWarehouseId() != null && sysOrderPackage.getDeliveryWarehouseId() != -1) {
                List<String> skus = new ArrayList<>();
                Map<String, Integer> map = new HashMap<>();
                List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDetailMapper.queryOrderPackageDetails(sysOrderPackage.getOrderTrackId());
                for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetails) {
                    skus.add(sysOrderPackageDetail.getSku());
                    map.put(sysOrderPackageDetail.getSku(), sysOrderPackageDetail.getSkuQuantity());
                }
                String str = JSON.toJSONString(skus);
                String result = remoteSupplierService.getsBySku(Integer.valueOf(sysOrderPackage.getDeliveryWarehouseId()), str);
                String data = Utils.returnRemoteResultDataString(result, "供应商服务异常");
                List<InventoryDTO> list = JSONObject.parseArray(data, InventoryDTO.class);
                if (CollectionUtils.isNotEmpty(list)) {
                    for (InventoryDTO inventoryDTO : list) {
                        String pinlianSku = inventoryDTO.getPinlianSku();
                        Integer availableQty = inventoryDTO.getLocalAvailableQty();
                        if (availableQty <= 0 || availableQty < map.get(pinlianSku)) {
                            orderNew.setIsOOS(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void checkIsAfterSaleOrder(SysOrderNew orderNew) {
        /*if (sysOrder.getIsAfterSaleOrder() == 0 && sysOrder.getOrderDeliveryStatus() == 5) {
            String deliveryTime = sysOrder.getDeliveryTime();//发货时间
            if (!OrderUtils.isValidDate(deliveryTime) || sysOrder.getPayStatus() != 21) { //当前时间>发货时间后60天 或 支付状态不为21付款成功
                sysOrder.setIsAfterSaleOrder((byte) 1);
            }
        }*/
        orderNew.getSysOrderPackageList().forEach(orderPackage -> {
            orderPackage.getSysOrderPackageDetailList().forEach(sysOrderPackageDetail -> {
                if (sysOrderPackageDetail.getIsAfterSale() != 0) {
                    orderNew.setIsAfterSaleOrder((byte) 1);
                    return;
                }
            });
        });

    }

    @Override
    /**
     * description: 编辑系统订单 TODO  BUG   包裹详情如果前端没传的话  会为空，就不能设置物流费进去  需改进
     * @Param: sysOrder  系统订单对象
     * @return java.lang.String  编辑系统订单结果
     * create by wujiachuang
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateSysOrder(SysOrderNew orderNew, String area) {
        orderNew.getSysOrderPackageList().forEach(orderPackage -> reSetLogisticsStrategy(orderPackage));
        if (!redissLockUtil.tryLock(orderNew.getSysOrderId(), 10, 5)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统繁忙，请稍后尝试！");
        }
        //除了卖家、管理后台之外，其他平台无权限编辑
        if (getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() != 1 && getLoginUserInformationByToken.getUserInfo().getUser().getPlatformType() != 2) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401);
        }
        //不能编辑的是，配货中、作废、已发货、已收货，拆分的主订单不能编辑，合并的子订单不能编辑
        SysOrderNew order = queryOrderForDetails(orderNew.getSysOrderId());
        checkOrderStatus(order);//订单状态校验
        //1.校验订单必填项
        String msg = CheckOrderUtils.checkEditSysOrder(orderNew, area);
        if (msg.equalsIgnoreCase("success")) {
            insertSkuBindLog(orderNew, order);//如果是转入的订单，则插入SKU的绑定日志
            setNewOrderInfoToOldOrder(orderNew, order); //将需要编辑的信息设置到查出的对象中
            switch (area) {
                case "3"://TODO 编辑全部订单信息
                    String loginName = getLoginUserInformationByToken.getUserDTO().getLoginName();
                    if (order.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {
                        //设置订单商品信息
                        sysOrderUpdateService.setSysOrderItemInfo(orderNew, order);
                        //设置包裹详情信息
                        sysOrderUpdateService.setOrderPackageDetailInfo(loginName, order);
                        //设置SKU的绑定状态
                        reSetSkuBingStatus(order);
                    }
                    //判断是否选中仓库、物流等信息
                    Boolean logisticsFull = this.logisticsFull(order.getSysOrderPackageList());
                    reSetPackageDetailPrice(order);  //TODO 分仓定价业务，根据仓库ID 重新设置商品价格
                    reSetOrderMoney(order);// 重新计算订单总价、订单总售价
                    order.setIsErrorOrder("no"); //清空异常信息
                    if (StringUtils.isBlank(loginName)) {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
                    } else {
                        order.setUpdateBy(loginName);
                        order.getSysOrderReceiveAddress().setModifier(loginName);
                        sysOrderReceiveAddressMapper.updateAddressByOrderId(order.getSysOrderReceiveAddress());
                        order.getSysOrderPackageList().forEach(orderPackage -> {
                            orderPackage.setModifier(loginName);
                            orderPackage.setWarehouseShipException("");
                            setWareHouseInfo(orderPackage);//调用供应商服务设置仓库信息
                        });
                        if (logisticsFull) {
                            setShipFee(order);//物流费计算
                        } else {
                            order.setEstimateShipCost(null);
                        }

                        //TODO 编辑订单，如果是合并订单还要将合并前的订单编辑 包裹SKU运费，订单预估物流费  订单总售价 利润
                        List<SysOrderNew> list = new ArrayList<>();
                        if (order.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) { //合并订单
                            list = setSoureMergeOrderInfo(order, loginName);
                            sysOrderService.commonDeal(order);
                            for (SysOrderNew sysOrderNew : list) {
                                sysOrderNewMapper.updateOrder(sysOrderNew);
                                sysOrderNew.getSysOrderPackageList().forEach(orderPackage -> {  //TODO 编辑包裹信息
                                    sysOrderPackageMapper.editPackageInfo(orderPackage);
                                    for (SysOrderPackageDetail item : orderPackage.getSysOrderPackageDetailList()) { //TODO 编辑包裹详情
                                        sysOrderPackageDetailMapper.editPackageDetailInfo(item);
                                    }
                                });
                            }
                        } else {   //拆分普通订单
                            resetData(order); //重新设置订单数据
                            if (order.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                                sysOrderService.commonDeal(order);
                            } else {
                                //更新包裹、包裹详情，订单详情：先删除再添加【手工、普通订单】
                                sysOrderUpdateService.updateInfo(order);
                            }
                            sysOrderNewMapper.updateOrder(order);
                        }
                        redissLockUtil.unlock(order.getSysOrderId());
                        return "采购订单更改成功！";
                    }
                default:
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
            }
        } else {
            return "";
        }
    }

    /**
     * 判断订单物流信息是否完整
     *
     * @param sysOrderPackageList
     * @return
     */
    public Boolean logisticsFull(List<SysOrderPackage> sysOrderPackageList) {
        for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {
            if (ObjectUtils.isEmpty(sysOrderPackage.getDeliveryWarehouseId())
                    || StringUtils.isBlank(sysOrderPackage.getDeliveryWarehouse())
                    || StringUtils.isBlank(sysOrderPackage.getShippingCarrierUsedCode())
                    || StringUtils.isBlank(sysOrderPackage.getShippingCarrierUsed())
                    || StringUtils.isBlank(sysOrderPackage.getDeliveryMethodCode())
                    || StringUtils.isBlank(sysOrderPackage.getDeliveryMethod())) {
                return false;
            }
        }
        return true;
    }

    public void insertSkuBindLog(SysOrderNew orderNew, SysOrderNew order) {
        if (order.getIsConvertOrder().equalsIgnoreCase(Constants.isConvertOrder.YES)) {
            setPlatformOrderInfo(order);
            HashMap<String, SysOrderPackageDetail> skuSourceBindMap = new HashMap<>();
            for (SysOrderPackage sysOrderPackage : order.getSysOrderPackageList()) {
                for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                    skuSourceBindMap.put(sysOrderPackageDetail.getSourceOrderLineItemId(), sysOrderPackageDetail);
                }
            }
            for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
                for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                    SysOrderPackageDetail oldSourcePackageDetail = skuSourceBindMap.get(sysOrderPackageDetail.getSourceOrderLineItemId());
                    String sourceSku = oldSourcePackageDetail.getSourceSku();
                    String oldBindStatus = oldSourcePackageDetail.getBindStatus();
                    String newBindStatus = sysOrderPackageDetail.getBindStatus();
                    String oldSku = oldSourcePackageDetail.getSku();
                    String newSku = sysOrderPackageDetail.getSku();
                    if (newBindStatus.equalsIgnoreCase(SkuBindEnum.BIND.getValue())) {
                        if (oldBindStatus.equalsIgnoreCase(SkuBindEnum.UNBIND.getValue())) {
                            sysOrderLogMapper.insertSelective(new SysOrderLog(order.getSysOrderId(),
                                    OrderHandleLogEnum.Content.SKU_BIND_INSERT.skuBindInsert(sourceSku, newSku),
                                    OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                                    userUtils.getUser().getUsername()));
                        } else if (oldBindStatus.equalsIgnoreCase(SkuBindEnum.BIND.getValue())) {
                            if (!oldSku.equalsIgnoreCase(newSku)) {
                                sysOrderLogMapper.insertSelective(new SysOrderLog(order.getSysOrderId(),
                                        OrderHandleLogEnum.Content.SKU_BIND_UPDATE.skuBindUpdate(sourceSku, newSku),
                                        OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                                        userUtils.getUser().getUsername()));
                            }
                        } else {
                            sysOrderLogMapper.insertSelective(new SysOrderLog(order.getSysOrderId(),
                                    OrderHandleLogEnum.Content.SKU_BIND_UPDATE.skuBindUpdate(sourceSku, newSku),
                                    OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                                    userUtils.getUser().getUsername()));
                        }
                    } else if (newBindStatus.equalsIgnoreCase(SkuBindEnum.REMOVE.getValue())) {
                        if (!oldBindStatus.equalsIgnoreCase(SkuBindEnum.REMOVE.getValue())) {
                            sysOrderLogMapper.insertSelective(new SysOrderLog(order.getSysOrderId(),
                                    OrderHandleLogEnum.Content.SKU_BIND_REMOVE.skuBindRemove(sourceSku),
                                    OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                                    userUtils.getUser().getUsername()));
                        }
                    } else {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "SKU绑定状态有误！");
                    }
                }
            }
        }
    }

    public void reSetSkuBingStatus(SysOrderNew order) {
        Map<String, String> bindStatusMap = new HashMap<>();
        for (SysOrderPackage sysOrderPackage : order.getSysOrderPackageList()) {
            for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                if (StringUtils.isBlank(sysOrderPackageDetail.getSku())) {
                    sysOrderPackageDetail.setBindStatus(SkuBindEnum.REMOVE.getValue());
                    continue;
                }
                bindStatusMap.put(sysOrderPackageDetail.getSku(), sysOrderPackageDetail.getBindStatus());
            }
        }
        for (SysOrderDetail sysOrderDetail : order.getSysOrderDetails()) {
            if (StringUtils.isBlank(sysOrderDetail.getSku())) {
                sysOrderDetail.setBindStatus(SkuBindEnum.REMOVE.getValue());
                continue;
            }
            sysOrderDetail.setBindStatus(bindStatusMap.get(sysOrderDetail.getSku()));

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String editOrderInfo(SysOrderNew orderNew, String area) {
        //1. 判断操作者是否为登录用户
        String loginName = getLoginUserInformationByToken.getUserDTO().getLoginName();
        if (org.apache.commons.lang3.StringUtils.isBlank(loginName)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100406, "用户登录状态信息异常");
        }
        //2. 判断订单号是否存在
        if (orderNew == null || org.apache.commons.lang3.StringUtils.isBlank(orderNew.getSysOrderId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403, "参数不得为空");
        }
        //3. 查询数据库中对应的订单信息
        SysOrderNew order = sysOrderNewMapper.queryOrderByOrderId(orderNew.getSysOrderId());
        if (order == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "订单编号错误，无法查询到此单号的相关信息");
        }
        //4. 判断订单基础信息是否完善
        CheckOrderUtils.checkAddress(orderNew.getSysOrderReceiveAddress());
        //5. 判断订单详情是否为空
        return "";

    }

    public void reSetOrderMoney(SysOrderNew order) {
        BigDecimal orderAmount = BigDecimal.ZERO;
        for (SysOrderPackage orderPackage : order.getSysOrderPackageList()) {
            for (SysOrderPackageDetail sysOrderPackageDetail : orderPackage.getSysOrderPackageDetailList()) {
                if (StringUtils.isNotBlank(sysOrderPackageDetail.getSku())) {
                    orderAmount = orderAmount.add(sysOrderPackageDetail.getSkuPrice().multiply(BigDecimal.valueOf(sysOrderPackageDetail.getSkuQuantity())));
                }
            }
        }
        order.setOrderAmount(orderAmount);//设置订单成本价
        if (orderAmount != null && order.getEstimateShipCost() != null) {
            order.setTotal(orderAmount.add(order.getEstimateShipCost()));//设置订单总售价：订单成本价+订单预估物流费
        }
    }

    public void reSetPackageDetailPrice(SysOrderNew order) {
        for (SysOrderPackage orderPackage : order.getSysOrderPackageList()) {
            List<SysOrderPackageDetail> sysOrderPackageDetailList = orderPackage.getSysOrderPackageDetailList();
            for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetailList) {
                if (StringUtils.isNotBlank(sysOrderPackageDetail.getSku())) {
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
                                if (String.valueOf(skuInventoryVo.getWarehouseId()).equals(String.valueOf(orderPackage.getDeliveryWarehouseId()))) {
                                    count++;
                                    //商品系统成本价
                                    sysOrderPackageDetail.setSkuCost(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                                    //商品系统单价
                                    sysOrderPackageDetail.setSkuPrice(new BigDecimal(skuInventoryVo.getWarehousePrice()));
                                }
                            }
                            if (count == 0) {
                                //商品系统成本价
                                sysOrderPackageDetail.setSkuCost(commodityDetail.getCommodityPriceUs());
                                //商品系统单价
                                sysOrderPackageDetail.setSkuPrice(commodityDetail.getCommodityPriceUs());
                            }
                        } else {
                            //商品系统成本价
                            sysOrderPackageDetail.setSkuCost(commodityDetail.getCommodityPriceUs());
                            //商品系统单价
                            sysOrderPackageDetail.setSkuPrice(commodityDetail.getCommodityPriceUs());
                        }
                    }
                }
            }
        }
        HashMap<String, BigDecimal> map = new HashMap<>();
        for (SysOrderPackage sysOrderPackage : order.getSysOrderPackageList()) {
            for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                map.put(sysOrderPackageDetail.getSku(), sysOrderPackageDetail.getSkuPrice());
            }
        }
        for (SysOrderDetail sysOrderDetail : order.getSysOrderDetails()) {
            sysOrderDetail.setItemPrice(map.get(sysOrderDetail.getSku()));
            sysOrderDetail.setSupplierSkuPrice(map.get(sysOrderDetail.getSku()));
            sysOrderDetail.setItemCost(map.get(sysOrderDetail.getSku()));
            ;
        }
    }

    public void setNewOrderInfoToOldOrder(SysOrderNew orderNew, SysOrderNew order) {
        List<SysOrderPackage> list = new ArrayList<>();

        order.setSysOrderReceiveAddress(orderNew.getSysOrderReceiveAddress()); //地址
        List<SysOrderPackage> sysOrderPackageList = order.getSysOrderPackageList(); //设置新的包裹信息到原来的包裹对象
        for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) { //new
            for (SysOrderPackage sysOrderPackage : sysOrderPackageList) { //old
                if (orderPackage.getOrderTrackId().equals(sysOrderPackage.getOrderTrackId())) {
                    //orderPackage.setSysOrderPackageDetailList(orderPackage.getSysOrderPackageDetailList());
                    list.add(orderPackage);
                }
            }
        }
        order.setSysOrderPackageList(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void commonDeal(SysOrderNew orderNew) {
        setSysOrderInvoiceInfo(orderNew);//新建或者编辑订单发票信息
        for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {//TODO 编辑包裹信息并且添加操作日志
            sysOrderPackageMapper.editPackageInfo(orderPackage);
            for (SysOrderPackageDetail item : orderPackage.getSysOrderPackageDetailList()) { //TODO 编辑包裹详情
                sysOrderPackageDetailMapper.editPackageDetailInfo(item);
            }
            sysOrderLogService.insertSelective(new SysOrderLog(orderNew.getSysOrderId(), OrderHandleLogEnum.Content.EDIT_PACKAGE.editPackage(orderPackage.getOrderTrackId(), orderPackage.getDeliveryWarehouse(), orderPackage.getDeliveryMethod()), OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(), userUtils.getUser().getUsername()));
        }
    }

    public List<SysOrderNew> setSoureMergeOrderInfo(SysOrderNew orderNew, String loginName) {
        List<SysOrderNew> sourceMergeOrderList = new ArrayList<>();
        List<SysOrderPackageDetail> mergeList = orderNew.getSysOrderPackageList().get(0).getSysOrderPackageDetailList();//合并后的包裹
        List<String> list = Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"));
        for (String orderId : list) {
            SysOrderNew sysOrderNew = queryOrderAndNoShowPackage(orderId);
            sysOrderNew.setUpdateBy(loginName);
            String orderTrackId = sysOrderNew.getSysOrderPackageList().get(0).getOrderTrackId();
            SysOrderPackage sysOrderPackage = sysOrderPackageMapper.queryOrderPackageByOrderTrackId(orderTrackId);
            sysOrderPackage.setModifier(loginName);
            BigDecimal orderPackageFee = new BigDecimal("0");
            List<SysOrderPackageDetail> sysOrderPackageDetails = sysOrderPackageDetailMapper.queryOrderPackageDetails(orderTrackId);
            for (SysOrderPackageDetail item : sysOrderPackageDetails) {
                item.setModifier(loginName);
                for (SysOrderPackageDetail mergeItem : mergeList) {
                    if (mergeItem.getSku().equals(item.getSku())) {
                        item.setSupplierShipFee(mergeItem.getSupplierShipFee());
                        item.setSellerShipFee(mergeItem.getSellerShipFee());
                        orderPackageFee = orderPackageFee.add(mergeItem.getSellerShipFee().multiply(BigDecimal.valueOf(item.getSkuQuantity())));
                    }
                }
            }
            sysOrderPackage.setEstimateShipCost(orderPackageFee);
            sysOrderNew.setEstimateShipCost(orderPackageFee);
            resetData(sysOrderNew);//重新设置订单数据  总售价  利润 利润率
            sourceMergeOrderList.add(sysOrderNew);
        }
        return sourceMergeOrderList;
    }


    public void setSysOrderInvoiceInfo(SysOrderNew sysOrder) {
        if (sysOrder.getSysOrderInvoiceInsertOrUpdateDTO() != null) {
            String loginName = getLoginUserInformationByToken.getUserDTO().getLoginName();
            if ((sysOrderInvoiceService.getSysOrderInvoiceBySysOrderId(sysOrder.getSysOrderId()) == null)) {
                //新建  +创建人  +更新人
                sysOrder.getSysOrderInvoiceInsertOrUpdateDTO().setCreator(loginName);
                sysOrder.getSysOrderInvoiceInsertOrUpdateDTO().setModifier(loginName);
                sysOrderInvoiceService.insertOrUpdateSysOrderInvoice(sysOrder.getSysOrderInvoiceInsertOrUpdateDTO());
            } else {
                //编辑   只+更新人
                sysOrder.getSysOrderInvoiceInsertOrUpdateDTO().setModifier(loginName);
                sysOrderInvoiceService.insertOrUpdateSysOrderInvoice(sysOrder.getSysOrderInvoiceInsertOrUpdateDTO());
            }
        }
    }

    public void resetData(SysOrderNew orderNew) {
        Boolean b = this.logisticsFull(orderNew.getSysOrderPackageList());
        if (b) {
            orderNew.setTotal(orderNew.getOrderAmount().add(orderNew.getEstimateShipCost())); //设置总售价
            if (orderNew.getIsConvertOrder().equalsIgnoreCase(Constants.isConvertOrder.YES)) {
                systemOrderCommonService.setGrossMarginAndProfitMarginAndTotal(orderNew);
//                SysOrder sysOrder = new SysOrder() {{
//                    setCommoditiesAmount(orderNew.getCommoditiesAmount());
//                    setShippingServiceCost(orderNew.getShippingServiceCost());
//                    setOrderSource(orderNew.getOrderSource());
//                    setOrderAmount(orderNew.getOrderAmount());
//                    setEstimateShipCost(orderNew.getEstimateShipCost());
//                    setInterest(orderNew.getInterest());
//                }};
//                systemOrderCommonService.setGrossMarginAndProfitMarginAndTotal(sysOrder);
//                orderNew.setGrossMargin(sysOrder.getGrossMargin());
//                orderNew.setProfitMargin(sysOrder.getProfitMargin());
            }
        } else {
            orderNew.setGrossMargin(null);
            orderNew.setProfitMargin(null);
            orderNew.setOrderAmount(null);
            orderNew.setEstimateShipCost(null);
            orderNew.setOrderAmount(null);
            orderNew.setTotal(null);
        }
    }

    /**
     * description: 设置供应链公司信息
     *
     * @return create by wujiachuang
     * @Param: sysOrder  系统订单对象
     * @Param: sysOrderDetail 系统订单项对象
     * @Param: isSeller true卖家  false供应商
     */
    public void setSupplyChainInfo(SysOrder sysOrder, SysOrderDetail sysOrderDetail, Boolean isSeller) {
        if (sysOrder != null) {
            if (isSeller) {  //卖家查询供应链公司信息
                List<Integer> list = new ArrayList<>();
                list.add(sysOrder.getSellerPlId());
                String result = remoteUserService.getSupplyChainByUserId("1", list);
                String string = com.rondaful.cloud.common.utils.Utils.returnRemoteResultDataString(result, null);
                if (StringUtils.isBlank(string)) {
                    logger.error("新建订单调用用户服务查询供应链公司信息异常！");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
                }
                List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(string, SupplyChainCompany.class);
                String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
                String supplyId = supplyChainCompanies.get(0).getSupplyId();
                if (StringUtils.isBlank(supplyChainCompanyName)) {
                    logger.error("订单新建异常，供应链公司名称为空");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
                }
                if (StringUtils.isBlank(supplyId)) {
                    logger.error("订单新建异常，供应链公司ID为空");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
                }
                sysOrder.setSupplyChainCompanyId(Integer.valueOf(supplyId));  //供应链公司ID
                sysOrder.setSupplyChainCompanyName(supplyChainCompanyName);   //供应链公司名称
            } else {   //供应商查询供应链公司信息
                List<Integer> list = new ArrayList<>();
                list.add(sysOrder.getSellerPlId());
                String result = remoteUserService.getSupplyChainByUserId("0", list);
                String string = com.rondaful.cloud.common.utils.Utils.returnRemoteResultDataString(result, null);
                if (StringUtils.isBlank(string)) {
                    logger.error("新建订单调用用户服务查询供应链公司信息异常！");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
                }
                List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(string, SupplyChainCompany.class);
                String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
                String supplyId = supplyChainCompanies.get(0).getSupplyId();
                sysOrder.setSupplyChainCompanyId(Integer.valueOf(supplyId));  //供应链公司ID
                sysOrder.setSupplyChainCompanyName(supplyChainCompanyName);   //供应链公司名称
            }
        } else {   //sysOrder为null,sysOrderDetail!=null
            if (isSeller) {  //卖家查询供应链公司信息
                List<Integer> list = new ArrayList<>();
                list.add(Integer.valueOf(Long.toString(sysOrderDetail.getSupplierId())));
                String result = remoteUserService.getSupplyChainByUserId("1", list);
                String string = com.rondaful.cloud.common.utils.Utils.returnRemoteResultDataString(result, null);
                if (StringUtils.isBlank(string)) {
                    logger.error("新建订单调用用户服务查询供应链公司信息异常！");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
                }
                List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(string, SupplyChainCompany.class);
                String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
                String supplyId = supplyChainCompanies.get(0).getSupplyId();
                sysOrderDetail.setSupplyChainCompanyId(Integer.valueOf(supplyId));  //供应链公司ID
                sysOrderDetail.setSupplyChainCompanyName(supplyChainCompanyName);   //供应链公司名称
            } else {   //供应商查询供应链公司信息
                List<Integer> list = new ArrayList<>();
                list.add(Integer.valueOf(Long.toString(sysOrderDetail.getSupplierId())));
                String result = remoteUserService.getSupplyChainByUserId("0", list);
                String string = com.rondaful.cloud.common.utils.Utils.returnRemoteResultDataString(result, "用户服务异常");
                if (StringUtils.isBlank(string)) {
                    logger.error("新建订单调用用户服务查询供应链公司信息异常！");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "用户服务异常");
                }
                List<SupplyChainCompany> supplyChainCompanies = JSONObject.parseArray(string, SupplyChainCompany.class);
                String supplyChainCompanyName = supplyChainCompanies.get(0).getSupplyChainCompanyName();
                String supplyId = supplyChainCompanies.get(0).getSupplyId();
                if (StringUtils.isBlank(supplyChainCompanyName)) {
                    logger.error("订单新建异常，供应链公司名称为空");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
                }
                if (StringUtils.isBlank(supplyId)) {
                    logger.error("订单新建异常，供应链公司ID为空");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "数据异常");
                }
                sysOrderDetail.setSupplyChainCompanyId(Integer.valueOf(supplyId));  //供应链公司ID
                sysOrderDetail.setSupplyChainCompanyName(supplyChainCompanyName);   //供应链公司名称
            }
        }

    }

    /**
     * description: 校验系统订单状态
     *
     * @return create by wujiachuang
     * @Param: sysOrder 系统订单
     */
    private void checkOrderStatus(SysOrderNew sysOrder) {
        Byte orderDeliveryStatus = sysOrder.getOrderDeliveryStatus();
        if (Objects.equals(orderDeliveryStatus, OrderDeliveryStatusNewEnum.WAIT_PAY.getValue())
                || Objects.equals(orderDeliveryStatus, OrderDeliveryStatusNewEnum.INTERCEPTED.getValue())) {
            sysOrder.getSysOrderPackageList().forEach(orderPackage -> {
                if (!Objects.equals(orderPackage.getPackageStatus(), OrderPackageStatusEnum.WAIT_PUSH.getValue())) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
                }
            });
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
    }

    @Override
    /**
     * description: 新增ERP推过来的订单
     * @Param: sysOrder  系统订单对象
     * @return java.lang.String  订单创建结果
     * create by wujiachuang
     */
    public String addErpCreateSysOrder(SysOrder sysOrder) {
     /*   List<String> skuList = new ArrayList<>();
        HashMap<String,Integer> map = new HashMap<>();*/
        //1.校验订单必填项
        String msg = null;
        try {
            msg = CheckOrderUtils.checkErpSysOrder(sysOrder);
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, e.getMessage());
        }
        if (msg == "success") {
            sysOrder.setOrderTrackId(OrderUtils.getPLTrackNumber());//设置订单跟踪号
            String plOrderNumber = OrderUtils.getPLOrderNumber();
            sysOrder.setSysOrderId(plOrderNumber);//2设置品连系统订单ID
            sysOrder.setSellerPlAccount("Rondaful");  //3设置PL账号为 ERP
            sysOrder.setSellerPlId(88888888);    //3设置PL ID 为 12369855
            sysOrder.setOrderSource((byte) 3);//设置来源状态为第三方推送

            BigDecimal orderTotal = new BigDecimal(0);//订单价格：商品数量*商品金额
            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
                sysOrderDetail.setSysOrderId(plOrderNumber);  //4设置品连系统订单ID
                sysOrderDetail.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());//5设置品连系统订单项ID
                 /*   map.put(sysOrderDetail.getSku(), sysOrderDetail.getSkuNum());
                    skuList.add(map.toString());*/
                //调用商品服务设置商品属性，传入供应商SKU
                String result = null;
                try {
                    result = remoteCommodityService.test("1", "1", null, null, null, "", "", "111111", "");
                } catch (Exception e) {
                    logger.error("调用商品服务异常");
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，新增订单失败！");
                }
                JSONObject parse = (JSONObject) JSONObject.parse(result);
                String data = parse.getString("data");
                JSONObject parse1 = (JSONObject) JSONObject.parse(data);
                String pageInfo = parse1.getString("pageInfo");
                JSONObject parse2 = (JSONObject) JSONObject.parse(pageInfo);
                JSONArray list1 = parse2.getJSONArray("list");
                List<CommoditySpec> commodityDetails = null;
                commodityDetails = list1.toJavaList(CommoditySpec.class);
                for (CommoditySpec commodityDetail : commodityDetails) {
                    //体积
                    try {
                        sysOrderDetail.setBulk(commodityDetail.getPackingHeight().multiply(commodityDetail.getPackingWidth()).multiply(commodityDetail.getPackingLength()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //重量
                    sysOrderDetail.setWeight(commodityDetail.getPackingWeight());
                    //商品ID
                    sysOrderDetail.setItemId(commodityDetail.getCommodityId());
                    //品连单个商品成本价
                    sysOrderDetail.setItemCost(commodityDetail.getCommodityPrice());
                    //商品URL
                    sysOrderDetail.setItemUrl(commodityDetail.getMasterPicture());
                    //商品名称
                    sysOrderDetail.setItemName(commodityDetail.getCommodityNameCn());
                    //商品属性
                    sysOrderDetail.setItemAttr(commodityDetail.getCommoditySpec());
                    //商品系统单价
                    sysOrderDetail.setItemPrice(commodityDetail.getCommodityPrice());
                    //订单项SKU
                    sysOrderDetail.setSku(commodityDetail.getSystemSku());
                    //SKU标题
                    sysOrderDetail.setSkuTitle(commodityDetail.getCommodityNameCn());
                    //供应商ID
                    try {
                        String supplierId = commodityDetail.getSupplierId();
                        System.out.println(supplierId);
                        sysOrderDetail.setSupplierId(Long.valueOf(commodityDetail.getSupplierId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //供应商名称
                    sysOrderDetail.setSupplierName(commodityDetail.getSupplierName());
                    //供应商SKU
                    sysOrderDetail.setSupplierSku(commodityDetail.getSupplierSku());
                    //供应商SKU标题
                    sysOrderDetail.setSupplierSkuTitle(commodityDetail.getCommodityNameCn());
                    //供应商sku单价
                    sysOrderDetail.setSupplierSkuPrice(commodityDetail.getCommodityPrice());
                    //累加订单价格
                    orderTotal.add((sysOrderDetail.getSupplierSkuPrice() == null ? new BigDecimal(0) : sysOrderDetail.getSupplierSkuPrice()).multiply(new BigDecimal(sysOrderDetail.getSkuQuantity())));
                }
            }
              /*  //5调用ERP试算接口重新计算物流费设置进系统订单表
                String estimateShipCost="" ;
                try {
                   *//* estimateShipCost =erpUtils.erpTrialByWeight(sysOrder.getDeliveryWarehouseCode(), sysOrder.getShipToCountry(), new ArrayList<String>() {{
                        add(sysOrder.getShippingCarrierUsedCode());}}, sysOrder.getTotalWeight().intValue());*//*
                    //ERP试算接口（重量方式）
                    estimateShipCost =   erpUtils.erpTrialByWeight("2_ZSW", "GB", new ArrayList<String>() {{
                        add("27_EUDDP-PPAR");}}, 550);  //测试用
                } catch (Exception e) {
                    logger.error("远程调用ERP试算接口异常！");
                    System.out.println(e.getMessage());
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,"系统远程调用异常，新增订单失败！");
                }
                JSONObject data1 = (JSONObject) JSONObject.parseObject(estimateShipCost).getJSONArray("data").get(0);
                sysOrder.setEstimateShipCost(data1.getBigDecimal("cny_amount"));//设置预估物流费
            */



             /*   //邮寄方式集合
                List<String> getShippingCarrierUsedCodes = new ArrayList<>();
                getShippingCarrierUsedCodes.add(sysOrder.getShippingCarrierUsedCode());
                //5调用ERP试算接口重新计算物流费设置进系统订单表
                String estimateShipCost = erpUtils.erpTrialBySKUS(sysOrder.getDeliveryWarehouseCode(), sysOrder.getShipToCountry(), getShippingCarrierUsedCodes, skuList);
                JSONObject jsonObject = JSONObject.parseObject(estimateShipCost);
                JSONArray array = jsonObject.getJSONArray("data");
                for (int u = 0; u < array.size(); u++) {
                    JSONObject jsonObject1 = array.getJSONObject(u);
                    String amount = (String) jsonObject1.get("amount");
                    sysOrder.setEstimateShipCost(new BigDecimal(amount));  //设置预估物流费
                }*/

            //设置订单总售价:商品金额+预估物流费',
//                sysOrder.setTotal(orderTotal.add(sysOrder.getEstimateShipCost()));---------------------
            //系统订单总价暂时不设置?????
            //'订单货款=商品单价(供应商填的成本价)X数量'??
            sysOrder.setOrderAmount(orderTotal);
            sysOrderMapper.insertSelective(sysOrder);   //6插入系统订单
            for (SysOrderDetail sysOrderDetail : sysOrder.getSysOrderDetails()) {
                try {

                    sysOrderDetailMapper.insertSelective(sysOrderDetail);   //7插入系统订单项
                } catch (Exception e) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, e.getMessage());
                }
            }
            return "新增系统订单成功！订单号为：" + plOrderNumber;
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，新增订单失败!");
        }
    }

    /**
     * description: 作废订单：更改系统订单状态为已作废,如果该订单已支付还需要取消冻结金额并清空支付方式
     *
     * @return create by wujiachuang
     * @Param: 订单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @TxTransaction(isStart = true)
    public String deleteInvalidOrders(String sysOrderId) {
        SysOrderNew orderNew = queryOrder(sysOrderId);
        Byte orderStatus = orderNew.getOrderDeliveryStatus();
        //订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6部分发货,7已作废,8已完成'   （暂时没有缺货这个状态）
        if (orderStatus == OrderDeliveryStatusNewEnum.WAIT_PAY.getValue() || orderStatus == OrderDeliveryStatusNewEnum.INTERCEPTED.getValue()) {    //作废： 待发货、已拦截
            judgeOrderStatusIsHandleAndLock(orderNew);//判断包裹状态是否符合要求->上锁
            if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {  //合包订单
                updateMergePackageOrderStatus(orderNew);
            } else {   //普通订单或者拆包订单
                updateOrderStatus(sysOrderId, orderNew);
            }
            redissLockUtil.unlock(sysOrderId);
            return "作废成功！";
        } else {
            return throwException(orderStatus);
        }
    }

    public String throwException(Byte orderStatus) {
        if (orderStatus == OrderDeliveryStatusNewEnum.CANCELLED.getValue()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单作废失败，该订单已经是已作废状态!");
        } else if (orderStatus == OrderDeliveryStatusNewEnum.DELIVERED.getValue()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单作废失败，该订单已经发货!");
        } else if (orderStatus == OrderDeliveryStatusNewEnum.COMPLETED.getValue()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单作废失败，该订单已完成!");
        } else if (orderStatus == OrderDeliveryStatusNewEnum.PARTIALLYSHIPPED.getValue()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单作废失败，该订单状态为部分发货!");
        } else if (orderStatus == OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单作废失败，该订单已经开始配货!");
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
    }

    public void updateOrderStatus(String sysOrderId, SysOrderNew orderNew) {
        sysOrderNewMapper.updateOrdersStatus(sysOrderId, OrderDeliveryStatusNewEnum.CANCELLED.getValue());
        sysOrderPackageMapper.updatePackageStatus(sysOrderId, OrderPackageStatusEnum.WAIT_PUSH.getValue());
        sendMsgAndAddLog(orderNew, orderNew.getSysOrderId());
    }

    public void updateMergePackageOrderStatus(SysOrderNew orderNew) {
        List<String> list = Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"));
        for (String id : list) { //TODO 更改合并前的订单状态和包裹状态
            sysOrderNewMapper.updateOrdersStatus(id, OrderDeliveryStatusNewEnum.CANCELLED.getValue());
            sysOrderPackageMapper.updatePackageStatus(id, OrderPackageStatusEnum.WAIT_PUSH.getValue());
        }
        String orderTrackId = orderNew.getSysOrderPackageList().get(0).getOrderTrackId();//TODO 更改合并后的订单包裹状态
        sysOrderPackageMapper.updatePackageStatusByOrderTrackId(orderTrackId, OrderPackageStatusEnum.WAIT_PUSH.getValue());
        for (String orderId : list) {
            sendMsgAndAddLog(orderNew, orderId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @TxTransaction(isStart = true)
    public void sendMsgAndAddLog(SysOrderNew orderNew, String orderId) {
        try {
            orderMessageSender.sendOrderStockOut(orderNew.getSellerPlAccount(), orderId,
                    MessageEnum.ORDER_INVALID_NOTICE, null);
        } catch (JSONException e) {
            logger.error("异常：发送订单作废消息错误，订单id为：" + orderId, e.toString());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
        UserDTO userDTO = getLoginUserInformationByToken.getUserDTO();
        if (userDTO != null) {
            String childPLAccount = userDTO.getLoginName();
            if (!childPLAccount.equalsIgnoreCase(orderNew.getSellerPlAccount())) {
                try {
                    orderMessageSender.sendOrderStockOut(childPLAccount, orderId,
                            MessageEnum.ORDER_INVALID_NOTICE, null);
                } catch (JSONException e) {
                    logger.error("异常：发送订单作废消息错误，订单id为：" + orderId, e.toString());
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
                }
            }
        }
        sysOrderLogService.insertSelective(new SysOrderLog(orderId, OrderHandleLogEnum.Content.CANCEL.cancel(orderId),
                OrderHandleLogEnum.OrderStatus.STATUS_7.getMsg(), userUtils.getUser().getUsername()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @TxTransaction(isStart = true)
    public void sendMsgAndAddLogByOther(SysOrderNew orderNew, String orderId) {
        try {
            orderMessageSender.sendOrderStockOut(orderNew.getSellerPlAccount(), orderId,
                    MessageEnum.ORDER_INVALID_NOTICE, null);
        } catch (JSONException e) {
            logger.error("异常：发送订单作废消息错误，订单id为：" + orderId, e.toString());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
        //子账号发消息
        String result = remoteUserService.getsUserByStore(orderNew.getPlatformShopId());
        if (StringUtils.isNotBlank(result)) {
            String data = Utils.returnRemoteResultDataString(result, "用户服务异常");
            List<ProviderUserDTO> list = JSONObject.parseArray(data, ProviderUserDTO.class);
            for (ProviderUserDTO dto : list) {
                String userName = dto.getUserName();
                try {
                    orderMessageSender.sendOrderStockOut(userName, orderId,
                            MessageEnum.ORDER_INVALID_NOTICE, null);
                } catch (JSONException e) {
                    logger.error("异常：发送订单作废消息错误，订单id为：" + orderId, e.toString());
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
                }
            }
        }
        sysOrderLogService.insertSelective(new SysOrderLog(orderId, OrderHandleLogEnum.Content.CANCELINVALID.cancelInvalid(orderId),
                OrderHandleLogEnum.OrderStatus.STATUS_7.getMsg(), orderNew.getSellerPlAccount()));
    }

    @Override
    public void cancelMoney(String orderId) {
        //远程调用财务接口 退钱给卖家
        String cancel = remoteFinanceService.cancel(orderId);
        if (StringUtils.isBlank(cancel)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "财务服务异常");
        }
        JSONObject json = JSONObject.parseObject(cancel);
        // 获取到key为success的值
        String res = json.getString("success");
        String messsage = json.getString("msg");
        if ("true".equals(res)) {
            sysOrderNewMapper.updatePayIdAndPayMethod(orderId);
        } else {
            logger.error("异常：订单号：" + orderId + "调用财务服务成功，返回false,异常:" + messsage);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统调用异常");
        }
    }

    /**
     * 判断包裹状态是否符合要求-上锁
     *
     * @param orderNew
     * @param orderNew
     */
    @Override
    public void judgeOrderStatusIsHandleAndLock(SysOrderNew orderNew) {
        orderNew.getSysOrderPackageList().forEach(orderPackage -> {
            if (!Objects.equals(orderPackage.getPackageStatus(), OrderPackageStatusEnum.WAIT_PUSH.getValue())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹状态有误！");
            }
        });
        if (!redissLockUtil.tryLock(orderNew.getSysOrderId(), 10, 10)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统繁忙，请稍后尝试！");
        }
    }

    public void updateTkAndAddLogAndsendMsg(String sysOrderId, SysOrder order) {
        try {
            sysOrderMapper.updateOrderTrackIdByOrderId(sysOrderId, OrderUtils.getPLTrackNumber());
        } catch (Exception e) {
            logger.error("异常：更改订单跟踪号失败！订单ID为" + sysOrderId, e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
        try {
            orderMessageSender.sendOrderStockOut(order.getSellerPlAccount(), order.getSysOrderId(),
                    MessageEnum.ORDER_INVALID_NOTICE, null);
        } catch (JSONException e) {
            logger.error("异常：发送订单作废消息错误，订单id为：" + order.getSysOrderId(), e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
        try {
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.CANCEL.cancel(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_7.getMsg(), userUtils.getUser().getUsername()));
        } catch (Exception e) {
            logger.error("异常：订单作废添加进操作日志失败，订单id为：" + order.getSysOrderId(), e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常");
        }
    }

    /**
     * 取消冻结
     *
     * @param sysOrderId
     */
    private void CancelFreeze(String sysOrderId) {
        String cancel = null;
        try {
            //远程调用财务接口 退钱给卖家
            cancel = remoteFinanceService.cancel(sysOrderId);
        } catch (Exception e) {
            logger.error("异常：调用财务取消冻结接口失败！订单ID为：" + sysOrderId, e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
        if (StringUtils.isBlank(cancel)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "财务服务异常");
        }
        JSONObject json = JSONObject.parseObject(cancel);
        // 获取到key为success的值
        String res = json.getString("success");
        String messsage = json.getString("msg");
        if ("true".equals(res)) {
            sysOrderMapper.updatePayIdAndPayMethod(sysOrderId);
        } else {
            logger.error("异常：订单号：" + sysOrderId + "调用财务服务异常:" + messsage);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统调用异常，作废失败！");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void InterceptSplitPackageOrderByDelievryUse(SysOrderNew orderNew) {
        logger.error("发货异常调用拦截订单数据：{}", FastJsonUtils.toJsonString(orderNew));
        boolean b = false;
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            if (sysOrderPackage.getPackageStatus().equals(OrderPackageStatusEnum.WAIT_DELIVER.getValue())) {
                b = true;
            }
        }
        if (!b) {
            logger.error("发货异常调用拦截订单包裹状态均无待发货的，直接返回！订单ID：{}", orderNew.getSysOrderId());
            return;
        }
        String sysOrderId = orderNew.getSysOrderId();
        int intereceptSuccessCount = 0;//拦截成功的包裹数
        boolean flag = true;//默认全部拦截成功
        //TODO 更新仓库类型来拦截订单，拦截成功的intereceptSuccessCount++，添加操作日志  拦截失败的设置FLAG为false，并且设置该包裹为拦截失败的布尔值并添加操作日志
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            if (sysOrderPackage.getPackageStatus().equals(OrderPackageStatusEnum.WAIT_DELIVER.getValue())) { //推送成功的，需要拦截
                String deliveryWarehouseId = String.valueOf(sysOrderPackage.getDeliveryWarehouseId());
                String orderTrackId = sysOrderPackage.getOrderTrackId();
                String warehouse = judgeWarehouseByWarehouseId(deliveryWarehouseId);
                if (Objects.equals(warehouse, Constants.Warehouse.GOODCANG)) {
                    String referenceId = sysOrderPackage.getReferenceId();
                    if (cancelOrderByGranaryApi(sysOrderId, deliveryWarehouseId, referenceId, orderTrackId)) {  //TODO  谷仓的包裹拦截成功
                        intereceptSuccessCount++;
                        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT.packageIntercept(orderTrackId),
                                OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                    } else {                   //TODO  谷仓的包裹拦截失败
                        flag = false;
                        sysOrderPackage.setInterceptSuccessful(false);
                        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT_FAILURE.packageInterceptFailure(orderTrackId),
                                OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                    }
                } else if (Objects.equals(warehouse, Constants.Warehouse.WMS)) {
                    // TODO WMS的包裹
                    if (cancelOrderByWms(String.valueOf(sysOrderPackage.getDeliveryWarehouseId()), sysOrderId, orderTrackId)) {
                        intereceptSuccessCount++;
                        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT.packageIntercept(orderTrackId),
                                OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                    } else {
                        flag = false;
                        sysOrderPackage.setInterceptSuccessful(false);
                        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT_FAILURE.packageInterceptFailure(orderTrackId),
                                OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                    }
                } else if (Objects.equals(warehouse, Constants.Warehouse.RONDAFUL)) {
                    // TODO ERP的包裹
                    if (cancelOrderByErp(sysOrderId, orderTrackId)) {    //TODO erp的包裹拦截成功
                        intereceptSuccessCount++;
                        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT.packageIntercept(orderTrackId),
                                OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                    } else {           //TODO erp的包裹拦截失败
                        flag = false;
                        sysOrderPackage.setInterceptSuccessful(false);
                        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT_FAILURE.packageInterceptFailure(orderTrackId),
                                OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                    }
                } else {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "未知的仓库名");
                }
            }
        }
        if (flag) {  //需要拦截的包裹全部拦截成功   订单状态更改为已拦截
            sysOrderService.updatePackageOrderIdOrSetExceptionInfo2(orderNew);// 根据待发货的包裹拦截状态选择设置拦截失败异常信息或者更新包裹号和包裹详情号
            sysOrderService.updateOrderStatusAndEmptyTrackInfoAndSendMsg(orderNew, sysOrderId);//更改订单为拦截状态，清空所有包裹的异常信息并且设置为待推送状态，发送拦截消息给卖家
            if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                sysOrderService.updateOperateTrackId(orderNew);//对拆分后的包裹号进行#符号拼接设置到拆分前包裹的操作包裹号
            }
        } else {  //部分或者全部拦截失败
            sysOrderService.updatePackageOrderIdOrSetExceptionInfo(orderNew);// 根据待发货的包裹拦截状态选择设置拦截失败异常信息或者更新包裹号和包裹详情号,部分拦截成功的清空异常信息
            sysOrderNewMapper.updateToExceptionOrder(sysOrderId); //  TODO 标记为异常订单
            if (intereceptSuccessCount == 0) {   //全部拦截失败
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT_FAILURE.orderInterceptFailure(sysOrderId), OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), userUtils.getUser().getUsername()));
            } else {   //部分拦截成功
                sysOrderNewMapper.updateOrdersStatus(sysOrderId, OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());//更改订单状态为已拦截
                if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                    sysOrderService.updateOperateTrackId(orderNew);//对拆分后的包裹号进行#符号拼接设置到拆分前包裹的操作包裹号
                }
                sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT_PARTLY_FAILURE.orderInterceptPartlyFailure(sysOrderId), OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), userUtils.getUser().getUsername()));
                try {
                    orderMessageSender.sendOrderStockOut(orderNew.getSellerPlAccount(), orderNew.getSysOrderId(), MessageEnum.ORDER_INTERCEPT_NOTICE, null);
                } catch (JSONException e) {
                    logger.error("异常：发送订单拦截消息错误，订单id为：" + orderNew.getSysOrderId(), e.toString());
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
                }
            }
        }
    }


    /**
     * 根据待发货的包裹拦截状态选择设置拦截失败异常信息或者更新包裹号和包裹详情号
     *
     * @param orderNew
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePackageOrderIdOrSetExceptionInfo(SysOrderNew orderNew) {
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            if (sysOrderPackage.getPackageStatus().equals(OrderPackageStatusEnum.WAIT_DELIVER.getValue())) {
                if (!sysOrderPackage.isInterceptSuccessful()) {     // TODO 设置拦截失败的包裹发货异常信息为：拦截失败
                    sysOrderPackageMapper.updateException(sysOrderPackage.getOrderTrackId());
                } else {
                    String orderTrackId = sysOrderPackage.getOrderTrackId();  // TODO 拦截成功的包裹更新包裹号和包裹详情号
                    String plTrackNumber = OrderUtils.getPLTrackNumber();
                    sysOrderPackageMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                    sysOrderPackageDetailMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                    //根据订单ID更改包裹状态为待推送并清空包裹发货异常信息、跟踪单号、物流商单号
//                    sysOrderPackageMapper.updatePackageInfoByOrderTrackId(plTrackNumber);
                }
            }
        }
    }

    /**
     * 根据待发货的包裹拦截状态选择设置拦截失败异常信息或者更新包裹号和包裹详情号
     *
     * @param orderNew
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePackageOrderIdOrSetExceptionInfo2(SysOrderNew orderNew) {
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            if (sysOrderPackage.getPackageStatus().equals(OrderPackageStatusEnum.WAIT_DELIVER.getValue())) {
                String orderTrackId = sysOrderPackage.getOrderTrackId();  // TODO 拦截成功的包裹更新包裹号和包裹详情号
                String plTrackNumber = OrderUtils.getPLTrackNumber();
                sysOrderPackageMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                sysOrderPackageDetailMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
            }
        }
    }

    /**
     * 更改订单为拦截状态，清空所有包裹的异常信息并且设置为待推送状态，发送拦截消息给卖家
     *
     * @param orderNew
     * @param sysOrderId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatusAndEmptyTrackInfoAndSendMsg(SysOrderNew orderNew, String sysOrderId) {
        sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT.orderIntercept(sysOrderId),
                OrderHandleLogEnum.OrderStatus.STATUS_4.getMsg(), userUtils.getUser().getUsername()));

        try {
            sysOrderNewMapper.updateOrdersStatus(sysOrderId, OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());//更改订单状态为已拦截
        } catch (Exception e) {
            logger.error("异常：更改系统订单状态为已拦截失败！订单ID为" + sysOrderId, e.toString());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
//        try {
//            sysOrderPackageMapper.updatePackageInfoByIntercept(sysOrderId);
//        } catch (Exception e) {
//            logger.error("异常：根据订单ID更改包裹状态并清空包裹发货异常信息、跟踪单号、物流商单号失败！订单ID为" + sysOrderId, e.toString());
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
//        }
        try {
            orderMessageSender.sendOrderStockOut(orderNew.getSellerPlAccount(), orderNew.getSysOrderId(),
                    MessageEnum.ORDER_INTERCEPT_NOTICE, null);
        } catch (JSONException e) {
            logger.error("异常：发送订单拦截消息错误，订单id为：" + orderNew.getSysOrderId(), e.toString());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
    }

    /**
     * 根据仓库ID 判断仓库
     *
     * @param warehouseId 仓库ID
     * @return WMS RONDAFUL GOODCANG
     */
    @Override
    public String judgeWarehouseByWarehouseId(String warehouseId) {
        WarehouseDTO warehouseDTO = systemOrderCommonService.getWarehouseInfo(warehouseId);
        if (warehouseDTO == null || StringUtils.isBlank(warehouseDTO.getFirmCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "根据仓库ID找不到对应的仓库");
        } else {
            return warehouseDTO.getFirmCode();
        }
    }

    @Override
    /**
     * description: 拦截订单：先查询系统订单状态是不是配货中状态，如果是可以拦截，不是的话提示尚未配货或已经发货,拦截失败
     * @Param: sysOrderId  订单ID
     * @return java.lang.String   拦截的结果
     * create by wujiachuang
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String interceptSystemOrder(String sysOrderId) {
        try {
            if (!redissLockUtil.tryLock(sysOrderId, 10, 5)) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "请求频繁，请稍后尝试！");
            }
            //TODO 订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6部分发货,7已作废,8已完成
            SysOrderNew orderNew = queryOrder(sysOrderId);
            Byte orderStatus = orderNew.getOrderDeliveryStatus();
            if (orderStatus == OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue()) {
                judgePackageStatusAndLock(orderNew);//判断包裹状态是否符合要求->上锁
                if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue()) ||
                        orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
                    interceptOrderByWarehouseType(sysOrderId, orderNew); //TODO 拦截单包裹订单：根据仓库类型来拦截订单-取消冻结-修改订单状态
                } else if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                    interceptMutiPackageOrder(sysOrderId, orderNew);//TODO 拦截多包裹订单： 根据仓库类型来拦截订单-取消冻结-修改订单状态
                } else {
                    logger.error("错误的订单类型,订单ID：{}", sysOrderId);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "错误的订单类型，拦截失败！");
                }
            } else {
                throwOtherException(orderStatus);
            }
        } catch (GlobalException e) {
            if (e.toString().contains("包裹拦截成功，订单取消冻结失败")) {
                return Constants.InterceptResponse.RESPONSE_1;
            } else {
                throw e;
            }
        }
        return Constants.Intercept.INTERCEPT_SUCCESS;
    }

    public void interceptMutiPackageOrder(String sysOrderId, SysOrderNew orderNew) {
        boolean flag = true; // 订单包裹拦截标记，默认全部拦截成功
        flag = dealSplitPackage(sysOrderId, orderNew, flag);
        if (flag) {
            String msg = sysOrderService.returnInterceptSuccessful(sysOrderId, orderNew);// 订单全部包裹拦截成功
            cancelMoneyAndUpdateOrderStatusAfterIntercept(sysOrderId, orderNew, msg);
        } else {  //全部拦截失败或者部分拦截失败
            String msg = sysOrderService.returnSplitPackageInterceptFailure(sysOrderId, orderNew);  // 订单包裹部分拦截失败或全部拦截失败
            if (msg.contains(Constants.Intercept.INTERCEPT_PART_FAIL)) {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300138);
            } else {
                throw new GlobalException(OrderCodeEnum.RETURN_CODE_300135);
            }
        }
    }

    public void interceptOrderByWarehouseType(String sysOrderId, SysOrderNew orderNew) {
        String deliveryWarehouseId = String.valueOf(orderNew.getSysOrderPackageList().get(0).getDeliveryWarehouseId());
        String orderTrackId = orderNew.getSysOrderPackageList().get(0).getOrderTrackId();
        String warehouseId = String.valueOf(orderNew.getSysOrderPackageList().get(0).getDeliveryWarehouseId());
        String warehouse = judgeWarehouseByWarehouseId(warehouseId);
        if (Objects.equals(warehouse, Constants.Warehouse.GOODCANG)) {
            String msg = cancelGoodCangOrder(sysOrderId, orderNew, deliveryWarehouseId, orderTrackId);//拦截谷仓订单，失败抛异常
            cancelMoneyAndUpdateOrderStatusAfterIntercept(sysOrderId, orderNew, msg);
        } else if (Objects.equals(warehouse, Constants.Warehouse.WMS)) {
            String msg = cancelWmsOrder(sysOrderId, orderNew, orderTrackId, warehouseId);   //拦截WMS订单，失败抛异常
            cancelMoneyAndUpdateOrderStatusAfterIntercept(sysOrderId, orderNew, msg);
        } else if (Objects.equals(warehouse, Constants.Warehouse.RONDAFUL)) {
            String msg = cancelErpOrder(sysOrderId, orderNew, orderTrackId);//拦截ERP订单，失败抛异常
            cancelMoneyAndUpdateOrderStatusAfterIntercept(sysOrderId, orderNew, msg);
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "未知的仓库名");
        }
    }

    public void throwOtherException(Byte orderStatus) {
        if (orderStatus == OrderDeliveryStatusNewEnum.INTERCEPTED.getValue()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单是被拦截状态，无需再次拦截！");
        } else {
            if (orderStatus < OrderDeliveryStatusNewEnum.WAIT_SHIP.getValue()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单尚未配货，拦截失败！");
            } else if (orderStatus == OrderDeliveryStatusNewEnum.CANCELLED.getValue()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单已作废，拦截失败！");
            } else if (orderStatus == OrderDeliveryStatusNewEnum.COMPLETED.getValue()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单已完成，拦截失败！");
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单已经发货，拦截失败！");
            }
        }
    }


    public void cancelMoneyAndUpdateOrderStatusAfterIntercept(String sysOrderId, SysOrderNew orderNew, String msg) {
        cancelMoneyAfterIntercept(sysOrderId, orderNew, msg);//取消冻结，取消失败改为异常订单
        updateOrderInterceptStatus(orderNew);//拦截成功且取消冻结成功则更改为已拦截状态  over
    }

    public void updateErrorOrderInfo(SysOrderNew orderNew) {
//        SysOrderNew orderNew = sysOrderService.queryOrderByOther(orderId);
        if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue()) |
                orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {
            sysOrderNewMapper.updateToExceptionOrder(orderNew.getSysOrderId()); //变成异常订单
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

    public void cancelMoneyAfterIntercept(String sysOrderId, SysOrderNew orderNew, String msg) {
        if (msg.contains(Constants.Intercept.INTERCEPT_SUCCESS)) {
            if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {
                for (String orderId : Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"))) {
                    cancelMoney(orderNew, orderId);
                }
            } else {
                cancelMoney(orderNew, sysOrderId);
            }
        }
    }

    public void cancelMoney(SysOrderNew orderNew, String orderId) {
        try {
            sysOrderService.cancelMoney(orderId);
        } catch (Exception e) {
            logger.error("包裹拦截成功，订单取消冻结失败！订单ID：{}，异常原因：{}", orderNew.getSysOrderId(), e);
            updateErrorOrderInfo(orderNew);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "包裹拦截成功，订单取消冻结失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInterceptStatus(SysOrderNew orderNew) {
        if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {  //TODO 合包订单
            List<String> list = Arrays.asList(orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId().split("\\#"));
            String orderTrackId = orderNew.getSysOrderPackageList().get(0).getOrderTrackId();
            sysOrderPackageMapper.updatePackageStatusByOrderTrackId(orderTrackId, OrderPackageStatusEnum.WAIT_PUSH.getValue());   //更改合并后的订单包裹状态
            for (String orderId : list) { //更改合并前的订单状态和包裹状态
                sysOrderNewMapper.updateOrdersStatus(orderId, OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());
                sysOrderPackageMapper.updatePackageStatus(orderId, OrderPackageStatusEnum.WAIT_PUSH.getValue());
                sendMsgAndAddLogByOther(orderNew, orderId);
            }
        } else if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())
                || orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {   //TODO 普通订单或者拆包订单
            sysOrderNewMapper.updateOrdersStatus(orderNew.getSysOrderId(), OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());
            sysOrderPackageMapper.updatePackageStatus(orderNew.getSysOrderId(), OrderPackageStatusEnum.WAIT_PUSH.getValue());
            sendMsgAndAddLogByOther(orderNew, orderNew.getSysOrderId());
        } else {
            throw new GlobalException(OrderCodeEnum.RETURN_CODE_300130);
        }
    }

    public void judgePackageStatusAndLock(SysOrderNew orderNew) {
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            if (!sysOrderPackage.getPackageStatus().equals(OrderPackageStatusEnum.WAIT_DELIVER.getValue()) && !sysOrderPackage.getPackageStatus().equals(OrderPackageStatusEnum.PUSH_FAIL.getValue())) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹状态非推送失败或待发货，拦截失败！");
            }
        }
    }

    @Override
    public String cancelGoodCangOrder(String sysOrderId, SysOrderNew orderNew, String deliveryWarehouseId, String orderTrackId) {
        boolean b = cancelOrderByGranaryApi(sysOrderId, deliveryWarehouseId, orderNew.getSysOrderPackageList().get(0).getReferenceId(), orderTrackId);
        if (b) {  // 谷仓的包裹拦截成功
            return sysOrderService.returnInterceptSuccessful(sysOrderId, orderNew);
        } else { // 谷仓的包裹拦截失败
            return sysOrderService.returnInterceptFailure(sysOrderId, orderTrackId, orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId());
        }
    }

    @Override
    public String cancelErpOrder(String sysOrderId, SysOrderNew orderNew, String orderTrackId) {
        if (cancelOrderByErp(sysOrderId, orderTrackId)) {       // erp的包裹拦截成功
            return sysOrderService.returnInterceptSuccessful(sysOrderId, orderNew);
        } else {      //erp的包裹拦截失败
            //添加包裹异常信息，订单更新为异常订单
            return sysOrderService.returnInterceptFailure(sysOrderId, orderTrackId, orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId());
        }
    }

    @Override
    public String cancelWmsOrder(String sysOrderId, SysOrderNew orderNew, String orderTrackId, String warehouseId) {
        boolean flag = cancelOrderByWms(warehouseId, sysOrderId, orderTrackId);
        if (flag) { //Wms的包裹拦截成功
            return sysOrderService.returnInterceptSuccessful(sysOrderId, orderNew);
        } else {//wms的包裹拦截失败
            return sysOrderService.returnInterceptFailure(sysOrderId, orderTrackId, orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId());
        }
    }

    public boolean cancelOrderByWms(String warehouseId, String sysOrderId, String orderTrackId) {
        String result = wmsUtils.wmsCannelOrder(warehouseId, orderTrackId);
        logger.info("调用wms取消订单接口:请求参数为：{},返回的数据结构：{}", orderTrackId, FastJsonUtils.toJsonString(result));
        if (StringUtils.isBlank(result)) {
            logger.error("调用WMS取消订单接口返回空，订单ID：{},包裹ID:{}", sysOrderId, orderTrackId);
            return false;
        } else {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String success = jsonObject.getString("success");
            if ("true".equals(success)) {    //wms取消订单成功
                logger.error("调用WMS取消订单接口，取消成功！订单号:{}，包裹号：{}", sysOrderId, orderTrackId);
                sysOrderPackageMapper.updatePackageInfoByOrderTrackId(orderTrackId);//根据包裹ID更改包裹状态并清空包裹发货异常信息、跟踪单号、物流商单号
                reduceInventory(warehouseId, orderTrackId);//推送SKU和数量到仓库减少本地库存数
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String returnSplitPackageInterceptFailure(String sysOrderId, SysOrderNew orderNew) {
        int count = 0;//TODO 记录包裹拦截失败
        sysOrderNewMapper.updateToExceptionOrder(sysOrderId); //  TODO 标记为异常订单
        List<SysOrderPackage> sysOrderPackageList = orderNew.getSysOrderPackageList();
        for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {
            if (!sysOrderPackage.isInterceptSuccessful()) {     // TODO 设置拦截失败的包裹发货异常信息为：拦截失败
                sysOrderPackageMapper.updateException(sysOrderPackage.getOrderTrackId());
                count++;
            } else {
                String orderTrackId = sysOrderPackage.getOrderTrackId();  // TODO 拦截成功的包裹更新包裹号和包裹详情号
                String plTrackNumber = OrderUtils.getPLTrackNumber();
                sysOrderPackageMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                sysOrderPackageDetailMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                //根据订单ID更改包裹状态为待推送并清空包裹发货异常信息、跟踪单号、物流商单号
//                sysOrderPackageMapper.updatePackageInfoByOrderTrackId(plTrackNumber);
            }
        }
        if (Objects.equals(count, sysOrderPackageList.size())) { //全部拦截失败
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT_FAILURE.orderInterceptFailure(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), userUtils.getUser().getUsername()));
            redissLockUtil.unlock(sysOrderId);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "全部拦截失败！");
        } else {  //部分拦截失败  TODO 没有发送订单拦截消息，同样更改为已拦截状态
            try {
                sysOrderNewMapper.updateOrdersStatus(sysOrderId, OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());//更改订单状态为已拦截
            } catch (Exception e) {
                logger.error("异常：更改系统订单状态为已拦截失败！订单ID为" + sysOrderId, e.toString());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
            }
            if (orderNew.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {
                sysOrderService.updateOperateTrackId(orderNew);//对拆分后的包裹号进行#符号拼接设置到拆分前包裹的操作包裹号
            }
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT_PARTLY_FAILURE.orderInterceptPartlyFailure(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), userUtils.getUser().getUsername()));
            try {
                orderMessageSender.sendOrderStockOut(orderNew.getSellerPlAccount(), orderNew.getSysOrderId(), MessageEnum.ORDER_INTERCEPT_NOTICE, null);
            } catch (JSONException e) {
                logger.error("异常：发送订单拦截消息错误，订单id为：" + orderNew.getSysOrderId(), e.toString());
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
            }
            redissLockUtil.unlock(sysOrderId);
            return "部分包裹拦截失败！";
        }
    }

    /**
     * 对拆分后的包裹号进行#符号拼接设置到拆分前包裹的操作包裹号
     *
     * @param orderNew
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOperateTrackId(SysOrderNew orderNew) {
        String newOrderId = ""; //更改拆分前的操作包裹号，因为拆分后的包裹号已经更新
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            String orderTrackId = sysOrderPackage.getOrderTrackId();
            newOrderId += orderTrackId + "#";
        }
        String operateOrderTrackId = orderNew.getSysOrderPackageList().get(0).getOperateOrderTrackId(); //拆分前的包裹号
        sysOrderPackageMapper.updateOperateTrackId(operateOrderTrackId, newOrderId.substring(0, newOrderId.length() - 1)); //更改被合并的主包裹号
    }

    @Override
    public boolean dealSplitPackage(String sysOrderId, SysOrderNew orderNew, boolean flag) {
        for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
            String deliveryWarehouseId = String.valueOf(sysOrderPackage.getDeliveryWarehouseId());//仓库ID
            String orderTrackId = sysOrderPackage.getOrderTrackId();//包裹跟踪号
            String warehouse = judgeWarehouseByWarehouseId(deliveryWarehouseId);//仓库名：GOODCANG|WMS|RONDAFUL
            if (Objects.equals(warehouse, Constants.Warehouse.GOODCANG)) {
                String referenceId = sysOrderPackage.getReferenceId();
                if (cancelOrderByGranaryApi(sysOrderId, deliveryWarehouseId, referenceId, orderTrackId)) {  //谷仓的包裹拦截成功
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT.packageIntercept(orderTrackId),
                            OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                } else { //谷仓的包裹拦截失败
                    flag = false;
                    sysOrderPackage.setInterceptSuccessful(false);
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT_FAILURE.packageInterceptFailure(orderTrackId),
                            OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                }
            } else if (Objects.equals(warehouse, Constants.Warehouse.WMS)) {
                if (cancelOrderByWms(String.valueOf(sysOrderPackage.getDeliveryWarehouseId()), sysOrderId, orderTrackId)) { //Wms的包裹拦截成功
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT.packageIntercept(orderTrackId),
                            OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                } else {//wms的包裹拦截失败
                    flag = false;
                    sysOrderPackage.setInterceptSuccessful(false);
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT_FAILURE.packageInterceptFailure(orderTrackId),
                            OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                }
            } else if (Objects.equals(warehouse, Constants.Warehouse.RONDAFUL)) {
                if (cancelOrderByErp(sysOrderId, orderTrackId)) {    //erp的包裹拦截成功
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT.packageIntercept(orderTrackId),
                            OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                } else {           //erp的包裹拦截失败
                    flag = false;
                    sysOrderPackage.setInterceptSuccessful(false);
                    sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.PACKAGE_INTERCEPT_FAILURE.packageInterceptFailure(orderTrackId),
                            OrderHandleLogEnum.OrderStatus.STATUS_14.getMsg(), userUtils.getUser().getUsername()));
                }
            } else {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "未知的仓库名");
            }
        }
        return flag;
    }

    @Override
    public String returnInterceptFailure(String sysOrderId, String orderTrackId, String operateSysOrderId) {
        //添加包裹异常信息，订单更新为异常订单
        sysOrderPackageMapper.updateException(orderTrackId);
        if (StringUtils.isNotBlank(operateSysOrderId)) {  //合并订单
            List<String> list = Arrays.asList(operateSysOrderId.split("\\#"));
            for (String orderId : list) {
                sysOrderNewMapper.updateToExceptionOrder(orderId);
                sysOrderLogService.insertSelective(new SysOrderLog(orderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT_FAILURE.orderInterceptFailure(orderId),
                        OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), userUtils.getUser().getUsername()));
            }
        } else {
            sysOrderNewMapper.updateToExceptionOrder(sysOrderId);
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT_FAILURE.orderInterceptFailure(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_3.getMsg(), userUtils.getUser().getUsername()));
        }
        redissLockUtil.unlock(sysOrderId);
        throw new GlobalException(OrderCodeEnum.RETURN_CODE_300135);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String returnInterceptSuccessful(String sysOrderId, SysOrderNew orderNew) {
        String operateSysOrderId = orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId();
        if (StringUtils.isNotBlank(operateSysOrderId)) {  //合并订单
            List<String> list = Arrays.asList(operateSysOrderId.split("\\#"));
            for (String orderId : list) {
                sysOrderLogService.insertSelective(new SysOrderLog(orderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT.orderIntercept(orderId),
                        OrderHandleLogEnum.OrderStatus.STATUS_4.getMsg(), userUtils.getUser().getUsername()));
            }
        } else { //普通或者拆包订单
            sysOrderLogService.insertSelective(new SysOrderLog(sysOrderId, OrderHandleLogEnum.Content.ORDER_INTERCEPT.orderIntercept(sysOrderId),
                    OrderHandleLogEnum.OrderStatus.STATUS_4.getMsg(), userUtils.getUser().getUsername()));
        }
        sysOrderService.setAndSendDataAfterInterceptOrder(sysOrderId, orderNew);
        redissLockUtil.unlock(sysOrderId);
        return Constants.Intercept.INTERCEPT_SUCCESS;
    }


    public boolean cancelOrderByErp(String sysOrderId, String orderTrackId) {
        String result = null;
        try {
            result = erpUtils.erpCannelOrder(orderTrackId);
        } catch (Exception e) {
            logger.error("异常：远程调用ERP取消订单接口失败！订单ID为" + sysOrderId, e);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统远程调用取消订单异常，更改系统订单状态为已拦截失败!");
        }
        if (StringUtils.isBlank(result)) {
            logger.error("调用ERP拦截订单接口返回Null,订单ID：{}，包裹号：{}", sysOrderId, orderTrackId);
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        // 获取到key为message的值
        String msg = jsonObject.getString("status");
        if ("-10".equals(msg)) {
            //取消订单失败  ERP返回错误信息
            String errorMsg = jsonObject.getString("message");
            logger.error("异常：调用ERP取消订单接口返回的错误信息：订单号为：" + sysOrderId + "," + errorMsg);
            return false;
        }
        if ("1".equals(msg)) {    //ERP取消订单成功
            logger.error("调用ERP取消订单接口，取消成功！订单号{}", sysOrderId);
            sysOrderPackageMapper.updatePackageInfoByOrderTrackId(orderTrackId);//根据包裹ID更改包裹状态并清空包裹发货异常信息、跟踪单号、物流商单号
            String deliveryWarehouseId = String.valueOf(sysOrderPackageMapper.queryOrderPackageByPk(orderTrackId).getDeliveryWarehouseId());
            reduceInventory(deliveryWarehouseId, orderTrackId);//推送SKU和数量到仓库减少本地库存数
            return true;
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统调用异常，拦截失败！");
        }
    }

    public boolean cancelOrderByGranaryApi(String sysOrderId, String deliveryWarehouseId, String referenceId, String orderTrackId) {
//        List<SysOrderPackage> sysOrderPackageList = orderNew.getSysOrderPackageList();
//        if (CollectionUtils.isEmpty(sysOrderPackageList)) {
//            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "包裹集合为空，不能拦截！");
//        }
//        String deliveryWarehouseId = String.valueOf(sysOrderPackageList.get(0).getDeliveryWarehouseId());
//        String referenceId = sysOrderPackageList.get(0).getReferenceId();
//        String orderTrackId = sysOrderPackageList.get(0).getOrderTrackId();
        //调用谷仓取消订单api
        String gcJson = goodCangOrderInterceptService.cancelOrder(referenceId, "客户自身原因", deliveryWarehouseId);
        JSONObject jsonObject = JSONObject.parseObject(gcJson);
        Integer cancelOrderStatus = jsonObject.getInteger("cancel_status");
        if ((Integer.valueOf(2)).equals(cancelOrderStatus)) {
            sysOrderPackageMapper.updatePackageInfoByOrderTrackId(orderTrackId);//根据包裹ID更改包裹状态并清空包裹发货异常信息、跟踪单号、物流商单号
            reduceInventory(deliveryWarehouseId, orderTrackId);//推送SKU和数量到仓库减少本地库存数
            return true;
        }
        //谷仓取消订单失败
        if (null == cancelOrderStatus) {
            //当谷仓取消订单失败时,获取Error对象（错误信息对象）,记录日志抛出失败异常
            String error = jsonObject.getString("Error");
            JSONObject errorObject = JSONObject.parseObject(error);
            Integer errCode = errorObject.getInteger("errCode");
            String errMessage = errorObject.getString("errMessage");
            switch (errCode) {
                case 1110100003:
                    logger.error("异常：调用谷仓取消订单接口返回的错误信息：订单号为：" + sysOrderId + "," + errMessage);
//                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单不存在");
                case 10103099:
                    logger.error("异常：调用谷仓取消订单接口返回的错误信息：订单号为：" + sysOrderId + "," + errMessage);
//                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单已废弃，无需再次拦截！");
                case 1110100002:
                    logger.error("异常：调用谷仓取消订单接口返回的错误信息：订单号为：" + sysOrderId + "," + errMessage);
//                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "必填,谷仓关联订单号reference_id不能为null！");
                default:
                    logger.error("异常：调用谷仓取消订单接口其他错误信息");
//                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "其他错误信息");
            }
        }
        return false;
    }

    /**
     * 推送SKU和数量到仓库减少本地库存数
     *
     * @param deliveryWarehouseId
     * @param orderTrackId
     */
    public void reduceInventory(String deliveryWarehouseId, String orderTrackId) {
        for (SysOrderPackageDetail item : sysOrderPackageDetailMapper.queryOrderPackageDetails(orderTrackId)) {
            String sku = item.getSku();
            Integer skuQuantity = item.getSkuQuantity();
            logger.info("推送发货数量到仓库，请求参数：仓库id:{},sku:{},sku数量：{}", deliveryWarehouseId, sku, -skuQuantity);
            String result = remoteSupplierService.updateLocalShipping(Integer.valueOf(deliveryWarehouseId), sku, -skuQuantity);
            logger.info("推送发货数量到仓库，返回：{}", result);
        }
    }

    /**
     * 更改订单数据并且发送消息   TODO 订单更改为已拦截状态   包裹全部更改为待推送并清空发货异常信息，跟踪单号、物流商单号、发送订单拦截消息，更新包裹号下次才能继续推送到仓库
     *
     * @param sysOrderId
     * @param order
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAndSendDataAfterInterceptOrder(String sysOrderId, SysOrderNew order) {
        String operateSysOrderId = order.getSysOrderPackageList().get(0).getOperateSysOrderId();
        if (StringUtils.isNotBlank(operateSysOrderId)) {
            List<String> list = Arrays.asList(operateSysOrderId.split("\\#"));
            for (String orderId : list) {
                sysOrderService.updateOrderStatusAndSendMsg(orderId, order);//更改拦截状态，重置异常信息，发送消息
            }
        } else {
            sysOrderService.updateOrderStatusAndSendMsg(sysOrderId, order);//更改拦截状态，重置异常信息，发送消息
        }
        sysOrderService.updateOrderTrackId(sysOrderId, order);//根据订单类型更改包裹跟踪号，包裹详情号

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @TxTransaction(isStart = true)
    public void updateOrderStatusAndSendMsg(String sysOrderId, SysOrderNew order) {
        sysOrderNewMapper.updateOrdersStatus(sysOrderId, OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());//更改订单状态为已拦截
        sysOrderNewMapper.resetOrderErrorStatus(sysOrderId);//重置订单异常标记
        try {//主账号发消息
            orderMessageSender.sendOrderStockOut(order.getSellerPlAccount(), sysOrderId,
                    MessageEnum.ORDER_INTERCEPT_NOTICE, null);
        } catch (JSONException e) {
            logger.error("异常：发送订单拦截消息错误，订单id为：" + sysOrderId, e.toString());
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
        }
        UserDTO userDTO = getLoginUserInformationByToken.getUserDTO();
        if (userDTO != null) {
            String childPLAccount = userDTO.getLoginName();
            if (!childPLAccount.equalsIgnoreCase(order.getSellerPlAccount())) {
                try {
                    orderMessageSender.sendOrderStockOut(childPLAccount, sysOrderId,
                            MessageEnum.ORDER_INTERCEPT_NOTICE, null);
                } catch (JSONException e) {
                    logger.error("异常：发送订单拦截消息错误，订单id为：" + sysOrderId, e.toString());
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常!");
                }
            }
        }
    }

    /**
     * 全部拦截成功才可以走这里
     *
     * @param sysOrderId
     * @param order
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderTrackId(String sysOrderId, SysOrderNew order) {
        if (order.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue())) {//普通订单
            for (SysOrderPackage sysOrderPackage : order.getSysOrderPackageList()) {
                String orderTrackId = sysOrderPackage.getOrderTrackId();
                String plTrackNumber = OrderUtils.getPLTrackNumber();
                sysOrderPackageMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                sysOrderPackageDetailMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
            }
        } else if (order.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.SPLIT.getValue())) {//拆分订单
            String newOrderId = "";
            for (SysOrderPackage sysOrderPackage : order.getSysOrderPackageList()) {
                String orderTrackId = sysOrderPackage.getOrderTrackId();
                String plTrackNumber = OrderUtils.getPLTrackNumber();
                sysOrderPackageMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                sysOrderPackageDetailMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                newOrderId += plTrackNumber + "#";
            }
            String operateOrderTrackId = order.getSysOrderPackageList().get(0).getOperateOrderTrackId(); //拆分前的包裹号
            sysOrderPackageMapper.updateOperateTrackId(operateOrderTrackId, newOrderId.substring(0, newOrderId.length() - 1)); //更改被合并的主包裹号
        } else if (order.getSplittedOrMerged().equals(OrderPackageHandleEnum.OperateStatusEnum.MERGED.getValue())) {//合并订单
            for (SysOrderPackage sysOrderPackage : order.getSysOrderPackageList()) {
                String orderTrackId = sysOrderPackage.getOrderTrackId();
                String plTrackNumber = OrderUtils.getPLTrackNumber();
                sysOrderPackageMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                sysOrderPackageDetailMapper.updateOrderTrackId(orderTrackId, plTrackNumber);
                for (String trackId : sysOrderPackage.getOperateOrderTrackId().split("\\#")) {
                    sysOrderPackageMapper.updateOperateTrackId(trackId, plTrackNumber); //更改被合并的主包裹号
                }
            }
        } else {
            logger.error("错误的订单类型,订单ID：{}", sysOrderId);
        }
    }

    /*  @Override
     */

    /**
     * description: ERP取消订单   根据订单状态判断去作废或者拦截
     *
     * @return java.lang.String  ERP取消订单的结果
     * create by wujiachuang
     * @Param: sysOrderId  订单ID
     *//*
    public String deleteErpInvalidOrders(String sysOrderId) {
        //订单发货状态:1待发货,2缺货,3配货中,4已拦截,5已发货,6已收货,7已作废'

        Byte i = sysOrderMapper.selectSysOrderStatus(sysOrderId);
        if (i == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        if (i == 5 || i == 6) {  //5已发货,6已收货
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "订单取消失败，该订单已经发货!");
        } else {
            if (i < 3) {  //1待发货,2缺货
                try {
                    if ("作废成功！".equals(deleteInvalidOrders(sysOrderId))) {
                        return "订单取消成功！";
                    } else {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，订单取消失败!");
                    }
                } catch (Exception e) {
                    logger.error("作废订单调用失败！", e);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，订单取消失败!");
                }
            } else if (i == 4) {  //4已拦截
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单为已拦截状态，无需再次拦截!");
            } else if (i == 7) {  // 7已作废
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "该订单为已作废状态，无需再次作废!");
            } else {  //3配货中
                try {
                    if ("拦截成功！".equals(getInterceptSystemOrder(sysOrderId))) {
                        return "订单取消成功！";
                    } else {
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，订单取消失败!");
                    }
                } catch (Exception e) {
                    logger.error("拦截订单调用失败！", e);
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统异常，取消订单失败!");
                }
            }
        }
    }*/
    @Override
    /**
     * description: 设置数据
     * @Param: sysOrderList  系统订单集合
     * @Param: isSeller       true卖家   false品连
     * @return java.util.List<com.rondaful.cloud.order.entity.SystemExport>  导出的数据集合
     * create by wujiachuang
     */
    public List<SystemExport> setData(List<SysOrderNew> orderNewList, boolean isSeller) {
        List<SystemExport> systemExportList = new ArrayList<>();


        if (isSeller) { //卖家系统展示
            orderNewList.forEach(orderNew -> {
                setOrderAttributes(systemExportList, orderNew);
            });
        } else {  // 品连系统展示
            orderNewList.forEach(orderNew -> {
                setOrderAttributes(systemExportList, orderNew);
            });
        }
        return systemExportList;
    }

    /**
     * 设置订单参数
     *
     * @param systemExportList
     * @param orderNew
     */
    private void setOrderAttributes(List<SystemExport> systemExportList, SysOrderNew orderNew) {
        String itemPrice = "";
        AtomicInteger itemCount = new AtomicInteger(0);
        String skus = "";
        String freightUnitPrice = "";
        String wareHouse = "";
        String deliveryMethod = "";
        String orderTrackIds = "";
        String platformSKU = "";
        String trackNumber = "";

        SystemExport systemExport = new SystemExport();
        StringBuilder freightSB = new StringBuilder();
        StringBuilder quantitySB = new StringBuilder();
        StringBuilder skuSB = new StringBuilder();
        StringBuilder purchasePriceSB = new StringBuilder();
        StringBuilder platformSkuSB = new StringBuilder();

        Map<String, String> map = new HashMap<>();
        List<SysOrderPackageDetail> details = new CopyOnWriteArrayList<>();
        StringBuffer buffer = new StringBuffer();
        StringBuffer bufferSku = new StringBuffer();

        List<SysOrderDetail> sysOrderDetails = orderNew.getSysOrderDetails();

        boolean flag = true;

        for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
//            if (sysOrderDetail.getSku() != null && StringUtils.isNotBlank(sysOrderDetail.getSourceSku())) {
//                map.put(sysOrderDetail.getSku(), sysOrderDetail.getSourceSku());
//            }
            if (StringUtils.isNotBlank(sysOrderDetail.getSku())) {

                BigDecimal logisticCompanyShipFee = new BigDecimal(0);
                for (SysOrderPackage sysOrderPackage : orderNew.getSysOrderPackageList()) {
                    for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackage.getSysOrderPackageDetailList()) {
                        if (sysOrderPackageDetail.getSku().equals(sysOrderDetail.getSku()) && sysOrderPackageDetail.getFreeFreight() == 0) {
                            logisticCompanyShipFee = sysOrderPackageDetail.getSellerShipFee();
                            break;
                        }
                    }
                }
                if (flag) {
                    freightSB.append(logisticCompanyShipFee);
                    quantitySB.append(sysOrderDetail.getSkuQuantity());
                    skuSB.append(sysOrderDetail.getSku());
                    purchasePriceSB.append(sysOrderDetail.getItemPrice().setScale(2, BigDecimal.ROUND_DOWN));
                } else {
                    freightSB.append(",").append(logisticCompanyShipFee);
                    quantitySB.append(",").append(sysOrderDetail.getSkuQuantity());
                    skuSB.append(",").append(sysOrderDetail.getSku());
                    purchasePriceSB.append(",").append(sysOrderDetail.getItemPrice().setScale(2, BigDecimal.ROUND_DOWN));
                }
                flag = false;
            }
        }

        systemExport.setFreightUnitPrice(freightSB.toString());
        systemExport.setItemCount(quantitySB.toString());
        systemExport.setPlSkus(skuSB.toString());
        systemExport.setItemPrice(purchasePriceSB.toString());

        boolean flag2 = true;
        List<SysOrderPackage> sysOrderPackageList = orderNew.getSysOrderPackageList();
        for (SysOrderPackage sysOrderPackage : sysOrderPackageList) {
            List<SysOrderPackageDetail> sysOrderPackageDetailList = sysOrderPackage.getSysOrderPackageDetailList();
            for (SysOrderPackageDetail sysOrderPackageDetail : sysOrderPackageDetailList) {
                if (StringUtils.isNotBlank(sysOrderPackageDetail.getSourceSku())) {
                    if (flag2) {
                        platformSkuSB.append(sysOrderPackageDetail.getSourceSku());
                        flag2 = false;
                    } else {
                        platformSkuSB.append(",").append(sysOrderPackageDetail.getSourceSku());
                    }
                }

            }
        }

        systemExport.setPlatformSku(StringUtils.isBlank(platformSkuSB.toString()) ? "--" : platformSkuSB.toString());

        for (SysOrderPackage orderPackage : orderNew.getSysOrderPackageList()) {
            if (orderPackage.getDeliveryWarehouse() != null) {
                wareHouse += orderPackage.getDeliveryWarehouse() + ",";
            }
            if (orderPackage.getDeliveryMethod() != null) {

                deliveryMethod += orderPackage.getDeliveryMethod() + ",";
            }
            if (orderPackage.getOrderTrackId() != null) {

                orderTrackIds += orderPackage.getOrderTrackId() + ",";
            }
            if (orderPackage.getSysOrderPackageDetailList() != null && !orderPackage.getSysOrderPackageDetailList().isEmpty()) {

                details.addAll(orderPackage.getSysOrderPackageDetailList());
            }
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(orderPackage.getShipTrackNumber())) {
                trackNumber += orderPackage.getShipTrackNumber();
                trackNumber += ",";
            } else if (org.apache.commons.lang3.StringUtils.isNotEmpty(orderPackage.getShipOrderId())) {
                trackNumber += orderPackage.getShipOrderId();
                trackNumber += ",";
            }
        }
//        for (SysOrderPackageDetail detail : details) {
//            if (map.containsKey(detail.getSku())) {
//                buffer.append(map.get(detail.getSku()) + ",");
//            }
//            bufferSku.append(detail.getSku() + ",");
//            itemPrice += String.valueOf(detail.getSkuPrice().setScale(2, BigDecimal.ROUND_DOWN)) + ",";
//            itemCount.addAndGet(detail.getSkuQuantity());
//        }
//        skus = bufferSku.toString();
//        platformSKU = buffer.toString();

        //
        systemExport.setName(orderNew.getSysOrderReceiveAddress().getShipToName());
        //
        systemExport.setCountry(orderNew.getSysOrderReceiveAddress().getShipToCountryName());
        //
        systemExport.setProvince(orderNew.getSysOrderReceiveAddress().getShipToState());
        //
        systemExport.setCity(orderNew.getSysOrderReceiveAddress().getShipToCity());
        //
        systemExport.setAddress(orderNew.getSysOrderReceiveAddress().getShipToAddrStreet1());
        //1.订单号
        systemExport.setOrderId(orderNew.getSysOrderId());
        //2.平台订单号
        systemExport.setPlatformOrderId(orderNew.getSourceOrderId());
        //3.卖家
        systemExport.setSeller(orderNew.getSellerPlAccount());
        //4.店铺账号
        systemExport.setSellerId(orderNew.getPlatformSellerAccount());
        //5.包裹号
        if (orderTrackIds.length() - 1 <= 0) {
            systemExport.setPackageId("--");
        } else {
            systemExport.setPackageId(orderTrackIds.substring(0, orderTrackIds.length() - 1));
        }

        //6.平台SKU
//        if (platformSKU.length() - 1 <= 0) {
//            systemExport.setPlatformSku(platformSKU);
//        } else {
//            systemExport.setPlatformSku(platformSKU.substring(0, platformSKU.length() - 1));
//        }
        //7.刊登人
        systemExport.setPlacer("--");
        //8.品连Sku
//        if (skus.length() - 1 <= 0) {
//            systemExport.setPlSkus(skus);
//        } else {
//            systemExport.setPlSkus(skus.substring(0, skus.length() - 1));
//        }
        //9.采购单价（$）
//        if (itemPrice.length() - 1 <= 0) {
//            systemExport.setItemPrice(itemPrice);
//        } else {
//            systemExport.setItemPrice(itemPrice.substring(0, itemPrice.length() - 1));
//        }


        //10.运费单价（$）
//        if (freightUnitPrice.length() - 1 <= 0) {
//            systemExport.setFreightUnitPrice("--");
//        } else {
//            systemExport.setFreightUnitPrice(freightUnitPrice.substring(0, freightUnitPrice.length() - 1));
//        }
//        systemExport.setFreightUnitPrice(String.valueOf(orderNew.getEstimateShipCost().doubleValue()));
        //11.数量
//        systemExport.setItemCount(itemCount.get());
        //12.商品总价（$）
        if (orderNew.getOrderAmount() != null) {

            systemExport.setGoodsTotalPrice("" + orderNew.getOrderAmount().doubleValue());
        }
        //13.总运费（$）
        if (orderNew.getEstimateShipCost() != null) {

            systemExport.setTotalFreight("" + orderNew.getEstimateShipCost());
        }
        //14.订单金额（$）
        if (orderNew.getTotal() != null) {
            systemExport.setPrices(String.valueOf(orderNew.getTotal().setScale(2, BigDecimal.ROUND_DOWN)));
        } else {
            systemExport.setPrices("--");
        }
        //15.平台销售金额
        if (orderNew.getPlatformTotalPrice() == null) {
            systemExport.setPlatformSalesAmount("--");
        } else {
            systemExport.setPlatformSalesAmount("" + orderNew.getPlatformTotalPrice());
        }
        //16.利润（$）
        if (orderNew.getGrossMargin() != null) {
            systemExport.setProfit(String.valueOf(orderNew.getGrossMargin().setScale(2, BigDecimal.ROUND_DOWN)));
        } else {
            systemExport.setProfit("--");
        }
        //17.利润率
        if (orderNew.getProfitMargin() != null) {

            systemExport.setProfitMargin("" + orderNew.getProfitMargin().doubleValue());
        } else {
            systemExport.setProfitMargin("--");
        }
        //18.仓库
        if (wareHouse.length() - 1 <= 0) {
            systemExport.setWareHouse("--");
        } else {
            systemExport.setWareHouse(wareHouse.substring(0, wareHouse.length() - 1));
        }
        //19.邮寄方式
        if (deliveryMethod.length() - 1 <= 0) {
            systemExport.setDeliveryMethod("--");
        } else {
            systemExport.setDeliveryMethod(deliveryMethod.substring(0, deliveryMethod.length() - 1));
        }
        //20.跟踪单号
        if (trackNumber.length() - 1 <= 0) {
            systemExport.setTrackId("--");
        } else {
            systemExport.setTrackId(trackNumber.substring(0, trackNumber.length() - 1));
        }
        //21.订单状态
        if (orderNew.getOrderDeliveryStatus() != null) {
            systemExport.setOrderStatus(SysOrderUtils.getOrderStatusValueMap().get(orderNew.getOrderDeliveryStatus()));
        }
        //22.创建时间
        if (orderNew.getCreatedTime() != null) {
            systemExport.setCreateTime(TimeUtil.DateToString2(orderNew.getCreatedTime()));
        }
        //23.发货时间
        if (orderNew.getDeliveryTime() != null) {
            systemExport.setDeliveryTime(orderNew.getDeliveryTime());
        }

        systemExportList.add(systemExport);
    }

    @Override
    public JSONArray export(List<SystemExport> systemExportList) {
        if (CollectionUtils.isEmpty(systemExportList)) {
            return new JSONArray();
        }
        JSONArray array = new JSONArray();
        for (SystemExport systemExport : systemExportList) {
            JSONObject json = new JSONObject();
            json.put("orderId", systemExport.getOrderId());
            json.put("platformOrderId", systemExport.getPlatformOrderId());
            json.put("seller", systemExport.getSeller());
            json.put("sellerId", systemExport.getSellerId());
            json.put("plSkus", systemExport.getPlSkus());
            json.put("itemPrice", systemExport.getItemPrice());
            json.put("itemCount", systemExport.getItemCount());
            json.put("prices", systemExport.getPrices());
            json.put("name", systemExport.getName());
            json.put("country", systemExport.getCountry());
            json.put("province", systemExport.getProvince());
            json.put("city", systemExport.getCity());
            json.put("address", systemExport.getAddress());
            json.put("wareHouse", Utils.translation(systemExport.getWareHouse()));
            json.put("deliveryMethod", Utils.translation(systemExport.getDeliveryMethod()));
            json.put("trackId", systemExport.getTrackId());
            json.put("profit", systemExport.getProfit());
            json.put("orderStatus", Utils.translation(systemExport.getOrderStatus()));
            json.put("createTime", systemExport.getCreateTime());
            json.put("deliveryTime", systemExport.getDeliveryTime());
            json.put("packageId", systemExport.getPackageId());
            json.put("platformSku", systemExport.getPlatformSku());
            json.put("placer", systemExport.getPlacer());
            json.put("freightUnitPrice", systemExport.getFreightUnitPrice());
            json.put("goodsTotalPrice", systemExport.getGoodsTotalPrice());
            json.put("totalFreight", systemExport.getTotalFreight());
            json.put("platformSalesAmount", systemExport.getPlatformSalesAmount());
            json.put("profitMargin", systemExport.getProfitMargin());

            array.add(json);
        }
        return array;
    }

    @Override
    /**
     * description: 根据卖家品连ID查询订单记录  APP用
     * @Param: sellerPlId   卖家品连ID
     * @return com.rondaful.cloud.order.entity.OrderRecord   封装好的数据
     * create by wujiachuang
     */
    public OrderRecord getOrderRecord(String sellerPlId) {
        //订单数量
        Long count = sysOrderMapper.selectOrderCount(sellerPlId);
        //订单销售额
        Double m = sysOrderMapper.selectOrderSaleroom(sellerPlId);
        if (m == null) {
            m = Double.valueOf(0);
        }
        BigDecimal bigDecimal = new BigDecimal(m);
//        BigDecimal saleroom = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);//四舍五入
        BigDecimal saleroom = bigDecimal;
        //远程调用接口获取EBAY刊登数
        String onlineCount = remoteSellerService.getOnlineCount(sellerPlId);
        Integer ebayCount = 0;
        if (onlineCount != null) {
            Map<String, Object> parse1 = (Map<String, Object>) JSON.parse(onlineCount);
            if (!(boolean) parse1.get("success")) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常！");
            }
            ebayCount = (Integer) parse1.get("data");
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常！");
        }
        //远程调用接口获取AMAZON刊登数
        Integer amazonCount = 0;
//        UserCommon user = userUtils.getUser();
        String managerUsername = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        String count1 = remoteSellerService.findCount(managerUsername);
        if (count1 != null) {
            Map<String, Object> parse1 = (Map<String, Object>) JSON.parse(count1);
            if (!(boolean) parse1.get("success")) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常！");
            }
            amazonCount = (Integer) parse1.get("data");
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常！");
        }

        //远程调用接口获取速卖通刊登数
        Integer aliExpressCount = 0;
        String count2 = remoteSellerService.findAliexpressPublishCount(managerUsername);
        if (count2 != null) {
            Map<String, Object> parse1 = (Map<String, Object>) JSON.parse(count2);
            if (!(boolean) parse1.get("success")) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常！");
            }
            aliExpressCount = (Integer) parse1.get("data");
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "卖家服务异常！");
        }

        Long number = Long.valueOf(ebayCount + amazonCount + aliExpressCount);
        //远程调用接口获取利润
       /* String y1 = remoteCmsService.findUserRefundMoney("X","");
        BigDecimal afterMoney = new BigDecimal(0);
        if (y1 != null ) {
            Map<String,Object> parse = (Map<String, Object>) JSON.parse(y1);
            if (! (boolean) parse.get("success")) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "售后服务异常");
            }
            Map<String,Object> data = (Map<String, Object>) parse.get("data");
            if (data!=null) {
                afterMoney = (BigDecimal) data.get("money");
            }
        }else{
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "售后服务异常");
        }*/
        Map<String, Object> x1 = sysOrderMapper.findUserGrossMargin(managerUsername, "X");
        //非空判断
        BigDecimal grossMargin = BigDecimal.valueOf(0);
        if (x1 != null) {
            grossMargin = (BigDecimal) x1.get("grossMargin");
        }
        BigDecimal money = new BigDecimal(0);
//        money = grossMargin.setScale(2, BigDecimal.ROUND_HALF_UP);    //总利润：总毛利-总售后扣款
        money = grossMargin;    //总利润：总毛利-总售后扣款
        return new OrderRecord(count, saleroom, number, money);
    }

//    @Override
//    public Map<String, BigDecimal> getOrderProfit(String sellerPlId, String startDate, String endDate) {
//        Long profit = sysOrderMapper.getOrderProfit(sellerPlId,startDate,endDate);
//        Map<String, BigDecimal> map = new HashMap<>();
//        map.put("profit", new BigDecimal(profit));
//        return map;
//    }

    @Override
    /**
     * description: 更改订单为售后状态
     * @Param: sysOrderId  订单ID
     * @return void
     * create by wujiachuang
     */
    public void updateOrderStatus(String sysOrderId, byte status) {
        sysOrderNewMapper.updateOrderAfterStatus(sysOrderId, status);
    }

    @Override
    /**
     * description: 更改订单项为售后状态
     * @Param: sysOrderId  订单项ID
     * @return void
     * create by wujiachuangtem
     */
    public void updateOrderItemStatus(String sysOrderId, String sku, byte status) {
        sysOrderDetailMapper.updateOrderItemAfterStatus(sysOrderId, sku, status);
    }

    @Override
    /**
     * description: 获取用户毛利
     * @Param: username   用户名
     * @return java.util.Map<java.lang.String, java.math.BigDecimal> 封装好的数据
     * create by wujiachuang
     */
    public Map<String, BigDecimal> getUserProfit(String username) {
//        UserCommon user = userUtils.getUser();
        Map<String, BigDecimal> map = new HashMap<>();
        BigDecimal profit = new BigDecimal(0);
        BigDecimal lastProfit = new BigDecimal(0);
        //本月毛利
        Map<String, Object> y = sysOrderMapper.findUserGrossMargin(username, "Y");
        BigDecimal grossMargin = BigDecimal.valueOf(0);
        if (y != null) {
            grossMargin = (BigDecimal) y.get("grossMargin");
        }
        //上月毛利
        Map<String, Object> n = sysOrderMapper.findUserGrossMargin(username, "N");
        BigDecimal lastGrossMargin = BigDecimal.valueOf(0);
        if (n != null) {
            lastGrossMargin = (BigDecimal) n.get("grossMargin");
        }
      /*  //本月售后扣的钱
        String y1 = remoteCmsService.findUserRefundMoney("Y","");
        BigDecimal afterMoney = new BigDecimal(0);
        if (y1 != null ) {
        Map<String,Object> parse = (Map<String, Object>) JSON.parse(y1);
            if (! (boolean) parse.get("success")) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "售后服务异常");
            }
            Map<String,Object> data = (Map<String, Object>) parse.get("data");
            if (data!=null) {
                afterMoney = (BigDecimal) data.get("money");
            }
        }else{
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "售后服务异常");
        }
        profit = grossMargin.subtract(afterMoney);    //本月预估利润：本月毛利-本月售后扣款

        //上月毛利
        Map<String, Object> n = sysOrderMapper.findUserGrossMargin(username, "N");
        BigDecimal lastGrossMargin = (BigDecimal) n.get("grossMargin");
        //本月售后扣的钱
        String y2 = remoteCmsService.findUserRefundMoney("N","");
        BigDecimal lastAfterMoney = new BigDecimal(0);
        if (y1 != null ) {
            Map<String,Object> parse = (Map<String, Object>) JSON.parse(y2);
            if (!(boolean) parse.get("success")) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "售后服务异常");
            }
            Map<String,Object> data = (Map<String, Object>) parse.get("data");
            if (data!=null) {
            lastAfterMoney  = (BigDecimal) data.get("money");
            }
        }else{
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "售后服务异常");
        }
        lastProfit = lastGrossMargin.subtract(lastAfterMoney);    //本月预估利润：本月毛利-本月售后扣款*/
//        BigDecimal bigDecimal = grossMargin.setScale(2, BigDecimal.ROUND_HALF_UP);
//        BigDecimal bigDecimal1 = lastGrossMargin.setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal bigDecimal = grossMargin;
        BigDecimal bigDecimal1 = lastGrossMargin;
        map.put("profit", bigDecimal);
        map.put("lastProfit", bigDecimal1);
        return map;
    }

    @Override
    /**
     * description: 获取今天和昨日的订单数量
     * @Param: loginName  用户名
     * @Param: shopName   店铺名
     * @return java.util.Map<java.lang.String, java.lang.Object> 封装好的数据
     * create by wujiachuang
     */
    public Map<String, Object> getOrderCountTodayAndYesterday(String loginName, String shopName) {
        String sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        Integer shopNameId = null;
        if (StringUtils.isNotBlank(shopName)) {
            shopNameId = getAccountAndShopInfoUtils.GetSellerShopIdByShopName(shopName, sellerPlAccount);
        }
        Long orderCountToday = sysOrderNewMapper.getOrderCountToday(loginName, shopNameId);
        Long orderCountYesterday = sysOrderNewMapper.getOrderCountYesterday(loginName, shopNameId);
        Map<String, Object> map = new HashMap<>();
        map.put("todayOrderCount", orderCountToday);
        map.put("yesterdayOrderCount", orderCountYesterday);
        return map;
    }

    @Override
    /**
     * description: 查询卖家日订单数量
     * @Param: loginName 用户名
     * @Param: shopName  店铺名
     * @return java.util.List<com.rondaful.cloud.order.entity.TheMonthOrderCount> 封装好的数据
     * create by wujiachuang
     */
    public List<TheMonthOrderCount> querySellerDayOrderCount(String loginName, String shopName) {
        String sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        Integer shopNameId = null;
        if (StringUtils.isNotBlank(shopName)) {
            shopNameId = getAccountAndShopInfoUtils.GetSellerShopIdByShopName(shopName, sellerPlAccount);
        }
        return sysOrderNewMapper.querySellerDayOrderCount(loginName, shopNameId);
    }

    @Override
    /**
     * description: 查询卖家的总销售额和总利润
     * @Param: loginName 用户名
     * @Param: shopName  店铺名
     * @return java.util.Map<java.lang.String, java.lang.Object> 封装好的数据
     * create by wujiachuang
     */
    public Map<String, Object> querySellerTotalSalesAndTotalProfit(String loginName, String shopName) {
        String sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        Integer shopNameId = null;
        if (StringUtils.isNotBlank(shopName)) {
            shopNameId = getAccountAndShopInfoUtils.GetSellerShopIdByShopName(shopName, sellerPlAccount);
        }
        //总销售额
        Double totalSales = sysOrderNewMapper.querySellerTotalSales(loginName, shopNameId, null);
        //总利润
        Double totalProfit = sysOrderNewMapper.querySellerTotalProfit(loginName, shopNameId, null);
        Map<String, Object> map = new HashMap<>();
        if (totalProfit == null) {
            totalProfit = Double.valueOf(0);
        }
        if (totalSales == null) {
            totalSales = Double.valueOf(0);
        }
        map.put("totalSales", new BigDecimal(totalSales));
        map.put("totalProfit", new BigDecimal(totalProfit));
        return map;
    }

    @Override
    /**
     * description: 查询卖家日总销售额和日总利润
     * @Param: loginName  用户名
     * @Param: shopName  店铺名
     * @return java.util.List<com.rondaful.cloud.order.entity.TheMonthOrderSaleAndProfit> 封装好的数据
     * create by wujiachuang
     */
    public List<TheMonthOrderSaleAndProfit> querySellerDayTotalSalesAndTotalProfit(String loginName, String shopName) {
        String sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        Integer shopNameId = null;
        if (StringUtils.isNotBlank(shopName)) {
            shopNameId = getAccountAndShopInfoUtils.GetSellerShopIdByShopName(shopName, sellerPlAccount);
        }
        List<TheMonthOrderSaleAndProfit> theMonthOrderSaleAndProfits = sysOrderNewMapper.queryDaySellerSalesAndProfit(loginName, shopNameId);
        for (TheMonthOrderSaleAndProfit theMonthOrderSaleAndProfit : theMonthOrderSaleAndProfits) {
            BigDecimal profit = new BigDecimal(theMonthOrderSaleAndProfit.getProfit());
            BigDecimal sale = new BigDecimal(theMonthOrderSaleAndProfit.getSale());
            theMonthOrderSaleAndProfit.setProfit(String.valueOf(profit));
            theMonthOrderSaleAndProfit.setSale(String.valueOf(sale));
        }
        return theMonthOrderSaleAndProfits;
    }

    @Override
    /**
     * description: 获取买家人数和国家
     * @Param: loginName    用户名
     * @Param: shopName    店铺名
     * @return java.util.List<com.rondaful.cloud.order.entity.BuyerCountAndCountryCode> 封装好的数据
     * create by wujiachuang
     */
    public List<BuyerCountAndCountryCode> queryTotalBuyerCountAndCountry(String loginName, String shopName) {
        String sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        Integer shopNameId = null;
        if (StringUtils.isNotBlank(shopName)) {
            shopNameId = getAccountAndShopInfoUtils.GetSellerShopIdByShopName(shopName, sellerPlAccount);
        }
//        List<BuyerCountAndCountryCode> buyerCountAndCountryCodes = sysOrderNewMapper.queryTotalBuyerCountAndCountry(loginName, shopNameId);
        List<BuyerCountAndCountryCode> buyerCountAndCountryCodes = sysOrderMapper.queryTotalBuyerCountAndCountry(loginName, shopNameId);
        return buyerCountAndCountryCodes;
    }

    @Override
    /**
     * description: 获取购买的人数和重复购买的人数
     * @Param: loginName   用户名
     * @Param: shopName    店铺名
     * @return java.util.Map<java.lang.String, java.lang.Object>  封装好的数据
     * create by wujiachuang
     */
    public Map<String, Object> queryBuyerCountAndBuyerCountBuyAgain(String loginName, String shopName) {
        String sellerPlAccount = getLoginUserInformationByToken.getUserDTO().getTopUserLoginName();
        Integer shopNameId = getAccountAndShopInfoUtils.GetSellerShopIdByShopName(shopName, sellerPlAccount);
        Long buyerCount = sysOrderMapper.queryBuyerCount(loginName, shopNameId);
        Long buyerCountAgain = sysOrderMapper.queryBuyerCountBuyAgain(loginName, shopNameId);
        Map<String, Object> map = new HashMap<>();
        map.put("buyerCount", buyerCount);
        map.put("buyerCountAgain", buyerCountAgain);
        return map;
    }

    @Override
    /**
     * @Author chenjunhua
     * @Description
     * @Date 2019/4/28 15:37
     * @param orderSource    订单来源类型
     * @param sourceOrderId  来源订单ID
     * @return com.rondaful.cloud.order.entity.SysOrder
     **/
    public List<SysOrder> selectSysOrdersBySourceOrderIdAndType(String sourceOrderId, Integer orderSource) {
        List<SysOrder> sysOrderList = sysOrderMapper.selectSysOrdersBySourceOrderIdAndType(sourceOrderId, orderSource);
        return sysOrderList;
    }

    /**
     * 手工标记异常订单信息  wujiachuang
     *
     * @param orderId
     * @param text
     */
    @Override
    public void updateMarkException(String orderId, String text) {
        sysOrderNewMapper.updateMarkException(orderId, text);
    }


    /**
     * 取消作废订单
     *
     * @param orderId
     */
    @Override
    @TxTransaction(isStart = true)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String cancelInvalidOrders(String orderId) {
        SysOrderNew orderNew = queryOrderAndLock(orderId); //查询订单并判断状态然后加锁
        //TODO 判断作废前该订单是待发货状态还是已拦截状态
        if (orderNew.getPayStatus() == PayInfoEnum.PayStatusEnum.CANCELLED.getValue()) { //TODO 支付状态为已取消，证明之前是已拦截状态
            sysOrderService.updateInterceptStatus(orderNew);
        } else if (orderNew.getPayStatus() == PayInfoEnum.PayStatusEnum.TO_BE_PAY.getValue()) {//TODO 支付状态为待支付，证明之前是待发货状态
            sysOrderService.updateCancelStatus(orderNew);
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "系统内部错误！");
        }
        redissLockUtil.unlock(orderId);
        return "取消作废成功！";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCancelStatus(SysOrderNew orderNew) {
        String operateSysOrderId = orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId();
        if (StringUtils.isNotBlank(operateSysOrderId)) {//合并订单
            List<String> list = Arrays.asList(operateSysOrderId.split("\\#"));
            for (String id : list) {
                sysOrderService.updateStatusAndAddLog(id);
            }
        } else {//普通订单或者拆分订单
            sysOrderService.updateStatusAndAddLog(orderNew.getSysOrderId());
        }
    }

    public void updateOrderInterceptStatus(SysOrderNew orderNew) {
        String operateSysOrderId = orderNew.getSysOrderPackageList().get(0).getOperateSysOrderId();
        if (StringUtils.isNotBlank(operateSysOrderId)) {//合并订单
            List<String> list = Arrays.asList(operateSysOrderId.split("\\#"));
            for (String id : list) {
                orderNew.setSysOrderId(id);
                sysOrderService.reactivationOrder(orderNew);
            }
        } else {//普通订单或者拆分订单
            sysOrderService.reactivationOrder(orderNew);
        }
    }

    public SysOrderNew queryOrderAndLock(String orderId) {
        SysOrderNew orderNew = queryOrder(orderId);
        if (orderNew == null || !Objects.equals(orderNew.getOrderDeliveryStatus(), OrderDeliveryStatusNewEnum.CANCELLED.getValue())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400);
        }
        judgeOrderStatusIsHandleAndLock(orderNew); //判断包裹状态是否符合要求
        return orderNew;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusAndAddLog(String orderId) {
        sysOrderNewMapper.updateOrdersStatus(orderId, OrderDeliveryStatusNewEnum.WAIT_PAY.getValue());//将订单更改为待发货状态
        //添加操作日志
        sysOrderLogService.insertSelective(
                new SysOrderLog(orderId,
                        OrderHandleLogEnum.Content.CANCELINVALID.cancelInvalid(orderId),
                        OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                        userUtils.getUser().getUsername()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reactivationOrder(SysOrderNew orderNew) {
        orderNew.setOrderDeliveryStatus(OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());//设置订单发货状态为已拦截
        orderNew.setPayStatus((byte) 40);
        orderNew.setPayMethod((byte) 0);
        sysOrderNewMapper.updateOrderPayMethod(orderNew);
//        //添加操作日志
//        sysOrderLogService.insertSelective(
//                new SysOrderLog(orderNew.getSysOrderId(),
//                        OrderHandleLogEnum.Content.CANCELINVALID.cancelInvalid(orderNew.getSysOrderId()),
//                        OrderHandleLogEnum.OrderStatus.STATUS_4.getMsg(),
//                        userUtils.getUser().getUsername()));
       /* String result = remoteFinanceService.reactivation(orderNew.getSysOrderId());
        if (StringUtils.isBlank(result)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "财务服务异常！");
        } else {
            JSONObject object = JSONObject.parseObject(result);
            String messsage = object.getString("msg");
            String flag = object.getString("success");
            if (flag.equalsIgnoreCase("true")) {
                //重新冻结成功,更改支付状态和支付方式
                orderNew.setPayStatus(PayInfoEnum.PayStatusEnum.FREEZE_SUCCESS.getValue());//设置支付状态为冻结成功
                orderNew.setPayMethod(PayInfoEnum.PayMethodEnum.ACCOUNT_BALANCE.getPayMethod());//设置支付方式为账户余额
                orderNew.setOrderDeliveryStatus(OrderDeliveryStatusNewEnum.INTERCEPTED.getValue());//设置订单发货状态为已拦截
                sysOrderNewMapper.updateOrderPayMethod(orderNew);
                //添加操作日志
                sysOrderLogService.insertSelective(
                        new SysOrderLog(orderNew.getSysOrderId(),
                                OrderHandleLogEnum.Content.CANCELINVALID.cancelInvalid(orderNew.getSysOrderId()),
                                OrderHandleLogEnum.OrderStatus.STATUS_4.getMsg(),
                                userUtils.getUser().getUsername()));
            } else {
                logger.error("异常：订单号：{}调用财务服务重新冻结失败:{}", orderNew.getSysOrderId(), messsage);
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "财务系统：" + messsage);
            }
        }*/
    }

    @Override
    public SysOrder selectSysOrderBySysOrderId(String sysOrderId) {
        return sysOrderMapper.selectSysOrderBySysOrderId(sysOrderId);
    }

    /**
     * 根据订单详情列表返回订单的包邮字段
     *
     * @param sysOrderDetails List<SysOrderDetail>
     * @return @see{@link Constants.SysOrder}
     */
    public static Integer getSysOrderFreeFreightType(List<SysOrderDetail> sysOrderDetails) {
        Set<Integer> detailFreeFreightSet = new HashSet<>();

        for (SysOrderDetail sysOrderDetail : sysOrderDetails) {
            Integer detailFreeFreight = sysOrderDetail.getFreeFreight();
            if (null == detailFreeFreight) {
                detailFreeFreight = Constants.SysOrder.NOT_FREE_FREIGHT;
            }
            detailFreeFreightSet.add(detailFreeFreight);
        }

        if (detailFreeFreightSet.contains(Constants.SysOrder.FREE_FREIGHT) &&
                detailFreeFreightSet.contains(Constants.SysOrder.NOT_FREE_FREIGHT)) {
            return Constants.SysOrder.PART_FREE_FREIGHT;
        }

        if (detailFreeFreightSet.contains(Constants.SysOrder.FREE_FREIGHT) &&
                !detailFreeFreightSet.contains(Constants.SysOrder.NOT_FREE_FREIGHT)) {
            return Constants.SysOrder.FREE_FREIGHT;
        }

        if (!detailFreeFreightSet.contains(Constants.SysOrder.FREE_FREIGHT) &&
                detailFreeFreightSet.contains(Constants.SysOrder.NOT_FREE_FREIGHT)) {
            return Constants.SysOrder.NOT_FREE_FREIGHT;
        }

        return Constants.SysOrder.NOT_FREE_FREIGHT;
    }

    public List<SysOrder> findSysOrderBySourceOrderId(String sourceOrderId) {
        return sysOrderMapper.findSysOrderBySourceOrderId(sourceOrderId);
    }

    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void insertSysOrderBatch(SysOrderTransferInsertOrUpdateDTO sysOrderInsertDTO) throws Exception {
        // 组装批量插入订单的数据
        logger.info("准备批量插入的数据: {}", FastJsonUtils.toJsonString(sysOrderInsertDTO));
        sysOrderNewMapper.insertBatchSelective(sysOrderInsertDTO.getSysOrderDTOList());
        sysOrderDetailMapper.insertBatchSelective(sysOrderInsertDTO.getSysOrderDetailDTOList());
        sysOrderReceiveAddressMapper.insertBatchSelective(sysOrderInsertDTO.getSysOrderReceiveAddressDTOList());
        sysOrderPackageMapper.insertBatchSelective(sysOrderInsertDTO.getSysOrderPackageDTOList());
        sysOrderPackageDetailMapper.insertBatchSelective(sysOrderInsertDTO.getSysOrderPackageDetailDTOList());
        addOrderLogAndInformSeller(sysOrderInsertDTO);//添加订单操作日志并通知卖家
        sysOrderInsertDTO.getSysOrderPackageDTOList().forEach(orderPackageDTO -> {
            List<String> list = Arrays.asList(orderPackageDTO.getMappingOrderRuleLog().split("#"));
            for (String orderLog : list) {
                sysOrderLogMapper.insertSelective(new SysOrderLog(orderPackageDTO.getSysOrderId(),
                        orderLog,
                        OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                        OrderHandleLogEnum.Operator.SYSTEM.getMsg()));
            }
        });
        logger.info("成功转换{}条订单", sysOrderInsertDTO.getSysOrderDTOList().size());
        //TODO 自动发货：自动支付校验，调用发货接口、符合要求的自动发货。这版未加押金判断
        for (SysOrderDTO sysOrderDTO : sysOrderInsertDTO.getSysOrderDTOList()) {
            if (sysOrderDTO.getGrossMargin() != null) {
                try {
                    if (Boolean.valueOf(Utils.returnRemoteResultDataString(remoteFinanceService.autopayVerify(sysOrderDTO.getSellerPlId()), "财务服务异常"))
                            && systemOrderCommonService.getThreshold(sysOrderDTO.getSellerPlId(), sysOrderDTO.getPlatformShopId(), sysOrderDTO.getProfitMargin())) {
                        try {
                            logger.error("自动发货订单号：{}", sysOrderDTO.getSysOrderId());
                            systemOrderService.deliverGoodSingleNew(sysOrderDTO.getSysOrderId(), true);
                        } catch (Exception e) {
                            logger.error("自动发货失败，订单号：{},异常信息：{}", sysOrderDTO.getSysOrderId(), e.toString());
                            continue;
                        }
                    }
                } catch (Exception e) {
                    logger.error("校验是否可以自动支付，调用财务服务异常返回Null，用户ID：{}", sysOrderDTO.getSellerPlId());
                    continue;
                }
            }
        }
    }

    public void addOrderLogAndInformSeller(SysOrderTransferInsertOrUpdateDTO sysOrderInsertDTO) throws JSONException {
        for (SysOrderDTO sysOrderDTO : sysOrderInsertDTO.getSysOrderDTOList()) {
            sysOrderLogService.insertSelective(
                    new SysOrderLog(sysOrderDTO.getSysOrderId(),
                            OrderHandleLogEnum.Content.NEW_ORDER.newOrder(sysOrderDTO.getSysOrderId()),
                            OrderHandleLogEnum.OrderStatus.STATUS_1.getMsg(),
                            OrderHandleLogEnum.Operator.SYSTEM.getMsg()));
            orderMessageSender.sendOrderStockOut(sysOrderDTO.getSellerPlAccount(), sysOrderDTO.getSysOrderId(),
                    MessageEnum.ORDER_NEW_NOTICE, null);
        }
    }

    /**
     * 分割插入订单
     *
     * @param sysOrderDTOList {@link List<SysOrderDTO>}
     * @return
     */
    @Override
    public SysOrderTransferInsertOrUpdateDTO splitInsertSysOrderData(List<SysOrderDTO> sysOrderDTOList) {
        // 获取需要插入的数据
        List<SysOrderDTO> sysOrderInsertDTOList = new ArrayList<>();
        List<SysOrderDetailDTO> sysOrderDetailDTOList = new ArrayList<>();
        List<SysOrderReceiveAddressDTO> sysOrderReceiveAddressDTOList = new ArrayList<>();
        List<SysOrderPackageDTO> sysOrderPackageDTOList = new ArrayList<>();
        List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOList = new ArrayList<>();
        // 需要更新的源订单
        List<UpdateSourceOrderDTO> updateSourceOrderDTOList = new ArrayList<>();
        List<UpdateSourceOrderDetailDTO> updateSourceOrderDetailDTOList = new ArrayList<>();

        for (SysOrderDTO sysOrderDTO : sysOrderDTOList) {

            //设置EBAY来源订单的最迟发货时间
            setEbayLatestDeliveryTime(sysOrderDTOList, sysOrderDTO);

            //设置创建人，修改人，平台佣金率，毛利和利润
            sysOrderDTO.setCreateBy(SysOrderLogEnum.Operator.SYSTEM.getMsg());
            sysOrderDTO.setUpdateBy(SysOrderLogEnum.Operator.SYSTEM.getMsg());
            //设置平台佣金率
            if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
                sysOrderDTO.setPlatformCommissionRate(PlatformCommissionEnum.AMAZON.getValue());
            } else if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
                sysOrderDTO.setPlatformCommissionRate(PlatformCommissionEnum.EBAY.getValue());
            } else if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
                sysOrderDTO.setPlatformCommissionRate(PlatformCommissionEnum.WISH.getValue());
            }
//            setPlatformCommissionRateAndProfitMarginAndGrossMargin(sysOrderDTO);
            systemOrderCommonService.setGrossMarginAndProfitMarginAndTotal(sysOrderDTO);

            //将订单设置为转入来源的订单，对部分转入成功的订单先设置订单项都为转入成功，然后设置订单为转入成功
            reSetOrderStatus(sysOrderDTO);

            updateSourceOrderDTOList.addAll(this.getUpdateSourceOrderDTOList(sysOrderDTO));

            // 订单商品详情
            List<SysOrderDetailDTO> sysOrderDetailDTOS = sysOrderDTO.getSysOrderDetailList();
            sysOrderDetailDTOS.forEach(sysOrderDetailDTO -> {
                updateSourceOrderDetailDTOList.addAll(this.getUpdateSourceOrderDetailDTOList(sysOrderDetailDTO));
            });

            if (sysOrderDTO.getConverSysStatus() == ConvertSysStatusEnum.CONVERT_FAILURE.getValue()) {
                continue;
            }

            String sysOrderId = sysOrderDTO.getSysOrderPackageList().get(0).getSysOrderId();
            sysOrderDetailDTOS.forEach(sysOrderDetailDTO -> {
                sysOrderDetailDTO.setSysOrderId(sysOrderId);
                sysOrderDetailDTO.setOrderLineItemId(OrderUtils.getPLOrderItemNumber());
                sysOrderDetailDTOList.add(sysOrderDetailDTO);
            });

            sysOrderDTO.setSysOrderId(sysOrderId);
            sysOrderInsertDTOList.add(sysOrderDTO);

            // 收货地址
            sysOrderDTO.getSysOrderReceiveAddress().setSysOrderId(sysOrderId);
            sysOrderReceiveAddressDTOList.add(sysOrderDTO.getSysOrderReceiveAddress());

            // 包裹信息
            List<SysOrderPackageDTO> packages = sysOrderDTO.getSysOrderPackageList();
            sysOrderPackageDTOList.addAll(packages);
            for (SysOrderPackageDTO sysOrderPackageDTO : packages) {
                sysOrderPackageDTO.setCreater(SysOrderLogEnum.Operator.SYSTEM.getMsg());
                sysOrderPackageDTO.setModifier(SysOrderLogEnum.Operator.SYSTEM.getMsg());
                sysOrderPackageDTO.setSysOrderId(sysOrderId);
                sysOrderPackageDTO.setSourceOrderId(sysOrderDTO.getSourceOrderId());
                sysOrderPackageDTO.setOperateStatus(OrderPackageHandleEnum.OperateStatusEnum.GENERAL.getValue());
                String orderTrackId = sysOrderPackageDTO.getOrderTrackId();
                List<SysOrderPackageDetailDTO> sysOrderPackageDetailDTOS = sysOrderPackageDTO.getSysOrderPackageDetailList();

                sysOrderPackageDetailDTOS.forEach(sysOrderPackageDetailDTO -> {
                    sysOrderPackageDetailDTO.setCreater(SysOrderLogEnum.Operator.SYSTEM.getMsg());
                    sysOrderPackageDetailDTO.setModifier(SysOrderLogEnum.Operator.SYSTEM.getMsg());
                    sysOrderPackageDetailDTO.setOrderTrackId(orderTrackId);
                    sysOrderPackageDetailDTOList.add(sysOrderPackageDetailDTO);
                });
            }
        }

        // 需要批量插入或更新的数据
        SysOrderTransferInsertOrUpdateDTO insertOrUpdateDTO = new SysOrderTransferInsertOrUpdateDTO();
        insertOrUpdateDTO.setSysOrderDTOList(sysOrderInsertDTOList);
        insertOrUpdateDTO.setSysOrderDetailDTOList(sysOrderDetailDTOList);
        insertOrUpdateDTO.setSysOrderPackageDTOList(sysOrderPackageDTOList);
        insertOrUpdateDTO.setSysOrderPackageDetailDTOList(sysOrderPackageDetailDTOList);
        insertOrUpdateDTO.setSysOrderReceiveAddressDTOList(sysOrderReceiveAddressDTOList);
        insertOrUpdateDTO.setUpdateSourceOrderDTOList(updateSourceOrderDTOList);
        insertOrUpdateDTO.setUpdateSourceOrderDetailDTOList(updateSourceOrderDetailDTOList);

        return insertOrUpdateDTO;
    }

    public void reSetOrderStatus(SysOrderDTO sysOrderDTO) {
        sysOrderDTO.setIsConvertOrder(Constants.isConvertOrder.YES);
        if (sysOrderDTO.getConverSysStatus() == ConvertSysStatusEnum.CONVERT_PORTION_SUCCESS.getValue()) {
            sysOrderDTO.setConverSysStatus(ConvertSysStatusEnum.CONVERT_SUCCESS.getValue());
            sysOrderDTO.setOrderAmount(null);
            sysOrderDTO.setTotal(null);
            sysOrderDTO.setEstimateShipCost(null);
            sysOrderDTO.getSysOrderDetailList().forEach(sysOrderDetailDTO -> sysOrderDetailDTO.setConverSysDetailStatus((byte) ConvertSysStatusEnum.CONVERT_SUCCESS.getValue()));
        }
    }

    public void setEbayLatestDeliveryTime(List<SysOrderDTO> sysOrderDTOList, SysOrderDTO sysOrderDTO) {
        if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
            sysOrderDTO.getSysOrderDetailList().forEach(sysOrderDetailDTO -> {
                List<String> list = new ArrayList<>();
                for (String sourceItemId : Arrays.asList(sysOrderDetailDTO.getSourceOrderLineItemId().split("#"))) {
                    list.add(ebayOrderDetailMapper.selectDeliverDeadLineByOrderLineItemId(sourceItemId));
                }
                try {
                    sysOrderDetailDTO.setDeliverDeadline(TimeUtil.getMinTime(list));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            ebayOrderService.getEbayOrderDeliverDeadline(sysOrderDTOList);
        }
    }

    public void setPlatformCommissionRateAndProfitMarginAndGrossMargin(SysOrderDTO sysOrderDTO) {
        sysOrderDTO.setCreateBy(SysOrderLogEnum.Operator.SYSTEM.getMsg());
        sysOrderDTO.setUpdateBy(SysOrderLogEnum.Operator.SYSTEM.getMsg());
        //设置平台佣金率
        if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_AMAZON.getValue()) {
            sysOrderDTO.setPlatformCommissionRate(PlatformCommissionEnum.AMAZON.getValue());
        } else if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_EBAY.getValue()) {
            sysOrderDTO.setPlatformCommissionRate(PlatformCommissionEnum.EBAY.getValue());
        } else if (sysOrderDTO.getOrderSource() == OrderSourceEnum.CONVER_FROM_WISH.getValue()) {
            sysOrderDTO.setPlatformCommissionRate(PlatformCommissionEnum.WISH.getValue());
        }
        //如果有仓库、邮寄方式的时候才计算预估物流费
        SysOrderPackageDTO dto = sysOrderDTO.getSysOrderPackageList().get(0);
        if (dto.getDeliveryWarehouseId() != null && dto.getDeliveryMethodCode() != null) {
            SysOrder sysOrder = new SysOrder();
            sysOrder.setCommoditiesAmount(sysOrderDTO.getCommoditiesAmount() == null ? BigDecimal.ZERO : sysOrderDTO.getCommoditiesAmount());//订单货款
            sysOrder.setShippingServiceCost(sysOrderDTO.getShippingServiceCost() == null ? BigDecimal.ZERO : sysOrderDTO.getShippingServiceCost());//平台运费
            sysOrder.setOrderSource(Byte.valueOf(String.valueOf(sysOrderDTO.getOrderSource())));//订单来源
            sysOrder.setOrderAmount(sysOrderDTO.getOrderAmount() == null ? BigDecimal.ZERO : sysOrderDTO.getOrderAmount());//商品成本
            sysOrder.setEstimateShipCost(sysOrderDTO.getEstimateShipCost() == null ? BigDecimal.ZERO : sysOrderDTO.getEstimateShipCost());//设置预估物流费
            sysOrder.setTotal(sysOrderDTO.getTotal() == null ? BigDecimal.ZERO : sysOrderDTO.getTotal());//设置订单总售价：预估物流费+系统商品总金额---------
            logger.info("转单计算利润、利润率前的值：{}", FastJsonUtils.toJsonString(sysOrder));
            //设置预估利润、利润率
            systemOrderCommonService.setGrossMarginAndProfitMargin(sysOrder);
            logger.info("转单后计算利润、利润率前的值：{}", FastJsonUtils.toJsonString(sysOrder));
            sysOrderDTO.setProfitMargin(sysOrder.getProfitMargin());
            sysOrderDTO.setGrossMargin(sysOrder.getGrossMargin());
        }
    }

    private List<UpdateSourceOrderDetailDTO> getUpdateSourceOrderDetailDTOList(SysOrderDetailDTO sysOrderDetailDTO) {
        String sourceOrderItemId = sysOrderDetailDTO.getSourceOrderLineItemId();
        String orderId = sysOrderDetailDTO.getSourceOrderId();
        List<String> allSourceOrderItemIdList = new ArrayList<>();
        if (sourceOrderItemId.contains(Constants.SplitSymbol.HASH_TAG)) {
            List<String> sourceOrderIds = this.getSourceIds(sourceOrderItemId);
            allSourceOrderItemIdList.addAll(sourceOrderIds);
        } else {
            allSourceOrderItemIdList.add(sourceOrderItemId);
        }

        List<UpdateSourceOrderDetailDTO> updateSourceOrderDetailDTOList = new ArrayList<>();

        for (String orderItemId : allSourceOrderItemIdList) {
            Date now = new Date();
            UpdateSourceOrderDetailDTO updateSourceOrderDetailDTO = new UpdateSourceOrderDetailDTO();
            updateSourceOrderDetailDTO.setOrderId(orderId);
            updateSourceOrderDetailDTO.setOrderItemId(orderItemId);
            updateSourceOrderDetailDTO.setConverSysStatus(Integer.valueOf(sysOrderDetailDTO.getConverSysDetailStatus()));
            updateSourceOrderDetailDTO.setUpdateBy(Constants.DefaultUser.SYSTEM);
            updateSourceOrderDetailDTO.setUpdateDate(now);
            updateSourceOrderDetailDTOList.add(updateSourceOrderDetailDTO);
        }
        return updateSourceOrderDetailDTOList;
    }

    private List<UpdateSourceOrderDTO> getUpdateSourceOrderDTOList(SysOrderDTO sysOrderDTO) {
        String sourceOrderId = sysOrderDTO.getSourceOrderId();
        List<String> allSourceOrderIdList = new ArrayList<>();
        if (sourceOrderId.contains(Constants.SplitSymbol.HASH_TAG)) {
            List<String> sourceOrderIds = this.getSourceIds(sourceOrderId);
            allSourceOrderIdList.addAll(sourceOrderIds);
        } else {
            allSourceOrderIdList.add(sourceOrderId);
        }

        List<UpdateSourceOrderDTO> updateSourceOrderDTOList = new ArrayList<>();

        for (String orderId : allSourceOrderIdList) {
            Date now = new Date();
            UpdateSourceOrderDTO updateSourceOrderDTO = new UpdateSourceOrderDTO();
            updateSourceOrderDTO.setOrderId(orderId);
            updateSourceOrderDTO.setConverSysStatus(sysOrderDTO.getConverSysStatus());
            updateSourceOrderDTO.setUpdateBy(Constants.DefaultUser.SYSTEM);
            updateSourceOrderDTO.setUpdateDate(now);
            updateSourceOrderDTOList.add(updateSourceOrderDTO);
        }
        return updateSourceOrderDTOList;
    }

    private List<String> getSourceIds(String idStr) {
        String[] ids = StringUtils.split(idStr, Constants.SplitSymbol.HASH_TAG);
        return new ArrayList<>(Arrays.asList(ids));
    }

    @Override
    public Map<String, List<String>> queryPlOrderIdBySourceOrderId(List<String> sourceOrderIds) {
        Map<String, List<String>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(sourceOrderIds)) {
            sourceOrderIds.forEach(sourceOrderId -> {
                List<String> list = sysOrderNewMapper.queryPlOrderIdBySourceOrderId(sourceOrderId);
                map.put(sourceOrderId, list);
            });
        }
        return map;
    }

    @Override
    public void updateOrderPackageItemStatus(String orderTrackId, String sku, byte status) {
        sysOrderPackageDetailMapper.updateOrderPackageItemStatus(orderTrackId, sku, status);
    }

    @Override
    public List<PLOrderInfoDTO> getPLOrderInfo(List<String> sysOrderIds) {
        List<PLOrderInfoDTO> list = new ArrayList<>();
        for (String sysOrderId : sysOrderIds) {
            SysOrderNew order = null;
            try {
                order = queryOrderByOther(sysOrderId);
            } catch (Exception e) {
                continue;
            }
            Map<String, String> map = new HashMap<>();
            for (SysOrderDetail item : order.getSysOrderDetails()) {
                map.put(item.getSku(), item.getSupplierSku());
            }
            for (SysOrderPackage orderPackage : order.getSysOrderPackageList()) {
                for (SysOrderPackageDetail packageDetail : orderPackage.getSysOrderPackageDetailList()) {
                    PLOrderInfoDTO infoDTO = new PLOrderInfoDTO();
                    infoDTO.setSupplierSku(map.get(packageDetail.getSku()));
                    infoDTO.setTotalPrice(order.getOrderAmount());
                    infoDTO.setOrderTotal(order.getTotal());
                    infoDTO.setSysOrderId(sysOrderId);
                    infoDTO.setItemName(packageDetail.getSkuName());
                    infoDTO.setItemNameEn(packageDetail.getSkuNameEn());
                    infoDTO.setItemPrice(packageDetail.getSkuPrice());
                    infoDTO.setOrderTrackId(packageDetail.getOrderTrackId());
                    infoDTO.setSku(packageDetail.getSku());
                    infoDTO.setSellerShipFee(packageDetail.getSellerShipFee());
                    infoDTO.setSkuQuantity(packageDetail.getSkuQuantity());
                    infoDTO.setSupplierShipFee(packageDetail.getSupplierShipFee());
                    infoDTO.setFreeFreight(packageDetail.getFreeFreight());
                    //调用售后接口
                    String result = null;
                    String data = null;
                    try {
                        result = remoteCmsService.getOrderAfterSaleByOrderTrackIdAndSku(orderPackage.getOrderTrackId(), packageDetail.getSku(), sysOrderId);
                        logger.error("调用售后服务返回结果:{}", result);
                        if (StringUtils.isNotBlank(result)) {
                            data = Utils.returnRemoteResultDataString(result, "售后服务异常");
                        }

                        if (StringUtils.isNotBlank(data)) {
                            OrderAfterSalesModel orderAfterSalesModel = JSONObject.parseObject(data, OrderAfterSalesModel.class);
                            if (StringUtils.isNotBlank(orderAfterSalesModel.getRefundMoney())) {
                                infoDTO.setRefundMoney(new BigDecimal(orderAfterSalesModel.getRefundMoney()));
                            } else {
                                infoDTO.setRefundMoney(new BigDecimal("0"));
                            }
                        } else {
                            infoDTO.setRefundMoney(new BigDecimal("0"));
                        }
                    } catch (Exception e) {
                        logger.error("调用售后服务异常:{}", e);
                        continue;
                    }
                    list.add(infoDTO);
                }
            }
        }
        return list;
    }

    @Override
    public boolean judgePLOrderStatus(List<String> sysOrderIds) {
        for (String sysOrderId : sysOrderIds) {
            SysOrderNew order = sysOrderNewMapper.queryOrderByOrderId(sysOrderId);
            if (!order.getOrderDeliveryStatus().equals(OrderDeliveryStatusNewEnum.COMPLETED.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<PLOrderInfoDTO> getPLOrderInfoBatch(List<String> sysOrderIds) {
        List<PLOrderInfoDTO> list = new ArrayList<>();
        for (String sysOrderId : sysOrderIds) {
            SysOrderNew order = null;
            try {
                order = queryOrderByOther(sysOrderId);
            } catch (Exception e) {
                continue;
            }
            Map<String, String> map = new HashMap<>();
            for (SysOrderDetail item : order.getSysOrderDetails()) {
                map.put(item.getSku(), item.getSupplierSku());
            }
            for (SysOrderPackage orderPackage : order.getSysOrderPackageList()) {
                for (SysOrderPackageDetail packageDetail : orderPackage.getSysOrderPackageDetailList()) {
                    PLOrderInfoDTO infoDTO = new PLOrderInfoDTO();
                    infoDTO.setSupplierSku(map.get(packageDetail.getSku()));
                    infoDTO.setTotalPrice(order.getOrderAmount());
                    infoDTO.setOrderTotal(order.getTotal());
                    infoDTO.setSysOrderId(sysOrderId);
                    infoDTO.setItemName(packageDetail.getSkuName());
                    infoDTO.setItemNameEn(packageDetail.getSkuNameEn());
                    infoDTO.setItemPrice(packageDetail.getSkuPrice());
                    infoDTO.setOrderTrackId(packageDetail.getOrderTrackId());
                    infoDTO.setSku(packageDetail.getSku());
                    infoDTO.setSellerShipFee(packageDetail.getSellerShipFee());
                    infoDTO.setSkuQuantity(packageDetail.getSkuQuantity());
                    infoDTO.setSupplierShipFee(packageDetail.getSupplierShipFee());
                    infoDTO.setFreeFreight(packageDetail.getFreeFreight());
                    list.add(infoDTO);
                }
            }
        }
        return list;
    }

    @Override
    public OrderInfoVO getOrderInfoToSupplier(String packageId) {
        SysOrderPackage orderPackage = sysOrderPackageMapper.queryOrderPackageByPk(packageId);
        SysOrderNew order = sysOrderNewMapper.queryOrderByOrderId(orderPackage.getSysOrderId());
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        List<String> list = Arrays.asList(orderPackage.getSourceOrderId().split("\\#"));
        orderInfoVO.setPlatformOrderIdList(list);
        orderInfoVO.setDeliveryWarehouse(orderPackage.getDeliveryWarehouse());
        orderInfoVO.setPlatformShopId(order.getPlatformShopId());
        return orderInfoVO;
    }

    @Override
    public Object intercept() {
        Map<String, Object> map = new HashMap<>();
        List<String> list = sysOrderNewMapper.queryInterceptOrder();
        logger.info("需要取消冻结的订单：{}条", list.size());
        int count = 0;
        int error = 0;
        List<String> errorList = new ArrayList<>();
        for (String orderId : list) {
            try {
                sysOrderService.cancelMoney(orderId);
            } catch (Exception e) {
                errorList.add(orderId);
                error++;
                continue;
            }
            count++;
        }
        logger.info("成功取消冻结订单：{}条", count);
        logger.info("取消冻结订单失败：{}条,明细：{}", error, FastJsonUtils.toJsonString(errorList));
        map.put("需要取消冻结的订单", list.size());
        map.put("success", count);
        map.put("failure", error);
        map.put("失败明细", errorList);
        return map;
    }
}