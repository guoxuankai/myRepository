package com.rondaful.cloud.seller.entity.aliexpress;

import java.io.Serializable;
import java.util.List;

public class AliexpressAeopAeProductSku implements Serializable {

    private List<AliexpressSkuDiscountPrice> aeopSKUNationalDiscountPriceList;

    private List<AliexpressAeopSkuProperty> aeopSKUPropertyList;

    private String barcode;

    private String currencyCode;

    private String id;

    private Long ipmSkuStock;

    private String skuCode;

    private String skuDiscountPrice;

    private String skuPrice;

    private Boolean skuStock;

    public List<AliexpressSkuDiscountPrice> getAeopSKUNationalDiscountPriceList() {
        return aeopSKUNationalDiscountPriceList;
    }

    public void setAeopSKUNationalDiscountPriceList(List<AliexpressSkuDiscountPrice> aeopSKUNationalDiscountPriceList) {
        this.aeopSKUNationalDiscountPriceList = aeopSKUNationalDiscountPriceList;
    }

    public List<AliexpressAeopSkuProperty> getAeopSKUPropertyList() {
        return aeopSKUPropertyList;
    }

    public void setAeopSKUPropertyList(List<AliexpressAeopSkuProperty> aeopSKUPropertyList) {
        this.aeopSKUPropertyList = aeopSKUPropertyList;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getIpmSkuStock() {
        return ipmSkuStock;
    }

    public void setIpmSkuStock(Long ipmSkuStock) {
        this.ipmSkuStock = ipmSkuStock;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSkuDiscountPrice() {
        return skuDiscountPrice;
    }

    public void setSkuDiscountPrice(String skuDiscountPrice) {
        this.skuDiscountPrice = skuDiscountPrice;
    }

    public String getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice) {
        this.skuPrice = skuPrice;
    }

    public Boolean getSkuStock() {
        return skuStock;
    }

    public void setSkuStock(Boolean skuStock) {
        this.skuStock = skuStock;
    }
}
