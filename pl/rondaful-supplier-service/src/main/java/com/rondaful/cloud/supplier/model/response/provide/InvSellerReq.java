package com.rondaful.cloud.supplier.model.response.provide;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/7/23
 * @Description:
 */
public class InvSellerReq implements Serializable {
    private static final long serialVersionUID = 6701859291254747289L;

    @ApiModelProperty(value = "可用数量")
    private Integer availableQty;

    @ApiModelProperty(value = "预警值:-1不受限")
    private Integer warnVal;

    @ApiModelProperty(value = "1下架 3正常")
    private Integer status;

    @ApiModelProperty(value = "1侵权，0不侵权 ")
    private Integer tortFlag;

    @ApiModelProperty(value = "商品价美元 ,")
    private String commodityPriceUs;

    @ApiModelProperty(value = "品连sku")
    private String pinlianSku;





    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

    public Integer getWarnVal() {
        return warnVal;
    }

    public void setWarnVal(Integer warnVal) {
        this.warnVal = warnVal;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCommodityPriceUs() {
        return commodityPriceUs;
    }

    public void setCommodityPriceUs(String commodityPriceUs) {
        this.commodityPriceUs = commodityPriceUs;
    }


    public Integer getTortFlag() {
        return tortFlag;
    }

    public void setTortFlag(Integer tortFlag) {
        this.tortFlag = tortFlag;
    }

    public String getPinlianSku() {
        return pinlianSku;
    }

    public void setPinlianSku(String pinlianSku) {
        this.pinlianSku = pinlianSku;
    }
}
