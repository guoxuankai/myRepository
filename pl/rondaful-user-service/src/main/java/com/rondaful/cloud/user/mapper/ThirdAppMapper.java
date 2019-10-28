package com.rondaful.cloud.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.ThirdApp;

public interface ThirdAppMapper extends BaseMapper<ThirdApp> {
	/**
	 * 根据key取得第三方应信息
	* @Title: getByAppKey
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param @param appkey
	* @param @return    参数
	* @return ThirdApp    返回类型
	* @throws
	 */
	ThirdApp getByAppKey(@Param("appKey")String appKey);
	
	  /**
          * 分页查询第三方应信息
     * @param warehouseId
     * @param pinlianSku
     * @param supplierSku
     * @param status
     * @return
     */
    List<ThirdApp> getsPage(@Param("status") Integer status);

	/**
	 * 根据id列表查询
	 * @param list
	 * @return
	 */
	List<ThirdApp> getsById(@Param("list") List<Integer> list);
}