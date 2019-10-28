package com.rondaful.cloud.user.service;


import java.util.List;
import java.util.Map;

import com.rondaful.cloud.common.entity.Page;
import com.rondaful.cloud.user.entity.User;


public interface UserRoleService {
	/**
	 * 增加角色和用户的关系
	 * @param map
	 * @return
	 */
	Integer insertUser_Orle(Map<String,Object> map);
	
	/**
	 * 根据用户id删除对应的角色关系
	 * @param uid
	 * @return
	 */
//	Integer deleteUser_Role(Integer uid);
	
	/**
	 * 查询用户列表信息
	 * @param map  查询条件
	 * @return
	 */
	Page userListQuery(Map<String,Object> map);
	
}
