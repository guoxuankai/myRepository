package com.example.demo.ui;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response){
        return "/login.html";
    }

    @GetMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response){
        return "/index.html";
    }

}
