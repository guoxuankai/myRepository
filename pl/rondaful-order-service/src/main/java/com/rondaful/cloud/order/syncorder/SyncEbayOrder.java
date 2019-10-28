package com.rondaful.cloud.order.syncorder;

import com.ebay.soap.eBLBaseComponents.AddressType;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CancelStatusCodeType;
import com.ebay.soap.eBLBaseComponents.CheckoutStatusType;
import com.ebay.soap.eBLBaseComponents.CompleteStatusCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.MultiLegShipmentType;
import com.ebay.soap.eBLBaseComponents.MultiLegShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.OrderStatusCodeType;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.ebay.soap.eBLBaseComponents.PaymentTransactionStatusCodeType;
import com.ebay.soap.eBLBaseComponents.PaymentsInformationType;
import com.ebay.soap.eBLBaseComponents.RefundInformationType;
import com.ebay.soap.eBLBaseComponents.RefundTransactionInfoType;
import com.ebay.soap.eBLBaseComponents.ShipmentTrackingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingPackageInfoType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.TransactionArrayType;
import com.ebay.soap.eBLBaseComponents.TransactionType;
import com.ebay.soap.eBLBaseComponents.UserType;
import com.ebay.soap.eBLBaseComponents.VariationType;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.enums.OrderHandleEnum;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.order.base.AbstractSyncOrder;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.constant.Constants;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.entity.eBay.EbayOrderStatus;
import com.rondaful.cloud.order.mapper.EbayOrderStatusMapper;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.seller.EmpowerMapper;
import com.rondaful.cloud.order.service.IEbayOrderHandleService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.utils.ApplicationContextProvider;
import com.rondaful.cloud.order.utils.BeanConvertor;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.RateUtil;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Blade
 * @date 2019-07-05 17:19:02
 **/
public class SyncEbayOrder extends AbstractSyncOrder {
    private EmpowerMapper empowerMapper;
    private Empower executeEmpower;
    private IEbayOrderHandleService ebayOrderHandleService;
    private List<OrderType> orderTypeList;
    private RateUtil rateUtil;
    private EbayOrderStatusMapper ebayOrderStatusMapper;
    private ISkuMapService skuMapService;

    private Map<String, Object> platformOrderInsertData;
    private List<EbayOrder> ebayOrderListInsertUpdate = new ArrayList<>();
    private List<EbayOrderDetail> ebayDetailListInsertUpdate = new ArrayList<>();
    private List<EbayOrderStatus> ebayOrderStatusListInsertUpdate = new ArrayList<>();
    private String rateCurrency = PropertyUtil.getProperty("rateCurrency");
    private int syncEbayOrderInterval = Integer.valueOf(PropertyUtil.getProperty("syncEbayOrderInterval"));
    private String daysOverdueStr = PropertyUtil.getProperty("daysOverdue");

    public SyncEbayOrder(Empower empower) {
        empowerMapper = (EmpowerMapper) ApplicationContextProvider.getBean("empowerMapper");
        ebayOrderHandleService = (IEbayOrderHandleService) ApplicationContextProvider.getBean("ebayOrderHandleServiceImpl");
        ebayOrderStatusMapper = (EbayOrderStatusMapper) ApplicationContextProvider.getBean("ebayOrderStatusMapper");
        skuMapService = (ISkuMapService) ApplicationContextProvider.getBean("skuMapServiceImpl");
        rateUtil = (RateUtil) ApplicationContextProvider.getBean("rateUtil");
        this.executeEmpower = empower;
        super.className = this.getClass().getSimpleName();
    }

    @Override
    public void getAuthorizedEmpower() {
        logger.info("{}获取授权的Ebay账号是：{}", className, executeEmpower.getAccount());
        super.goOn = true;
        logger.info("{}获取授权的Ebay账号的后续步骤标识是：{}", className, super.goOn);
    }

    @Override
    public void syncPlatformOrder() {
        logger.info("{}开始同步Ebay订单", className);
        syncEbayOrder(executeEmpower);

        super.goOn = !CollectionUtils.isEmpty(this.orderTypeList);

        logger.info("{}同步Ebay订单的后续步骤标识是：{}", className, super.goOn);
    }

