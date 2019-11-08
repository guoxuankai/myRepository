package com.brandslink.cloud.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.brandslink.cloud.common.utils.DateUtils;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OutExcel {

	/** 是否可为空 **/
	boolean notNull() default false;

	/** 错误描述信息 **/
	String messgae() default "";

	/** 时间格式 **/
	String dateFormat() default DateUtils.FORMAT_3;

	/**
	 * Excel格式为下拉选择的Key<br>
	 * 动态的不要设置默认，如需要请传入默认参数
	 **/
	String selectedKey() default "";

	/** 别名(可写可不写(不写会使用对应属性名称),唯一不能重复) **/
	String alias() default "";

	/** Excel列顺序(从第 1 开始,唯一不能重复、不能为空) **/
	String columnIndex() default "1";

}
