package com.brandslink.cloud.logistics.strategy;

import com.brandslink.cloud.logistics.thirdLogistics.BaseHandler;

import java.util.Map;

public class HandlerContext {

    private Map<String, Class> handlerMap;

    public HandlerContext(Map<String, Class> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public BaseHandler getInstance(String type) {
        Class clazz = handlerMap.get(type);
        if (clazz == null) {
            throw new IllegalArgumentException("not found handler for type: " + type);
        }
        return (BaseHandler) BeanTool.getBean(clazz);
    }

}