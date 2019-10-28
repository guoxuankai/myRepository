package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.dto.request.AddOrUpdateUserRequestDTO;
import com.brandslink.cloud.user.dto.request.GetUserListRequestDTO;
import com.brandslink.cloud.user.dto.response.UserWarehouseDetailResponseDTO;
import com.brandslink.cloud.user.entity.MenuInfo;
import com.brandslink.cloud.user.entity.RoleWarehouseResult;
import com.brandslink.cloud.user.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 查询用户列表信息，包含角色列表
     *
     * @param request
     * @return
     */
    List<UserInfo> getUserList(GetUserListRequestDTO request);

    /**
     * 根据用户id设置角色
     *
     * @param id
     * @param roleIds
     */
    void insertRolesByUserId(@Param("id") Integer id, @Param("roleIds") List<Integer> roleIds);

    /**
     * 根据用户id删除角色
     *
     * @param id
     */
    void deleteRolesByUserId(Integer id);

    /**
     * 根据账号查询用户信息
     *
     * @param account
     * @return
     */
    UserInfo selectByAccount(String account);

    /**
     * 根据姓名查询用户信息
     *
     * @param name
     * @return
     */
    UserInfo selectByName(String name);

    /**
     * 根据账号查询用户信息
     *
     * @param account
     * @return
     */
    List<UserInfo> selectByAccountResult(String account);

    /**
     * 批量启用账号
     *
     * @param ids
     */
    void enabledByIds(List<Integer> ids);

    /**
     * 批量禁用账号
     *
     * @param ids
     */
    void disabledByIds(List<Integer> ids);

    /**
     * 根据用户id设置所属仓库
     *
     * @param warehouseDetail
     */
    void insertWarehouseDetailByUserId(List<RoleWarehouseResult> warehouseDetail);

    /**
     * 删除用户对应的仓库信息
     *
     * @param id
     */
    void deleteWarehouseDetailByUserId(Integer id);

    /**
     * 根据用户id查询所属仓库列表
     *
     * @param id
     * @return
     */
    List<UserWarehouseDetailResponseDTO> selectWarehouseInfoByUserId(Integer id);

    /**
     * 根据所属仓库code查询所有账户id、Account和name
     *
     * @return
     */
    List<UserInfo> selectIdAndNamesByWarehouseId(@Param("warehouseCode") String warehouseCode);

    /**
     * 根据公司id修改所有公司名称
     *
     * @param id
     * @param name
     */
    void updateCompanyNameByCompanyId(@Param("id") Integer id, @Param("name") String name);

    /**
     * 根据部门id修改所有部门名称
     *
     * @param id
     * @param name
     */
    void updateDepartmentNameByDepartmentId(@Param("id") Integer id, @Param("name") String name);

    /**
     * 根据仓库id更新仓库名称
     *
     * @param warehouseCode
     * @param warehouseName
     */
    void updateWarehouseNameByWarehouseId(@Param("warehouseCode") String warehouseCode, @Param("warehouseName") String warehouseName);

    /**
     * 根据账户id添加部门信息
     *
     * @param userInfoId
     * @param detailList
     */
    void insertDepartmentDetailByUserId(@Param("id") Integer userInfoId, @Param("list") List<AddOrUpdateUserRequestDTO.DepartmentDetail> detailList);

    /**
     * 根据账户id删除部门信息
     *
     * @param id
     */
    void deleteDepartmentDetailByUserId(Integer id);

    /**
     * 根据账户id插入快捷菜单
     *
     * @param userId
     * @param shortcutMenus
     * @param flag
     */
    void insertShortcutMenusByUserId(@Param("userId") Integer userId, @Param("shortcutMenus") List<Integer> shortcutMenus, @Param("flag") Integer flag);

    /**
     * 根据账户id查询所属快捷菜单
     *
     * @param userId
     * @param flag
     * @return
     */
    List<MenuInfo> getShortcutMenusByUserId(@Param("userId") Integer userId, @Param("flag") Integer flag);

    /**
     * 根据用户id删除对应的快捷菜单
     *
     * @param userId
     * @param flag
     */
    void deleteShortcutMenusByUserI(@Param("userId") Integer userId, @Param("flag") Integer flag);

    /**
     * 根据账户id查询账号
     *
     * @param id
     * @return
     */
    String selectAccountById(@Param("id") Integer id);
}