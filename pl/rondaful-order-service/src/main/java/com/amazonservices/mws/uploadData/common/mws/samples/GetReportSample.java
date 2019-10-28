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

import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebService;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceClient;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceConfig;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceException;
import com.amazonservices.mws.uploadData.common.mws.model.GetReportRequest;
import com.amazonservices.mws.uploadData.common.mws.model.GetReportResponse;
import com.amazonservices.mws.uploadData.common.mws.model.ResponseMetadata;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * Get Report  Samples
 *
 *
 */
public class GetReportSample {

    /**
     * Just add a few required parameters, and try the service
     * Get Report functionality
     *
     * @param args unused
     * @throws FileNotFoundException 
     */
    public static void main(String... args) throws IOException {

        /************************************************************************
         * Access Key ID and Secret Access Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
       

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        /************************************************************************
         * Uncomment to set the appropriate MWS endpoint.
         ************************************************************************/
        // US

        // UK
        // config.setServiceURL("https://mws.amazonservices.co.uk/");
        // Germany
        // config.setServiceURL("https://mws.amazonservices.de/");
        // France
        // config.setServiceURL("https://mws.amazonservices.fr/");
        // Italy
        // config.setServiceURL("https://mws.amazonservices.it/");
        // Japan
        // config.setServiceURL("https://mws.amazonservices.jp/");
        // China
        // config.setServiceURL("https://mws.amazonservices.com.cn/");
        // Canada
        // config.setServiceURL("https://mws.amazonservices.ca/");
        // India
        // config.setServiceURL("https://mws.amazonservices.in/");
        // config.setServiceURL("https://mws-eu.amazonservices.com");
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
//         final String accessKeyId = KeyValueConts.accessKeyId;
//         final String secretAccessKey = KeyValueConts.secretAccessKey;
//
//         final String appName = KeyValueConts.appName;//"<Your Application or Company Name>";
//         final String appVersion = KeyValueConts.appVersion; //"<Your Application Version or Build Number or Release Date>";
        MarketplaceId developer = com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList.createMarketplaceForKeyId().get("A1PA6795UKMFR9");
        String uri = developer.getUri();   //请求地址
        String accessKey = developer.getMarketplaceIdDeveloper().getAccessKeyId();// 访问公钥
        String secretKey = developer.getMarketplaceIdDeveloper().getSecretAccessKey();//访问秘钥
        String apName = developer.getMarketplaceIdDeveloper().getName();   //开发者名字

        config.setServiceURL(uri);
        final String accessKeyId = accessKey;
        final String secretAccessKey =secretKey;

        final String appName = apName;//"<Your Application or Company Name>";
        final String appVersion = "2009-01-01"; //"<Your Application Version or Build Number or Release Date>";
        MarketplaceWebService service = new MarketplaceWebServiceClient(
                accessKeyId, secretAccessKey, appName, appVersion, config);

        /************************************************************************
         * Setup request parameters and uncomment invoke to try out 
         * sample for Get Report 
         ***********************************************************************/

        /************************************************************************
         * Marketplace and Merchant IDs are required parameters for all 
         * Marketplace Web Service calls.
         ***********************************************************************/
        final String merchantId = "A3UPZ1E6WSR9P";
        final String sellerDevAuthToken = "amzn.mws.c6b5bcce-17c4-ca9c-dca6-08c1c5476cf8";

        GetReportRequest request = new GetReportRequest();
        request.setMerchant( merchantId );
        request.setMarketplace("A1PA6795UKMFR9");
        request.setMWSAuthToken(sellerDevAuthToken);
        request.setReportId("17072148918018097");
        
        // Note that depending on the type of report being downloaded, a report can reach 
        // sizes greater than 1GB. For this reason we recommend that you _always_ program to
        // MWS in a streaming fashion. Otherwise, as your business grows you may silently reach
        // the in-memory size limit and have to re-work your solution.

//         OutputStream report = new FileOutputStream( "report.xml" );
//         request.setReportOutputStream( report );


        String filePath = "/logs/rondaful-order-service/"+ 1 +".xml";
        OutputStream processingResult = new FileOutputStream( filePath );//？？？？？？？？

        request.setReportOutputStream(processingResult);
        GetReportResponse response =  invokeGetReport(service, request);

        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Superhero\\Desktop\\1..txt");
        long begin=System.currentTimeMillis();
        FileInputStream fis=new FileInputStream("/logs/rondaful-order-service/"+ 1 +".xml");
        BufferedInputStream bis=new BufferedInputStream(fis);
        BufferedOutputStream bos=new BufferedOutputStream(fileOutputStream);
        int size=0;
        byte[] buffer=new byte[10240];
        while((size=bis.read(buffer))!=-1){
            bos.write(buffer, 0, size);
        }
        //刷新此缓冲的输出流，保证数据全部都能写出
        bos.flush();
        bis.close();
        bos.close();
        long end=System.currentTimeMillis();
        System.out.println("使用缓冲输出流和缓冲输入流实现文件的复制完毕！耗时："+(end-begin)+"毫秒");
        // 要清掉这个报告文件，以免积压到服务器，删除时没异常可获
        File reportFile = new File(filePath);
        reportFile.delete();
    }



    /**
     * Get Report  request sample
     * The GetReport operation returns the contents of a report. Reports can potentially be
     * very large (>100MB) which is why we only return one report at a time, and in a
     * streaming fashion.
     *   
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static GetReportResponse invokeGetReport(MarketplaceWebService service, GetReportRequest request) {
        try {

            GetReportResponse response = service.getReport(request);
             
            if(true) {
            	return response;
            }
            System.out.println ("GetReport Action Response");
            System.out.println ("=============================================================================");
            System.out.println ();

            System.out.print("    GetReportResponse");
            System.out.println();
            System.out.print("    GetReportResult");
            System.out.println();
            System.out.print("            MD5Checksum");
            System.out.println();
            System.out.print("                " + response.getGetReportResult().getMD5Checksum());
            System.out.println();
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

            System.out.println("Report");
            System.out.println ("=============================================================================");
            System.out.println();
            System.out.println( request.getReportOutputStream().toString() );
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
		return null;
    }

}
