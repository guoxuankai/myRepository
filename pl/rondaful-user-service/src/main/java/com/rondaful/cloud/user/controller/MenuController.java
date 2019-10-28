package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.entity.user.UserCommon;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.Utils;
import com.rondaful.cloud.user.entity.Menu;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.service.MenuService;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限
 *
 * @author Administrator
 */
@RestController
@Api(description = "菜单---权限相关操作")
@RequestMapping("/menu")
public class MenuController extends BaseController{


    private final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuService menuService;


    @AspectContrLog(descrption = "获取对应的用户权限", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "获取菜单列表")
    @GetMapping(value = "/api/getAllMenu")
    public List<Menu> getMenuByUser() {
        UserDTO userDTO=super.userToken.getUserDTO();
        List<Menu> all = menuService.findAll(new Menu(){{
            setPlatformType(userDTO.getPlatformType());
            setDelFlag(0);
            setVshow(String.valueOf(0));
        }});
        return all;
    }


    @AspectContrLog(descrption = "管理后台获取权限列表", actionType = SysLogActionType.QUERY)
    @ApiOperation(value = "管理后台获取权限列表")
    @GetMapping(value = "/getAllMenu")
    @ApiImplicitParams({@ApiImplicitParam(name = "platformType", value = "平台类型   0供应商平台  1卖家平台  2管理平台", dataType = "Integer", paramType = "query", required = true)})
    public List<Menu> getAllMenu(Integer platformType) {
        try {
            List<Menu> all = menuService.findAll(new Menu() {{
                setPlatformType(platformType);
            }});
            return all;
        } catch (Exception e) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "管理后台获取权限列表异常");
        }
    }



    @ApiOperation("添加菜单")
    @AspectContrLog(descrption = "添加菜单", actionType = SysLogActionType.ADD)
    @PostMapping("/addMenu")
    public Integer addMenu(@RequestBody Menu menu) {
        if (menu.getPlatformType() == null){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403.getCode(), "菜单平台不能为空");
        }
        if (menu.getParentId() == null || menu.getParentId() == 0) {
            menu.setParentId(0);
            menu.setLevel(1);
        } else {
            Menu menuByMenuId = menuService.getMenuByMenuId(menu.getParentId());
            if (menuByMenuId == null){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "添加菜单的父id不存在");
            }
            if (!menuByMenuId.getPlatformType().equals(menu.getPlatformType())){
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "添加菜单与父id菜单不属于同一平台");
            }

            if (menuByMenuId.getLevel() == 1){
                menu.setLevel(2);
            }else{
                menu.setLevel(3);
            }
        }
        List<Menu> all = menuService.findAllNotSet(new Menu() {{
            setParentId(menu.getParentId());
            setPlatformType(menu.getPlatformType());
        }});
        if (CollectionUtils.isEmpty(all)){
            menu.setSort(1);
        }else {
            menu.setSort(all.size() + 1);
        }
        menu.setHref(this.checkHref(menu.getHref()));
        menuService.addMenu(menu);
        return menu.getId();
    }

    /**
     * 更新菜单
     *
     * @param menu 更新的菜单实体
     * @return 更新的id
     */
    @ApiOperation("更新菜单")
    @AspectContrLog(descrption = "更新菜单", actionType = SysLogActionType.UDPATE)
    @PutMapping("/updateMenu")
    public Integer updateMenu(@RequestBody Menu menu) {
        if (menu.getId() == null) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100403.getCode(), "更新菜单需要菜单id");
        }
        Menu oldMenu = menuService.getMenuByMenuId(menu.getId());
        Integer parentId;
        if (menu.getParentId() == null || menu.getParentId().equals(oldMenu.getParentId())) {       //没有更改父目录
            parentId = oldMenu.getParentId();
        } else {   //更改了父目录
            parentId = menu.getParentId();
        }
        Menu parentMenu = null;
        if (parentId != 0)
            parentMenu = menuService.getMenuByMenuId(parentId);
        List<Menu> sonMenu = menuService.findAllNotSet(new Menu() {{
            setParentId(menu.getId());
        }});
        if (parentMenu == null) {
            menu.setLevel(1);
        } else if (parentMenu.getLevel() == 1) {
            menu.setLevel(2);
        } else
            menu.setLevel(3);
        if (parentMenu != null) {
            if (menu.getPlatformType() != null) {
                if (!menu.getPlatformType().equals(parentMenu.getPlatformType()))
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "更新后与父菜单不在同一平台");
            } else {
                if (!oldMenu.getPlatformType().equals(parentMenu.getPlatformType()))
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "更新后与父菜单不在同一平台");
            }
        }

        if (sonMenu != null && sonMenu.size() > 0) {
            if (menu.getPlatformType() != null) {
                sonMenu.forEach(m -> {
                    if (!m.getPlatformType().equals(menu.getPlatformType()))
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "与子菜单平台不一致");
                });
            } else {
                sonMenu.forEach(m -> {
                    if (!m.getPlatformType().equals(oldMenu.getPlatformType()))
                        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "与子菜单平台不一致");
                });
            }
        }
        menu.setHref(this.checkHref(menu.getHref()));
        menuService.updateMenu(menu);
        return menu.getId();
    }


    @ApiOperation("根据id删除菜单")
    @AspectContrLog(descrption = "根据id删除菜单", actionType = SysLogActionType.DELETE)
    @DeleteMapping("/deleteMenu/{id}")
    public Integer deleteMenu(@ApiParam(value = "菜单id", name = "id", required = true) @PathVariable Integer id) {
        Menu menuByMenuId = menuService.getMenuByMenuId(id);
        if (menuByMenuId == null)
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "没有对应菜单");
        List<Menu> all = menuService.findAllNotSet(new Menu() {{
            setParentId(id);
        }});
        if (all != null && all.size() > 0) {
            all.forEach(m -> {
                if (m.getDelFlag() == 0)
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "该菜单下还有未删除子菜单");
            });
        }
        menuService.deleteMenu(id);
        return id;
    }


    @ApiOperation(value = "获取当前角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platformType", value = "0-供应商;1-卖家;2-CMS", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Integer", paramType = "query")
    })
    @GetMapping("getsMenu")
    public List<MenuCommon> getsMenu(Integer userId){
        List<MenuCommon> list=super.userToken.getUserInfo().getMenus();
        if (StringUtils.isEmpty(super.request.getHeader("i18n"))){
            return list;
        }
        return this.getTrans(list,super.request.getHeader("i18n"));
    }


    /**
     * 检查验证href
     * @param href href
     */
    private String checkHref(String href){
        if(StringUtils.isNotBlank(href)){
         href = href.replace("\n","").replace("，",",");
            String[] split = href.split(",");
            if(split.length == 0){
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for(int i = 0 ; i < split.length; i++){
                if(StringUtils.isNotBlank(split[i])){
                    sb.append(split[i].trim());
                    if(i < split.length - 1){
                        sb.append(",");
                    }
                }
            }
            return sb.toString();
        }
        return null;
    }


    private List<MenuCommon> getTrans(List<MenuCommon> list,String languageType){
        for (MenuCommon menuCommon:list) {
            menuCommon.setName(Utils.translation(menuCommon.getName()));
            if (CollectionUtils.isNotEmpty(menuCommon.getChildren())){
                menuCommon.setChildren(this.getTrans(menuCommon.getChildren(),languageType));
            }
        }
        return list;
    }

}
