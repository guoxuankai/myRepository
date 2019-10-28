package com.rondaful.cloud.seller.mapper;

import java.util.List;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingAttribute;

public interface EbayPublishListingAttributeMapper extends BaseMapper<EbayPublishListingAttribute> {
	
	void insertAttributeList(List<EbayPublishListingAttribute> recordList);
	
	void deleteByValue(EbayPublishListingAttribute record);
}