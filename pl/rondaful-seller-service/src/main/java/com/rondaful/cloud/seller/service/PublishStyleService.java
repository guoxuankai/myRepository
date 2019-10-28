package com.rondaful.cloud.seller.service;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.seller.entity.PublishStyle;
import com.rondaful.cloud.seller.entity.PublishStyleType;
import com.rondaful.cloud.seller.vo.PublishStyleSearchVO;
import com.rondaful.cloud.seller.vo.PublishStyleTypeSearchVO;

import java.util.List;

public interface PublishStyleService {

    /**
     * 列表信息
     * @param vo
     * @return
     * @throws Exception
     */
    Page<PublishStyleType> findPublishStyleTypePage(PublishStyleTypeSearchVO vo) throws Exception;

    /**
     * 风格类型保存
     * @param publishStyleType
     * @return
     */
    PublishStyleType savePublishStyleType(PublishStyleType publishStyleType);

    /**
     * 删除
     * @param id
     * @return
     */
    Integer deletePublishStyleType(Long id);

    /**
     * 风格类型详细
     * @param id
     * @return
     */
    PublishStyleType getPublishStyleTypeById(Long id);

    /**
     * 下拉风格类型数据
     * @param platform
     * @param plAccount
     * @return
     */
    List<PublishStyleType> getPublishStyleTypeAll(Integer platform,Integer createId,Boolean systemIs);



    /**
     * 列表信息
     * @param vo
     * @return
     * @throws Exception
     */
    Page<PublishStyle> findPublishStylePage(PublishStyleSearchVO vo) throws Exception;

    /**
     * 保存风格
     * @param publishStyle
     * @return
     */
    PublishStyle savePublishStyle(PublishStyle publishStyle);

    /**
     * 删除
     * @param id
     * @return
     */
    Integer deletePublishStyle(Long id);

    /**
     * 详情
     * @param id
     * @return
     */
    PublishStyle getPublishStyleById(Long id);

    /**
     * 风格类型下是否有风格
     * @param styleTypeId
     * @return
     */
    Integer checkPublishStyle(Long styleTypeId);


    /**
     * 品连分类获取分格
     * @param publishStyle
     * @return
     */
    PublishStyle getStyleTypeCategory(Integer platform,Long createId,String plCategory);
}
