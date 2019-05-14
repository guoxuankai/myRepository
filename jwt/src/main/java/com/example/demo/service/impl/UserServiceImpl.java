package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {


    @Override
    public User findByUsername(User user) {
        if (user.getUsername().equals("root")) {
            return new User("123", "root", "root");
        }
        return null;
    }

    @Override
    public User findUserById(String userId) {
        if (userId.equals("123")) {
            return new User("123", "root", "root");
        }
        return null;
    }
}
