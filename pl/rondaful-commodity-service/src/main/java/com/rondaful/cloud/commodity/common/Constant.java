package com.rondaful.cloud.commodity.common;


import java.util.HashMap;
import java.util.Map;

/**
 * 全局常量
 */
public class Constant {

    public static final String[] CATEGORY_ARRY = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static final Map map = new HashMap<>();

    static {
        map.put(-1, "商品待提交");
        map.put(0, "商品审核中");
        map.put(1, "商品待上架");
        map.put(2, "商品审核拒绝");
        map.put(3, "商品上架");
    }

}
