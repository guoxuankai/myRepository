package com.rondaful.cloud.commodity.mapper;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.SpuPushRecord;

public interface SpuPushRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SpuPushRecord record);

    SpuPushRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SpuPushRecord record);

    int updateByPrimaryKey(SpuPushRecord record);
    
    SpuPushRecord selectBySpu(@Param("spu")String spu,@Param("sellerId")Long sellerId);
}