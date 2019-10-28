package com.rondaful.cloud.seller.common.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.seller.enums.AmazonPublishEnums;
import com.rondaful.cloud.seller.remote.RemoteLogisticsService;
import com.rondaful.cloud.seller.utils.AmazonSubListingUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.common.spring.ApplicationContextProvider;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.entity.AmazonPublishListing;
import com.rondaful.cloud.seller.entity.AmazonPublishSubListing;
import com.rondaful.cloud.seller.entity.amazon.AmazonPublishListStatus;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.seller.generated.Inventory;
import com.rondaful.cloud.seller.generated.Price;
import com.rondaful.cloud.seller.generated.Product;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.generated.ProductSupplierDeclaredDGHZRegulation;
import com.rondaful.cloud.seller.generated.Relationship;
import com.rondaful.cloud.seller.service.AmazonPublishListingService;
import com.rondaful.cloud.seller.service.AmazonPublishSubListingService;
import com.rondaful.cloud.seller.utils.ClassXmlUtil;

/**
 * 根据提交项生成xml
 * @author ouxiangfeng
 */
public class ProcessXmlTask implements Callable<String>  { //如果使用Callable会等待，但这里又不想等待

	private final Logger logger = LoggerFactory.getLogger(ProcessXmlTask.class);
	
	AmazonRequestProduct<?> requestProduct;
	private AmazonSubListingUtil amazonSubListingUtil;
	
	// product专用
	private static final String [] cDataElements = { "^Title","^Description","^MfrPartNumber","^SKU"};

	/**
	 * 根据提交项 生成xml
	 * @param requestProduct
	 */
	public ProcessXmlTask(AmazonRequestProduct<?> requestProduct)
	{
		this.requestProduct = requestProduct;
	}

	public ProcessXmlTask(AmazonRequestProduct<?> requestProduct,AmazonSubListingUtil amazonSubListingUtil)
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
			AmazonPublishListingService amazonPublishListingService  = (AmazonPublishListingService) ApplicationContextProvider.getBean("amazonPublishListingServiceImpl");
			AmazonPublishSubListingService amazonPublishsubListingService  = (AmazonPublishSubListingService) ApplicationContextProvider.getBean("amazonPublishSubListingServiceImpl");
			//AmazonConvert amazonConvert = (AmazonConvert) ApplicationContextProvider.getBean("amazonConvert");
			//RedisUtils redisUtils = (RedisUtils) ApplicationContextProvider.getBean("redisUtils");
			AmazonTemplateGenerate amazonTemplateGenerate = (AmazonTemplateGenerate) ApplicationContextProvider.getBean("amazonTemplateGenerate");
			//RemoteLogisticsService remoteLogisticsService  = (RemoteLogisticsService) ApplicationContextProvider.getBean("remoteLogisticsServiceImpl");   //todo 可能拿不到
			
			ClassXmlUtil xmlutil = new ClassXmlUtil();
			List<AmazonPublishSubListing> subListing = new ArrayList<AmazonPublishSubListing>();
			
			//父商品
			Product product = amazonTemplateGenerate.generagteProduct(requestProduct, Boolean.TRUE);
			
			logger.debug("==============  生成商品xml ================");
			logger.debug(xmlutil.toXML(product,cDataElements));
			
			//价格
			Price price = amazonTemplateGenerate.generagtePrice(requestProduct, Boolean.TRUE);
			logger.debug("==============  生成价格 xml ================");
			logger.debug(xmlutil.toXML(price,new String[] {"^SKU"}));
			
			// 库存
			Inventory inventory = amazonTemplateGenerate.generagteInventory(requestProduct, Boolean.TRUE);
			logger.debug("==============  生成库存 xml ================");
			logger.debug(xmlutil.toXML(inventory,new String[] {"^SKU"}));
			
