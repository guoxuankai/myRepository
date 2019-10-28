package com.rondaful.cloud.seller.entity.aliexpress;

import java.io.Serializable;

public class AliexpressSkuDiscountPrice implements Serializable {

    private String discountPrice;

    private String shiptoCountry;

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getShiptoCountry() {
        return shiptoCountry;
    }

    public void setShiptoCountry(String shiptoCountry) {
        this.shiptoCountry = shiptoCountry;
    }
}
