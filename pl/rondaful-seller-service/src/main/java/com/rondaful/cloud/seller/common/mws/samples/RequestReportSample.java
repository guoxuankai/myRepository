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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.common.mws.*;
import com.rondaful.cloud.seller.common.mws.model.*;
import com.rondaful.cloud.seller.common.mws.mock.MarketplaceWebServiceMock;

/**
 *
 * Request Report  Samples
 *
 *
 */
public class RequestReportSample {

    /**


     * @param args unused
     */
    public static void main(String... args) {

        Map<String, MarketplaceId> marketplace = MarketplaceIdList.resourceMarketplaceForKeyId();;
        MarketplaceId marketplaceId = marketplace.get("A13V1IB3VIYZZH");
        final String accessKeyId = marketplaceId.getMarketplaceIdDeveloper().getAccessKeyId();
        final String secretAccessKey = marketplaceId.getMarketplaceIdDeveloper().getSecretAccessKey();
        final String appName = marketplaceId.getMarketplaceIdDeveloper().getName();
        final String appVersion = KeyValueConts.appVersion;

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        config.setServiceURL(marketplaceId.getUri());

        MarketplaceWebService service = new MarketplaceWebServiceClient(
                accessKeyId, secretAccessKey, appName, appVersion, config);


        final String sellerDevAuthToken = "amzn.mws.68b5be6e-7be3-75e7-15d0-3d9488fcaa02";

        ArrayList<String> strings = new ArrayList<>();
        strings.add(marketplaceId.getMarketplaceId());
        //strings.add("A1F83G8C2ARO7P");
        IdList idList = new IdList(strings);

        RequestReportRequest request = new RequestReportRequest()
                .withMerchant("AYLBUETAE26AR")
                .withMarketplaceIdList(idList)
                //.withReportType("_GET_MERCHANT_LISTINGS_DATA_")
                .withReportType("_GET_MERCHANT_LISTINGS_ALL_DATA_")
                // .withReportOptions("ShowSalesChannel=true");
                .withMWSAuthToken(sellerDevAuthToken);

        // demonstrates how to set the date range
        DatatypeFactory df = null;
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
/*		XMLGregorianCalendar startDate = df
				.newXMLGregorianCalendar(new GregorianCalendar(2019, 4, 16));
        XMLGregorianCalendar endDate = df
                .newXMLGregorianCalendar(new GregorianCalendar(2019, 4, 17));
		request.setStartDate(startDate);*/
        //request.setEndDate(endDate);

        // @TODO: set additional request parameters here
        //request.setEndDate(startDate);
        invokeRequestReport(service, request);
    }



    /**
     * Request Report  request sample
     * requests the generation of a report
     *
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static void invokeRequestReport(MarketplaceWebService service, RequestReportRequest request) {
        try {

            RequestReportResponse response = service.requestReport(request);


            System.out.println ("RequestReport Action Response");
            System.out.println ("=============================================================================");
            System.out.println ();

            System.out.print("    RequestReportResponse");
            System.out.println();
            if (response.isSetRequestReportResult()) {
                System.out.print("        RequestReportResult");
                System.out.println();
                RequestReportResult  requestReportResult = response.getRequestReportResult();
                if (requestReportResult.isSetReportRequestInfo()) {
                    System.out.print("            ReportRequestInfo");
                    System.out.println();
                    ReportRequestInfo  reportRequestInfo = requestReportResult.getReportRequestInfo();
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
