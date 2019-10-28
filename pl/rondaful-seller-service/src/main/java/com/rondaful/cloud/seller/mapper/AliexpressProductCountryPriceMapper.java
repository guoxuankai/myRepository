package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressProductCountryPrice;

import java.util.List;

public interface AliexpressProductCountryPriceMapper extends BaseMapper<AliexpressProductCountryPrice> {

    public List<AliexpressProductCountryPrice> getProductCountryPriceByPublishListingId(Long listingId);

    /**
     * 根据刊登id删除数据
     * @param publishListingId
     * @return
     */
    public int deleteByListingId(Long publishListingId);
}