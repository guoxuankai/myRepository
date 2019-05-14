package com.example.demo.service;


import com.example.demo.entity.User;

public interface UserService {

    User findByUsername(User user);

    User findUserById(String userId);
}
