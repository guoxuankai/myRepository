package com.brandslink.cloud.common.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;


/**
 * 日期类型转json格式化工具类
 */
public class Date2JsonFormat implements JsonValueProcessor{

    private  String datePattern = "yyyy-MM-dd HH:mm:ss";

    public Date2JsonFormat() {
        super();
    }

    public Date2JsonFormat(String datePattern) {
        super();
        this.datePattern = datePattern;
    }


    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        try {
            if(value instanceof Date){
                SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                Date date = (Date)value;
                return sdf.format(date);
            }
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        try {
            if(value instanceof Date){
                SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                Date date = (Date)value;
                return sdf.format(date);
            }
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }
    
}

