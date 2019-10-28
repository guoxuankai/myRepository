package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingShipping;

public interface EbayPublishListingShippingMapper extends BaseMapper<EbayPublishListingShipping> {

    int deleteByListingId(Integer listingId);
}