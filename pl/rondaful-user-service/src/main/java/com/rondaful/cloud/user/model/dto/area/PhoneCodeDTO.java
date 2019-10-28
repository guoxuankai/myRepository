package com.rondaful.cloud.user.model.dto.area;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/5/28
 * @Description:
 */
public class PhoneCodeDTO implements Serializable {
    private static final long serialVersionUID = -1116265064911520694L;

    @ApiModelProperty(value = "区号",name = "phoneCode",dataType = "Integer")
    private Integer phoneCode;
    @ApiModelProperty(value = "名称",name = "phoneCode",dataType = "Integer")
    private String name;

    public Integer getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(Integer phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PhoneCodeDTO(){}

    public PhoneCodeDTO(Integer phoneCode, String name) {
        this.phoneCode = phoneCode;
        this.name = name;
    }

    @Override
    public String toString() {
        return "PhoneCodeDTO{" +
                "phoneCode=" + phoneCode +
                ", name='" + name + '\'' +
                '}';
    }
}
