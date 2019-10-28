package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.dto.request.GetRoleListRequestDTO;
import com.brandslink.cloud.user.entity.RoleInfo;
import com.brandslink.cloud.user.entity.RoleWarehouseResult;
import com.brandslink.cloud.user.entity.UserAndRoleEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleInfoMapper extends BaseMapper<RoleInfo> {

    /**
     * 查询最大的角色代码
     *
     * @return
     */
    Integer selectMaxRoleCode();

    /**
     * 根据角色id设置角色权限
     *
     * @param id
     * @param menuList
     * @param flag
     */
    void insertMenusByRoleId(@Param("id") Integer id, @Param("menuList") List<Integer> menuList, @Param("flag") Integer flag);

    /**
     * 根据角色名称查询角色数量
     *
     * @param roleName
     * @return
     */
    Integer selectByRoleName(String roleName);

    /**
     * 根据角色id删除对应的角色权限
     *
     * @param id
     * @param flag
     */
    void deleteMenusByRoleId(@Param("id") Integer id, @Param("flag") Integer flag);

    /**
     * 根据角色id查询用户数量
     *
     * @param id
     * @return
     */
    Integer selectUserCountByRoleId(Integer id);

    /**
     * 获取角色列表
     *
     * @param roleInfo
     * @return
     */
    List<RoleInfo> getPage(GetRoleListRequestDTO roleInfo);

    /**
     * 根据角色id获取当前角色对应的菜单id
     *
     * @param id
     * @return
     */
    List<Integer> selectMenusByRoleId(@Param("id") Integer id, @Param("flag") Integer flag);

    /**
     * 根据所属仓库查询对应的角色列表
     *
     * @param codeList
     * @return
     */
    List<RoleWarehouseResult> selectRoleListByWarehouseCode(List<String> codeList);

    /**
     * 通过请求url查询所需要的角色列表
     *
     * @param requestUrl
     * @return
     */
    List<Integer> getRoleListByMenuUrl(String requestUrl);

    /**
     * 根据角色id设置所属仓库
     *
     * @param warehouseDetail
     */
    void insertWarehouseDetailByRoleId(List<RoleWarehouseResult> warehouseDetail);

    /**
     * 删除角色对应的仓库信息
     *
     * @param id
     */
    void deleteWarehouseDetailByRoleId(Integer id);

    /**
     * 根据角色id获取角色信息，包含所属仓库信息
     *
     * @param longValue
     * @return
     */
    List<RoleInfo> selectAndWarehouseByPrimaryKey(Integer longValue);

    /**
     * 根据仓库名称删除对应账号所属仓库
     *
     * @param oldNames
     */
    void deleteWarehouseDetailByRoleIdAndWarehouseName(@Param("list") List<String> oldNames);

    /**
     * 根据仓库名称查询对应账号id
     *
     * @param oldNames
     * @return
     */
    List<Integer> selectUserIdsByRoleIdAndWarehouseName(List<String> oldNames);

    /**
     * 根据账号id查询对应角色列表
     *
     * @param userIds
     * @return
     */
    List<UserAndRoleEntity> selectUserIdAndRoleNamesByUserIds(List<Integer> userIds);

    /**
     * 根据账号id批量删除角色
     *
     * @param userIds
     */
    void deleteRoleByUserIds(List<Integer> userIds);

    /**
     * 根据账号id和角色名称批量添加账号角色
     *
     * @param list
     */
    void insertUserAndRoleByUserAndRoleEntity(List<UserAndRoleEntity> list);

    /**
     * 根据角色id查询所有url列表
     *
     * @param authorityList
     * @return
     */
    List<String> selectMenuUrlByRoleList(List<Integer> authorityList);

    /**
     * 批量更新关联账户角色名称
     *
     * @param oldRoleName
     * @param newRoleName
     */
    void updateUserRoleNameByRoleName(@Param("oldRoleName") String oldRoleName, @Param("newRoleName") String newRoleName);
}