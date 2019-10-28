package com.rondaful.cloud.finance.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.swagger.annotations.ApiModelProperty;

/**
 * 参数校验工具类
 *
 */
public class ParamCheckUtil {

	private static final String TIP = "不能为空!";

	private static final Logger logger = LoggerFactory.getLogger(ParamCheckUtil.class);

	public static void isNull(Object obj) {
		Class<?> clazz = obj.getClass();

		List<Field> fieldlist = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));

		fieldlist.addAll(new ArrayList<Field>(Arrays.asList(clazz.getSuperclass().getDeclaredFields())));

		fieldlist.forEach(f -> {
			ApiModelProperty anno = f.getAnnotation(ApiModelProperty.class);
			if (anno != null && anno.required()) {// 是否为必须字段
				Object value = null;
				try {
					f.setAccessible(true);
					value = f.get(obj);
				} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
					logger.debug("反射异常：" + e.getMessage());
				}
				Assert.notNull(value, anno.name() + TIP);
			}
		});
	}

}
