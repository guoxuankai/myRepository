/*******************************************************************************
 *  Copyright 2009 Amazon Services.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 *  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 * *****************************************************************************
 *
 *  Marketplace Web Service Java Library
 *  API Version: 2009-01-01
 *  Generated: Wed Feb 18 13:28:48 PST 2009
 *
 */



package com.amazonservices.mws.uploadData.common.mws.samples;

import com.amazonservices.mws.client.MwsUtl;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebService;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceClient;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceConfig;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceException;
import com.amazonservices.mws.uploadData.common.mws.model.*;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.order.entity.Time;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Get Feed Submission List  Samples
 *
 *
 */
public class GetFeedSubmissionListSample {

    /**
     * Just add a few required parameters, and try the service
     * Get Feed Submission List functionality
     *
     * @param args unused
     */
    public static GetFeedSubmissionListResponse GetFeedSubmissionList(String marketPlaceId, String mwsToken, String sellerId, String feedType, Time max, Time min ) {

        /************************************************************************
         * Access Key ID and Secret Access Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
        MarketplaceId developer = com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList.createMarketplaceForKeyId().get(marketPlaceId);
        String uri = developer.getUri();   //请求地址
        String accessKeyId = developer.getMarketplaceIdDeveloper().getAccessKeyId();// 访问公钥
        String secretAccessKey = developer.getMarketplaceIdDeveloper().getSecretAccessKey();//访问秘钥
        String appName = developer.getMarketplaceIdDeveloper().getName();   //开发者名字





//        final String accessKeyId = MwsDeveloperAccount.accessKey; //"<Your Access Key ID>";
//        final String secretAccessKey = MwsDeveloperAccount.secretKey; //"<Your Secret Access Key>";
//
//        final String appName = MwsDeveloperAccount.appName; //"<Your Application or Company Name>";
        final String appVersion = "2009-01-01"; // "<Your Application Version or Build Number or Release Date>";

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        /************************************************************************
         * Uncomment to set the appropriate MWS endpoint.
         ************************************************************************/
         config.setServiceURL(uri);

        /************************************************************************
         * You can also try advanced configuration options. Available options are:
         *
         *  - Signature Version
         *  - Proxy Host and Proxy Port
         *  - User Agent String to be sent to Marketplace Web Service
         *
         ***********************************************************************/

        /************************************************************************
         * Instantiate Http Client Implementation of Marketplace Web Service
         ***********************************************************************/

        MarketplaceWebService service = new MarketplaceWebServiceClient(
                accessKeyId, secretAccessKey, appName, appVersion, config);
        // MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        // MarketplaceWebService service = new MarketplaceWebServiceClient(accessKeyId, secretAccessKey, config);

        /************************************************************************
         * Uncomment to try out Mock Service that simulates Marketplace Web Service
         * responses without calling Marketplace Web Service  service.
         *
         * Responses are loaded from local XML files. You can tweak XML files to
         * experiment with various outputs during development
         *
         * XML files available under com/amazonaws/mws/mock tree
         *
         *
         ***********************************************************************/
        // MarketplaceWebService service = new MarketplaceWebServiceMock();

        /************************************************************************
         * Setup request parameters and uncomment invoke to try out
         * sample for Get Feed Submission List
         ***********************************************************************/
        XMLGregorianCalendar maxTime = MwsUtl.getDTF().newXMLGregorianCalendar();
        maxTime.setYear(max.getYear());
        maxTime.setMonth(max.getMonth());
        maxTime.setDay(max.getDay());
        maxTime.setTime(max.getHour(),max.getMinute(),max.getSecond(),999);
//        maxTime.setTimezone(0);

        XMLGregorianCalendar minTime = MwsUtl.getDTF().newXMLGregorianCalendar();
       /* lastUpdatedAfter.setYear(2018);
        lastUpdatedAfter.setMonth(12);
        lastUpdatedAfter.setDay(10);*/
//        lastUpdatedAfter.setTimezone(0);
        minTime.setYear(min.getYear());
        minTime.setMonth(min.getMonth());
        minTime.setDay(min.getDay());
        minTime.setTime(min.getHour(),min.getMinute(),min.getSecond(),0);
        /************************************************************************
         * Marketplace and Merchant IDs are required parameters for all
         * Marketplace Web Service calls.
         ***********************************************************************/
        final String merchantId = sellerId; //"<Your Merchant ID>";
        final String sellerDevAuthToken =mwsToken;

        GetFeedSubmissionListRequest request = new GetFeedSubmissionListRequest();
        request.setMerchant( merchantId );
        request.setSubmittedToDate(maxTime);
        request.setSubmittedFromDate(minTime);

        request.setMWSAuthToken(sellerDevAuthToken);
        request.setMaxCount(100);
        List<String> list=new ArrayList<>();
        list.add("_POST_ORDER_FULFILLMENT_DATA_");
        request.setFeedTypeList(new TypeList(list));
        // @TODO: set request parameters here

//        request.setFeedSubmissionIdList(new IdList().withId("70087017893"));

        request.setFeedTypeList(new TypeList().withType(feedType));
        return invokeGetFeedSubmissionList(service, request);

    }



