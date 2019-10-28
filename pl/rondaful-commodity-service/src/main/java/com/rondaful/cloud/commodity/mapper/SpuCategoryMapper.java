package com.rondaful.cloud.commodity.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.commodity.entity.SpuCategory;
import com.rondaful.cloud.common.mapper.BaseMapper;

public interface SpuCategoryMapper extends BaseMapper<SpuCategory>{
    
	/**
	 * @Description:根据spu-平台名称-站点编码查询
	 * @param spu
	 * @param platform
	 * @param siteCode
	 * @return int
	 * @author:范津
	 */
	SpuCategory queryBySpuPlatformSite(Map<String, Object> map);
	
	/**
	 * @Description:通过spuId-平台名称-站点名称更新 
	 * @param spuCategory
	 * @return void
	 * @author:范津
	 */
	void updateBySpuPlatformSite(SpuCategory spuCategory);
	
	/**
	 * @Description:根据spuId查询spu分类映射
	 * @param spu
	 * @return List<SpuCategory>
	 * @author:范津
	 */
	List<SpuCategory> queryList(@Param("spu")String spu);
}