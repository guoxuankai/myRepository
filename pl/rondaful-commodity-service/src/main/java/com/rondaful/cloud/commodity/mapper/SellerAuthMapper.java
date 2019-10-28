package com.rondaful.cloud.commodity.mapper;

import com.rondaful.cloud.commodity.entity.SellerAuth;

public interface SellerAuthMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(SellerAuth record);

    int insertSelective(SellerAuth record);

    SellerAuth selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SellerAuth record);

    int updateByPrimaryKey(SellerAuth record);
    
    SellerAuth selectBySellerId(Long sellerId);
}