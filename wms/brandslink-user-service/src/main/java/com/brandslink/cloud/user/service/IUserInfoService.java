package com.brandslink.cloud.user.service;

import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.AddOrUpdateUserRequestDTO;
import com.brandslink.cloud.user.dto.request.GetUserListRequestDTO;
import com.brandslink.cloud.user.dto.response.CodeAndNameResponseDTO;
import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.UserInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.UserWarehouseDetailResponseDTO;
import com.brandslink.cloud.user.entity.MenuInfo;

import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @ClassName IUserInfoService
 * @Author tianye
 * @Date 2019/6/12 10:28
 * @Version 1.0
 */
public interface IUserInfoService {

    /**
     * 根据模糊条件查询用户列表信息
     *
     * @param request
     * @return
     */
    Page<UserInfoResponseDTO> getUserList(GetUserListRequestDTO request);

    /**
     * 添加用户
     *
     * @param request
     */
    void addUser(AddOrUpdateUserRequestDTO request);

    /**
     * 修改用户信息
     *
     * @param request
     */
    void updateUser(AddOrUpdateUserRequestDTO request);

    /**
     * 重置密码
     *
     * @param id
     */
    void reset(Integer id);

    /**
     * 绑定角色
     *
     * @param id
     * @param warehouseList
     * @param roleIds
     */
    void updateBindingRoles(Integer id, List<RoleInfoResponseDTO.WarehouseDetail> warehouseList, List<Integer> roleIds);

    /**
     * 修改密码
     *
     * @param id
     * @param oldPassword
     * @param changePassword
     */
    void updatePassword(Integer id, String oldPassword, String changePassword);

    /**
     * 账号启用
     *
     * @param ids
     */
    void enabled(List<Integer> ids);

    /**
     * 账号禁用
     *
     * @param ids
     */
    void disabled(List<Integer> ids);

    /**
     * 根据账号查询所属仓库
     *
     * @param account
     * @return
     */
    List<UserWarehouseDetailResponseDTO> getWarehouseDetail(String account);

    /**
     * 查询账户id和姓名
     *
     * @param warehouseCode
     * @return
     */
    List<Map<String, String>> getAccountNameListByWarehouseId(String warehouseCode);

    /**
     * 根据用户id查询所属仓库信息
     *
     * @return
     */
    List<CodeAndNameResponseDTO> getWarehouseDetailByUserId();

    /**
     * 绑定快捷菜单
     *
     * @param shortcutMenus
     * @param flag
     */
    void bindingShortcutMenus(List<Integer> shortcutMenus, Integer flag);

    /**
     * 获取当前用户绑定的快捷菜单
     *
     * @param flag
     * @return
     */
    List<MenuInfo> getShortcutMenus(Integer flag);
}