    @Override
    public void persistencePlatformOrder() {
        logger.info("{}开始持久化平台订单", className);
        logger.info("{} 本次需要持久化的平台订单的数量有：{}", className, this.orderTypeList.size());
        try {
            this.platformOrderInsertData = ebayOrderHandleService.classifyOrders(executeEmpower, orderTypeList);
            this.assembleInsertPlatformOrderData();
        } catch (ParseException e) {
            logger.error("{}持久化平台订单异常", className, e);
            super.goOn = false;
            return;
        }
        super.goOn = true;
        logger.info("{}持久化平台订单的后续步骤标识是：{}", className, super.goOn);
    }

    /**
     * 同步ebay订单
     *
     * @param empower {@link Empower} 店铺账号
     */
    private void syncEbayOrder(Empower empower) {
        logger.info("处理的品连账号是：{}。同步的eBay店铺为{}", empower.getPinlianaccount(), empower.getAccount());
        // 初始化
        this.orderTypeList = new ArrayList<>();

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.add(Calendar.HOUR, syncEbayOrderInterval);
        empower.setModTimeFrom(calendarFrom);
        Calendar calendarTo = Calendar.getInstance();
        empower.setModTimeTo(calendarTo);

        OrderType[] orders = ebayOrderHandleService.sendReqGetEbayResp(empower);
        if (orders == null || orders.length == 0) {
            return;
        }

        List<OrderType> orderTypes = ebayOrderHandleService.filterOrders(orders);
        orderTypeList.addAll(orderTypes);
    }

    /**
     * 组装插入ebay订单表的数据
     */
    private void assembleInsertPlatformOrderData() {
        this.ebayOrderListInsertUpdate = new ArrayList<>();
        this.ebayDetailListInsertUpdate = new ArrayList<>();
        this.ebayOrderStatusListInsertUpdate = new ArrayList<>();

        for (OrderType order : orderTypeList) {
            String orderId = order.getOrderID();
            if (StringUtils.isBlank(orderId)) {
                continue;
            }

            try {
                assembleEbayOrderData(order);
            } catch (ParseException e) {
                logger.error("", e);
            }
        }
    }

    /**
     * 组装ebayOrder的订单数据
     *
     * @param order {@link OrderType} ebay平台的订单数据
     */
    private void assembleEbayOrderData(OrderType order) throws ParseException {
        String[] ebayOrderIgnoreProper = {};
        AddressType shippingAddress = order.getShippingAddress();
        EbayOrder ebayOrder = BeanConvertor.copy(shippingAddress, EbayOrder.class, ebayOrderIgnoreProper);

        if (shippingAddress.getCountry() != null) {
            ebayOrder.setCountry(shippingAddress.getCountry().value());
        }

        ebayOrder = this.convert2EbayOrder(order, ebayOrder);

        EbayOrderStatus ebayOrderStatus = new EbayOrderStatus();
        ebayOrderStatus = this.convert2EbayOrderStatus(executeEmpower, order, ebayOrderStatus);

        Date lastModifiedTimeDB = ebayOrderStatusMapper.selectLastModTimeById(order.getOrderID());
        if (lastModifiedTimeDB == null) {
            this.ebayOrderListInsertUpdate.add(ebayOrder);
            this.ebayOrderStatusListInsertUpdate.add(ebayOrderStatus);
            this.addEbayDetailUpdateInsert(order, executeEmpower, ebayOrder);
        } else {
            CheckoutStatusType checkoutStatus = order.getCheckoutStatus();
            Date ebayModifiedTime = null;
            if (checkoutStatus != null) {
                Calendar modifiedTime = checkoutStatus.getLastModifiedTime();
                if (modifiedTime != null) {
                    ebayModifiedTime = TimeUtil.calendarToDate(modifiedTime);
                }
            }
            if (lastModifiedTimeDB.compareTo(ebayModifiedTime) == -1) {
                ebayOrderListInsertUpdate.add(ebayOrder);
                ebayOrderStatusListInsertUpdate.add(ebayOrderStatus);
                this.addEbayDetailUpdateInsert(order, executeEmpower, ebayOrder);
            }
        }
    }

