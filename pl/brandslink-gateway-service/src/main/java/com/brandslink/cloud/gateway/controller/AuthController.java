package com.brandslink.cloud.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.gateway.entity.LoginSuccessResponseDTO;
import com.brandslink.cloud.gateway.entity.Massage;
import com.brandslink.cloud.gateway.enums.ResponseCodeEnum;
import com.brandslink.cloud.gateway.exception.GlobalException;
import com.brandslink.cloud.gateway.remote.RemoteUserService;
import com.brandslink.cloud.gateway.utils.RemoteUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Api(tags = "接口调试授权")
@RestController
public class AuthController {

    @Resource
    private HttpServletRequest request;

    @Resource
    private RemoteUserService remoteUserService;


    @PostMapping(value = "/loginForPC")
    @ApiOperation("PC登录调试授权 最高权限用户： admin/123456")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "账号", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "登录密码", dataType = "string", paramType = "query", required = true)})
    public void loginForPC(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100004);
        }
        String newPassword = password + "-" + username;
        Object login = remoteUserService.loginForPC(username, new BASE64Encoder().encode(newPassword.getBytes()));
        commonVerify(login);
    }

    @PostMapping(value = "/loginForPDA")
    @ApiOperation("PDA登录调试授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "账号", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "password", value = "登录密码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "warehouseCode", value = "仓库代码", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "warehouseName", value = "仓库名称", dataType = "string", paramType = "query", required = true)})
    public void loginForPDA(String username, String password, String warehouseCode, String warehouseName) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100004);
        }
        if (StringUtils.isBlank(warehouseCode) || StringUtils.isBlank(warehouseName)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100005);
        }
        String newPassword = password + "-" + username;
        Object login = remoteUserService.loginForPDA(username, new BASE64Encoder().encode(newPassword.getBytes()), warehouseCode, warehouseName);
        commonVerify(login);
    }

    @PostMapping(value = "/selectWarehouseDetailsByAccount")
    @ApiOperation("根据账号获取所属仓库信息")
    @ApiImplicitParams(@ApiImplicitParam(name = "account", value = "账号", dataType = "string", paramType = "query", required = true))
    public String selectWarehouseDetailsByAccount(String account) {
        if (StringUtils.isBlank(account)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100002);
        }
        Object details = remoteUserService.selectWarehouseDetailsByAccount(account);
        Massage massage = JSON.parseObject(JSON.toJSONString(details), Massage.class);
        if (!massage.isSuccess()) {
            throw new GlobalException(massage.getErrorCode(), massage.getMsg());
        }
        return JSON.toJSONString(massage.getData());
    }

    private void commonVerify(Object object) {
        if (null == object) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500);
        }
        Massage massage = JSON.parseObject(JSON.toJSONString(object), Massage.class);
        if (!massage.isSuccess()) {
            throw new GlobalException(massage.getErrorCode(), massage.getMsg());
        }
        LoginSuccessResponseDTO response = JSON.parseObject(JSON.toJSONString(massage.getData()), LoginSuccessResponseDTO.class);
        String token = response.getToken();
        if (StringUtils.isNotBlank(token)) {
            HttpSession session = request.getSession();
            session.setAttribute("token", token);
        } else {
            throw new GlobalException(RemoteUtil.getErrorCode(), RemoteUtil.getMsg());
        }
    }

}
