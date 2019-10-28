package com.amazonservices.mws.uploadData.common.mws.intface;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.amazonservices.mws.MarketplaceId;
import com.amazonservices.mws.MarketplaceIdList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebService;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceClient;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceConfig;
import com.amazonservices.mws.uploadData.common.mws.MarketplaceWebServiceException;
import com.amazonservices.mws.uploadData.common.mws.model.FeedSubmissionInfo;
import com.amazonservices.mws.uploadData.common.mws.model.IdList;
import com.amazonservices.mws.uploadData.common.mws.model.SubmitFeedRequest;
import com.amazonservices.mws.uploadData.common.mws.model.SubmitFeedResponse;
import com.amazonservices.mws.uploadData.common.mws.model.SubmitFeedResult;
import com.amazonservices.mws.uploadData.constants.AmazonConstants;
import com.amazonservices.mws.uploadData.utils.AmazonContentMD5;

/**
   *    上报数据到Amazon(提交xml报文)
 * @author ouxiangfeng
 *
 */
@Component
public class SubmitFeed extends BaseMission{
	
	private final Logger logger = LoggerFactory.getLogger(SubmitFeed.class);
	
	@Autowired
	RedisUtils redisUtils;
	
    /**
              * 上报xml数据
     * @param countryCode
     * 		国家代码
     * @param merchantId
     * 		卖家id
     * @param filePath
     * 		上报数据的文件路
     */
    public SubmitFeedResponse invoke(String countryCode,final String merchantId,String uploadXml,String feedType,String awmsToken)
    {
    	//String xml = (String) redisUtils.get(redidsUploadVersion);
		MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplace().get(countryCode);
		config.setServiceURL(marketplaceId.getUri());
		MarketplaceWebService service = new MarketplaceWebServiceClient(accessKeyId, secretAccessKey, appName,
				appVersion, config);
		SubmitFeedRequest request = new SubmitFeedRequest();
	    request.setMerchant(merchantId);
	    request.setMarketplaceIdList(new IdList(Arrays.asList(marketplaceId.getMarketplaceId())));
        request.setFeedType(feedType);
        request.setMWSAuthToken(awmsToken); // token auth
        //String filePath = "D:\\workspace\\MaWSJava\\resources\\product.xml";
        String md5;
        SubmitFeedResponse response = new SubmitFeedResponse();
        SubmitFeedResult submitFeedResult = new SubmitFeedResult();
        submitFeedResult.setFeedSubmissionInfo(new FeedSubmissionInfo());
        // 默认为异常  临时使用FeedSubmissionId做为消息回传上层
        submitFeedResult.getFeedSubmissionInfo().setFeedSubmissionId(AmazonConstants.RESPORT_RESULT_ERROR); 
		try {
			md5 = AmazonContentMD5.computeContentMD5HeaderValue(AmazonContentMD5.toInputStream(uploadXml));
			request.setContentMD5(md5);
		    request.setFeedContent(AmazonContentMD5.toInputStream(uploadXml));
		    response = service.submitFeed(request);
		    logger.debug("result FeedSubmissionId>>>>>>>>>>>:" + response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedSubmissionId());
		    logger.debug("result:"+JSON.toJSONString(response));
		    return response;
		} catch (NoSuchAlgorithmException e) { //临时使用FeedType做为消息回传上层
			submitFeedResult.getFeedSubmissionInfo().setFeedType(e.getMessage());
			logger.error("",e);
		} catch (IOException e) { //临时使用FeedType做为消息回传上层
			submitFeedResult.getFeedSubmissionInfo().setFeedType(e.getMessage());
			logger.error("",e);
		} catch (MarketplaceWebServiceException e) { //临时使用FeedType做为消息回传上层
			logger.error("",e);
			submitFeedResult.getFeedSubmissionInfo().setFeedSubmissionId(AmazonConstants.RESPORT_RESULT_ERROR);
			submitFeedResult.getFeedSubmissionInfo().setFeedType(e.getMessage());
		}catch (Exception e) { //临时使用FeedType做为消息回传上层
			submitFeedResult.getFeedSubmissionInfo().setFeedType(e.getMessage());
			logger.error("",e);
		}
		
		response.setSubmitFeedResult(submitFeedResult);
		return response;
    }
    
}
