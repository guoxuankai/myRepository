package com.brandslink.cloud.user.strategy;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 登录策略接口
 *
 * @ClassName IUserNameLoadStrategy
 * @Author tianye
 * @Date 2019/8/29 10:46
 * @Version 1.0
 */
public interface IUserNameLoadStrategy {

    /**
     * 用户名密码登录
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * 手机号短信验证码登录
     *
     * @param mobile
     * @return
     * @throws UsernameNotFoundException
     */
    UserDetails loadUserByMobile(String mobile) throws UsernameNotFoundException;

}
