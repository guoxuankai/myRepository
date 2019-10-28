package com.brandslink.cloud.finance.controller;

import com.brandslink.cloud.common.controller.BaseController;
import com.brandslink.cloud.finance.pojo.entity.SysAccount;
import com.brandslink.cloud.finance.service.SysAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yangzefei
 * @Classname SysAccountController
 * @Description 平台资金账号
 * @Date 2019/9/3 13:49
 */
@Api(value = "平台资金账号")
@RestController
@RequestMapping("/sysAccount")
public class SysAccountController extends BaseController {

    @Resource
    private SysAccountService sysAccountService;

    @ApiOperation(value = "获取资金账号信息")
    @GetMapping("/get")
    public SysAccount get(@ApiParam(value = "资金账号ID",required = true)@RequestParam("sysId") Integer sysId){
        return sysAccountService.get(sysId);
    }
}
