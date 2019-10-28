package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.GetRoleListRequestDTO;
import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.RoleListResponseDTO;
import com.brandslink.cloud.user.service.IRoleInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 角色
 *
 * @ClassName MenuInfoController
 * @Author tianye
 * @Date 2019/6/10 10:01
 * @Version 1.0
 */
@RestController
@Api("角色相关接口")
@RequestMapping(value = "/role")
public class RoleInfoController {

    @Resource
    private IRoleInfoService roleInfoService;

    @ApiOperation("获取角色列表")
    @PostMapping("/getRoleList")
    @RequestRequire(require = "page, row", parameter = GetRoleListRequestDTO.class)
    public Page<RoleInfoResponseDTO> getRoleList(@RequestBody GetRoleListRequestDTO request) {
        return roleInfoService.getRoleList(request);
    }

    @ApiOperation("新增角色")
    @PostMapping("/addRole")
    public void addRole(@ApiParam(name = "roleName", value = "角色名称", required = true) @RequestParam("roleName") String roleName,
                        @ApiParam(name = "warehouseName", value = "所属仓库名称", required = true) @RequestParam("warehouseName") String warehouseName,
                        @ApiParam(name = "warehouseCode", value = "所属仓库代码", required = true) @RequestParam("warehouseCode") String warehouseCode) {
        roleInfoService.addRole(roleName, warehouseName, warehouseCode);
    }

    @ApiOperation("修改角色")
    @PostMapping("/UpdateRole")
    public void UpdateRole(@ApiParam(name = "roleName", value = "角色名称", required = true) @RequestParam("roleName") String roleName,
                           @ApiParam(name = "warehouseName", value = "所属仓库名称", required = true) @RequestParam("warehouseName") String warehouseName,
                           @ApiParam(name = "warehouseCode", value = "所属仓库代码", required = true) @RequestParam("warehouseCode") String warehouseCode,
                           @ApiParam(name = "id", value = "角色id", required = true) @RequestParam(value = "id") String id) {
        roleInfoService.updateRole(id, roleName, warehouseName, warehouseCode);
    }

    @ApiOperation("设置权限")
    @PostMapping("/addPermission")
    public void addPermission(@ApiParam(name = "id", value = "角色id", required = true) @RequestParam("id") Integer id,
                              @ApiParam(name = "menuIds", value = "菜单id集合", required = true) @RequestParam("menuIds") List<Integer> menuIds,
                              @ApiParam(name = "flag", value = "请求标识 0：PC端 1：PDA端") @RequestParam(value = "flag") Integer flag) {
        roleInfoService.addPermission(id, menuIds, flag);
    }

    @ApiOperation("获取当前角色所拥有的权限")
    @GetMapping("/getPermission")
    public Map<String, Object> getPermission(@ApiParam(name = "id", value = "角色id", required = true) @RequestParam("id") Integer id,
                                             @ApiParam(name = "flag", value = "请求标识 0：PC端 1：PDA端") @RequestParam(value = "flag") Integer flag) {
        return roleInfoService.getPermission(id, flag);
    }

    @ApiOperation("根据所属仓库查询对应的角色列表")
    @GetMapping("/getRoleListByWarehouseCode")
    public List<RoleListResponseDTO> getRoleListByWarehouseCode() {
        return roleInfoService.getRoleListByWarehouseCode();
    }

    @ApiOperation("删除角色")
    @GetMapping("/deleteRole")
    public void deleteRole(@ApiParam(name = "id", value = "角色id", required = true) @RequestParam("id") Integer id) {
        roleInfoService.deleteRole(id);
    }

    @ApiOperation("通过请求url查询所需要的角色列表 -> 内部调用")
    @PostMapping("/getMenusByRequestUrl")
    public String getMenusByRequestUrl(@RequestParam("requestUrl") String requestUrl, @RequestParam("platformType") String platformType) {
        return roleInfoService.getMenusByRequestUrl(requestUrl, platformType);
    }

    @ApiOperation("通过用户角色id查询所能访问的所有url -> 内部调用")
    @PostMapping("/getMenusByRoleList")
    public String getMenusByRoleList(@RequestParam("authority") String authority, @RequestParam("platformType") String platformType) {
        return roleInfoService.getMenusByRoleList(authority,platformType);
    }
}
