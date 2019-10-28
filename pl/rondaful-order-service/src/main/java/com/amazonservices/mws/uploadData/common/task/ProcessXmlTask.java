package com.amazonservices.mws.uploadData.common.task;/*
package com.rondaful.cloud.seller.common.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedisUtils;
import AmazonConvert;
import ApplicationContextProvider;
import AmazonPostMethod;
import com.rondaful.cloud.seller.controller.AmazonPublishController;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.Amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;

*/
/**
 * 根据提交项生成xml
 * @author ouxiangfeng
 *//*

public class ProcessXmlTask implements Callable<String>  { //如果使用Callable会等待，但这里又不想等待

	private final Logger logger = LoggerFactory.getLogger(ProcessXmlTask.class);
	
	AmazonRequestProduct<?> requestProduct;
	
	*/
/**
	 * 根据提交项 生成xml
	 * @param requestProduct
	 *//*

	public ProcessXmlTask(AmazonRequestProduct<?> requestProduct)
	{
		this.requestProduct = requestProduct;
	}
	
	
	*/
/*@Override
	public void run() {
		
		if(this.requestProduct == null || StringUtils.isEmpty(this.requestProduct.getBatchNo()))
		{
			logger.error("获取请求对象或批次号为空，操作失败");
			return;
		}
		
		AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
		AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
		AmazonConvert amazonConvert = (AmazonConvert) ApplicationContextProvider.getBean("amazonConvert");
		RedisUtils redisUtils = (RedisUtils) ApplicationContextProvider.getBean("redisUtils");
		
		// 生成刊登xml
		List<String> versions = amazonConvert.createXML(requestProduct);
		if(CollectionUtils.isEmpty(versions))
		{
			logger.error("生成xml数据为空，操作失败");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "生成xml数据为空，操作失败");
		}
		// 获取草稿数据
		List<AmazonPublishListing> baseListings = amazonPublishListingService.selectBybatchNo(this.requestProduct.getBatchNo());
		if(baseListings == null || baseListings.size() <= 0)
		{
			logger.error("根据批次号：{} 未能获取数据，操作失败",this.requestProduct.getBatchNo());
			return;
		}
		
		// 写入上报状态表
		AmazonPublishListing listingObj = baseListings.get(0);
		for(String v : versions)
		{
			AmazonPublishSubListing subListing = new AmazonPublishSubListing();
			subListing.setListingId(listingObj.getId());
			subListing.setMsgType(AmazonConvert.getPostMethod(v));
			//subListing.setProcessStatus(AmazonPostMethod.UPLOAD_STATUS_SUBMITTED_); //default
			Object xmlStr = redisUtils.get(v);
			subListing.setXmls(xmlStr == null ? "" : xmlStr.toString());
			amazonPublishsubListingService.save(subListing); //TODO 批量保存待优化
			//下一个任务,进行刊登
			AmaznoExecutors.getInstance().addTask(new SubmitFeedTask(subListing.getId()));
		}
		
		// 清除redis数据
		String tempKeys []= new String[versions.size()]; 
		tempKeys = versions.toArray(tempKeys);
		redisUtils.remove(tempKeys);
	}*//*



	@Override
	public String call() throws Exception {
		if(this.requestProduct == null || StringUtils.isEmpty(this.requestProduct.getBatchNo()))
		{
			logger.error("获取请求对象或批次号为空，操作失败");
			return "参数请求错误，获取请求对象或批次号为空，操作失败";
		}
		
		AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
		AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
		AmazonConvert amazonConvert = (AmazonConvert) ApplicationContextProvider.getBean("amazonConvert");
		RedisUtils redisUtils = (RedisUtils) ApplicationContextProvider.getBean("redisUtils");
		
		// 生成刊登xml
		List<String> versions = amazonConvert.createXML(requestProduct);
		if(CollectionUtils.isEmpty(versions))
		{
			logger.debug(JSON.toJSONString(requestProduct));
			logger.error("生成xml数据为空，操作失败");
			return "生成xml数据为空，操作失败";
			//throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "生成xml数据为空，操作失败");
		}
		// 获取草稿数据
		List<AmazonPublishListing> baseListings = amazonPublishListingService.selectBybatchNo(this.requestProduct.getBatchNo());
		if(baseListings == null || baseListings.size() <= 0)
		{
			logger.error("根据批次号：{} 未能获取数据，操作失败",this.requestProduct.getBatchNo());
			return "根据批次号："+this.requestProduct.getBatchNo()+" 未能获取数据，操作失败";
		}
		
		// 写入上报状态表
		AmazonPublishListing listingObj = baseListings.get(0);
		for(String v : versions)
		{
			AmazonPublishSubListing subListing = new AmazonPublishSubListing();
			subListing.setListingId(listingObj.getId());
			subListing.setMsgType(AmazonConvert.getPostMethod(v));
			//subListing.setProcessStatus(AmazonPostMethod.UPLOAD_STATUS_SUBMITTED_); //default
			Object xmlStr = redisUtils.get(v);
			subListing.setXmls(xmlStr == null ? "" : xmlStr.toString());
			amazonPublishsubListingService.save(subListing); //TODO 批量保存待优化
			//下一个任务,进行刊登
			AmaznoExecutors.getInstance().addTask(new SubmitFeedTask(subListing.getId()));
		}
		
		// 清除redis数据
		String tempKeys []= new String[versions.size()]; 
		tempKeys = versions.toArray(tempKeys);
		redisUtils.remove(tempKeys);
		return "";
	}

}
*/
