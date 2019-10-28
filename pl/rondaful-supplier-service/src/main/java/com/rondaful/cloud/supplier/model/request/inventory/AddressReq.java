package com.rondaful.cloud.supplier.model.request.inventory;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/8/7
 * @Description:
 */
public class AddressReq implements Serializable {
    private static final long serialVersionUID = -5638101618530766900L;

    @ApiModelProperty(value = "修改时必传新增时不传")
    private Integer id;

    @ApiModelProperty(value = "揽收联系人-名")
    private String firstName;

    @ApiModelProperty(value = "揽收联系人-姓")
    private String lastName;

    @ApiModelProperty(value = "揽收联系人电话")
    private String contactPhone;

    @ApiModelProperty(value = "揽收地址州/省份")
    private String state;

    @ApiModelProperty(value = "揽收地址城市")
    private String city;

    @ApiModelProperty(value = "揽收地址国家")
    private String countryCode;

    @ApiModelProperty(value = "揽收地址邮编")
    private String zipcode;

    @ApiModelProperty(value = "揽收地址1")
    private String address1;

    @ApiModelProperty(value = "揽收地址2")
    private String address2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }
}
