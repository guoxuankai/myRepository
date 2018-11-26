package com.example.demo.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        System.out.println("待加密的密码---"+charSequence.toString());
        return charSequence.toString()+"abc";
    }

    @Override
    public boolean matches(CharSequence charSequence, String encodedPassword) {
        System.out.println("前台传来的密码---"+charSequence.toString());
        System.out.println("数据库中的密码---"+encodedPassword);
        String encode = this.encode(charSequence);
        return encodedPassword.equals(encode);
    }
}
