package com.rondaful.cloud.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private String a;

    @RequestMapping("/map")
    public String test(){
        return a;
    }
}
