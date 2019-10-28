package com.rondaful.cloud.supplier.model.dto.reomte.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/10/8
 * @Description:
 */
public class FeignUserDTO implements Serializable {
    private static final long serialVersionUID = -7795796882712402345L;

    @ApiModelProperty("对应用户id")
    private Integer userId;

    @ApiModelProperty("供应链id")
    private Integer supplyId;

    @ApiModelProperty("供应链公司名称")
    private String supplyChainCompanyName;

    @ApiModelProperty("供应商公司名称")
    private String supplierCompanyName;

    @ApiModelProperty("顶级账号id")
    private Integer topUserId;
    @ApiModelProperty("登录名")
    private String loginName;

    private String userName;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }

    public String getSupplyChainCompanyName() {
        return supplyChainCompanyName;
    }

    public void setSupplyChainCompanyName(String supplyChainCompanyName) {
        this.supplyChainCompanyName = supplyChainCompanyName;
    }

    public String getSupplierCompanyName() {
        return supplierCompanyName;
    }

    public void setSupplierCompanyName(String supplierCompanyName) {
        this.supplierCompanyName = supplierCompanyName;
    }

    public Integer getTopUserId() {
        return topUserId;
    }

    public void setTopUserId(Integer topUserId) {
        this.topUserId = topUserId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
