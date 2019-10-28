package com.rondaful.cloud.commodity.mapper;

import java.util.List;

import com.rondaful.cloud.commodity.entity.SkuMapImportLog;

public interface SkuMapImportLogMapper {

    int insert(SkuMapImportLog record);

    int insertSelective(SkuMapImportLog record);

    SkuMapImportLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SkuMapImportLog record);

    int updateByPrimaryKey(SkuMapImportLog record);
    
    List<SkuMapImportLog> selectByImportId(Long importId);
}