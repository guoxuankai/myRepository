package com.rondaful.cloud.commodity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.PublishPackRecord;

public interface PublishPackRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PublishPackRecord record);

    int insertSelective(PublishPackRecord record);

    PublishPackRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PublishPackRecord record);

    int updateByPrimaryKey(PublishPackRecord record);
    
    PublishPackRecord getByCommodityId(@Param("commodityId")Long commodityId);
    
    List<String> get7DaysBefore();
    
    void deleteByTask();
}