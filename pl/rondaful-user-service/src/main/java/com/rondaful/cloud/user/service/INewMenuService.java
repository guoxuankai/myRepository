package com.rondaful.cloud.user.service;

import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.user.model.dto.menu.MenuDTO;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/28
 * @Description:
 */
@Deprecated
public interface INewMenuService {

    /**
     * 根据平台类型获取菜单列表
     * @param platformType
     * @return
     */
    List<MenuCommon> getsAll(Integer platformType);

    /**
     * 根据菜单id批量查询
     * @param menuIds
     * @return
     */
    List<MenuCommon> getsByIds(List<Integer> menuIds);
}
