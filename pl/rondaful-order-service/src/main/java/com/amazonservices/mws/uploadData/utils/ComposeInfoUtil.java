package com.amazonservices.mws.uploadData.utils;

import com.alibaba.fastjson.JSON;
import com.amazonservices.mws.uploadData.constants.MessageTypeConstant;
import com.amazonservices.mws.uploadData.entity.amazon.AmazonCategory;
import com.amazonservices.mws.uploadData.entity.amazon.AmazonRequestProduct;
import com.amazonservices.mws.uploadData.exception.ProcessXmlException;
import com.rondaful.cloud.common.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.*;
import com.rondaful.cloud.seller.generated.Product.ProductData;
import com.rondaful.cloud.seller.generated.Sports.VariationData;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComposeInfoUtil {

	
	/**
	 * @param merchantIdentifier
	 * @param version
	 * @return
	 */
	public static Header createHeader(String merchantIdentifier,String version)
	{
		Header header = new Header();
    	header.setMerchantIdentifier(merchantIdentifier);
    	header.setDocumentVersion(version);
    	return header;
	}
	/**
     * @param amazonRequestProduct
     * @return
     */
    public static AmazonEnvelope.Message composeBaseProduct(AmazonRequestProduct<?> amazonRequestProduct,
    		boolean isParent,List<AmazonCategory> amazonCategoryList)
    {
    	Product baseProduct = new Product();
    	baseProduct.setSKU(amazonRequestProduct.getSku());
    	// ISBN,UPC,EAN,ASIN,GTIN,GCID,PZN
    	StandardProductID spid = new StandardProductID();
    	spid.setType(amazonRequestProduct.getStandardProductType());
    	spid.setValue(amazonRequestProduct.getStandardProductID());
    	baseProduct.setStandardProductID(spid); 
    	baseProduct.setDescriptionData(new Product.DescriptionData());
    	baseProduct.getDescriptionData().setBrand(amazonRequestProduct.getBrand());
    	baseProduct.getDescriptionData().setManufacturer(amazonRequestProduct.getManufacturer());
    	baseProduct.getDescriptionData().setMfrPartNumber(amazonRequestProduct.getMfrPartNumber());
    	baseProduct.getDescriptionData().setTitle(amazonRequestProduct.getTitle());
    	baseProduct.getDescriptionData().getBulletPoint().addAll(amazonRequestProduct.getBulletPoint());
    	baseProduct.getDescriptionData().getSearchTerms().addAll(amazonRequestProduct.getSearchTerms());
    	// Dimensions.LengthDimension, length、height、width 尺寸
    	Dimensions dis = new Dimensions();
    	if(checkOneNull(amazonRequestProduct.getDimensionUnitOfMeasure(),amazonRequestProduct.getDimensionLength())) {
	    	LengthDimension length = new LengthDimension();
	    	length.setUnitOfMeasure(LengthUnitOfMeasure.valueOf(amazonRequestProduct.getDimensionUnitOfMeasure()));
	    	length.setValue(BigDecimal.valueOf(amazonRequestProduct.getDimensionLength()));
	    	dis.setLength(length);
    	}
    	if(checkOneNull(amazonRequestProduct.getDimensionUnitOfMeasure(),amazonRequestProduct.getDimensionLength())) {
	    	LengthDimension height = new LengthDimension();
	    	height.setUnitOfMeasure(LengthUnitOfMeasure.valueOf(amazonRequestProduct.getDimensionUnitOfMeasure()));
	    	height.setValue(BigDecimal.valueOf(amazonRequestProduct.getDimensionHeight()));
	    	dis.setHeight(height);
    	}
    	if(checkOneNull(amazonRequestProduct.getDimensionUnitOfMeasure(),amazonRequestProduct.getDimensionLength())) {
	    	LengthDimension width = new LengthDimension();
	    	width.setUnitOfMeasure(LengthUnitOfMeasure.valueOf(amazonRequestProduct.getDimensionUnitOfMeasure()));
	    	width.setValue(BigDecimal.valueOf(amazonRequestProduct.getDimensionWidth()));
	    	dis.setWidth(width);
    	}
    	// 重量 
    	baseProduct.getDescriptionData().setItemDimensions(dis);
    	//  包装前重量
    	if(checkOneNull(amazonRequestProduct.getDimensionUnitOfMeasure(),amazonRequestProduct.getDimensionLength())) {
	    	PositiveWeightDimension positiveWeightDimension = new PositiveWeightDimension();
	    	positiveWeightDimension.setUnitOfMeasure(WeightUnitOfMeasure.valueOf(amazonRequestProduct.getWeightUnitOfMeasure()));
	    	positiveWeightDimension.setValue(new BigDecimal(amazonRequestProduct.getPackageWeight()));
	    	baseProduct.getDescriptionData().setPackageWeight(positiveWeightDimension);
    	}
    	//  包装后重量
    	if(checkOneNull(amazonRequestProduct.getDimensionUnitOfMeasure(),amazonRequestProduct.getDimensionLength())) {
	    	WeightDimension weightDimension = new WeightDimension();
	    	weightDimension.setUnitOfMeasure(WeightUnitOfMeasure.valueOf(amazonRequestProduct.getWeightUnitOfMeasure()));
	    	weightDimension.setValue(new BigDecimal(amazonRequestProduct.getItemWeight()));
	    	baseProduct.getDescriptionData().setItemWeight(weightDimension);
	    	baseProduct.setCondition(amazonRequestProduct.getConditionInfo());
	    	baseProduct.getDescriptionData().setDescription(amazonRequestProduct.getDescription());
    	}
    	// product.getProductData().setSports((Sports) JSON.parseObject(this.categoryPropertyJson, reqClz));
    	if(baseProduct.getProductData() == null) {
    		baseProduct.setProductData(new ProductData());
    	}
    	ClassReflectionUtil.setProductData(baseProduct.getProductData(), 
    			amazonRequestProduct.getCategoryPropertyJson(), 
    			amazonRequestProduct.getTemplatesName());
    	
    	// TODO 变体信息
    	/*HealthMisc hm = new HealthMisc();
    	HealthMisc.UnitCount unitcount = new HealthMisc.UnitCount();
    	unitcount.setUnitOfMeasure("Unittest");
    	unitcount.setValue(BigDecimal.valueOf(30L));
    	hm.setUnitCount(unitcount);
    	hm.setVariationData(new HealthMisc.VariationData());
    	hm.getVariationData().setParentage(isParent == false ? "child" : "parent");
    	hm.getVariationData().setSize("L");;
    	hm.getVariationData().setVariationTheme("Size");
    	Health.ProductType pt =new Health.ProductType();
    	pt.setHealthMisc(hm);
    	Health health = new Health();
    	
    	baseProduct.getProductData().setHealth(health);
    	baseProduct.getProductData().getHealth().setProductType(pt);*/
    	if(!"US".equals(amazonRequestProduct.getCountryCode())) // 
    	{
    		for(AmazonCategory cat : amazonCategoryList)
    		{
    			Map<String,BigInteger> attr = toMapBigInteger(cat.getAttributes());
    			List<BigInteger> nodeList = attr.values().stream().collect(Collectors.toList());
    			baseProduct.getDescriptionData().getRecommendedBrowseNode().add(nodeList.get(0));
    		}
    		// baseProduct.getDescriptionData().getRecommendedBrowseNode().add(new BigInteger(amazonRequestProduct.getProductCategory()));
    	}else{
    		AmazonCategory cat = amazonCategoryList.get(0);
    		Map<String,String> attr =  toMapString(cat.getAttributes());
    		baseProduct.getDescriptionData().setItemType(attr.values().iterator().next());
    	}
      	AmazonEnvelope.Message productMessage = new AmazonEnvelope.Message();
    	productMessage.setMessageID(BigInteger.valueOf(AmazonEquenceUtil.VALUE()));
    	productMessage.setOperationType("Update");
    	productMessage.setProduct(baseProduct);
    	return productMessage;
    }
    
    /** 判二个值，一个为空，或二个都为空，  返回true为正常数据 */
    private static boolean checkOneNull(Object arg1,Object arg2) throws ProcessXmlException
    {
    	// 都不空，或 都为空，
    	if((arg1 != null && arg2 != null))
    	{
    		return Boolean.TRUE;
    	}
    	if(arg1 == null && arg2 == null)
    	{
    		return Boolean.FALSE;
    	}
    	throw new ProcessXmlException(ResponseCodeEnum.RETURN_CODE_100403,"参数错误");
    	/*if((arg1 == null || arg2 != null) || (arg1 != null || arg2 == null))
    	{
    		throw new ProcessXmlException(ResponseCodeEnum.RETURN_CODE_100403,"参数错误");
    	}
    	return Boolean.TRUE;*/
    }
    
    private static Map<String,BigInteger> toMapBigInteger(String json)
    {
    	
    	return JSON.parseObject(json, Map.class);
    }
    private static Map<String,String> toMapString(String json)
    {
    	return JSON.parseObject(json, Map.class);
    }
    /**
     * @param amazonRequestProduct
     * @return
     */
    public static AmazonEnvelope.Message composeBaseInventory(AmazonRequestProduct<?> amazonRequestProduct)
    {
    	Inventory inventory = new Inventory();
    	inventory.setSKU(amazonRequestProduct.getSku());
    	inventory.setQuantity(BigInteger.valueOf(amazonRequestProduct.getQuantity()));
    	AmazonEnvelope.Message inventoryMessage = new AmazonEnvelope.Message();
    	inventoryMessage.setMessageID(BigInteger.valueOf(AmazonEquenceUtil.VALUE()));
    	inventoryMessage.setOperationType("Update");
    	inventoryMessage.setInventory(inventory);
    	return inventoryMessage;
    }
    
    
    /**
     * @param amazonRequestProduct
     * @return
     */
    public static AmazonEnvelope composeBaseProductImage(AmazonRequestProduct<?> amazonRequestProduct)
    {
    	AmazonEnvelope envProductImage = new AmazonEnvelope();
    	envProductImage.setMessageType(MessageTypeConstant.HEADER_MESSAGETYPE_PRODUCTIMAGE);
    	envProductImage.setPurgeAndReplace(Boolean.FALSE);
    	
    	AmazonEnvelope.Message productImageMessage = new AmazonEnvelope.Message();
    	List<ProductImage>  list = amazonRequestProduct.getImages();
    	for(ProductImage image : list)
    	{
    		image.setSKU(amazonRequestProduct.getSku());
    		productImageMessage = new AmazonEnvelope.Message();
    		productImageMessage.setMessageID(BigInteger.valueOf(AmazonEquenceUtil.VALUE()));
    		productImageMessage.setOperationType("Update");
    		productImageMessage.setProductImage(image);
    		
    		envProductImage.getMessage().add(productImageMessage);
    	}
    	
    	return envProductImage;
    }
    /**
     * @param amazonRequestProduct
     * @return
     */
    public static AmazonEnvelope.Message composeBasePrice(AmazonRequestProduct<?> amazonRequestProduct)
    {
    	AmazonEnvelope.Message priceMessage = new AmazonEnvelope.Message();
    	Price price = new Price();
    	OverrideCurrencyAmount overrideCurrencyAmount = new OverrideCurrencyAmount();
    	overrideCurrencyAmount.setCurrency(BaseCurrencyCodeWithDefault.valueOf(amazonRequestProduct.getStandardPriceUnit()));
    	overrideCurrencyAmount.setValue(amazonRequestProduct.getStandardPrice());
    	price.setStandardPrice(overrideCurrencyAmount);
    	price.setSKU(amazonRequestProduct.getSku());
    	priceMessage.setPrice(price);
    	priceMessage.setMessageID(BigInteger.valueOf(AmazonEquenceUtil.VALUE()));
    	priceMessage.setOperationType("Update");
    	//envPrice.getMessage().add(priceMessage);
    	return priceMessage;
    }


    public static void main(String[] args) {
		Sports sports = new Sports();
//		sports.setAlarm("alarm");
//		sports.setAvailableCourses("availableCourses");
		sports.setVariationData(new VariationData());
//		sports.getVariationData().setParentage("xxxxxxxxxxx");
		sports.getVariationData().setAgeGenderCategory("setAgeGenderCategory");

		AmperageDimension ad = new AmperageDimension();
		ad.setValue(new BigDecimal(100));
		ad.setUnitOfMeasure(AmperageUnitOfMeasure.AMPS);
		sports.getVariationData().setAmperage(ad);
		
		
		System.out.println(JSON.toJSONString(sports));
    	//System.out.println(checkOneNull(null,null));
    	
    	
	}
}
