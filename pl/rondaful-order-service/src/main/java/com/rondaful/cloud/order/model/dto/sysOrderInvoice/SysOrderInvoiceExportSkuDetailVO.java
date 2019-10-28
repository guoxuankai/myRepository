package com.rondaful.cloud.order.model.dto.sysOrderInvoice;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Blade
 * @date 2019-06-19 11:01:04
 **/
public class SysOrderInvoiceExportSkuDetailVO implements Serializable {
    private static final long serialVersionUID = -7744611461452110036L;

    @ApiModelProperty(value = "品连订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "订单项SKU")
    private String sku;

    @ApiModelProperty(value = "购买此SKU总数量")
    private Integer skuQuantity;

    @ApiModelProperty(value = "商品英文名称")
    private String itemNameEn;

    @ApiModelProperty(value = "商品属性")
    private String itemAttr;

    @ApiModelProperty(value = "商品系统单价")
    private BigDecimal itemPrice;

    @ApiModelProperty(value = "来源订单商品ID")
    private String sourceOrderLineItemId;

    @ApiModelProperty(value = "生成日期")
    private String invoiceDate;

    @ApiModelProperty(value = "生成时间")
    private String invoiceDatetime;

    @ApiModelProperty(value = "商品总价： 商品价格*数量")
    private BigDecimal total;

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getSkuQuantity() {
        return skuQuantity;
    }

    public void setSkuQuantity(Integer skuQuantity) {
        this.skuQuantity = skuQuantity;
    }

    public String getItemNameEn() {
        return itemNameEn;
    }

    public void setItemNameEn(String itemNameEn) {
        this.itemNameEn = itemNameEn;
    }

    public String getItemAttr() {
        return itemAttr;
    }

    public void setItemAttr(String itemAttr) {
        this.itemAttr = itemAttr;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getSourceOrderLineItemId() {
        return sourceOrderLineItemId;
    }

    public void setSourceOrderLineItemId(String sourceOrderLineItemId) {
        this.sourceOrderLineItemId = sourceOrderLineItemId;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceDatetime() {
        return invoiceDatetime;
    }

    public void setInvoiceDatetime(String invoiceDatetime) {
        this.invoiceDatetime = invoiceDatetime;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
