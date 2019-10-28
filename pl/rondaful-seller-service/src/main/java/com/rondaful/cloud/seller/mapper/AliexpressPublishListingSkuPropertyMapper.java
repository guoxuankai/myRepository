package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingSkuProperty;
import com.rondaful.cloud.seller.entity.aliexpress.AliexpressPublishListingSkuPropertyModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressPublishListingSkuPropertyMapper extends BaseMapper<AliexpressPublishListingSkuProperty> {

    public List<AliexpressPublishListingSkuProperty> getPublishListingSkuPropertyByListingId(@Param("publishListingId") Long publishListingId,@Param("productId")Long productId);


    public List<AliexpressPublishListingSkuPropertyModel> getPublishListingSkuPropertyByListingIds(@Param("publishListingIds") List<Long> publishListingIds);

    public List<AliexpressPublishListingSkuPropertyModel> getSkuPropertyByListingId(@Param("publishListingId") Long publishListingId);

    /**
     * 根据刊登id删除数据
     * @param publishListingId
     * @return
     */
    public int deleteByListingId(Long publishListingId);
}