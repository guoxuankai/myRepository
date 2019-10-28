package com.rondaful.cloud.user.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "sellerInfo")
public class SellerInfo implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3869069143312855350L;

	@ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "卖家账户", required = true)
    private String sellerAccount;

    @ApiModelProperty(value = "卖家类型  1:个人卖家  2企业卖家",required = true)
    private Integer sellerType;

    @ApiModelProperty(value = "经营平台")
    private String managementPlatform;

    @ApiModelProperty(value = "主营类目")
    private String mainCategory;

    @ApiModelProperty(value = "月营业额")
    private String monthlyTurnover;

    @ApiModelProperty(value = "平台信息")
    private Integer platformType;

    @ApiModelProperty(value = "用户id",required = true)
    private Integer userId;

   

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    public Integer getSellerType() {
        return sellerType;
    }

    public void setSellerType(Integer sellerType) {
        this.sellerType = sellerType;
    }

    public String getManagementPlatform() {
        return managementPlatform;
    }

    public void setManagementPlatform(String managementPlatform) {
        this.managementPlatform = managementPlatform;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getMonthlyTurnover() {
        return monthlyTurnover;
    }

    public void setMonthlyTurnover(String monthlyTurnover) {
        this.monthlyTurnover = monthlyTurnover;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}