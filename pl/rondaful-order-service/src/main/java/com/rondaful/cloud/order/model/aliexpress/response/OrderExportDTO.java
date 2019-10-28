package com.rondaful.cloud.order.model.aliexpress.response;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/4/12
 * @Description:
 */
@ExcelTarget("OrderExportDTO")
public class OrderExportDTO implements Serializable {
    private static final long serialVersionUID = -1338653857982028384L;

    @Excel(name = "parentOrderId",width = 16)
    private String parentOrderId;
    @Excel(name = "orderId",width = 16)
    private String orderId;
    @Excel(name = "OrderCreationTime",format = "yyyy-MM-dd HH:mm:ss",width = 18)
    private Date gmtCreate;
    @Excel(name = "shopName",width = 16)
    private String sellerSignerFullName;
    @Excel(name = "platformSku",width = 16)
    private String skuCode;
    @Excel(name = "commodityPrice",width = 16)
    private String amount;
    @Excel(name = "theNumber",width = 16)
    private Integer productCount;
    @Excel(name = "payAmount",width = 16)
    private String payAmountBySettlementCur;
    @Excel(name = "contactPerson",width = 16)
    private String contactPerson;
    @Excel(name = "country",width = 16)
    private String country;
    @Excel(name = "province",width = 16)
    private String province;
    @Excel(name = "city",width = 16)
    private String city;
    @Excel(name = "address",width = 16)
    private String address;
    @Excel(name = "zipCode",width = 16)
    private String zip;
    @Excel(name = "mobile",width = 16)
    private String mobileNo;
    @Excel(name = "orderStatus",width = 16)
    private String orderStatus;
    @Excel(name = "processStatus",width = 16)
    private String plProcessStatus;

    public String getParentOrderId() {
        return parentOrderId;
    }

    public void setParentOrderId(String parentOrderId) {
        this.parentOrderId = parentOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getSellerSignerFullName() {
        return sellerSignerFullName;
    }

    public void setSellerSignerFullName(String sellerSignerFullName) {
        this.sellerSignerFullName = sellerSignerFullName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public String getPayAmountBySettlementCur() {
        return payAmountBySettlementCur;
    }

    public void setPayAmountBySettlementCur(String payAmountBySettlementCur) {
        this.payAmountBySettlementCur = payAmountBySettlementCur;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPlProcessStatus() {
        return plProcessStatus;
    }

    public void setPlProcessStatus(String plProcessStatus) {
        this.plProcessStatus = plProcessStatus;
    }
}
