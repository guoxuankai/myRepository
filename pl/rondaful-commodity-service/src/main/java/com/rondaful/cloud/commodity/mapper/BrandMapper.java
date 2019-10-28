package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.Brand;
import com.rondaful.cloud.common.mapper.BaseMapper;

import java.util.List;

public interface BrandMapper extends BaseMapper<Brand> {
    List<Brand> findBrandList(Brand brand);
    
    int getUnAuditNum();
}