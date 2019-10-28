package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingDetail;

public interface AliexpressPublishListingDetailMapper extends BaseMapper<AliexpressPublishListingDetail> {
    /**
     * 根据刊登id查询详情
     * @param publishListingId
     * @return
     */
    public AliexpressPublishListingDetail getPublishListingDetailByPublishListingId(Long publishListingId);
}