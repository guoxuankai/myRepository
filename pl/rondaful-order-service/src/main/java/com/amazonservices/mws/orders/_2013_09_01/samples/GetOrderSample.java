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

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrders;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersAsyncClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersConfig;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersException;
import com.amazonservices.mws.orders._2013_09_01.model.GetOrderRequest;
import com.amazonservices.mws.orders._2013_09_01.model.GetOrderResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ResponseHeaderMetadata;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;

import java.util.ArrayList;
import java.util.List;


/** Sample call for GetOrder. */
public class GetOrderSample {
    private static final String appVersion = "2013-09-01";
    /**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     *
     * @return The response.
     */
    public static GetOrderResponse invokeGetOrder(
            MarketplaceWebServiceOrders client, 
            GetOrderRequest request) {
        try {
            // Call the service.
            GetOrderResponse response = client.getOrder(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            System.out.println("Response:");
            System.out.println("RequestId: "+rhmd.getRequestId());
            System.out.println("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            System.out.println(responseXml);
            return response;
        } catch (MarketplaceWebServiceOrdersException ex) {
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

    public static void main(String[] args) {
        MarketplaceId developer = com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList.createMarketplaceForKeyId().get("A13V1IB3VIYZZH");
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
        MarketplaceWebServiceOrdersClient client = new MarketplaceWebServiceOrdersAsyncClient(accessKey, secretKey,
                appName, appVersion, config, null);
        // Get a client connection.
        // Make sure you've set the variables in MarketplaceWebServiceOrdersSampleConfig.
//        MarketplaceWebServiceOrdersClient client = MarketplaceWebServiceOrdersSampleConfig.getClient();

        // Create a request.
        GetOrderRequest request = new GetOrderRequest();
        String sellerId = "A3UPZ1E6WSR9P";
        request.setSellerId(sellerId);
        String mwsAuthToken = "amzn.mws.c6b5bcce-17c4-ca9c-dca6-08c1c5476cf8";
        request.setMWSAuthToken(mwsAuthToken);
        List<String> amazonOrderId = new ArrayList<String>();
        amazonOrderId.add("404-3236093-9191566");
        request.setAmazonOrderId(amazonOrderId);

        // Make the call.
        GetOrderSample.invokeGetOrder(client, request);
    }
}
