package com.example.demo.ui;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/a.html");
//        requestDispatcher.forward(request,response);
        response.sendRedirect("/a.html");
        return "/b.html";

    }

}
