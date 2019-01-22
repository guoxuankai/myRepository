package com.example.demo.enums;

public enum ErrorEnum {

    SUCCESS("操作成功", "10001"), FAILED("操作失败", "10002");

    private String message;
    private String code;

    ErrorEnum(String message, String code) {
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