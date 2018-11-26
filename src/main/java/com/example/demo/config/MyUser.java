package com.example.demo.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class MyUser extends User {


    public MyUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    //可以自定义账户被锁定的逻辑
//    @Override
//    public boolean isAccountNonLocked() {
//        return false;
//    }
}
