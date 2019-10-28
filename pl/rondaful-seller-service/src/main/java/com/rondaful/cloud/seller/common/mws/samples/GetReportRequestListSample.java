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



package com.rondaful.cloud.seller.common.mws.samples;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.common.mws.*;
import com.rondaful.cloud.seller.common.mws.model.*;
import com.rondaful.cloud.seller.common.mws.mock.MarketplaceWebServiceMock;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * Get Report Request List  Samples
 *
 *
 */
public class GetReportRequestListSample {

    /**
     * Just add a few required parameters, and try the service
     * Get Report Reques
     *
     *
     * @param args unused
     */
    public static void main(String... args) {

        /************************************************************************
         * Access Key ID and Secret Access Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
        Map<String, MarketplaceId> marketplace = MarketplaceIdList.resourceMarketplaceForKeyId();;
        MarketplaceId marketplaceId = marketplace.get("A1F83G8C2ARO7P");
        final String accessKeyId = marketplaceId.getMarketplaceIdDeveloper().getAccessKeyId();
        final String secretAccessKey = marketplaceId.getMarketplaceIdDeveloper().getSecretAccessKey();
        final String appName = marketplaceId.getMarketplaceIdDeveloper().getName();
        final String appVersion = KeyValueConts.appVersion;

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        config.setServiceURL(marketplaceId.getUri());

        MarketplaceWebService service = new MarketplaceWebServiceClient(
                accessKeyId, secretAccessKey, appName, appVersion, config);


        final String merchantId = "A9O6B3N4CJQ7B";
        final String sellerDevAuthToken = "amzn.mws.b6b5be72-05b3-a66c-8512-9e27de2382eb";

        GetReportRequestListRequest request = new GetReportRequestListRequest();
        request.setMerchant( merchantId );
        request.setMWSAuthToken(sellerDevAuthToken);
        final IdList ReportRequestIdList = new IdList(Arrays.asList(
                "53961018108"));
        request.setReportRequestIdList(ReportRequestIdList);
        request.setReportTypeList(new TypeList(Arrays.asList(KeyValueConts.reportType)));

        //request.setMarketplace("ATVPDKIKX0DER");


        // @TODO: set request parameters here

        invokeGetReportRequestList(service, request);

    }



    /**
     * Get Report Request List  request sample
     * returns a list of report requests ids and their associated metadata
     *
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static void invokeGetReportRequestList(MarketplaceWebService service, GetReportRequestListRequest request) {
        try {

            GetReportRequestListResponse response = service.getReportRequestList(request);
            List<ReportRequestInfo> infoList = response.getGetReportRequestListResult().getReportRequestInfoList();
            ReportRequestInfo reportRequestInfo1 = infoList.get(0);
            XMLGregorianCalendar startedProcessingDate = reportRequestInfo1.getStartedProcessingDate();
            System.out.println(startedProcessingDate);
            System.out.println(JSONObject.toJSONString(startedProcessingDate));
            String s = startedProcessingDate.toString();


            System.out.println ("GetReportRequestList Action Response");
            System.out.println ("=============================================================================");
            System.out.println ();

            System.out.print("    GetReportRequestListResponse");
            System.out.println();
            if (response.isSetGetReportRequestListResult()) {
                System.out.print("        GetReportRequestListResult");
                System.out.println();
                GetReportRequestListResult  getReportRequestListResult = response.getGetReportRequestListResult();
                if (getReportRequestListResult.isSetNextToken()) {
                    System.out.print("            NextToken");
                    System.out.println();
                    System.out.print("                " + getReportRequestListResult.getNextToken());
                    System.out.println();
                }
                if (getReportRequestListResult.isSetHasNext()) {
                    System.out.print("            HasNext");
                    System.out.println();
                    System.out.print("                " + getReportRequestListResult.isHasNext());
                    System.out.println();
                }
                java.util.List<ReportRequestInfo> reportRequestInfoList = getReportRequestListResult.getReportRequestInfoList();
                for (ReportRequestInfo reportRequestInfo : reportRequestInfoList) {
                    System.out.print("            ReportRequestInfo");
                    System.out.println();
                    if(reportRequestInfo.isSetGeneratedReportId())
                    {
                        System.out.print("                GeneratedReportId");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getGeneratedReportId());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetReportRequestId()) {
                        System.out.print("                ReportRequestId");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportRequestId());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetReportType()) {
                        System.out.print("                ReportType");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportType());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetStartDate()) {
                        System.out.print("                StartDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getStartDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetEndDate()) {
                        System.out.print("                EndDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getEndDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetSubmittedDate()) {
                        System.out.print("                SubmittedDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getSubmittedDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetCompletedDate()) {
                        System.out.print("                CompletedDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getCompletedDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetReportProcessingStatus()) {
                        System.out.print("                ReportProcessingStatus");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportProcessingStatus());
                        System.out.println();
                    }
                }
            }
            if (response.isSetResponseMetadata()) {
                System.out.print("        ResponseMetadata");
                System.out.println();
                ResponseMetadata  responseMetadata = response.getResponseMetadata();
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


        } catch (MarketplaceWebServiceException ex) {
            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
    }

}
