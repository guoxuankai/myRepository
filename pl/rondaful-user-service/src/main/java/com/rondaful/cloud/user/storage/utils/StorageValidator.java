package com.rondaful.cloud.user.storage.utils;

import java.util.regex.Pattern;

public class StorageValidator<main> {

    //验证用户名表达式   中文+字母  8-64位
    static final String USERNAMEMATCH = "[A-Za-z\\u4e00-\\u9fa5]{8,64}";

    //地址
    static final String ADDRESSMATCH = "[A-Za-z\\u4e00-\\u9fa5]{8,180}";

    //联系人名称
    static final String LINKEMATCH = "[A-Za-z\\u4e00-\\u9fa5]{2,32}";

    //验证手机号
    public static final String REGEX_MOBILE = "^^((17[0-9])|(14[0-9])|(12[0-9])|(16[0-9])|(19[0-9])|(13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$";

    //验证电子邮件
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(USERNAMEMATCH, username);
    }

    /**
     * 校验地址
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isAddress(String username) {
        return Pattern.matches(ADDRESSMATCH, username);
    }

    /**
     * 校验联系人名称
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isLinke(String username) {
        return Pattern.matches(LINKEMATCH, username);
    }

    /**
     * 校验职位名称
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isJob(String username) {
        return Pattern.matches(LINKEMATCH, username);
    }

    /**
     * 校验手机号码
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPhone(String username) {
        return Pattern.matches(REGEX_MOBILE, username);
    }

    /**
     * 校验邮箱
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String username) {
        return Pattern.matches(REGEX_EMAIL, username);
    }

    public static void main(String[] args) {
        System.out.println(isUsername("黄振123"));

    }

}
