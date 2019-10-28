package com.rondaful.cloud.user.controller;


import com.rondaful.cloud.common.constant.UserConstants;
import com.rondaful.cloud.common.entity.user.UserAll;
import com.rondaful.cloud.common.utils.GetLoginUserInformationByToken;
import com.rondaful.cloud.common.utils.RedisUtils;
import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基础控制层
 * */
@RestController
@Api(description = "基础控制层")
public class BaseController {

    @Autowired
    public HttpServletRequest request;

    @Autowired
    public HttpServletResponse response;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    protected GetLoginUserInformationByToken userToken;


    protected void updateToken(Integer userId,Integer empowerId){

        String token=request.getHeader("token");
        UserAll userAll=userToken.getUserInfo();
        userAll.getUser().getBinds().get(0).getBindCode().add(empowerId.toString());
        this.redisUtils.remove(UserConstants.REDIS_USER_KEY_fix + token);
        this.redisUtils.set(UserConstants.REDIS_USER_KEY_fix + token, userAll, UserConstants.REDIS_USER_TOKEN_TIMEOUT);
    }


}
