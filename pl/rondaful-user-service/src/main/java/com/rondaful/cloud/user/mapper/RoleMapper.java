package com.rondaful.cloud.user.mapper;

import java.util.List;
import java.util.Map;

import com.rondaful.cloud.user.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色表对应的Mapper
 * @author Administrator
 *
 */
@Mapper
public interface RoleMapper {

	/**
	 * 获取角色名称和角色id
	 * @param userId
	 * @return
	 */
	List<Map<String,Object>> getroleInfo(@Param("userId") Integer userId);

	/**
	 * 根据角色名称找到角色信息
	 * @param roleName
	 * @return
	 */
	Role isRoleByName(@Param("roleName") String roleName, @Param("createId") Integer createId);
	
	/**
	 * 获取角色代码
	 * @param userId
	 * @return
	 */
	Integer getRoleCode(@Param("userId") Integer userId);
	
	/**
	 * 创建角色
	 * @param role
	 * @return
	 */
    Integer insertRole( Role role );
    
    /**
     * 	根据角色id修改角色状态
     * @param map
     * @return
     */
    Integer updataStatus(Map<String, Object> map);
    
    
    /**
     * 	根据多个角色id找到对应的角色信息
     * @param roleIds
     * @return
     */
    List<Role> selectRolesFindByIds(List<Integer> roleIds);
    
    /**
     * 	删除角色
     * @param roleIds
     * @return
     */
    Integer delectRole(List<Integer> roleIds);
    
    /**
     * 	修改角色资料
     * @param role
     * @return
     */
    Integer updateInfo(Role role);
    
    /**
   	 * 根据角色状态，角色名称，角色代码查询角色信息
   	 * @map       查询条件
   	 * @return    用户数据
   	 */
   	List<Role> getRoleByMap(Map<String,Object> map);
   	
   	/**
     * 	根据角色id找到对应的角色信息
     * @param rid
     * @return
     */
    Role selectRoleFindById(@Param("rid") Integer rid);

    
}