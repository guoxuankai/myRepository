package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.Page;

import java.util.List;
import java.util.Map;

public interface RoleMenuService {
	
	/**
	 * 创建角色权限关系
	 * @param map
	 * @return
	 */
	Integer insertOrleMenu(Map<String,Object> map,List<Integer> menuIds);
	
	/**
	 * 删除对应角色的权限
	 * @param rid
	 * @return
	 */
	Integer roleMenuDelete(Integer rid,Integer platformType);
	
	/**
	 * 分页查询角色列表
	 * @param map
	 * @return
	 */
	Page getRoleList(Map<String,Object> map);
	
}
