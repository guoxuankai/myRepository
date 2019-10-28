package com.rondaful.cloud.user.model.dto;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/5/24
 * @Description:
 */
public class SmsDTO implements Serializable {
    private static final long serialVersionUID = 223123406104594901L;

    private String code;

    private String name;

    private String passWord;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public SmsDTO(){}

    public SmsDTO(String code) {
        this.code = code;
    }

    public SmsDTO(String name, String passWord) {
        this.name = name;
        this.passWord = passWord;
    }
}
