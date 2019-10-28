/*
package com.amazonservices.mws.uploadData.common.mws;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.amazonservices.mws.uploadData.constants.MessageTypeConstant;
import com.amazonservices.mws.uploadData.entity.Amazon.AmazonCategory;
import com.amazonservices.mws.uploadData.entity.Amazon.AmazonRequestProduct;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.amazonservices.mws.uploadData.constants.AmazonConstants;
import com.amazonservices.mws.uploadData.constants.AmazonPostMethod;
import com.rondaful.cloud.seller.entity.Amazon.AmazonCategory;
import com.rondaful.cloud.seller.entity.Amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.AmazonEnvelope;
import com.rondaful.cloud.seller.generated.Header;
import com.rondaful.cloud.seller.generated.Relationship;
import com.rondaful.cloud.seller.service.AmazonCategoryService;
import com.amazonservices.mws.uploadData.utils.AmazonEquenceUtil;
import com.amazonservices.mws.uploadData.utils.ClassXmlUtil;
import com.amazonservices.mws.uploadData.utils.ComposeInfoUtil;

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
	*/
/**
	 *     生成xml<br/>
	 *     生成xml后，将会以文件方式保存，文件以版本号命名如：1.135559854788.xml 
	 * @param arpobj
	 * 		转xml的对对象
	 *//*

	public List<String> createXML(AmazonRequestProduct<?> arpobj) {
		init();
		try {
			List<String> versions = new ArrayList<>();
			// -------------- 头 ------------------------//
			Header header = ComposeInfoUtil.createHeader(arpobj.getMerchantIdentifier(), version.get());

			// =================== 父商品信息开始 1 ===========================
			AmazonEnvelope envProduct = new AmazonEnvelope();
			envProduct.setHeader(header);
			envProduct.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_PRODUCT);
			envProduct.setPurgeAndReplace(Boolean.FALSE);
			List<AmazonCategory> categorys = getCategoryList(arpobj.getProductCategory());
			envProduct.getMessage().add(ComposeInfoUtil.composeBaseProduct(arpobj, Boolean.TRUE,categorys));

			// =================== 库存信息 2 ===========================
			AmazonEnvelope envInventory = new AmazonEnvelope();
			envInventory.setHeader(header);
			envInventory.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_INVENTORY);
			envInventory.setPurgeAndReplace(Boolean.FALSE);
			envInventory.getMessage().add(ComposeInfoUtil.composeBaseInventory(arpobj));

			// -----------------------ProductImage 3 ------------------------------ //
			AmazonEnvelope envProductImage = ComposeInfoUtil.composeBaseProductImage(arpobj);
			envProductImage.setHeader(header);

			// -----------------------Price 4 ------------------------------ //
			AmazonEnvelope envPrice = new AmazonEnvelope();
			envPrice.setHeader(header);
			envPrice.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_PRICE);
			envPrice.setPurgeAndReplace(Boolean.FALSE);
			envPrice.getMessage().add(ComposeInfoUtil.composeBasePrice(arpobj));
			
			
			String varVersion = "";
			String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\r\n<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amznenvelope.xsd\">";
			String xml="";
			
			// ----------------多属性与变体处理 --多属性与变体关系是具有捆绑特性--------------------//
			// ------------------------ 变体5 ---------------------------------------//
			List<AmazonRequestProduct> plist = arpobj.getVarRequestProductList();
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
					relation = new Relationship.Relation();
					relation.setSKU(avrp.getSku());
					relation.setType("Variation");
					envProduct.getMessage().add(ComposeInfoUtil.composeBaseProduct(avrp, Boolean.FALSE,categorys));
					envInventory.getMessage().add(ComposeInfoUtil.composeBaseInventory(avrp));
					envPrice.getMessage().add(ComposeInfoUtil.composeBasePrice(avrp));
					ship.getRelation().add(relation);
					relationshipMessage.setRelationship(ship);
				}
				relationshipMessage.setMessageID(BigInteger.valueOf(AmazonEquenceUtil.VALUE()));
				relationshipMessage.setOperationType("Update");
				envRelationship.getMessage().add(relationshipMessage);
				
				System.out.println("==============  Relationship 变体上报5 ================");
				xml = ClassXmlUtil.toXML(envRelationship);
				xml = xml.replace("<AmazonEnvelope>", xmlHeader);
				varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envRelationship.getHeader().getDocumentVersion()
						+ "_" + envRelationship.getMessageType();
				redisUtils.set(varVersion, xml, (3600 * 24L));
				versions.add(varVersion);
				System.out.println(xml);
			}
			
			
			// result
			System.out.println("============== 商品 上报1 ================");
			xml = ClassXmlUtil.toXML(envProduct);
			xml = xml.replace("<AmazonEnvelope>", xmlHeader);
			varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envProduct.getHeader().getDocumentVersion()
					+ "_" + envProduct.getMessageType();
			redisUtils.set(varVersion, xml, (3600 * 24L));
			versions.add(varVersion);
			System.out.println(xml);
			System.out.println(" ");

			System.out.println("============== Inventory 上报2 ================");
			xml = ClassXmlUtil.toXML(envInventory);
			xml = xml.replace("<AmazonEnvelope>", xmlHeader);
			varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envInventory.getHeader().getDocumentVersion()
					+ "_" + envInventory.getMessageType();
			redisUtils.set(varVersion, xml, (3600 * 24L));
			versions.add(varVersion);
			System.out.println(xml);
			System.out.println(" ");

			System.out.println("============== ProductImage 图片上报3 ================");
			xml = ClassXmlUtil.toXML(envProductImage);
			xml = xml.replace("<AmazonEnvelope>", xmlHeader);
			varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envProductImage.getHeader().getDocumentVersion()
					+ "_" + envProductImage.getMessageType();
			redisUtils.set(varVersion, xml, (3600 * 24L));
			versions.add(varVersion);
			System.out.println(xml);
			System.out.println(" ");

			System.out.println("============== price 价格上报4 ================");
			xml = ClassXmlUtil.toXML(envPrice);
			xml = xml.replace("<AmazonEnvelope>", xmlHeader);
			varVersion = AmazonConstants.REDIS_AMAZON_PREFIX + envPrice.getHeader().getDocumentVersion()
					+ "_" + envPrice.getMessageType();
			redisUtils.set(varVersion, xml, (3600 * 24L));
			versions.add(varVersion);
			System.out.println(xml);
			System.out.println(" ");

			
			return versions;
		} catch (Exception e) {
			logger.error("生成xml转换异常", e);
		}
		return null;
	}
	
	*/
/**
	 * 转换类型
	 * @param greanVersion
	 * @return
	 *//*

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

}
*/
