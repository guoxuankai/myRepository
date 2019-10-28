package com.rondaful.cloud.order.service.impl;

import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.call.GetOrdersCall;
import com.ebay.soap.eBLBaseComponents.*;
import com.google.common.collect.Maps;
import com.rondaful.cloud.common.enums.OrderHandleEnum;
import com.rondaful.cloud.common.enums.OrderRuleEnum;
import com.rondaful.cloud.order.config.PropertyUtil;
import com.rondaful.cloud.order.entity.eBay.EbayOrder;
import com.rondaful.cloud.order.entity.eBay.EbayOrderDetail;
import com.rondaful.cloud.order.entity.eBay.EbayOrderStatus;
import com.rondaful.cloud.order.enums.OrderSourceEnum;
import com.rondaful.cloud.order.mapper.EbayOrderStatusMapper;
import com.rondaful.cloud.order.rabbitmq.OrderMessageSender;
import com.rondaful.cloud.order.remote.RemoteSellerService;
import com.rondaful.cloud.order.seller.Empower;
import com.rondaful.cloud.order.seller.EmpowerMapper;
import com.rondaful.cloud.order.service.IEbayOrderHandleService;
import com.rondaful.cloud.order.service.ISkuMapService;
import com.rondaful.cloud.order.task.BatchSyncEbayOrderDealData;
import com.rondaful.cloud.order.utils.BeanConvertor;
import com.rondaful.cloud.order.utils.OrderUtils;
import com.rondaful.cloud.order.utils.RateUtil;
import com.rondaful.cloud.order.utils.TimeUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Service
public class EbayOrderHandleServiceImpl implements IEbayOrderHandleService, InitializingBean {
    @Autowired
    private EbayOrderStatusMapper ebayOrderStatusMapper;
    @Autowired
    private BatchSyncEbayOrderDealData batchSyncEbayOrderDealData;
    @Autowired
    private RateUtil rateUtil;
    @Autowired
    private EmpowerMapper empowerMapper;

    @Autowired
    private ISkuMapService skuMapService;
    @Autowired
    RemoteSellerService sellerService;
    @Autowired
    private OrderMessageSender orderMessageSender;

    private static String eBayServerUrl = PropertyUtil.getProperty("eBayServerUrl");
    private static String rateCurrency = PropertyUtil.getProperty("rateCurrency");
    private static String daysOverdueStr = PropertyUtil.getProperty("daysOverdue");

    private static String[] ebaySyncOrderFilters;

    private static Logger _log = LoggerFactory.getLogger(SyncEbayOrderServiceImpl.class);

    public EbayOrderHandleServiceImpl() {
        ebaySyncOrderFilters = this.getEbaySyncOrdersRequestFilter();
    }

