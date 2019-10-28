package com.rondaful.cloud.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMenuMapper {

	/**
	 * 添加角色权限关系
	 * @param map
	 * @return
	 */
	Integer insertOrleMenu(Map<String, Object> map);
	
	Integer deleteRoleMenu(@Param("rid") Integer rid,@Param("platformType")Integer platformType);
	
	List<Map<String,Object>> getMenuNameByRoleId(@Param("roleId") Integer roleId);
	
	/**
	 * 根据角色Id获取对应的权限id 
	 * @param roleIds
	 * @return
	 */
	List<Integer> getMenuIdsByRoleIds(@Param("roleIds") Integer roleIds);
}
