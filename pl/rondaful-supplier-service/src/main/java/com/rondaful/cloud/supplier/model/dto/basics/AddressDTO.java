package com.rondaful.cloud.supplier.model.dto.basics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/8/6
 * @Description:
 */
public class AddressDTO implements Serializable {
    private static final long serialVersionUID = -3659395461277652282L;

    @ApiModelProperty(value = "仓库id")
    private Integer id;

    @ApiModelProperty(value = "州/省/郡")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "邮编")
    private String zipCode;

    @ApiModelProperty(value = "联系电话1")
    private String phone1;

    @ApiModelProperty(value = "联系电话2")
    private String phone2;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "收件人名")
    private String userName;

    @ApiModelProperty(value = "收件公司名")
    private String corporationName;

    private String country;

    private String countryName;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCorporationName() {
        return corporationName;
    }

    public void setCorporationName(String corporationName) {
        this.corporationName = corporationName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
