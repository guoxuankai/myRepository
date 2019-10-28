package com.rondaful.cloud.order.model.dto.wms;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 收件人信息
 *
 * @author Blade
 * @date 2019-08-12 14:08:39
 **/
public class WmsRecipientsDTO implements Serializable {
    private static final long serialVersionUID = -6019346821426472013L;

    @ApiModelProperty(value = "国家二字码", required = true)
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "省/州", required = true)
    private String state;

    @ApiModelProperty(value = "城市", required = true)
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "详情地址1", required = true)
    private String addressOne;

    @ApiModelProperty(value = "详情地址2")
    private String addressTwo;

    @ApiModelProperty(value = "移动电话[与固定电话二选一]")
    private String mobilePhone;

    @ApiModelProperty(value = "固定电话[与移动电话二选一]")
    private String fixationPhone;

    @ApiModelProperty(value = "姓", required = true)
    private String sur;

    @ApiModelProperty(value = "名")
    private String name;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "买家id")
    private String buyerId;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddressOne() {
        return addressOne;
    }

    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }

    public String getAddressTwo() {
        return addressTwo;
    }

    public void setAddressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFixationPhone() {
        return fixationPhone;
    }

    public void setFixationPhone(String fixationPhone) {
        this.fixationPhone = fixationPhone;
    }

    public String getSur() {
        return sur;
    }

    public void setSur(String sur) {
        this.sur = sur;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
}
