package com.rondaful.cloud.supplier.model.dto.logistics;

import io.swagger.annotations.ApiModelProperty;

public class ThirdAppLogisticsDTO {

    @ApiModelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "物流方式简称")
    private String shortName;

    @ApiModelProperty(value = "物流方式代码")
    private String code;

    @ApiModelProperty(value = "物流方式类型 默认0 0自营仓库物流 1品连仓库物流")
    private Integer type;

    @ApiModelProperty(value = "物流商代码")
    private String carrierCode;

    @ApiModelProperty(value = "物流商名称")
    private String carrierName;

    @ApiModelProperty(value = "状态 默认0 0停用 1启用")
    private Integer status;

    @ApiModelProperty(value = "ebay物流商代码")
    private String ebayCarrier;

    @ApiModelProperty(value = "amazon物流商代码")
    private String amazonCarrier;

    @ApiModelProperty(value = "amazon物流方式")
    private String amazonCode;

    @ApiModelProperty(value = "速卖通物流方式code")
    private String aliexpressCode;

    @ApiModelProperty(value = "其他amazon物流商")
    private String otherAmazonCarrier;

    @ApiModelProperty(value = "其他amazon物流方式代码")
    private String otherAmazonCode;

    @ApiModelProperty(value = "其他ebay物流商")
    private String otherEbayCarrier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEbayCarrier() {
        return ebayCarrier;
    }

    public void setEbayCarrier(String ebayCarrier) {
        this.ebayCarrier = ebayCarrier;
    }

    public String getAmazonCarrier() {
        return amazonCarrier;
    }

    public void setAmazonCarrier(String amazonCarrier) {
        this.amazonCarrier = amazonCarrier;
    }

    public String getAmazonCode() {
        return amazonCode;
    }

    public void setAmazonCode(String amazonCode) {
        this.amazonCode = amazonCode;
    }

    public String getAliexpressCode() {
        return aliexpressCode;
    }

    public void setAliexpressCode(String aliexpressCode) {
        this.aliexpressCode = aliexpressCode;
    }

    public String getOtherAmazonCarrier() {
        return otherAmazonCarrier;
    }

    public void setOtherAmazonCarrier(String otherAmazonCarrier) {
        this.otherAmazonCarrier = otherAmazonCarrier;
    }

    public String getOtherAmazonCode() {
        return otherAmazonCode;
    }

    public void setOtherAmazonCode(String otherAmazonCode) {
        this.otherAmazonCode = otherAmazonCode;
    }

    public String getOtherEbayCarrier() {
        return otherEbayCarrier;
    }

    public void setOtherEbayCarrier(String otherEbayCarrier) {
        this.otherEbayCarrier = otherEbayCarrier;
    }
}
