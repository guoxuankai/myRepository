package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/5/10
 * @Description:
 */
public class ProviderUserDTO implements Serializable {
    private static final long serialVersionUID = -1783416282558906948L;

    private Integer userId;

    private String loginName;

    private String userName;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "供应链公司")
    private String supplyChainCompany;

    @ApiModelProperty(value = "供应链公司名称")
    private String supplyChainCompanyName;

    @ApiModelProperty(value = "主账号id")
    private Integer topUserId;

    @ApiModelProperty(value = "平台")
    private Integer platformType;

    @ApiModelProperty(value = "最大商品数")
    private Integer maxCommodity;


    public ProviderUserDTO(){}

    public ProviderUserDTO(Integer userId, String loginName, String companyName, String supplyChainCompany, String supplyChainCompanyName) {
        this.userId = userId;
        this.loginName = loginName;
        this.companyName = companyName;
        this.supplyChainCompany = supplyChainCompany;
        this.supplyChainCompanyName = supplyChainCompanyName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSupplyChainCompany() {
        return supplyChainCompany;
    }

    public void setSupplyChainCompany(String supplyChainCompany) {
        this.supplyChainCompany = supplyChainCompany;
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

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Integer getMaxCommodity() {
        return maxCommodity;
    }

    public void setMaxCommodity(Integer maxCommodity) {
        this.maxCommodity = maxCommodity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
