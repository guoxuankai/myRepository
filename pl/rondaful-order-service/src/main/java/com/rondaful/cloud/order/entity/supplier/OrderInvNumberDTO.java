package com.rondaful.cloud.order.entity.supplier;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: wujiachuang
 * @Date: 2019/7/11
 * @Description:
 */
public class OrderInvNumberDTO implements Serializable {
    private static final long serialVersionUID = 2176534759276516960L;

    @ApiModelProperty(value = "可用数量")
    private Integer availableQty;

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

    @Override
    public String toString() {
        return "OrderInvNumberDTO{" +
                "availableQty=" + availableQty +
                ", pinlianSku='" + pinlianSku + '\'' +
                '}';
    }
}
