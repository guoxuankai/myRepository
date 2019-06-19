package com.example.demo.controller;


import com.example.demo.annotation.Log;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {


    @RequestMapping("/hello")
    //对应的自定义注解，当方法上写这个注解时，就会进入切面类中
    @Log(title = "模块A", action = "hello")
    public String sayHello() {
        System.out.println("hello...");
        return "hello";
    }


}
