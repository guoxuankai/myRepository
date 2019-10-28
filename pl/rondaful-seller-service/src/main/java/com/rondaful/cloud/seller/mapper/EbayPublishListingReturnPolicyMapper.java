package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingReturnPolicy;

public interface EbayPublishListingReturnPolicyMapper extends BaseMapper<EbayPublishListingReturnPolicy> {
    public int deleteByListingId(Long listingId);
}