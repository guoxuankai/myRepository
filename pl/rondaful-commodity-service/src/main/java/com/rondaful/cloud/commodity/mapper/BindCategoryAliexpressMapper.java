package com.rondaful.cloud.commodity.mapper;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.BindCategoryAliexpress;

public interface BindCategoryAliexpressMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BindCategoryAliexpress record);

    int insertSelective(BindCategoryAliexpress record);

    BindCategoryAliexpress selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BindCategoryAliexpress record);

    int updateByPrimaryKey(BindCategoryAliexpress record);
    
    BindCategoryAliexpress getBindByCategoryId(@Param("pinlianCategoty3Id")Long pinlianCategoty3Id);
}