package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.result.LoginResultData;

import javax.transaction.Transactional;


@Transactional
public interface UserService {

    LoginResultData validateUser(Employee employee);

    void register(Employee employee);


}
