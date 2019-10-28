package com.rondaful.cloud.order.model.dto.syncorder;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Blade
 * @date 2019-07-18 14:40:21
 **/
public class PreCovertEbayOrderDetailDTO implements Serializable {

    private static final long serialVersionUID = -3627740297463229600L;

    @ApiModelProperty(value = "平台的sku价格")
    private String platformSKUPriceStr;

    @ApiModelProperty(value = "平台的sku价格")
    private BigDecimal platformSKUPrice;

    @ApiModelProperty(value = "Item的sku")
    private String itemSku;

    @ApiModelProperty(value = "变体sku ")
    private String variationSku;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "来源订单项ID")
    private String sourceOrderLineItemId;

    @ApiModelProperty(value = "系统订单项ID")
    private String orderLineItemId;

    @ApiModelProperty(value = "商品最迟发货时间")
    private String deliverDeadline;

    @ApiModelProperty(value = "购买此SKU总数量")
    private Integer skuQuantity;

    public String getPlatformSKUPriceStr() {
        return platformSKUPriceStr;
    }

    public void setPlatformSKUPriceStr(String platformSKUPriceStr) {
        this.platformSKUPriceStr = platformSKUPriceStr;
    }

    public BigDecimal getPlatformSKUPrice() {
        return platformSKUPrice;
    }

    public void setPlatformSKUPrice(BigDecimal platformSKUPrice) {
        this.platformSKUPrice = platformSKUPrice;
    }

    public String getItemSku() {
        return itemSku;
    }

    public void setItemSku(String itemSku) {
        this.itemSku = itemSku;
    }

    public String getVariationSku() {
        return variationSku;
    }

    public void setVariationSku(String variationSku) {
        this.variationSku = variationSku;
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public String getSourceOrderLineItemId() {
        return sourceOrderLineItemId;
    }

    public void setSourceOrderLineItemId(String sourceOrderLineItemId) {
        this.sourceOrderLineItemId = sourceOrderLineItemId;
    }

    public String getOrderLineItemId() {
        return orderLineItemId;
    }

    public void setOrderLineItemId(String orderLineItemId) {
        this.orderLineItemId = orderLineItemId;
    }

    public String getDeliverDeadline() {
        return deliverDeadline;
    }

    public void setDeliverDeadline(String deliverDeadline) {
        this.deliverDeadline = deliverDeadline;
    }

    public Integer getSkuQuantity() {
        return skuQuantity;
    }

    public void setSkuQuantity(Integer skuQuantity) {
        this.skuQuantity = skuQuantity;
    }
}
