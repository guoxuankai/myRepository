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

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdDeveloper;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.common.mws.KeyValueConts;
import com.rondaful.cloud.seller.common.mwsProducts.*;
import com.rondaful.cloud.seller.common.mwsProducts.model.*;
import com.sun.org.apache.xerces.internal.dom.DeferredElementDefinitionImpl;
import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


/**
 * Sample call for GetMatchingProductForId.
 */
public class GetMatchingProductForIdSample {

    /**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     * @return The response.
     */
    public static GetMatchingProductForIdResponse invokeGetMatchingProductForId(
            MarketplaceWebServiceProducts client,
            GetMatchingProductForIdRequest request) {
        try {
            // Call the service.
            GetMatchingProductForIdResponse response = client.getMatchingProductForId(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            List<GetMatchingProductForIdResult> getMatchingProductForIdResult = response.getGetMatchingProductForIdResult();
/*            System.out.println(JSONObject.toJSONString(getMatchingProductForIdResult));
            ProductList products = getMatchingProductForIdResult.get(1).getProducts();
            List<Product> product = products.getProduct();
            Product product1 = product.get(0);
            RelationshipList relationships = product1.getRelationships();
            Object o = relationships.getAny().get(0);
            DeferredElementNSImpl obj;
            // if(o instanceof DeferredElementDefinitionImpl){
            obj = (DeferredElementNSImpl) o;
            System.out.println(JSONObject.toJSONString(obj));
            String localName = obj.getLocalName();
            String nodeValue = obj.getFirstChild().getFirstChild().getFirstChild().getNextSibling().getFirstChild().getNodeValue();*/



            for(GetMatchingProductForIdResult result: getMatchingProductForIdResult){
                Product product2 = result.getProducts().getProduct().get(0);
                List<Object> any = product2.getRelationships().getAny();
                if(any == null || any.size() == 0){
                    System.out.println("单属性");
                }else {
                    DeferredElementNSImpl ob = (DeferredElementNSImpl)any.get(0);
                    String localName1 = ob.getLocalName();         //关系名称
                    String nodeValue1 = ob.getFirstChild().getFirstChild().getFirstChild().getNextSibling().getFirstChild().getNodeValue();  //关系的asin
                    System.out.println("关系类型：" + localName1 + " 对应asin值 " + nodeValue1);
                }
                // 去其中的 asin   SmallImage   PartNumber
/*                DeferredElementNSImpl ob = (DeferredElementNSImpl)product2.getAttributeSets().getAny().get(0);
                String PartNumber = ob.getElementsByTagName("ns2:PartNumber").item(0).getFirstChild().getNodeValue();
                String SmallImage = ob.getElementsByTagName("ns2:SmallImage").item(0).getFirstChild().getFirstChild().getNodeValue();
                String asin = product2.getIdentifiers().getMarketplaceASIN().getASIN();*/



            }











            // We recommend logging every the request id and timestamp of every call.
            System.out.println("Response:");
            System.out.println("RequestId: " + rhmd.getRequestId());
            System.out.println("Timestamp: " + rhmd.getTimestamp());
            String responseXml = response.toXML();
            System.out.println(responseXml);
            return response;
        } catch (MarketplaceWebServiceProductsException ex) {
            // Exception properties are important for diagnostics.
            System.out.println("Service Exception:");
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if (rhmd != null) {
                System.out.println("RequestId: " + rhmd.getRequestId());
                System.out.println("Timestamp: " + rhmd.getTimestamp());
            }
            System.out.println("Message: " + ex.getMessage());
            System.out.println("StatusCode: " + ex.getStatusCode());
            System.out.println("ErrorCode: " + ex.getErrorCode());
            System.out.println("ErrorType: " + ex.getErrorType());
            throw ex;
        }
    }

    /**
     * Command line entry point.
     */
    public static void main(String[] args) {

        IdListType idList = new IdListType();

        ArrayList<String> ids = new ArrayList<>();

        String sellerId = "A3UPZ1E6WSR9P";
        String marketplaceId = "A1RKKUPIHCS9HS";
        String mwsAuthToken = "amzn.mws.c6b5bcce-17c4-ca9c-dca6-08c1c5476cf8";
        //ids.add("8ytdBvWveTwUv2eZ7xPv27kGn82T");  // B07T7FTTYZ   8ytdBvWveTwUv2eZ7xPv27kGn82T
        //ids.add("8ytdBvWveTwUv2eZ7xPv27kGn82T1");  //B07RWC8QSJ   8ytdBvWveTwUv2eZ7xPv27kGn82T1
        //ids.add("852180CFAA6E413B9801BC85561FFD91");  //B07RS31PFS  852180CFAA6E413B9801BC85561FFD91
        ids.add("B07JGCNWG7");
        //ids.add("E7110A1951EF4541B8ED1435DF1EAC7B");
        //ids.add("1399|A-2-55230FF3|512");

 /*       ids.add("lx19021612110");
        ids.add("jgz9021611040");
        ids.add("lx9021613420");
        ids.add("jgz9021614180");*/
        //strings.add("jgz9021614440");
        //strings.add("jgz9030518370");
        String idType = KeyValueConts.amazonProductIdTye.ASIN.getType();
        idList.setId(ids);

        GetMatchingProductForIdRequest request = new GetMatchingProductForIdRequest();
        request.setIdType(idType);
        request.setSellerId(sellerId);
        request.setMWSAuthToken(mwsAuthToken);
        request.setMarketplaceId(marketplaceId);
        request.setIdList(idList);

        MarketplaceId markModel = MarketplaceIdList.resourceMarketplaceForKeyId().get(marketplaceId);
        MarketplaceIdDeveloper developer = markModel.getMarketplaceIdDeveloper();
        MarketplaceWebServiceProductsConfig config = new MarketplaceWebServiceProductsConfig();
        config.setServiceURL(markModel.getUri());
        MarketplaceWebServiceProductsClient client = new MarketplaceWebServiceProductsAsyncClient(developer.getAccessKeyId(), developer.getSecretAccessKey(),
                developer.getName(), KeyValueConts.appVersion, config, null);

        int i = 100;
        for (; i > 0; i--) {
            try {
                GetMatchingProductForIdSample.invokeGetMatchingProductForId(client, request);
                // Thread.sleep(100L);
            } catch (Exception ignored) {

            }
        }
    }

}
