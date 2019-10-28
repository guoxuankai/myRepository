package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AmazonTemplateAttribute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AmazonTemplateAttributeMapper extends BaseMapper<AmazonTemplateAttribute> {

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
     * 	根据Template And MarketplaceId 查询
     * @param amazonTemplateAttribute
     * @return
     */
    List<AmazonTemplateAttribute> selectByTemplateAndMarketplaceId(AmazonTemplateAttribute amazonTemplateAttribute);

    void setSign();

}