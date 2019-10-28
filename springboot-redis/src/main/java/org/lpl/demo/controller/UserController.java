package org.lpl.demo.controller;

import org.lpl.demo.bean.User;
import org.lpl.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author guoxuankai
 * @date 2019/8/26 10:11
 */
@RestController
public class UserController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/test")
    public String test() {
        User jack = new User(1L, "jack", 1, 1);
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("user1",jack);
        redisUtil.set("map_1", objectObjectHashMap);
        return "success";
    }





}

