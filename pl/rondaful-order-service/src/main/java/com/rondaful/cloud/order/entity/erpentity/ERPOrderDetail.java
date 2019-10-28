package com.rondaful.cloud.order.entity.erpentity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value ="ERPOrderDetail")//发货时调用ERP订单接收接口orderReceive使用
public class ERPOrderDetail implements Serializable {
    @ApiModelProperty(value = "渠道产品ID")
    private String channel_item_id;//系统订单项商品ID
    @ApiModelProperty(value = "渠道sku")
    private String channel_sku;//传供应商SKU
    @ApiModelProperty(value = "渠道sku标题")
    private String channel_sku_title;//传供应商的商品名称：就是系统商品名称
    @ApiModelProperty(value = "渠道sku价格")
    private String channel_sku_price;//传供应商的商品单价
    @ApiModelProperty(value = "渠道sku数量")
    private Integer sku_quantity;

    @ApiModelProperty(value = "渠道交易号")
    private String transaction_id;

    @ApiModelProperty(value = "渠道产品链接")
    private String channel_item_link;//不用传
    @ApiModelProperty(value = "渠道价格货币简写")
    private String chanel_currency_code;//默认CNY
    @ApiModelProperty(value = "渠道sku运费")
    private Double channel_sku_shipping_free;//默认传0
    @ApiModelProperty(value = "渠道产品颜色（只限于wish）")
    private String color;
    @ApiModelProperty(value = "渠道产品尺寸（只限于wish）")
    private String size;

    public String getChannel_item_id() {
        return channel_item_id;
    }

    public void setChannel_item_id(String channel_item_id) {
        this.channel_item_id = channel_item_id;
    }

    public String getChannel_sku() {
        return channel_sku;
    }

    public void setChannel_sku(String channel_sku) {
        this.channel_sku = channel_sku;
    }

    public String getChannel_sku_title() {
        return channel_sku_title;
    }

    public void setChannel_sku_title(String channel_sku_title) {
        this.channel_sku_title = channel_sku_title;
    }

    public String getChannel_sku_price() {
        return channel_sku_price;
    }

    public void setChannel_sku_price(String channel_sku_price) {
        this.channel_sku_price = channel_sku_price;
    }

    public Integer getSku_quantity() {
        return sku_quantity;
    }

    public void setSku_quantity(Integer sku_quantity) {
        this.sku_quantity = sku_quantity;
    }

    public String getChannel_item_link() {
        return channel_item_link;
    }

    public void setChannel_item_link(String channel_item_link) {
        this.channel_item_link = channel_item_link;
    }

    public String getChanel_currency_code() {
        return chanel_currency_code;
    }

    public void setChanel_currency_code(String chanel_currency_code) {
        this.chanel_currency_code = chanel_currency_code;
    }

    public Double getChannel_sku_shipping_free() {
        return channel_sku_shipping_free;
    }

    public void setChannel_sku_shipping_free(Double channel_sku_shipping_free) {
        this.channel_sku_shipping_free = channel_sku_shipping_free;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
}
