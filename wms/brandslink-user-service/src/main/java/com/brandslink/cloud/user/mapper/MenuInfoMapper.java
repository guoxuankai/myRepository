package com.brandslink.cloud.user.mapper;

import com.brandslink.cloud.common.mapper.BaseMapper;
import com.brandslink.cloud.user.entity.MenuInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuInfoMapper extends BaseMapper<MenuInfo> {

    /**
     * 查询所有菜单列表信息
     *
     * @return
     */
    List<MenuInfo> selectAll(@Param("flag") Integer flag);

    /**
     * 根据父级id查询最大排序号
     *
     * @param i
     * @return
     */
    Byte selectMaxSeqByParentId(int i);

    /**
     * 根据菜单id删除角色对应的权限 -> wms
     *
     * @param id
     */
    void deleteRoleMenuByMenuIdForWms(Integer id);

    /**
     * 根据父级id该父级下面该名称的数量
     *
     * @param id
     * @param name
     * @param parentId
     * @return
     */
    Integer selectNameByParentId(@Param("id") Integer id, @Param("name") String name, @Param("parentId") Integer parentId, @Param("belong") Integer belong);

    /**
     * 根据用户id查询拥有权限的菜单列表 -> wms
     *
     * @param id
     * @return
     */
    List<MenuInfo> selectMenusAllByUserIdForWms(@Param("id") Integer id, @Param("flag") Integer flag);

    /**
     * 获取菜单树列表，过滤功能菜单
     *
     * @param id
     * @return
     */
    List<MenuInfo> selectAllByUserIdOfMenus(@Param("id") Integer id, @Param("flag") Integer flag, @Param("filter") Integer filter);

    /**
     * 查询首页菜单id
     *
     * @return
     */
    List<Integer> selectHomeMenuIds(@Param("belong") Integer belong);

    /**
     * 根据用户id查询拥有权限的菜单列表 -> oms
     *
     * @param id
     * @param flag
     * @return
     */
    List<MenuInfo> selectMenusAllByUserIdForOms(@Param("id") Integer id, @Param("flag") Integer flag);

    /**
     * 根据菜单id删除角色对应的权限 -> oms
     *
     * @param id
     */
    void deleteRoleMenuByMenuIdForOms(Integer id);
}