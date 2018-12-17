package com.guoxunkai.springSecurity.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
//如果定义了自定义验证，此代码将不会执行
@Component
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("用户名："+username);
//        if(true){
//            throw new UsernameNotFoundException("用户名不存在");
//        }
        User user = new User("root", "123abc", AuthorityUtils.commaSeparatedStringToAuthorityList("xxx"));
        return user;
    }
}
