package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-09 15:25
 * 包名: com.rondaful.cloud.order.entity
 * 描述:
 */
@ApiModel(value ="SystemExport")
public class SystemExport {


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SystemExport{");
        sb.append("orderId='").append(orderId).append('\'');
        sb.append(", platformOrderId='").append(platformOrderId).append('\'');
        sb.append(", seller='").append(seller).append('\'');
        sb.append(", sellerId='").append(sellerId).append('\'');
        sb.append(", packageId='").append(packageId).append('\'');
        sb.append(", platformSku='").append(platformSku).append('\'');
        sb.append(", placer='").append(placer).append('\'');
        sb.append(", plSkus='").append(plSkus).append('\'');
        sb.append(", purchasePrices='").append(purchasePrices).append('\'');
        sb.append(", freightUnitPrice='").append(freightUnitPrice).append('\'');
        sb.append(", quantity='").append(quantity).append('\'');
        sb.append(", goodsTotalPrice='").append(goodsTotalPrice).append('\'');
        sb.append(", totalFreight='").append(totalFreight).append('\'');
        sb.append(", orderAmount='").append(orderAmount).append('\'');
        sb.append(", platformSalesAmount='").append(platformSalesAmount).append('\'');
        sb.append(", profit='").append(profit).append('\'');
        sb.append(", profitMargin='").append(profitMargin).append('\'');
        sb.append(", itemPrice='").append(itemPrice).append('\'');
        sb.append(", itemCount='").append(itemCount).append('\'');
        sb.append(", prices='").append(prices).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", country='").append(country).append('\'');
        sb.append(", province='").append(province).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", wareHouse='").append(wareHouse).append('\'');
        sb.append(", deliveryMethod='").append(deliveryMethod).append('\'');
        sb.append(", trackId='").append(trackId).append('\'');
        sb.append(", orderStatus='").append(orderStatus).append('\'');
        sb.append(", CreateTime='").append(CreateTime).append('\'');
        sb.append(", deliveryTime='").append(deliveryTime).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
    private String orderId;
    private String platformOrderId;
    private String seller;
    private String sellerId;
    private String packageId;//包裹ID
    private String platformSku;//平台SKU
    private String placer;//刊登人
    private String plSkus;
    private String purchasePrices;//采购单价（$）
    private String freightUnitPrice;//运费单价（$）
    private String quantity;//数量（$）
    private String goodsTotalPrice;//商品总价（$）
    private String totalFreight;//总运费（$）
    private String orderAmount;//订单金额（$）
    private String platformSalesAmount;//平台销售金额（$）
    private String profit;//利润（$）
    private String profitMargin;//利润率
    private String itemPrice;
    private String itemCount;
    private String prices;
    private String name;
    private String country;
    private String province;
    private String city;
    private String address;
    private String wareHouse;
    private String deliveryMethod;
    private String trackId;
    private String orderStatus;
    private String CreateTime;
    private String deliveryTime;
    private String trackNumber;

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPlatformOrderId() {
        return platformOrderId;
    }

    public void setPlatformOrderId(String platformOrderId) {
        this.platformOrderId = platformOrderId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPlSkus() {
        return plSkus;
    }

    public void setPlSkus(String plSkus) {
        this.plSkus = plSkus;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(String wareHouse) {
        this.wareHouse = wareHouse;
    }



    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPlatformSku() {
        return platformSku;
    }

    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku;
    }

    public String getPlacer() {
        return placer;
    }

    public void setPlacer(String placer) {
        this.placer = placer;
    }

    public String getPurchasePrices() {
        return purchasePrices;
    }

    public void setPurchasePrices(String purchasePrices) {
        this.purchasePrices = purchasePrices;
    }

    public String getFreightUnitPrice() {
        return freightUnitPrice;
    }

    public void setFreightUnitPrice(String freightUnitPrice) {
        this.freightUnitPrice = freightUnitPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getGoodsTotalPrice() {
        return goodsTotalPrice;
    }

    public void setGoodsTotalPrice(String goodsTotalPrice) {
        this.goodsTotalPrice = goodsTotalPrice;
    }

    public String getTotalFreight() {
        return totalFreight;
    }

    public void setTotalFreight(String totalFreight) {
        this.totalFreight = totalFreight;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getPlatformSalesAmount() {
        return platformSalesAmount;
    }

    public void setPlatformSalesAmount(String platformSalesAmount) {
        this.platformSalesAmount = platformSalesAmount;
    }

    public String getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(String profitMargin) {
        this.profitMargin = profitMargin;
    }
}
