package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressCategoryAttributeKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressCategoryAttributeKeyMapper extends BaseMapper<AliexpressCategoryAttributeKey> {

    public int updateCategoryAttributeKeyByCategoryId(@Param("categoryId") Long categoryId);

    public int deleteByCategoryAttributeKey(@Param("categoryId") Long categoryId,@Param("empowerId")Long empowerId);

    /**
     *	 批量写入
     * @param list
     * @return
     */
    Integer insertBatch(List<AliexpressCategoryAttributeKey> list);

}