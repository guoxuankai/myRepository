package com.rondaful.cloud.seller.mapper;

import java.util.List;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.seller.entity.amazon.AmazonCategory;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;

public interface AmazonCategoryMapper  extends BaseMapper<AmazonCategory> {
	public List<AmazonCategory> selectCategory(AmazonCategory amazonCategory);
	
	public List<AmazonCategory> selectCategoryListByCategoryId(@Param("categoryIds") Long [] categoryIds,@Param("siteName") String siteName);
	
	public List<AmazonCategory> selectCategoryListById(Integer [] ids);

	public List<AmazonCategory> selectCategoryListBySiteAndKeyWord(AmazonCategory amazonCategory);
}