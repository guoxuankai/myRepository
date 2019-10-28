package com.rondaful.cloud.supplier.model.dto.reomte.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/10/23
 * @Description:
 */
public class LogisticsInfo implements Serializable {
    private static final long serialVersionUID = 3745789663545221169L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @ApiModelProperty(value = "全称")
    private String fullName;

    @ApiModelProperty(value = "联系人")
    private String userName;

    @ApiModelProperty(value = "手机")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "供应链公司id")
    private Integer supplyId;

    @ApiModelProperty(value = "供应链公司名称")
    private String supplyName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }

    public String getSupplyName() {
        return supplyName;
    }

    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName;
    }
}
