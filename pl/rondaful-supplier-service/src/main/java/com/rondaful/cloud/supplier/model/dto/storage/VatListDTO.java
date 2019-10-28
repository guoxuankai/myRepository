package com.rondaful.cloud.supplier.model.dto.storage;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/26
 * @Description:
 */
public class VatListDTO implements Serializable {
    private static final long serialVersionUID = -1658840047016040757L;

    @ApiModelProperty(value = "进出口商,1：进口商,2：出口商")
    private Integer vatType;

    @ApiModelProperty(value = "进/出口商编码")
    private Integer cvId;

    @ApiModelProperty(value = "增值税号")
    private String vatNumber;

    @ApiModelProperty(value = "增值税豁免号")
    private String exemptionNumber;

    @ApiModelProperty(value = "EORI")
    private String eori;

    public Integer getVatType() {
        return vatType;
    }

    public void setVatType(Integer vatType) {
        this.vatType = vatType;
    }

    public Integer getCvId() {
        return cvId;
    }

    public void setCvId(Integer cvId) {
        this.cvId = cvId;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getExemptionNumber() {
        return exemptionNumber;
    }

    public void setExemptionNumber(String exemptionNumber) {
        this.exemptionNumber = exemptionNumber;
    }

    public String getEori() {
        return eori;
    }

    public void setEori(String eori) {
        this.eori = eori;
    }
}
