package com.brandslink.cloud.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyStringUtils {


    //首字母转小写
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return String.valueOf(Character.toLowerCase(s.charAt(0))) + s.substring(1);
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return String.valueOf(Character.toUpperCase(s.charAt(0))) + s.substring(1);
    }

    //length用户要求产生字符串的长度
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    public static void main(String[] str) {
       /* String s = "asfasddfsadvfasd";
        String s1 = toUpperCaseFirstOne(s);
        System.out.println(s1);*/

        //System.out.println(new Random().n);
        String str1 = "[{\"随机欢迎语\":[\"aaa\",\"bbbbb\",\"ccccc\"],\"no\":\"1\"},{\"商品标题\":\"商品标题\",\"no\":\"2\"},{\"商品卖点\":\"商品卖点\",\"no\":\"3\"},{\"商品描述\":\"商品描述\",\"no\":\"2\"},{\"包装清单\":\"包装清单\",\"no\":\"2\"},{\"随机结束语\":[\"1111\",\"2222\",\"33333\"],\"no\":\"2\"}]";
        //System.out.println(JSONObject.);
        JSONArray objects = JSONObject.parseArray(str1);
        List<JSONObject> jsonObjects = JSONObject.parseArray(str1, JSONObject.class);
        for (JSONObject object : jsonObjects) {
            for (Map.Entry<String, Object> en : object.entrySet()) {
                System.out.println(en.getValue());
            }
        }

    }


}
