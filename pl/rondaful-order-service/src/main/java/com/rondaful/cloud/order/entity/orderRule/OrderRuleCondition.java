package com.rondaful.cloud.order.entity.orderRule;

import com.rondaful.cloud.order.entity.SysOrder;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单规则的接口使用参数类
 */
public class OrderRuleCondition {

    private String sellerAccount;            //品连卖家账号
    private String platform;            //订单所属平台 [Amazon, eBay, wish, aliexpress]
    private String account;           //订单所属账号id（卖家在平台的账号id）
    private String receiveGoodsCountry; //收货国家，使用国家国际简写[USA  DE  UK]
    private String receiveGoodsZipCode; //收货邮编
    private List<String> skus;               //订单在品连的sku
    private SysOrder order;
    private BigDecimal price;           //订单的总价格 RMB
    private BigDecimal weight;          //订单的总重量 g
    private BigDecimal volume;          //订单的总体积 m³

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getReceiveGoodsCountry() {
        return receiveGoodsCountry;
    }

    public void setReceiveGoodsCountry(String receiveGoodsCountry) {
        this.receiveGoodsCountry = receiveGoodsCountry;
    }

    public String getReceiveGoodsZipCode() {
        return receiveGoodsZipCode;
    }

    public void setReceiveGoodsZipCode(String receiveGoodsZipCode) {
        this.receiveGoodsZipCode = receiveGoodsZipCode;
    }

    public List<String> getSkus() {
        return skus;
    }

    public void setSkus(List<String> skus) {
        this.skus = skus;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public SysOrder getOrder() {
        return order;
    }

    public void setOrder(SysOrder order) {
        this.order = order;
    }
}
