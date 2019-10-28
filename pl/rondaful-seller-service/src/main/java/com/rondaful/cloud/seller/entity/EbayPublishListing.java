//package com.rondaful.cloud.seller.entity;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.Date;
//
//import org.springframework.format.annotation.DateTimeFormat;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//
//import io.swagger.annotations.ApiModelProperty;
//
//public class EbayPublishListing implements Serializable {
//	@ApiModelProperty(value = "主键id")
//    private Integer id;
//
//    @ApiModelProperty(value = "物品刊登成功后的id")
//    private String itemid;
//
//    @ApiModelProperty(value = "刊登站点")
//    private String site;
//
//    @ApiModelProperty(value = "标题")
//    private String title;
//
//    @ApiModelProperty(value = "子标题")
//    private String subTitle;
//
//    @ApiModelProperty(value = "备注")
//    private String remarks;
//
//    @ApiModelProperty(value = "1=草稿,2=刊登中,3=已下线,4=在线,5=刊登失败,6=删除")
//    private Integer status;
//
//    @ApiModelProperty(value = "图片集合,多张用逗号分割")
//    private String picture;
//
//    @ApiModelProperty(value = "产品分类1")
//    private String productCategory1;
//
//    @ApiModelProperty(value = "产品分类2")
//    private String productCategory2;
//
//    @ApiModelProperty(value = "店铺1")
//    private String storeCategory1;
//
//    @ApiModelProperty(value = "店铺2")
//    private String storeCategory2;
//
//    @ApiModelProperty(value = "刊登类型(1=单属性一口价 2=多属性一口价 3=拍卖)")
//    private Integer listingType;
//
//    @ApiModelProperty(value = "刊登天数")
//    private String listingDuration;
//
//    @ApiModelProperty(value = "产品信息详情 如upc epid")
//    private String productListingDetails;
//
//    @ApiModelProperty(value = "平台sku")
//    private String platformSku;
//
//    @ApiModelProperty(value = "品莲sku")
//    private String plSku;
//
//    @ApiModelProperty(value = "品莲spu")
//    private String plSpu;
//
//    @ApiModelProperty(value = "数量")
//    private Integer quantity;
//
//    @ApiModelProperty(value = "单属性时的初始价格  拍卖时的起拍价 ")
//    private BigDecimal startPrice;
//
//    @ApiModelProperty(value = "是否接受议价(true 接受，false 不接受)")
//    private Boolean bestOfferEnabled;
//
//    @ApiModelProperty(value = "自动接受价格")
//    private BigDecimal autoAcceptPrice;
//
//    @ApiModelProperty(value = "自动拒绝价格")
//    private BigDecimal minimumBestOfferPrice;
//
//    @ApiModelProperty(value = "物品状态")
//    private String conditionId;
//
//    @ApiModelProperty(value = "物品状况描述")
//    private String conditionDescription;
//
//    @ApiModelProperty(value = "描述")
//    private String description;
//
//    @ApiModelProperty(value = "一口价")
//    private BigDecimal buyItNowPrice;
//
//    @ApiModelProperty(value = "最低价")
//    private BigDecimal floorPrice;
//
//    @ApiModelProperty(value = "paypal帐号")
//    private String paypal;
//
//    @ApiModelProperty(value = "支付选项")
//    private String paymentOption;
//
//    @ApiModelProperty(value = "支付说明")
//    private String paymentDetail;
//
//    @ApiModelProperty(value = "卖家要求 true 允许所有买家购买 false 禁止以下买家购买我的商品")
//    private Boolean disableBuyerRequirements;
//
//    @ApiModelProperty(value = "发货时间天数")
//    private Integer dispatchTimeMax;
//
//    @ApiModelProperty(value = "物品所在地")
//    private String local;
//
//    @ApiModelProperty(value = "物品所在国家")
//    private String country;
//
//    @ApiModelProperty(value = "邮编")
//    private String zipCode;
//
//    @ApiModelProperty(value = "退货政策")
//    private String returnPolicy;
//
//    @ApiModelProperty(value = "刊登帐号")
//    private String publishAccount;
//
//    @ApiModelProperty(value = "产品描述")
//    private String listingDesc;
//
//    @ApiModelProperty(value = "产品描述原来的描述")
//    private String listingDescOriginal;
//
//    @ApiModelProperty(value = "授权id")
//    private Integer empowerId;
//
//    @ApiModelProperty(value = "卖家")
//    private String seller;
//
//    @ApiModelProperty(value = "刊登站点所用的币种")
//	private String currency;
//
//    @ApiModelProperty(value = "创建时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date creationTime;
//
//    @ApiModelProperty(value = "修改时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date updateTime;
//
//    @ApiModelProperty(value = "发布时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date publishTime;
//
//    @ApiModelProperty(value = "下架时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date endTimes;
//
//    @ApiModelProperty(value = "上线时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date onlineTime;
//
//    @ApiModelProperty(value = "前端回写数据使用,不参与业务 ")
//    private String ext;
//
//    //版本2.2
//    // GalleryTypeCode  None Featured Gallery Plus CustomCode
//    @ApiModelProperty(value = "橱窗展示 None Featured Gallery Plus CustomCode")
//    private String galleryTypeCode;
//    //AutoPay
//    @ApiModelProperty(value = "立即付款 ture false")
//    private Boolean autoPay;
//
//    private Long createId;//创建人id
//    private String createName;//创建人名称
//    @ApiModelProperty(value = "更新状态 1更新中2更新成功3更新失败")
//    private Integer updateStatus;//1更新中2更新成功3更新失败
//    @ApiModelProperty(value = "是否是平台listing 0是历史刊登数据 1是新刊登 2Ebay平台")
//    private Integer platformListing;//
//
//    //
//
//    /**
//     * This field was generated by MyBatis Generator.
//     * This field corresponds to the database table ebay_publish_listing
//     *
//     * @mbg.generated
//     */
//    private static final long serialVersionUID = 1L;
//
//    public Integer getUpdateStatus() {
//        return updateStatus;
//    }
//
//    public void setUpdateStatus(Integer updateStatus) {
//        this.updateStatus = updateStatus;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getItemid() {
//        return itemid;
//    }
//
//    public void setItemid(String itemid) {
//        this.itemid = itemid == null ? null : itemid.trim();
//    }
//
//    public String getSite() {
//        return site;
//    }
//
//    public void setSite(String site) {
//        this.site = site == null ? null : site.trim();
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title == null ? null : title.trim();
//    }
//
//    public String getSubTitle() {
//        return subTitle;
//    }
//
//    public void setSubTitle(String subTitle) {
//        this.subTitle = subTitle == null ? null : subTitle.trim();
//    }
//
//    public String getRemarks() {
//        return remarks;
//    }
//
//    public void setRemarks(String remarks) {
//        this.remarks = remarks == null ? null : remarks.trim();
//    }
//
//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }
//
//    public String getPicture() {
//        return picture;
//    }
//
//    public void setPicture(String picture) {
//        this.picture = picture == null ? null : picture.trim();
//    }
//
//    public String getProductCategory1() {
//        return productCategory1;
//    }
//
//    public void setProductCategory1(String productCategory1) {
//        this.productCategory1 = productCategory1 == null ? null : productCategory1.trim();
//    }
//
//    public String getProductCategory2() {
//        return productCategory2;
//    }
//
//    public void setProductCategory2(String productCategory2) {
//        this.productCategory2 = productCategory2 == null ? null : productCategory2.trim();
//    }
//
//    public String getStoreCategory1() {
//        return storeCategory1;
//    }
//
//    public void setStoreCategory1(String storeCategory1) {
//        this.storeCategory1 = storeCategory1 == null ? null : storeCategory1.trim();
//    }
//
//    public String getStoreCategory2() {
//        return storeCategory2;
//    }
//
//    public void setStoreCategory2(String storeCategory2) {
//        this.storeCategory2 = storeCategory2 == null ? null : storeCategory2.trim();
//    }
//
//    public Integer getListingType() {
//        return listingType;
//    }
//
//    public void setListingType(Integer listingType) {
//        this.listingType = listingType;
//    }
//
//    public String getListingDuration() {
//        return listingDuration;
//    }
//
//    public void setListingDuration(String listingDuration) {
//        this.listingDuration = listingDuration == null ? null : listingDuration.trim();
//    }
//
//    public String getProductListingDetails() {
//        return productListingDetails;
//    }
//
//    public void setProductListingDetails(String productListingDetails) {
//        this.productListingDetails = productListingDetails == null ? null : productListingDetails.trim();
//    }
//
//    public String getPlatformSku() {
//        return platformSku;
//    }
//
//    public void setPlatformSku(String platformSku) {
//        this.platformSku = platformSku == null ? null : platformSku.trim();
//    }
//
//    public String getPlSku() {
//        return plSku;
//    }
//
//    public void setPlSku(String plSku) {
//        this.plSku = plSku == null ? null : plSku.trim();
//    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }
//
//    public BigDecimal getStartPrice() {
//        return startPrice;
//    }
//
//    public void setStartPrice(BigDecimal startPrice) {
//        this.startPrice = startPrice;
//    }
//
//    public Boolean getBestOfferEnabled() {
//        return bestOfferEnabled;
//    }
//
//    public void setBestOfferEnabled(Boolean bestOfferEnabled) {
//        this.bestOfferEnabled = bestOfferEnabled;
//    }
//
//    public BigDecimal getAutoAcceptPrice() {
//        return autoAcceptPrice;
//    }
//
//    public void setAutoAcceptPrice(BigDecimal autoAcceptPrice) {
//        this.autoAcceptPrice = autoAcceptPrice;
//    }
//
//    public BigDecimal getMinimumBestOfferPrice() {
//        return minimumBestOfferPrice;
//    }
//
//    public void setMinimumBestOfferPrice(BigDecimal minimumBestOfferPrice) {
//        this.minimumBestOfferPrice = minimumBestOfferPrice;
//    }
//
//    public String getConditionId() {
//        return conditionId;
//    }
//
//    public void setConditionId(String conditionId) {
//        this.conditionId = conditionId == null ? null : conditionId.trim();
//    }
//
//    public String getConditionDescription() {
//        return conditionDescription;
//    }
//
//    public void setConditionDescription(String conditionDescription) {
//        this.conditionDescription = conditionDescription == null ? null : conditionDescription.trim();
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description == null ? null : description.trim();
//    }
//
//    public BigDecimal getBuyItNowPrice() {
//        return buyItNowPrice;
//    }
//
//    public void setBuyItNowPrice(BigDecimal buyItNowPrice) {
//        this.buyItNowPrice = buyItNowPrice;
//    }
//
//    public BigDecimal getFloorPrice() {
//        return floorPrice;
//    }
//
//    public void setFloorPrice(BigDecimal floorPrice) {
//        this.floorPrice = floorPrice;
//    }
//
//    public String getPaypal() {
//        return paypal;
//    }
//
//    public void setPaypal(String paypal) {
//        this.paypal = paypal == null ? null : paypal.trim();
//    }
//
//    public String getPaymentOption() {
//        return paymentOption;
//    }
//
//    public void setPaymentOption(String paymentOption) {
//        this.paymentOption = paymentOption == null ? null : paymentOption.trim();
//    }
//
//    public String getPaymentDetail() {
//        return paymentDetail;
//    }
//
//    public void setPaymentDetail(String paymentDetail) {
//        this.paymentDetail = paymentDetail == null ? null : paymentDetail.trim();
//    }
//
//    public Boolean getDisableBuyerRequirements() {
//        return disableBuyerRequirements;
//    }
//
//    public void setDisableBuyerRequirements(Boolean disableBuyerRequirements) {
//        this.disableBuyerRequirements = disableBuyerRequirements;
//    }
//
//    public Integer getDispatchTimeMax() {
//        return dispatchTimeMax;
//    }
//
//    public void setDispatchTimeMax(Integer dispatchTimeMax) {
//        this.dispatchTimeMax = dispatchTimeMax;
//    }
//
//    public String getLocal() {
//        return local;
//    }
//
//    public void setLocal(String local) {
//        this.local = local == null ? null : local.trim();
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country == null ? null : country.trim();
//    }
//
//    public String getZipCode() {
//        return zipCode;
//    }
//
//    public void setZipCode(String zipCode) {
//        this.zipCode = zipCode == null ? null : zipCode.trim();
//    }
//
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
//    public Date getCreationTime() {
//        return creationTime;
//    }
//
//    public void setCreationTime(Date creationTime) {
//        this.creationTime = creationTime;
//    }
//
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
//    public Date getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(Date updateTime) {
//        this.updateTime = updateTime;
//    }
//
//    public String getReturnPolicy() {
//        return returnPolicy;
//    }
//
//    public void setReturnPolicy(String returnPolicy) {
//        this.returnPolicy = returnPolicy == null ? null : returnPolicy.trim();
//    }
//
//    public String getPublishAccount() {
//        return publishAccount;
//    }
//
//    public void setPublishAccount(String publishAccount) {
//        this.publishAccount = publishAccount;
//    }
//
//    public String getListingDesc() {
//        return listingDesc;
//    }
//
//    public void setListingDesc(String listingDesc) {
//        this.listingDesc = listingDesc == null ? null : listingDesc.trim();
//    }
//
//	public Integer getEmpowerId() {
//		return empowerId;
//	}
//
//	public void setEmpowerId(Integer empowerId) {
//		this.empowerId = empowerId;
//	}
//
//	public String getSeller() {
//		return seller;
//	}
//
//	public void setSeller(String seller) {
//		this.seller = seller;
//	}
//
//	public String getCurrency() {
//		return currency;
//	}
//
//	public void setCurrency(String currency) {
//		this.currency = currency;
//	}
//
//	public Date getPublishTime() {
//		return publishTime;
//	}
//
//	public void setPublishTime(Date publishTime) {
//		this.publishTime = publishTime;
//	}
//
//	public Date getOnlineTime() {
//		return onlineTime;
//	}
//
//	public void setOnlineTime(Date onlineTime) {
//		this.onlineTime = onlineTime;
//	}
//
//	public Date getEndTimes() {
//		return endTimes;
//	}
//
//	public void setEndTimes(Date endTimes) {
//		this.endTimes = endTimes;
//	}
//
//	public String getExt() {
//		return ext;
//	}
//
//	public void setExt(String ext) {
//		this.ext = ext;
//	}
//
//    public Boolean getAutoPay() {
//        return autoPay;
//    }
//
//    public void setAutoPay(Boolean autoPay) {
//        this.autoPay = autoPay;
//    }
//
//    public String getGalleryTypeCode() {
//        return galleryTypeCode;
//    }
//
//    public void setGalleryTypeCode(String galleryTypeCode) {
//        this.galleryTypeCode = galleryTypeCode;
//    }
//
//    public Long getCreateId() {
//        return createId;
//    }
//
//    public void setCreateId(Long createId) {
//        this.createId = createId;
//    }
//
//    public String getCreateName() {
//        return createName;
//    }
//
//    public void setCreateName(String createName) {
//        this.createName = createName;
//    }
//
//    public String getListingDescOriginal() {
//        return listingDescOriginal;
//    }
//
//    public void setListingDescOriginal(String listingDescOriginal) {
//        this.listingDescOriginal = listingDescOriginal;
//    }
//
//    public Integer getPlatformListing() {
//        return platformListing;
//    }
//
//    public void setPlatformListing(Integer platformListing) {
//        this.platformListing = platformListing;
//    }
//
//    public String getPlSpu() {
//        return plSpu;
//    }
//
//    public void setPlSpu(String plSpu) {
//        this.plSpu = plSpu;
//    }
//}