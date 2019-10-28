package com.rondaful.cloud.seller.common.task;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;
import com.rondaful.cloud.seller.entity.amazon.AmazonRequestProduct;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.generated.AmazonEnvelope;
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
import com.rondaful.cloud.seller.generated.Relationship;
import com.rondaful.cloud.seller.generated.StandardProductID;
import com.rondaful.cloud.seller.generated.WeightDimension;
import com.rondaful.cloud.seller.generated.WeightUnitOfMeasure;
import com.rondaful.cloud.seller.service.AmazonCategoryService;
import com.rondaful.cloud.seller.utils.AmazonEquenceUtil;
import com.rondaful.cloud.seller.utils.ClassReflectionUtil;

@Component
public class AmazonTemplateGenerateBatchUpdate extends AbstractBaseGenerate {

	private final Logger logger = LoggerFactory.getLogger(AmazonTemplateGenerateBatchUpdate.class);
	
	@Autowired
	AmazonCategoryService amazonCategoryService;
	
	@Autowired
	RedisUtils redisUtils;
	
	@Override
	Product generagteProduct(AmazonRequestProduct<?> amazonRequestProduct , boolean isParent) {
//		ComposeInfoUtil compose = new ComposeInfoUtil();
//		List<AmazonCategory> categorys = getCategoryList(amazonRequestProduct.getProductCategory());
		try
    	{
	    	Product baseProduct = new Product();
	    	baseProduct.setSKU(amazonRequestProduct.getSku());
	    	/*if(StringUtils.isNotBlank(amazonRequestProduct.getStandardProductID())) // upc有可能为空。为空是允许的，只有不为空时才会设置值项
	    	{
	    		// ISBN,UPC,EAN,ASIN,GTIN,GCID,PZN
		    	StandardProductID spid = new StandardProductID();
		    	spid.setType(amazonRequestProduct.getStandardProductType());
		    	spid.setValue(amazonRequestProduct.getStandardProductID());
		    	baseProduct.setStandardProductID(spid); 
	    	}*/
	    	
	    	
	    	baseProduct.setDescriptionData(new Product.DescriptionData());
	    	/*baseProduct.getDescriptionData().setBrand(addCDATA(amazonRequestProduct.getBrand()));
	    	baseProduct.getDescriptionData().setManufacturer(amazonRequestProduct.getManufacturer());
	    	baseProduct.getDescriptionData().setMfrPartNumber(amazonRequestProduct.getMfrPartNumber());*/
	    	baseProduct.getDescriptionData().setTitle(addCDATA(amazonRequestProduct.getTitle()));
	    	baseProduct.getDescriptionData().getBulletPoint().addAll(amazonRequestProduct.getBulletPoint());
	    	baseProduct.getDescriptionData().getSearchTerms().addAll(amazonRequestProduct.getSearchTerms());
	    	
	    	
	    	// 产品描述
	    	baseProduct.getDescriptionData().setDescription(addCDATA(amazonRequestProduct.getDescription()));
	    	// product.getProductData().setSports((Sports) JSON.parseObject(this.categoryPropertyJson, reqClz));
//	    	if(baseProduct.getProductData() == null) {
//	    		baseProduct.setProductData(new ProductData());
//	    	}
	    	//如果是多属性，则分为父与子，如果是单属性，则默认是子
	    	/*isParent = amazonRequestProduct.getIsMultiattribute() ? isParent : Boolean.FALSE;
	    	String templateName2 = StringUtils.isBlank(amazonRequestProduct.getTemplatesName2()) ? "" : amazonRequestProduct.getTemplatesName2();
	    	ClassReflectionUtil reflection = new ClassReflectionUtil();
	    	reflection.setProductData(baseProduct.getProductData(), 
	    			amazonRequestProduct.getCategoryPropertyJson(), 
	    			amazonRequestProduct.getTemplatesName(),
					templateName2 ,
	    			isParent);*/
	    	return baseProduct;
    	}catch(Exception e)
    	{
    		logger.error("", e);
    		throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500,e.getMessage());
    	}
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
		Inventory inventory = new Inventory();
    	inventory.setSKU(requestProduct.getSku());
    	inventory.setQuantity(BigInteger.valueOf(requestProduct.getQuantity()));
    	return inventory;
	}


	@Override
	List<ProductImage> generagteProductImage(AmazonRequestProduct<?> requestProduct, boolean isParent) {
		return null;
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
		return null;
	}
	


}
