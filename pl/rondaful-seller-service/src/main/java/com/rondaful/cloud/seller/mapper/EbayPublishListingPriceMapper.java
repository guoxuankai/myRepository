package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingPrice;

public interface EbayPublishListingPriceMapper extends BaseMapper<EbayPublishListingPrice> {

    public int deleteByListingId(Long listingId);

}