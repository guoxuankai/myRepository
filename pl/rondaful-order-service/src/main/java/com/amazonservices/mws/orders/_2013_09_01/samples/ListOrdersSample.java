/*******************************************************************************
 * Copyright 2009-2018 Amazon Services. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************
 * Marketplace Web Service Orders
 * API Version: 2013-09-01
 * Library Version: 2018-10-31
 * Generated: Mon Oct 22 22:40:32 UTC 2018
 */
package com.amazonservices.mws.orders._2013_09_01.samples;


import com.amazonservices.mws.client.MwsUtl;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrders;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersAsyncClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersConfig;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersException;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ResponseHeaderMetadata;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.order.entity.Time;
import com.rondaful.cloud.order.utils.FastJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Sample call for ListOrders. */
public class ListOrdersSample {
    public  static String xml="";

    public static String serviceUrl;   //请求地址

    private static final String appVersion = "2013-09-01";
    private final static Logger logger = LoggerFactory.getLogger(ListOrdersSample.class);
    /**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     *
     * @return The response.
     */
    public static ListOrdersResponse invokeListOrders(
            MarketplaceWebServiceOrders client, 
            ListOrdersRequest request) throws ParserConfigurationException, IOException, SAXException {
        try {
            // Call the service.
            ListOrdersResponse response = client.listOrders(request);


            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            System.out.println("Response:");
            System.out.println("RequestId: "+rhmd.getRequestId());
            System.out.println("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            xml=responseXml;
            System.out.println(responseXml);
          /*  //创建xStream对象
            XStream xstream = new XStream();
            //将别名与xml名字相对应
            xstream.alias("ListOrdersResponse", ListOrdersResponse.class);
            xstream.alias("ListOrdersResult", com.amazonservices.mws.orders._2013_09_01.AmazonListOrder.ListOrdersResult.class);
            xstream.alias("ResponseMetadata", ResponseMetadata.class);
            xstream.alias("Order", Order.class);
            xstream.alias("OrderTotal", OrderTotal.class);
            xstream.alias("Payment", Payment.class);
            xstream.alias("PaymentExecutionDetail", PaymentExecutionDetail.class);
            xstream.alias("PaymentExecutionDetailItem", PaymentExecutionDetailItem.class);
            xstream.alias("PaymentMethodDetails", PaymentMethodDetails.class);
            xstream.alias("ShippingAddress", ShippingAddress.class);
            com.amazonservices.mws.orders._2013_09_01.AmazonListOrder.ListOrdersResponse response1 =
                    (com.amazonservices.mws.orders._2013_09_01.AmazonListOrder.ListOrdersResponse) xstream.fromXML(responseXml);
            System.out.println(response1);*/
            return response;
        } catch (MarketplaceWebServiceOrdersException ex) {
            logger.error("error info , ErrorCode={} , ErrorType()={}, StatusCode()={}", ex.getErrorCode(),ex.getErrorType(),ex.getStatusCode());
            logger.error("error info , ex.getMessage()={}", ex.getMessage());
            // Exception properties are important for diagnostics.
            System.out.println("Service Exception:");
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
                System.out.println("RequestId: "+rhmd.getRequestId());
                System.out.println("Timestamp: "+rhmd.getTimestamp());
            }
            System.out.println("Message: "+ex.getMessage());
            System.out.println("StatusCode: "+ex.getStatusCode());
            System.out.println("ErrorCode: "+ex.getErrorCode());
            System.out.println("ErrorType: "+ex.getErrorType());
            throw ex;
        }
    }

