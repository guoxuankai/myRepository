package com.rondaful.cloud.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.entity.user.UserDTO;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.model.PageDTO;
import com.rondaful.cloud.user.model.dto.menu.TreeDTO;
import com.rondaful.cloud.user.model.dto.role.QueryRolePageDTO;
import com.rondaful.cloud.user.model.dto.role.RoleDTO;
import com.rondaful.cloud.user.model.request.role.QueryRoleReq;
import com.rondaful.cloud.user.service.INewRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
@Api(description = "4.30期角色相关接口")
@RestController
@RequestMapping("new/role/")
public class NewRoleController extends BaseController{

    @Autowired
    private INewRoleService roleService;

    @ApiOperation(value = "角色分页查询接口")
    @PostMapping("getPage")
    public PageDTO<RoleDTO> getPage(QueryRoleReq req){
        QueryRolePageDTO dto=new QueryRolePageDTO();
        BeanUtils.copyProperties(req,dto);
        dto.setPlatform(super.userToken.getUserDTO().getPlatformType());
        dto.setAttribution(super.userToken.getUserDTO().getTopUserId());
        if (UserEnum.platformType.CMS.getPlatformType().equals(dto.getPlatform())){
            if (req.getPlatformType()!=null&&req.getUserId()!=null){
                    dto.setAttribution(req.getUserId());
                    dto.setPlatform(req.getPlatformType());
            }else {
                dto.setAttribution(null);
            }
        }
        return this.roleService.getPage(dto);
    }

    @AspectContrLog(descrption = "修改角色", actionType = SysLogActionType.UDPATE)
    @ApiOperation(value = "修改角色名及备注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "Integer",paramType = "query",required = true),
            @ApiImplicitParam(name = "roleName", value = "角色名", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "角色备注", dataType = "String",paramType = "query")
    })
    @PostMapping("updateRole")
    public Integer updateRole(Integer roleId,String roleName,String remark){
        if (StringUtils.isEmpty(roleName)||"null".equals(roleName)){
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100435.getCode(),"error.params");
        }
        return this.roleService.updateRoleName(roleId,roleName,remark,super.userToken.getUserDTO().getLoginName());
    }

    @AspectContrLog(descrption = "删除角色", actionType = SysLogActionType.DELETE)
    @ApiOperation(value = "删除角色")
    @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "Integer",paramType = "query",required = true)
    @PostMapping("delete")
    public Integer delete(Integer roleId){
        return this.roleService.delete(roleId);
    }


    @ApiOperation(value = "修改功能权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menus", value = "功能权限数组json字符串", dataType = "String",paramType = "query",required = true),
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "Integer",paramType = "query",required = true)
    })
    @PostMapping("updateMenu")
    public Integer updateMenu(String menus,Integer roleId){
        if (roleId==null){
            return 0;
        }
        List<Integer> list=new ArrayList<>();
        if (StringUtils.isNotEmpty(menus)){
            list= JSONArray.parseArray(menus,Integer.class);
        }
        return this.roleService.updateRoleMenu(list,roleId);
    }

    @AspectContrLog(descrption = "新增角色", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "新增角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menus", value = "功能权限数组json字符串", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "roleName", value = "角色名", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "角色备注", dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "平台类型", dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "所属账号id", dataType = "Integer",paramType = "query")
    })
    @PostMapping("add")
    public Integer add(String roleName,String remark,String menus,Integer platform,Integer userId){
        UserDTO baseUser=super.userToken.getUserDTO();
        RoleDTO dto=new RoleDTO();
        dto.setRoleName(roleName);
        dto.setRemark(remark);
        dto.setCreateBy(baseUser.getLoginName());
        dto.setPlatform(baseUser.getPlatformType());
        dto.setAttribution(baseUser.getTopUserId());
        if (UserEnum.platformType.CMS.getPlatformType().equals(dto.getPlatform())){
            if (platform!=null&&userId!=null){
                dto.setAttribution(userId);
                dto.setPlatform(platform);
            }
        }
        List<Integer> list=new ArrayList<>();
        if (StringUtils.isNotEmpty(menus)){
            list= JSONArray.parseArray(menus,Integer.class);
        }
        dto.setList(list);
        return this.roleService.add(dto);
    }

    @ApiOperation(value = "获取角色对应的菜单权限")
    @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "Integer",paramType = "query",required = true)
    @GetMapping("getMenu")
    public List<Integer> getMenu(Integer roleId){
        return this.roleService.getMenu(roleId);
    }


    @ApiOperation(value = "获取角色树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platformType", value = "平台类型", dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "Integer",paramType = "query")
    })
    @PostMapping("getTree")
    public List<TreeDTO> getTree(Integer platformType,Integer userId){
        UserDTO baseUser=super.userToken.getUserDTO();
        if (platformType ==null ||userId==null){
            platformType=baseUser.getPlatformType();
            if (UserEnum.platformType.CMS.getPlatformType().equals(platformType)){
                userId=null;
            }else {
                userId=baseUser.getTopUserId();
            }
        }
        return this.roleService.getTree(platformType,userId);
    }


}
