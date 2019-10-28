package com.rondaful.cloud.seller.common.task;

import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.seller.constants.AmazonPublishUpdateStatus;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.remote.RemoteCommodityService;
import com.rondaful.cloud.seller.vo.CodeAndValueVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.utils.RedissLockUtil;
import com.rondaful.cloud.seller.common.spring.ApplicationContextProvider;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.Empower;
import com.rondaful.cloud.seller.entity.SellerSkuMap;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.remote.RemoteOrderRuleService;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;
import com.rondaful.cloud.seller.service.AuthorizationSellerService;

import static org.apache.commons.lang3.ArrayUtils.toArray;

/**
 * 扫描成功的数据，并更新状态，同时提交绑定sku与plsku.
 * 
 * @author ouxiangfeng
 *
 */
@Component
public class ScanSuccessStatusTask implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(ScanSuccessStatusTask.class);
	
	@Autowired
	RemoteOrderRuleService remoteOrderRuleService;


	@Autowired
	private RemoteCommodityService remoteCommodityService;
	
	@Autowired
	RedissLockUtil redissLockUtil;
	
	private final String lockKey = "redis_lock_key_ScanSuccessStatusTask";
	
	
	public ScanSuccessStatusTask get()
	{
		return this;
	}
	
	
	/*public static void main(String[] args) {
		String json = "[{\"errorCount\":0,\"id\":2802,\"pubingCount\":12,\"successCount\":0,\"totalCount\":12,\"wiatCount\":0}]";
		List<StatisticsPublishReport> results = JSON.parseArray(json, StatisticsPublishReport.class);
		Set<Long> errorids = new HashSet<>();
		Set<Long> successids = new HashSet<>();
		for(StatisticsPublishReport report : results)
		{
			System.out.println(JSON.toJSONString(report));
			if(report.getErrorCount() > 0) // 如果存在错误数据则直接更新 为失败
			{
				//amazonPublishListingService.updateByPrimaryKey(t)
				System.out.println("存在刊登错误的数据，id={}"+report.getId());
				//errorids.add(report.getId());
				continue;
			}
			
			if(report.getTotalCount() >= 4 &&  report.getTotalCount() == report.getSuccessCount()) // 全部成功
			{
				System.out.println("存在刊登成功的数据，id={}"+report.getId());
				//successids.add(report.getId());
			}
		}
		
		// 批量更新状态
		if(!errorids.isEmpty())
		{
			Long [] ids = new Long[errorids.size()];
			ids = errorids.toArray(ids);
			System.out.println("执行刊登失败的数据={}"+ids);
		}
		
		if(!successids.isEmpty())
		{
			Long [] ids = new Long[successids.size()];
			ids = successids.toArray(ids);
			System.out.println("执行刊登失败的数据={}"+ids);
		}
		System.out.println("////////////");
	}*/
	@Override
	public void run() {

		logger.debug("amazon_task_run......ScanSuccessStatusTask.....");
		/*if(!redissLockUtil.tryLock(lockKey, 10, 60 * 10)) //等待10秒，10分放开锁
		{
			logger.debug("redis_lock_key_LoadProductTaskScheduler 其它服务正在执行。locking....");
			return ;
		}*/
		
		AmazonPublishSubListingService amazonPublishsubListingService =
				(AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
		
		AmazonPublishListingService amazonPublishListingService  = 
				(AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
		
		
		List<StatisticsPublishReport> results = amazonPublishsubListingService.selectStatisticsPublish();
		Set<Long> errorids = new HashSet<>();
		Set<Long> successids = new HashSet<>();
		Set<Long> exceptionids = new HashSet<>();
		
		logger.debug("扫描数据准备更新是否完成...");
		logger.debug("扫描数据，找到数据{}条...",results.size());
		
		for(StatisticsPublishReport report : results)
		{
			logger.debug(JSON.toJSONString(report));
			if(report.getErrorCount() > 0 && report.getPubingCount() <= 0) // 如果存在错误数据则直接更新 为失败,但要同时没有刊登中状态的。
			{
				//amazonPublishListingService.updateByPrimaryKey(t)
				logger.debug("存在刊登错误的数据，id={}",report.getId());
				errorids.add(report.getId());
				
				logger.debug("执行刊登失败的数据={}",report.getId());
				List<AmazonPublishListing> amazonPublishListings =amazonPublishListingService.findListIfOnline(new Long[] {report.getId()});
				if (amazonPublishListings.get(0).getUpdateStatus()>0){           //在线修改失败，只改变修改状态
					amazonPublishListingService.updatelistingStatus(new Long[] {report.getId()},AmazonPublishUpdateStatus.UPDATE_FAIL);
                    AmazonPublishListing publishListing = new AmazonPublishListing();//在线修改完成后把状态修改成在线
                    publishListing.setId(report.getId());
                    publishListing.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE);
                    amazonPublishListingService.updateByPrimaryKeySelective(publishListing);
				}else if (amazonPublishListings.get(0).getUpdateStatus()==0) {  //非在线修改失败，变成刊登失败
					amazonPublishListingService.updateLoadTaskPulishBatch(new Long[]{report.getId()}, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL, null);
				}
				continue;
			}
			
			// 未能正常生成子表数据
			if(report.getTotalCount().intValue() == 1)
			{
				exceptionids.add(report.getId());
				logger.debug("此数据不完整，id={}",report.getId());
				continue;
			}
			
			if(/*report.getTotalCount() >= 4 && */ report.getTotalCount() == report.getSuccessCount()) // 全部成功
			{
				if(report.getTotalCount() < 4)
				{
					logger.debug("该数据可能来自于Amazon后台同步的。");
				}
				logger.debug("存在刊登成功的数据，id={}",report.getId());
				successids.add(report.getId());
			}
			
			
			
		}
		
		// 批量更新有异常的数据
		if(!exceptionids.isEmpty())
		{
			Long [] ids = new Long[exceptionids.size()];
			ids = exceptionids.toArray(ids);
			logger.debug("执行刊登有异常的数据={}",ids);
			//在线修改失败和不在线修改失败做不同的操作
			updateStatusOnlineOrNOT(amazonPublishListingService,ids,false);

		//	amazonPublishListingService.updateLoadTaskPulishBatch(ids, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT,"系统扫描发现数据不完整，需要重新刊登");
		}
		
		/*// 批量更新状态
		if(!errorids.isEmpty())
		{
			Long [] ids = new Long[errorids.size()];
			ids = errorids.toArray(ids);
			logger.debug("执行刊登失败的数据={}",ids);
			amazonPublishListingService.updateLoadTaskPulishBatch(ids, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_FAIL,null);
		}*/
		
		if(!successids.isEmpty())
		{
			Long [] ids = new Long[successids.size()];
			ids = successids.toArray(ids);
			logger.debug("执行刊登成功的数据={}",ids);
			//在线修改成功和不在线修改成功做不同的操作
			updateStatusOnlineOrNOT(amazonPublishListingService,ids,true);
			amazonPublishListingService.updateLoadTaskPulishBatch(ids, AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE,null);
			
			/*Page.builder("1", "1000");
			AmazonPublishSubListing querySubIsSuccessData  = new AmazonPublishSubListing();
			querySubIsSuccessData.setCompleteStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE);
			querySubIsSuccessData.setSubmitfeedId(sublisting.getSubmitfeedId());
			querySubIsSuccessData.setMsgType(AmazonPostMethod.POST_PRODUCT_DATA);*/
			//List<AmazonPublishSubListing> page = amazonPublishsubListingService.selectByListingId(ids);
			sendSkuCountToCommodity(ids,amazonPublishListingService);
			//sendOrderSkuMaps(page);
		}
	}



	private void sendSkuCountToCommodity(Long [] ids,AmazonPublishListingService amazonPublishListingService){
		try {
			if(ids == null ||ids.length == 0){
				return;
			}
			String[] strIds = new String[ids.length];
			for(int i = 0;i < ids.length; i++){
				strIds[i] = String.valueOf(strIds[i]);
			}
			HashMap<String, Object> param = new HashMap<>();
			param.put("ids",strIds);
			List<AmazonPublishListing> batchByIds = amazonPublishListingService.getBatchByIds(param);
			if(batchByIds == null || batchByIds.size() == 0)
				return;

			ArrayList<CodeAndValueVo> codeAndValueVos = new ArrayList<>();
			for(AmazonPublishListing a : batchByIds ){
				try {
					String publishMessage = a.getPublishMessage();
					AmazonRequestProduct requestProduct = JSONObject.parseObject(publishMessage, AmazonRequestProduct.class);
					if(StringUtils.isNotBlank(requestProduct.getPlSku()) && requestProduct.getQuantity() != null){
						CodeAndValueVo codeAndValueVo = new CodeAndValueVo();
						codeAndValueVo.setCode(requestProduct.getPlSku());
						codeAndValueVo.setValue(String.valueOf(requestProduct.getQuantity()));
						codeAndValueVos.add(codeAndValueVo);
					}
					if(requestProduct.getVarRequestProductList() != null){
						List varRequestProductList = requestProduct.getVarRequestProductList();
						List<AmazonRequestProduct> amazonRequestProducts = JSONObject.parseArray(JSONObject.toJSONString(varRequestProductList), AmazonRequestProduct.class);
						for(AmazonRequestProduct req: amazonRequestProducts){
							if(StringUtils.isNotBlank(req.getPlSku()) && req.getQuantity() != null){
								CodeAndValueVo codeAndValueVo = new CodeAndValueVo(){{
									setCode(req.getPlSku());
									setValue(String.valueOf(req.getQuantity()));
								}};
								codeAndValueVos.add(codeAndValueVo);
							}
						}
					}

				}catch (Exception e){
					logger.error("刊登成功后向商品推送成功数量分装数据出错错误。",e);
				}
			}

			remoteCommodityService.updateSkuPublishNum(codeAndValueVos);

		}catch (Exception e){
			logger.error("刊登成功后向商品推送成功数量发现系统错误。",e);
		}
	}






	// ,AmazonPublishUpdateStatus.UPDATE_SUCCESS,AmazonPublishUpdateStatus.NOT_UPDATE

	//在线修改和不在线修改做不同的操作
	public void updateStatusOnlineOrNOT(AmazonPublishListingService amazonPublishListingService,Long[] ids,boolean ifSuccess){
		List<AmazonPublishListing> amazonPublishListings =amazonPublishListingService.findListIfOnline(ids);
		
		// 未在线过的id
		List<Long> notOnlineIds = new ArrayList<>();
		//之前已在线的id
		List<Long> onlineIds = new ArrayList<>();
		amazonPublishListings.forEach(listing->     //判断是否是在线修改
		{
			if (listing.getUpdateStatus()>0){
				onlineIds.add(listing.getId());
			}else {
				notOnlineIds.add(listing.getId());
			}
		});
		
		
		if (!onlineIds.isEmpty()) {   //在线修改成功,或者修改失败
			//onlineIds
			Long[] longs = new Long[onlineIds.size()];
			for(int i = 0;i < onlineIds.size(); i ++){
				longs[i] = onlineIds.get(i);
				AmazonPublishListing publishListing = new AmazonPublishListing();//在线修改完成后把状态修改成在线
				publishListing.setId(onlineIds.get(i));
				publishListing.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_ONLINE);
				amazonPublishListingService.updateByPrimaryKeySelective(publishListing);
			}
			if(ifSuccess) {
				logger.info("修改已在线数据成功,{}"+longs);
			}else if (!ifSuccess){
				logger.info("修改已在线数据失败,{}"+longs);
			}
             amazonPublishListingService.updatelistingStatus(longs, 
            		 ifSuccess ?  AmazonPublishUpdateStatus.UPDATE_SUCCESS : AmazonPublishUpdateStatus.UPDATE_FAIL); //在线修改成功
		}
		
		
		
		if (!notOnlineIds.isEmpty()) {  //非在线修改成功或者失败
			Long[] longs = new Long[notOnlineIds.size()];
			for(int i = 0;i < notOnlineIds.size(); i ++){
				longs[i] = notOnlineIds.get(i);
			}
			if(ifSuccess) {
				logger.info("修改未在线数据成功,{}"+longs);
			}else if (!ifSuccess){
				logger.info("修改未在线数据失败,{}"+longs);
			}
            amazonPublishListingService.updatelistingStatus(longs,
            		ifSuccess ?  AmazonPublishUpdateStatus.NOT_UPDATE : AmazonPublishUpdateStatus.INIT);//非在线修改成功
            
			if (!ifSuccess){
                //amazonPublishListingService.updatelistingStatus(longs,AmazonPublishUpdateStatus.INIT);  //非在线修改失败
				amazonPublishListingService.updateLoadTaskPulishBatch((Long[]) notOnlineIds.toArray(), AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_DRAFT,"系统扫描发现数据不完整，需要重新刊登");
			}
		}


	}




	private void sendOrderSkuMaps(List<AmazonPublishSubListing> page)
	{
		try
		{
			if(CollectionUtils.isEmpty(page))
			{
				return;
			}
			List<SellerSkuMap> callList = new  ArrayList<SellerSkuMap>();
			AuthorizationSellerService authorizationSellerService  = (AuthorizationSellerService) ApplicationContextProvider.getApplicationContext().getBean("authorizationSellerServiceImpl");
			for(AmazonPublishSubListing sublisting : page)
			{
				
				Empower empower = new Empower();
				empower.setStatus(1);
				// empower.setAccount(UserSession.getUserBaseUserInfo().getUsername());
				empower.setWebName(sublisting.getMarketplaceId());
				empower.setThirdPartyName(sublisting.getMerchantId());
				empower.setPlatform(2);
				empower = authorizationSellerService.selectAmazonAccount(empower);
				if(empower == null)
				{
					logger.error("刊登成功后将即将绑定平台sku,但找不到授权数据。");
					return ;
				}
				
				SellerSkuMap sku = new SellerSkuMap();
				sku.setAuthorizationId(String.valueOf(empower.getEmpowerId()));
				sku.setPlatform("amazon");
				sku.setPlatformSku(sublisting.getSku());
				sku.setPlSku(sublisting.getPlSku());
				sku.setStatus(1);
				callList.add(sku);
			}
			logger.debug("开始调用addSkuMaps子系统接口，params:{}",JSON.toJSON(callList));
			String remResult = remoteOrderRuleService.addSkuMaps(callList);
			logger.debug("调用addSkuMaps子系统接口结束");
		}catch(Exception e)
		{
			logger.error("刊登成功后将即将绑定平台sku发现系统错误。",e);
		}
	}

}