    /**
     * 将eBay的Order对象转化为EbayOrder本地对象并设置数据
     *
     * @param ebayOrder
     * @param order
     * @return
     */
    private EbayOrder convert2EbayOrder(OrderType order, EbayOrder ebayOrder) {
        String orderID = order.getOrderID();
        ebayOrder.setOrderId(orderID);
        AmountType total = order.getTotal();
        //金额
        if (total != null) {
            //金额数值
            double value = total.getValue();
            //币种
            CurrencyCodeType currencyID = total.getCurrencyID();
            if (currencyID != null) {
                ebayOrder.setTotal(value + "#" + currencyID.value() + "#" + rateUtil.remoteExchangeRateByCurrencyCode(currencyID.value(), rateCurrency));
            }
        }
        Calendar createdTime = order.getCreatedTime();
        if (createdTime != null) {
            ebayOrder.setCreatedTime(TimeUtil.calendarToStr(createdTime));
        }

        Calendar paidTime = order.getPaidTime();
        if (paidTime != null) {
            ebayOrder.setPaidTime(TimeUtil.calendarToStr(paidTime));
        }

        String sellerUserID = order.getSellerUserID();
        if (sellerUserID != null) {
            ebayOrder.setSellerUserId(sellerUserID);
        }

        ShippingDetailsType shippingDetails = order.getShippingDetails();
        if (shippingDetails != null) {
            Integer recordNumber = shippingDetails.getSellingManagerSalesRecordNumber();
            if (recordNumber != null && sellerUserID != null) {
                ebayOrder.setRecordNumber(sellerUserID + "-" + recordNumber);
            }
        }

        String buyerUserID = order.getBuyerUserID();
        if (StringUtils.isNotBlank(buyerUserID)) {
            ebayOrder.setBuyerUserId(buyerUserID);
        }

        TransactionArrayType transactionArray = order.getTransactionArray();
        Set<String> buyerEmailSet = new HashSet<>();
        if (transactionArray != null) {
            TransactionType[] transaction = transactionArray.getTransaction();
            if (transaction != null) {
                for (TransactionType transactionType : transaction) {
                    if (transactionType != null) {
                        UserType buyer = transactionType.getBuyer();
                        if (buyer != null)
                            buyerEmailSet.add(buyer.getEmail());
                    }
                }
            }
        }
        StringBuilder buyerEmail = new StringBuilder();
        if (buyerEmailSet.size() == 1) {
            buyerEmail = new StringBuilder(new ArrayList<>(buyerEmailSet).get(0));
        } else {
            for (String str : buyerEmailSet) {
                buyerEmail.append(str).append("#");
            }
        }
        ebayOrder.setBuyerEmail(buyerEmail.toString());

        Calendar shippedTime = order.getShippedTime();
        if (shippedTime != null) {
            ebayOrder.setShippedTime(TimeUtil.calendarToStr(shippedTime));
        }

        //金额
        ShippingServiceOptionsType shippingServiceSelected = order.getShippingServiceSelected();
        if (shippingServiceSelected != null) {
            AmountType shippingServiceCost = shippingServiceSelected.getShippingServiceCost();
            if (shippingServiceCost != null) {
                double value1 = shippingServiceCost.getValue();
                CurrencyCodeType currencyID1 = shippingServiceCost.getCurrencyID();
                if (currencyID1 != null) {
                    ebayOrder.setShippingServiceCost(value1 + "#" + currencyID1.value() + "#" + rateUtil.remoteExchangeRateByCurrencyCode(currencyID1.value(), rateCurrency));
                }
            }
        }
        //金额
        AmountType amountPaid = order.getAmountPaid();
        if (amountPaid != null) {
            double value2 = amountPaid.getValue();
            CurrencyCodeType currencyID2 = amountPaid.getCurrencyID();
            if (currencyID2 != null) {
                ebayOrder.setAmountPaid(value2 + "#" + currencyID2.value() + "#" + rateUtil.remoteExchangeRateByCurrencyCode(currencyID2.value(), rateCurrency));
            }
        }
        CheckoutStatusType checkoutStatus = order.getCheckoutStatus();
        if (checkoutStatus != null) {
            CompleteStatusCodeType paymentStatus = checkoutStatus.getStatus();
            if (paymentStatus != null) {
                ebayOrder.setPaymentStatus(paymentStatus.value());
            }

            BuyerPaymentMethodCodeType paymentMethod = checkoutStatus.getPaymentMethod();
            if (paymentMethod != null) {
                ebayOrder.setPaymentMethod(paymentMethod.value());
            }

            MultiLegShippingDetailsType multiLegShippingDetails = order.getMultiLegShippingDetails();
            if (multiLegShippingDetails != null) {
                MultiLegShipmentType sellerShipmentToLogisticsProvider = multiLegShippingDetails.getSellerShipmentToLogisticsProvider();
                if (sellerShipmentToLogisticsProvider != null) {
                    AddressType shipToAddress = sellerShipmentToLogisticsProvider.getShipToAddress();
                    if (shipToAddress != null) {
                        String referenceID = shipToAddress.getReferenceID();
                        if (referenceID != null) {
                            ebayOrder.setReferenceId(referenceID);
                        }
                    }
                }
            }
        }
        Calendar lastModifiedTime = checkoutStatus.getLastModifiedTime();
        if (lastModifiedTime != null) {
            ebayOrder.setLastModifiedTime(TimeUtil.calendarToStr(lastModifiedTime));
        }
        String sellerEmail = order.getSellerEmail();
        if (sellerEmail != null) {
            ebayOrder.setSellerEmail(sellerEmail);
        }
        String buyerCheckoutMessage = order.getBuyerCheckoutMessage();
        if (buyerCheckoutMessage != null) {
            ebayOrder.setBuyerCheckoutMessage(OrderUtils.filterEmoji(buyerCheckoutMessage));
        }

        ebayOrder.setCreateBy(Constants.DefaultUser.SYSTEM);
        ebayOrder.setUpdateBy(Constants.DefaultUser.SYSTEM);
        return ebayOrder;
    }

