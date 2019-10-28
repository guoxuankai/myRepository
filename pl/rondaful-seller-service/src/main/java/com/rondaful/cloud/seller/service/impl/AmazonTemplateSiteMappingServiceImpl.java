package com.rondaful.cloud.seller.service.impl;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.service.impl.BaseServiceImpl;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import com.rondaful.cloud.seller.entity.AmazonTemplateSiteMapping;
import com.rondaful.cloud.seller.mapper.AmazonTemplateAttributeMapper;
import com.rondaful.cloud.seller.mapper.AmazonTemplateSiteMappingMapper;
import com.rondaful.cloud.seller.service.AmazonTemplateSiteMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmazonTemplateSiteMappingServiceImpl extends BaseServiceImpl<AmazonTemplateSiteMapping> implements AmazonTemplateSiteMappingService {

    @Autowired
    private AmazonTemplateSiteMappingMapper amazonTemplateSiteMappingMapper;

    @Autowired
    private AmazonTemplateAttributeMapper amazonTemplateAttributeMapper;


    @Override
    public Page<AmazonTemplateSiteMapping> findAllByPage(AmazonTemplateSiteMapping mapping) {
        List<AmazonTemplateSiteMapping> list = this.amazonTemplateSiteMappingMapper.newPage(mapping);
        return new Page(list);
    }

    @Override
    public List<AmazonTemplateSiteMapping> findAllByList(AmazonTemplateSiteMapping mapping) {
        return this.amazonTemplateSiteMappingMapper.newPage(mapping);
    }

    @Override
    public List<AmazonTemplateAttribute> findAttributeList(AmazonTemplateAttribute attribute) {
        return amazonTemplateAttributeMapper.page(attribute);
    }
}