    /**
     *  Command line entry point.
     */
//    public static ListOrdersResponse getAmazonOrders() throws IOException, SAXException, ParserConfigurationException {
//
//        // Get a client connection.
//        // Make sure you've set the variables in MarketplaceWebServiceOrdersSampleConfig.
//        MarketplaceWebServiceOrdersClient client = MarketplaceWebServiceOrdersSampleConfig.getClient();
//        // Create a request.创建连接
//        ListOrdersRequest request = new ListOrdersRequest();
//        //设置卖家ID
//        String sellerId = "A2MTJNRZNNI2TD";
//        request.setSellerId(sellerId);
//        //设置MWS 授权令牌
////        String mwsAuthToken = "amzn.mws.e6b02cd4-6248-97c5-481c-427a52c58700";
////        request.setMWSAuthToken(mwsAuthToken);
//      /*  XMLGregorianCalendar createdAfter = MwsUtl.getDTF().newXMLGregorianCalendar();
//        request.setCreatedAfter(createdAfter);
//        XMLGregorianCalendar createdBefore = MwsUtl.getDTF().newXMLGregorianCalendar();
//        request.setCreatedBefore(createdBefore);*/
//        XMLGregorianCalendar lastUpdatedAfter = MwsUtl.getDTF().newXMLGregorianCalendar();
////        lastUpdatedAfter.setYear(2017);
////        lastUpdatedAfter.setMonth(5);
////        lastUpdatedAfter.setDay(28);
////        lastUpdatedAfter.setTime(11, 11, 11);
//        lastUpdatedAfter.setYear(2018);
//        lastUpdatedAfter.setMonth(12);
//        lastUpdatedAfter.setDay(10);
//        request.setLastUpdatedAfter(lastUpdatedAfter);
////        XMLGregorianCalendar lastUpdatedBefore = MwsUtl.getDTF().newXMLGregorianCalendar();
////        request.setLastUpdatedBefore(lastUpdatedBefore);
//        List<String> orderStatus = new ArrayList<String>();
//        orderStatus.add("Unshipped");//已付款，待发货状态
//        orderStatus.add("PartiallyShipped");//订单中的一个或多个（但并非全部）商品已经发货。
//        orderStatus.add("Shipped");//订单中的所有商品均已发货
//        //订单中的所有商品均已发货。但是卖家还没有向亚马逊确认已经向买家寄出发票。请注意：此参数仅适用于中国地区。
//        orderStatus.add("InvoiceUnconfirmed");
//        //订单无法进行配送。该状态仅适用于通过亚马逊零售网站之外的渠道下达但由亚马逊进行配送的订单。
//        orderStatus.add("Unfulfillable");
//        request.setOrderStatus(orderStatus);
//        List<String> marketplaceId = new ArrayList<String>();
////        销售站点ID  （美国）
//        marketplaceId.add("A1F83G8C2ARO7P");
//        request.setMarketplaceId(marketplaceId);
//      /*  List<String> fulfillmentChannel = new ArrayList<String>();
//        request.setFulfillmentChannel(fulfillmentChannel);*/
//       /* List<String> paymentMethod = new ArrayList<String>();
//        request.setPaymentMethod(paymentMethod);
//        String buyerEmail = "example";
//        request.setBuyerEmail(buyerEmail);
//        String sellerOrderId = "example";
//        request.setSellerOrderId(sellerOrderId);
//        Integer maxResultsPerPage = 1;
//        request.setMaxResultsPerPage(maxResultsPerPage);
//        List<String> tfmShipmentStatus = new ArrayList<String>();
//        request.setTFMShipmentStatus(tfmShipmentStatus);
//        List<String> easyShipShipmentStatus = new ArrayList<String>();
//        request.setEasyShipShipmentStatus(easyShipShipmentStatus);
//*/
//        // Make the call.
//        ListOrdersResponse listOrdersResponse = ListOrdersSample.invokeListOrders(client, request);
//       /* List<Order> orders = listOrdersResponse.getListOrdersResult().getOrders();
//        System.out.println(orders.size());
//        for (Order order : orders) {
//            System.out.println(order.getAmazonOrderId());
//        }*/
////        System.out.println(listOrdersResponse.getListOrdersResult().getNextToken());
//
//        return listOrdersResponse;
//    }
    public static void main(String[] args) {
        Time time = new Time(2019, 8, 23, 14, 14, 35, 0);
//        ListOrdersResponse amazonOrders = new ListOrdersSample().getAmazonOrders(time, "A3RMQHKBEVQFCF", "ATVPDKIKX0DER", "amzn.mws.84b5cc32-29b8-fb8d-a6b1-6af3d0277607");
    }

