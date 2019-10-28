package com.brandslink.cloud.user.controller;

import com.brandslink.cloud.common.annotation.RequestRequire;
import com.brandslink.cloud.common.entity.Page;
import com.brandslink.cloud.user.dto.request.AddOrUpdateCustomerAccountRequestDTO;
import com.brandslink.cloud.user.dto.request.CustomerAccountRequestDTO;
import com.brandslink.cloud.user.dto.response.CustomerUserInfoResponseDTO;
import com.brandslink.cloud.user.service.ICustomerAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author zhaojiaxing
 * @version 1.0
 * @description: 账号管理
 * @date 2019/9/4 10:37
 */
@RestController
@Api(tags = "客户端账号管理相关接口")
@RequestMapping(value = "/customer/account")
public class CustomerAccountController {

    @Resource
    private ICustomerAccountService customerAccountService;

    @ApiOperation(value = "根据用户名获取获取仓库",notes = "根据用户名获取获取仓库")
    @PostMapping("/getWarehouseList")
    @RequestRequire(require = "account", parameter = String.class)
    public List<Map<String, String>> getWarehouseList(@RequestParam("account") String account) {
        return customerAccountService.getWarehouseList(account);
    }

    @ApiOperation(value = "查询账号信息",notes = "账号信息查询（支持分页）")
    @PostMapping("/getAccountList")
    @RequestRequire(require = "page,row", parameter = CustomerAccountRequestDTO.class)
    public Page<CustomerUserInfoResponseDTO> getAccountList(@RequestBody CustomerAccountRequestDTO request) {
        return customerAccountService.getAccountList(request);
    }

    @ApiOperation(value = "新添账号信息",notes = "账号新添（适用于主账号新添子账号）")
    @PostMapping("/addAccount")
    @RequestRequire(require = "password", parameter = AddOrUpdateCustomerAccountRequestDTO.class)
    public void addAccount(@Valid @RequestBody AddOrUpdateCustomerAccountRequestDTO request){
        customerAccountService.addAccount(request);
    }

    @ApiOperation(value = "修改账号信息",notes = "账号信息修改")
    @PostMapping("/updateAccount")
    @RequestRequire(require = "id,account,name", parameter = AddOrUpdateCustomerAccountRequestDTO.class)
    public void updateAccount(@Valid @RequestBody AddOrUpdateCustomerAccountRequestDTO request){
        customerAccountService.updateAccount(request);
    }

    @ApiOperation(value = "修改账号状态",notes = "修改账号状态,启用：1，禁用：0")
    @PostMapping("/updateAccountStatus")
    public void updateAccountStatus(@RequestParam(value = "userId") Integer userId,
                                      @RequestParam(value = "status") Integer status){
        customerAccountService.updateAccountStatus(userId, status);
    }

    @ApiOperation(value = "绑定角色",notes = "绑定角色")
    @PostMapping("/bindAccountRole")
    public void bindAccountRole(@RequestParam(value = "userId") Integer userId,
                                  @RequestBody List<Integer> roleIds){
        customerAccountService.bindAccountRole(userId, roleIds);
    }

    @ApiOperation(value = "重置密码",notes = "重置密码")
    @PostMapping("/restorePassword")
    public void restorePassword(@RequestParam(value = "userId") Integer userId){
        customerAccountService.restorePassword(userId);
    }

    @ApiOperation(value = "首页修改密码",notes = "首页修改密码")
    @PostMapping("/updatePassword")
    @RequestRequire(require = "account,oldPassword,newPassword", parameter = String.class)
    public void restorePassword(@RequestParam(value = "account") String account,
                                  @RequestParam(value = "oldPassword") String oldPassword,
                                  @RequestParam(value = "newPassword") String newPassword){
        customerAccountService.updatePassword(account, oldPassword, newPassword);
    }

}
