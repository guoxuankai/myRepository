package com.guoxunkai.springSecurity.ui;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
public class SMSCodeController {

    @RequestMapping("/code/sms")
    public void hello(@RequestParam("tel")String tel){
        System.out.println("生成随机验证码。。4321");
        System.out.println("将验证码存入session当中");
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        request.getSession().setAttribute("SMSCheckCode",4321);
        System.out.println("向手机号"+tel+"发送短信验证码");
    }

}
