package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AmazonTemplateSiteMapping;

import java.util.List;

public interface AmazonTemplateSiteMappingMapper extends BaseMapper<AmazonTemplateSiteMapping> {

    /**
     * 查询模板列表
     * @param mapping 参数
     * @return 列表结果
     */
    List<AmazonTemplateSiteMapping> newPage(AmazonTemplateSiteMapping mapping);

}