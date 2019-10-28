package com.rondaful.cloud.gateway.controller;

import com.rondaful.cloud.gateway.enums.ResponseCodeEnum;
import com.rondaful.cloud.gateway.exception.GlobalException;
import com.rondaful.cloud.gateway.remote.RemoteUserService;
import com.rondaful.cloud.gateway.utils.RemoteUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;


@Api(tags = "接口调试授权")
@RestController
//@CrossOrigin
public class AuthController {

    @Value("${gateway.username}")
    private String username;

    @Value("${gateway.password}")
    private String debug;

    @Autowired
    private HttpServletRequest request;

    //@Resource
    //private RedisUtils redisUtils;

    @Autowired
    private RemoteUserService remoteUserService;



    @PostMapping(value = "/loginForSupplier")
    @ApiOperation(value="供应商登录调试授权", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name="username",value="账号",dataType="string", paramType = "query", required=true),
            @ApiImplicitParam(name="password",value="登录密码",dataType="string", paramType = "query", required=true)})
    public void loginForSupplier(String username, String password) {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            RemoteUtil.invoke(remoteUserService.loginForSupplier(username, password, debug));
            if (!"100200".equals(RemoteUtil.getErrorCode())) throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
            Map m = RemoteUtil.getMap();
            if (m == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
            String token = (String) m.get("token");
            if (StringUtils.isNotBlank(token)) {
                HttpSession session = request.getSession();
                session.setAttribute("token", token);
            } else {
                throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }


    @PostMapping(value = "/loginForSeller")
    @ApiOperation(value="卖家登录调试授权", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name="username",value="账号",dataType="string", paramType = "query", required=true),
            @ApiImplicitParam(name="password",value="登录密码",dataType="string", paramType = "query", required=true)})
    public void loginForSeller(String username, String password) {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            RemoteUtil.invoke(remoteUserService.loginForSeller(username, password));
            if (!"100200".equals(RemoteUtil.getErrorCode())) throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
            Map m = RemoteUtil.getMap();
            if (m == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
            String token = (String) m.get("token");
            if (StringUtils.isNotBlank(token)) {
                HttpSession session = request.getSession();
                session.setAttribute("token", token);
            } else {
                throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }


    @PostMapping(value = "/loginForCms")
    @ApiOperation(value="后台管理登录调试授权", notes="")
    @ApiImplicitParams({
            @ApiImplicitParam(name="username",value="账号",dataType="string", paramType = "query", required=true),
            @ApiImplicitParam(name="password",value="登录密码",dataType="string", paramType = "query", required=true)})
    public void manageUserLogin(String username, String password) {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            //RemoteUtil.invoke(remoteUserService.loginForCms(username, password));
            RemoteUtil.invoke(remoteUserService.login(username, password,2));
            if (!"100200".equals(RemoteUtil.getErrorCode())) throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
            Map m = RemoteUtil.getMap();
            if (m == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
            String token = (String) m.get("token");
            if (StringUtils.isNotBlank(token)) {
                HttpSession session = request.getSession();
                session.setAttribute("token", token);
            } else {
                throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
            }
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }


    @PostMapping(value = "/login")
    @ApiOperation(value="登录管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name="username",value="账号",dataType="string", paramType = "query", required=true),
            @ApiImplicitParam(name="password",value="登录密码",dataType="string", paramType = "query", required=true),
            @ApiImplicitParam(name="type",value="登录平台类型 0:供应商  1 卖家  2  后台",dataType="Integer", paramType = "query", required=true)
    })
    public Object login(String username, String password,Integer type) {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            RemoteUtil.invoke(remoteUserService.login(username, password,type));
            if (!"100200".equals(RemoteUtil.getErrorCode())) throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
            Map m = RemoteUtil.getMap();
            if (m == null) throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
            String token = (String) m.get("token");
            if (StringUtils.isNotBlank(token)) {
                HttpSession session = request.getSession();
                session.setAttribute("token", token);
            } else {
                throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
            }
            return m;
        } else {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
    }

}
