package com.rondaful.cloud.commodity.mapper;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.commodity.entity.SkuImport;

public interface SkuImportMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SkuImport record);

    int insertSelective(SkuImport record);

    SkuImport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SkuImport record);

    int updateByPrimaryKey(SkuImport record);
    
    List<SkuImport> page(Map<String, Object> param);
}