package com.brandslink.cloud.common.security.handler;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.CustomerDetails;
import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.common.entity.UserDetailInfo;
import com.brandslink.cloud.common.entity.UserEntity;
import com.brandslink.cloud.common.entity.response.LoginSuccessResponseDTO;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.utils.MD5;
import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.common.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 登录成功Handler
 *
 * @ClassName MyAuthenticationSuccessHandler
 * @Author tianye
 * @Date 2019/5/29 10:31
 * @Version 1.0
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 平台登录类型
        String platformType = request.getParameter(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME);
        // 使用账号+加密的密码+时间戳通过MD5加密生成token
        StringBuilder tokenDetail = new StringBuilder(UserConstant.REDIS_USER_TOKEN_KEY_FIX);
        UserEntity entity = (UserEntity) authentication.getPrincipal();
        String username = entity.getUsername();
        List<String> details = Arrays.asList(username, entity.getPassword());
        String token = MD5.md5Password(tokenDetail.append(StringUtils.join("-", details)).append("-").append(System.currentTimeMillis()).toString());
        // 存入Redis，1小时有效
        entity.setPassword(null);
        redisUtils.set(token, entity, UserConstant.REDIS_USER_TOKEN_KEY_TIMEOUT);
        LoginSuccessResponseDTO result = new LoginSuccessResponseDTO();
        if (StringUtils.equals(platformType, "0")) {
            UserDetailInfo userDetailInfo = (UserDetailInfo) redisUtils.get(UserConstant.PLATFORM_TYPE_ACCOUNT_REDIS_USER_TOKEN_KEY_FIX_WMS + username);
            redisUtils.set(username + token, userDetailInfo, UserConstant.REDIS_USER_TOKEN_KEY_TIMEOUT);
            BeanUtils.copyProperties(userDetailInfo, result);
        } else if (StringUtils.equals(platformType, "1")) {
            CustomerDetails customerUserInfo = (CustomerDetails) redisUtils.get(UserConstant.PLATFORM_TYPE_ACCOUNT_REDIS_USER_TOKEN_KEY_FIX_OMS + username);
            redisUtils.set(username + token, customerUserInfo, UserConstant.REDIS_USER_TOKEN_KEY_TIMEOUT);
            BeanUtils.copyProperties(customerUserInfo.getCustomerUserDetailInfo(), result);
        }else if (StringUtils.equals(platformType,"2")){
            result.setId(1);
            result.setName(entity.getUsername());
            result.setAccount("admin");
        }
        result.setToken(token);
        result.setPlatformType(platformType);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=utf-8");
        Utils.print(JSON.toJSONString(new Result(ResponseCodeEnum.RETURN_CODE_100200, result)));
    }
}
