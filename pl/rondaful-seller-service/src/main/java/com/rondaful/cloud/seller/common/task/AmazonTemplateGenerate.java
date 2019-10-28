package com.rondaful.cloud.seller.common.task;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.BaseCurrencyCodeWithDefault;
import com.rondaful.cloud.seller.generated.Dimensions;
import com.rondaful.cloud.seller.generated.Inventory;
import com.rondaful.cloud.seller.generated.LengthDimension;
import com.rondaful.cloud.seller.generated.LengthUnitOfMeasure;
import com.rondaful.cloud.seller.generated.OverrideCurrencyAmount;
import com.rondaful.cloud.seller.generated.PositiveWeightDimension;
import com.rondaful.cloud.seller.generated.Price;
import com.rondaful.cloud.seller.generated.Product;
import com.rondaful.cloud.seller.generated.Product.ProductData;
import com.rondaful.cloud.seller.generated.ProductImage;
import com.rondaful.cloud.seller.generated.ProductSupplierDeclaredDGHZRegulation;
import com.rondaful.cloud.seller.generated.Relationship;
import com.rondaful.cloud.seller.generated.StandardProductID;
import com.rondaful.cloud.seller.generated.WeightDimension;
import com.rondaful.cloud.seller.generated.WeightUnitOfMeasure;
import com.rondaful.cloud.seller.service.AmazonCategoryService;
import com.rondaful.cloud.seller.utils.ClassReflectionUtil;

@Component
public class AmazonTemplateGenerate extends AbstractBaseGenerate {

	private final Logger logger = LoggerFactory.getLogger(AmazonTemplateGenerate.class);
	
	@Autowired
	AmazonCategoryService amazonCategoryService;
	
	@Autowired
	RedisUtils redisUtils;
	