    public  ListOrdersResponse getAmazonOrders(Time time,String sellerId, String marketplaceIds, String mwsAuthToken) throws IOException, SAXException, ParserConfigurationException {
        logger.info("进来调用亚马逊API方法,时间：{},卖家ID为{},站点ID为{},token为{}", FastJsonUtils.toJsonString(time),sellerId, marketplaceIds,mwsAuthToken);
        MarketplaceId developer = com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList.createMarketplaceForKeyId().get(marketplaceIds);
             String uri = developer.getUri();   //请求地址
             String accessKey = developer.getMarketplaceIdDeveloper().getAccessKeyId();// 访问公钥
             String secretKey = developer.getMarketplaceIdDeveloper().getSecretAccessKey();//访问秘钥
             String appName = developer.getMarketplaceIdDeveloper().getName();   //开发者名字
//             String uri = "https://mws-eu.amazonservices.com";   //请求地址
//             String accessKey ="AKIAIZO77RV5LL3G5MLQ";// 访问公钥
//             String secretKey = "iYyhXnVqcvWb+Vmow4Utd4024NBXnbvcBoF5qmtJ";//访问秘钥
//             String appName = "colorest";   //开发者名字

//             serviceUrl = MarketplaceIdList.createMarketplace().get(marketplaceIds).getUri();
//             MarketplaceIdDeveloper developer = com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList.createMarketplaceForKeyId().get(marketplaceIds).getMarketplaceIdDeveloper();

             // Get a client connection.
             // Make sure you've set the variables in MarketplaceWebServiceOrdersSampleConfig.
             MarketplaceWebServiceOrdersConfig config = new MarketplaceWebServiceOrdersConfig();
             config.setServiceURL(uri);

             // Set other client connection configurations here.
             MarketplaceWebServiceOrdersClient   client = new MarketplaceWebServiceOrdersAsyncClient(accessKey, secretKey,
                     appName, appVersion, config, null);
        // Get a client connection.
        // Make sure you've set the variables in MarketplaceWebServiceOrdersSampleConfig.
//        MarketplaceWebServiceOrdersClient client = MarketplaceWebServiceOrdersSampleConfig.getClient();
        // Create a request.创建连接
        ListOrdersRequest request = new ListOrdersRequest();
        //设置卖家ID
//        String sellerId = sellerID;
        request.setSellerId(sellerId);
        //设置MWS 授权令牌
//        String mwsAuthToken = "amzn.mws.e6b02cd4-6248-97c5-481c-427a52c58700";
        request.setMWSAuthToken(mwsAuthToken);
      /*  XMLGregorianCalendar createdAfter = MwsUtl.getDTF().newXMLGregorianCalendar();
        request.setCreatedAfter(createdAfter);
        XMLGregorianCalendar createdBefore = MwsUtl.getDTF().newXMLGregorianCalendar();
        request.setCreatedBefore(createdBefore);*/
        XMLGregorianCalendar lastUpdatedAfter = MwsUtl.getDTF().newXMLGregorianCalendar();
       /* lastUpdatedAfter.setYear(2018);
        lastUpdatedAfter.setMonth(12);
        lastUpdatedAfter.setDay(10);*/

        lastUpdatedAfter.setYear(time.getYear());
        lastUpdatedAfter.setMonth(time.getMonth());
        lastUpdatedAfter.setDay(time.getDay());
        lastUpdatedAfter.setTime(time.getHour(),time.getMinute(),time.getSecond(),time.getMilliSecond());
        request.setLastUpdatedAfter(lastUpdatedAfter);
//        XMLGregorianCalendar lastUpdatedBefore = MwsUtl.getDTF().newXMLGregorianCalendar();
//        request.setLastUpdatedBefore(lastUpdatedBefore);
        List<String> orderStatus = new ArrayList<String>();
//        orderStatus.add("PendingAvailability");//预订订单，尚未到商品发售时间，待付款(预订)   仅适用于日本
//        orderStatus.add("Pending");//待付款（标准）
        orderStatus.add("Unshipped");//已付款，待发货状态
        orderStatus.add("PartiallyShipped");//订单中的一个或多个（但并非全部）商品已经发货。
        orderStatus.add("Shipped");//订单中的所有商品均已发货
        //订单中的所有商品均已发货。但是卖家还没有向亚马逊确认已经向买家寄出发票。请注意：此参数仅适用于中国地区。
        orderStatus.add("InvoiceUnconfirmed");
        orderStatus.add("Canceled");//已取消
        //订单无法进行配送。该状态仅适用于通过亚马逊零售网站之外的渠道下达但由亚马逊进行配送的订单。
//        orderStatus.add("Unfulfillable");
        request.setOrderStatus(orderStatus);
        List<String> marketplaceId = new ArrayList<String>();
//        销售站点ID  （美国）
        marketplaceId.add(marketplaceIds);
        request.setMarketplaceId(marketplaceId);
      /*  List<String> fulfillmentChannel = new ArrayList<String>();
        request.setFulfillmentChannel(fulfillmentChannel);*/
       /* List<String> paymentMethod = new ArrayList<String>();
        request.setPaymentMethod(paymentMethod);
        String buyerEmail = "example";
        request.setBuyerEmail(buyerEmail);
        String sellerOrderId = "example";
        request.setSellerOrderId(sellerOrderId);
        Integer maxResultsPerPage = 1;
        request.setMaxResultsPerPage(maxResultsPerPage);
        List<String> tfmShipmentStatus = new ArrayList<String>();
        request.setTFMShipmentStatus(tfmShipmentStatus);
        List<String> easyShipShipmentStatus = new ArrayList<String>();
        request.setEasyShipShipmentStatus(easyShipShipmentStatus);
*/
        // Make the call.
             ListOrdersResponse listOrdersResponse = null;
             try {
                 logger.error("请求亚马逊API参数request：{}", FastJsonUtils.toJsonString(request));
                 logger.error("请求亚马逊API参数client：{}", FastJsonUtils.toJsonString(client));
                 listOrdersResponse = ListOrdersSample.invokeListOrders(client, request);
             } catch (Exception e) {
                 logger.error("调用亚马逊API异常:{}", e);
                 throw e;
             }
       /* List<Order> orders = listOrdersResponse.getListOrdersResult().getOrders();
        System.out.println(orders.size());
        for (Order order : orders) {
            System.out.println(order.getAmazonOrderId());
        }*/
//        System.out.println(listOrdersResponse.getListOrdersResult().getNextToken());

        return listOrdersResponse;
    }

}
