package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressFreightTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressFreightTemplateMapper extends BaseMapper<AliexpressFreightTemplate> {

    public List<AliexpressFreightTemplate> getAliexpressFreightTemplateByPlAccountList(@Param("empowerId") Long empowerId,
                                                                                       @Param("plAccount") String plAccount,@Param("templateId") Long templateId);
}