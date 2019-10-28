package com.rondaful.cloud.commodity.mapper;

import java.util.List;

import com.rondaful.cloud.commodity.entity.SkuImportErrorRecord;

public interface SkuImportErrorRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SkuImportErrorRecord record);

    int insertSelective(SkuImportErrorRecord record);

    SkuImportErrorRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SkuImportErrorRecord record);

    int updateByPrimaryKey(SkuImportErrorRecord record);
    
    void insertBatch(List<SkuImportErrorRecord> list);
    
    void deleteByImportId(Long importId);
    
    List<SkuImportErrorRecord> selectByImportId(Long importId);
}