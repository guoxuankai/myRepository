package com.rondaful.cloud.seller.common.mws;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.common.task.LoadProductRequest;
import com.rondaful.cloud.seller.constants.AmazonConstants;
import com.rondaful.cloud.seller.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.constants.MessageTypeConstant;
import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;
import com.rondaful.cloud.seller.entity.amazon.AmazonQueryLoadTaskResult;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import com.rondaful.cloud.seller.generated.Header;
import com.rondaful.cloud.seller.generated.Relationship;
import com.rondaful.cloud.seller.service.AmazonCategoryService;
import com.rondaful.cloud.seller.utils.AmazonEquenceUtil;
import com.rondaful.cloud.seller.utils.ClassXmlUtil;
import com.rondaful.cloud.seller.utils.ComposeInfoUtil;

@Component
public class AmazonConvert {
	
	private final Logger logger = LoggerFactory.getLogger(AmazonConvert.class);
	@Autowired
	RedisUtils redisUtils;
	
	@Autowired
	AmazonCategoryService amazonCategoryService;
	
	
	private ThreadLocal<String> version = new ThreadLocal<String>();
	public void init() {
		int v1 = new Random().nextInt(2) + 1;
		int v2 = new Random().nextInt(2) + 2;
		version.set(v1 + "." + v2);
	}

	private List<AmazonCategory> getCategoryList(Long[] categorys)
	{
		if(categorys.length < 1)
		{
			logger.error("参数错误，分类参数为空");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "未能获取分类");
		}
		Integer queryParams [] = new Integer[categorys.length];
		queryParams[0] = categorys[0].intValue();
		if(categorys.length > 1) queryParams[1] = categorys[1].intValue();
		
