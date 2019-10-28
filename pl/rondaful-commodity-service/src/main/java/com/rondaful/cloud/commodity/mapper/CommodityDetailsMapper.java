package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.CommodityDetails;
import com.rondaful.cloud.common.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface CommodityDetailsMapper extends BaseMapper<CommodityDetails> {
    void deleteCommodityDetailsByCommodityId(long id);
    
    List<CommodityDetails> getAllDetailBySupplierId(Map<String, Object> param);
    
    int getAllDetailCountBySupplierId(Map<String, Object> param);
    
    CommodityDetails selectByCommodityId(Long commodityId);
}