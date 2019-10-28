package com.rondaful.cloud.seller.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rondaful.cloud.common.constant.marketplace.MarketplaceId;
import com.rondaful.cloud.common.constant.marketplace.MarketplaceIdList;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.seller.entity.AmazonTemplateSiteMapping;
import com.rondaful.cloud.seller.entity.amazon.AmazonNode;
import com.rondaful.cloud.seller.enums.ResponseCodeEnum;
import com.rondaful.cloud.seller.mapper.AmazonTemplateSiteMappingMapper;
import com.rondaful.cloud.seller.service.AmazonXsdTemplateService;
import com.rondaful.cloud.seller.utils.AmazonNodeCompare;
import com.rondaful.cloud.seller.utils.ClassReflectionUtil;

/**
 * 
 * @author ouxiangfeng
 *
 */
@Service
public class AmazonXsdTemplateServiceImpl implements  AmazonXsdTemplateService{
	private final Logger logger = LoggerFactory.getLogger(AmazonXsdTemplateServiceImpl.class);
	
	@Autowired
	private AmazonTemplateSiteMappingMapper amazonTemplateSiteMappingMapper;
	
	/** 仅开放的模板 */
	private List<String>   templateInclude = Arrays.asList(
			"fba"
			,"additionalProductInformation"
			,"euCompliance"
			,"miscellaneous",
			"threeDPrinting"
/*            "petSupplies"
            ,"entertainmentCollectibles"
            ,"baby"
            ,"educationSupplies"
            ,"tools"
            ,"clothing"
            ,"clothingAccessories"
            ,"health"
            ,"home"
            ,"sports"
            ,"beauty"
            ,"gourmet"
            ,"labSupplies"
            ,"shoes"
            ,"luggage"
            ,"mechanicalFasteners"
            ,"autoAccessory"*/
    );
	
	/**
	 *  获取xsd对应的模版属性
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@Override
	public List<AmazonNode> getFirstXsdTemplateCategory(String marketplaceId) throws Exception {
		ClassReflectionUtil cru = new ClassReflectionUtil();
		List<AmazonNode> categoryOne = cru.getFirstXsdTemplateCategory();
		
		List<AmazonTemplateSiteMapping> ats = getTemplate(marketplaceId,null);
		Set<String> result = ats.stream().map(obj -> obj.getTemplateParent()).collect(Collectors.toSet());
		// 过滤模板并排序
		List<AmazonNode> resultNode = new ArrayList<>();
		for(AmazonNode node : categoryOne)
		{
			for(String dbNode : result) // 由于有大小写，不能用contains,所以做二次for
			{
				if(dbNode.equalsIgnoreCase(node.getFieldName()) 
						&& !templateInclude.contains(node.getFieldName())) //必需忽略大小写
				{
					resultNode.add(node);
					break;
				}
			}
		}
		resultNode.sort(new AmazonNodeCompare(true));
		return resultNode;
	}

	/**
	 * 根据path通过类反射获得属性
	 */
	@Override
	public List<AmazonNode> getNextXsdTemplateCategory(String classPath,String marketplaceId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		ClassReflectionUtil cru = new ClassReflectionUtil();
		List<AmazonNode> NodeList = cru.getNextXsdTemplateCategory(classPath);
		

		List<String> result = new ArrayList<>();
		String templateParentName = getParentTemplateName(classPath);
		
		List<AmazonTemplateSiteMapping> ats = getTemplate(marketplaceId,templateParentName);
		// List<String> result = ats.stream().map(obj -> obj.getTemplateChild()).collect(Collectors.toList());
		
		for(AmazonTemplateSiteMapping map : ats)
		{
			// 可能导入的数据有规范，有"null"值
			if(map.getTemplateChild() == null || "null".equals(map.getTemplateChild().trim()))
			{
				continue;
			}
			result.add(map.getTemplateChild());
			/*if(templateParentName.equals("ProductClothingClothingType") && map.getTemplateParent().equalsIgnoreCase("clothing")) //clothing下所有的数据
			{
				result.add(map.getTemplateChild());
				continue;
			}
			if(templateParentName.equals("ShoesClothingType") && map.getTemplateParent().equalsIgnoreCase("Shoes"))
			{
				result.add(map.getTemplateChild());
				continue;
			}
			if(templateParentName.equalsIgnoreCase(map.getTemplateParent()))
			{
				result.add(map.getTemplateChild());
			}*/
		}
		if(CollectionUtils.isEmpty(result)) //至少有一个二级模板
		{
			logger.error("缺少二级模板,需要配置，缺少二级模板，请检查是当前站点是否支持该模板：{}" , templateParentName);
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100600,"缺少二级模板，请检查是当前站点是否支持该模板");
		}
		List<AmazonNode> resultNode = new ArrayList<>();
		for(AmazonNode node : NodeList)
		{
			for(String dbNode : result) // 由于有大小写，不能用contains,所以做二次for
			{
				if(StringUtils.isBlank(dbNode))
				{
					logger.debug("{}找不到二级模板，站点为：{}",classPath,marketplaceId);
					continue;
				}
				if(dbNode.equalsIgnoreCase(node.getFieldName())) //必需忽略大小写
				{
					resultNode.add(node);
					break;
				}
			}
		}
		resultNode.sort(new AmazonNodeCompare(true));
		return resultNode;
	}

	/**
	 * 	需要特殊处理的模板
	 * @param classPath
	 * @return
	 */
	private String getParentTemplateName(String classPath) {
		String templateParentName =  classPath.substring(classPath.lastIndexOf(".") + 1 , classPath.length());
		templateParentName = templateParentName.replace("$ProductType", "");
		if(templateParentName.equals("ProductClothingClothingType")) //clothing下所有的数据
		{
			templateParentName = "Clothing";
		}else if(templateParentName.equals("ShoesClothingType") )
		{
			templateParentName = "Shoes";
		}else if("Sports100Enum".equals(templateParentName))
			
		{
			templateParentName = "Sports";
		}else if("Luggage100Enum".equals(templateParentName))
		{
			templateParentName = "Luggage";
		}else if("ToysBabyProductType".equals(templateParentName))
		{
			templateParentName = "ToysBaby";
		}
		return templateParentName;
	}
	
	private List<AmazonTemplateSiteMapping> getTemplate(String marketplaceId,String templateParentName)
	{
		// 获取站点信息
		MarketplaceId marketplaceIdObject = MarketplaceIdList.createMarketplaceForKeyId().get(marketplaceId);
		if(marketplaceIdObject == null)
		{
			logger.error("找不到站点信息...");
			return new ArrayList<AmazonTemplateSiteMapping>();
		}
		
		if("GB".equals(marketplaceIdObject.getCountryCode()))
		{
			marketplaceIdObject.setCountryCode("UK");
		}
		
		AmazonTemplateSiteMapping tempSiteMapping = new AmazonTemplateSiteMapping();
		tempSiteMapping.setSite(marketplaceIdObject.getCountryCode());
		tempSiteMapping.setTemplateParent(templateParentName);
		return amazonTemplateSiteMappingMapper.page(tempSiteMapping);
		
//		List<String> result = ats.stream().map(obj -> obj.getTemplateParent()).collect(Collectors.toList());
//		return result;
	}

}
