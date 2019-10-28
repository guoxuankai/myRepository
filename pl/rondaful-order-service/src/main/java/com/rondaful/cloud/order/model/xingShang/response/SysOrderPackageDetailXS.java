package com.rondaful.cloud.order.model.xingShang.response;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 第三方供应商接口--包裹详情类
 *
 * @author Blade
 * @date 2019-08-05 09:23:13
 **/
public class SysOrderPackageDetailXS implements Serializable {
    private static final long serialVersionUID = 306374680305649177L;

    @ApiModelProperty(value = "订单跟踪号")
    private String orderTrackId;

    @ApiModelProperty(value = "来源sku")
    private String sourceSku;

    @ApiModelProperty(value = "购买此SKU总数量")
    private Integer skuQuantity;

    public String getOrderTrackId() {
        return orderTrackId;
    }

    public void setOrderTrackId(String orderTrackId) {
        this.orderTrackId = orderTrackId;
    }

    public String getSourceSku() {
        return sourceSku;
    }

    public void setSourceSku(String sourceSku) {
        this.sourceSku = sourceSku;
    }

    public Integer getSkuQuantity() {
        return skuQuantity;
    }

    public void setSkuQuantity(Integer skuQuantity) {
        this.skuQuantity = skuQuantity;
    }
}