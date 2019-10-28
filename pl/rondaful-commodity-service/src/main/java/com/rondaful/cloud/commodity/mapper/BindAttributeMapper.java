package com.rondaful.cloud.commodity.mapper;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.BindAttribute;

public interface BindAttributeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BindAttribute record);

    int insertSelective(BindAttribute record);

    BindAttribute selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BindAttribute record);

    int updateByPrimaryKey(BindAttribute record);
    
    BindAttribute getByErpNameAndValue(@Param("erpAttrName")String erpAttrName,@Param("erpAttrValue")String erpAttrValue);
    
    BindAttribute getByPlNameAndValue(@Param("attrCnName")String attrCnName,@Param("attrCnValue")String attrCnValue);
}