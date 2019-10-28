package com.rondaful.cloud.seller.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.AliexpressCategoryAttributeSelect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AliexpressCategoryAttributeSelectMapper extends BaseMapper<AliexpressCategoryAttributeSelect> {

    public List<AliexpressCategoryAttributeSelect> getCategoryAttributeSelectByList(@Param("categoryId")Long categoryId,@Param("attributeId")Long attributeId,@Param("empowerId")Long empowerId);

    /**
     * xml 文件用到的接口
     * @param attributeId
     * @return
     */
    public List<AliexpressCategoryAttributeSelect> getCategoryAttributeSelectByAttributeId(@Param("categoryId")Long categoryId,@Param("attributeId")Long attributeId);

    /**
     * 根据刊登账号修改属性值是否可用
     * @param empowerId
     * @return
     */
    public int updateAttributeSelectByEmpowerId(Long empowerId);

    /**
     *
     * @param selectIds
     * @return
     */
    public List<AliexpressCategoryAttributeSelect> getCategoryAttributeSelectByselectIds(@Param("selectIds")List<Long> selectIds);


    /**
     *
     * @param selectId
     * @return
     */
    public AliexpressCategoryAttributeSelect getCategoryAttributeSelectBySelectId(@Param("selectId")Long selectId);

}