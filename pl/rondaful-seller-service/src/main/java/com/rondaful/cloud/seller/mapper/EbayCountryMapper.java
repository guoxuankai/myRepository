package com.rondaful.cloud.seller.mapper;

import java.util.List;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayCountry;

public interface EbayCountryMapper extends BaseMapper<EbayCountry> {
	 
	List<EbayCountry> selectByValue(EbayCountry record);
	    
}