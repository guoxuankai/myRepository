package com.brandslink.cloud.common.utils;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.brandslink.cloud.common.enums.ResponseCodeEnum;
import com.brandslink.cloud.common.exception.GlobalException;

/**
 * 
 * 任务中心接口调用返回
 *
 */
public class TaskSchedulingUtils {

	public static String getResult(String result, String message) {
		if (StringUtils.isBlank(result))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, message);
		JSONObject json = JSONObject.parseObject(result);
		if (!json.getBoolean("status"))
			throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, json.getString("message"));
		return json.getString("data");
	}

}
