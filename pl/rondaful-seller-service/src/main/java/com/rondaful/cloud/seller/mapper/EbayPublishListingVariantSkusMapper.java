package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.EbayPublishListingVariantSkus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EbayPublishListingVariantSkusMapper extends BaseMapper<EbayPublishListingVariantSkus> {
    /**
     * 根据刊登id删除
     * @param listingId
     * @return
     */
    public Integer delectVariantSkus(@Param("listingId") Long listingId);

    /**
     * 根据刊登id获取
     * @param listingId
     * @return
     */
    public List<EbayPublishListingVariantSkus> getVariantSkusByListingId(@Param("listingId") Long listingId);

    /**
     * 根据刊登id获取
     * @param listingId
     * @return
     */
    public List<EbayPublishListingVariantSkus> getVariantSkusByListingIds(@Param("listingIds") List<Long> listingIds);
}