package com.rondaful.cloud.order.entity;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-02-13 14:41
 * 包名: com.rondaful.cloud.order.entity
 * 描述:
 */
public class BuyerCountAndCountryCode {
    private String buyerCount;
    private String buyerCountryCode;

    @Override
    public String toString() {
        return "BuyerCountAndCountryCode{" +
                "buyerCount='" + buyerCount + '\'' +
                ", buyerCountryCode='" + buyerCountryCode + '\'' +
                '}';
    }

    public String getBuyerCount() {
        return buyerCount;
    }

    public String getBuyerCountryCode() {
        return buyerCountryCode;
    }

    public void setBuyerCount(String buyerCount) {
        this.buyerCount = buyerCount;
    }

    public void setBuyerCountryCode(String buyerCountryCode) {
        this.buyerCountryCode = buyerCountryCode;
    }
}
