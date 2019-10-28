package com.rondaful.cloud.user.service.impl;

import com.rondaful.cloud.common.constant.UserConstants;
import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.common.utils.RedisUtils;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.user.entity.Menu;
import com.rondaful.cloud.user.entity.NewMenu;
import com.rondaful.cloud.user.mapper.MenuMapper;
import com.rondaful.cloud.user.service.MenuService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Autowired
    private RedisUtils redisUtils;


    @Autowired
    public MenuServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }


    @Override
    public List<Menu> findAll(Menu menu) {
        return setMenu(menuMapper.findAll(menu));
    }

    @Override
    public List<Menu> findAllNotSet(Menu menu) {
        return menuMapper.findAll(menu);
    }

    @Override
    public Menu getMenuByMenuId(Integer menuId) {
        return menuMapper.getMenuByMenuId(menuId);
    }

    @Override
    public Integer addMenu(Menu menu) {
        return menuMapper.addMenu(menu);
    }

    @Override
    public Integer updateMenu(Menu menu) {
        return menuMapper.updateMenu(menu);
    }

    @Override
    public Integer deleteMenu(Integer id) {
        return menuMapper.deleteMenu(id);
    }

    @Override
    public List<String> getMenuUrlsByPlatform(Integer platform) {
        /*Object o = redisUtils.get(UserConstants.AUTHEN_LIST_KEY + platform);
        if(o != null){
            if(o instanceof List){
                List<String> list = (List<String>) o;
                if(list.size() > 0)
                    return list;
            }
        }*/
        List<Menu> menus = this.findAll(new Menu() {{
            setPlatformType(platform);
        }});
        ArrayList<String> urls = new ArrayList<>();
        menus.forEach(m ->{
            if(StringUtils.isNotBlank(m.getHref()))
                urls.add(m.getHref());
        });
        redisUtils.set(UserConstants.AUTHEN_LIST_KEY + platform,urls);
        return urls;
    }


    /**
     * 根据菜单id批量获取列表
     *
     * @param menuIds
     * @return
     */
    @Override
    public List<MenuCommon> getsByIds(List<Integer> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)){
            return new ArrayList<>();
        }
        List<Menu> list=this.menuMapper.getsByIds(menuIds);
        return this.toMenuCommon(list);
    }


    /**
     * 根据平台类型获取所有菜单
     *
     * @param platformType
     * @return
     */
    @Override
    public List<MenuCommon> getsAll(Integer platformType,Boolean remove) {
        List<Menu> menuDOs=this.menuMapper.getsAll(platformType,remove);
        return this.toMenuCommon(menuDOs);
    }

    /**
     * 将菜单列表设置为树状结构
     *
     * @param menus 菜单列表
     * @return 树状结构的列表
     */
    private List<Menu> setMenu(List<Menu> menus) {
        menus.forEach(m -> m.setName(Utils.translation(m.getName())));
        ArrayList<Menu> menus1 = new ArrayList<>();
        menus.forEach(m -> {
            if (m.getParentId() == 0)
                menus1.add(m);
            menus.forEach(mm -> {
                if (mm.getParentId().equals(m.getId())) {
                    if (m.getChildren() == null) {
                        m.setChildren(new ArrayList<>());
                    }
                    m.getChildren().add(mm);
                }
            });
        });
        return menus1;
    }

    /**
     * 转成公共list对象
     * @param list
     * @return
     */
    private List<MenuCommon> toMenuCommon(List<Menu> list){
        List<MenuCommon> menuDTOList=new ArrayList<>();
        for (Menu menu:list ) {
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
