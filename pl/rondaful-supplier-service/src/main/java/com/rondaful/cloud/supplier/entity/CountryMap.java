package com.rondaful.cloud.supplier.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class CountryMap implements Serializable {
    private Integer id;

    @ApiModelProperty(value = "国家简码")
    private String countryCode;

    @ApiModelProperty(value = "国家中文名称")
    private String countryName;

    @ApiModelProperty(value = "国家英文名称")
    private String countryNameEn;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    public  CountryMap(){}

    public CountryMap(String countryCode) {
        this.countryCode = countryCode;
    }

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode == null ? null : countryCode.trim();
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName == null ? null : countryName.trim();
    }

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn == null ? null : countryNameEn.trim();
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Override
    public String toString() {
        return "CountryMap{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", countryNameEn='" + countryNameEn + '\'' +
                ", postCode='" + postCode + '\'' +
                '}';
    }
}