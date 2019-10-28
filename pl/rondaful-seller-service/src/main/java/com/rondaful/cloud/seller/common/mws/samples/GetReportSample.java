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

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.common.mws.*;
import com.rondaful.cloud.seller.common.mws.model.*;
import com.rondaful.cloud.seller.common.mws.mock.MarketplaceWebServiceMock;

/**
 *
 * Get Report  Samples
 *
 *
 */
public class GetReportSample {

    /**
     * Just add a few required parameters, and try the service
     * Get Report
     *
     *
     * @param args unused
     * @throws FileNotFoundException
     */
    public static void main(String... args) throws FileNotFoundException {

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();

        Map<String, MarketplaceId> marketplace = MarketplaceIdList.resourceMarketplaceForKeyId();
        MarketplaceId marketplaceId = marketplace.get("A1VC38T7YXB528");
        final String accessKeyId = marketplaceId.getMarketplaceIdDeveloper().getAccessKeyId();
        final String secretAccessKey = marketplaceId.getMarketplaceIdDeveloper().getSecretAccessKey();

        final String appName = marketplaceId.getMarketplaceIdDeveloper().getName();
        final String appVersion = KeyValueConts.appVersion;
        config.setServiceURL(marketplaceId.getUri());
        MarketplaceWebServiceClientMy service = new MarketplaceWebServiceClientMy(
                accessKeyId, secretAccessKey, appName, appVersion, config);


        GetReportRequest request = new GetReportRequest();
        request.setMerchant("A79IG110B8VR4");
        request.setMWSAuthToken("amzn.mws.a4b52901-f781-dfef-1252-285849f9582c");
        request.setReportId("3109457778018142");   //15820209533018003  3109457778018142

      /*  try {
            String str1 = "wode s是是是测试 ！!";
            byte[] cp1252s = str1.getBytes("Cp1252");
            // 请将 cp1252s 数组转换成没有乱码的字符串
        } catch (Exception e) {

        }*/

        MyInteger myInteger = new MyInteger();
        myInteger.setL(10);
        test(myInteger);


        OutputStream report = new FileOutputStream("report.txt");
        request.setReportOutputStream(report);

        //GetReportResponse response = invokeGetReport(service, request);



        try {
            JSONObject object = new JSONObject();
            GetReportResponse response = service.getReport(request,object);
            File file = new File("report.txt");
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), object.getString("charSet")));

             /*   FileReader fr = new FileReader("report.txt");
                new InputStreamReader(fr,"");
                BufferedReader bf = new BufferedReader(fr);*/
            String str;
            ArrayList<JSONObject> jsons = new ArrayList<>();
            JSONObject json;
            String[] split1 = new String[0];
            int i = 1;
            while ((str = bf.readLine()) != null) {
                String[] split = str.split("\\t");
                if (i == 1) {
                    split1 = split;
                } else {
                    json = new JSONObject();
                    for (int j = 0; j < split.length; j++) {   //Content-Type: text/plain;charset=Cp1252
                        //json.put(split1[j],new String( split[j].getBytes("Cp1252") , StandardCharsets.UTF_8)    );
                        json.put(split1[j], split[j]);
                    }
                    jsons.add(json);
                }
                i++;
            }
            System.out.println(jsons.size() + "   -------------");
            System.out.println(JSONObject.toJSONString(jsons.get(0)));
            System.out.println(JSONObject.toJSONString(jsons));

        } catch (Exception e) {
        }


    }


    private static void test(MyInteger l) {
        l.setL(3);
    }


    public static class MyInteger {
        private Integer l;

        public Integer getL() {
            return l;
        }

        public void setL(Integer l) {
            this.l = l;
        }
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

            if (true) {
                return response;
            }
            System.out.println("GetReport Action Response");
            System.out.println("=============================================================================");
            System.out.println();

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
            System.out.println("=============================================================================");
            System.out.println();
            System.out.println(request.getReportOutputStream().toString());
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
