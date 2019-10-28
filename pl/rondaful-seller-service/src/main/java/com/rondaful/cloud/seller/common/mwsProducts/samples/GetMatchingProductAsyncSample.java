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
package com.rondaful.cloud.seller.common.mwsProducts.samples;

import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsAsync;
import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsAsyncClient;
import com.rondaful.cloud.seller.common.mwsProducts.MarketplaceWebServiceProductsException;
import com.rondaful.cloud.seller.common.mwsProducts.model.ASINListType;
import com.rondaful.cloud.seller.common.mwsProducts.model.GetMatchingProductRequest;
import com.rondaful.cloud.seller.common.mwsProducts.model.GetMatchingProductResponse;
import com.rondaful.cloud.seller.common.mwsProducts.model.ResponseHeaderMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/** Sample async call for GetMatchingProduct. */
public class GetMatchingProductAsyncSample {

    /**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     *
     * @return The response.
     */
    public static List<Object> invokeGetMatchingProduct(
            MarketplaceWebServiceProductsAsync client,
            List<GetMatchingProductRequest> requestList) {
        // Call the service async.
        List<Future<GetMatchingProductResponse>> futureList =
            new ArrayList<Future<GetMatchingProductResponse>>();
        for (GetMatchingProductRequest request : requestList) {
            Future<GetMatchingProductResponse> future = 
                client.getMatchingProductAsync(request);
            futureList.add(future);
        }
        List<Object> responseList = new ArrayList<Object>();
        for (Future<GetMatchingProductResponse> future : futureList) {
            Object xresponse;
            try {
                GetMatchingProductResponse response = future.get();
                ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
                // We recommend logging every the request id and timestamp of every call.
                System.out.println("Response:");
                System.out.println("RequestId: "+rhmd.getRequestId());
                System.out.println("Timestamp: "+rhmd.getTimestamp());
                String responseXml = response.toXML();
                System.out.println(responseXml);
                xresponse = response;
            } catch (ExecutionException ee) {
                Throwable cause = ee.getCause();
                if (cause instanceof MarketplaceWebServiceProductsException) {
                    // Exception properties are important for diagnostics.
                    MarketplaceWebServiceProductsException ex = 
                        (MarketplaceWebServiceProductsException)cause;
                    ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
                    System.out.println("Service Exception:");
                    System.out.println("RequestId: "+rhmd.getRequestId());
                    System.out.println("Timestamp: "+rhmd.getTimestamp());
                    System.out.println("Message: "+ex.getMessage());
                    System.out.println("StatusCode: "+ex.getStatusCode());
                    System.out.println("ErrorCode: "+ex.getErrorCode());
                    System.out.println("ErrorType: "+ex.getErrorType());
                    xresponse = ex;
                } else {
                    xresponse = cause;
                }
            } catch (Exception e) {
                xresponse = e;
            }
            responseList.add(xresponse);
        }
        return responseList;
    }

    /**
     *  Command line entry point.
     */
    public static void main(String[] args) {

        // Get a client connection.
        MarketplaceWebServiceProductsAsyncClient client = MarketplaceWebServiceProductsSampleConfig.getAsyncClient();

        // Create a request list.
        List<GetMatchingProductRequest> requestList = new ArrayList<GetMatchingProductRequest>();
        GetMatchingProductRequest request = new GetMatchingProductRequest();
        String sellerId = "example";
        request.setSellerId(sellerId);
        String mwsAuthToken = "example";
        request.setMWSAuthToken(mwsAuthToken);
        String marketplaceId = "example";
        request.setMarketplaceId(marketplaceId);
        ASINListType asinList = new ASINListType();
        request.setASINList(asinList);
        requestList.add(request);

        // Make the calls.
        GetMatchingProductAsyncSample.invokeGetMatchingProduct(client, requestList);

    }

}
