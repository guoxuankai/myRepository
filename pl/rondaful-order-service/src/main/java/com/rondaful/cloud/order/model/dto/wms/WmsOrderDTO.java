package com.rondaful.cloud.order.model.dto.wms;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Blade
 * @date 2019-08-09 11:39:16
 **/
public class WmsOrderDTO implements Serializable {
    private static final long serialVersionUID = 7949960642692928735L;

    @ApiModelProperty(value = "平台订单号(电商)")
    private String orderNum;

    @ApiModelProperty(value = "包裹编号", required = true)
    private String packageNum;

    @ApiModelProperty(value = "物流商Code", required = true)
    private String receiverCode;

    @ApiModelProperty("物流商名称")
    private String receiver;

    @ApiModelProperty(value = "邮寄方式Code", required = true)
    private String mailingMethodCode;

    @ApiModelProperty(value = "邮寄方式名称")
    private String mailingMethod;

    @ApiModelProperty(value = "买家")
    private String buyer;

    @ApiModelProperty(value = "货币简写")
    private String currency;

    @ApiModelProperty("平台订单的创建时间[格式:yyyy-MM-dd HH:mm:ss]")
    private String foundTime;

    @ApiModelProperty("创建人,平台订单创建人")
    private String founder;

    @ApiModelProperty(value = "包裹最迟发货时间[格式:yyyy-MM-dd HH:mm:ss]", required = true)
    private String latestDeliveryTime;

    @ApiModelProperty(value = "包裹详情,可传多个", required = true)
    private List<WmsOrderDetailDTO> orderDetailsList;

    @ApiModelProperty(value = "订单类型[1B2C订单、2调拨出库、3退供出库]", required = true)
    private String orderType;

    @ApiModelProperty(value = "商品付款时间[格式:yyyy-MM-dd HH:mm:ss]")
    private String paymentTime;

    @ApiModelProperty(value = "优先级[1.低、2.中、3.高]")
    private Integer priority;

    @ApiModelProperty(value = "收件人信息有且仅有一个", required = true)
    private WmsRecipientsDTO recipients;

    @ApiModelProperty(value = "包裹信息的备注", required = true)
    private String remark;

    @ApiModelProperty(value = "平台编码1:ebay,2:amazon,3:aliexpress,4:wish,5:other默认为5")
    private String salesChannels;

    @ApiModelProperty(value = "寄件人信息存在时，只能为一个")
    private WmsSenderDTO sender;

    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal totalMoney;

    @ApiModelProperty(value = "订单支付金额")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "仓库编码", required = true)
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(String packageNum) {
        this.packageNum = packageNum;
    }

    public String getReceiverCode() {
        return receiverCode;
    }

    public void setReceiverCode(String receiverCode) {
        this.receiverCode = receiverCode;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMailingMethodCode() {
        return mailingMethodCode;
    }

    public void setMailingMethodCode(String mailingMethodCode) {
        this.mailingMethodCode = mailingMethodCode;
    }

    public String getMailingMethod() {
        return mailingMethod;
    }

    public void setMailingMethod(String mailingMethod) {
        this.mailingMethod = mailingMethod;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFoundTime() {
        return foundTime;
    }

    public void setFoundTime(String foundTime) {
        this.foundTime = foundTime;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getLatestDeliveryTime() {
        return latestDeliveryTime;
    }

    public void setLatestDeliveryTime(String latestDeliveryTime) {
        this.latestDeliveryTime = latestDeliveryTime;
    }

    public List<WmsOrderDetailDTO> getOrderDetailsList() {
        return orderDetailsList;
    }

    public void setOrderDetailsList(List<WmsOrderDetailDTO> orderDetailsList) {
        this.orderDetailsList = orderDetailsList;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public WmsRecipientsDTO getRecipients() {
        return recipients;
    }

    public void setRecipients(WmsRecipientsDTO recipients) {
        this.recipients = recipients;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSalesChannels() {
        return salesChannels;
    }

    public void setSalesChannels(String salesChannels) {
        this.salesChannels = salesChannels;
    }

    public WmsSenderDTO getSender() {
        return sender;
    }

    public void setSender(WmsSenderDTO sender) {
        this.sender = sender;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public BigDecimal getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(BigDecimal payMoney) {
        this.payMoney = payMoney;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}
