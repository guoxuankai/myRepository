package com.example.demo.service;

import com.example.demo.config.MyUser;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("用户名："+username);
        if(true){
            throw new UsernameNotFoundException("用户名不存在");
        }
        User user = new User("root", null, AuthorityUtils.commaSeparatedStringToAuthorityList("xxx"));
        return user;
    }
}
