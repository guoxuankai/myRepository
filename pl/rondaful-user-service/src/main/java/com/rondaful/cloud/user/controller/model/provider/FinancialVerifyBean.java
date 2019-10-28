package com.rondaful.cloud.user.controller.model.provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("银行卡绑定验证法人姓名身份证")
public class FinancialVerifyBean implements Serializable {
    private static final long serialVersionUID = -2006466947016381138L;

    @ApiModelProperty(name = "username",value = "法人姓名")
    private String username ;

    @ApiModelProperty(name = "legalpersonIdentitycard",value = "法人身份证")
    private String legalpersonIdentitycard;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLegalpersonIdentitycard() {
        return legalpersonIdentitycard;
    }

    public void setLegalpersonIdentitycard(String legalpersonIdentitycard) {
        this.legalpersonIdentitycard = legalpersonIdentitycard;
    }
}
