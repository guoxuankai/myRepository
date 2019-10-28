package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingVariantValue;

public interface EbayPublishListingVariantValueMapper extends BaseMapper<EbayPublishListingVariantValue> {
	void deleteByValue(Integer listId);
}