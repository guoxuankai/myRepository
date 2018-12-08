package com.guoxunkai.springSecurity.ui;

import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(@PathVariable("keyword") String keyword) throws IOException, ServletException {

        return keyword;

    }

}
