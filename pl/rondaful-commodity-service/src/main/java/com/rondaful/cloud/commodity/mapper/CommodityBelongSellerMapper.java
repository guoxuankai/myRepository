package com.rondaful.cloud.commodity.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.CommodityBelongSeller;

public interface CommodityBelongSellerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CommodityBelongSeller record);

    int insertSelective(CommodityBelongSeller record);

    CommodityBelongSeller selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CommodityBelongSeller record);

    int updateByPrimaryKey(CommodityBelongSeller record);
    
    void insertBatch(List<CommodityBelongSeller> list);
    
    int deleteByCommodityId(Long commodityId);
    
    int selectCountByCommodityId(Map<String, Object> param);
    
    List<String> selectAllCommodityId(Map<String, Object> map);
    
    List<String> selectCommodityIdBySellerId(@Param("sellerId")Long sellerId);
    
    List<String> getSellerIdBySku(@Param("sku") String sku);
}