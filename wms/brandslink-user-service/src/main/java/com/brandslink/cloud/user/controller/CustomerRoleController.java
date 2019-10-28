package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.CustomerRoleRequestDTO;
import com.brandslink.cloud.user.dto.response.CustomerRoleInfoResponseDTO;
import com.brandslink.cloud.user.service.ICustomerRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 客户端角色管理相关接口
 * @date 2019/9/6 9:27
 */
@RestController
@Api(tags = "客户端角色管理相关接口")
@RequestMapping(value = "/customer/role")
public class CustomerRoleController {

    @Resource
    private ICustomerRoleService customerRoleService;

    @ApiOperation(value = "角色查询", notes = "角色查询")
    @PostMapping("/getRoleList")
    @RequestRequire(require = "page,row", parameter = CustomerRoleRequestDTO.class)
    public Page<CustomerRoleInfoResponseDTO> getRoleList(@RequestBody CustomerRoleRequestDTO request) {
        return customerRoleService.getRoleList(request);
    }

    @ApiOperation("新增角色")
    @PostMapping("/addRole")
    public void addRole(@ApiParam(name = "roleName", value = "角色名称", required = true) @RequestParam("roleName") String roleName,
                        @ApiParam(name = "roleDescription", value = "角色描述", required = true) @RequestParam("roleDescription") String roleDescription,
                        @ApiParam(name = "menuIds", value = "菜单id集合", required = true) @RequestParam("menuIds") List<Integer> menuIds) {
        customerRoleService.addRole(roleName, roleDescription, menuIds);
    }

    @ApiOperation("设置权限")
    @PostMapping("/addPermission")
    public void addPermission(@ApiParam(name = "id", value = "角色id", required = true) @RequestParam("id") Integer id,
                              @ApiParam(name = "roleDescription", value = "角色描述", required = true) @RequestParam("roleDescription") String roleDescription,
                              @ApiParam(name = "menuIds", value = "菜单id集合", required = true) @RequestParam("menuIds") List<Integer> menuIds) {
        customerRoleService.addPermission(id, roleDescription, menuIds);
    }

    @ApiOperation("获取当前角色所拥有的权限")
    @GetMapping("/getPermission")
    public Map<String, Object> getPermission(@ApiParam(name = "id", value = "角色id", required = true) @RequestParam("id") Integer id) {
        return customerRoleService.getPermission(id);
    }

}
