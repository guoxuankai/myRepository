package com.rondaful.cloud.transorder.entity.ebay;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * ebay平台订单表
 * 实体类对应的数据表为：  tb_ebay_order
 * @author guoxuankai
 * @date 2019-09-21 14:58:56
 */
@ApiModel(value ="EbayOrder")
public class EbayOrder extends EbayOrderKey implements Serializable {

    @ApiModelProperty(value = "平台订单总价")
    private String total;

    @ApiModelProperty(value = "订单创建时间")
    private String createdTime;

    @ApiModelProperty(value = "付款时间")
    private String paidTime;

    @ApiModelProperty(value = "卖家平台账号")
    private String sellerUserId;

    @ApiModelProperty(value = "订单编号:卖家店铺名-recordNumber")
    private String recordNumber;

    @ApiModelProperty(value = "买家")
    private String buyerUserId;

    @ApiModelProperty(value = "买家email:email1#emai2")
    private String buyerEmail;

    @ApiModelProperty(value = "发货时间")
    private String shippedTime;

    @ApiModelProperty(value = "平台物流费：卖家填的")
    private String shippingServiceCost;

    @ApiModelProperty(value = "付款金额[买家最终付款金额]")
    private String amountPaid;

    @ApiModelProperty(value = "订单支付状态")
    private String paymentStatus;

    @ApiModelProperty(value = "订单支付方式")
    private String paymentMethod;

    @ApiModelProperty(value = "全球配送订单发货的唯一编号")
    private String referenceId;

    @ApiModelProperty(value = "订单最近修改时间")
    private String lastModifiedTime;

    @ApiModelProperty(value = "卖家email")
    private String sellerEmail;

    @ApiModelProperty(value = "买家留言")
    private String buyerCheckoutMessage;

    @ApiModelProperty(value = "收货人姓名")
    private String name;

    @ApiModelProperty(value = "收货人电话")
    private String phone;

    @ApiModelProperty(value = "国家代码")
    private String country;

    @ApiModelProperty(value = "收货目的地/国家名")
    private String countryName;

    @ApiModelProperty(value = "收货省/州名")
    private String stateOrProvince;

    @ApiModelProperty(value = "收货城市")
    private String cityName;

    @ApiModelProperty(value = "收货地址1")
    private String street1;

    @ApiModelProperty(value = "收货地址2")
    private String street2;

    @ApiModelProperty(value = "收货邮编")
    private String postalCode;

    @ApiModelProperty(value = "是否列表展示。0-不展示(订单的所有SKU转换不成功)，1-展示")
    private String isShowOnList;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "订单明细")
    private List<EbayOrderDetail> childs;

    @ApiModelProperty(value = "订单状态")
    private EbayOrderStatus orderStatus;


    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table tb_ebay_order
     *
     * @mbg.generated 2019-09-21 14:58:56
     */
    private static final long serialVersionUID = 1L;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total == null ? null : total.trim();
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime == null ? null : createdTime.trim();
    }

    public String getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(String paidTime) {
        this.paidTime = paidTime == null ? null : paidTime.trim();
    }

    public String getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId == null ? null : sellerUserId.trim();
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber == null ? null : recordNumber.trim();
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId == null ? null : buyerUserId.trim();
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail == null ? null : buyerEmail.trim();
    }

    public String getShippedTime() {
        return shippedTime;
    }

    public void setShippedTime(String shippedTime) {
        this.shippedTime = shippedTime == null ? null : shippedTime.trim();
    }

    public String getShippingServiceCost() {
        return shippingServiceCost;
    }

    public void setShippingServiceCost(String shippingServiceCost) {
        this.shippingServiceCost = shippingServiceCost == null ? null : shippingServiceCost.trim();
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid == null ? null : amountPaid.trim();
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus == null ? null : paymentStatus.trim();
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod == null ? null : paymentMethod.trim();
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId == null ? null : referenceId.trim();
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime == null ? null : lastModifiedTime.trim();
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail == null ? null : sellerEmail.trim();
    }

    public String getBuyerCheckoutMessage() {
        return buyerCheckoutMessage;
    }

    public void setBuyerCheckoutMessage(String buyerCheckoutMessage) {
        this.buyerCheckoutMessage = buyerCheckoutMessage == null ? null : buyerCheckoutMessage.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName == null ? null : countryName.trim();
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince == null ? null : stateOrProvince.trim();
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1 == null ? null : street1.trim();
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2 == null ? null : street2.trim();
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode == null ? null : postalCode.trim();
    }

    public String getIsShowOnList() {
        return isShowOnList;
    }

    public void setIsShowOnList(String isShowOnList) {
        this.isShowOnList = isShowOnList == null ? null : isShowOnList.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public List<EbayOrderDetail> getChilds() {
        return childs;
    }

    public void setChilds(List<EbayOrderDetail> childs) {
        this.childs = childs;
    }

    public EbayOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(EbayOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}