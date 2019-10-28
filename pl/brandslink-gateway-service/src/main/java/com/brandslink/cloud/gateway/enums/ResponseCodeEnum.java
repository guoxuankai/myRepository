package com.brandslink.cloud.gateway.enums;

public enum ResponseCodeEnum {

    /**
     * 定义返回码
     */

    RETURN_CODE_100001("100001", "用户名或密码错误"),
    RETURN_CODE_100002("100002", "账号不能为空"),
    RETURN_CODE_100004("100004", "用户名或密码不能为空"),
    RETURN_CODE_100005("100005", "仓库id或仓库名称不能为空"),
    RETURN_CODE_100406("100406", "未登录"),
    RETURN_CODE_100401("100401", "权限不足"),
    RETURN_CODE_100400("100400", "权限不足"),


    RETURN_CODE_100200("100200", "请求成功"),
    RETURN_CODE_100500("100500", "系统异常");


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
