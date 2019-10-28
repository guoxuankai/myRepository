package com.rondaful.cloud.supplier.model.dto.inventory;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/7/11
 * @Description:
 */
public class OrderInvNumberDTO implements Serializable {
    private static final long serialVersionUID = 2176534759276516960L;

    @ApiModelProperty(value = "可用数量")
    private Integer availableQty;

    @ApiModelProperty(value = "本地可用数量")
    private Integer localAvailableQty;

    @ApiModelProperty(value = "品连sku")
    private String pinlianSku;

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

    public String getPinlianSku() {
        return pinlianSku;
    }

    public void setPinlianSku(String pinlianSku) {
        this.pinlianSku = pinlianSku;
    }

    public Integer getLocalAvailableQty() {
        return localAvailableQty;
    }

    public void setLocalAvailableQty(Integer localAvailableQty) {
        this.localAvailableQty = localAvailableQty;
    }
}