    /**
     * Get Feed Submission List  request sample
     * returns a list of feed submission identifiers and their associated metadata
     *
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static GetFeedSubmissionListResponse invokeGetFeedSubmissionList(MarketplaceWebService service, GetFeedSubmissionListRequest request) {
        try {

            GetFeedSubmissionListResponse response = service.getFeedSubmissionList(request);


            System.out.println ("GetFeedSubmissionList Action Response");
            System.out.println ("=============================================================================");
            System.out.println ();

            System.out.print("    GetFeedSubmissionListResponse");
            System.out.println();
            if (response.isSetGetFeedSubmissionListResult()) {
                System.out.print("        GetFeedSubmissionListResult");
                System.out.println();
                GetFeedSubmissionListResult getFeedSubmissionListResult = response.getGetFeedSubmissionListResult();
                if (getFeedSubmissionListResult.isSetNextToken()) {
                    System.out.print("            NextToken");
                    System.out.println();
                    System.out.print("                " + getFeedSubmissionListResult.getNextToken());
                    System.out.println();
                }
                if (getFeedSubmissionListResult.isSetHasNext()) {
                    System.out.print("            HasNext");
                    System.out.println();
                    System.out.print("                " + getFeedSubmissionListResult.isHasNext());
                    System.out.println();
                }
                List<FeedSubmissionInfo> feedSubmissionInfoList = getFeedSubmissionListResult.getFeedSubmissionInfoList();
                for (FeedSubmissionInfo feedSubmissionInfo : feedSubmissionInfoList) {
                    System.out.print("            FeedSubmissionInfo");
                    System.out.println();
                    if (feedSubmissionInfo.isSetFeedSubmissionId()) {
                        System.out.print("                FeedSubmissionId");
                        System.out.println();
                        System.out.print("                    " + feedSubmissionInfo.getFeedSubmissionId());
                        System.out.println();
                    }
                    if (feedSubmissionInfo.isSetFeedType()) {
                        System.out.print("                FeedType");
                        System.out.println();
                        System.out.print("                    " + feedSubmissionInfo.getFeedType());
                        System.out.println();
                    }
                    if (feedSubmissionInfo.isSetSubmittedDate()) {
                        System.out.print("                SubmittedDate");
                        System.out.println();
                        System.out.print("                    " + feedSubmissionInfo.getSubmittedDate());
                        System.out.println();
                    }
                    if (feedSubmissionInfo.isSetFeedProcessingStatus()) {
                        System.out.print("                FeedProcessingStatus");
                        System.out.println();
                        System.out.print("                    " + feedSubmissionInfo.getFeedProcessingStatus());
                        System.out.println();
                    }
                    if (feedSubmissionInfo.isSetStartedProcessingDate()) {
                        System.out.print("                StartedProcessingDate");
                        System.out.println();
                        System.out.print("                    " + feedSubmissionInfo.getStartedProcessingDate());
                        System.out.println();
                    }
                    if (feedSubmissionInfo.isSetCompletedProcessingDate()) {
                        System.out.print("                CompletedProcessingDate");
                        System.out.println();
                        System.out.print("                    " + feedSubmissionInfo.getCompletedProcessingDate());
                        System.out.println();
                    }
                }
            }
            if (response.isSetResponseMetadata()) {
                System.out.print("        ResponseMetadata");
                System.out.println();
                ResponseMetadata responseMetadata = response.getResponseMetadata();
                if (responseMetadata.isSetRequestId()) {
                    System.out.print("            RequestId");
                    System.out.println();
                    System.out.print("                " + responseMetadata.getRequestId());
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println(response.getResponseHeaderMetadata());
            System.out.println();

            return  response;
        } catch (MarketplaceWebServiceException ex) {

            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
        return null;
    }

}
