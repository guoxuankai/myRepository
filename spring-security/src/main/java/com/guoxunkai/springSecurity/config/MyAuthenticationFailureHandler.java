package com.guoxunkai.springSecurity.config;

import com.guoxunkai.springSecurity.enums.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
//        String s = objectMapper.writeValueAsString(e);
        String localizedMessage = e.getLocalizedMessage();
        System.out.println("登陆失败原因："+localizedMessage);
        LoginResponse loginResponse = new LoginResponse(1002, "登陆失败");
        String s = objectMapper.writeValueAsString(loginResponse);
        httpServletResponse.getWriter().write(s);
    }
}
