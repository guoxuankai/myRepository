package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressPromiseTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressPromiseTemplateMapper extends BaseMapper<AliexpressPromiseTemplate> {

    public List<AliexpressPromiseTemplate> getAliexpressPromiseTemplateByPlAccountList(@Param("empowerId") Long empowerId,
                                                                                       @Param("plAccount") String plAccount,@Param("promiseTemplateId") Long promiseTemplateId);


}