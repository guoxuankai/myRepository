package com.brandslink.cloud.common.mapper;

import java.util.List;


/**
 * 基础Mapper
 * */
public interface BaseMapper<T> {

	int deleteByPrimaryKey(Long primaryKey);

	int insert(T t);

	int insertSelective(T t);

	T selectByPrimaryKey(Long primaryKey);

	int updateByPrimaryKeySelective(T t);

	int updateByPrimaryKey(T t);
	
	/**
	 * 查询列表
	 * */
	List<T> page(T t);
	
}
