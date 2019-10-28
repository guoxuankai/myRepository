package com.rondaful.cloud.commodity.mapper;

import java.util.List;

import com.rondaful.cloud.commodity.entity.CommodityLimitSale;

public interface CommodityLimitSaleMapper {
    
	int deleteByCommodityId(Long commodityId);

    int insert(CommodityLimitSale limitSale);

    CommodityLimitSale selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CommodityLimitSale limitSale);
    
    void insertBatch(List<CommodityLimitSale>  limitSaleList);
    
    List<String> selectAllCode(CommodityLimitSale limitSale);

}