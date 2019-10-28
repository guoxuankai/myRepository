package com.rondaful.cloud.seller.common.mws.intface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebService;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceClient;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceConfig;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceException;
import com.rondaful.cloud.seller.common.mws.model.GetFeedSubmissionResultRequest;
import com.rondaful.cloud.seller.common.task.AmazonReportListResult;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdDeveloper;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import com.rondaful.cloud.seller.generated.ProcessingReport;
import com.rondaful.cloud.seller.utils.ClassXmlUtil;

/**
 * 获取上报xml的结果报告
 * 
 * @author ouxiangfeng
 *
 */
@Component
public class GetFeedSubmissionListResultReport extends BaseMission {

	private final Logger logger = LoggerFactory.getLogger(GetFeedSubmissionListResultReport.class);

	/**
	 * 执行调用amazon接口
	 * @param countryCode
	 * @param merchantId
	 * @return
	 */
	public List<AmazonReportListResult> invoke(String marketplaceId,final String merchantId,String FeedSubmissionId,String feedType,String mWStoken) {
		
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		MarketplaceId marketplaceIdObj = MarketplaceIdList.createMarketplaceForKeyId().get(marketplaceId);
		MarketplaceIdDeveloper marketplaceIdDeveloper = marketplaceIdObj.getMarketplaceIdDeveloper();
		logger.error("get report marketplace={}",JSON.toJSONString(marketplaceIdObj));
		
		// 获取开发者与站点
        if(marketplaceIdDeveloper == null)
        {
        	logger.error("初始数据中找不到开发者信息,marketplaceIdObj={}",JSON.toJSONString(marketplaceIdDeveloper));
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "站点信息不完整");
        }
        
		config.setServiceURL(marketplaceIdObj.getUri());
		MarketplaceWebService service = new MarketplaceWebServiceClient(marketplaceIdDeveloper.getAccessKeyId(), 
				marketplaceIdDeveloper.getSecretAccessKey(), marketplaceIdDeveloper.getName(),
				BaseMission.appVersion, config);
		
		//////////////// request//////////////////////////
		GetFeedSubmissionResultRequest request = new GetFeedSubmissionResultRequest();
        request.setMerchant( merchantId );
        request.setFeedSubmissionId( FeedSubmissionId ); 
        request.setMWSAuthToken(mWStoken);
        OutputStream processingResult = null;
        
