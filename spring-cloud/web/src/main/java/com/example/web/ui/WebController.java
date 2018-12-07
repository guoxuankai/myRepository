package com.example.web.ui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/")
    public String index() {
        return "hello index";
    }

    @GetMapping("/login")
    public String login() {
        return "hello login";
    }

}
