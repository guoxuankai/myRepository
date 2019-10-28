package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.common.mapper.BaseMapper;
import com.rondaful.cloud.user.entity.NewRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewRoleMenuMapper extends BaseMapper<NewRoleMenu> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer insertBatch(List<NewRoleMenu> list);

    /**
     * 根据角色id物理删除
     * @param roleId
     * @return
     */
    Integer deleteByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据角色id获取url的授权
     * @param roleId
     * @return
     */
    List<Integer> getMenu(@Param("roleId") Integer roleId);

    /**
     * 根据用户id批量查询菜单
     * @param roleIds
     * @return
     */
    List<Integer> getsMenu(@Param("roleIds") List<Integer> roleIds);

    /**
     * 根据路由查询角色
     * @param href
     * @return
     */
    List<Integer> getsByHref(@Param("href") String href,@Param("platformType") Integer platformType);


}