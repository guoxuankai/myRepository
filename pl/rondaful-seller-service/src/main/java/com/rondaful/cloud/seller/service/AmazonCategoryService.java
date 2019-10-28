package com.rondaful.cloud.seller.service;

import java.util.List;

import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;

public interface AmazonCategoryService {
	
	List<AmazonCategory> queryCategoryList(Long categoryId, String siteName);
	
	/** 根据分类Category_id获取数据*/
	List<AmazonCategory> queryCategoryListByCategoryId(Long [] ids,String siteName);
	
	/** 根据分类id获取数据*/
	public List<AmazonCategory> selectCategoryListById(Integer [] ids);

	/** 根据站点及分类关键词(name)获取数据**/
	List<AmazonCategory> queryCategoryListSiteNameAndKeyWord(String keyWord, String siteName);
}
