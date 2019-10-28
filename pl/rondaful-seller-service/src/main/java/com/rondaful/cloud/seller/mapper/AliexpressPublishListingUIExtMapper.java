package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingUIExt;

public interface AliexpressPublishListingUIExtMapper extends BaseMapper<AliexpressPublishListingUIExt> {
    /**
     *
     * @param listingId
     * @return
     */
    public AliexpressPublishListingUIExt getExtByListingId(Long listingId);
}