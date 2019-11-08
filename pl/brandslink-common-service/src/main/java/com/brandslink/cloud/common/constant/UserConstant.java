package com.brandslink.cloud.common.constant;

/**
 * 用户相关常量
 *
 * @ClassName UserConstant
 * @Author tianye
 * @Date 2019/6/20 10:18
 * @Version 1.0
 */
public class UserConstant {

    /**
     * 超级管理员账号
     */
    public static final String ADMIN = "admin";

    /**
     * 用户默认密码
     */
    public static final String INITIAL_PASSWORD = "111111";

    /**
     * 响应token头
     */
    public static final String TOKEN_REQUEST_HEADER_NAME = "token";

    /**
     * Redis存储有效时间  1小时
     */
    public static final Long REDIS_USER_TOKEN_KEY_TIMEOUT = 3600L;

    /**
     * Redis存储有效时间  5分钟
     */
    public static final Long REDIS_AUTH_CODE_KEY_TIMEOUT = 300L;

    /**
     * Redis存储有效时间  1分钟
     */
    public static final Long REDIS_TIMEOUT = 60L;

    /**
     * 登录接口
     */
    public static final String LOGIN_URL = "/testLogin";

    /**
     * 登录接口
     */
    public static final String SMS_LOGIN_URL = "/sms/login";

    /**
     * 修改密码接口
     */
    public static final String CHANGE_PASSWORD_URL = "/user/changes";

    /**
     * Redis存储前缀
     */
    public static final String REDIS_USER_TOKEN_KEY_FIX = "wms_token_key";

    /**
     * feign请求头名称
     */
    public static final String FEIGN_REQUEST_HEADER_NAME = "brands_link_feign_request_header_name_user_defined";

    /**
     * feign请求头值
     */
    public static final String FEIGN_REQUEST_HEADER_VALUE = "brands_link_feign_request_header_value_user_defined";

    /**
     * 校验手机号正则
     */
    public static final String IS_MOBILE_NUM = "^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";

    /**
     * 校验邮箱正则
     */
    public static final String IS_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 默认
     */
    public static final String[] DEF = {"rondaful.oss-cn-shenzhen.aliyuncs.com", "rondaful-file-test.oss-cn-shenzhen.aliyuncs.com", "rondaful-file-dev.oss-cn-shenzhen.aliyuncs.com"};

    /**
     * 域名
     */
    public static final String[] DO_MAIN = {"img.brandslink.com", "testimg.brandslink.com", "devimg.brandslink.com"};

    /**
     * 平台类型请求头名称
     */
    public static final String PLATFORM_TYPE_REQUEST_HEADER_NAME = "platformType";

    /**
     * 平台账号Redis存储前缀 wms
     */
    public static final String PLATFORM_TYPE_ACCOUNT_REDIS_USER_TOKEN_KEY_FIX_WMS = "platform_type_wms";

    /**
     * 平台账号Redis存储前缀 oms
     */
    public static final String PLATFORM_TYPE_ACCOUNT_REDIS_USER_TOKEN_KEY_FIX_OMS = "platform_type_oms";

    /**
     * 平台类型标识 oms
     */
    public static final String PLATFORM_TYPE_FLAG_OMS = "userNameLoadByOMS";

    /**
     * 平台类型标识 wms
     */
    public static final String PLATFORM_TYPE_FLAG_WMS = "userNameLoadByWMS";

    /**
     * 平台类型标识 wms
     */
    public static final String PLATFORM_TYPE_FLAG_OCMS = "userNameLoadByOCMS";

}
