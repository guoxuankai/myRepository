package com.brandslink.cloud.common.constant;

/**
 * 客户相关常量
 *
 * @ClassName UserConstant
 * @Author tianye
 * @Date 2019/6/20 10:18
 * @Version 1.0
 */
public class CustomerConstant {

    /**
     * redis前缀
     */
    public final static String REDIS_PREFIX = "customer_prefix_";

    /**
     * 字符编码
     */
    public final static String INPUT_CHARSET = "UTF-8";

    /**
     * 签名请求头
     */
    public final static String REQUEST_PARAMETER_SIGN = "sign";

    /**
     * 客户id请求头
     */
    public final static String REQUEST_PARAMETER_CUSTOMER_ID = "customerAppId";

    /**
     * 客户appId请求头
     */
    public final static String REQUEST_PARAMETER_ACCESS_TOKEN = "access_token";

    /**
     * 请求方式POST
     */
    public final static String REQUEST_METHOD_POST = "POST";

    /**
     * 请求方式GET
     */
    public final static String REQUEST_METHOD_GET = "GET";

    /**
     * 短信验证码Redis存储前缀
     */
    public final static String AUTH_CODE_REDIS_PREFIX = "message_auth_code";


}
