package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingShippingTransport;

public interface EbayPublishListingShippingTransportMapper extends BaseMapper<EbayPublishListingShippingTransport> {

    int deleteByListingId(Long listingId);
}