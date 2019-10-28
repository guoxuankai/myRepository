package com.rondaful.cloud.commodity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.SkuMapBind;

public interface SkuMapBindMapper {
	
    int deleteByMapId(@Param("mapId") Long mapId);

    int insert(SkuMapBind record);

    List<SkuMapBind> selectByMapId(@Param("mapId") Long mapId);
}