package com.rondaful.cloud.seller.listen;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.common.task.GetReportListTaskBatch;
import com.rondaful.cloud.seller.common.task.LoadProductTaskBatch;
import com.rondaful.cloud.seller.common.task.ScanSuccessStatusTask;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;

@Component
public class AmazonPulshListen implements ApplicationRunner {

	@Autowired
	GetReportListTaskBatch getReportListTaskBatch;
	
	@Autowired
	LoadProductTaskBatch loadProductTaskBatch;
	
	@Autowired
	ScanSuccessStatusTask scanSuccessStatusTask;
	
	@Autowired
	RedisUtils redisUtils;
	
	@Autowired
	AmazonPublishSubListingService amazonPublishSubListingService;

	private final Logger logger = LoggerFactory.getLogger(AmazonPulshListen.class);

	@Override
	public void run(ApplicationArguments args) throws Exception {

		logger.debug("start loadProductTask、reportListTask..");
		
	/*	ScheduledExecutorService loadProductTaskService = Executors.newSingleThreadScheduledExecutor();
		loadProductTaskService.scheduleAtFixedRate(loadProductTask.get(), 0, 60, TimeUnit.SECONDS);

		
		ScheduledExecutorService reportListTaskService = Executors.newSingleThreadScheduledExecutor();
		reportListTaskService.scheduleAtFixedRate(getReportListTask.get(), 0, 60, TimeUnit.SECONDS);
*/
		/** 刊登 */
		/*ScheduledExecutorService loadProductTaskBatchService = Executors.newSingleThreadScheduledExecutor();
		loadProductTaskBatchService.scheduleAtFixedRate(loadProductTaskBatch.get(), 0, 60*20 , TimeUnit.SECONDS);*/
	 
		/** 报告 */
		/*ScheduledExecutorService reportListTaskBatchService = Executors.newSingleThreadScheduledExecutor();
		reportListTaskBatchService.scheduleAtFixedRate(getReportListTaskBatch.get(), 60*10, 60*35 , TimeUnit.SECONDS);*/
		
		/** 状态 */
		/*ScheduledExecutorService ScanSuccessStatusTaskService = Executors.newSingleThreadScheduledExecutor();
		ScanSuccessStatusTaskService.scheduleAtFixedRate(scanSuccessStatusTask.get(), 60*12, 60*5 , TimeUnit.SECONDS);*/
		
		/** 防止redis数据被清除，如果被清除，则以数据库中的最后id为准。 */
		Long maxMessageid = amazonPublishSubListingService.selectLastMaxMessageId();
		Long curValue = redisUtils.incrt(AmazonConstants.REDIS_XML_MESSAGE_ID);
		if(maxMessageid + 2 > curValue) {
			redisUtils.setIncrt(AmazonConstants.REDIS_XML_MESSAGE_ID, maxMessageid+1);
		}
	}

}

