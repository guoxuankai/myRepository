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

import com.amazonservices.mws.orders._2013_09_01.*;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsByNextTokenRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsByNextTokenResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ResponseHeaderMetadata;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;


/** Sample call for ListOrderItemsByNextToken. */
public class ListOrderItemsByNextTokenSample {
    private static final String appVersion = "2013-09-01";
    /**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     *
     * @return The response.
     */
    public static ListOrderItemsByNextTokenResponse invokeListOrderItemsByNextToken(
            MarketplaceWebServiceOrders client, 
            ListOrderItemsByNextTokenRequest request) {
        try {
            // Call the service.
            ListOrderItemsByNextTokenResponse response = client.listOrderItemsByNextToken(request);
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

    /**
     *  Command line entry point.
     */
    public  ListOrderItemsByNextTokenResponse getListOrderItemsByNextTokenResponse(String nextToken, String sellerId, String
            mwsAuthToken,String marketPlaceId) {

        MarketplaceId developer = com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList.createMarketplaceForKeyId().get(marketPlaceId);
        String uri = developer.getUri();   //请求地址
        String accessKey = developer.getMarketplaceIdDeveloper().getAccessKeyId();// 访问公钥
        String secretKey = developer.getMarketplaceIdDeveloper().getSecretAccessKey();//访问秘钥
        String appName = developer.getMarketplaceIdDeveloper().getName();   //开发者名字

        // Get a client connection.
        // Make sure you've set the variables in MarketplaceWebServiceOrdersSampleConfig.
//        MarketplaceWebServiceOrdersClient client = MarketplaceWebServiceOrdersSampleConfig.getClient();
        MarketplaceWebServiceOrdersConfig config = new MarketplaceWebServiceOrdersConfig();
        config.setServiceURL(uri);

        // Set other client connection configurations here.
        MarketplaceWebServiceOrdersClient   client = new MarketplaceWebServiceOrdersAsyncClient(accessKey, secretKey,
                appName, appVersion, config, null);

        // Create a request.
        ListOrderItemsByNextTokenRequest request = new ListOrderItemsByNextTokenRequest();
//        String sellerId = "A2MTJNRZNNI2TD";
        request.setSellerId(sellerId);
//        String mwsAuthToken = "example";
        request.setMWSAuthToken(mwsAuthToken);
//        String nextToken = "example";
        request.setNextToken(nextToken);

        // Make the call.
        ListOrderItemsByNextTokenResponse listOrderItemsByNextTokenResponse = ListOrderItemsByNextTokenSample.invokeListOrderItemsByNextToken(client, request);
        return listOrderItemsByNextTokenResponse;

    }

}
