package com.brandslink.cloud.finance.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author yangzefei
 * @Classname DynamicDataSourceAspect
 * @Description 数据源切换AOP
 * @Date 2019/9/4 11:05
 */
@Aspect
@Component
public class DynamicDataSourceAspect {

    @Before("@annotation(DataSource)")
    public void beforeSwitchDS(JoinPoint point){
        //获得当前访问的class
        Class<?> className = point.getTarget().getClass();
        DataSource annotation =className.getAnnotation(DataSource.class);
        if(annotation!=null){
            DynamicDataSource.setDB(annotation.value());
        }else{
            MethodSignature signature = (MethodSignature) point.getSignature();
            annotation=signature.getMethod().getAnnotation(DataSource.class);
            DynamicDataSource.setDB(annotation.value());
        }

    }


    @After("@annotation(DataSource)")
    public void afterSwitchDS(JoinPoint point){
        DynamicDataSource.clearDB();
    }
}
