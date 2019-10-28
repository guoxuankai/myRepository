package com.rondaful.cloud.seller.common.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.common.spring.ApplicationContextProvider;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.alibaba.fastjson.JSON;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.generated.Inventory;
import com.rondaful.cloud.seller.generated.Price;
import com.rondaful.cloud.seller.generated.Product;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;
import com.rondaful.cloud.seller.utils.AmazonSubListingUtil;
import com.rondaful.cloud.seller.utils.ClassXmlUtil;

/**
 * 根据提交项生成xml
 * @author ouxiangfeng
 */
public class ProcessXmlBatchUpdateTask implements Callable<String>  { //如果使用Callable会等待，但这里又不想等待
 
	private final Logger logger = LoggerFactory.getLogger(ProcessXmlBatchUpdateTask.class);
	
	AmazonRequestProduct<?> requestProduct;
	boolean createProductXml;
	boolean createPriceXml;
	boolean createInventoryXml;
	// product专用
	private static final String [] cDataElements = { "^Title","^Description","^MfrPartNumber","^SKU"};

	/**
	 * 根据提交项 生成xml
	 * @param requestProduct
	 */
	public ProcessXmlBatchUpdateTask(AmazonRequestProduct<?> requestProduct,boolean createProductXml,boolean createPriceXml,boolean createInventoryXml)
	{
		this.requestProduct = requestProduct;
		this.createProductXml = createProductXml;
		this.createPriceXml = createPriceXml;
		this.createInventoryXml = createInventoryXml;
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
			AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
			AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
			//AmazonConvert amazonConvert = (AmazonConvert) ApplicationContextProvider.getBean("amazonConvert");
			//RedisUtils redisUtils = (RedisUtils) ApplicationContextProvider.getBean("redisUtils");
			AmazonTemplateGenerateBatchUpdate amazonTemplateGenerate = (AmazonTemplateGenerateBatchUpdate) ApplicationContextProvider.getBean("amazonTemplateGenerateBatchUpdate");
			AmazonSubListingUtil amazonSubListingUtil = (AmazonSubListingUtil) ApplicationContextProvider.getBean("amazonSubListingUtil");
			
			ClassXmlUtil xmlutil = new ClassXmlUtil();
			List<AmazonPublishSubListing> subListing = new ArrayList<AmazonPublishSubListing>();
			Product product =null;
			Price price = null;
			Inventory inventory =null;
			if(createProductXml) {
				//父商品
				 product = amazonTemplateGenerate.generagteProduct(requestProduct, Boolean.TRUE);
				
				logger.debug("==============  生成商品xml ================");
				logger.debug(xmlutil.toXML(product,cDataElements));
				
			}
			if(createPriceXml) {
				//价格
				 price = amazonTemplateGenerate.generagtePrice(requestProduct, Boolean.TRUE);
				logger.debug("==============  生成价格 xml ================");
				logger.debug(xmlutil.toXML(price,new String[] {"^SKU"}));
			}
			
			if(createInventoryXml) {
				// 库存
				 inventory = amazonTemplateGenerate.generagteInventory(requestProduct, Boolean.TRUE);
				logger.debug("==============  生成库存 xml ================");
				logger.debug(xmlutil.toXML(inventory,new String[] {"^SKU"}));
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
 
			//ExecutorService executor = Executors.newFixedThreadPool(5);
			//amazonPublishsubListingService.deleteForBaseId(listingObj.getId());
			
			
			List<AmazonRequestProduct> plist = requestProduct.getVarRequestProductList();
			requestProduct.setIsMultiattribute(Boolean.FALSE);
			if(CollectionUtils.isNotEmpty(plist))
			{
				requestProduct.setIsMultiattribute(Boolean.TRUE);
			}
			if(requestProduct.getIsMultiattribute())
			{
				for (AmazonRequestProduct<?> avrp : plist) { // 多属性与变体集中在这里处理了
					//------------- 设置公共部份  bgein-------------
					avrp.setTemplatesName(requestProduct.getTemplatesName());
					avrp.setTemplatesName2(requestProduct.getTemplatesName2());
					avrp.setCountryCode(requestProduct.getCountryCode());
					avrp.setIsMultiattribute(requestProduct.getIsMultiattribute());
					avrp.setMerchantIdentifier(requestProduct.getMerchantIdentifier());
					avrp.setManufacturer(requestProduct.getManufacturer());
					avrp.setBulletPoint(requestProduct.getBulletPoint());
					avrp.setSearchTerms(requestProduct.getSearchTerms());
					avrp.setConditionInfo(requestProduct.getConditionInfo());
					avrp.setBrand(requestProduct.getBrand());
					avrp.setDescription(requestProduct.getDescription());
					avrp.setDimensionUnitOfMeasure(requestProduct.getDimensionUnitOfMeasure());
					avrp.setDimensionHeight(requestProduct.getDimensionHeight());
					avrp.setDimensionLength(requestProduct.getDimensionLength());
					avrp.setDimensionWidth(requestProduct.getDimensionWidth());
					
					avrp.setWeightUnitOfMeasure(requestProduct.getWeightUnitOfMeasure());
					avrp.setPackageWeight(requestProduct.getPackageWeight());
					avrp.setItemWeight(requestProduct.getItemWeight());
					avrp.setProductCategory(requestProduct.getProductCategory());
					
					// avrp.setCategoryPropertyJson(arpobj.getCategoryPropertyJson());
					//------------- 设置公共部份  end -------------
					
					Product childProduct = amazonTemplateGenerate.generagteProduct(avrp, Boolean.FALSE);
					
					//envInventory.getMessage().add(compose.composeBaseInventory(avrp));
					Inventory childInventory = amazonTemplateGenerate.generagteInventory(avrp, Boolean.FALSE);
					
					//envPrice.getMessage().add(compose.composeBasePrice(avrp));
					Price childPrice = amazonTemplateGenerate.generagtePrice(avrp, Boolean.FALSE);
					
					
					subListing.add(getNewSubListing(avrp,listingObj.getId(),AmazonPostMethod.POST_PRODUCT_DATA,Boolean.FALSE,childProduct,cDataElements));
					subListing.add(getNewSubListing(avrp,listingObj.getId(),AmazonPostMethod.POST_PRICING_DATA,Boolean.FALSE,childPrice,new String[] {"^SKU"}));
					subListing.add(getNewSubListing(avrp,listingObj.getId(),AmazonPostMethod.POST_INVENTORY_DATA,Boolean.FALSE,childInventory,new String[] {"^SKU"}));
					
				}
				
			}

			if(product != null) {
				subListing.add(getNewSubListing(requestProduct,listingObj.getId(),AmazonPostMethod.POST_PRODUCT_DATA,Boolean.TRUE,product,cDataElements));
			}
			if(price != null) {
				subListing.add(getNewSubListing(requestProduct,listingObj.getId(),AmazonPostMethod.POST_PRICING_DATA,Boolean.TRUE,price,new String[] {"^SKU"}));
			}
			if(inventory != null) {
				subListing.add(getNewSubListing(requestProduct,listingObj.getId(),AmazonPostMethod.POST_INVENTORY_DATA,Boolean.TRUE,inventory,new String[] {"^SKU"}));
			}
			
			if(!CollectionUtils.isEmpty(subListing)) {
				amazonSubListingUtil.setPlSkuStatusAndCount(subListing, requestProduct.getWarehouseId());
				logger.info("---insertBatch()---批量添加参数{}",JSON.toJSONString(subListing));
				int insertBatch = amazonPublishsubListingService.insertBatch(subListing);
				///--------2019-07-24
				if(insertBatch>0) {
					//添加成功后删除之前的被添加类型数据
					subListing.forEach(sub->{
						sub.setProcessStatus(AmazonConstants.RESPORT_RESULT_COMPLETE);
						amazonPublishsubListingService.deleteBatchBylistingIdProcessStatusMsgTypeParentType(sub);
					});
				}
			}
			
			
	/*		AmazonPublishListing amazonPublishListing = new AmazonPublishListing();
			amazonPublishListing.setId(listingObj.getId());
			amazonPublishListing.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_REST_PUSH);
			amazonPublishListingService.updateByPrimaryKeySelective(amazonPublishListing);*/
			
			return "";
		}catch(Exception e)
		{
			logger.error("",e);
			return e.getMessage();
		}
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
		subListing.setAsin(requestProduct.getAsin());
		
		MarketplaceId marketplaceId = MarketplaceIdList.createMarketplaceForKeyId().get(requestProduct.getCountryCode());
		if(marketplaceId == null)
		{
			marketplaceId = MarketplaceIdList.createMarketplace().get(requestProduct.getCountryCode());
		}
		if(marketplaceId == null)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100400,"请求参数错误");
		}
		
		subListing.setMarketplaceId(marketplaceId.getMarketplaceId());
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
