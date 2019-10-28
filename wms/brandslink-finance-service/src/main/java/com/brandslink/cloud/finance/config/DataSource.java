package com.brandslink.cloud.finance.config;

import java.lang.annotation.*;

/**
 * @author yangzefei
 * @Classname DataSource
 * @Description 数据源注解
 * @Date 2019/9/4 11:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface DataSource {
    String value() default DynamicDataSource.DEFAULT_DS;
}

