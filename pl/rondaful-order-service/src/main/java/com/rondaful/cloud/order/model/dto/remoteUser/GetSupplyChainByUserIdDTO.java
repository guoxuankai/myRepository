package com.rondaful.cloud.order.model.dto.remoteUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class GetSupplyChainByUserIdDTO implements Serializable {


    private static final long serialVersionUID = -1663210998251498547L;

    @ApiModelProperty("对应用户id")
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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

    @ApiModelProperty("用户名称")
    private String userName;

    public String getSupplierCompanyName() {
        return supplierCompanyName;
    }

    public void setSupplierCompanyName(String supplierCompanyName) {
        this.supplierCompanyName = supplierCompanyName;
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