    private EbayOrderStatus convert2EbayOrderStatus(Empower empower, OrderType order, EbayOrderStatus status) {
        status.setOrderId(order.getOrderID());
        status.setSellerPlId(empower.getPinlianid());
        status.setSellerPlAccount(empower.getPinlianaccount());
        status.setEmpowerId(empower.getEmpowerid());
        String sellerUserID = order.getSellerUserID();
        if (sellerUserID != null) {
            status.setSellerUserId(sellerUserID);
        }
        //setLastModifiedTime
        //setOrderStatus发货状态
        OrderStatusCodeType orderStatus = order.getOrderStatus();
        if (orderStatus != null) {
            status.setOrderStatus(orderStatus.value());
        }
        //setHandleByTime  订单最迟发货时间
        ShippingServiceOptionsType shippingServiceSelected = order.getShippingServiceSelected();
        Calendar deliverDeadLine = null;
        if (shippingServiceSelected != null) {
            ShippingPackageInfoType[] packageInfo = shippingServiceSelected.getShippingPackageInfo();
            if (packageInfo != null && packageInfo.length > 0) {
                for (ShippingPackageInfoType type : packageInfo) {
                    Calendar handleByTime = type.getHandleByTime();
                    if (handleByTime != null) {
                        if (deliverDeadLine == null) {
                            deliverDeadLine = handleByTime;
                        }
                        if (deliverDeadLine.compareTo(handleByTime) > 0) {
                            deliverDeadLine = handleByTime;
                        }
                    }
                }
            }
        }
        status.setHandleByTime(deliverDeadLine == null ? "" : TimeUtil.calendarToStr(deliverDeadLine));
        //setPaymentStatus
        CheckoutStatusType checkoutStatus = order.getCheckoutStatus();
        if (checkoutStatus != null) {
            Calendar lastModifiedTime = checkoutStatus.getLastModifiedTime();
            if (lastModifiedTime != null) status.setLastModifiedTime(TimeUtil.calendarToStr(lastModifiedTime));
            CompleteStatusCodeType paymentStatus = checkoutStatus.getStatus();
            if (paymentStatus != null) status.setPaymentStatus(paymentStatus.value());//订单支付状态
        }
        //setCancelStatus
        CancelStatusCodeType cancelStatus = order.getCancelStatus();
        if (cancelStatus != null) {
            status.setCancelStatus(cancelStatus.value());
        }

        //setRefundStatus 订单退货状态
        PaymentsInformationType monetaryDetails = order.getMonetaryDetails();
        if (monetaryDetails != null) {
            RefundInformationType refunds = monetaryDetails.getRefunds();
            if (refunds != null) {
                RefundTransactionInfoType[] refund = refunds.getRefund();
                if (refund != null && refund.length != 0) {
                    int count = 0;
                    for (RefundTransactionInfoType refundType : refund) {
                        PaymentTransactionStatusCodeType refundStatus = refundType.getRefundStatus();
                        if (refundStatus != null) {
                            if (PaymentTransactionStatusCodeType.SUCCEEDED.value().equals(refundStatus.value()))
                                count++;
                        }
                    }
                    if (count == refund.length) {
                        status.setRefundStatus(OrderHandleEnum.RefundStatus.ALLREFUND.getValue());
                    } else if (count == 0) {
                        status.setRefundStatus(OrderHandleEnum.RefundStatus.NONEREFUND.getValue());
                    } else {
                        status.setRefundStatus(OrderHandleEnum.RefundStatus.PARTIONREFUND.getValue());
                    }
                }
            }
        }
        status.setCreateBy(Constants.DefaultUser.SYSTEM);
        status.setUpdateBy(Constants.DefaultUser.SYSTEM);
        return status;
    }

