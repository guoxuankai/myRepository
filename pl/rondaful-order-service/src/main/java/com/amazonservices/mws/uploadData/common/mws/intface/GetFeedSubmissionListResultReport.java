package com.amazonservices.mws.uploadData.common.mws.intface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.amazonservices.mws.MarketplaceId;
import com.amazonservices.mws.MarketplaceIdList;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceClient;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceConfig;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceException;
import com.amazonservices.mws.uploadData.common.mws.model.GetFeedSubmissionResultRequest;
import com.amazonservices.mws.uploadData.constants.AmazonConstants;
import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import com.rondaful.cloud.seller.generated.ProcessingReport;
import com.amazonservices.mws.uploadData.utils.ClassXmlUtil;

/**
 * 获取上报xml的结果报告
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
	public String invoke(String countryCode,final String merchantId,String FeedSubmissionId,String feedType) {
		
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplace().get(countryCode);
		config.setServiceURL(marketplaceId.getUri());
		MarketplaceWebService service = new MarketplaceWebServiceClient(accessKeyId, secretAccessKey, appName,
				appVersion, config);
		
		//////////////// request//////////////////////////
		GetFeedSubmissionResultRequest request = new GetFeedSubmissionResultRequest();
        request.setMerchant( merchantId );
        request.setFeedSubmissionId( FeedSubmissionId ); 
        
        OutputStream processingResult;
        StringBuilder errorStr = new StringBuilder( "");
        String outfileName = FeedSubmissionId +"-"+feedType+"-"+merchantId+".xml";
		try {
			processingResult = new FileOutputStream( outfileName);
			request.setFeedSubmissionResultOutputStream( processingResult );
			//GetFeedSubmissionResultResponse response = 
			service.getFeedSubmissionResult(request);
			
			//读取报告
			AmazonEnvelope report = ClassXmlUtil.xmlToBean(new FileInputStream(outfileName), AmazonEnvelope.class);
			if(report.getHeader() == null)
			{
				logger.warn("报告未成生.FeedSubmissionId:{}",FeedSubmissionId);
				return AmazonConstants.RESPORT_RESULT_UPLOADING;
			}
			
			List<AmazonEnvelope.Message> messages = report.getMessage();
			for(AmazonEnvelope.Message msg : messages)
			{
				List<ProcessingReport.Result> result = msg.getProcessingReport().getResult();
				if(CollectionUtils.isEmpty(result))
				{
					return AmazonConstants.RESPORT_RESULT_SUCCESS;
				}
				
				for(ProcessingReport.Result r : result)
				{
					errorStr.append(r.getResultDescription());
					errorStr.append("#$$$#"); //分隔符
				}
			}
			return errorStr.toString();
			
		} catch (FileNotFoundException e) {
			logger.error("",e);
		} catch (MarketplaceWebServiceException e) {
			logger.error("网络或调用接口异常",e);
			// errorStr.append(e.getErrorType());
		} catch (JAXBException e) {
			logger.error("解释xml结果出错",e);
		} catch (IOException e) {
			logger.error("读取报告异常",e);
		}finally {
			// 要清掉这个报告文件，以免积压到服务器，删除时没异常可获
			File reportFile = new File(outfileName);
			reportFile.delete();
		}
        return null;
	}
	
	// 测试
	public static void main(String[] args) {
		File reportFile = new File("d:\\ddd.txt");
		reportFile.delete();
	}
}
