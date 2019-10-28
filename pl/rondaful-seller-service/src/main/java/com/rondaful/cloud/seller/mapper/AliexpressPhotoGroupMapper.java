package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPhotoGroup;
import org.apache.ibatis.annotations.Param;

public interface AliexpressPhotoGroupMapper extends BaseMapper<AliexpressPhotoGroup> {

    public int deletePhotoGroupByEmpowerId(@Param("empowerId") Long empowerId);
}