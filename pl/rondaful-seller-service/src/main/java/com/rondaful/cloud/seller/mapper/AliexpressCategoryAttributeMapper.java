package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressCategoryAttribute;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingAttribute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressCategoryAttributeMapper extends BaseMapper<AliexpressCategoryAttribute> {
    /**
     * 获取分类id的属性值
     * @param categoryId
     * @return
     */
    public List<AliexpressCategoryAttribute> getAliexpressCategoryAttributeByCategoryIdList(@Param(value = "categoryId") Long categoryId,@Param(value = "empowerId")Long empowerId);
    /**
     *
     * @param categoryAttributeIds
     * @return
     */
    public List<AliexpressCategoryAttribute> getAttributeByCategoryAttributeIdsList(@Param(value = "categoryAttributeIds") List<Long> categoryAttributeIds);


    /**
     * 获取分类id的下的属性
     * @param categoryId
     * @return
     */
    public List<AliexpressCategoryAttribute> getCategoryAttributeByCategoryId(@Param(value = "categoryId") Long categoryId);

}