    private void addEbayDetailUpdateInsert(OrderType order, Empower empower, EbayOrder ebayOrder) throws ParseException {
        TransactionArrayType transactionArray = order.getTransactionArray();
        if (transactionArray != null) {
            TransactionType[] transaction = transactionArray.getTransaction();
            List<String> itemIDList = new ArrayList<>(transaction.length);
            for (TransactionType detail : Arrays.asList(transaction)) {
                ItemType item = detail.getItem();
                if (item != null) {
                    itemIDList.add(item.getItemID());
                }
            }
            Map<String, Integer> deadlineMap = this.getDispatchTimeMax(empower.getEmpowerid(), itemIDList);
            if (transaction != null) {
                String paidTime = ebayOrder.getPaidTime();
                boolean isShow = false;
                for (TransactionType transactionType : transaction) {
                    EbayOrderDetail ebayOrderDetail = new EbayOrderDetail();
                    ebayOrderDetail.setOrderId(order.getOrderID());
                    ebayOrderDetail = this.conver2Detail(empower, transactionType, ebayOrderDetail, deadlineMap, paidTime);
                    this.ebayDetailListInsertUpdate.add(ebayOrderDetail);

                    String sku = this.getSku(ebayOrderDetail);
                    String plSku = skuMapService.queryPlSku(OrderRuleEnum.platformEnm.E_BAU.getPlatform(), String.valueOf(empower.getEmpowerid()), sku, String.valueOf(empower.getPinlianid()));
                    if (StringUtils.isNotEmpty(plSku)) {
                        // 如果其中一个SKU有对应的品连SKU，则列表显示
                        isShow = true;
                    }
                }

                // 增加 ebayOrder 的一个判断条件，如果 ebayOrderDetail 的sku全部转换失败，则在列表不显示。
                // 其余的情况显示。
                if (isShow) {
                    ebayOrder.setShowOnList("1");
                } else {
                    ebayOrder.setShowOnList("0");
                }
            }
        }
    }

    private Map<String, Integer> getDispatchTimeMax(Integer empowerId, List<String> itemIDList) {
        List<Map<String, Object>> list = empowerMapper.getDispatchTimeMax(empowerId, itemIDList);
        Map<String, Integer> map = Maps.newHashMap();
        if (list != null) {
            for (Map<String, Object> vv : list) {
                map.put(vv.get("itemId").toString(), (Integer) vv.get("maxTime"));
            }
        }
        return map;
    }

    /**
     * 获取ebay订单信息的商品SKU
     *
     * @param ebayOrderDetail ebay订单信息
     * @return sku
     */
    private String getSku(EbayOrderDetail ebayOrderDetail) {
        String variationSku = ebayOrderDetail.getVariationSku();

        if (StringUtils.isNotEmpty(variationSku)) {
            return variationSku;
        }

        String sku = ebayOrderDetail.getSku();
        if (StringUtils.isNotEmpty(sku)) {
            return sku;
        }

        return null;
    }

