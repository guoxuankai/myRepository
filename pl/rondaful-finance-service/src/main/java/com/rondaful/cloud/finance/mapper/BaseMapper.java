package com.rondaful.cloud.finance.mapper;

import java.util.List;

import com.rondaful.cloud.finance.vo.conditions.BaseConditionVo;

/**
 * 基础Mapper
 * */
public interface BaseMapper<T> {

	int deleteByPrimaryKey(Integer primaryKey);

	int insert(T t);

	int insertSelective(T t);

	T selectByPrimaryKey(Integer primaryKey);

	int updateByPrimaryKeySelective(T t);

	int updateByPrimaryKey(T t);
	
	List<T> pageQueryWithConditions(BaseConditionVo conditions);
	
}
