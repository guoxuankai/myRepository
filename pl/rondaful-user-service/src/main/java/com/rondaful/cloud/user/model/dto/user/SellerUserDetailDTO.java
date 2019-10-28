package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/29
 * @Description:
 */
public class SellerUserDetailDTO extends SellerUserDTO {
    private static final long serialVersionUID = 3401132811552414236L;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "注册区域")
    private String regArea;

    @ApiModelProperty(value = "注册地址")
    private String regAddress;

    @ApiModelProperty(value = "统一信用代码")
    private String creditCode;

    @ApiModelProperty(value = "成立日期")
    private String createDate;

    @ApiModelProperty(value = "经营期限")
    private String operatingPeriod;

    @ApiModelProperty(value = "公司规模")
    private String companyScale;

    @ApiModelProperty(value = "法人名称")
    private String legalpersonName;

    @ApiModelProperty(value = "法人身份证")
    private String legalpersonIdentitycard;

    @ApiModelProperty(value = "身份证正面")
    private String identitycardFrontImage;

    @ApiModelProperty(value = "身份证反面")
    private String identitycardReverseImage;

    @ApiModelProperty(value = "营业执照")
    private String businessLicense;

    @ApiModelProperty(value = "公司简介")
    private String companyProfile;

    @ApiModelProperty(value = "月营业额")
    private String monthlyTurnover;

    @ApiModelProperty(value = "平台类型   0供应商平台  1卖家平台  2管理平台")
    private Integer platformType;

    @ApiModelProperty(value = "品牌授权书")
    private String brandLicensing;

    @ApiModelProperty(value = "其他证书")
    private String otherCertificates;

    @ApiModelProperty(value = "卖家类型  1:个人卖家  2企业卖家")
    private Integer sellerType;

    @ApiModelProperty(value = "经营平台")
    private String managementPlatform;

    @ApiModelProperty(value = "主营类目")
    private String mainCategory;

    @ApiModelProperty(value = "图片的备注")
    private String imageContent;


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegArea() {
        return regArea;
    }

    public void setRegArea(String regArea) {
        this.regArea = regArea;
    }

    public String getRegAddress() {
        return regAddress;
    }

    public void setRegAddress(String regAddress) {
        this.regAddress = regAddress;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getOperatingPeriod() {
        return operatingPeriod;
    }

    public void setOperatingPeriod(String operatingPeriod) {
        this.operatingPeriod = operatingPeriod;
    }

    public String getCompanyScale() {
        return companyScale;
    }

    public void setCompanyScale(String companyScale) {
        this.companyScale = companyScale;
    }

    public String getLegalpersonName() {
        return legalpersonName;
    }

    public void setLegalpersonName(String legalpersonName) {
        this.legalpersonName = legalpersonName;
    }

    public String getLegalpersonIdentitycard() {
        return legalpersonIdentitycard;
    }

    public void setLegalpersonIdentitycard(String legalpersonIdentitycard) {
        this.legalpersonIdentitycard = legalpersonIdentitycard;
    }

    public String getIdentitycardFrontImage() {
        return identitycardFrontImage;
    }

    public void setIdentitycardFrontImage(String identitycardFrontImage) {
        this.identitycardFrontImage = identitycardFrontImage;
    }

    public String getIdentitycardReverseImage() {
        return identitycardReverseImage;
    }

    public void setIdentitycardReverseImage(String identitycardReverseImage) {
        this.identitycardReverseImage = identitycardReverseImage;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getCompanyProfile() {
        return companyProfile;
    }

    public void setCompanyProfile(String companyProfile) {
        this.companyProfile = companyProfile;
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

    public String getBrandLicensing() {
        return brandLicensing;
    }

    public void setBrandLicensing(String brandLicensing) {
        this.brandLicensing = brandLicensing;
    }

    public String getOtherCertificates() {
        return otherCertificates;
    }

    public void setOtherCertificates(String otherCertificates) {
        this.otherCertificates = otherCertificates;
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

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }
}
