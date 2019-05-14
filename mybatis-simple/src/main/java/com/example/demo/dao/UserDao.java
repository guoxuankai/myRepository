package com.example.demo.dao;

import com.example.demo.entity.User;


public interface UserDao {

    User getUserInfo(int id);

    int updateUserName(String newName, int id);

    int insertUser(int id, String name, int age, String addr);
}
