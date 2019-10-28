package com.rondaful.cloud.order.model.aliexpress.dto;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/5/29
 * @Description:
 */
public class ChildOrderDTO implements Serializable {
    private static final long serialVersionUID = 3980281398272957773L;

    @ApiModelProperty(value = "交易编号 (订单的交易编号)")
    private String orderId;

    @ApiModelProperty(value = "交易编号 (父订单的交易编号)")
    private String parentOrderId;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种")
    private String currencyCode;

    @ApiModelProperty(value = "SKU信息")
    private String skuCode;

    @ApiModelProperty(value = "品连sku")
    private String plSkuCode;

    @ApiModelProperty(value = "商品数量")
    private Integer productCount;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "当前状态超时日期 （此时间为美国太平洋时间）")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date overTimeLeft;

    @ApiModelProperty(value = "卖家登录ID")
    private String sellerLoginId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getPlSkuCode() {
        return plSkuCode;
    }

    public void setPlSkuCode(String plSkuCode) {
        this.plSkuCode = plSkuCode;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOverTimeLeft() {
        return overTimeLeft;
    }

    public void setOverTimeLeft(Date overTimeLeft) {
        this.overTimeLeft = overTimeLeft;
    }

    public String getSellerLoginId() {
        return sellerLoginId;
    }

    public void setSellerLoginId(String sellerLoginId) {
        this.sellerLoginId = sellerLoginId;
    }
}
