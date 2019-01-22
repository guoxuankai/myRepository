package com.example.demo.ui;

import com.example.demo.entity.Employee;
import com.example.demo.enums.ErrorEnum;
import com.example.demo.result.LoginResultData;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;


    @PostMapping(value = "/login")
    public LoginResultData login(@RequestBody Employee employee) {
        return userService.validateUser(employee);
    }


    @PostMapping(value = "/register")
    public ErrorEnum register(@RequestBody Employee employee) {
        try {
            userService.register(employee);
            return ErrorEnum.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorEnum.FAILED;
        }
    }


}
