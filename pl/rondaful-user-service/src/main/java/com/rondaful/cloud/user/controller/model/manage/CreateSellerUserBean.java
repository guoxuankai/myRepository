package com.rondaful.cloud.user.controller.model.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "新增卖家")
public class CreateSellerUserBean implements Serializable {

    private static final long serialVersionUID = 1452859445817928105L;

    @ApiModelProperty(name = "username", value = "账号", dataType = "string",required = true)
    private String username;
    @ApiModelProperty(name = "password", value = "密码", dataType = "string", required = true)
    private String password;
    @ApiModelProperty(name = "linkman", value = "联系人", dataType = "string", required = false)
    private String linkman;
    @ApiModelProperty(name = "phone", value = "联系人手机", dataType = "string",  required = true)
    private String phone;
    @ApiModelProperty(name = "email", value = "联系邮箱", dataType = "string", required = false)
    private String email;
    @ApiModelProperty(name = "site", value = "联系地址", dataType = "string",  required = false)
    private String site;
    @ApiModelProperty(name = "supplyChainCompany", value = "供应链公司id,一定要填数字！！！！", dataType = "string", required = true)
    private String supplyChainCompany;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
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

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSupplyChainCompany() {
        return supplyChainCompany;
    }

    public void setSupplyChainCompany(String supplyChainCompany) {
        this.supplyChainCompany = supplyChainCompany;
    }
}
