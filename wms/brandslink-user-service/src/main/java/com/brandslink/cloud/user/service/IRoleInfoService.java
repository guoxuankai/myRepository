package com.brandslink.cloud.user.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.GetRoleListRequestDTO;
import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.RoleListResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * 角色
 *
 * @ClassName IRoleInfoService
 * @Author tianye
 * @Date 2019/6/11 15:56
 * @Version 1.0
 */
public interface IRoleInfoService {

    /**
     * 获取角色列表
     *
     * @param requeste
     * @return
     */
    Page<RoleInfoResponseDTO> getRoleList(GetRoleListRequestDTO requeste);

    /**
     * 新增角色
     *
     * @param roleName
     * @param warehouseName
     * @param warehouseCode
     */
    void addRole(String roleName, String warehouseName, String warehouseCode);

    /**
     * 编辑角色
     *
     * @param id
     * @param roleName
     * @param warehouseName
     * @param warehouseCode
     */
    void updateRole(String id, String roleName, String warehouseName, String warehouseCode);

    /**
     * 删除角色
     *
     * @param id
     */
    void deleteRole(Integer id);

    /**
     * 设置权限
     *
     * @param id
     * @param menuIds
     * @param flag
     */
    void addPermission(Integer id, List<Integer> menuIds, Integer flag);

    /**
     * 根据角色id和平台获取当前角色所拥有的权限
     *
     * @param id
     * @param flag
     * @return
     */
    Map<String, Object> getPermission(Integer id, Integer flag);

    /**
     * 根据所有仓库查询对应的角色列表
     *
     * @return
     */
    List<RoleListResponseDTO> getRoleListByWarehouseCode();

    /**
     * 通过请求url查询所需要的角色列表
     *
     * @param requestUrl
     * @return
     */
    String getMenusByRequestUrl(String requestUrl, String platformType);

    /**
     * 通过用户角色id查询所能访问的所有url
     *
     * @param authority
     * @return
     */
    String getMenusByRoleList(String authority, String platformType);
}
