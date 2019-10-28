package com.rondaful.cloud.user.utils;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImtegerJsonToType implements JsonValueProcessor {

    public ImtegerJsonToType() {
        super();
    }

    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        try {
            if(value instanceof Integer){
                return null;
            }
            return value == null ? null : value;
        } catch (Exception e) {
            return "";
        }
    }

    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        try {
            if(value instanceof Integer){
                return null;
            }
            return value == null ? null : value;
        } catch (Exception e) {
            return "";
        }
    }


}
