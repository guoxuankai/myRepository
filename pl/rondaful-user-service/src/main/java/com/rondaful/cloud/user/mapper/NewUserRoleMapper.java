package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.NewUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewUserRoleMapper extends BaseMapper<NewUserRole> {

    /**
     * 批量插入用户角色绑定关系
     * @param list
     * @return
     */
    Integer insertBatch(List<NewUserRole> list);

    /**
     * 根据用户id查询对应平台的角色
     * @param userId
     * @param platformType
     * @return
     */
    List<Integer> getsByUser(@Param("userId") Integer userId,@Param("platformType") Integer platformType);

    /**
     * 根据角色id获取平台对应的用户
     * @param roleId
     * @param platformType
     * @return
     */
    List<Integer> getsUserByRole(@Param("roleId") Integer roleId,@Param("platformType") Integer platformType);

    /**
     * 根据用户与平台删除用户与角色绑定关系
     * @param userId
     * @param platformType
     * @return
     */
    Integer deleteByUserId(@Param("userId") Integer userId,@Param("platformType") Integer platformType);

    /**
     * 获取角色绑定的关系
     * @param roleId
     * @return
     */
    List<NewUserRole> getByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据角色id批量查询用户did
     * @param roleIds
     * @return
     */
    List<Integer> getsByRoleId(@Param("roleIds") List<Integer> roleIds);
}