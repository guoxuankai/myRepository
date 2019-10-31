package com.brandslink.cloud.common.utils;


import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import java.math.BigDecimal;


/**
 * BigDecimal类型转json格式化工具类
 */
public class BigDecimal2JsonFormat implements JsonValueProcessor{

    public BigDecimal2JsonFormat() {
        super();
    }

    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        try {
            if(value instanceof BigDecimal){
                return value.toString();
            }
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        try {
            if(value instanceof BigDecimal){
                return value.toString();
            }
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }
    }

}

