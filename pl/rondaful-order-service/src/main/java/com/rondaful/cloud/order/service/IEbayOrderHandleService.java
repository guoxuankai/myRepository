package com.rondaful.cloud.order.service;

import com.ebay.sdk.ApiContext;
import com.ebay.soap.eBLBaseComponents.OrderType;
import com.rondaful.cloud.order.seller.Empower;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IEbayOrderHandleService {
    /**
     * 根据Empower对象（其中的token）请求同步ebay订单
     *
     * @param empower
     * @return
     * @throws Exception
     */
    OrderType[] sendReqGetEbayResp(Empower empower);

    /**
     * 过滤支付状态OrderArray.Order.CheckoutStatus.Status为Complete的订单
     */
    List<OrderType> filterOrders(OrderType[] orders);

    /**
     * 分类处理平台订单，分到ebayOrderListInsertUpdate，ebayDetailListInsertUpdate，ebayOrderStatusListInsertUpdate
     * 以便持久化
     *
     * @param orders
     * @return
     */
    Map<String, Object> classifyOrders(Empower empower, List<OrderType> orders) throws ParseException;

    /**
     * 获取订单中商品的最大发货时间
     *
     * @param empowerId
     * @param itemIDList
     * @return
     */
    Map<String, Integer> getDispatchTimeMax(Integer empowerId, List<String> itemIDList);

    /**
     * 处理金额项(0.0000#CNY#1.00)返回BigDecimal金额
     *
     * @param costStr
     * @return
     */
    BigDecimal string2BigDecimal(String costStr);

    /**
     * 拆分ebayOrderListInsertUpdate，ebayDetailListInsertUpdate，ebayOrderStatusListInsertUpdate
     * 分别持久化到数据库
     *
     * @param stringObjectMap
     * @throws Exception
     */
    void centryDealData(Map<String, Object> stringObjectMap);

    /**
     * 将token封装进ApiContext对象
     *
     * @param token
     * @return
     */
    ApiContext getApiContext(String token);
}