    private EbayOrderDetail conver2Detail(Empower empower, TransactionType transactionType, EbayOrderDetail detail,
                                          Map<String, Integer> deadlineMap, String paidTime) throws ParseException {
        //setOrderId
        //setOrderLineItemId
        String orderLineItemID = transactionType.getOrderLineItemID();
        if (orderLineItemID != null) {
            detail.setOrderLineItemId(orderLineItemID);
        }
        String transactionID = transactionType.getTransactionID();
        //setTransactionId
        if (transactionID != null) {
            detail.setTransactionId(transactionID);
        }
        //setBuyerEmail
        UserType buyer = transactionType.getBuyer();
        if (buyer != null) {
            detail.setBuyerEmail(buyer.getEmail());
        }
        //setTransactionPrice
        //金额
        AmountType transactionPrice = transactionType.getTransactionPrice();
        if (transactionPrice != null) {
            double value = transactionPrice.getValue();
            CurrencyCodeType currencyID = transactionPrice.getCurrencyID();
            if (currencyID != null) {
                detail.setTransactionPrice(value + "#" + currencyID.value() + "#" + rateUtil.remoteExchangeRateByCurrencyCode(currencyID.value(), rateCurrency));
            }
        }
        //setQuantityPurchased
        //setRecordNumber
        //setShippingCarrierUsed
        //setShipmentTrackingNumber
        Integer quantityPurchased = transactionType.getQuantityPurchased();
        detail.setQuantityPurchased(quantityPurchased);
        ShippingDetailsType shippingDetails = transactionType.getShippingDetails();
        if (shippingDetails != null) {
            Integer sellingManagerSalesRecordNumber = shippingDetails.getSellingManagerSalesRecordNumber();
            if (sellingManagerSalesRecordNumber != null) {
                detail.setRecordNumber(sellingManagerSalesRecordNumber);
            }
            ShipmentTrackingDetailsType[] shipmentTrackingDetails = shippingDetails.getShipmentTrackingDetails();
            if (shipmentTrackingDetails != null) {
                detail.setShippingCarrierUsed("");
                detail.setShipmentTrackingNumber("");
                for (ShipmentTrackingDetailsType shipmentTrackingDetail : shipmentTrackingDetails) {
                    if (shipmentTrackingDetail != null && shipmentTrackingDetail.getShippingCarrierUsed() != null) {
                        detail.setShippingCarrierUsed(detail.getShippingCarrierUsed()
                                .concat(shipmentTrackingDetail.getShippingCarrierUsed() + "#"));
                    }
                    if (shipmentTrackingDetail != null && shipmentTrackingDetail.getShipmentTrackingNumber() != null) {
                        detail.setShipmentTrackingNumber(detail.getShipmentTrackingNumber()
                                .concat(shipmentTrackingDetail.getShipmentTrackingNumber() + "#"));
                    }
                }
            }
        }
        //setItemId
        ItemType item = transactionType.getItem();
        if (item != null) {
            String itemID = item.getItemID();
            String itemTitle = item.getTitle();
            String sku = item.getSKU();
            if (StringUtils.isNotBlank(itemID)) {
                detail.setItemId(itemID);
            }
            Integer days = deadlineMap.get(itemID);
            if (days != null && StringUtils.isNotBlank(paidTime)) {
                detail.setHandleByTime(TimeUtil.stringAddSubtract(paidTime, deadlineMap.get(itemID)));
            }
            if (StringUtils.isNotBlank(itemTitle)) {
                detail.setItemTitle(itemTitle);
            }
            if (StringUtils.isNotBlank(sku)) {
                detail.setSku(sku);
            }
        }
        //setVariationSku
        //setVariationTitle
        //setVariationViewItemUrl
        VariationType variation = transactionType.getVariation();
        if (variation != null) {
            if (StringUtils.isNotBlank(variation.getSKU())) {
                detail.setVariationSku(variation.getSKU());
            }
            if (StringUtils.isNotBlank(variation.getVariationTitle())) {
                detail.setVariationTitle(variation.getVariationTitle());
            }
            if (StringUtils.isNotBlank(variation.getVariationViewItemURL())) {
                detail.setVariationViewItemUrl(variation.getVariationViewItemURL());
            }
        }
        ShippingServiceOptionsType shippingServiceSelected = transactionType.getShippingServiceSelected();
        Calendar deliverDeadLine = null;
        if (shippingServiceSelected != null) {
            ShippingPackageInfoType[] packageInfo = shippingServiceSelected.getShippingPackageInfo();
            if (packageInfo == null || packageInfo.length == 0) {
                for (ShippingPackageInfoType type : packageInfo) {
                    Calendar handleByTime = type.getHandleByTime();
                    if (handleByTime != null) {
                        if (deliverDeadLine == null) {
                            deliverDeadLine = handleByTime;
                        }
                        if (deliverDeadLine.compareTo(handleByTime) > 0) {
                            deliverDeadLine = handleByTime;
                        }
                    }
                }
            }
        }
        if (deliverDeadLine != null) {
            detail.setHandleByTime(TimeUtil.calendarToStr(deliverDeadLine));
        } else if (paidTime != null) {
            detail.setHandleByTime(detail.getHandleByTime() == null ? TimeUtil.stringAddSubtract(paidTime, Integer.valueOf(daysOverdueStr)) : detail.getHandleByTime());
        }
        String operator = empower.getOperator();
        detail.setCreateBy(operator);
        detail.setUpdateBy(operator);
        return detail;
    }

    private void assemblePreCovertOrderData() {

    }

    public class ExecuteSyncEbayOrder extends Thread {
        private SyncEbayOrder syncEbayOrder;

        public ExecuteSyncEbayOrder(SyncEbayOrder syncEbayOrder) {
            this.syncEbayOrder = syncEbayOrder;
        }

        @Override
        public void run() {
            this.syncEbayOrder.syncOrder();
        }
    }
}