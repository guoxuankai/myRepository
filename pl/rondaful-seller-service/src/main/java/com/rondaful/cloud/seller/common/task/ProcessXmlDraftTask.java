package com.rondaful.cloud.seller.common.task;

import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.common.spring.ApplicationContextProvider;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.generated.*;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;
import com.rondaful.cloud.seller.utils.AmazonSubListingUtil;
import com.rondaful.cloud.seller.utils.ClassXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.lang.Override;

/**
 * 根据提交项生成xml
 * @author ouxiangfeng
 */
public class ProcessXmlDraftTask implements Callable<String>  { //如果使用Callable会等待，但这里又不想等待

	private final Logger logger = LoggerFactory.getLogger(ProcessXmlDraftTask.class);

	AmazonRequestProduct<?> requestProduct;
	private AmazonSubListingUtil amazonSubListingUtil;

	// product专用
	private static final String [] cDataElements = { "^Title","^Description","^MfrPartNumber","^SKU"};

	/**
	 * 根据提交项 生成xml
	 * @param requestProduct
	 */
	public ProcessXmlDraftTask(AmazonRequestProduct<?> requestProduct)
	{
		this.requestProduct = requestProduct;
	}

	public ProcessXmlDraftTask(AmazonRequestProduct<?> requestProduct, AmazonSubListingUtil amazonSubListingUtil)
	{
		this.requestProduct = requestProduct;
		this.amazonSubListingUtil = amazonSubListingUtil;
	}
	
