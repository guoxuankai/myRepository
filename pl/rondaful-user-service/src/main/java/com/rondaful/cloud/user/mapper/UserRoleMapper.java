package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.user.entity.RoleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

//@Mapper
public interface UserRoleMapper {
	/**
	 * 创建用户id 和 角色id 的关系
	 * @param map
	 * @return
	 */
	Integer insertUserRole(Map<String,Object> map);
	
	/**
	 * 根据对应的用户id删除对应的角色关系
	 * @param uid
	 * @return
	 */
	Integer deleteUser_Role(@Param("uid") Integer uid);
	
	/**
	 * 根据用户id获取每一位用户的角色名称
	 * @param userId
	 * @return
	 */
	List<RoleInfo> getRoleName(@Param("userId") Integer userId);
	
	/**
	 * 根据userId获取对应的角色id
	 * @param userId
	 * @return
	 */
	List<Integer> getRoleIdsByUserId(@Param("userId") Integer userId);
	
	/**
	 * 找出当前角色id相关联的用户id
	 * @param rid
	 * @return
	 */
	List<Integer> selectUserByRole(@Param("rid") Integer rid);
}

