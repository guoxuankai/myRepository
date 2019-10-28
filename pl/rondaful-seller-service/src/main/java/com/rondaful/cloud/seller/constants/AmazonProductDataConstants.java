package com.rondaful.cloud.seller.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import com.rondaful.cloud.seller.entity.amazon.AmazonAttr;

public class AmazonProductDataConstants {
	private static AmazonAttr greanAmazonAttr(String attrName,String attrType,String attrNote,Boolean required,String ...defaultValue)
	{
		AmazonAttr attrObj = new AmazonAttr();
		attrObj = new AmazonAttr();
		attrObj.setAttrName(attrName);
		attrObj.setAttrType(attrType);
		attrObj.setDefaultValue(defaultValue == null ? null : Arrays.asList(defaultValue));
		attrObj.setRequired(required);
		return attrObj;
	}
	
	private static List<AmazonAttr> getInitProductAttr()
	{
		
		List<AmazonAttr> productAttrList = new ArrayList<>();
		productAttrList.add(greanAmazonAttr("ItemPackageQuantity","java.lang.Integer",null,Boolean.FALSE,null));
		productAttrList.add(greanAmazonAttr("NumberOfItems","java.lang.Integer",null,Boolean.FALSE,null));
		productAttrList.add(greanAmazonAttr("Designer","java.lang.String",null,Boolean.FALSE,null));
		productAttrList.add(greanAmazonAttr("ItemType","java.lang.String",null,Boolean.FALSE,null));
		productAttrList.add(greanAmazonAttr("TargetAudience","java.util.List",null,Boolean.FALSE,null));
		productAttrList.add(greanAmazonAttr("SafetyDataSheetURL","java.lang.String","anyURI",Boolean.FALSE,null));
		productAttrList.add(greanAmazonAttr("SupplierDeclaredDGHZRegulation","java.util.List",null,Boolean.FALSE,"ghs","storage","waste","not_applicable","transportation","other","unknown"));
		productAttrList.add(greanAmazonAttr("HazmatUnitedNationsRegulatoryID","java.lang.String",null,Boolean.FALSE,null));
		return productAttrList;
	}
	

	public static List<AmazonAttr> productBaseAttrSetter(List<AmazonTemplateAttribute> amazonTemplateAttributeList)
	{
		List<AmazonAttr> initData = getInitProductAttr();
		if(CollectionUtils.isEmpty(amazonTemplateAttributeList))
		{
			return initData;
		}
		
		for(AmazonTemplateAttribute attribute : amazonTemplateAttributeList)
		{
			if(attribute.getAttributeName().indexOf(".") <= 0) //不存在“.”
			{
				continue;
			}
			String [] attrNames = attribute.getAttributeName().split("\\.");
			for(AmazonAttr attr : initData)
			{
				
				if(attr.getAttrName().equalsIgnoreCase(attrNames[1]))
				{
					attr.setRequired(attribute.getRequired() == 1 ? Boolean.TRUE : Boolean.FALSE);
					String [] vArray = StringUtils.isBlank(attribute.getOptions()) ? new String[] {} : attribute.getOptions().split("\\|");
					attr.setDefaultValue(Arrays.asList(vArray));
					break;
				}
			}
		}
		return initData;
	}
	

   public static void main(String[] args) {
	   List<AmazonTemplateAttribute> amazonTemplateAttributeList = new ArrayList<>();
	   AmazonTemplateAttribute att = new AmazonTemplateAttribute();
	   att.setAttributeName("product.TargetAudience");
	   att.setRequired(1);
	   amazonTemplateAttributeList.add(att);
	   AmazonTemplateAttribute att1 = new AmazonTemplateAttribute();
	   att1.setAttributeName("ItemType");
	   att1.setRequired(1);
	   amazonTemplateAttributeList.add(att1);
	   productBaseAttrSetter(amazonTemplateAttributeList);
	   
   }
   
}
