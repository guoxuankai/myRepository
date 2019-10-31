package com.brandslink.cloud.common.security.handler;

import com.alibaba.fastjson.JSON;
import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.common.entity.UserEntity;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.utils.RedisUtils;
import com.brandslink.cloud.common.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登出成功Handler
 *
 * @ClassName MyLogoutSuccessHandler
 * @Author tianye
 * @Date 2019/6/20 9:45
 * @Version 1.0
 */
@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String token = request.getHeader(UserConstant.TOKEN_REQUEST_HEADER_NAME);
        if (StringUtils.isNotBlank(token)) {
            // 清除token以及用户信息
            Object obj = redisUtils.get(token);
            if (null != obj) {
                redisUtils.remove(((UserEntity) obj).getUsername() + token);
            }
            redisUtils.remove(token);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=utf-8");
        Utils.print(JSON.toJSONString(new Result(ResponseCodeEnum.RETURN_CODE_100200.getCode(), Utils.translation("注销成功!"))));
    }
}
