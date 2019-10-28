package com.rondaful.cloud.seller.enums;

import com.rondaful.cloud.common.enums.ResponseCodeEnumSupper;

public enum ResponseCodeEnum  implements ResponseCodeEnumSupper{

    /**
     * 定义返回码
     * */

    RETURN_CODE_100001("100001","用户名或密码错误"),
    RETURN_CODE_100400("100400","请求参数错误"),
    RETURN_CODE_100401("100401","权限不足"),
    RETURN_CODE_100403("100403","参数不允许为空"),
    RETURN_CODE_100406("100406","未登录"),

    RETURN_CODE_100200("100200","请求成功"),
    RETURN_CODE_100500("100500","系统异常"),

    RETURN_CODE_100600("10060","找不到数据"),
	RETURN_CODE_100601("10061","数据解释异常");





    private String code;
    private String msg;
    private ResponseCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }

}
