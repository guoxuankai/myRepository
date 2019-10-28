package com.rondaful.cloud.seller.common.mws.intface;




import com.rondaful.cloud.seller.common.mws.*;
import com.rondaful.cloud.seller.common.mws.model.IdList;
import com.rondaful.cloud.seller.common.mws.model.RequestReportRequest;
import com.rondaful.cloud.seller.common.mws.model.RequestReportResponse;
import com.rondaful.cloud.seller.common.task.AmazonRequestReportResult;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdDeveloper;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.enums.ReportTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


/**
 * 提交创建报告
 */
@Component
public class RequestReport {

    private static Logger logger = LoggerFactory.getLogger(RequestReport.class);



    /**
     * 请求亚马逊生成报告
     * @param marketplaceId 站点（商城）ID
     * @param merchant 亚马逊卖家ID
     * @param reportTypeEnum 报告类型
     * @param token 亚马逊卖家授权token
     * @return 报告请求后信息
     */
    public AmazonRequestReportResult invoke(String marketplaceId, String merchant, ReportTypeEnum reportTypeEnum, String token){
        logger.info("开始向亚马逊请求生成报告，请求人：{}，站点：{}，报告类型：{}",merchant,marketplaceId,reportTypeEnum.getReportTyp());
        ArrayList<String> objects = new ArrayList<>();
        objects.add(marketplaceId);
        IdList idList = new IdList(objects);
        RequestReportRequest request = new RequestReportRequest()
                .withMerchant(merchant)
                .withMarketplaceIdList(idList)
                .withReportType(reportTypeEnum.getReportTyp())
                .withMWSAuthToken(token);

        MarketplaceId markModel = MarketplaceIdList.resourceMarketplaceForKeyId().get(marketplaceId);
        MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
        config.setServiceURL(markModel.getUri());

        MarketplaceIdDeveloper developer = markModel.getMarketplaceIdDeveloper();
        if(developer == null){
            return new AmazonRequestReportResult("other error","other error","没有开发者账号",HttpStatus.SC_OK);
        }

        //todo 这里版本注意
        String appVersion = KeyValueConts.appVersion;

        MarketplaceWebService service = new MarketplaceWebServiceClient(
                developer.getAccessKeyId(), developer.getSecretAccessKey(), developer.getName(), appVersion, config);
        try {
            RequestReportResponse response = service.requestReport(request);
            String reportRequestId = response.getRequestReportResult().getReportRequestInfo().getReportRequestId();
            if(StringUtils.isBlank(reportRequestId)){
                return new AmazonRequestReportResult(){{
                    setHttErrorCode(HttpStatus.SC_OK);
                    setErrorCode("no id");
                    setErrorType("no id");
                }};
            }
            return new AmazonRequestReportResult(){{
                setHttErrorCode(HttpStatus.SC_OK);
                setResultId(reportRequestId);
            }};
        }catch (MarketplaceWebServiceException e){
            logger.error("请求生成亚马逊报告官方异常",e);
            return new AmazonRequestReportResult(e.getErrorType(),e.getErrorCode(),e.getMessage(),HttpStatus.SC_OK);
        }catch (Exception e){
            logger.error("请求生成亚马逊报告其他异常",e);
            return new AmazonRequestReportResult("other error","other error",e.getMessage(),HttpStatus.SC_OK);
        }
    }











}
