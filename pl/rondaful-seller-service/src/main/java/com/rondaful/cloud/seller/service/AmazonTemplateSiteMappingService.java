package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import com.rondaful.cloud.seller.entity.AmazonTemplateSiteMapping;

import java.util.List;

public interface AmazonTemplateSiteMappingService extends BaseService<AmazonTemplateSiteMapping> {

    /**
     * 分页查询数据
     * @param mapping 参数
     * @return 分页结果
     */
    Page<AmazonTemplateSiteMapping> findAllByPage(AmazonTemplateSiteMapping mapping);

    /**
     * 分页查询数据
     * @param mapping 参数
     * @return 分页结果
     */
    List<AmazonTemplateSiteMapping> findAllByList(AmazonTemplateSiteMapping mapping);

    /**
     * 查询某个站点某个模板下的属性列表
     * @param attribute 参数
     * @return 结果
     */
    List<AmazonTemplateAttribute> findAttributeList(AmazonTemplateAttribute attribute);

}
