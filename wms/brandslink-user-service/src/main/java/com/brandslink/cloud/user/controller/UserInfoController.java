package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.AddOrUpdateUserRequestDTO;
import com.brandslink.cloud.user.dto.request.GetUserListRequestDTO;
import com.brandslink.cloud.user.dto.response.CodeAndNameResponseDTO;
import com.brandslink.cloud.user.dto.response.RoleInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.UserInfoResponseDTO;
import com.brandslink.cloud.user.dto.response.UserWarehouseDetailResponseDTO;
import com.brandslink.cloud.user.entity.MenuInfo;
import com.brandslink.cloud.user.service.IUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @ClassName MenuInfoController
 * @Author tianye
 * @Date 2019/6/10 10:01
 * @Version 1.0
 */
@RestController
@Api("用户相关接口")
@RequestMapping(value = "/user")
public class UserInfoController {

    @Resource
    private IUserInfoService userInfoService;

    @ApiOperation("获取用户列表")
    @PostMapping("/getUserList")
    @RequestRequire(require = "page, row", parameter = GetUserListRequestDTO.class)
    public Page<UserInfoResponseDTO> getUserList(@RequestBody GetUserListRequestDTO request) {
        return userInfoService.getUserList(request);
    }

    @ApiOperation("新增用户")
    @PostMapping("/addUser")
    public void addUser(@RequestBody AddOrUpdateUserRequestDTO request) {
        userInfoService.addUser(request);
    }

    @ApiOperation("修改用户")
    @PostMapping("/UpdateUser")
    public void UpdateUser(@RequestBody AddOrUpdateUserRequestDTO request) {
        userInfoService.updateUser(request);
    }

    @ApiOperation("重置密码")
    @GetMapping("/reset")
    public void reset(@ApiParam(name = "id", value = "用户id", required = true) @RequestParam("id") Integer id) {
        userInfoService.reset(id);
    }

    @ApiOperation("绑定角色")
    @PostMapping("/bindingRoles")
    public void bindingRoles(@ApiParam(name = "id", value = "用户id", required = true) @RequestParam("id") Integer id,
                             @ApiParam(name = "roleIds", value = "角色id集合", required = true) @RequestParam("roleIds") List<Integer> roleIds,
                             @ApiParam(name = "warehouseList", value = "所属仓库信息", required = true)
                             @RequestBody List<RoleInfoResponseDTO.WarehouseDetail> warehouseList) {
        userInfoService.updateBindingRoles(id, warehouseList, roleIds);
    }

    @ApiOperation("绑定快捷菜单")
    @PostMapping("/bindingShortcutMenus")
    public void bindingShortcutMenus(@ApiParam(name = "shortcutMenus", value = "快捷菜单id集合", required = true) @RequestParam("shortcutMenus") List<Integer> shortcutMenus,
                                     @ApiParam(name = "flag", value = "所属平台 0：wms系统 1：oms系统", required = true) @RequestParam(value = "flag") Integer flag) {
        userInfoService.bindingShortcutMenus(shortcutMenus, flag);
    }

    @ApiOperation("获取当前用户绑定的快捷菜单")
    @GetMapping("/getShortcutMenus")
    public List<MenuInfo> getShortcutMenus(@ApiParam(name = "flag", value = "所属平台 0：wms系统 1：oms系统", required = true) @RequestParam(value = "flag") Integer flag) {
        return userInfoService.getShortcutMenus(flag);
    }

    @ApiOperation("修改密码")
    @PostMapping("/changes")
    public void updatePassword(@ApiParam(name = "id", value = "用户id", required = true) @RequestParam("id") Integer id,
                               @ApiParam(name = "oldPassword", value = "原始密码", required = true) @RequestParam("oldPassword") String oldPassword,
                               @ApiParam(name = "changePassword", value = "更改的密码", required = true) @RequestParam("changePassword") String changePassword) {
        userInfoService.updatePassword(id, oldPassword, changePassword);
    }

    @ApiOperation("账号启用")
    @PostMapping("/enabled")
    public void enabled(@ApiParam(name = "ids", value = "用户id集合", required = true) @RequestParam("ids") List<Integer> ids) {
        userInfoService.enabled(ids);
    }

    @ApiOperation("账号禁用")
    @PostMapping("/disabled")
    public void disabled(@ApiParam(name = "ids", value = "用户id集合", required = true) @RequestParam("ids") List<Integer> ids) {
        userInfoService.disabled(ids);
    }

    @ApiOperation("根据账号查询所属仓库信息")
    @GetMapping("/getWarehouseDetailByAccount")
    public List<UserWarehouseDetailResponseDTO> getWarehouseDetail(@ApiParam(name = "account", value = "账号", required = true) @RequestParam("account") String account) {
        return userInfoService.getWarehouseDetail(account);
    }

    @ApiOperation("获取当前用户所拥有的仓库信息")
    @GetMapping("/getWarehouseDetail")
    public List<CodeAndNameResponseDTO> getWarehouseDetail() {
        return userInfoService.getWarehouseDetailByUserId();
    }

    @ApiOperation("根据仓库code查询账户信息")
    @GetMapping("/getAccountNameListByWarehouseId")
    public List<Map<String, String>> getAccountNameListByWarehouseId(@ApiParam(name = "warehouseCode", value = "所属仓库id") @RequestParam(value = "warehouseCode", required = false) String warehouseCode) {
        return userInfoService.getAccountNameListByWarehouseId(warehouseCode);
    }

}
