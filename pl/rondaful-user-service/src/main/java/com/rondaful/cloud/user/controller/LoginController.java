package com.rondaful.cloud.user.controller;

import com.rondaful.cloud.common.aspect.AspectContrLog;
import com.rondaful.cloud.common.enums.SysLogActionType;
import com.rondaful.cloud.common.enums.UserEnum;
import com.rondaful.cloud.common.exception.GlobalException;
import com.rondaful.cloud.user.constants.UserConstants;
import com.rondaful.cloud.user.enums.ResponseCodeEnum;
import com.rondaful.cloud.user.service.ILoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/4/26
 * @Description:
 */
@Api(description = "登录登出接口",hidden = true)
@RestController
@RequestMapping("login/")
public class LoginController extends BaseController{

    @Autowired
    private ILoginService loginService;


    @AspectContrLog(descrption = "通用方式登录", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "用户登录")
    @PostMapping("home")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名:支持邮箱与手机登陆", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "passWord", value = "用户名:支持邮箱与手机登陆", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "type", value = "登录类型", dataType = "string", paramType = "query", required = true)
    })
    public Map<String,Object> login(String userName, String passWord, Integer type){
        Map<String,Object> map=this.loginService.login(userName,passWord,type,super.response);
        return map;
    }


    @AspectContrLog(descrption = "卖家登录",actionType = SysLogActionType.QUERY)
    @ApiOperation(value ="卖家登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名:支持邮箱与手机登陆", dataType = "string",paramType = "query",required = true),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "string",paramType = "query",required = true)
    })
    @PostMapping(value = "sellerLogin")
    public Map<String,Object> sellerLogin(String username, String password, HttpServletResponse response ) {
        return this.loginService.login(username,password, UserEnum.platformType.SELLER.getPlatformType(),response);
    }


    @AspectContrLog(descrption = "供应商登录", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "供应商登录")
    @ApiImplicitParams({ @ApiImplicitParam(name = "username", value = "用户名 支持手机，邮箱登陆", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "string", paramType = "query", required = true)
    })
    @PostMapping(value = "supplierLogin")
    public Map<String, Object> supplierLogin( String username, String password, HttpServletResponse response) {
        return this.loginService.login(username,password, UserEnum.platformType.SUPPLIER.getPlatformType(),response);
    }


    @AspectContrLog(descrption = "管理后台登录", actionType = SysLogActionType.ADD)
    @ApiOperation(value = "管理后台登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名:支持邮箱与手机登陆", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "string", paramType = "query", required = true)
    })
    @PostMapping(value = "manageUserLogin")
    public Map<String, Object> manageUserLogin(String username, String password, HttpServletResponse response) {
        return this.loginService.login(username,password, UserEnum.platformType.CMS.getPlatformType(),response);
    }




}
