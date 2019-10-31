package com.brandslink.cloud.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yangzefei
 * @Classname OpenAPI
 * @Description 对外接口验证注解
 * @Date 2019/7/31 15:16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface OpenAPI {
    /**
     * post请求时，是否需要把form-data中的参数
     * 绑定到方法的第一个参数上
     * @return
     */
    boolean isRequire() default true;

    /**
     * ArrayList参数时 泛型类型
     * @return
     */
    Class listType() default Object.class;

}
