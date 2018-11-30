package com.guoxunkai.springSecurity.filter;

import com.guoxunkai.springSecurity.config.MyAuthenticationFailureHandler;
import com.guoxunkai.springSecurity.exception.ValidateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ValidateCodeFilter extends OncePerRequestFilter {

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals("/security/login")){
            String frontCode = request.getParameter("imgCode");
            //将前台传来的验证码转换成大写
            String s = frontCode.toUpperCase();
            String checkcode = (String)request.getSession().getAttribute("checkcode");
            if(!checkcode.equals(s)){
                myAuthenticationFailureHandler.onAuthenticationFailure(request,response,new ValidateException("验证码错误"));
                return;
            }
        }
        filterChain.doFilter(request,response);
    }
}
