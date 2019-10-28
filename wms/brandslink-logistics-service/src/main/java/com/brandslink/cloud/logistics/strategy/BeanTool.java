package com.brandslink.cloud.logistics.strategy;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取bean工具类
 */
@Component
public class BeanTool implements ApplicationContextAware {
 
    private static ApplicationContext applicationContext;
 
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (applicationContext == null) {
            applicationContext = context;
        }
    }
 
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }
 
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
 
}
