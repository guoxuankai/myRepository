package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value ="PlatformExport")
public class PlatformOrderItemInfo {
    @ApiModelProperty(value = "平台SKU")
    private String platformSku;
    @ApiModelProperty(value = "平台SKU单价")
    private String platformPrice;
    @ApiModelProperty(value = "平台SKU数量")
    private Integer skuNum;
    @ApiModelProperty(value = "货币类型")
    private String currencyCode;

    public PlatformOrderItemInfo() {
    }

    public PlatformOrderItemInfo(String platformSku, String platformPrice, Integer skuNum, String currencyCode) {
        this.platformSku = platformSku;
        this.platformPrice = platformPrice;
        this.skuNum = skuNum;
        this.currencyCode = currencyCode;
    }
}
