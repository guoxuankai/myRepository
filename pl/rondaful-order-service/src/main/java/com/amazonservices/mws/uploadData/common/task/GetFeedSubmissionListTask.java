/*
package com.amazonservices.mws.uploadData.common.task;

import java.util.Date;
import java.util.List;

import com.amazonservices.mws.uploadData.common.mws.intface.GetFeedSubmissionList;
import com.amazonservices.mws.uploadData.common.spring.ApplicationContextProvider;
import com.amazonservices.mws.uploadData.entity.AmazonPublishListing;
import com.amazonservices.mws.uploadData.entity.AmazonPublishSubListing;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.amazonservices.mws.uploadData.common.mws.model.GetFeedSubmissionListResponse;
import com.amazonservices.mws.uploadData.constants.AmazonConstants;

public class GetFeedSubmissionListTask  implements Runnable  {

	private final Logger logger = LoggerFactory.getLogger(GetFeedSubmissionListTask.class);
	
	@Override
	public void run() {
		logger.debug("run GetFeedSubmissionListTask");
		// sub
		AmazonPublishSubListingService subListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
		AmazonPublishListingService listingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
		
		
		AmazonPublishSubListing sub = new AmazonPublishSubListing();
		// 提交并完成的数据
		sub.setCurrInterface("SubmitFeed");
		sub.setProcessStatus("_DONE_");
		sub.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_COMPLETE);
		List<AmazonPublishSubListing> list = subListingService.selectPage(sub);
		if(CollectionUtils.isEmpty(list)) return;
		
		for(AmazonPublishSubListing apsl : list)
		{
			logger.debug("run GetFeedSubmissionListTask.rowid={}",apsl.getId());
			AmazonPublishListing listMain = listingService.selectByPrimaryKey(apsl.getListingId());
			GetFeedSubmissionList getFeedSubmissionList = new GetFeedSubmissionList();
			GetFeedSubmissionListResponse response = getFeedSubmissionList.invoke(listMain.getPublishSite(), listMain.getPublishAccount(), 
					apsl.getSubmitfeedId(), apsl.getMsgType());
			String resultJson =	JSON.toJSONString(response);
			logger.debug("response:{}",resultJson);
			AmazonPublishSubListing sublisting = new AmazonPublishSubListing();
			sublisting.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_COMPLETE);
			sublisting.setUpdateTime(new Date());
			sublisting.setCurrInterface("GetFeedSubmissionList");
			sublisting.setResultMessage(resultJson);
			sublisting.setProcessStatus(response.getGetFeedSubmissionListResult().getFeedSubmissionInfoList().toString());
			subListingService.update(sublisting);
		}
	}

}
*/
