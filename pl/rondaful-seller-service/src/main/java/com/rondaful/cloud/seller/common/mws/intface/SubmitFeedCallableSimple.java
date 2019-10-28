package com.rondaful.cloud.seller.common.mws.intface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
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
import com.rondaful.cloud.seller.common.spring.ApplicationContextProvider;
import com.rondaful.cloud.seller.common.task.AmazonSubmitFeedResult;
import com.rondaful.cloud.seller.common.task.LoadProductRequest;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;
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
public class SubmitFeedCallableSimple {
	
	private final Logger logger = LoggerFactory.getLogger(SubmitFeedCallableSimple.class);
	private final String FIX = "_";
	private LoadProductRequest loadRequest;
	private Set<Long> mainIds ;
	private List<Long> subIds ;
	//private AmazonSubmitFeedResult response;
	private Map<Long,Boolean> map;
	public SubmitFeedCallableSimple(LoadProductRequest loadRequest,Set<Long> mainIds ,List<Long> subIds  ,Map<Long,Boolean> map)
	{
		this.loadRequest = loadRequest;
		this.subIds = subIds;
		this.mainIds = mainIds;
		this.map = map;
	}
	
	

	public AmazonSubmitFeedResult SubmitFeedRequest() throws Exception {
		
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
	
	public String call() {
		try {
			AmazonSubmitFeedResult response = SubmitFeedRequest();
			String feedSubmissionId = response.getFeedSubmissionId();
			
			logger.debug("result -- response:{}",JSON.toJSON(response));
			
			//  服务器错误
			if(StringUtils.isBlank(feedSubmissionId) &&  response.getHttErrorCode() >= HttpStatus.SC_INTERNAL_SERVER_ERROR)
			{
				logger.warn("submit feed is error.{}",JSON.toJSON(response));
                // new File(response.getFilePath()).delete(); // 出错了就删除文件，当它没执行过
				return "ERROR";
			}
			
			// 非成成功的状态,如果成功没异常的话，是不存在filePath的。
			if(response.getHttErrorCode() != HttpStatus.SC_OK && response.getHttErrorCode() < HttpStatus.SC_INTERNAL_SERVER_ERROR)
			{
				new File(response.getFilePath()).delete(); //非服务请求或网络异常的，都不需要重试.
			}
			
			AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) 
					ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
			
			AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) 
					ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
			
		
			
			//这个时候如果仍有非200状态的请求，那说明账号或其它基本信息有误，如果是401则说没权限
			if(response.getHttErrorCode() != HttpStatus.SC_OK && response.getHttErrorCode() != HttpStatus.SC_UNAUTHORIZED) 
			{
				logger.error("刊登时发现请求异常！response.getHttErrorCode()={},info={}",response.getHttErrorCode(),JSON.toJSONString(response));
				return "ERROR";
			}
			
			Long [] mainIdsArr = new Long[mainIds.size()];
			mainIdsArr = mainIds.toArray(mainIdsArr) ; //ArrayUtils.toArray(subIdsArr);
			logger.debug("Callable mainIdsArr:{}",mainIdsArr);
			
			Long [] subIdsArr = new Long[subIds.size()];
			subIdsArr = subIds.toArray(subIdsArr) ; //ArrayUtils.toArray(subIdsArr);
			logger.debug("Callable subIdsArr:{}",subIdsArr);
			amazonPublishsubListingService.updateLoadTaskPulishSubBatch(
					response.getResultDescription() //AmazonConstants.RESPORT_RESULT_PUBLISHING
					,response.getHttErrorCode() != HttpStatus.SC_OK ? AmazonConstants.COMPLETE_STATUS_FAILED : AmazonConstants.COMPLETE_STATUS_PRESSING
					,response.getFeedSubmissionId()
					,AmazonConstants.RESPORT_RESULT_COMPLETE
					,subIdsArr);
			
			//amazonPublishListingService.updateLoadTaskPulishBatch(mainIdsArr  ,AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_PUBLISHING,null);
			logger.debug("update updateLoadTaskPulishSubBatch isuccess!");
			return "";
		}
		catch (Exception e) {
			logger.error("UpdateStatusCallable============Exception=============",e);
		} catch (Throwable e) {
			logger.error("UpdateStatusCallable============Throwable=============",e);
		}finally
		{
			logger.debug("UpdateStatusCallable finally ...");
		}
		return "ERROR";
	}

    
}