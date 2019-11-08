package com.brandslink.cloud.common.security.handler;

import com.brandslink.cloud.common.entity.Result;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.utils.Utils;
import net.sf.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败Handler
 *
 * @ClassName MyAuthenticationSuccessHandler
 * @Author tianye
 * @Date 2019/5/29 10:31
 * @Version 1.0
 */
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String result;
        if (exception instanceof BadCredentialsException) {
            result = "密码不正确，请重新登录!";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            result = exception.getMessage();
        } else if (exception instanceof DisabledException) {
            result = "账号不可用，请联系管理员启用!";
        } else {
            result = "账号或密码不正确，请重新登录!";
        }
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=utf-8");
        Utils.print(JSONObject.fromObject(new Result(ResponseCodeEnum.RETURN_CODE_100001.getCode(), Utils.translation(result))));
    }
}
