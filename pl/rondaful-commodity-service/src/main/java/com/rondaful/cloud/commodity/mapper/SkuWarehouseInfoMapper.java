package com.rondaful.cloud.commodity.mapper;

import java.util.List;

import com.rondaful.cloud.commodity.entity.SkuWarehouseInfo;

public interface SkuWarehouseInfoMapper {
    
	int deleteBySku(String systemSku);

    int insert(SkuWarehouseInfo record);

    int insertSelective(SkuWarehouseInfo record);

    SkuWarehouseInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SkuWarehouseInfo record);

    int updateByPrimaryKey(SkuWarehouseInfo record);
    
    List<SkuWarehouseInfo> selectBySku(String systemSku);
    
    void insertBatch(List<SkuWarehouseInfo> list);
}