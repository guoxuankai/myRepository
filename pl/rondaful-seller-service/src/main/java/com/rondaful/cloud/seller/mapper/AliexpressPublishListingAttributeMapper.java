package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingAttribute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressPublishListingAttributeMapper extends BaseMapper<AliexpressPublishListingAttribute> {
    /**
     *
     * @param publishListingId
     * @return
     */
    public List<AliexpressPublishListingAttribute> getPublishListingAttributeByPublishListingId(Long publishListingId);

    /**
     *
     * @param publishListingId
     * @return
     */
    public List<AliexpressPublishListingAttribute> getPublishListingAttributeByPublishListingIds(@Param("publishListingIds") List<Long> publishListingIds);

    /**
     * 根据刊登id删除数据
     * @param publishListingId
     * @return
     */
    public int deleteByListingId(Long publishListingId);

}