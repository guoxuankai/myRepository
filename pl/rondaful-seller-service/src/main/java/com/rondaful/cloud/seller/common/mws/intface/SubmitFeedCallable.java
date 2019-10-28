package com.rondaful.cloud.seller.common.mws.intface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebService;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceClient;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceConfig;
import com.rondaful.cloud.seller.common.mws.MarketplaceWebServiceException;
import com.rondaful.cloud.seller.common.mws.model.IdList;
import com.rondaful.cloud.seller.common.mws.model.SubmitFeedRequest;
import com.rondaful.cloud.seller.common.mws.model.SubmitFeedResponse;
import com.rondaful.cloud.seller.common.task.AmazonSubmitFeedResult;
import com.rondaful.cloud.seller.common.task.LoadProductRequest;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdDeveloper;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.utils.AmazonContentMD5;

/**
   *    上报数据到Amazon(提交xml报文)
 * @author ouxiangfeng
 *
 */
// @Component
public class SubmitFeedCallable implements Callable<AmazonSubmitFeedResult>{
	
	private final Logger logger = LoggerFactory.getLogger(SubmitFeedCallable.class);
	private final String FIX = "_";
	private LoadProductRequest loadRequest;
	public SubmitFeedCallable(LoadProductRequest loadRequest)
	{
		this.loadRequest = loadRequest;
	}

	@Override
	public AmazonSubmitFeedResult call() throws Exception {
		
		// --------------- config begin ---------------------
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplace().get(loadRequest.getPublishSite());
		config.setServiceURL(marketplaceId.getUri());
		MarketplaceIdDeveloper marketplaceIdDeveloper = marketplaceId.getMarketplaceIdDeveloper();
		logger.debug("get SubmitFeed marketplace={}",JSON.toJSONString(marketplaceId));
		// 获取开发者与站点
        if(marketplaceIdDeveloper == null)
        {
        	logger.error("初始数据中找不到开发者信息,marketplaceIdObj={}",JSON.toJSONString(marketplaceIdDeveloper));
        	throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400, "站点信息不完整");
        }
        
        
        MarketplaceWebService service = new MarketplaceWebServiceClient(marketplaceIdDeveloper.getAccessKeyId(), 
        		marketplaceIdDeveloper.getSecretAccessKey(), marketplaceIdDeveloper.getName(),
				BaseMission.appVersion, config);
        
		SubmitFeedRequest request = new SubmitFeedRequest();
	    request.setMerchant(loadRequest.getMerchantIdentifier());
	    request.setMarketplaceIdList(new IdList(Arrays.asList(marketplaceId.getMarketplaceId())));
        request.setFeedType(loadRequest.getMsgType());
        request.setMWSAuthToken(loadRequest.getAmwToken()); // token auth
        // --------------- config end ---------------------

        
        
        String md5;
        SubmitFeedResponse response = new SubmitFeedResponse();
        AmazonSubmitFeedResult errorResult = new AmazonSubmitFeedResult();
		try {
			InputStream contentStream = loadRequest.getBody();
			md5 = AmazonContentMD5.computeContentMD5HeaderValue(contentStream);
			request.setContentMD5(md5);
		    request.setFeedContent(contentStream);
		    response = service.submitFeed(request);
		    contentStream.close();
		    logger.debug("result FeedSubmissionId>>>>>>>>>>>:" + response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedSubmissionId());
		    logger.debug("result:"+JSON.toJSONString(response));
		    errorResult.setHttErrorCode(HttpStatus.SC_OK);
			errorResult.setFeedSubmissionId(response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedSubmissionId());
			errorResult.setResultDescription(AmazonConstants.RESPORT_RESULT_PUBLISHING);
		    return errorResult;
		}catch (MarketplaceWebServiceException e) {
			errorResult.setHttErrorCode(e.getStatusCode());
			errorResult.setErrorType(e.getErrorType());
			errorResult.setErrorCode(e.getErrorCode());
			errorResult.setResultDescription("["+e.getErrorCode()+"]"+e.getMessage());
			errorResult.setFeedSubmissionId(null);
			
			logger.error("MarketplaceWebServiceException result:"+JSON.toJSONString(e));
			StringBuilder path = new StringBuilder("/logs/rondaful-seller-service/up_err_");
			path.append(loadRequest.getMerchantIdentifier()).append(FIX);
			path.append(loadRequest.getPublishSite()).append(FIX);
			path.append(loadRequest.getMsgType()).append(FIX);
			path.append(System.currentTimeMillis()).append(".xml");
			errorResult.setFilePath(path.toString());
			
			FileOutputStream outStream = new FileOutputStream(new File(path.toString()));
			loadRequest.getBody().reset();
			byte [] byteConx = new byte[loadRequest.getBody().available()];
			loadRequest.getBody().read(byteConx);
			outStream.write(byteConx);
			outStream.flush();
			loadRequest.getBody().close();
			outStream.close();
			logger.error("",e);
		}catch(Throwable e)
		{
			logger.error("MarketplaceWebServiceException result:{}", ExceptionUtils.getStackTrace(e));
		}finally
		{
			logger.debug("SubmitFeedCallable.finally...");
		}
		
		return errorResult;
	}

	public static void main(String[] args) {
		MarketplaceId id = MarketplaceIdList.createMarketplace().get("ES");
		System.out.println(id);
	}
    
}