			// 图片
			List<ProductImage> productImage = amazonTemplateGenerate.generagteProductImage(requestProduct,  Boolean.TRUE);
			
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
			amazonPublishsubListingService.deleteForBaseId(listingObj.getId());
			
			
			List<AmazonRequestProduct> plist = requestProduct.getVarRequestProductList();
			requestProduct.setIsMultiattribute(Boolean.FALSE);
			if(CollectionUtils.isNotEmpty(plist))
			{
				requestProduct.setIsMultiattribute(Boolean.TRUE);
			}
			List<String> childSkus = new ArrayList<>();
			if(requestProduct.getIsMultiattribute())
			{
				/*AmazonEnvelope envRelationship = new AmazonEnvelope();
				envRelationship.setHeader(header);
				envRelationship.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_RELATIONSHIP);
				envRelationship.setPurgeAndReplace(Boolean.FALSE);
				AmazonEnvelope.Message relationshipMessage = new AmazonEnvelope.Message();
				Relationship ship = new Relationship();
				ship.setParentSKU(arpobj.getSku());
				Relationship.Relation relation = null;*/
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
					avrp.setItemPackageQuantity(requestProduct.getItemPackageQuantity());
					avrp.setNumberOfItems(requestProduct.getNumberOfItems());
					avrp.setDimensionUnitOfMeasure(requestProduct.getDimensionUnitOfMeasure());
					avrp.setDimensionHeight(requestProduct.getDimensionHeight());
					avrp.setDimensionLength(requestProduct.getDimensionLength());
					avrp.setDimensionWidth(requestProduct.getDimensionWidth());
					
					avrp.setWeightUnitOfMeasure(requestProduct.getWeightUnitOfMeasure());
					avrp.setPackageWeight(requestProduct.getPackageWeight());
					avrp.setItemWeight(requestProduct.getItemWeight());
					avrp.setProductCategory(requestProduct.getProductCategory());
					// avrp.setCategoryPropertyJson(arpobj.getCategoryPropertyJson());
					
					//////////////	    add add v2.2.0_5.17 begin //////////
					avrp.setDesigner(requestProduct.getDesigner());
			    	if(CollectionUtils.isNotEmpty(requestProduct.getTargetAudience()))
			    	{
			    		avrp.getTargetAudience().addAll(requestProduct.getTargetAudience());
			    	}
			    	if(CollectionUtils.isNotEmpty(requestProduct.getSupplierDeclaredDGHZRegulation()))
			    	{
			    		avrp.getSupplierDeclaredDGHZRegulation().addAll(requestProduct.getSupplierDeclaredDGHZRegulation());
			    	}
			    	avrp.setHazmatUnitedNationsRegulatoryID(requestProduct.getHazmatUnitedNationsRegulatoryID());
			    	avrp.setSafetyDataSheetURL(requestProduct.getSafetyDataSheetURL());
			    	//////////////add add v2.2.0_5.17 end //////////
					//------------- 设置公共部份  end -------------
					
					
					
					
					/*relation = new Relationship.Relation();
					relation.setSKU(avrp.getSku());
					relation.setType("Variation");*/
					// envProduct.getMessage().add(compose.composeBaseProduct(avrp, Boolean.FALSE,categorys));
					Product childProduct = amazonTemplateGenerate.generagteProduct(avrp, Boolean.FALSE);
					
					//envInventory.getMessage().add(compose.composeBaseInventory(avrp));
					Inventory childInventory = amazonTemplateGenerate.generagteInventory(avrp, Boolean.FALSE);
					
					//envPrice.getMessage().add(compose.composeBasePrice(avrp));
					Price childPrice = amazonTemplateGenerate.generagtePrice(avrp, Boolean.FALSE);
					
					List<ProductImage> childimages = amazonTemplateGenerate.generagteProductImage(avrp, Boolean.FALSE);
					/*ship.getRelation().add(relation);
					relationshipMessage.setRelationship(ship);*/
					childSkus.add(avrp.getSku());
					
					subListing.add(getNewSubListing(avrp,listingObj.getId(),AmazonPostMethod.POST_PRODUCT_DATA,Boolean.FALSE,childProduct,cDataElements));
					subListing.add(getNewSubListing(avrp,listingObj.getId(),AmazonPostMethod.POST_PRICING_DATA,Boolean.FALSE,childPrice,new String[] {"^SKU"}));
					subListing.add(getNewSubListing(avrp,listingObj.getId(),AmazonPostMethod.POST_INVENTORY_DATA,Boolean.FALSE,childInventory,new String[] {"^SKU"}));
					// logger.debug("==============  生成图片 xml ================");
					for(ProductImage image : childimages)
					{
						subListing.add(getNewSubListing(avrp,listingObj.getId(),AmazonPostMethod.POST_IMAGE_DATA,Boolean.FALSE,image,new String[] {"^SKU"}));
					}
				}
				
				Relationship relationship = amazonTemplateGenerate.generagteRelationship(requestProduct.getSku(), childSkus, Boolean.FALSE);
				subListing.add(getNewSubListing(requestProduct,listingObj.getId(),AmazonPostMethod.POST_RELATIONSHIP_DATA,Boolean.FALSE,relationship,new String[] {"^SKU","^ParentSKU"}));
			}
			
			subListing.add(getNewSubListing(requestProduct,listingObj.getId(),AmazonPostMethod.POST_PRODUCT_DATA,Boolean.TRUE,product,cDataElements));
			if(price != null)
			{
				subListing.add(getNewSubListing(requestProduct,listingObj.getId(),AmazonPostMethod.POST_PRICING_DATA,Boolean.TRUE,price,new String[] {"^SKU"}));
			}
			if(inventory != null)
			{
				subListing.add(getNewSubListing(requestProduct,listingObj.getId(),AmazonPostMethod.POST_INVENTORY_DATA,Boolean.TRUE,inventory,new String[] {"^SKU"}));
			}
			logger.debug("==============  生成图片 xml ================");
			for(ProductImage image : productImage)
			{
				subListing.add(getNewSubListing(requestProduct,listingObj.getId(),AmazonPostMethod.POST_IMAGE_DATA,Boolean.TRUE,image,new String[] {"^SKU"}));
				// logger.debug(xmlutil.toXML(image));
			}

			// --
			Integer warehouseId = requestProduct.getWarehouseId();
			this.amazonSubListingUtil.setPlSkuStatusAndCount(subListing,warehouseId);

			amazonPublishsubListingService.insertBatch(subListing);
			
			AmazonPublishListing amazonPublishListing = new AmazonPublishListing();
			amazonPublishListing.setId(listingObj.getId());
			amazonPublishListing.setPublishStatus(AmazonPublishListStatus.AMAZON_PUBLISH_STATUS_AWAIT);
			amazonPublishListingService.updateByPrimaryKeySelective(amazonPublishListing);
			
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
