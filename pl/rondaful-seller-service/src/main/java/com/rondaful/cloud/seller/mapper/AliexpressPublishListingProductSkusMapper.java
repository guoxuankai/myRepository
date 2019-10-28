package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProductSkus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressPublishListingProductSkusMapper extends BaseMapper<AliexpressPublishListingProductSkus> {

    /**
     * 根据刊登id删除
     * @param publishListingId
     * @return
     */
    public Integer delectProductSkus(@Param("publishListingId") Long publishListingId);

    /**
     * 根据刊登id查询
      * @param publishListingId
     * @return
     */
    public List<AliexpressPublishListingProductSkus> getProductSkusByPublishListingId(@Param("publishListingId") Long publishListingId);

    /**
     * 根据刊登id查询
     * @param publishListingIds
     * @return
     */
    public List<AliexpressPublishListingProductSkus> getProductSkusByPublishListingIds(@Param("publishListingIds") List<Long> publishListingIds);

}