	@Override
	public String call() throws Exception {
		if(this.requestProduct == null || StringUtils.isEmpty(this.requestProduct.getBatchNo()))
		{
			logger.error("获取请求对象或批次号为空，操作失败");
			return "参数请求错误，获取请求对象或批次号为空，操作失败";
		}
		
		try
		{
			//AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
			AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
			amazonPublishsubListingService.deleteForBaseId(requestProduct.getId());

			//Long[] longs = {requestProduct.getId()};
			//List<AmazonPublishSubListing> amazonPublishSubListings = amazonPublishsubListingService.selectByListingId(longs);


			String markId= null;
			if(StringUtils.isNotBlank(requestProduct.getCountryCode())){
				markId = MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode()).getMarketplaceId();
			}

			List<AmazonPublishSubListing> subListing = new ArrayList<AmazonPublishSubListing>();
			RedisUtils redisUtils = (RedisUtils) ApplicationContextProvider.getBean("redisUtils");
			Date date = new Date();
			AmazonPublishSubListing sub ;
			if(StringUtils.isNotBlank(requestProduct.getPlSku())){
				/*boolean b = this.checkIsSubOldOrSuccessData(requestProduct.getPlSku(), requestProduct.getSku(), amazonPublishSubListings);*/
				sub = createSub(requestProduct, requestProduct, date, AmazonConstants.PARENT_TYPE_YES,redisUtils,markId);
				subListing.add(sub);
			}
			List<AmazonRequestProduct> varRequestProductList = requestProduct.getVarRequestProductList();
			if(varRequestProductList != null && varRequestProductList.size() != 0){
				for(AmazonRequestProduct request: varRequestProductList){
					if(StringUtils.isNotBlank(request.getPlSku())){
						sub = createSub(requestProduct, request, date, AmazonConstants.PARENT_TYPE_NO,redisUtils,markId);
						subListing.add(sub);
					}
				}
			}

			// --
			Integer warehouseId = requestProduct.getWarehouseId();
			this.amazonSubListingUtil.setPlSkuStatusAndCount(subListing,warehouseId);
			if(!CollectionUtils.isEmpty(subListing)) {
				amazonPublishsubListingService.insertBatch(subListing);
			}
			
			return "";
		}catch(Exception e)
		{
			logger.error("",e);
			return e.getMessage();
		}
	}

	/*
	 * 检查对应新的数据是否是没有被编辑过的老数据 是返回true 否则返回false
	 * @param plSku pl sku
	 * @param sku 平台sku
	 * @param amazonPublishSubListings  品连旧的 子数据列表
	 * @return 结果
	 */
	/*private boolean checkIsSubOldOrSuccessData(String plSku,String sku,List<AmazonPublishSubListing> amazonPublishSubListings){
		if(amazonPublishSubListings == null || amazonPublishSubListings.size() == 0){           // 如果原数据没有老数据那么肯定不是老数据
			return false;
		}
		if(StringUtils.isBlank(plSku) || StringUtils.isBlank(sku)){                             //新传入的数据中某一个sku没有，那么它一定不是原来的数据
			return false;
		}

		for(AmazonPublishSubListing sub : amazonPublishSubListings){
			try {
				if(plSku.equalsIgnoreCase(sub.getPlSku()) && sku.equalsIgnoreCase(sub.getSku())){    //现在判断只要数据库中的数据和传入的数据的两个sku有能匹配的，就说明这个是一个老数据
					return true;
				}
			}catch (Exception ignored){

			}
		}
		return false;              //没有匹配到相同的值，返回错误
	}*/



	public AmazonPublishSubListing createSub(AmazonRequestProduct<?> requestProduc,AmazonRequestProduct<?> request,Date date,Integer parentType,RedisUtils redisUtils,String marId){
		AmazonPublishSubListing sub = new AmazonPublishSubListing();
		sub.setListingId(requestProduc.getId());
		sub.setPlSku(request.getPlSku());
		sub.setSku(StringUtils.isBlank(request.getSku())?"virtualSku":request.getSku());
		sub.setMsgType(AmazonPostMethod.POST_PRODUCT_DATA);
		sub.setProcessStatus(AmazonConstants.RESPORT_RESULT_COMPLETE);
		sub.setXmls("virtualXML");
		sub.setCreateTime(date);
		sub.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_FAILED);
		sub.setUpdateTime(date);
		sub.setMessageId(redisUtils.incrt(AmazonConstants.REDIS_XML_MESSAGE_ID));
		sub.setParentType(parentType);
		sub.setMerchantId(requestProduc.getMerchantIdentifier());
		sub.setMarketplaceId(marId);
		sub.setAsin(request.getAsin());
		sub.setResultMessage("编辑未刊登的数据,或者亚马逊同步数据");
		sub.setPlSkuSaleNum(request.getPlSkuSaleNum());
		return sub;
	}











	/**
	 * 	
	 * @param parentId
	 * @param msgType
	 * @return
	 */
	private AmazonPublishSubListing getNewSubListing(AmazonRequestProduct<?> requestProduct,
			Long parentId,
			String msgType,
			boolean parentType,
			Object t,String [] cDataElements)
	{
		ClassXmlUtil xmlutil = new ClassXmlUtil();
		RedisUtils redisUtils = (RedisUtils) ApplicationContextProvider.getBean("redisUtils");
		AmazonPublishSubListing subListing  = new AmazonPublishSubListing();
		subListing.setListingId(parentId);
		subListing.setMsgType(msgType);
		subListing.setResultMessage(AmazonConstants.RESPORT_RESULT_AWAIT);
		subListing.setProcessStatus(AmazonConstants.RESPORT_RESULT_AWAIT);
		subListing.setCompleteStatus(AmazonConstants.COMPLETE_STATUS_AWAIT);
		subListing.setMarketplaceId(MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode()).getMarketplaceId());
		subListing.setMessageId(redisUtils.incrt(AmazonConstants.REDIS_XML_MESSAGE_ID));
		subListing.setParentType(parentType?0:1);
		subListing.setMerchantId(requestProduct.getMerchantIdentifier());
		/*if(AmazonPostMethod.POST_IMAGE_DATA.equals(msgType))
		{
			StringBuilder builder = new StringBuilder();
			List<ProductImage> productImage  = (List<ProductImage>) t;
			for(ProductImage image : productImage)
			{
				builder.append(xmlutil.toXML(image)).append("\n");
			} 
			subListing.setXmls(builder.toString());
		}else
		{
			subListing.setXmls(xmlutil.toXML(t));
		}*/
		
		if(cDataElements == null || cDataElements.length <= 0)
		{
			subListing.setXmls(xmlutil.toXML(t));
		}else
		{
			subListing.setXmls(xmlutil.toXML(t,cDataElements));
		}
		
		logger.debug(subListing.getXmls());
		subListing.setSku(requestProduct.getSku());
		subListing.setPlSku(requestProduct.getPlSku());
		subListing.setPlSkuSaleNum(requestProduct.getPlSkuSaleNum());
		return subListing;
	}
	
	private AmazonPublishSubListing getNewSubListing(AmazonRequestProduct<?> requestProduct,
			Long parentId,
			String msgType,
			boolean parentType,
			Object t)
	{
		return this.getNewSubListing(requestProduct, parentId, msgType, parentType, t,null);
	}

}
