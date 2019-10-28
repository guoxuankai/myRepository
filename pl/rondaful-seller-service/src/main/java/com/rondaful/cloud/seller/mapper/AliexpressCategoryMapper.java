package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressCategoryMapper extends BaseMapper<AliexpressCategory> {

    public List<AliexpressCategory> getAliexpressCategoryByList(@Param(value = "categoryParentId")Long categoryParentId);

    public List<AliexpressCategory> getCategoryByCategoryIdsList(@Param(value = "categoryIds") List<Long> categoryIds);

    public AliexpressCategory getCategoryByCategoryId(@Param(value = "categoryId")Long categoryId);

}