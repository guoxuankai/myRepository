package com.rondaful.cloud.transorder.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author Blade
 * @date 2019-08-02 10:04:06
 **/
public class FastJsonUtils {

    /**
     * 用fastjson工具 转换成json字符串，使用SerializerFeature.DisableCircularReferenceDetect
     *
     * @param o {@link Object}任何需要转换的对象
     * @return String
     */
    public static String toJsonString(Object o) {
        return JSONObject.toJSONString(o, SerializerFeature.DisableCircularReferenceDetect);
    }
}
