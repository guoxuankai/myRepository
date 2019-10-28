package com.rondaful.cloud.user.utils;

import com.rondaful.cloud.user.entity.Companyinfo;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 全局格式校验工具类
 * */
public class ValidatorUtil {

    /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[\\u4e00-\\u9fa5]*$";

    /**
     * 正则表达式：验证密码   数字+字母，数字+特殊字符，字母+特殊字符，数字+字母+特殊字符组合，可以是纯数字，纯字母， 6-16位
     */
    public static final String REGEX_PASSWORD = "^[^ \\u4e00-\\u9fa5\\s\\（\\）\\(\\)][^\\u4e00-\\u9fa5\\s\\（\\）\\(\\)]{4,14}[^ \\u4e00-\\u9fa5\\s\\（\\）\\(\\)]$";

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^^((17[0-9])|(14[0-9])|(12[0-9])|(16[0-9])|(19[0-9])|(13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

    /**
     * 正则表达式：验证数字
     */
    public static final String REGEX_MATH = "^[0-9]*[1-9][0-9]*$";

    /**
     * 正则表达式：小数点两位
     */
    public static final String REGEX_FLOAT = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";

    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

    /**
     * 正则表达式：验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

    /**
     * 正则表达式：验证qq
     */
    public static final String QQ = "[1-9][0-9]{4,}";

    //地址
    public static final String ADDRESSMATCH = "[A-Za-z\\u4e00-\\u9fa5]{8,180}";

    //联系人名称
    public static final String LINKEMATCH = "[A-Za-z\\u4e00-\\u9fa5]{2,32}";

    //联系人邮编
    public static final String POSTCODE = "[0-9]{5,10}";//"[1-9]\\d{5}(?!\\d)";

    /**
     * 校验地址
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPostcode(String username) {
        return Pattern.matches(POSTCODE, username);
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
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isQq(String username) {
        return Pattern.matches(QQ, username);
    }


    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }



    /**
     * 校验汉字
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * 校验数字
     *
     * @param regex_math
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMath(String regex_math) {
        return Pattern.matches(REGEX_MATH, regex_math);
    }


    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    /**
     * 校验URL
     *
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    /**
     * 校验IP地址
     *
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }

   

    /**
     * 校验数字小数点后两位
     *
     * @param floating
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMathFloat(String floating) {
        Pattern pattern=Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
        Matcher match=pattern.matcher(floating);
        return match.matches();
    }

    public static String reverseStr(String str){
        System.out.println("递归中的===》"+str);
        if(str.length() <= 1){
            return str;
        }
        return reverseStr(str.substring(1)) + str.charAt(0);
    }

    public static void main(String[] args) {
      System.out.println(reverseStr("huangzhenheng"));
    }
    
}
