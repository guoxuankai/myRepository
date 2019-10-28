package com.rondaful.cloud.seller.common.task;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.common.mws.intface.GetFeedSubmissionListResultReport;
import com.rondaful.cloud.seller.common.spring.ApplicationContextProvider;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;

@Component
//@EnableScheduling
public class GetReportListTaskBatch implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(GetReportListTaskBatch.class);
	
	private final String lockKey = "redis_lock_key_GetFeedSubmissionResultScheduler1";
	
	@Autowired
	RedissLockUtil redissLockUtil;
	
	@Autowired
	RemoteOrderRuleService remoteOrderRuleService;
	
	public GetReportListTaskBatch get()
	{
		return this;
	}
	
	//@Scheduled(initialDelay=60, fixedRate=330000 )//第一次延迟1秒后执行，之后按fixedRate的规则每5.5分钟执行一次。
	public void process(){
		
		logger.debug("amazon_task_process......GetReportListTaskBatch.....");
		
		/*if(!redissLockUtil.tryLock(lockKey, 10, 60*15)) //等待10秒，10分放开锁
		{
			logger.debug("其它服务正在执行。lock....");
			return ;
		}*/
		logger.debug("GetFeedSubmissionResultScheduler 检查报告开始..");
		try
		{
			
			AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
			AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
			
			// 根据submitfeddId，卖家，站点分组的结果，CurrInterface做为临时传递的listing_id(1,2,3,4)
			List<AmazonPublishSubListing> subListings = amazonPublishsubListingService.selectSubmitfeedIds();
			if(CollectionUtils.isEmpty(subListings))
			{
				logger.warn("无可刊登数据需要获取报告，些次任务告束");
				return;
			}
			
			AmazonPublishListing mainListing = null;
			for(AmazonPublishSubListing sublisting : subListings)
			{
				// sublisting.getCurrInterface() //
				mainListing = new AmazonPublishListing();
				//mainListing.setId(sublisting.getListingId());
				mainListing.setSubmitfeedid(sublisting.getSubmitfeedId()); // 根据这个条件出来的条一个迭代数据都是同一批，这一批数据也是根据卖家，站点进行刊登的
				mainListing.setMerchantIdentifier(sublisting.getMerchantId()); //TODO 加入站点查询
				mainListing = amazonPublishListingService.selectBySubmitfeedId(mainListing);
				// List<AmazonPublishListing> mainListingList = (List<AmazonPublishListing>) amazonPublishListingService.page(mainListing);
				if(mainListing == null)
				{
					// logger.error("找不到主:{} 数据，数据有误,当前条数据被忽略.",sublisting.getListingId()); // 这些数据后续需要设置状态，以免每次都会存读一次
					// amazonPublishListingService.updateLoadTaskPulishBatch(new Long[] {sublisting.getListingId()}, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT,"数据不完整重置为草稿");
					continue;
				}
				
				if(StringUtils.isBlank(sublisting.getMarketplaceId()))
				{
					logger.error("老数据，暂时跳过，方便测试, sublisting_id={}",sublisting.getId()); //TODO 老数据，暂时跳过，方便测试
					continue;
				}
				
				// 同一个submitfeedid肯定是同一个卖家、站点、token、msgType;不可能产品、图片等混在一个submitFeedId中。
				// 以resultList.messageId为数据标准
				List<AmazonReportListResult> resultList = new GetFeedSubmissionListResultReport().invoke(sublisting.getMarketplaceId(), 
						sublisting.getMerchantId(), sublisting.getSubmitfeedId(), sublisting.getMsgType(),mainListing.getAmwToken());
				// logger.debug("errorMsg={}",errorMsg);
				
				
				if(CollectionUtils.isEmpty(resultList))
				{
					logger.error("获取报告发生不可知的异常,sublisting_id:{}",sublisting.getId()); //TODO 老数据，暂时跳过，方便测试
					continue;
				}
				
				if(resultList.get(0).getProcessStatys() == AmazonConstants.GET_REPORT_PROCESS_STATYS_NOTANY)
				{
					logger.debug("未能获取报告。");
					continue;
				}
				
				//if(resultList.get(0).getHttErrorCode() != HttpStatus.SC_OK)
				if(resultList.get(0).getProcessStatys() == AmazonConstants.GET_REPORT_PROCESS_STATYS_EXCEPTION)
				{
					logger.debug("请求失败。未能获取报告,httpstatus:{}",resultList.get(0).getHttErrorCode());
					AmazonPublishSubListing submitfeedIdSubListing = new AmazonPublishSubListing();
					if("FeedCanceled".equalsIgnoreCase(resultList.get(0).getResultCode())) //feed被amazon取消了。
					{
						submitfeedIdSubListing.setSubmitfeedId(sublisting.getSubmitfeedId());
						submitfeedIdSubListing.setMerchantId(sublisting.getMerchantId());
						submitfeedIdSubListing.setMarketplaceId(sublisting.getMarketplaceId());
						submitfeedIdSubListing.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_FAILED);
						submitfeedIdSubListing.setResultMessage("["+resultList.get(0).getResultCode()+"]("+resultList.get(0).getHttErrorCode()+")"+resultList.get(0).getResultDescription());
						amazonPublishsubListingService.updateBySubmitfeedIdSelective(submitfeedIdSubListing);
					}
					continue;
					
				}
				
				
				// 设置所有默认为成功
				try
				{
					amazonPublishsubListingService.updatePulishDefaultSuccess(sublisting.getSubmitfeedId() , resultList ,sublisting);
				}catch(Exception e)
				{
					logger.error("更新数据异常，数据被回滚:submitfeedid={}",sublisting.getSubmitfeedId(),e);
				}
			}
			
			
			
			logger.debug("GetFeedSubmissionResultScheduler 检查报告结束..");
		}catch(Exception e)
		{
			logger.error("获取上报结果异常", e);
		}
		finally
		{
			logger.debug("释放报告同步锁...");
			redissLockUtil.unlock(lockKey); // 解放锁
		}
    }
	


	@Override
	public void run() {
		process();
	}
}

