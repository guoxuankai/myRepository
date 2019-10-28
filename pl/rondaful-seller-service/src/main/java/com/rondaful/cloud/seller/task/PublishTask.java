package com.rondaful.cloud.seller.task;

import com.rondaful.cloud.seller.entity.EbayPublishListingNew;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishModel;
import com.rondaful.cloud.seller.mapper.EbayPublishListingNewMapper;
import com.rondaful.cloud.seller.rabbitmq.AliexpressSender;
import com.rondaful.cloud.seller.service.IAliexpressPublishListingService;
import com.rondaful.cloud.seller.service.impl.EbayBaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PublishTask {
	
    private final Logger logger = LoggerFactory.getLogger(EbayBaseServiceImpl.class);
    
	
	@Autowired
	private EbayPublishListingNewMapper listingMapper;
	@Autowired
	private IAliexpressPublishListingService aliexpressPublishListingService;
	@Autowired
	private AliexpressSender aliexpressSender;
	/**
	 * 下架  如果这件商品刊登上了ebay,到时间后ebay会自已处理并下架。这里不用调下架接口进行操作 
	 */
    @Scheduled(cron = "0 0 23 * * ?")
    public void ebayEndTask(){
    	logger.info("定时执行下架功能start------------------------------------------->");
    	List<Long> findListingByTask = listingMapper.findListingByTask();
    	logger.info("下架数据{}条,{}",findListingByTask.size(),findListingByTask);
    	findListingByTask.forEach( id ->{
    		EbayPublishListingNew listing = new EbayPublishListingNew();
    		listing.setId(id);
    		listing.setStatus(3);
    		listing.setEndTimes(new Date());
    		listingMapper.updateByPrimaryKeySelective(listing);
    	});
    	logger.info("定时执行下架功能end------------------------------------------->");
    }
	/**
	 * 速卖通定时查询审核中的刊登商品是否审核成功 调度系统中调用
	 */
	//@Scheduled(cron = "0 0/30 * * * ?")
	public void aliexpressPublishTask(){
		logger.info("定时执行查询审核中的刊登商品是否审核成功start------------------------------------------->");
		List<AliexpressPublishModel> listAliexpressPublishModel= aliexpressPublishListingService.getAliexpressPublishModelList(4);
		logger.info("数据{}条",listAliexpressPublishModel.size());
		listAliexpressPublishModel.forEach( model ->{
			aliexpressSender.send(model);
		});
		logger.info("定时执行查询审核中的刊登商品是否审核成功end------------------------------------------->");
	}

}
