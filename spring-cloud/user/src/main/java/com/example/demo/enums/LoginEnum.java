package com.example.demo.enums;

public enum LoginEnum {

    SUCCESS("登陆成功", "100"), ERROR("用户名或密码错误", "101");

    private String message;
    private String code;

    LoginEnum(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}