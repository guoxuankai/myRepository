package com.rondaful.cloud.seller.common.mws.intface;


import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.seller.common.mws.*;
import com.rondaful.cloud.seller.common.mws.model.*;
import com.rondaful.cloud.seller.common.task.AmazonRequestReportResult;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdDeveloper;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 返回报告内容及所返回报告正文的 Content-MD5 标头。
 */
@Component
public class GetReport {

    private static Logger logger = LoggerFactory.getLogger(GetReport.class);

    /**
     * 返回报告内容
     *
     * @param marketplaceId  站点（商城）ID
     * @param merchant       亚马逊卖家ID
     * @param reportTypeEnum 报告类型
     * @param token          亚马逊卖家授权token
     * @param reportId       报告ID
     * @return 报告请求后信息
     */
    public AmazonRequestReportResult invoke(String marketplaceId, String merchant, ReportTypeEnum reportTypeEnum, String token, String reportId, File file, JSONObject object) throws FileNotFoundException {
        logger.info("返回报告内容，请求人：{}，站点：{}，报告类型：{},报告ID：{}", merchant, marketplaceId, reportTypeEnum.getReportTyp(), reportId);
        GetReportRequest request = new GetReportRequest();
        request.setMerchant( merchant );
        request.setMWSAuthToken(token);
        request.setReportId(reportId);

        OutputStream report = new FileOutputStream( file);
        request.setReportOutputStream( report );

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
        MarketplaceId markModel = MarketplaceIdList.resourceMarketplaceForKeyId().get(marketplaceId);
        MarketplaceIdDeveloper developer = markModel.getMarketplaceIdDeveloper();
        config.setServiceURL(markModel.getUri());

        if(developer == null){
            return new AmazonRequestReportResult("other error","other error","没有开发者账号",HttpStatus.SC_OK);
        }

        //todo 这里版本注意
        String appVersion = KeyValueConts.appVersion;

        MarketplaceWebServiceClientMy service = new MarketplaceWebServiceClientMy(
                developer.getAccessKeyId(), developer.getSecretAccessKey(), developer.getName(), appVersion, config);
        try {
            GetReportResponse response = service.getReport(request,object);
            return new AmazonRequestReportResult(){{
                setHttErrorCode(HttpStatus.SC_OK);
            }};
        }catch (MarketplaceWebServiceException  e){
            MarketplaceWebServiceException exception = (MarketplaceWebServiceException) e.getCause();
            return new AmazonRequestReportResult(exception.getErrorType(),exception.getErrorCode(),exception.getMessage(),HttpStatus.SC_OK);
        }catch (Exception e){
            return new AmazonRequestReportResult("other error","other error",e.getMessage(),HttpStatus.SC_OK);
        }
    }


}
