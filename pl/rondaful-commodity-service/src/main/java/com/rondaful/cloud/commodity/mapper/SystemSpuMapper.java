package com.rondaful.cloud.commodity.mapper;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.SystemSpu;
import com.rondaful.cloud.common.mapper.BaseMapper;

public interface SystemSpuMapper extends BaseMapper<SystemSpu> {
	
	int getSpuCount(@Param("spuValue") String spuValue);
	
	String getSpuBySku(@Param("systemSku") String systemSku);
}