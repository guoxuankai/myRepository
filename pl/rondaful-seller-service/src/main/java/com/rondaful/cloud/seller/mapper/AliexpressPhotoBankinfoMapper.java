package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPhotoBankinfo;
import org.apache.ibatis.annotations.Param;

public interface AliexpressPhotoBankinfoMapper extends BaseMapper<AliexpressPhotoBankinfo> {

    public AliexpressPhotoBankinfo getAliexpressPhotoBankinfoByEmpowerId(@Param("empowerId") Long empowerId);
}