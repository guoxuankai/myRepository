package com.rondaful.cloud.seller.common.mws.intface;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.utils.DateUtils;
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

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 返回可用于获取报告的 ReportRequestId 的报告请求列表。
 */
@Component
public class GetReportRequestList {

    private static Logger logger = LoggerFactory.getLogger(GetReportRequestList.class);


    /**
     * 向亚马逊获取报告生成结果
     *
     * @param marketplaceId    站点（商城）ID
     * @param merchant         亚马逊卖家ID
     * @param reportTypeEnum   报告类型
     * @param token            亚马逊卖家授权token
     * @param reportRequestIds reportRequestId列表
     * @return 报告请求后信息
     */
    public List<AmazonRequestReportResult> invoke(String marketplaceId, String merchant, ReportTypeEnum reportTypeEnum, String token, List<String> reportRequestIds) {
        logger.info("开始向亚马逊获取报告生成结果，请求人：{}，站点：{}，报告类型：{},ID列表：{}",merchant,marketplaceId,reportTypeEnum.getReportTyp(),reportRequestIds.toString());
        if(reportRequestIds == null || reportRequestIds.size() == 0)
            return new ArrayList<>();
        GetReportRequestListRequest request = new GetReportRequestListRequest();
        request.setMerchant( merchant);
        IdList idList = new IdList(reportRequestIds);
        request.setReportRequestIdList(idList);
        request.setMWSAuthToken(token);

        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
        MarketplaceId markModel = MarketplaceIdList.resourceMarketplaceForKeyId().get(marketplaceId);
        config.setServiceURL(markModel.getUri());
        config.setMaxAsyncThreads(1);

        MarketplaceIdDeveloper developer = markModel.getMarketplaceIdDeveloper();
        if(developer == null){
            AmazonRequestReportResult result = new AmazonRequestReportResult("other error","other error","没有开发者账号",HttpStatus.SC_OK);
            return new ArrayList<AmazonRequestReportResult>(){{
                add(result);
            }};
        }

        //todo 这里版本注意
        String appVersion = KeyValueConts.appVersion;

        MarketplaceWebService service = new MarketplaceWebServiceClient(developer.getAccessKeyId(), developer.getSecretAccessKey(), developer.getName(), appVersion, config);
        Future<GetReportRequestListResponse> future = service.getReportRequestListAsync(request);
        try {
            GetReportRequestListResponse response = future.get();
            List<ReportRequestInfo> infoList = response.getGetReportRequestListResult().getReportRequestInfoList();
            List<AmazonRequestReportResult> results = new ArrayList<>();
            AmazonRequestReportResult result;
            for(ReportRequestInfo info:infoList){
                   result = new AmazonRequestReportResult();
                   result.setSourceId(info.getReportRequestId());
                   result.setResultId(info.getGeneratedReportId());
                   result.setReportProcessingStatus(info.getReportProcessingStatus());
                   result.setBeginTime( DateUtils.UTCToCST(info.getStartedProcessingDate().toString()));
                   result.setEndTime( DateUtils.UTCToCST(info.getCompletedDate().toString()));
                   results.add(result);
            }
            return results;
        }catch (Exception e){
            logger.error("开始向亚马逊获取报告生成结果异常",e);
            logger.debug("开始向亚马逊获取报告生成结果异常返回数据", JSONObject.toJSONString(future));
            if (e.getCause() instanceof MarketplaceWebServiceException) {
                MarketplaceWebServiceException exception = (MarketplaceWebServiceException) e.getCause();
                AmazonRequestReportResult result = new AmazonRequestReportResult(exception.getErrorType(),exception.getErrorCode(),exception.getMessage(),HttpStatus.SC_OK);
                return new ArrayList<AmazonRequestReportResult>(){{
                    add(result);
                }};
            } else {
                AmazonRequestReportResult result = new AmazonRequestReportResult("other error","other error",e.getMessage(),HttpStatus.SC_OK);
                return new ArrayList<AmazonRequestReportResult>(){{
                    add(result);
                }};
            }
        }
    }






}
