package com.rondaful.cloud.order.entity.cms;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "订单详情里面的售后订单商品明细")
public class OrderAfterSalesCommodityOrderDetail implements Serializable {
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "名称")
    private String commodityName;

    @ApiModelProperty(value = "英文名称")
    private String commodityNameEn;

    @ApiModelProperty(value = "商品退款金额总价")
    private String commodityRefundMoney;

    @ApiModelProperty(value = "商品单价")
    private String commodityMoney;

    @ApiModelProperty(value = "退货数量")
    private Long commodityNumber;

    @ApiModelProperty(value = "商品SKU")
    private String commoditySku;

    @ApiModelProperty(value = "售后订单ID")
    private String orderAfterSalesId;
    @ApiModelProperty(value = "包裹号")
    private String orderTrackId;

    public String getOrderTrackId() {
        return orderTrackId;
    }

    public void setOrderTrackId(String orderTrackId) {
        this.orderTrackId = orderTrackId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCommodityRefundMoney() {
        return commodityRefundMoney;
    }

    public void setCommodityRefundMoney(String commodityRefundMoney) {
        this.commodityRefundMoney = commodityRefundMoney;
    }

    public String getCommodityMoney() {
        return commodityMoney;
    }

    public void setCommodityMoney(String commodityMoney) {
        this.commodityMoney = commodityMoney;
    }

    public Long getCommodityNumber() {
        return commodityNumber;
    }

    public void setCommodityNumber(Long commodityNumber) {
        this.commodityNumber = commodityNumber;
    }

    public String getCommoditySku() {
        return commoditySku;
    }

    public void setCommoditySku(String commoditySku) {
        this.commoditySku = commoditySku;
    }

    public String getOrderAfterSalesId() {
        return orderAfterSalesId;
    }

    public void setOrderAfterSalesId(String orderAfterSalesId) {
        this.orderAfterSalesId = orderAfterSalesId;
    }

    public String getCommodityNameEn() {
        return commodityNameEn;
    }

    public void setCommodityNameEn(String commodityNameEn) {
        this.commodityNameEn = commodityNameEn;
    }
}