    /**
     * 封装请求对象，发送请求到eBay，返回orders数组
     *
     * @return
     */
    public OrderType[] sendReqGetEbayResp(Empower empower) {
        String account = empower.getAccount();
        GetOrdersCall request = new GetOrdersCall(this.getApiContext(empower.getToken()));
        request.setModTimeFrom(empower.getModTimeFrom());
        request.setModTimeTo(empower.getModTimeTo());
        request.setOrderRole(TradingRoleCodeType.SELLER);
        request.setOutputSelector(ebaySyncOrderFilters);
        int count = 0;
        OrderType[] orders = null;
        while (true) {
            if (count == 3)
                break;
            try {
                long start = System.currentTimeMillis();
                orders = request.getOrders();
                long end = System.currentTimeMillis();
                _log.info("店铺{} 本次请求耗时 {} ms,同步下来 {} 个订单", account, (end - start), orders.length);
                break;
            } catch (Exception e) {
                _log.error("店铺{} 本次请求同步eBay订单平台抛异常", account, e);
                count++;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return orders;
    }

    /**
     * 过滤支付状态OrderArray.Order.CheckoutStatus.Status为Complete的订单
     * 2019-07-02 版本，要求所有状态都可以存入表中
     */
    public List<OrderType> filterOrders(OrderType[] orders) {
        return Arrays.asList(orders);
    }

    /**
     * 分类处理ebay订单和订单项对象到insertList和updateList
     *
     * @param orders
     * @return
     */
    public Map<String, Object> classifyOrders(Empower empower, List<OrderType> orders) throws ParseException {
        String account = empower.getAccount();
        long timeBegin = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>(3);
        List<EbayOrder> ebayOrderListInsertUpdate = new ArrayList<>();
        List<EbayOrderDetail> ebayDetailListInsertUpdate = new ArrayList<>();
        List<EbayOrderStatus> ebayOrderStatusListInsertUpdate = new ArrayList<>();
        for (OrderType order : orders) {
            String orderID = order.getOrderID();
            if (orderID == null) {
                continue;
            }
//                String[] ebayOrderIgnoreProper = {"orderId", "total", "createdTime", "paidTime", "buyerEmail", "shipServiceCost", "amountPaid", "ebayPaymentStatus",
//                        "paymentMethod", "lastModifiedTime", "cancelStatus", "sellerUserId"};

            String[] ebayOrderIgnoreProper = {};
            AddressType shippingAddress = order.getShippingAddress();
            if (shippingAddress != null && shippingAddress.getPhone().equalsIgnoreCase("Invalid Request")) {
                shippingAddress.setPhone("");
            }
            EbayOrder ebayOrder = BeanConvertor.copy(shippingAddress, EbayOrder.class, ebayOrderIgnoreProper);
            if (shippingAddress.getCountry() != null) {
                ebayOrder.setCountry(shippingAddress.getCountry().value());
            }
            ebayOrder = this.conver2EbayOrder(empower, order, ebayOrder);
            EbayOrderStatus status = new EbayOrderStatus();
            status = this.conver2EbayOrderStatus(empower, order, status);

            Date lastModifiedTimeDB = ebayOrderStatusMapper.selectLastModTimeById(orderID);
            if (lastModifiedTimeDB == null) {
                ebayOrderListInsertUpdate.add(ebayOrder);
                ebayOrderStatusListInsertUpdate.add(status);
                this.addEbayDetailUpdateInsert(order, empower, ebayOrder, ebayDetailListInsertUpdate);
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
                    ebayOrderStatusListInsertUpdate.add(status);
                    this.addEbayDetailUpdateInsert(order, empower, ebayOrder, ebayDetailListInsertUpdate);
                }
            }
        }
        map.put("ebayOrderListInsertUpdate", ebayOrderListInsertUpdate);
        _log.info("店铺{} 需要插入更新的ebay平台表订单个数为: {}", account, ebayOrderListInsertUpdate.size());
        map.put("ebayDetailListInsertUpdate", ebayDetailListInsertUpdate);
        _log.info("店铺{} 需要插入更新的ebay平台订单项表订单项个数为: {}", account, ebayDetailListInsertUpdate.size());
        map.put("ebayOrderStatusListInsertUpdate", ebayOrderStatusListInsertUpdate);
        _log.info("店铺{} 需要插入更新的ebay平台订单状态表订单个数为: {}", account, ebayOrderStatusListInsertUpdate.size());
        long timeEnd = System.currentTimeMillis();
        _log.info("店铺{} 订单数据分类到List结束总耗时:{}ms", account, (timeEnd - timeBegin));
        return map;
    }

    private void addEbayDetailUpdateInsert(OrderType order, Empower empower, EbayOrder ebayOrder, List<EbayOrderDetail> ebayDetailListInsertUpdate) throws ParseException {
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
                String handleByTime = TimeUtil.addTime(ebayOrder.getCreatedTime(), 2);
                boolean isShow = false;
                for (TransactionType transactionType : transaction) {
                    EbayOrderDetail ebayOrderDetail = new EbayOrderDetail();
                    ebayOrderDetail.setOrderId(order.getOrderID());
                    ebayOrderDetail = this.conver2Detail(handleByTime, empower, transactionType, ebayOrderDetail, deadlineMap, paidTime);
                    ebayDetailListInsertUpdate.add(ebayOrderDetail);

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

    @Override
    public Map<String, Integer> getDispatchTimeMax(Integer empowerId, List<String> itemIDList) {
        List<Map<String, Object>> list = empowerMapper.getDispatchTimeMax(empowerId, itemIDList);
        Map<String, Integer> map = Maps.newHashMap();
        if (list != null) {
            for (Map<String, Object> vv : list) {
                map.put(vv.get("itemId").toString(), (Integer) vv.get("maxTime"));
            }
        }
        return map;
    }

    public synchronized void centryDealData(Map<String, Object> stringObjectMap) {
        try {
            List<EbayOrder> ebayOrderListInsertUpdate = (List<EbayOrder>) stringObjectMap.get("ebayOrderListInsertUpdate");
            if (ebayOrderListInsertUpdate != null && ebayOrderListInsertUpdate.size() != 0) {
                this.insertUpdateEbayOrder(ebayOrderListInsertUpdate);
            }
            List<EbayOrderDetail> ebayDetailListInsertUpdate = (List<EbayOrderDetail>) stringObjectMap.get("ebayDetailListInsertUpdate");
            if (ebayDetailListInsertUpdate != null && ebayDetailListInsertUpdate.size() != 0) {
                this.insertUpdateEbayOrderDetail(ebayDetailListInsertUpdate);
            }
            List<EbayOrderStatus> ebayStatusListInsertUpdate = (List<EbayOrderStatus>) stringObjectMap.get("ebayOrderStatusListInsertUpdate");
            if (ebayStatusListInsertUpdate != null && ebayStatusListInsertUpdate.size() != 0) {
                this.insertUpdateEbayOrderStatus(ebayStatusListInsertUpdate);
            }

            if (!ebayOrderListInsertUpdate.isEmpty()) {
                // 处理发送消息的参数
                for (EbayOrder ebayOrder : ebayOrderListInsertUpdate) {
                    String orderId = ebayOrder.getOrderId();
                    if (StringUtils.isBlank(orderId)) {
                        continue;
                    }
                    for (EbayOrderDetail ebayOrderDetail : ebayDetailListInsertUpdate) {
                        if (orderId.equals(ebayOrderDetail.getOrderId())) {
                            ebayOrder.getEbayOrderDetails().add(ebayOrderDetail);
                        }
                    }
                }

                // 发送转单消息
                try {
                    orderMessageSender.sendBaseConvertOrder(ebayOrderListInsertUpdate, OrderSourceEnum.CONVER_FROM_EBAY);
                } catch (Exception e) {
                    _log.error("ebay发送转单消息异常", e);
                }
            }

        } catch (Exception e) {
            _log.error("持久化ebay订单数据异常", e);
        }
    }

    private void insertUpdateEbayOrder(List<EbayOrder> list) throws Exception {
        long timeBegin = System.currentTimeMillis();
        int ordersSize = list.size();
        batchSyncEbayOrderDealData.insertUpdateEbayOrder(list);
        /*if (ordersSize <= 2000) {
            batchSyncEbayOrderDealData.insertUpdateEbayOrder(list);
        } else {
            int a = ordersSize / 2000;
            int b = (a == 0) ? a : a + 1;
            for (int i = 1; i <= b; i++) {
                int from = (i - 1) * 2000;
                int to = 2000 * i - 1;
                List<EbayOrder> ebayOrders = list.subList(from, to);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            batchSyncEbayOrderDealData.insertUpdateEbayOrder(ebayOrders);
                        } catch (Exception e) {
                            _log.error("________________这批订单insertUpdateEbayOrder持久化失败________________" + e);
                        }
                    }
                });
            }
        }*/
        long timeEnd = System.currentTimeMillis();
        _log.info("ebay订单主表 数据批量插入更新insertUpdateEbayOrder结束。总耗时{}ms", (timeEnd - timeBegin));
    }

    private void insertUpdateEbayOrderDetail(List<EbayOrderDetail> list) throws Exception {
        long timeBegin = System.currentTimeMillis();
        int ordersDetailSize = list.size();
        batchSyncEbayOrderDealData.insertUpdateEbayOrderDetail(list);
        /*if (ordersDetailSize <= 2000) {
            batchSyncEbayOrderDealData.insertUpdateEbayOrderDetail(list);
        } else {
            int a = ordersDetailSize / 2000;
            int b = (a == 0) ? a : a + 1;
            for (int i = 1; i <= b; i++) {
                int from = (i - 1) * 2000;
                int to = 2000 * i - 1;
                List<EbayOrderDetail> ebayOrderDetails = list.subList(from, to);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            batchSyncEbayOrderDealData.insertUpdateEbayOrderDetail(ebayOrderDetails);
                        } catch (Exception e) {
                            _log.error("________________这批订单insertEbayOrderDetail持久化失败________________" + e);
                        }
                    }
                });
            }
        }*/
        long timeEnd = System.currentTimeMillis();
        _log.info("ebay订单项表 数据批量插入insertEbayOrderDetail结束。总耗时{}ms", (timeEnd - timeBegin));
    }

    public void insertUpdateEbayOrderStatus(List<EbayOrderStatus> list) throws Exception {
        long timeBegin = System.currentTimeMillis();
        int ordersStatusSize = list.size();
        batchSyncEbayOrderDealData.insertUpdateEbayOrderStatus(list);
        /*if (ordersStatusSize <= 2000) {
            batchSyncEbayOrderDealData.insertUpdateEbayOrderStatus(list);
        } else {
            int a = ordersStatusSize / 2000;
            int b = (a == 0) ? a : a + 1;
            for (int i = 1; i <= b; i++) {
                int from = (i - 1) * 2000;
                int to = 2000 * i - 1;
                List<EbayOrderStatus> ebayOrderStatuses = list.subList(from, to);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            batchSyncEbayOrderDealData.insertUpdateEbayOrderStatus(ebayOrderStatuses);
                        } catch (Exception e) {
                            _log.error("________________此批订单insertUpdateEbayOrderStatus持久化失败________________" + e);
                        }
                    }
                });
            }
        }*/
        long timeEnd = System.currentTimeMillis();
        _log.info("ebay订单状态表 数据批量插入更新insertUpdateEbayOrderStatus结束。总耗时 {}ms", (timeEnd - timeBegin));
    }

    /**
     * 将eBay的Order对象转化为EbayOrder本地对象并设置数据
     *
     * @param ebayOrder
     * @param order
     * @return
     */
    private EbayOrder conver2EbayOrder(Empower empower, OrderType order, EbayOrder ebayOrder) {
        String orderID = order.getOrderID();
        ebayOrder.setOrderId(orderID);
        AmountType total = order.getTotal();
        //金额
        if (total != null) {
            double value = total.getValue();//金额数值
            CurrencyCodeType currencyID = total.getCurrencyID();//币种
            if (currencyID != null) {
                ebayOrder.setTotal(value + "#" + currencyID.value() + "#" + rateUtil.remoteExchangeRateByCurrencyCode(currencyID.value(), rateCurrency));
//                _log.error("________{}===》{}________汇率________{}_______", currencyID.value(), rateCurrency, rateUtil.remoteExchangeRateByCurrencyCode(currencyID.value(), rateCurrency));
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
                        if (buyer != null && !(buyer.getEmail().equals("Invalid Request")))
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

        String operator = empower.getOperator();
        ebayOrder.setCreateBy(operator);
        ebayOrder.setUpdateBy(operator);
        return ebayOrder;
    }

    private EbayOrderDetail conver2Detail(String time, Empower empower, TransactionType transactionType, EbayOrderDetail detail, Map<String, Integer> deadlineMap, String paidTime) throws ParseException {
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
        if (buyer != null && !(buyer.getEmail().equals("Invalid Request"))) {
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
//            String resultStr = sellerService.getProductPicture(detail.getVariationSku(), detail.getItemId());
//            String data = Utils.returnRemoteResultDataString(resultStr, "获取商品图片发生异常");
//            List<String> pictureList = JSON.parseArray(data, String.class);
//            if (!CollectionUtils.isEmpty(pictureList)){
//                detail.setVariationViewItemUrl(pictureList.get(0));
//            }
            //if (StringUtils.isNotBlank(variation.getVariationViewItemURL())) {
            //       detail.setVariationViewItemUrl(variation.getVariationViewItemURL());
            //}
        }
        ShippingServiceOptionsType shippingServiceSelected = transactionType.getShippingServiceSelected();
        Calendar deliverDeadLine = null;
        if (shippingServiceSelected != null) {
            ShippingPackageInfoType[] packageInfo = shippingServiceSelected.getShippingPackageInfo();
            if (packageInfo != null || packageInfo.length != 0) {
                for (ShippingPackageInfoType type : packageInfo) {
                    Calendar handleByTime = type.getHandleByTime();
                    if (handleByTime != null) {
                        deliverDeadLine = handleByTime;
//                        if (deliverDeadLine == null) {
//                        }
//                        if (deliverDeadLine.compareTo(handleByTime) > 0) {
//                            deliverDeadLine = handleByTime;
//                        }
                    }
                }
            }
        }
        if (deliverDeadLine != null) {
            detail.setHandleByTime(TimeUtil.calendarToStr(deliverDeadLine));
        } else if (deliverDeadLine == null) {
            detail.setHandleByTime(time); //为空则设置订单创建时间加两天
        } else if (paidTime != null) {
            detail.setHandleByTime(detail.getHandleByTime() == null ? TimeUtil.stringAddSubtract(paidTime, Integer.valueOf(daysOverdueStr)) : detail.getHandleByTime());
        }
//        _log.error("____________ebay订单项表中最迟发货时间_________{}_________", detail.getHandleByTime());
        String operator = empower.getOperator();
        detail.setCreateBy(operator);
        detail.setUpdateBy(operator);
        return detail;
    }

    private EbayOrderStatus conver2EbayOrderStatus(Empower empower, OrderType order, EbayOrderStatus status) {
        status.setOrderId(order.getOrderID());
        status.setSellerPlId(empower.getPinlianid());
        status.setSellerPlAccount(empower.getPinlianaccount());
        status.setEmpowerId(empower.getEmpowerid());
        status.setSellerPlShopAccount(empower.getAccount());
        String sellerUserID = order.getSellerUserID();
        if (sellerUserID != null) status.setSellerUserId(sellerUserID);
        //setLastModifiedTime
        //setOrderStatus发货状态
        OrderStatusCodeType orderStatus = order.getOrderStatus();
        if (orderStatus != null) status.setOrderStatus(orderStatus.value());
        //setHandleByTime
        ShippingServiceOptionsType shippingServiceSelected = order.getShippingServiceSelected();
        Calendar deliverDeadLine = null;
        if (shippingServiceSelected != null) {
            ShippingPackageInfoType[] packageInfo = shippingServiceSelected.getShippingPackageInfo();
            if (packageInfo == null || packageInfo.length == 0) {
                for (ShippingPackageInfoType type : packageInfo) {
                    Calendar handleByTime = type.getHandleByTime();
                    if (handleByTime != null) {
                        if (deliverDeadLine == null)
                            deliverDeadLine = handleByTime;
                        if (deliverDeadLine.compareTo(handleByTime) > 0)
                            deliverDeadLine = handleByTime;
                    }
                }
            }
        }
        status.setHandleByTime(deliverDeadLine == null ? "" : TimeUtil.calendarToStr(deliverDeadLine));
//        _log.error("____________ebay订单状态表中最迟发货时间_________{}_________", status.getHandleByTime());
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
        if (cancelStatus != null) status.setCancelStatus(cancelStatus.value());
        //setRefundStatus
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
                    if (count == refund.length)
                        status.setRefundStatus(OrderHandleEnum.RefundStatus.ALLREFUND.getValue());
                    else if (count == 0) status.setRefundStatus(OrderHandleEnum.RefundStatus.NONEREFUND.getValue());
                    else status.setRefundStatus(OrderHandleEnum.RefundStatus.PARTIONREFUND.getValue());
                }
            }
        }
        String operator = empower.getOperator();
        status.setCreateBy(operator);
        status.setUpdateBy(operator);
        return status;
    }

    /**
     * 将0.0000#CNY#1.00：卖家填的平台物流费转成BigDecimal类型
     */
    public BigDecimal string2BigDecimal(String costStr) {
        if (StringUtils.isBlank(costStr)) return BigDecimal.valueOf(0);
        String[] split = StringUtils.split(costStr, "#");
        if (split.length < 3) {
            return new BigDecimal(split[0]);
        } else {
            return new BigDecimal(split[0]).multiply(new BigDecimal(split[2] == null ? "1.00" : split[2]));
        }
    }

    /**
     * 根据token和eBayServerUrl设置ApiContext
     *
     * @param token
     * @return
     */
    public ApiContext getApiContext(String token) {
        ApiContext apiContext = new ApiContext();
        apiContext.setTimeout(30000);//毫秒
        ApiCredential apiCredential = apiContext.getApiCredential();
        apiCredential.seteBayToken(token);
        apiContext.setApiServerUrl(eBayServerUrl);
        return apiContext;
    }

    private String[] getEbaySyncOrdersRequestFilter() {
        String[] ebayOrderFilters = PropertyUtil.getProperty("ebayOrderFilter").split("#");
        String[] ebayOrderDetailFilters = PropertyUtil.getProperty("ebayOrderDetailFilter").split("#");
        return (String[]) ArrayUtils.addAll(ebayOrderFilters, ebayOrderDetailFilters);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println();
    }
}
