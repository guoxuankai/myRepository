package com.rondaful.cloud.seller.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingError;

public interface EbayPublishListingErrorMapper extends BaseMapper<EbayPublishListingError> {
	
	void batchInsert(List<EbayPublishListingError> record);
	
	int deleteByListingId(@Param("listingId") Long listingId);
}