package com.rondaful.cloud.order.model.dto.syncorder;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Blade
 * @date 2019-07-18 14:40:06
 **/
public class PreCovertEbayOrderDTO implements Serializable {

    private static final long serialVersionUID = 6863315214502278990L;

    @ApiModelProperty(value = "ebay平台订单项集合")
    private List<PreCovertEbayOrderDetailDTO> preCovertEbayOrderDetailDTOList = new ArrayList<>();

    @ApiModelProperty(value = "卖家品连ID")
    private Integer sellerPlId;

    @ApiModelProperty(value = "卖家品连账号(关联用户管理模块)")
    private String sellerPlAccount;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "eBay平台订单的ID")
    private String orderId;

    @ApiModelProperty(value = "平台订单总价")
    private String platformTotal;

    @ApiModelProperty(value = "订单创建时间")
    private String orderTime;

    @ApiModelProperty(value = "订单编号:卖家店铺名-recordNumber")
    private String recordNumber;

    @ApiModelProperty(value = "买家")
    private String buyerUserId;

    @ApiModelProperty(value = "全球配送订单发货的唯一编号")
    private String referenceId;

    @ApiModelProperty(value = "买家留言")
    private String buyerCheckoutMessage;

    @ApiModelProperty(value = "卖家店铺授权ID")
    private Integer empowerId;

    @ApiModelProperty(value = "卖家平台账号(授权店铺名)")
    private String platformSellerAccount;

    @ApiModelProperty(value = "买家email:email1#emai2")
    private String shipToEmail;

    @ApiModelProperty(value = "付款金额[买家最终付款金额]")
    private String shippingServiceCostStr;

    @ApiModelProperty(value = "收货人姓名")
    private String shipToName;

    @ApiModelProperty(value = "收货人电话")
    private String shipToPhone;

    @ApiModelProperty(value = "国家代码")
    private String shipToCountry;

    @ApiModelProperty(value = "收货目的地/国家名")
    private String shipToCountryName;

    @ApiModelProperty(value = "收货省/州名")
    private String shipToState;

    @ApiModelProperty(value = "收货城市")
    private String shipToCity;

    @ApiModelProperty(value = "收货地址1")
    private String shipToAddrStreet1;

    @ApiModelProperty(value = "收货地址2")
    private String shipToAddrStreet2;

    @ApiModelProperty(value = "收货邮编")
    private String shipToPostalCode;

    @ApiModelProperty(value = "订单最迟发货时间")
    private String deliverDeadline;

    public List<PreCovertEbayOrderDetailDTO> getPreCovertEbayOrderDetailDTOList() {
        return preCovertEbayOrderDetailDTOList;
    }

    public void setPreCovertEbayOrderDetailDTOList(List<PreCovertEbayOrderDetailDTO> preCovertEbayOrderDetailDTOList) {
        this.preCovertEbayOrderDetailDTOList = preCovertEbayOrderDetailDTOList;
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public Integer getSellerPlId() {
        return sellerPlId;
    }

    public void setSellerPlId(Integer sellerPlId) {
        this.sellerPlId = sellerPlId;
    }

    public String getSellerPlAccount() {
        return sellerPlAccount;
    }

    public void setSellerPlAccount(String sellerPlAccount) {
        this.sellerPlAccount = sellerPlAccount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPlatformTotal() {
        return platformTotal;
    }

    public void setPlatformTotal(String platformTotal) {
        this.platformTotal = platformTotal;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getBuyerCheckoutMessage() {
        return buyerCheckoutMessage;
    }

    public void setBuyerCheckoutMessage(String buyerCheckoutMessage) {
        this.buyerCheckoutMessage = buyerCheckoutMessage;
    }

    public Integer getEmpowerId() {
        return empowerId;
    }

    public void setEmpowerId(Integer empowerId) {
        this.empowerId = empowerId;
    }

    public String getPlatformSellerAccount() {
        return platformSellerAccount;
    }

    public void setPlatformSellerAccount(String platformSellerAccount) {
        this.platformSellerAccount = platformSellerAccount;
    }

    public String getShipToEmail() {
        return shipToEmail;
    }

    public void setShipToEmail(String shipToEmail) {
        this.shipToEmail = shipToEmail;
    }

    public String getShippingServiceCostStr() {
        return shippingServiceCostStr;
    }

    public void setShippingServiceCostStr(String shippingServiceCostStr) {
        this.shippingServiceCostStr = shippingServiceCostStr;
    }

    public String getShipToName() {
        return shipToName;
    }

    public void setShipToName(String shipToName) {
        this.shipToName = shipToName;
    }

    public String getShipToPhone() {
        return shipToPhone;
    }

    public void setShipToPhone(String shipToPhone) {
        this.shipToPhone = shipToPhone;
    }

    public String getShipToCountry() {
        return shipToCountry;
    }

    public void setShipToCountry(String shipToCountry) {
        this.shipToCountry = shipToCountry;
    }

    public String getShipToCountryName() {
        return shipToCountryName;
    }

    public void setShipToCountryName(String shipToCountryName) {
        this.shipToCountryName = shipToCountryName;
    }

    public String getShipToState() {
        return shipToState;
    }

    public void setShipToState(String shipToState) {
        this.shipToState = shipToState;
    }

    public String getShipToCity() {
        return shipToCity;
    }

    public void setShipToCity(String shipToCity) {
        this.shipToCity = shipToCity;
    }

    public String getShipToAddrStreet1() {
        return shipToAddrStreet1;
    }

    public void setShipToAddrStreet1(String shipToAddrStreet1) {
        this.shipToAddrStreet1 = shipToAddrStreet1;
    }

    public String getShipToAddrStreet2() {
        return shipToAddrStreet2;
    }

    public void setShipToAddrStreet2(String shipToAddrStreet2) {
        this.shipToAddrStreet2 = shipToAddrStreet2;
    }

    public String getShipToPostalCode() {
        return shipToPostalCode;
    }

    public void setShipToPostalCode(String shipToPostalCode) {
        this.shipToPostalCode = shipToPostalCode;
    }

    public String getDeliverDeadline() {
        return deliverDeadline;
    }

    public void setDeliverDeadline(String deliverDeadline) {
        this.deliverDeadline = deliverDeadline;
    }
}
