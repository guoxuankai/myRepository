package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.PublishTemplate;
import com.rondaful.cloud.seller.vo.PublishTemplateSearchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PublishTemplateMapper extends BaseMapper<PublishTemplate> {

    public List<PublishTemplate> findPage(PublishTemplateSearchVO vo);

    public List<PublishTemplate> getPublishTemplateALLList(@Param("platform")Integer platform,@Param("site")String site,
                                                           @Param("templateType")Integer templateType, @Param("plAccount")String plAccount,@Param("defaultIs")Boolean defaultIs,@Param("empowerId")String empowerId);

    int updatePublishTemplateDefault(@Param("id")Long id, @Param("plAccount")String plAccount,
                                 @Param("site")String site, @Param("templateType")Integer templateType);

    int checktemplateName(@Param("id")Long id,@Param("plAccount")String plAccount,@Param("templateName")String templateName);
}