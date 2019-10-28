package com.rondaful.cloud.seller.common.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.common.mws.AmazonConvert;
import com.rondaful.cloud.seller.common.mws.intface.SubmitFeedCallable;
import com.rondaful.cloud.seller.common.mws.intface.SubmitFeedCallableSimple;
import com.rondaful.cloud.seller.common.mws.samples.SubmitFeedSample;
import com.rondaful.cloud.seller.common.spring.ApplicationContextProvider;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.constants.MessageTypeConstant;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.entity.amazon.AmazonQueryLoadTaskResult;
import com.rondaful.cloud.seller.entity.amazon.LoadPulishXmlObject;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;


/**
 * 开始刊登
 * @author ouxiangfeng
 *
 */
@Component
public class LoadProductTaskBatch implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(LoadProductTaskBatch.class);
	private final String lockKey = "redis_lock_key_LoadProductTaskScheduler1";
	
	@Autowired
	RedissLockUtil redissLockUtil;
	
	public  LoadProductTaskBatch get()
	{
		return this;
	}
	
	//@Scheduled(initialDelay=1, fixedRate=330000)//第一次延迟1秒后执行，之后按fixedRate的规则每5.5分钟执行一次。
	private void process(){
		
		logger.debug("amazon_task_process......LoadProductTaskBatch.....");
	/*	if(true)
		{
			return;
		}*/
		// redissLockUtil.unlock(lockKey); // 解放锁
		/*if(!redissLockUtil.tryLock(lockKey, 10, 60 * 15)) //等待10秒，10分放开锁
		{
			logger.debug("redis_lock_key_LoadProductTaskScheduler 其它服务正在执行。locking....");
			return ;
		}*/
		try
		{
			logger.debug("开始执行刊登..............");
			AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
			AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
			// AuthorizationSellerService authorizationSellerService  = (AuthorizationSellerService) ApplicationContextProvider.getApplicationContext().getBean("authorizationSellerServiceImpl");
			// SubmitFeedCallable submitFeed = (SubmitFeedCallable) ApplicationContextProvider.getBean("submitFeed");
			// 取出主数据(刊登中的数据)
			//AmazonPublishListing mainQuery = new AmazonPublishListing();
			//mainQuery.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_AWAIT); // 刊登状态 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线 6:等待7：在线状态图片刊登
			//mainQuery.setPublishStatus(7); // 刊登状态 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线 6:等待7：在线状态图片刊登
			Map<String, Object> mainQueryMap=new HashMap<>();
			List<Integer> list=new ArrayList<>();
			list.add(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_AWAIT);
			mainQueryMap.put("statusArr", list);
			
			List<AmazonQueryLoadTaskResult>  loadTaskResult = amazonPublishListingService.selectLoadTaskPulish(mainQueryMap); //2304(Long), 2305(Long), 2301(Long), 2302(Long), 2303(Long)
			logger.debug("查询等待publishStatus=6  的刊登数据:{}",JSON.toJSONString(loadTaskResult));
			
			if(CollectionUtils.isEmpty(loadTaskResult))
			{
				logger.debug("LoadProductTaskBatch amazon 暂无等待刊登的数据(main)");
				return;
			}
			
			
			// 迭代同一个站点同一个卖家的数据
			for(AmazonQueryLoadTaskResult taskResult : loadTaskResult)
			{
				logger.debug("taskResult.getListingIds():{}",taskResult.getListingIds());
				String ids[] = taskResult.getListingIds().split(",");
				List<AmazonPublishSubListing> subListTaskResult =  amazonPublishsubListingService.selectByListingId(toLong(ids));
				if(CollectionUtils.isEmpty(subListTaskResult)) // 如果为空，则说明这个数据不完整，没有子数据。直接失败就可以。
				{
					amazonPublishListingService.updateLoadTaskPulishBatch(toLong(ids), AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT,"数据不完整重置为草稿");
					break;
				}
				
				
				//StringBuilder productBuilder = new StringBuilder(headerBegin);
				LoadPulishXmlObject productBuilder = new LoadPulishXmlObject();
				//StringBuilder inventorytBuilder = new StringBuilder(headerBegin);
				LoadPulishXmlObject inventorytBuilder = new LoadPulishXmlObject();
				//StringBuilder imageBuilder = new StringBuilder(headerBegin);
				LoadPulishXmlObject imageBuilder = new LoadPulishXmlObject();
				//StringBuilder pricingBuilder = new StringBuilder(headerBegin);
				LoadPulishXmlObject pricingBuilder = new LoadPulishXmlObject();
				//StringBuilder relationshipBuilder = new StringBuilder(headerBegin);
				LoadPulishXmlObject relationshipBuilder = new LoadPulishXmlObject();
				
				List<Long> productIds = new ArrayList<>();
				List<Long> inventoryIds = new ArrayList<>();
				List<Long> imageIds = new ArrayList<>();
				List<Long> pricingIds = new ArrayList<>();
				List<Long> relationshipIds = new ArrayList<>();
				
				Map<Long,Boolean>  restPushIds = new HashMap<>();
				Set<Long> productMainIds = new HashSet<>();
				Set<Long> inventoryMainIds = new HashSet<>();
				Set<Long> imageMainIds = new HashSet<>();
				Set<Long> pricingMainIds = new HashSet<>();
				Set<Long> relationshipMainIds = new HashSet<>();
				
				
				logger.debug("subListTaskResult size={}",subListTaskResult.size());
				
				for(AmazonPublishSubListing sublisting : subListTaskResult)
				{
					AmazonPublishListing amazonPublishListing = amazonPublishListingService.selectByPrimaryKey(sublisting.getListingId());
					boolean isPartialUpdate = (amazonPublishListing.getPublishStatus() == AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH) ?
							Boolean.TRUE : Boolean.FALSE;
							
					logger.debug("sub_id:{},listing_id:{},msgType:{}",sublisting.getId(),sublisting.getListingId(),sublisting.getMsgType());
					if(StringUtils.isNotBlank(sublisting.getSubmitfeedId()))
					{
						logger.warn("已刊登过，不需要再重新刊登，跳过此条数据，id={},submitfeedId={}",sublisting.getId(),sublisting.getSubmitfeedId());
						continue;
					}
					
					if(isPartialUpdate) 
					{
						restPushIds.put(sublisting.getListingId(), isPartialUpdate);
					}
					
					if(AmazonPostMethod.POST_PRODUCT_DATA.equals(sublisting.getMsgType()))
					{
						productBuilder.getBody().append(getMessageXML(sublisting.getMessageId(),isPartialUpdate));
						productBuilder.getBody().append(sublisting.getXmls());
						productBuilder.getBody().append("</Message>");
						logger.debug("publish productBuilder:{}",productBuilder.getBody().toString());
						productIds.add(sublisting.getId());
						productMainIds.add(sublisting.getListingId());
						
						continue;
					}
					if(AmazonPostMethod.POST_INVENTORY_DATA.equals(sublisting.getMsgType()))
					{
						inventorytBuilder.getBody().append(getMessageXML(sublisting.getMessageId(),isPartialUpdate));
						inventorytBuilder.getBody().append(sublisting.getXmls());
						inventorytBuilder.getBody().append("</Message>");
						inventoryIds.add(sublisting.getId());
						logger.debug("publish inventorytBuilder:{}",inventorytBuilder.getBody().toString());
						inventoryMainIds.add(sublisting.getListingId());
						continue;
					}
					if(AmazonPostMethod.POST_IMAGE_DATA.equals(sublisting.getMsgType()))
					{
						imageBuilder.getBody().append(getMessageXML(sublisting.getMessageId(),isPartialUpdate));
						imageBuilder.getBody().append(sublisting.getXmls());
						imageBuilder.getBody().append("</Message>");
						logger.debug("publish imageBuilder:{}",imageBuilder.getBody().toString());
						imageIds.add(sublisting.getId());
						imageMainIds.add(sublisting.getListingId());
						continue;
					}
					if(AmazonPostMethod.POST_PRICING_DATA.equals(sublisting.getMsgType()))
					{
						pricingBuilder.getBody().append(getMessageXML(sublisting.getMessageId(),isPartialUpdate));
						pricingBuilder.getBody().append(sublisting.getXmls());
						pricingBuilder.getBody().append("</Message>");
						logger.debug("publish pricingBuilder:{}",pricingBuilder.getBody().toString());
						pricingIds.add(sublisting.getId());
						pricingMainIds.add(sublisting.getListingId());
						continue;
					}
					if(AmazonPostMethod.POST_RELATIONSHIP_DATA.equals(sublisting.getMsgType()))
					{
						relationshipBuilder.getBody().append(getMessageXML(sublisting.getMessageId(),isPartialUpdate));
						relationshipBuilder.getBody().append(sublisting.getXmls());
						relationshipBuilder.getBody().append("</Message>");
						logger.debug("publish relationshipBuilder:{}",relationshipBuilder.getBody().toString());
						relationshipIds.add(sublisting.getId());
						relationshipMainIds.add(sublisting.getListingId());
					}
				}
				productBuilder.setMerchantIdentifier(taskResult.getMerchantIdentifier());
				productBuilder.setXmlType(MessageTypeConstant.HEADER_MESSAGETYPE_PRODUCT);
				
				//productBuilder.append(headerEnd);
				//inventorytBuilder.append(headerEnd);
				inventorytBuilder.setMerchantIdentifier(taskResult.getMerchantIdentifier());
				inventorytBuilder.setXmlType(MessageTypeConstant.HEADER_MESSAGETYPE_INVENTORY);
				
				
				//imageBuilder.append(headerEnd);
				imageBuilder.setMerchantIdentifier(taskResult.getMerchantIdentifier());
				imageBuilder.setXmlType(MessageTypeConstant.HEADER_MESSAGETYPE_PRODUCTIMAGE);
				
				
				// pricingBuilder.append(headerEnd);
				pricingBuilder.setMerchantIdentifier(taskResult.getMerchantIdentifier());
				pricingBuilder.setXmlType(MessageTypeConstant.HEADER_MESSAGETYPE_PRICE);
				
				//relationshipBuilder.append(headerEnd);
				relationshipBuilder.setMerchantIdentifier(taskResult.getMerchantIdentifier());
				relationshipBuilder.setXmlType(MessageTypeConstant.HEADER_MESSAGETYPE_RELATIONSHIP);
				
				AmazonConvert amazonConvert = new AmazonConvert();
				// List<Future<AmazonSubmitFeedResult>> futureList = Collections.synchronizedList(new ArrayList<Future<AmazonSubmitFeedResult>>());
				Integer count = 0;
				
				// 调用上报数据接口
				// 商品
				if(productMainIds.size() > 0) //第一轮只刊登商品
				{
				 	logger.debug("产品上传数 <<<<< {}" ,productBuilder.toXml());       
					LoadProductRequest product = amazonConvert.toSubmitFeedObject(productBuilder.toXml(), taskResult,
							AmazonPostMethod.POST_PRODUCT_DATA);
					
					// 提交上传
					ExecutorService productExecutorService = Executors.newFixedThreadPool(2);
					Future<AmazonSubmitFeedResult> productFuture =productExecutorService.submit(new SubmitFeedCallable(product));
					while (!productFuture.isDone()) {
		                Thread.yield();
		            }
					AmazonSubmitFeedResult response = productFuture.get();
					// 提交更新
					Future<String> updateResult = productExecutorService.submit(new UpdateStatusCallable(productMainIds,productIds, response,restPushIds));
					while (!updateResult.isDone()) {
		                Thread.yield();
		            }
					String productResult = updateResult.get();
					productExecutorService.shutdown();
					logger.debug("刊登商品请求完成.{}",productResult);
					if("ERROR".equals(productResult))
					{
						logger.error("商品刊登错误，当前数据被终止.");
						continue;// 这个比较特殊，因为必需要要商品数据完成才能进行下一步，否则退出当前次，进入下一批数据
					}
					//先执行商品，2分钟后再执行其它的，因为这里有可能会导致amazon处理的时间差。
					// 后续要修改为 获取到商品的报告后才会进行非商品数据刊登
					Thread.sleep(300000L);
					
				/*	SubmitFeedCallableSimple sample = new SubmitFeedCallableSimple(product,productMainIds,productIds, restPushIds);
					sample.call();*/
				}
				
				
				// 图片
				if(imageMainIds.size() > 0)
				{
					logger.debug("图片上传数 <<<<<;{}",imageBuilder.toXml());
					LoadProductRequest image = amazonConvert.toSubmitFeedObject(imageBuilder.toXml(), taskResult, AmazonPostMethod.POST_IMAGE_DATA);
					
					// 提交上传
					ExecutorService imageExecutorService = Executors.newFixedThreadPool(2);
					Future<AmazonSubmitFeedResult> imageFuture =imageExecutorService.submit(new SubmitFeedCallable(image));
					while (!imageFuture.isDone()) {
		                Thread.yield();
		            }
					AmazonSubmitFeedResult response = imageFuture.get();
					
					// 提交更新
					Future<String> updateResult = imageExecutorService.submit(new UpdateStatusCallable(imageMainIds,imageIds, response,restPushIds));
					while (!updateResult.isDone()) {
		                Thread.yield();
		            }
					String imageResult = updateResult.get();
					imageExecutorService.shutdown();
					
					logger.debug("刊登图片请求完成.{}",imageResult);
					if("ERROR".equals(imageResult)) // 
					{
						count++;
					}
					/*SubmitFeedCallableSimple sample = new SubmitFeedCallableSimple(image,imageMainIds,imageIds, restPushIds);
					sample.call();*/
				}
				
				//库存
				if(inventoryMainIds.size() > 0)
				{
					logger.debug("库存上传数 <<<<<{}",inventorytBuilder.toXml());
					LoadProductRequest inventory = amazonConvert.toSubmitFeedObject(inventorytBuilder.toXml(), taskResult, AmazonPostMethod.POST_INVENTORY_DATA);
					
					// 提交上传
					ExecutorService inventoryExecutorService = Executors.newFixedThreadPool(2);
					Future<AmazonSubmitFeedResult> inventoryFuture =inventoryExecutorService.submit(new SubmitFeedCallable(inventory));
					while (!inventoryFuture.isDone()) {
		                Thread.yield();
		            }
					AmazonSubmitFeedResult response = inventoryFuture.get();
					
					
					// 提交更新
					Future<String> updateResult = inventoryExecutorService.submit(new UpdateStatusCallable(inventoryMainIds,inventoryIds, response,restPushIds));
					while (!updateResult.isDone()) {
		                Thread.yield();
		            }
					String inventoryresult = updateResult.get();
					inventoryExecutorService.shutdown();
					logger.debug("刊登库存请求完成.{}",inventoryresult);
					if("ERROR".equals(inventoryresult)) // 
					{
						count++;
					}
				/*	SubmitFeedCallableSimple sample = new SubmitFeedCallableSimple(inventory,inventoryMainIds,inventoryIds, restPushIds);
					sample.call();*/
				}
				
				//价格
				if(pricingMainIds.size() > 0)
				{
					logger.debug("价格上传数 <<<<<{}",pricingBuilder.toXml());
					LoadProductRequest pricing = amazonConvert.toSubmitFeedObject(pricingBuilder.toXml(), taskResult, AmazonPostMethod.POST_PRICING_DATA);
					
					// 提交上传
					ExecutorService pricingExecutorService = Executors.newFixedThreadPool(2);
					Future<AmazonSubmitFeedResult> pricingFuture =pricingExecutorService.submit(new SubmitFeedCallable(pricing));
					while (!pricingFuture.isDone()) {
		                Thread.yield();
		            }
					AmazonSubmitFeedResult response = pricingFuture.get();
					
					// 提交更新
					Future<String> updateResult = pricingExecutorService.submit(new UpdateStatusCallable(pricingMainIds,pricingIds, response,restPushIds));
					while (!updateResult.isDone()) {
		                Thread.yield();
		            }
					String pricingresult = updateResult.get();
					pricingExecutorService.shutdown();
					if("ERROR".equals(pricingresult)) // 
					{
						count++;
					}
					/*SubmitFeedCallableSimple sample = new SubmitFeedCallableSimple(pricing,pricingMainIds,pricingIds, restPushIds);
					sample.call();*/
				}
				
				//关系
				logger.debug("多属数据relationshipMainIds={}",relationshipMainIds.toArray());
				if(relationshipMainIds.size() > 0)
				{
					logger.debug("关系上传数 <<<<<{}",relationshipBuilder.toXml());
					LoadProductRequest relationship = amazonConvert.toSubmitFeedObject(relationshipBuilder.toXml(), taskResult, AmazonPostMethod.POST_RELATIONSHIP_DATA);
					
					// 提交上传
					ExecutorService relationshipExecutorService = Executors.newFixedThreadPool(2);
					Future<AmazonSubmitFeedResult> relationshipFuture =relationshipExecutorService.submit(new SubmitFeedCallable(relationship));
					while (!relationshipFuture.isDone()) {
		                Thread.yield();
		            }
					AmazonSubmitFeedResult response = relationshipFuture.get();
					
					// 提交更新
					Future<String> updateResult = relationshipExecutorService.submit(new UpdateStatusCallable(relationshipMainIds,relationshipIds, response,restPushIds));
					while (!updateResult.isDone()) {
		                Thread.yield();
		            }
					String relationshipresult = updateResult.get();
					relationshipExecutorService.shutdown();
					logger.debug("刊登关系请求完成.{}",relationshipresult);
					if("ERROR".equals(relationshipresult)) // 
					{
						count++;
					}
					
					/*SubmitFeedCallableSimple sample = new SubmitFeedCallableSimple(relationship,relationshipMainIds,relationshipIds, restPushIds);
					sample.call();*/
				}
				//}
				
				Set<Long> updateIdSet = new HashSet<>();
				updateIdSet.addAll(productMainIds);
				updateIdSet.addAll(pricingMainIds);
				updateIdSet.addAll(inventoryMainIds);
				updateIdSet.addAll(imageMainIds);
				updateIdSet.addAll(relationshipMainIds);
				if(count == 0) // 等于0，说明没有任何网络错误。
				{
					
					Long updateMainIds [] = null;
					if(updateIdSet.size() <= 0)
					{
						updateMainIds = toLong(ids);
					}else
					{
						updateMainIds = new Long[updateIdSet.size()];
						updateMainIds = updateIdSet.toArray(updateMainIds);
						logger.debug("更新状态为刊登中，id={}",updateMainIds);
					}
					amazonPublishListingService.updateLoadTaskPulishBatch(updateMainIds  ,AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_PUBLISHING,null);

				}
			}
		} catch (Exception e) {
			logger.error("",e);
		}finally
		{
			logger.debug("释放同步锁.");
			redissLockUtil.unlock(lockKey); // 解放锁
		}
	}

	
	private Long[] toLong(String [] ids)
	{
		Long[] r = new Long[ids.length];
		for(int i = 0 ; i < ids.length; i++ )
		{
			r[i] = Long.valueOf(ids[i]);
		}
		return r;
	}
	
	private String getMessageXML(Long messageid)
	{
		return "<Message>\r\n" + 
				"        <MessageID>"+ messageid +"</MessageID>\r\n" + 
				"        <OperationType>Update</OperationType>\r\n";
	}
	/**
	 * 
	 * @param messageid
	 * @param isPartialUpdate
	 * 		true，更新替换刊登； false:首次刊登
	 * @return
	 */
	private String getMessageXML(Long messageid,boolean isPartialUpdate)
	{
		if(isPartialUpdate)
		{
			return "<Message>\r\n" + 
					"        <MessageID>"+ messageid +"</MessageID>\r\n" + 
					"        <OperationType>PartialUpdate</OperationType>\r\n";
		}
		return getMessageXML(messageid);
		
	}
	
	/**
	 * 	上报顺序
	 * @param subList1
	 * @return
	 */
	private AmazonPublishSubListing[] sortSubListing(List<AmazonPublishSubListing> subList1) {
		AmazonPublishSubListing [] tempArray = new AmazonPublishSubListing[subList1.size()];
		for(AmazonPublishSubListing sub : subList1)
		{
			if(AmazonPostMethod.POST_PRODUCT_DATA.equals(sub.getMsgType())) // 确保第0个下标是商品的数据
			{
				tempArray [0] = sub;
			}else if(AmazonPostMethod.POST_INVENTORY_DATA.equals(sub.getMsgType()))
			{
				tempArray [1] = sub;
			}else if(AmazonPostMethod.POST_PRICING_DATA.equals(sub.getMsgType()))
			{
				tempArray [2] = sub;
			}else if(AmazonPostMethod.POST_IMAGE_DATA.equals(sub.getMsgType()))
			{
				tempArray [3] = sub;
			}else  if(AmazonPostMethod.POST_RELATIONSHIP_DATA.equals(sub.getMsgType()))// 确保最后一个下标是关系的数据
			{
				tempArray [4] = sub;
			}
		}
		return tempArray;
	}

	@Override
	public void run() {
		process();
	}
	
	class UpdateStatusCallable implements Callable<String>
	{
		private  final Logger logger = LoggerFactory.getLogger(LoadProductTaskBatch.class);
		private Set<Long> mainIds ;
		private List<Long> subIds ;
		private AmazonSubmitFeedResult response;
		private Map<Long,Boolean> map;
		UpdateStatusCallable(Set<Long> mainIds ,List<Long> subIds  , final AmazonSubmitFeedResult response,Map<Long,Boolean> map)
		{
			this.subIds = subIds;
			this.response = response;
			this.mainIds = mainIds;
			this.map = map;
		}
		
		
		@Override
		public String call() {
			try {
				final String feedSubmissionId = response.getFeedSubmissionId();
				
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
	
	class UpdateStatusRunable implements Runnable
	{
		private  final Logger logger = LoggerFactory.getLogger(LoadProductTaskBatch.class);
		Set<Long> mainIds ;
		List<Long> subIds ;
		Future<AmazonSubmitFeedResult> future;
		UpdateStatusRunable(Set<Long> mainIds ,List<Long> subIds  , final Future<AmazonSubmitFeedResult> future)
		{
			this.subIds = subIds;
			this.future = future;
			this.mainIds = mainIds;
		}
		
		
		@Override
		public void run() {
			AmazonSubmitFeedResult response;
			try {
				response = future.get();
				String feedSubmissionId = response.getFeedSubmissionId();
				
				//  服务器错误
				if(StringUtils.isBlank(feedSubmissionId) &&  response.getHttErrorCode() >= HttpStatus.SC_SERVICE_UNAVAILABLE)
				{
					logger.warn("submit feed is error.{}",JSON.toJSON(response));
                    // new File(response.getFilePath()).delete(); // 出错了就删除文件，当它没执行过
					return ;
				}
				
				
				
				AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) 
						ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
				
				AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) 
						ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
				
				Long [] mainIdsArr = new Long[mainIds.size()];
				mainIdsArr = mainIds.toArray(mainIdsArr) ; //ArrayUtils.toArray(subIdsArr);
				
				// logger.debug("Callable mainIdsArr:{}",mainIdsArr);
				if(response.getHttErrorCode() != HttpStatus.SC_OK) //这个时候如果仍有非200状态的请求，那说明账号或其它基本信息有误
				{
					Integer status = AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL; //失败
					amazonPublishListingService.updateLoadTaskPulishBatch( mainIdsArr,status,null);
				}
				
				
				Long [] subIdsArr = new Long[subIds.size()];
				subIdsArr = subIds.toArray(subIdsArr) ; //ArrayUtils.toArray(subIdsArr);
				// logger.debug("Callable subIdsArr:{}",subIdsArr);
				amazonPublishsubListingService.updateLoadTaskPulishSubBatch(
						
						response.getResultDescription() //AmazonConstants.RESPORT_RESULT_PUBLISHING
						,StringUtils.isBlank(feedSubmissionId) ? AmazonConstants.COMPLETE_STATUS_FAILED : AmazonConstants.COMPLETE_STATUS_PRESSING
						,response.getFeedSubmissionId()
						,AmazonConstants.RESPORT_RESULT_COMPLETE
						,subIdsArr);
			} catch (ExecutionException | InterruptedException e) {
				logger.error("",e);
			}
		}

 
		
	}
}

