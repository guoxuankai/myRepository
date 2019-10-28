package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

public class EbayPublishListingVariant implements Serializable {
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "刊登数据主键id")
    private Integer listingId;

    @ApiModelProperty(value = "品莲sku")
    private String plSku;

    @ApiModelProperty(value = "平台sku ")
    private String platformSku;

    @ApiModelProperty(value = "价格")
    private BigDecimal startPrice;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "")
    private String upc;

    @ApiModelProperty(value = "")
    private String ean;

    @ApiModelProperty(value = "")
    private String mpn;

    @ApiModelProperty(value = "")
    private String isbn;

    @ApiModelProperty(value = "productReferenceID")
    private String productReferenceID;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationTime;
    
    private String multiattribute;//变体数据选择值

    @ApiModelProperty(value = "图片(数据冗余)")
    private String picture;

    @ApiModelProperty(value = "成本价")
    private BigDecimal estimatedFreight;

    @ApiModelProperty(value = "预估利润")
    private BigDecimal forecastProfits;

    @ApiModelProperty(value = "平台佣金")
    private BigDecimal platformCommission;

    @ApiModelProperty(value = "运费")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "其他费用")
    private BigDecimal otherFee;

    @ApiModelProperty(value = "其他")
    private BigDecimal otherFee1;

    @ApiModelProperty(value = "其他")
    private BigDecimal otherFee2;

    @ApiModelProperty(value = "其他")
    private BigDecimal otherFee3;

    @ApiModelProperty(value = "美元价格(显示字段)")
    private String commodityPriceUs;

    @ApiModelProperty(value = "库存数量(显示字段)")
    private Long availableQty;

    @ApiModelProperty(value = "显示状态 0正常1下架2侵权3缺货4低于预警(显示字段)")
    private Integer showStatus;
    @ApiModelProperty(value = "组合商品")
    private List<EbayPublishListingVariantSkus> listVariantSkus;

    public String getCommodityPriceUs() {
        return commodityPriceUs;
    }

    public void setCommodityPriceUs(String commodityPriceUs) {
        this.commodityPriceUs = commodityPriceUs;
    }

    public Long getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Long availableQty) {
        this.availableQty = availableQty;
    }

    public Integer getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(Integer showStatus) {
        this.showStatus = showStatus;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public String getPlSku() {
        return plSku;
    }

    public void setPlSku(String plSku) {
        this.plSku = plSku == null ? null : plSku.trim();
    }

    public String getPlatformSku() {
        return platformSku;
    }

    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku == null ? null : platformSku.trim();
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc == null ? null : upc.trim();
    }

    public String getMpn() {
        return mpn;
    }

    public void setMpn(String mpn) {
        this.mpn = mpn == null ? null : mpn.trim();
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

	public String getMultiattribute() {
		return multiattribute;
	}

	public void setMultiattribute(String multiattribute) {
		this.multiattribute = multiattribute;
	}

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getEstimatedFreight() {
        return estimatedFreight;
    }

    public void setEstimatedFreight(BigDecimal estimatedFreight) {
        this.estimatedFreight = estimatedFreight;
    }

    public BigDecimal getForecastProfits() {
        return forecastProfits;
    }

    public void setForecastProfits(BigDecimal forecastProfits) {
        this.forecastProfits = forecastProfits;
    }

    public BigDecimal getPlatformCommission() {
        return platformCommission;
    }

    public void setPlatformCommission(BigDecimal platformCommission) {
        this.platformCommission = platformCommission;
    }

    public BigDecimal getFreightFee() {
        return freightFee;
    }

    public void setFreightFee(BigDecimal freightFee) {
        this.freightFee = freightFee;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getOtherFee1() {
        return otherFee1;
    }

    public void setOtherFee1(BigDecimal otherFee1) {
        this.otherFee1 = otherFee1;
    }

    public BigDecimal getOtherFee2() {
        return otherFee2;
    }

    public void setOtherFee2(BigDecimal otherFee2) {
        this.otherFee2 = otherFee2;
    }

    public BigDecimal getOtherFee3() {
        return otherFee3;
    }

    public void setOtherFee3(BigDecimal otherFee3) {
        this.otherFee3 = otherFee3;
    }

    public String getProductReferenceID() {
        return productReferenceID;
    }

    public void setProductReferenceID(String productReferenceID) {
        this.productReferenceID = productReferenceID;
    }

    public List<EbayPublishListingVariantSkus> getListVariantSkus() {
        return listVariantSkus;
    }

    public void setListVariantSkus(List<EbayPublishListingVariantSkus> listVariantSkus) {
        this.listVariantSkus = listVariantSkus;
    }
}