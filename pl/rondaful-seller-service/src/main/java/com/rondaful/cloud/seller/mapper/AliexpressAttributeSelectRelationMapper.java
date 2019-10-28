package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressAttributeSelectRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressAttributeSelectRelationMapper extends BaseMapper<AliexpressAttributeSelectRelation> {

    public int deleteByAliexpressAttributeSelectRelation(@Param("categoryId")Long categoryId,@Param("attributeId")Long attributeId,@Param("empowerId")Long empowerId);

    /**
     *	 批量写入
     * @param list
     * @return
     */
    Integer insertBatch(List<AliexpressAttributeSelectRelation> list);


}