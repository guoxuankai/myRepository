package com.rondaful.cloud.user.mapper;

import com.rondaful.cloud.user.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface MenuMapper {
	
	/**
	 * 根据对应的角色id获取对应的权限
	 * @param roleIds
	 * @return
	 */
	List<Menu> getMenuByRoleId(List<Integer> roleIds);
	
	/**
	 * 根据menuid获取menu信息
	 */
   Menu getMenuByMenuId(@Param("menuId") Integer menuId);


	/**
	 * 通条件查询菜单列表
	 * @param menu 查询条件
	 * @return 菜单列表
	 */
   List<Menu> findAll(Menu menu);

    /**
     * 添加菜单
     * @param menu cd
     * @return 变动行数
     */
   Integer addMenu(Menu menu);

	/**
	 * 更新菜单
	 * @param menu 菜单
	 * @return 操作行数
	 */
	Integer updateMenu(Menu menu);

	/**
	 * 根据id删除菜单
	 * @param id id
	 * @return 受影响行数
	 */
	Integer deleteMenu (@Param("id") Integer id);

	/**
	 * 根据菜单id列表
	 * @param menuIds
	 * @return
	 */
	List<Menu> getsByIds(@Param("menuIds") List<Integer> menuIds);

	/**
	 * 获取所有列表
	 * @return
	 */
	List<Menu> getsAll(@Param("platformType") Integer platformType,@Param("remove") Boolean remove);
}