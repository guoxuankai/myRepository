package com.rondaful.cloud.seller.entity.aliexpress;

import java.util.Date;

/**
 * 同步商品列表对象
 * @author chenhan
 *
 */
public class AliexpressProductModel {


    private static final long serialVersionUID = 1471741119657598142L;

    private Date couponEndDate;

    private Date couponStartDate;

    private String currencyCode;

    private Long freightTemplateId;

    private Date gmtCreate;

    private Date gmtModified;

    private Long groupId;

    private String imageURLs;

    private String ownerMemberId;

    private Long ownerMemberSeq;

    private Long productId;

    private String productMaxPrice;

    private String productMinPrice;

    private String src;

    private String subject;

    private String wsDisplay;

    private Date wsOfflineDate;

    public Date getCouponEndDate() {
        return couponEndDate;
    }

    public void setCouponEndDate(Date couponEndDate) {
        this.couponEndDate = couponEndDate;
    }

    public Date getCouponStartDate() {
        return couponStartDate;
    }

    public void setCouponStartDate(Date couponStartDate) {
        this.couponStartDate = couponStartDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getFreightTemplateId() {
        return freightTemplateId;
    }

    public void setFreightTemplateId(Long freightTemplateId) {
        this.freightTemplateId = freightTemplateId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getImageURLs() {
        return imageURLs;
    }

    public void setImageURLs(String imageURLs) {
        this.imageURLs = imageURLs;
    }

    public String getOwnerMemberId() {
        return ownerMemberId;
    }

    public void setOwnerMemberId(String ownerMemberId) {
        this.ownerMemberId = ownerMemberId;
    }

    public Long getOwnerMemberSeq() {
        return ownerMemberSeq;
    }

    public void setOwnerMemberSeq(Long ownerMemberSeq) {
        this.ownerMemberSeq = ownerMemberSeq;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductMaxPrice() {
        return productMaxPrice;
    }

    public void setProductMaxPrice(String productMaxPrice) {
        this.productMaxPrice = productMaxPrice;
    }

    public String getProductMinPrice() {
        return productMinPrice;
    }

    public void setProductMinPrice(String productMinPrice) {
        this.productMinPrice = productMinPrice;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getWsDisplay() {
        return wsDisplay;
    }

    public void setWsDisplay(String wsDisplay) {
        this.wsDisplay = wsDisplay;
    }

    public Date getWsOfflineDate() {
        return wsOfflineDate;
    }

    public void setWsOfflineDate(Date wsOfflineDate) {
        this.wsOfflineDate = wsOfflineDate;
    }
}
