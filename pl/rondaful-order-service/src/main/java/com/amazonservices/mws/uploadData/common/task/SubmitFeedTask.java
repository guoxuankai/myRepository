/*
package com.amazonservices.mws.uploadData.common.task;

import java.util.Date;

import com.amazonservices.mws.uploadData.common.mws.intface.SubmitFeed;
import com.amazonservices.mws.uploadData.common.mws.model.SubmitFeedResponse;
import com.amazonservices.mws.uploadData.common.spring.ApplicationContextProvider;
import com.amazonservices.mws.uploadData.constants.AmazonConstants;
import com.amazonservices.mws.uploadData.entity.AmazonPublishListing;
import com.amazonservices.mws.uploadData.entity.AmazonPublishSubListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


*/
/**
 * 上报xml数据，并将返回的SubmitfeedId写入数据库。
 * @author ouxiangfeng
 *
 *//*

public class SubmitFeedTask  implements Runnable{

	private final Logger logger = LoggerFactory.getLogger(SubmitFeedTask.class);
	
	private Long sublistingId;
	public SubmitFeedTask(Long sublistingId) {
		this.sublistingId = sublistingId;
	}
	
	@SuppressWarnings("unused")
	@Override
	public void run() {
		AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
		AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
		AuthorizationSellerService authorizationSellerService  = (AuthorizationSellerService) ApplicationContextProvider.getApplicationContext().getBean("authorizationSellerServiceImpl");
		 
		AmazonPublishSubListing amazonPublishSubListing =amazonPublishsubListingService.selectByPrimaryKey(sublistingId);
		AmazonPublishListing amazonPublishListing = amazonPublishListingService.selectByPrimaryKey(amazonPublishSubListing.getListingId());
		
		SubmitFeed submitFeed = (SubmitFeed) ApplicationContextProvider.getBean("submitFeed");
		if(amazonPublishSubListing == null)
		{
			logger.error("AmazonPublishSubListing表找不到id为:{} 的数据",this.sublistingId);
			return;
		}
		
		Empower empower = new Empower();
		empower.setStatus(1); 												//正常授权
		empower.setWebName(MarketplaceIdList.createMarketplace().get(amazonPublishListing.getPublishSite()).getMarketplaceId()); 			//站点
		empower.setPlatform(2); 											//平台 (1 ebay   2 Amazon)
		empower.setThirdPartyName(amazonPublishListing.getPublishAccount());//卖家id
		empower = authorizationSellerService.selectOneByAcount(empower);
		if(empower == null)
		{
			amazonPublishSubListing.setResultMessage(String.format("在账户授权数据中，站点%s 找不到有效的授权数据sellerid：%s", 
					amazonPublishListing.getPublishSite(),amazonPublishListing.getPublishAccount()));
			amazonPublishSubListing.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_FAILED);
			amazonPublishSubListing.setUpdateTime(new Date());
			amazonPublishsubListingService.update(amazonPublishSubListing);
			logger.error("在账户授权数据中，站点{} 找不到有效的授权数据sellerid：{}   ",amazonPublishListing.getPublishSite(),amazonPublishListing.getPublishAccount());
			return ;
		}
		
		String xml = amazonPublishSubListing.getXmls();
		SubmitFeedResponse response = submitFeed.invoke(amazonPublishListing.getPublishSite(),
				amazonPublishListing.getPublishAccount(), xml, amazonPublishSubListing.getMsgType(),empower.getToken());
		
		AmazonPublishSubListing updateObj = new AmazonPublishSubListing();
		if(response == null || AmazonConstants.RESPORT_RESULT_ERROR.equals(
				response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedSubmissionId()))
		{
			amazonPublishSubListing.setResultMessage(response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedType());
			amazonPublishSubListing.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_FAILED);
			logger.error("请求amazon接口异常,原因:{}",response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedType());
		}else
		{
		amazonPublishSubListing.setProcessStatus(response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedProcessingStatus());
		amazonPublishSubListing.setSubmitfeedId(response.getSubmitFeedResult().getFeedSubmissionInfo().getFeedSubmissionId());
		amazonPublishSubListing.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_PRESSING);
		amazonPublishSubListing.setResultMessage(AmazonConstants.RESPORT_RESULT_SUCCESS);
		}
		amazonPublishSubListing.setUpdateTime(new Date());
		amazonPublishSubListing.setCurrInterface("SubmitFeed");
		amazonPublishsubListingService.update(amazonPublishSubListing);
	}
}
*/
