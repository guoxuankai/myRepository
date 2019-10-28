package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.PublishStyleType;
import com.rondaful.cloud.seller.vo.PublishStyleTypeSearchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PublishStyleTypeMapper extends BaseMapper<PublishStyleType> {

    List<PublishStyleType> findPublishStyleTypePage(PublishStyleTypeSearchVO vo);

    Integer checkStyleTypeName(@Param("id")Long id, @Param("plAccount")String plAccount,
                               @Param("styleTypeName")String styleTypeName);

    List<PublishStyleType> getPublishStyleTypeAll(@Param("platform")Integer platform,
                                                  @Param("createId")Integer createId,@Param("systemIs")Boolean systemIs);

}