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
import com.amazonservices.mws.uploadData.common.mws.model.GetFeedSubmissionResultRequest;
import com.amazonservices.mws.uploadData.common.mws.model.GetFeedSubmissionResultResponse;
import com.amazonservices.mws.uploadData.common.mws.model.ResponseMetadata;
import com.amazonservices.mws.uploadData.utils.ClassXmlUtil;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 * Get Feed Submission Result  Samples
 *
 *
 */
public class GetFeedSubmissionResultSample {
//    public static void main(String[] args) {
//        try {
//            AmazonEnvelope amazonEnvelope = GetFeedSubmissionResult("A1RKKUPIHCS9HS", "amzn.mws.08b4ee6f-3369-ad21-22d6-b68fc41178b8", "A1X56E2C9PYKCP", "55127018022");
//            System.out.println(amazonEnvelope);
//            List<AmazonEnvelope.Message> message = amazonEnvelope.getMessage();
//            for (AmazonEnvelope.Message message1 : message) {
//                System.out.println(message1);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(GetFeedSubmissionResultSample.class);
    /**
     * Just add a few required parameters, and try the service
     * Get Feed Submission Result functionality
     *
     * @param args unused
     * @throws FileNotFoundException
     */
    public static AmazonEnvelope GetFeedSubmissionResult(String marketPlaceId, String mwsToken, String sellerId,String
            feedSubmissionId) throws
            FileNotFoundException {

        /************************************************************************
         * Access Key ID and Secret Access Key ID, obtained from:
         * http://aws.amazon.com
         ***********************************************************************/
        MarketplaceId developer = com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList.createMarketplaceForKeyId().get(marketPlaceId);
        String uri = developer.getUri();   //请求地址
        String accessKeyId = developer.getMarketplaceIdDeveloper().getAccessKeyId();// 访问公钥
        String secretAccessKey = developer.getMarketplaceIdDeveloper().getSecretAccessKey();//访问秘钥
        String appName = developer.getMarketplaceIdDeveloper().getName();   //开发者名字


//        final String accessKeyId =MwsDeveloperAccount.accessKey; // "<Your Access Key ID>";
//        final String secretAccessKey = MwsDeveloperAccount.secretKey; // "<Your Secret Access Key>";
//
//        final String appName = MwsDeveloperAccount.appName; //"<Your Application or Company Name>";
        final String appVersion = "2009-01-01"; //"<Your Application Version or Build Number or Release Date>";

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        /************************************************************************
         * Uncomment to set the appropriate MWS endpoint.
         ************************************************************************/
        // US
        config.setServiceURL(uri);
//        config.setServiceURL(MarketplaceIdList.createMarketplace().get(marketPlaceId).getUri());
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

        /************************************************************************
         * Setup request parameters and uncomment invoke to try out
         * sample for Get Feed Submission Result
         ***********************************************************************/

        /************************************************************************
         * Marketplace and Merchant IDs are required parameters for all
         * Marketplace Web Service calls.
         ***********************************************************************/
        final String merchantId = sellerId; //"<Your Merchant ID>";
        final String sellerDevAuthToken = mwsToken;

        GetFeedSubmissionResultRequest request = new GetFeedSubmissionResultRequest();
        request.setMerchant( merchantId );
        request.setMWSAuthToken(sellerDevAuthToken);

        request.setFeedSubmissionId( feedSubmissionId ); //70381017899 70382017899

        // Note that depending on the size of the feed sent in, and the number of errors and warnings,
        // the result can reach sizes greater than 1GB. For this reason we recommend that you _always_
        // program to MWS in a streaming fashion. Otherwise, as your business grows you may silently reach
        // the in-memory size limit and have to re-work your solution.
        //
        String filePath = "/logs/rondaful-order-service/"+ feedSubmissionId +".xml";
        OutputStream processingResult = new FileOutputStream( filePath );//？？？？？？？？

        request.setFeedSubmissionResultOutputStream( processingResult );

        GetFeedSubmissionResultResponse getFeedSubmissionResultResponse = invokeGetFeedSubmissionResult(service, request);
        logger.error("wujiachuang,获取报告");
        try {
            AmazonEnvelope report = ClassXmlUtil.xmlToBean(new FileInputStream(filePath), AmazonEnvelope.class);
            logger.info("打印报告："+report);
            String s = ClassXmlUtil.toXML(report);
            logger.info("转化后的xml文件"+s);
            return  report;
        } catch (Exception e) {
            logger.error("上传数据异常"+e);
            e.printStackTrace();
        }finally {
            // 要清掉这个报告文件，以免积压到服务器，删除时没异常可获
            File reportFile = new File(filePath);
            reportFile.delete();
        }
        return  null;
    }



    /**
     * Get Feed Submission Result  request sample
     * retrieves the feed processing report
     *
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static GetFeedSubmissionResultResponse invokeGetFeedSubmissionResult(MarketplaceWebService service, GetFeedSubmissionResultRequest request) {
        try {

            GetFeedSubmissionResultResponse response = service.getFeedSubmissionResult(request);


            System.out.println ("GetFeedSubmissionResult Action Response");
            System.out.println ("=============================================================================");
            System.out.println ();

            System.out.print("    GetFeedSubmissionResultResponse");
            System.out.println();
            System.out.print("    GetFeedSubmissionResultResult");
            System.out.println();
            System.out.print("            MD5Checksum");
            System.out.println();
            System.out.print("                " + response.getGetFeedSubmissionResultResult().getMD5Checksum());
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

            System.out.println("Feed Processing Result");
            System.out.println ("=============================================================================");
            System.out.println();
            System.out.println( request.getFeedSubmissionResultOutputStream().toString() );
            System.out.println(response.getResponseHeaderMetadata());
            System.out.println();
            System.out.println();
            return response;
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
