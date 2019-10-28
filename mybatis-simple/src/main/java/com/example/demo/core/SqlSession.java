package com.example.demo.core;

import java.lang.reflect.Proxy;


public class SqlSession {


    public <T> T getMapper(Class<T> cls) {
        return (T) Proxy.newProxyInstance(cls.getClassLoader(),
                new Class[]{cls},
                new SqlExecuteHandler());
    }


}
