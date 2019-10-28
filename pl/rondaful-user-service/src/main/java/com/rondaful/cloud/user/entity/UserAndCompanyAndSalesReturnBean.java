package com.rondaful.cloud.user.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class UserAndCompanyAndSalesReturnBean implements Serializable {

    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="用户基础信息",required = false)
    private User user;

	@ApiModelProperty(value="企业信息",required = true)
    private Companyinfo companyinfo;

	@ApiModelProperty(value="退货信息",required = true)
    private Salesreturn salesreturn;

    public User getUser() {
        if (user == null)
            user = new User();
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Companyinfo getCompanyinfo() {
        return companyinfo;
    }

    public void setCompanyinfo(Companyinfo companyinfo) {
        this.companyinfo = companyinfo;
    }

    public Salesreturn getSalesreturn() {
        return salesreturn;
    }

    public void setSalesreturn(Salesreturn salesreturn) {
        this.salesreturn = salesreturn;
    }
}