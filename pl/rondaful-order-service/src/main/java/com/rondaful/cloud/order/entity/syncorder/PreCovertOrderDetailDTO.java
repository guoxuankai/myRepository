package com.rondaful.cloud.order.entity.syncorder;

import io.swagger.annotations.ApiModelProperty;

/**
 * 待转单的商品详情数据
 *
 * @author Blade
 * @date 2019-07-09 14:22:08
 **/
public class PreCovertOrderDetailDTO {
    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "来源订单项ID")
    private String sourceOrderLineItemId;

    @ApiModelProperty(value = "Item的sku")
    private String itemSku;

    @ApiModelProperty(value = "变体sku ")
    private String variationSku;

    @ApiModelProperty(value = "平台的sku价格")
    private String platformSKUPriceStr;

    @ApiModelProperty(value = "购买此SKU总数量")
    private Integer skuQuantity;

    @ApiModelProperty(value = "商品最迟发货时间")
    private String deliverDeadline;
}
