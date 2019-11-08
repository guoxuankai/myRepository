package com.brandslink.cloud.common.service;


import com.brandslink.cloud.common.entity.Page;

/**
 * 基础服务类
 * */
public interface BaseService<T> {

	int deleteByPrimaryKey(Long primaryKey);

	int insert(T t);

	int insertSelective(T t);

	T selectByPrimaryKey(Long primaryKey);

	int updateByPrimaryKeySelective(T t);

	int updateByPrimaryKey(T t) throws NoSuchFieldException, IllegalAccessException;
	
	/**
	 * 查询分页
	 * */
	Page<T> page(T t);
	
}
