package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.user.entity.Menu;
import com.rondaful.cloud.user.entity.NewMenu;
import com.rondaful.cloud.user.mapper.NewMenuMapper;
import com.rondaful.cloud.user.service.INewMenuService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/28
 * @Description:
 */
@Service("newMenuServiceImpl")
public class NewMenuServiceImpl implements INewMenuService {

    @Autowired
    private NewMenuMapper menuMapper;

    /**
     * 根据平台类型获取菜单列表
     *
     * @param platformType
     * @return
     */
    @Override
    public List<MenuCommon> getsAll(Integer platformType) {
        List<NewMenu> menuDOs=this.menuMapper.getsAll(platformType);
        return this.toMenuCommon(menuDOs);
    }


    /**
     * 根据菜单id批量查询
     *
     * @param menuIds
     * @return
     */
    @Override
    public List<MenuCommon> getsByIds(List<Integer> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)){
            return new ArrayList<>();
        }
        List<NewMenu> list=this.menuMapper.getsByIds(menuIds);
        return this.toMenuCommon(list);
    }

    /**
     * 转成公共list对象
     * @param list
     * @return
     */
    private List<MenuCommon> toMenuCommon(List<NewMenu> list){
        List<MenuCommon> menuDTOList=new ArrayList<>();
        for (NewMenu menu:list ) {
            MenuCommon dto=new MenuCommon();
            BeanUtils.copyProperties(menu,dto);
            menuDTOList.add(dto);
        }
        List<MenuCommon> result=new ArrayList<>();
        menuDTOList.forEach(m -> {
            if (m.getParentId() == 0){
                result.add(m);
            }
            menuDTOList.forEach(mm -> {
                if (mm.getParentId().equals(m.getId())) {
                    if (m.getChildren() == null) {
                        m.setChildren(new ArrayList<>());
                    }
                    m.getChildren().add(mm);
                }
            });
        });
        return result;
    }
}
