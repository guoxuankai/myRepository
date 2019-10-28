package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.service.BaseService;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AmazonTemplateAttributeService extends BaseService<AmazonTemplateAttribute> {


    /**
     * 查询所有的二级模板（没有二级模板的查询一级模板）
     * @return 模板列表
     */
    List<String> selectAllChildTemplate();

    /**
     * 根据传入的末班名称查询所有的属性
     * @param template 模板名称
     * @return 属性列表
     */
    List<AmazonTemplateAttribute> selectAllAttributeByTemplate(@Param("template") String template);

    /**
     * 不分页查询全部属性
     * @param templateAttribute 属性对象
     * @return 属性列表
     */
    List<AmazonTemplateAttribute> findAllNoPage(AmazonTemplateAttribute templateAttribute);
    
    
    /**
     * 	根据Template And MarketplaceId 查询
     * @param AmazonTemplateAttribute
     * @return
     */
    List<AmazonTemplateAttribute> selectByTemplateAndMarketplaceId(String templateParent ,String templateChild , String marketplaceId);

    void setSign();

    /**
     * 查询某个站点某个模板下的属性列表
     * @param attribute 参数
     * @return 结果
     */
    List<AmazonTemplateAttribute> findAttributeList(AmazonTemplateAttribute attribute);

    /**
     * 添加或者更改属性
     * @param updateAttributes 要更新的属性列表
     * @param addAttribute 要添加的属性列表
     */
    void addOrUpdateAttribute(List<AmazonTemplateAttribute> updateAttributes,List<AmazonTemplateAttribute> addAttribute );


}
