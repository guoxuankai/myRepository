/*******************************************************************************
 * Copyright 2009-2017 Amazon Services. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************
 * Marketplace Web Service Products
 * API Version: 2011-10-01
 * Library Version: 2017-03-22
 * Generated: Wed Mar 22 23:24:32 UTC 2017
 */
package com.amazonservices.mws.products.samples;

import com.amazonservices.mws.MarketplaceIdList;
import com.amazonservices.mws.products.*;
import com.amazonservices.mws.products.model.ASINListType;
import com.amazonservices.mws.products.model.GetMatchingProductRequest;
import com.amazonservices.mws.products.model.GetMatchingProductResponse;
import com.amazonservices.mws.products.model.ResponseHeaderMetadata;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;

import java.util.List;


/** Sample call for GetMatchingProduct. */
public class GetMatchingProductSample {
    public static  String serviceUrl;
    private static final String appVersion = "2011-10-01";
    /**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     *
     * @return The response.
     */
    public static GetMatchingProductResponse invokeGetMatchingProduct(
            MarketplaceWebServiceProducts client,
            GetMatchingProductRequest request) {
        try {
            // Call the service.
            GetMatchingProductResponse response = client.getMatchingProduct(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            System.out.println("Response:");
            System.out.println("RequestId: "+rhmd.getRequestId());
            System.out.println("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            System.out.println(responseXml);
            return response;
        } catch (MarketplaceWebServiceProductsException ex) {
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
    public  GetMatchingProductResponse get(List<String> list,String sellerId,String marketplaceId,String mwsAuthToken) {
        MarketplaceId developer = com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList.createMarketplaceForKeyId().get(marketplaceId);
        String uri = developer.getUri();   //请求地址
        String accessKey = developer.getMarketplaceIdDeveloper().getAccessKeyId();// 访问公钥
        String secretKey = developer.getMarketplaceIdDeveloper().getSecretAccessKey();//访问秘钥
        String appName = developer.getMarketplaceIdDeveloper().getName();   //开发者名字
        serviceUrl =MarketplaceIdList.createMarketplace().get(marketplaceId).getUri();
                // Get a client connection.
                // Make sure you've set the variables in MarketplaceWebServiceProductsSampleConfig.
//                MarketplaceWebServiceProductsClient client = MarketplaceWebServiceProductsSampleConfig.getClient();

        MarketplaceWebServiceProductsConfig config = new MarketplaceWebServiceProductsConfig();
        config.setServiceURL(GetMatchingProductSample.serviceUrl);
        // Set other client connection configurations here.
        MarketplaceWebServiceProductsClient client = new MarketplaceWebServiceProductsAsyncClient(accessKey, secretKey,
                appName, appVersion, config, null);

        // Create a request.
        GetMatchingProductRequest request = new GetMatchingProductRequest();
//        String sellerId = ;
        request.setSellerId(sellerId);
//        String mwsAuthToken = "example";
        request.setMWSAuthToken(mwsAuthToken);
//        String marketplaceId = "A1F83G8C2ARO7P";
        request.setMarketplaceId(marketplaceId);
 /*       List<String> asins = new ArrayList<>();
        asins.add(asin);*/
        ASINListType asinList = new ASINListType();
        asinList.setASIN(list);
        request.setASINList(asinList);

        // Make the call.
        GetMatchingProductResponse getMatchingProductResponse = GetMatchingProductSample.invokeGetMatchingProduct(client, request);
      /*  for (GetMatchingProductResult getMatchingProductResult : getMatchingProductResponse.getGetMatchingProductResult()) {
            String s = getMatchingProductResult.getProduct().getAttributeSets().toXML();
        }*/
        return getMatchingProductResponse;

    }

}
