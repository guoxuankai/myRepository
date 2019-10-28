package com.rondaful.cloud.user.model.response.third;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/8/5
 * @Description:
 */
public class CountryResp implements Serializable {
    private static final long serialVersionUID = -1599021589107936909L;

    @ApiModelProperty(value = "名称",name = "name",dataType = "String")
    private String name;

    @ApiModelProperty(value = "编码",name = "code",dataType = "String")
    private String code;

    public CountryResp(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public CountryResp() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
