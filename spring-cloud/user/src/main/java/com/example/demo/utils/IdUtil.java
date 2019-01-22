package com.example.demo.utils;

import java.util.UUID;

public class IdUtil {

    public static String randomUUID(){
        String uuid = UUID.randomUUID().toString();
        String id = uuid.replace("-", "");
        return id;
    }
}
