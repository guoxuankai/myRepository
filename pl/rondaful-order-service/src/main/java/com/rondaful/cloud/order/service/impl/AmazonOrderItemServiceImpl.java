package com.rondaful.cloud.order.service.impl;

import com.amazonservices.mws.orders._2013_09_01.model.OrderItem;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrder;
import com.rondaful.cloud.order.entity.Amazon.AmazonOrderDetail;
import com.rondaful.cloud.order.mapper.AmazonOrderDetailMapper;
import com.rondaful.cloud.order.mapper.AmazonOrderMapper;
import com.rondaful.cloud.order.model.dto.syncorder.SysOrderDTO;
import com.rondaful.cloud.order.service.IAmazonOrderItemService;
import com.rondaful.cloud.order.service.IAmazonOrderService;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * 作者: Administrator
 * 时间: 2018-12-07 8:17
 * 包名: com.rondaful.cloud.order.service.impl
 * 描述:
 */
@Service
public class AmazonOrderItemServiceImpl extends BaseServiceImpl<AmazonOrderDetail> implements
        IAmazonOrderItemService {
    @Autowired
    private IAmazonOrderService amazonOrderService;
    @Autowired
    private RedissLockUtil redissLockUtil;
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AmazonOrderItemServiceImpl.class);
    @Resource
    private AmazonOrderDetailMapper amazonOrderDetailMapper;
    @Resource
    private AmazonOrderMapper amazonOrderMapper;
    /*
     * 查询亚马逊商品ASIN码
     * */
    @Override
    public List<String> selectAmazonItemASINList(String plAccount,String marketPlaceId,String time ) {
        List<String> list =null;
        list = amazonOrderDetailMapper.selectAmazonItemASINList(plAccount,marketPlaceId,time);
        return list;
    }

    @Override
    public void updatePlProcessStatus(Byte converSysDetailStatus, String sourceOrderLineItemId) {
        amazonOrderDetailMapper.updatePlProcessStatus(converSysDetailStatus, sourceOrderLineItemId);
    }

    /**
     * 根据亚马逊平台SKU集合查询待转入或转入失败的亚马逊订单（已支付）集合 wujiachuang
     * @param platformSKUList  亚马逊平台SKU集合
     * @return
     */
    @Override
    public List<SysOrderDTO> getPendingConverAmazonListByCondition(List<String> platformSKUList) {
        logger.info("新增或保存映射触发转单拿到到SKU集合："+ FastJsonUtils.toJsonString(platformSKUList));
        List<AmazonOrder> list = new ArrayList<>();
        List<AmazonOrderDetail> amazonOrderDetails = amazonOrderDetailMapper.selectAmazonOrderDetailBySkus(platformSKUList);
        logger.info("查出待转入或者转入失败的亚马逊订单项集合：" + FastJsonUtils.toJsonString(amazonOrderDetails));
        if (CollectionUtils.isEmpty(amazonOrderDetails)) {
            return null;
        }
        Map<String, List<AmazonOrderDetail>> collect = amazonOrderDetails.stream().collect(Collectors.groupingBy(AmazonOrderDetail::getOrderId));
        for (String orderId : collect.keySet()) {
            if (StringUtils.isNotBlank(orderId)) {
//                AmazonOrder amazonOrder = amazonOrderMapper.onlyQeryAmazonOrderByOrderId(orderId);
                AmazonOrder amazonOrder = amazonOrderMapper.getAmazonOrderDetailByOrderId(orderId);
                if (amazonOrder != null) {
//                    amazonOrder.setAmazonOrderDetails(collect.get(orderId));
                    list.add(amazonOrder);
                }
            }
        }
        logger.info("进入映射前的数据：" + FastJsonUtils.toJsonString(list));
        return CollectionUtils.isNotEmpty(list)?amazonOrderService.getSysOrderList(list):null;
    }




    /* @Override
    *//*
     * 循环亚马逊订单ID来查询订单商品列表并持久化到数据库，有nextToken的再循环查询下一页订单商品列表并持久化到数据库
     * *//*
    public List<AmazonOrderDetail> GetOrderItemsByOrderIdAndInsertDb(List<AmazonOrder> lists,String sellerId,String
            mwsAuthToken ,String markerPlaceId,String plAccount)
            throws
            Exception {
        *//*List<AmazonOrderDetail> list = new ArrayList<>();
        if (lists.size() == 0) {
            return null;
        }
        int amazonOrderDetailscount = 0;
        for (AmazonOrder amazonOrder : lists) {
            String orderId = amazonOrder.getOrderId();
            if (amazonOrderDetailscount >= 30) {
                Thread.sleep(2000);
            }
            ListOrderItemsResponse listOrderItemsResponse = ListOrderItemsSample.getListOrderItemsResponse(orderId,sellerId,mwsAuthToken);
            List<OrderItem> orderItems = listOrderItemsResponse.getListOrderItemsResult().getOrderItems();
            List<AmazonOrderDetail> amazonOrderDetails = setAmazonOrderAndInsertDb(orderItems, orderId, sellerId, markerPlaceId, plAccount);
            list.addAll(amazonOrderDetails);
            amazonOrderDetailscount++;
            String nextToken = listOrderItemsResponse.getListOrderItemsResult().getNextToken();
            while (nextToken != null) {
                if (amazonOrderDetailscount >= 30) {
                    Thread.sleep(2000);
                }
                ListOrderItemsByNextTokenResponse listOrderItemsByNextTokenResponse =
                        ListOrderItemsByNextTokenSample.getListOrderItemsByNextTokenResponse(nextToken,sellerId,mwsAuthToken);
                List<OrderItem> orderItems1 = listOrderItemsByNextTokenResponse.getListOrderItemsByNextTokenResult()
                        .getOrderItems();
                List<AmazonOrderDetail> list1 = setAmazonOrderAndInsertDb(orderItems1, orderId, sellerId, markerPlaceId, plAccount);
                list.addAll(list1);
                nextToken = listOrderItemsByNextTokenResponse.getListOrderItemsByNextTokenResult()
                        .getNextToken();
                amazonOrderDetailscount++;
            }

        }
        return  list;*//*
    }*/



    /*
     * 设置AmazonOrderDetail对象并持久化到数据库
     * */
    public  List<AmazonOrderDetail> setAmazonOrderAndInsertDb(List<OrderItem> orderItems, String
            orderId ,String sellerId,String markerPlaceId,String plAccount) {
        List<AmazonOrderDetail> list = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            AmazonOrderDetail amazonOrderDetail = new AmazonOrderDetail();
            amazonOrderDetail.setOrderId(orderId);//订单ID

            amazonOrderDetail.setPlSellerAccount(plAccount);
            amazonOrderDetail.setMarketplaceId(markerPlaceId);
            amazonOrderDetail.setAmazonSellerAccount(sellerId);

            String orderItemId = orderItem.getOrderItemId();//订单项ID
            amazonOrderDetail.setAmazonOrderitemId(orderItemId);

            String asin = orderItem.getASIN();
            amazonOrderDetail.setAsin(asin);//asin码

            String sellerSKU = orderItem.getSellerSKU();//平台SKU
            amazonOrderDetail.setPlatformSku(sellerSKU);

            String title = orderItem.getTitle();
            amazonOrderDetail.setItemTitle(title);  //商品名

            if (orderItem.getItemPrice() != null) {
                String currencyCode = orderItem.getItemPrice().getCurrencyCode();
                amazonOrderDetail.setItemCurrencyCode(currencyCode);//商品金额货币
            }else{
                amazonOrderDetail.setItemCurrencyCode("");
            }

            int quantityOrdered = orderItem.getQuantityOrdered();//商品数量
            amazonOrderDetail.setQuantity(quantityOrdered);

            if (orderItem.getItemPrice() != null) {
                String amount = orderItem.getItemPrice().getAmount();
                double d = Double.parseDouble(amount);
                BigDecimal bigDecimal = new BigDecimal(d/quantityOrdered);
                amazonOrderDetail.setItemPrice(bigDecimal);  //商品单价
            }else{
                amazonOrderDetail.setItemPrice(BigDecimal.valueOf(0));
            }

            if (orderItem.getShippingPrice() != null) {
                String amount1 = orderItem.getShippingPrice().getAmount();
                BigDecimal bigDecimal2 = new BigDecimal(amount1);
                amazonOrderDetail.setShippingPrice(bigDecimal2);//商品运费

                String currencyCode1 = orderItem.getShippingPrice().getCurrencyCode();
                amazonOrderDetail.setShippingCurrencyCode(currencyCode1);//商品运费货币
            }else{
                amazonOrderDetail.setShippingPrice(BigDecimal.valueOf(0));//商品运费
                amazonOrderDetail.setShippingCurrencyCode("");//商品运费货币
            }

            String conditionNote = orderItem.getConditionNote();
            amazonOrderDetail.setConditionNote(conditionNote);//卖家商品备注
            System.out.println(amazonOrderDetail);

            list.add(amazonOrderDetail);
        }
        return list;
    }



}
