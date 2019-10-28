package com.brandslink.cloud.gateway.exception;


import com.brandslink.cloud.gateway.enums.ResponseCodeEnum;

/**
 * 全局异常类
 * */
public class GlobalException extends RuntimeException {


    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 消息是否为属性文件中的Key
     */
    private boolean propertiesKey = true;

    /**
     * 构造一个基本异常.
     *
     * @param message
     *            信息描述
     */
    /*public GlobalException(String message){
        super(message);
    }*/

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public GlobalException(String errorCode, String message){
        this(errorCode, message, true);
    }

    public GlobalException(ResponseCodeEnum responseCodeEnum){
        this(responseCodeEnum.getCode(), responseCodeEnum.getMsg(), true);
    }

    public GlobalException(ResponseCodeEnum responseCodeEnum, String msg){
        this(responseCodeEnum.getCode(), msg, true);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public GlobalException(String errorCode, String message, Throwable cause){
        this(errorCode, message, cause, true);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     * @param propertiesKey
     *            消息是否为属性文件中的Key
     */
    public GlobalException(String errorCode, String message, boolean propertiesKey){
        super(message);
        this.setErrorCode(errorCode);
        this.setPropertiesKey(propertiesKey);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public GlobalException(String errorCode, String message, Throwable cause, boolean propertiesKey){
        super(message, cause);
        this.setErrorCode(errorCode);
        this.setPropertiesKey(propertiesKey);
    }

    /**
     * 构造一个基本异常.
     *
     * @param message
     *            信息描述
     * @param cause
     *            根异常类（可以存入任何异常）
     */
    public GlobalException(String message, Throwable cause){
        super(message, cause);
    }
    
    public String getErrorCode(){
        return errorCode;
    }

    public void setErrorCode(String errorCode){
        this.errorCode = errorCode;
    }

    public boolean isPropertiesKey(){
        return propertiesKey;
    }

    public void setPropertiesKey(boolean propertiesKey){
        this.propertiesKey = propertiesKey;
    }
    
}
