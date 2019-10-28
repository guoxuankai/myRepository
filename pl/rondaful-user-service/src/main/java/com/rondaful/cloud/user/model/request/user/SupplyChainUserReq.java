package com.rondaful.cloud.user.model.request.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/22
 * @Description:
 */
public class SupplyChainUserReq implements Serializable {
    private static final long serialVersionUID = -3984474885309544261L;
    @ApiModelProperty(value = "修改时必穿",name = "id",dataType = "Integer")
    private Integer id;

    @ApiModelProperty(value = "用户名",name  ="userName",dataType = "String")
    private String userName;

    @ApiModelProperty(value = "电话",name  ="mobile",dataType = "String")
    private String mobile;

    @ApiModelProperty(value = "手机区号",name  ="phoneCode",dataType = "String")
    private String phoneCode;

    @ApiModelProperty(value = "联系地址",name  ="address",dataType = "String")
    private String address;

    @ApiModelProperty(value = "手机号",name  ="phone",dataType = "String")
    private String phone;

    @ApiModelProperty(value = "邮箱",name  ="email",dataType = "String")
    private String email;

    @ApiModelProperty(value = "qq",name  ="qq",dataType = "String")
    private String qq;

    @ApiModelProperty(value = "邮编",name  ="postCode",dataType = "String")
    private String postCode;

    @ApiModelProperty(value = "公司名称",name  ="companyName",dataType = "String")
    private String companyName;

    @ApiModelProperty(value = "注册区域",name  ="regArea",dataType = "String")
    private String regArea;

    @ApiModelProperty(value = "注册地址",name  ="regAddress",dataType = "String")
    private String regAddress;

    @ApiModelProperty(value = "信用代码",name  ="creditCode",dataType = "String")
    private String creditCode;

    @ApiModelProperty(value = "成立日期",name  ="creditDate",dataType = "String")
    private String creditDate;

    @ApiModelProperty(value = "经营期限",name  ="operatingPeriod",dataType = "String")
    private String operatingPeriod;

    @ApiModelProperty(value = "公司规模",name  ="companyScale",dataType = "String")
    private String companyScale;

    @ApiModelProperty(value = "法人名称",name  ="legalpersonName",dataType = "String")
    private String legalpersonName;

    @ApiModelProperty(value = "法人身份证",name  ="idCard",dataType = "String")
    private String idCard;

    @ApiModelProperty(value = "身份证正面",name  ="identitycardFrontImage",dataType = "String")

    private String identitycardFrontImage;

    @ApiModelProperty(value = "身份证反面",name  ="identitycardReverseImage",dataType = "String")
    private String identitycardReverseImage;

    @ApiModelProperty(value = "营业执照",name  ="businessLicense",dataType = "String")
    private String businessLicense;

    @ApiModelProperty(value = "公司简介",name  ="companyProfile",dataType = "String")

    private String companyProfile;

    @ApiModelProperty(value = "绑定类型:0-供应商,1-卖家,3-仓库。json数组tostring")
    private String bindType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
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

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

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

    public String getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(String creditDate) {
        this.creditDate = creditDate;
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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
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

    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
