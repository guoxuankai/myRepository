package com.brandslink.cloud.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * WebSecurityConfigurerAdapter 标识注解
 *
 * @ClassName WebSecurityConfigurerAdapterFlag
 * @Author tianye
 * @Date 2019/6/17 14:59
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebSecurityConfigurerAdapterFlag {
}
