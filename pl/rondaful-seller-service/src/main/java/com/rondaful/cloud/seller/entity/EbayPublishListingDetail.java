package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 物品刊登详情表
 * 实体类对应的数据表为：  ebay_publish_listing_detail
 * @author chenhan
 * @date 2019-06-18 16:05:45
 */
@ApiModel(value ="EbayPublishListingDetail")
public class EbayPublishListingDetail implements Serializable {
    private static final long serialVersionUID = -2239730776928969163L;
    @ApiModelProperty(value = "注键id")
    private Long id;

    @ApiModelProperty(value = "刊登id")
    private Long listingId;

    @ApiModelProperty(value = "店铺1")
    private String storeCategory1;

    @ApiModelProperty(value = "店铺2")
    private String storeCategory2;

    @ApiModelProperty(value = "刊登天数")
    private String listingDuration;

    @ApiModelProperty(value = "物品状态")
    private String conditionId;

    @ApiModelProperty(value = "物品状况描述")
    private String conditionDescription;

    @ApiModelProperty(value = "paypal帐号")
    private String paypal;

    @ApiModelProperty(value = "支付选项")
    private String paymentOption;

    @ApiModelProperty(value = "支付说明")
    private String paymentDetail;

    @ApiModelProperty(value = "卖家要求 true 允许所有买家购买 false 禁止以下买家购买我的商品")
    private Boolean disableBuyerRequirements;

    @ApiModelProperty(value = "物品所在地")
    private String local;

    @ApiModelProperty(value = "物品所在国家")
    private String country;

    @ApiModelProperty(value = "邮编")
    private String zipCode;

    @ApiModelProperty(value = "刊登站点所用的币种")
    private String currency;

    @ApiModelProperty(value = "橱窗展示 None Featured Gallery Plus CustomCode")
    private String galleryTypeCode;

    @ApiModelProperty(value = "立即付款")
    private Boolean autoPay;

    @ApiModelProperty(value = "风格id")
    private Long styleId;

    @ApiModelProperty(value = "模板ID{'template1':1,'template2':2}")
    private String templateIds;

    @ApiModelProperty(value = "转换汇率的国家")
    private String switchCountry;

    @ApiModelProperty(value = "国家汇率")
    private BigDecimal exchangeRates;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "产品描述原始数据")
    private String listingDescOriginal;

    @ApiModelProperty(value = "产品描述")
    private String listingDesc;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "收货国家")
    private String shipCountry;

    @ApiModelProperty(value = "物流类型")
    private String logisticsType;

    @ApiModelProperty(value = "发货仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "物流时效")
    private String logisticsAging;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public String getStoreCategory1() {
        return storeCategory1;
    }

    public void setStoreCategory1(String storeCategory1) {
        this.storeCategory1 = storeCategory1 == null ? null : storeCategory1.trim();
    }

    public String getStoreCategory2() {
        return storeCategory2;
    }

    public void setStoreCategory2(String storeCategory2) {
        this.storeCategory2 = storeCategory2 == null ? null : storeCategory2.trim();
    }

    public String getListingDuration() {
        return listingDuration;
    }

    public void setListingDuration(String listingDuration) {
        this.listingDuration = listingDuration == null ? null : listingDuration.trim();
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId == null ? null : conditionId.trim();
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public void setConditionDescription(String conditionDescription) {
        this.conditionDescription = conditionDescription == null ? null : conditionDescription.trim();
    }

    public String getPaypal() {
        return paypal;
    }

    public void setPaypal(String paypal) {
        this.paypal = paypal == null ? null : paypal.trim();
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption == null ? null : paymentOption.trim();
    }

    public String getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(String paymentDetail) {
        this.paymentDetail = paymentDetail == null ? null : paymentDetail.trim();
    }

    public Boolean getDisableBuyerRequirements() {
        return disableBuyerRequirements;
    }

    public void setDisableBuyerRequirements(Boolean disableBuyerRequirements) {
        this.disableBuyerRequirements = disableBuyerRequirements;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local == null ? null : local.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode == null ? null : zipCode.trim();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public String getGalleryTypeCode() {
        return galleryTypeCode;
    }

    public void setGalleryTypeCode(String galleryTypeCode) {
        this.galleryTypeCode = galleryTypeCode == null ? null : galleryTypeCode.trim();
    }

    public Boolean getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(Boolean autoPay) {
        this.autoPay = autoPay;
    }

    public Long getStyleId() {
        return styleId;
    }

    public void setStyleId(Long styleId) {
        this.styleId = styleId;
    }

    public String getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(String templateIds) {
        this.templateIds = templateIds == null ? null : templateIds.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSwitchCountry() {
        return switchCountry;
    }

    public void setSwitchCountry(String switchCountry) {
        this.switchCountry = switchCountry == null ? null : switchCountry.trim();
    }

    public BigDecimal getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(BigDecimal exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getListingDescOriginal() {
        return listingDescOriginal;
    }

    public void setListingDescOriginal(String listingDescOriginal) {
        this.listingDescOriginal = listingDescOriginal;
    }

    public String getListingDesc() {
        return listingDesc;
    }

    public void setListingDesc(String listingDesc) {
        this.listingDesc = listingDesc;
    }
    public String getShipCountry() {
        return shipCountry;
    }

    public void setShipCountry(String shipCountry) {
        this.shipCountry = shipCountry == null ? null : shipCountry.trim();
    }

    public String getLogisticsType() {
        return logisticsType;
    }

    public void setLogisticsType(String logisticsType) {
        this.logisticsType = logisticsType == null ? null : logisticsType.trim();
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode == null ? null : warehouseCode.trim();
    }

    public String getLogisticsAging() {
        return logisticsAging;
    }

    public void setLogisticsAging(String logisticsAging) {
        this.logisticsAging = logisticsAging == null ? null : logisticsAging.trim();
    }
}