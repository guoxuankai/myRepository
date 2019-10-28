package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-14 15:08
 * 包名: com.rondaful.cloud.order.entity
 * 描述:
 */
@ApiModel(value ="OrderRecord")
public class OrderRecord {
    @ApiModelProperty(value = "订单数", required = true)
    private Long orderQuantity;
    @ApiModelProperty(value = "销售额", required = true)
    private BigDecimal saleroom;
    @ApiModelProperty(value = "刊登数", required = true)
    private Long publishedQuantity;
    @ApiModelProperty(value = "利润", required = true)
    private BigDecimal profit;

    @Override
    public String toString() {
        return "OrderRecord{" +
                "orderQuantity=" + orderQuantity +
                ", saleroom=" + saleroom +
                ", publishedQuantity=" + publishedQuantity +
                ", profit=" + profit +
                '}';
    }

    public OrderRecord() {
    }

    public Long getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Long orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public BigDecimal getSaleroom() {
        return saleroom;
    }

    public void setSaleroom(BigDecimal saleroom) {
        this.saleroom = saleroom;
    }

    public Long getPublishedQuantity() {
        return publishedQuantity;
    }

    public void setPublishedQuantity(Long publishedQuantity) {
        this.publishedQuantity = publishedQuantity;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public OrderRecord(Long orderQuantity, BigDecimal saleroom, Long publishedQuantity, BigDecimal profit) {
        this.orderQuantity = orderQuantity;
        this.saleroom = saleroom;
        this.publishedQuantity = publishedQuantity;
        this.profit = profit;
    }
}