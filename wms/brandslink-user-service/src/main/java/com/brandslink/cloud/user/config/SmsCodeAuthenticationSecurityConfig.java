package com.brandslink.cloud.user.config;

import com.brandslink.cloud.common.security.handler.MyAuthenticationFailureHandler;
import com.brandslink.cloud.common.security.handler.MyAuthenticationSuccessHandler;
import com.brandslink.cloud.common.security.sms.SmsCodeAuthenticationFilter;
import com.brandslink.cloud.common.security.sms.SmsCodeAuthenticationProvider;
import com.brandslink.cloud.user.service.impl.SmsUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 手机号短信登录配置
 *
 * @ClassName SmsCodeAuthenticationSecurityConfig
 * @Author tianye
 * @Date 2019/9/2 17:38
 * @Version 1.0
 */
@Component
public class SmsCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private SmsUserDetailsServiceImpl smsUserDetailsService;
    @Autowired
    private MyAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private MyAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Autowired
    private SmsCodeAuthenticationProvider smsCodeAuthenticationProvider;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsCodeAuthenticationFilter smsCodeAuthenticationFilter = new SmsCodeAuthenticationFilter();
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);

        smsCodeAuthenticationProvider.setUserDetailsService(smsUserDetailsService);

        http.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
