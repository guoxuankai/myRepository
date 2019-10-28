package com.brandslink.cloud.user.service.impl;

import com.brandslink.cloud.common.constant.UserConstant;
import com.brandslink.cloud.user.strategy.UserNameLoadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户名密码登录入口
 *
 * @ClassName UserEntityService
 * @Author tianye
 * @Date 2019/5/27 17:47
 * @Version 1.0
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserNameLoadFactory factory;

    @Autowired
    private HttpServletRequest request;

    /**
     * 在执行登录的过程中，这个方法将根据用户名去查找用户，如果用户不存在，则抛出UsernameNotFoundException异常，否则直接将查到的结果返回
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return factory.loadUserByUsername((String) request.getAttribute(UserConstant.PLATFORM_TYPE_REQUEST_HEADER_NAME), username);
    }

}
