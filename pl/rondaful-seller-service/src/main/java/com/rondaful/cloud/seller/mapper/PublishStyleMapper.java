package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.PublishStyle;
import com.rondaful.cloud.seller.vo.PublishStyleSearchVO;
import com.rondaful.cloud.seller.vo.PublishStyleTypeSearchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PublishStyleMapper extends BaseMapper<PublishStyle> {

    List<PublishStyle> findPublishStylePage(PublishStyleSearchVO vo);

    Integer checkStyleName(@Param("id")Long id, @Param("createId")Long createId,@Param("styleName")String styleName);

    Integer checkStylePlCategory(@Param("id")Long id, @Param("createId")Long createId,@Param("category")String category);

    Integer checkPublishStyle(@Param("styleTypeId")Long styleTypeId);

    List<PublishStyle> findPublishStyle(@Param("platform")Integer platform,@Param("createId")Long createId,@Param("applyAccount")String applyAccount);
}