        // liunx 目录 
        String outfileName ="/logs/rondaful-seller-service/report_"+ FeedSubmissionId +"-"+feedType+"-"+merchantId+".xml";
        List<AmazonReportListResult> resultList = new ArrayList<AmazonReportListResult>();
		try {
			processingResult = new FileOutputStream( outfileName);
			request.setFeedSubmissionResultOutputStream( processingResult );
			//GetFeedSubmissionResultResponse response = 
			service.getFeedSubmissionResult(request);
			
			AmazonReportListResult reportRes = new AmazonReportListResult();
			reportRes.setMarketplaceId(marketplaceId);
			reportRes.setMerchantId(merchantId);
			reportRes.setFeedSubmissionId(FeedSubmissionId);
			
			//读取报告
			logger.debug("report xml path:"+outfileName);
			AmazonEnvelope report = new ClassXmlUtil().xmlToBean(new FileInputStream(outfileName), AmazonEnvelope.class);
			logger.debug(JSON.toJSONString(report));
			if(report.getHeader() == null)
			{
				logger.warn("报告未成生.FeedSubmissionId:{}",FeedSubmissionId);
				reportRes.setProcessStatys(AmazonConstants.GET_REPORT_PROCESS_STATYS_NOTANY); // 0 未生成报告
				resultList.add(reportRes);
				return resultList;
				//return AmazonConstants.RESPORT_RESULT_UPLOADING;
			}
			
			
			List<AmazonEnvelope.Message> messages = report.getMessage();
			for(AmazonEnvelope.Message msg : messages)
			{
				List<ProcessingReport.Result> results = msg.getProcessingReport().getResult();
				if(CollectionUtils.isEmpty(results))
				{
					logger.debug("FeedSubmissionId:{},获取报告成功，无错误信息。", FeedSubmissionId);
					reportRes = new AmazonReportListResult();
					reportRes.setHttErrorCode(HttpStatus.SC_OK);
					reportRes.setResultDescription("success");
					reportRes.setMessageId(null);
					reportRes.setProcessStatys(AmazonConstants.GET_REPORT_PROCESS_STATYS_SUCCESS); //1 生成报告成功，无任何错误信息
					resultList.add(reportRes);
					return resultList;
				}
				for(ProcessingReport.Result r : results)
				{
					//if(r.getResultCode().equalsIgnoreCase("Error")){          //警告和其他状态不算醋五
					reportRes = new AmazonReportListResult();
					reportRes.setHttErrorCode(HttpStatus.SC_OK);
					reportRes.setMessageId(r.getMessageID().longValue());
					reportRes.setResultCode(r.getResultCode());
					reportRes.setResultMessageCode(r.getResultMessageCode().longValue());
					reportRes.setResultDescription(r.getResultDescription());
					reportRes.setProcessStatys(AmazonConstants.GET_REPORT_PROCESS_STATYS_SUCC_HAVE_FAIL); //2生成报告成功，但有错误信息
					resultList.add(reportRes);
//					}else {
//						logger.warn("获取刊登报告时产生警告，提交ID为:{},message_id为：{} ,警告信息为：{}",FeedSubmissionId,r.getResultDescription());
//					}
				}
				
			}
			return resultList;
		} catch (FileNotFoundException e) {
			logger.error("",e);
		} catch (MarketplaceWebServiceException e) {
			logger.error("获取报告异常，异常信息：{} ",JSON.toJSONString(e));
			AmazonReportListResult reportRes = new AmazonReportListResult();
			reportRes.setHttErrorCode(e.getStatusCode());
			reportRes.setResultDescription(e.getMessage());
			reportRes.setMessageId(-1L);
			reportRes.setResultCode(e.getErrorCode());
			reportRes.setProcessStatys(AmazonConstants.GET_REPORT_PROCESS_STATYS_EXCEPTION); //获取报告异常了。
			// 404 暂无报告，
			// HttpStatus.SC_SERVICE_UNAVAILABLE =RequestThrottled
			// HttpStatus.SC_INTERNAL_SERVER_ERROR amazon服务错误
			/*if(e.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			{
				logger.warn("amazon服务器错误",FeedSubmissionId);
				return AmazonConstants.RESPORT_RESULT_UPLOADING;
			}
			if(e.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE)
			{
				logger.warn("请求太快，被amazom扼杀掉了。.FeedSubmissionId:{}",FeedSubmissionId);
				return AmazonConstants.RESPORT_RESULT_REQUESTTHROTTLED;
			}
			if(e.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			{
				logger.warn("500错误，amazon服务异常。.FeedSubmissionId:{}",FeedSubmissionId);
				return AmazonConstants.RESPORT_RESULT_UPLOADING;
			}*/
			logger.error("异常类型：{}，异常错误：{} ，异常原因：{}",e.getErrorType(),e.getErrorCode(),e.getMessage());
			logger.error("网络或调用接口异常",e);
			//return AmazonConstants.RESPORT_RESULT_UPLOADING;
			resultList.add(reportRes);
			return resultList;
			// errorStr.append(e.getErrorType());
		} catch (JAXBException e) {
			logger.error("解释xml结果出错",e);
			//return  resultList;
		} catch (IOException e) {
			logger.error("读取报告异常",e);
			//return resultList;
		}finally {
			if(processingResult != null)
			{
				try {
					processingResult.close();
				} catch (IOException e) {
				}
			}
			
			if(request.getFeedSubmissionResultOutputStream() != null )
			{
				try {
					request.getFeedSubmissionResultOutputStream().close();
				} catch (IOException e) {
				}
			}
			// 要清掉这个报告文件，以免积压到服务器，删除时没异常可获
			// TODO 测试阶段不用删除
			File reportFile = new File(outfileName);
			reportFile.delete();
		}
        return resultList;
	}

	// 测试
	public static void main(String[] args) {
		File reportFile = new File("d:\\ddd.txt");
		reportFile.delete();
	}
}