		List<AmazonCategory> list =  amazonCategoryService.selectCategoryListById(queryParams);
		if(CollectionUtils.isEmpty(list))
		{
			logger.error("根据分类，当前category_id:{},在数据库中找不到");
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "找不到分类数据");
		}

		// TODO 如果list的顺序与cat[]的顺序不一致，需要做处理
		return list;
	}
	/**
	 *     生成xml<br/>
	 *     生成xml后，将会以文件方式保存，文件以版本号命名如：1.135559854788.xml 
	 * @param arpobj
	 * 		转xml的对对象
	 *//*
	public List<String> createXML1(final AmazonRequestProduct<?> arpobj) {
		init();
		try {
			
			ClassXmlUtil xmlutil = new ClassXmlUtil();
			
			ComposeInfoUtil compose = new ComposeInfoUtil();
			List<String> versions = new ArrayList<>();
			// -------------- 头 ------------------------//
			Header header = compose.createHeader(arpobj.getMerchantIdentifier(), version.get());

			// =================== 父商品信息开始 1 ===========================
			AmazonEnvelope envProduct = new AmazonEnvelope();
			envProduct.setHeader(header);
			envProduct.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_PRODUCT);
			envProduct.setPurgeAndReplace(Boolean.FALSE);
			List<AmazonCategory> categorys = getCategoryList(arpobj.getProductCategory());
			envProduct.getMessage().add(compose.composeBaseProduct(arpobj, Boolean.TRUE,categorys));

			// =================== 库存信息 2 ===========================
			AmazonEnvelope envInventory = new AmazonEnvelope();
			envInventory.setHeader(header);
			envInventory.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_INVENTORY);
			envInventory.setPurgeAndReplace(Boolean.FALSE);
			envInventory.getMessage().add(compose.composeBaseInventory(arpobj));

			// -----------------------ProductImage 3 ------------------------------ //
			AmazonEnvelope envProductImage = compose.composeBaseProductImage(arpobj);
			envProductImage.setHeader(header);

			// -----------------------Price 4 ------------------------------ //
			AmazonEnvelope envPrice = new AmazonEnvelope();
			envPrice.setHeader(header);
			envPrice.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_PRICE);
			envPrice.setPurgeAndReplace(Boolean.FALSE);
			envPrice.getMessage().add(compose.composeBasePrice(arpobj));
			
			
			String varVersion = "";
			// String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">";
			String xmlHeader = new String( "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">"
					.getBytes(),"utf-8");
			String xml="";
			
			// ----------------多属性与变体处理 --多属性与变体关系是具有捆绑特性--------------------//
			// ------------------------ 变体5 ---------------------------------------//
			List<AmazonRequestProduct> plist = arpobj.getVarRequestProductList();
			arpobj.setIsMultiattribute(Boolean.FALSE);
			if(CollectionUtils.isNotEmpty(plist))
			{
				arpobj.setIsMultiattribute(Boolean.TRUE);
			}
			if(arpobj.getIsMultiattribute())
			{
				AmazonEnvelope envRelationship = new AmazonEnvelope();
				envRelationship.setHeader(header);
				envRelationship.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_RELATIONSHIP);
				envRelationship.setPurgeAndReplace(Boolean.FALSE);
				AmazonEnvelope.Message relationshipMessage = new AmazonEnvelope.Message();
				Relationship ship = new Relationship();
				ship.setParentSKU(arpobj.getSku());
				Relationship.Relation relation = null;
				for (AmazonRequestProduct<?> avrp : plist) { // 多属性与变体集中在这里处理了
					//------------- 设置公共部份  bgein-------------
					avrp.setTemplatesName(arpobj.getTemplatesName());
					avrp.setTemplatesName2(arpobj.getTemplatesName2());
					avrp.setCountryCode(arpobj.getCountryCode());
					avrp.setIsMultiattribute(arpobj.getIsMultiattribute());
					avrp.setMerchantIdentifier(arpobj.getMerchantIdentifier());
					avrp.setManufacturer(arpobj.getManufacturer());
					avrp.setBulletPoint(arpobj.getBulletPoint());
					avrp.setSearchTerms(arpobj.getSearchTerms());
					avrp.setConditionInfo(arpobj.getConditionInfo());
					avrp.setBrand(arpobj.getBrand());
					avrp.setDescription(arpobj.getDescription());
					avrp.setDimensionUnitOfMeasure(arpobj.getDimensionUnitOfMeasure());
					avrp.setDimensionHeight(arpobj.getDimensionHeight());
					avrp.setDimensionLength(arpobj.getDimensionLength());
					avrp.setDimensionWidth(arpobj.getDimensionWidth());
					
					avrp.setWeightUnitOfMeasure(arpobj.getWeightUnitOfMeasure());
					avrp.setPackageWeight(arpobj.getPackageWeight());
					avrp.setItemWeight(arpobj.getItemWeight());
					// avrp.setCategoryPropertyJson(arpobj.getCategoryPropertyJson());
					//------------- 设置公共部份  end -------------
					
					
					relation = new Relationship.Relation();
					relation.setSKU(avrp.getSku());
					relation.setType("Variation");
					envProduct.getMessage().add(compose.composeBaseProduct(avrp, Boolean.FALSE,categorys));
					envInventory.getMessage().add(compose.composeBaseInventory(avrp));
					envPrice.getMessage().add(compose.composeBasePrice(avrp));
					ship.getRelation().add(relation);
					relationshipMessage.setRelationship(ship);
				}
				relationshipMessage.setMessageID(BigInteger.valueOf(AmazonEquenceUtil.VALUE()));
				relationshipMessage.setOperationType("Update");
				envRelationship.getMessage().add(relationshipMessage);
				
				
				logger.debug("==============  Relationship 变体上报5 ================");
				xml = xmlutil.toXML(envRelationship);
				xml = xml.replace("<AmazonEnvelope>", xmlHeader);
				varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envRelationship.getHeader().getDocumentVersion()
						+ "_" + envRelationship.getMessageType();
				redisUtils.set(varVersion, xml, (3600 * 24L));
				versions.add(varVersion);
				logger.debug(xml);
			}
			
			
			// result
			logger.debug("============== 商品 上报1 ================");
			xml = xmlutil.toXML(envProduct);
			xml = xml.replace("<AmazonEnvelope>", xmlHeader);
			varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envProduct.getHeader().getDocumentVersion()
					+ "_" + envProduct.getMessageType();
			redisUtils.set(varVersion, xml, (3600 * 24L));
			versions.add(varVersion);
			logger.debug(xml);
			logger.debug(" ");

			logger.debug("============== Inventory 上报2 ================");
			xml = xmlutil.toXML(envInventory);
			xml = xml.replace("<AmazonEnvelope>", xmlHeader);
			varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envInventory.getHeader().getDocumentVersion()
					+ "_" + envInventory.getMessageType();
			redisUtils.set(varVersion, xml, (3600 * 24L));
			versions.add(varVersion);
			logger.debug(xml);
			logger.debug(" ");

			logger.debug("============== ProductImage 图片上报3 ================");
			xml = xmlutil.toXML(envProductImage);
			xml = xml.replace("<AmazonEnvelope>", xmlHeader);
			varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envProductImage.getHeader().getDocumentVersion()
					+ "_" + envProductImage.getMessageType();
			redisUtils.set(varVersion, xml, (3600 * 24L));
			versions.add(varVersion);
			logger.debug(xml);
			logger.debug(" ");

			logger.debug("============== price 价格上报4 ================");
			xml = xmlutil.toXML(envPrice);
			xml = xml.replace("<AmazonEnvelope>", xmlHeader);
			varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envPrice.getHeader().getDocumentVersion()
					+ "_" + envPrice.getMessageType();
			redisUtils.set(varVersion, xml, (3600 * 24L));
			versions.add(varVersion);
			logger.debug(xml);
			logger.debug(" ");
			return versions;
		} catch (Exception e) {
			logger.error("生成xml转换异常", e);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
		}
	}*/
	
	/**
	 * 转换类型
	 * @param greanVersion
	 * @return
	 */
	public static String getPostMethod(String greanVersion)
	{
		if(greanVersion == null || greanVersion.indexOf("_") <= 0)
		{
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "接口类型转换不正确");
		}
		String ver = greanVersion.substring(greanVersion.lastIndexOf("_")+1,greanVersion.length());
		if(MessageTypeConstant.HEADER_MESSAGETYPE_PRODUCTIMAGE.equals(ver)) 
		{
			return AmazonPostMethod.POST_IMAGE_DATA;
		}else if(MessageTypeConstant.HEADER_MESSAGETYPE_PRODUCT.equals(ver)) 
		{
			return AmazonPostMethod.POST_PRODUCT_DATA;
		}else if(MessageTypeConstant.HEADER_MESSAGETYPE_PRICE.equals(ver))
		{
			return AmazonPostMethod.POST_PRICING_DATA;
		}else if(MessageTypeConstant.HEADER_MESSAGETYPE_INVENTORY.equals(ver))
		{
			return AmazonPostMethod.POST_INVENTORY_DATA;
		}else if(MessageTypeConstant.HEADER_MESSAGETYPE_RELATIONSHIP.equals(ver))
		{
			return AmazonPostMethod.POST_RELATIONSHIP_DATA;
		}
		return AmazonPostMethod.POST_PRODUCT_DATA;
	}

	/**
	 * 
	 * @param body
	 * 		要刊登的xml内容
	 * @param taskResult
	 * 		
	 * @return
	 */
	public synchronized LoadProductRequest toSubmitFeedObject(String body,AmazonQueryLoadTaskResult taskResult,String msgType)
	{
		LoadProductRequest product = new LoadProductRequest();
		product.setAmwToken(taskResult.getAmwToken());
		product.setBody(body);
		product.setMerchantIdentifier(taskResult.getMerchantIdentifier());
		product.setMsgType(msgType);
		product.setPublishSite(taskResult.getPublishSite());
		return product;
	}
}
