package com.rondaful.cloud.commodity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.SkuOperateLog;

public interface SkuOperateLogMapper {

    int insert(SkuOperateLog record);

    List<SkuOperateLog> selectBySku(@Param("systemSku")String systemSku);

}