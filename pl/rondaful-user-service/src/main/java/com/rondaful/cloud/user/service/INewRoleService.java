package com.rondaful.cloud.user.service;

import com.rondaful.cloud.user.entity.NewRole;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.menu.TreeDTO;
import com.rondaful.cloud.user.model.dto.role.BindRoleDTO;
import com.rondaful.cloud.user.model.dto.role.QueryRolePageDTO;
import com.rondaful.cloud.user.model.dto.role.RoleDTO;
import com.rondaful.cloud.user.model.dto.role.RoleMenuDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
public interface INewRoleService {

    public static String ROLE_MENU="user:role.menu.v0";

    /**
     * 新增角色
     * @param dto
     * @return
     */
    Integer add(RoleDTO dto);

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    Integer delete(Integer roleId);

    /**
     * 修改角色名
     * @param roleId
     * @param roleName
     * @param remark
     * @return
     */
    Integer updateRoleName(Integer roleId,String roleName,String remark,String updateBy);

    /**
     * 修改菜单授权
     * @param list
     * @return
     */
    Integer updateRoleMenu(List<Integer> list,Integer roleId);

    /**
     * 分页查询角色
     * @param dto
     * @return
     */
    PageDTO<RoleDTO> getPage(QueryRolePageDTO dto);

    /**
     * 根据角色id获取路径权限
     * @param roleId
     * @return
     */
    List<Integer> getMenu(Integer roleId);

    /**
     * 根据角色id批量获取路径权限
     * @param roleIds
     * @return
     */
    List<Integer> getsMenu(List<Integer> roleIds);

    /**
     * 根据角色批量获取角色名
     * @param roleIds
     * @return
     */
    List<BindRoleDTO> getsName(List<Integer> roleIds);

    /**
     * 查询所有角色名
     * @param platformType
     * @param attributionId
     * @return
     */
    List<TreeDTO> getTree(Integer platformType,Integer attributionId);

    /**
     * 根据路由获取用户id
     * @param href
     * @return
     */
    List<Integer> getsByHref(String href,Integer platformType);


}