	@Override
	Product generagteProduct(AmazonRequestProduct<?> amazonRequestProduct , boolean isParent) {
//		ComposeInfoUtil compose = new ComposeInfoUtil();
		List<AmazonCategory> categorys = getCategoryList(amazonRequestProduct.getProductCategory());
		try
    	{
	    	Product baseProduct = new Product();
	    	baseProduct.setSKU(amazonRequestProduct.getSku());
	    	if(StringUtils.isNotBlank(amazonRequestProduct.getStandardProductID())) // upc有可能为空。为空是允许的，只有不为空时才会设置值项
	    	{
	    		// ISBN,UPC,EAN,ASIN,GTIN,GCID,PZN
		    	StandardProductID spid = new StandardProductID();
		    	spid.setType(amazonRequestProduct.getStandardProductType());
		    	spid.setValue(amazonRequestProduct.getStandardProductID());
		    	baseProduct.setStandardProductID(spid); 
	    	}
	    	
	    	// add add v2.2.0_5.17
	    	baseProduct.setItemPackageQuantity(amazonRequestProduct.getItemPackageQuantity());
	    	baseProduct.setNumberOfItems(amazonRequestProduct.getNumberOfItems());
	    	
	    	baseProduct.setDescriptionData(new Product.DescriptionData());
	    	baseProduct.getDescriptionData().setBrand(addCDATA(amazonRequestProduct.getBrand()));
	    	baseProduct.getDescriptionData().setManufacturer(amazonRequestProduct.getManufacturer());
	    	baseProduct.getDescriptionData().setMfrPartNumber(amazonRequestProduct.getMfrPartNumber());
	    	baseProduct.getDescriptionData().setTitle(addCDATA(amazonRequestProduct.getTitle()));
	    	baseProduct.getDescriptionData().getBulletPoint().addAll(amazonRequestProduct.getBulletPoint());
	    	baseProduct.getDescriptionData().getSearchTerms().addAll(amazonRequestProduct.getSearchTerms());
	    	
	    	
	    	//////////////    add add v2.2.0_5.17 begin //////////
	    	baseProduct.getDescriptionData().setDesigner(amazonRequestProduct.getDesigner());
	    	if(CollectionUtils.isNotEmpty(amazonRequestProduct.getTargetAudience()))
	    	{
	    		for(String audience : amazonRequestProduct.getTargetAudience())
	    		{
	    			if(StringUtils.isBlank(audience))
	    			{
	    				continue;
	    			}
	    			baseProduct.getDescriptionData().getTargetAudience().add(audience);
	    		}
	    	}
	    	
	    	
	    	if(CollectionUtils.isNotEmpty(amazonRequestProduct.getSupplierDeclaredDGHZRegulation()))
	    	{
	    		for(String sddghzre : amazonRequestProduct.getSupplierDeclaredDGHZRegulation())
	    		{
	    			if(StringUtils.isBlank(sddghzre))
	    			{
	    				continue;
	    			}
	    			baseProduct.getDescriptionData().getSupplierDeclaredDGHZRegulation().add(ProductSupplierDeclaredDGHZRegulation.fromValue(sddghzre));
	    		}
	    	}
	    	baseProduct.getDescriptionData().setHazmatUnitedNationsRegulatoryID(amazonRequestProduct.getHazmatUnitedNationsRegulatoryID());
	    	baseProduct.getDescriptionData().setSafetyDataSheetURL(amazonRequestProduct.getSafetyDataSheetURL());
	    	//////////////add add v2.2.0_5.17 end //////////
	    	
	    	
	    	// Dimensions.LengthDimension, length、height、width 尺寸
	    	Dimensions dis = new Dimensions();
	    	if(checkOneNull(amazonRequestProduct.getDimensionUnitOfMeasure(),amazonRequestProduct.getDimensionLength())) {
		    	LengthDimension length = new LengthDimension();
		    	length.setUnitOfMeasure(LengthUnitOfMeasure.fromValue(amazonRequestProduct.getDimensionUnitOfMeasure()));
		    	length.setValue(amazonRequestProduct.getDimensionLength());
		    	dis.setLength(length);
	    	}
	    	if(checkOneNull(amazonRequestProduct.getDimensionUnitOfMeasure(),amazonRequestProduct.getDimensionHeight())) {
		    	LengthDimension height = new LengthDimension();
		    	height.setUnitOfMeasure(LengthUnitOfMeasure.fromValue(amazonRequestProduct.getDimensionUnitOfMeasure()));
		    	height.setValue(amazonRequestProduct.getDimensionHeight());
		    	dis.setHeight(height);
	    	}
	    	if(checkOneNull(amazonRequestProduct.getDimensionUnitOfMeasure(),amazonRequestProduct.getDimensionWidth())) {
		    	LengthDimension width = new LengthDimension();
		    	width.setUnitOfMeasure(LengthUnitOfMeasure.fromValue(amazonRequestProduct.getDimensionUnitOfMeasure()));
		    	width.setValue(amazonRequestProduct.getDimensionWidth());
		    	dis.setWidth(width);
	    	}
	    	// 重量 
	    	baseProduct.getDescriptionData().setItemDimensions(dis);
	    	//  包装前重量
	    	if(checkOneNull(amazonRequestProduct.getWeightUnitOfMeasure(),amazonRequestProduct.getPackageWeight())) {
		    	PositiveWeightDimension positiveWeightDimension = new PositiveWeightDimension();
		    	positiveWeightDimension.setUnitOfMeasure(WeightUnitOfMeasure.fromValue(amazonRequestProduct.getWeightUnitOfMeasure()));
		    	positiveWeightDimension.setValue(amazonRequestProduct.getPackageWeight());
		    	baseProduct.getDescriptionData().setPackageWeight(positiveWeightDimension);
	    	}
	    	//  包装后重量
	    	if(checkOneNull(amazonRequestProduct.getWeightUnitOfMeasure(),amazonRequestProduct.getItemWeight())) {
		    	WeightDimension weightDimension = new WeightDimension();
		    	weightDimension.setUnitOfMeasure(WeightUnitOfMeasure.fromValue(amazonRequestProduct.getWeightUnitOfMeasure()));
		    	weightDimension.setValue(amazonRequestProduct.getItemWeight());
		    	baseProduct.getDescriptionData().setItemWeight(weightDimension);
		    	baseProduct.setCondition(amazonRequestProduct.getConditionInfo());
		    	if(amazonRequestProduct.getConditionInfo() != null)
		    	{
		    		String note = amazonRequestProduct.getConditionInfo() == null ? null : 
			    		amazonRequestProduct.getConditionInfo().getConditionNote();
			    	amazonRequestProduct.getConditionInfo().setConditionNote(StringUtils.isBlank(note) ? null : addCDATA(note));
		    	}
	    	}
	    	// 产品描述
	    	baseProduct.getDescriptionData().setDescription(addCDATA(amazonRequestProduct.getDescription()));
	    	// product.getProductData().setSports((Sports) JSON.parseObject(this.categoryPropertyJson, reqClz));
	    	if(baseProduct.getProductData() == null) {
	    		baseProduct.setProductData(new ProductData());
	    	}
	    	
	    	
	    	// String json = amazonRequestProduct.getCategoryPropertyJson();
	    	// ClassReflectionUtil.getExtendsName( );
	    	
	    	//如果是多属性，则分为父与子，如果是单属性，则默认是子
	    	isParent = amazonRequestProduct.getIsMultiattribute() ? isParent : Boolean.FALSE;
	    	
	    	
	    	//某些项需要特殊补充的 ////  begin //////////////////////////
	    	// 补充1 CA站，AutoAccessory模板，父模板需要子模板的其中的一条变体属性。
	    	if("CA".equals(amazonRequestProduct.getCountryCode()) 
	    			&& "AutoAccessory".equalsIgnoreCase(amazonRequestProduct.getTemplatesName())
	    			&& isParent)
	    	{
	    		JSONObject parentJsonObj = JSON.parseObject(amazonRequestProduct.getCategoryPropertyJson()); //父
	    		JSONObject childJsonObj = JSON.parseObject(amazonRequestProduct.getVarRequestProductList().get(0).getCategoryPropertyJson()); // 子
	    		if(parentJsonObj.get("productType") != null 
	    				&& ((JSONObject)parentJsonObj.get("productType")).get(amazonRequestProduct.getTemplatesName2()) != null
	    				&& ((JSONObject)parentJsonObj.get("productType")).get(amazonRequestProduct.getTemplatesName2()) != null
	    				&& ((JSONObject)((JSONObject)parentJsonObj.get("productType")).get(amazonRequestProduct.getTemplatesName2())).get("variationData") != null)
	    		{
	    			JSONObject parentVar = (JSONObject) ((JSONObject)((JSONObject)parentJsonObj.get("productType")).get(amazonRequestProduct.getTemplatesName2()));
	    			JSONObject childVar = (JSONObject) ((JSONObject)((JSONObject)childJsonObj.get("productType")).get(amazonRequestProduct.getTemplatesName2())).get("variationData");
	    			parentVar.put("variationData", childVar);
	    			((JSONObject)parentVar.get("variationData")).put("parentage", "parent");
	    			amazonRequestProduct.setCategoryPropertyJson(parentJsonObj.toJSONString());
	    		}
	    	}
	    	//某些项需要特殊补充的 ////  end //////////////////////////
	    	
	    	String templateName2 = StringUtils.isBlank(amazonRequestProduct.getTemplatesName2()) ? "" : amazonRequestProduct.getTemplatesName2();
	    	ClassReflectionUtil reflection = new ClassReflectionUtil();
	    	reflection.setProductData(baseProduct.getProductData(), 
	    			amazonRequestProduct.getCategoryPropertyJson(), 
	    			amazonRequestProduct.getTemplatesName(),
					templateName2 ,
	    			isParent);
	    	if(!"US".equals(amazonRequestProduct.getCountryCode())) // 
	    	{
	    		for(AmazonCategory cat : categorys)
	    		{
	    			if(StringUtils.isBlank(cat.getAttributes()) || StringUtils.isBlank(cat.getAttributes().replace("[]", "")))
	    			{
	    				baseProduct.getDescriptionData().getRecommendedBrowseNode().add(
	    						BigInteger.valueOf(amazonRequestProduct.getProductCategory()[0]));
	    				break;
	    			}
	    			Map<String,BigInteger> attr = toMapBigInteger(cat.getAttributes());
	    			List<BigInteger> nodeList = attr.values().stream().collect(Collectors.toList());
	    			baseProduct.getDescriptionData().getRecommendedBrowseNode().add(nodeList.get(0));
	    			//非美国站点吧ItemType设置成以及模板名字
						baseProduct.getDescriptionData().setItemType(isNeedItemType(amazonRequestProduct.getCountryCode(),amazonRequestProduct.getTemplatesName()));
	    		}
	    		// baseProduct.getDescriptionData().getRecommendedBrowseNode().add(new BigInteger(amazonRequestProduct.getProductCategory()));
	    	}else{
	    		AmazonCategory cat = categorys.get(0);
	    		if("[]".equals(cat.getAttributes()))
	    		{
	    			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600, "此分类不可用，请使用其它分类");
	    		}
	    		Map<String,String> attr =  toMapString(cat.getAttributes());
	    		baseProduct.getDescriptionData().setItemType(attr.values().iterator().next());
	    	}
	      /*	AmazonEnvelope.Message productMessage = new AmazonEnvelope.Message();
	    	productMessage.setMessageID(BigInteger.valueOf(AmazonEquenceUtil.VALUE()));
	    	productMessage.setOperationType("Update");
	    	productMessage.setProduct(baseProduct);*/
	    	return baseProduct;
    	}catch(Exception e)
    	{
    		logger.error("", e);
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
    	}
	}
	
	public static void main(String[] args) {
		Map<String,String> attr =  new AmazonTemplateGenerate().toMapString("{\"attribute\":\"automotive-light-bulbs\",\"attribute\":\"automotive-exterior-back-up-light-bulbs\"}");
		System.out.println(attr.values().iterator().next());
	}

	/**
	 * 判断是否需要ItemType属性，如果需要返回父级模板，否则返回null
	 * @param siteCode 站点
	 * @param templateParent 父级模板
	 * @return 结果
	 */
	private String isNeedItemType(String siteCode,String templateParent){
		if(siteCode.equalsIgnoreCase("MX")){
			return null;
		}
		if(siteCode.equalsIgnoreCase("CA") && templateParent.equalsIgnoreCase("sports")){
			return null;
		}
		if(siteCode.equalsIgnoreCase("ES") && templateParent.equalsIgnoreCase("sports")){
			return null;
		}
		if(siteCode.equalsIgnoreCase("JP")){
			return null;
		}
		if(siteCode.equalsIgnoreCase("AU") ){
			return null;
		}
		return templateParent;
	}


	
	private List<AmazonCategory> getCategoryList(Long[] categorys)
	{
		if(categorys == null || categorys[0] == null || categorys.length < 1)
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


	@Override
	Inventory generagteInventory(AmazonRequestProduct<?> requestProduct, boolean isParent) {
		if(requestProduct.getQuantity() == null)
		{
			return null;
		}
		Inventory inventory = new Inventory();
    	inventory.setSKU(requestProduct.getSku());
    	inventory.setQuantity(BigInteger.valueOf(requestProduct.getQuantity()));
    	return inventory;
	}


	@Override
	public List<ProductImage> generagteProductImage(AmazonRequestProduct<?> requestProduct, boolean isParent) {
		List<ProductImage>  list = requestProduct.getImages();
    	int ptN = 0;
    	for(ProductImage image : list)
    	{
    		if(!image.getImageType().equalsIgnoreCase("Main"))
    		{
    			ptN++;
    			image.setImageType("PT"+ptN); //PT1..N
    		}
    		image.setSKU(requestProduct.getSku());
    	}
		return list;
	}


	@Override
	Price generagtePrice(AmazonRequestProduct<?> requestProduct, boolean isParent) {
		if(requestProduct.getStandardPrice() == null 
				|| requestProduct.getStandardPrice().doubleValue() <= 0)
		{
			return null;
		}
		OverrideCurrencyAmount overrideCurrencyAmount = new OverrideCurrencyAmount();
    	//String priceUnit = CountryPriceConstants.convert(amazonRequestProduct.getCountryCode());
    	overrideCurrencyAmount.setCurrency(BaseCurrencyCodeWithDefault.valueOf(requestProduct.getStandardPriceUnit()));
    	overrideCurrencyAmount.setValue(requestProduct.getStandardPrice());
    	
    	Price price = new Price();
    	price.setStandardPrice(overrideCurrencyAmount);
    	price.setSKU(requestProduct.getSku());
    	/*priceMessage.setPrice(price);
    	priceMessage.setMessageID(BigInteger.valueOf(AmazonEquenceUtil.VALUE()));
    	priceMessage.setOperationType("Update");*/
		return price;
	}


	@Override
	Relationship generagteRelationship(String parentSku,List<String> childSkus, boolean isParent) {
		Relationship ship = new Relationship();
		ship.setParentSKU(parentSku);
		Relationship.Relation relation = null;
		for(String childsku : childSkus)
		{
			relation = new Relationship.Relation();
			relation.setSKU(childsku);
			relation.setType("Variation");
			ship.getRelation().add(relation);
		}
		return ship;
	}
	


}
