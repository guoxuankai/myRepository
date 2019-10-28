package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-09 14:03
 * 包名: com.rondaful.cloud.order.entity
 * 描述:
 */
@ApiModel(value ="PlatformExport")
public class PlatformExport {
    private String orderId;
    private String date;
    private String seller;
    private String sellerId;
    private String platformSku;
    private String itemPrice;
    private String itemCount;
    private String price;
    private String name;
    private String country;
    private String province;
    private String city;
    private String address;
    private String postcode ;
    private String phone ;
    private String email ;
    private String trackId ;
    private String orderStatus ;
    private String processStatus ;

    @Override
    public String toString() {
        return "Export{" +
                "orderId='" + orderId + '\'' +
                ", date='" + date + '\'' +
                ", seller='" + seller + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", platformSku='" + platformSku + '\'' +
                ", itemPrice='" + itemPrice + '\'' +
                ", itemCount='" + itemCount + '\'' +
                ", price='" + price + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", postcode='" + postcode + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", trackId='" + trackId + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", processStatus='" + processStatus + '\'' +
                '}';
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPlatformSku() {
        return platformSku;
    }

    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
}
