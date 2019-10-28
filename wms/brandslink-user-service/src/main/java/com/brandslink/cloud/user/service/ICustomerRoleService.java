package com.brandslink.cloud.user.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.CustomerRoleRequestDTO;
import com.brandslink.cloud.user.dto.response.CustomerRoleInfoResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 角色管理业务接口实现
 * @date 2019/9/6 10:30
 */
public interface ICustomerRoleService {

    /**
     * 获取角色列表
     *
     * @param request
     * @return
     */
    Page<CustomerRoleInfoResponseDTO> getRoleList(CustomerRoleRequestDTO request);

    /**
     * 新增角色
     *
     * @param roleName
     * @param roleDescription
     * @param menuIds
     */
    void addRole(String roleName, String roleDescription, List<Integer> menuIds);

    /**
     * 设置权限
     *
     * @param id
     * @param roleDescription
     * @param menuIds
     */
    void addPermission(Integer id, String roleDescription, List<Integer> menuIds);

    /**
     * 获取角色权限
     *
     * @param id
     * @return
     */
    Map<String, Object> getPermission(Integer id);
}
