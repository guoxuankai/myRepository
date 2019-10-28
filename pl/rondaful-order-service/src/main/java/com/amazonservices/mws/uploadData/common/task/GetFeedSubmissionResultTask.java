/*
package com.amazonservices.mws.uploadData.common.task;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.amazonservices.mws.uploadData.entity.AmazonPublishListing;
import com.amazonservices.mws.uploadData.entity.AmazonPublishSubListing;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonservices.mws.uploadData.common.mws.intface.GetFeedSubmissionListResultReport;
import com.amazonservices.mws.uploadData.common.spring.ApplicationContextProvider;
import com.amazonservices.mws.uploadData.constants.AmazonConstants;

@Component
@EnableScheduling
public class GetFeedSubmissionResultTask {

	private final Logger logger = LoggerFactory.getLogger(GetFeedSubmissionResultTask.class);
	Lock lock = new ReentrantLock();
	
	@Scheduled(initialDelay=1200, fixedRate=60000)//第一次延迟120秒后执行，之后按fixedRate的规则每60秒执行一次。
	private void process(){
		logger.debug("检查报告开始");
		
		if(!lock.tryLock())
		{
			logger.warn("前一个任务未完成，当前线程被忽略.等待前一个任务完执行完成...");
			return;
		}
		try
		{
			lock.lock(); //简单加一下锁，原因是当前任需要完成后才能走下一次
			AmazonPublishSubListingService subListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
			AmazonPublishListingService listingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
			
			AmazonPublishSubListing sub = new AmazonPublishSubListing();
			// 提交并完成的数据
			sub.setCurrInterface("SubmitFeed");
			//sub.setProcessStatus("_SUBMITTED_");
			sub.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_PRESSING); // 处理中
			List<AmazonPublishSubListing> list = subListingService.selectPage(sub);
			logger.debug("本次检查报告数量：{}",list.size());
			
			if(CollectionUtils.isEmpty(list)) return;
			
			for(AmazonPublishSubListing row : list)
			{
				AmazonPublishListing listingObj = listingService.selectByPrimaryKey(row.getListingId());
				String errorMsg = new GetFeedSubmissionListResultReport().invoke(listingObj.getPublishSite(), 
						listingObj.getPublishAccount(), row.getSubmitfeedId(), row.getMsgType());
				if(StringUtils.isEmpty(errorMsg))
				{
					logger.error("网络异常。。。,");
					continue;
				}
				
				row.setResultMessage(errorMsg);
				row.setCompleteStatus(getCompleteStatus(errorMsg));
				row.setProcessStatus("Complete");
				row.setCurrInterface("GetFeedSubmissionResult");
				row.setUpdateTime(new Date());
				subListingService.update(row);
			}
		}finally
		{
			lock.unlock();
		}
    }
	
	private Integer getCompleteStatus(String str)
	{
		if(str.equals(AmazonConstants.RESPORT_RESULT_SUCCESS))
		{
			return AmazonConstants.COMPLETE_STATUS_COMPLETE;
		}else if(str.equals(AmazonConstants.RESPORT_RESULT_UPLOADING))
		{
			return AmazonConstants.COMPLETE_STATUS_PRESSING;
		}else
		{
			return AmazonConstants.COMPLETE_STATUS_FAILED;
		}
	}
}
*/
