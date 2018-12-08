package com.example.zuul.ui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {

    @GetMapping("/gatewayTest")
    public String gatewayTest(){
        return "gateway test";
    }
}
