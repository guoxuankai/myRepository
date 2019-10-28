package com.brandslink.cloud.user.dto.request;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 新增or修改货主请求model
 *
 * @ClassName AddOrUpdateShipperRequestDTO
 * @Author tianye
 * @Date 2019/7/16 16:40
 * @Version 1.0
 */
public class AddOrUpdateShipperRequestDTO implements Serializable {

    @ApiModelProperty(value = "货主id")
    private Integer id;

    @ApiModelProperty(value = "oms系统传账号id，其他系统传客户id")
    private Integer customerId;

    @ApiModelProperty(value = "货主编码")
    private String shipperCode;

    @ApiModelProperty(value = "货主名称")
    private String shipperName;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "联系方式")
    private String contactWay;

    @ApiModelProperty(value = "省")
    private String provincial;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "详细地址")
    private String address;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public String getProvincial() {
        return provincial;
    }

    public void setProvincial(String provincial) {
        this.provincial = provincial;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
