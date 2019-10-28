package com.rondaful.cloud.user.model.response.user;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/5/4
 * @Description:
 */
public class BindOrgResp implements Serializable {
    private static final long serialVersionUID = 1163528113478126255L;

    private String code;


    private String name;

    public BindOrgResp(){}

    public BindOrgResp(String code, String name) {
        this.code = code;
        this.name = name;
    }

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
}
