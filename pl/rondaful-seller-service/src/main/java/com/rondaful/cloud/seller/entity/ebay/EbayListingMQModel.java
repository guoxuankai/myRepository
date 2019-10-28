package com.rondaful.cloud.seller.entity.ebay;


public class EbayListingMQModel {

    private Long empowerId;
    private Long userId;
    private String userName;
    private String sellerId;
    private String itemId;

    public Long getEmpowerId() {
        return empowerId;
    }

    public void setEmpowerId(Long empowerId) {
        this.empowerId = empowerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
