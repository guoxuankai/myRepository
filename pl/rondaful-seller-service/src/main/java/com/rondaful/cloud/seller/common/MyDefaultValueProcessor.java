package com.rondaful.cloud.seller.common;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.util.JSONUtils;

/**
* @Description:数据封装类型默认值设置
* @author:范津 
* @date:2019年3月21日 下午6:26:13
 */
public class MyDefaultValueProcessor implements DefaultValueProcessor {

	@Override
	public Object getDefaultValue(Class type) {
		if (JSONUtils.isArray(type)) {
			return new JSONArray();
		} else if (JSONUtils.isNumber(type)) {
			if (JSONUtils.isDouble(type)) {
				return new Double(0);
			} else {
				return new Integer(0);
			}
		} else if (JSONUtils.isBoolean(type)) {
			return Boolean.FALSE;
		} else if (JSONUtils.isString(type)) {
			return "";
		}
		return JSONNull.getInstance();
	}

}
