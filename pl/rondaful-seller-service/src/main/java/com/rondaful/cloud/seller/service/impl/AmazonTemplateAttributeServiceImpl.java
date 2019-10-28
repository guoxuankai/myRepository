package com.rondaful.cloud.seller.service.impl;

import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import com.rondaful.cloud.seller.mapper.AmazonTemplateAttributeMapper;
import com.rondaful.cloud.seller.service.AmazonTemplateAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmazonTemplateAttributeServiceImpl extends BaseServiceImpl<AmazonTemplateAttribute> implements AmazonTemplateAttributeService {


    @Autowired
    private AmazonTemplateAttributeMapper amazonTemplateAttributeMapper;

    @Override
    public List<String> selectAllChildTemplate() {
        return amazonTemplateAttributeMapper.selectAllChildTemplate();
    }

    @Override
    public List<AmazonTemplateAttribute> selectAllAttributeByTemplate(String template) {
        return amazonTemplateAttributeMapper.selectAllAttributeByTemplate(template);
    }

    @Override
    public List<AmazonTemplateAttribute> findAllNoPage(AmazonTemplateAttribute templateAttribute) {
        return amazonTemplateAttributeMapper.page(templateAttribute);
    }

	@Override
	public List<AmazonTemplateAttribute> selectByTemplateAndMarketplaceId(String templateParent ,String templateChild , String marketplaceId) {
		AmazonTemplateAttribute attribute = new AmazonTemplateAttribute();
		attribute.setMarketplaceId(marketplaceId);
		attribute.setTemplateChild(getChildName(templateChild));
		attribute.setTemplateParent(templateParent);
		return amazonTemplateAttributeMapper.selectByTemplateAndMarketplaceId(attribute);
	}

    @Override
    public void setSign() {
        amazonTemplateAttributeMapper.setSign();
    }
    


    @Override
    public List<AmazonTemplateAttribute> findAttributeList(AmazonTemplateAttribute attribute) {
        return amazonTemplateAttributeMapper.page(attribute);
    }

    @Override
    public void addOrUpdateAttribute(List<AmazonTemplateAttribute> updateAttributes, List<AmazonTemplateAttribute> addAttribute) {
        for (AmazonTemplateAttribute attribute: updateAttributes){
            amazonTemplateAttributeMapper.updateByPrimaryKeySelective(attribute);
        }
        for(AmazonTemplateAttribute attribute : addAttribute){
            amazonTemplateAttributeMapper.insertSelective(attribute);
        }
    }


    private String getChildName(String childName)
    {
        if(childName.indexOf("001") != -1) //MechanicalFasteners001Emun
        {
            childName = childName.substring(0, childName.indexOf("001"));
        }
        return childName;
    }
    public static void main(String[] args) {
        System.out.println(new AmazonTemplateAttributeServiceImpl().getChildName("MechanicalFasteners001Emun"));
    }




}
