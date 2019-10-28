package com.rondaful.cloud.supplier.model.dto.storage;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public class StorageCollectingDTO implements Serializable {
    private static final long serialVersionUID = 6243171676739447783L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "入库单id")
    private Long storageId;

    @ApiModelProperty(value = "揽收联系人-名")
    private String caFirstName;

    @ApiModelProperty(value = "揽收联系人-姓")
    private String caLastName;

    @ApiModelProperty(value = "揽收联系人电话")
    private String caContactPhone;

    @ApiModelProperty(value = "揽收地址州/省份")
    private String caState;

    @ApiModelProperty(value = "揽收地址城市")
    private String caCity;

    @ApiModelProperty(value = "揽收地址国家")
    private String caCountryCode;

    @ApiModelProperty(value = "揽收地址邮编")
    private String caZipcode;

    @ApiModelProperty(value = "揽收地址1")
    private String caAddress1;

    @ApiModelProperty(value = "揽收地址2")
    private String caAddress2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public String getCaFirstName() {
        return caFirstName;
    }

    public void setCaFirstName(String caFirstName) {
        this.caFirstName = caFirstName;
    }

    public String getCaLastName() {
        return caLastName;
    }

    public void setCaLastName(String caLastName) {
        this.caLastName = caLastName;
    }

    public String getCaContactPhone() {
        return caContactPhone;
    }

    public void setCaContactPhone(String caContactPhone) {
        this.caContactPhone = caContactPhone;
    }

    public String getCaState() {
        return caState;
    }

    public void setCaState(String caState) {
        this.caState = caState;
    }

    public String getCaCity() {
        return caCity;
    }

    public void setCaCity(String caCity) {
        this.caCity = caCity;
    }

    public String getCaCountryCode() {
        return caCountryCode;
    }

    public void setCaCountryCode(String caCountryCode) {
        this.caCountryCode = caCountryCode;
    }

    public String getCaZipcode() {
        return caZipcode;
    }

    public void setCaZipcode(String caZipcode) {
        this.caZipcode = caZipcode;
    }

    public String getCaAddress1() {
        return caAddress1;
    }

    public void setCaAddress1(String caAddress1) {
        this.caAddress1 = caAddress1;
    }

    public String getCaAddress2() {
        return caAddress2;
    }

    public void setCaAddress2(String caAddress2) {
        this.caAddress2 = caAddress2;
    }
}
