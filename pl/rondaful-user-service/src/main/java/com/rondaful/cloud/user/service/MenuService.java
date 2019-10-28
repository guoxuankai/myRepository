package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.user.entity.Menu;

import java.util.List;

public interface MenuService {


    /**
     * 通过条件查询菜单列表树状设置
     * @param menu 查询条件
     * @return 返回菜单列表
     */
    List<Menu> findAll(Menu menu);

    /**
     * 通过条件查询菜单列表不树状设置
     * @param menu 查询条件
     * @return 返回菜单列表
     */
    List<Menu> findAllNotSet(Menu menu);

    /**
     * 根据menuid获取menu信息
     */
    Menu getMenuByMenuId(Integer menuId);

    /**
     * 添加菜单
     * @param menu 添加菜单
     * @return 新添加菜单的id
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
    Integer deleteMenu (Integer id);

    /**
     * 获取某个平台中的权限Url列表
     * @param platform
     * @return
     */
    List<String> getMenuUrlsByPlatform(Integer platform);

    /**
     * 根据菜单id获取列表
     * @param menuIds
     * @return
     */
    List<MenuCommon> getsByIds(List<Integer> menuIds);

    /**
     * 根据平台类型获取所有菜单
     * @param platformType
     * @return
     */
    List<MenuCommon> getsAll(Integer platformType,Boolean remove);

}
