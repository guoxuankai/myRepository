package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.SkuMapImport;

public interface SkuMapImportMapper {

    int insert(SkuMapImport record);

    int insertSelective(SkuMapImport record);

    SkuMapImport selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SkuMapImport record);

    int updateByPrimaryKey(SkuMapImport record);
}