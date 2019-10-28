package com.rondaful.cloud.commodity.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.SiteCategory;
import com.rondaful.cloud.common.mapper.BaseMapper;

public interface SiteCategoryMapper extends BaseMapper<SiteCategory> {
   
	/**
	 * @Description:查询平台的站点分类映射列表
	 * @param platform
	 * @return List<SiteCategory>
	 * @author:范津
	 */
	List<SiteCategory> queryList(@Param("platform")String platform,@Param("categoryLevel3")Long categoryLevel3);
	
	/**
	 * @Description:清除站点的分类信息
	 * @param platform
	 * @return void
	 * @author:范津
	 */
	void cleanUp(@Param("platform")String platform,@Param("categoryLevel3")Long categoryLevel3);
	
	/**
	 * @Description:查询站点映射
	 * @param spuId
	 * @param platform
	 * @param siteCode
	 * @param categoryLevel3
	 * @return SiteCategory
	 * @author:范津
	 */
	SiteCategory querySiteCategory(Map<String, Object> map);
	
	
	SiteCategory querySiteCategoryInfo(Map<String, Object> map);

	/**
	 * @Description:批量新增
	 * @param list
	 * @return void
	 * @author:范津
	 */
	void insertBatch(List<SiteCategory> list);
	
	
	/**
	 * @Description:根据商品最后一级分类ID删除映射
	 * @param categoryLevel3
	 * @return void
	 * @author:范津
	 */
	void deleteByCategorylevel3(@Param("categoryLevel3")Long categoryLevel3);
}