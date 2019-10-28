package com.rondaful.cloud.user.service;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.user.entity.Role;

public interface RoleService {

	/**
	 * 获取角色的名称及id
	 * @param userId
	 * @return
	 */
	List<Map<String,Object>> getroleInfo(Integer userId);

	/**
	 * 获取角色代码
	 * @param userId
	 * @return
	 */
	Integer getRoleCode(Integer userId);
	
	/**
	 * 创建角色
	 * @param role
	 * @return
	 */
	Integer insertRole( Role role,Integer platformType );
	
	/**
	 * 修改角色状态
	 * @param status
	 * @param roleIds
	 * @return
	 */
	Integer updataStatus(Integer status, List<Integer> roleIds);
	
	/**
	 * 根据多个角色id找到对应的角色信息
	 * @param roleId
	 * @return
	 */
	List<Role> rolesFindByIds(List<Integer> roleId);
	
	
	/**
	 * 	删除角色
	 * @param rid
	 * @return
	 */
	Integer deleteRole(List<Integer> rid);
	
	/**
	 * 	修改角色资料
	 */
	Integer updateInfo(Role role);
}
