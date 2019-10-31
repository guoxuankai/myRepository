package com.brandslink.cloud.common.enums;

/**
 * @author yangzefei
 * @Classname OpenAPICodeEnum
 * @Description 对外接口返回错误码
 * @Date 2019/8/5 9:21
 */
public enum OpenAPICodeEnum implements ResponseCodeEnumSupper{

    /**
     * 定义返回码
     * */

    RETURN_CODE_200100("GL_200100","版本号不能为空"),

    RETURN_CODE_200200("GL_200200","access_token不能为空"),
    RETURN_CODE_200201("GL_200201","access_token错误"),
    RETURN_CODE_200202("GL_200202","app_id错误"),
    RETURN_CODE_200203("GL_200203","app_id被禁用"),

    RETURN_CODE_200300("GL_200300","dataJson不能为空"),
    RETURN_CODE_200301("GL_200301","dataJson解析错误"),

    RETURN_CODE_200400("GL_200400","签名验证未通过"),
    RETURN_CODE_200401("GL_200401","签名生成失败"),
    RETURN_CODE_200404("GL_200404","请求资源不存在"),
    RETURN_CODE_200405("GL_200405","参数不合法"),
    RETURN_CODE_200500("GL_200500","服务内部异常");


    private String code;
    private String msg;
    private OpenAPICodeEnum(String code, String msg) {
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
