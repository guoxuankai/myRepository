package com.rondaful.cloud.seller.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;
import com.rondaful.cloud.seller.entity.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "EbayPublishListing")
public class PublishListingVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "物品刊登成功后的id")
    private String itemid;
	
	@ApiModelProperty(value = "刊登站点")
	private String site;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "子标题")
	private String subTitle;

	@ApiModelProperty(value = "备注")
	private String remarks;

	@ApiModelProperty(value = "1=草稿,2=刊登中,3=已下线,4=在线,5=刊登失败")
	private Integer status;

	@ApiModelProperty(value = "图片集合,多张用逗号分割")
	private String picture;

	@ApiModelProperty(value = "产品分类1")
	private String productCategory1;

	@ApiModelProperty(value = "产品分类2")
	private String productCategory2;

	@ApiModelProperty(value = "店铺1")
	private String storeCategory1;

	@ApiModelProperty(value = "店铺2")
	private String storeCategory2;

	@ApiModelProperty(value = "刊登类型(1=单属性一口价 2=多属性一口价 3=拍卖)")
	private Integer listingType;

	@ApiModelProperty(value = "刊登天数")
	private String listingDuration;

	@ApiModelProperty(value = "产品信息详情 如upc epid")
	private EbayPublishListingVariant productListingDetails;

	@ApiModelProperty(value = "平台sku")
	private String platformSku;

	@ApiModelProperty(value = "品莲sku")
	private String plSku;

	@ApiModelProperty(value = "品莲spu")
	private String plSpu;

	@ApiModelProperty(value = "数量")
	private Integer quantity;

	@ApiModelProperty(value = "单属性时的初始价格  拍卖时的起拍价 ")
	private BigDecimal startPrice;

	@ApiModelProperty(value = "是否接受议价(true 接受，false 不接受)")
	private Boolean bestOfferEnabled;

	@ApiModelProperty(value = "自动接受价格")
	private BigDecimal autoAcceptPrice;

	@ApiModelProperty(value = "自动拒绝价格")
	private BigDecimal minimumBestOfferPrice;

	@ApiModelProperty(value = "物品状态")
	private String conditionId;

	@ApiModelProperty(value = "物品状况描述")
	private String conditionDescription;

	@ApiModelProperty(value = "描述")
	private String description;

	@ApiModelProperty(value = "一口价")
	private BigDecimal buyItNowPrice;

	@ApiModelProperty(value = "最低价")
	private BigDecimal floorPrice;

	@ApiModelProperty(value = "paypal帐号")
	private String paypal;

	@ApiModelProperty(value = "支付选项")
	private String paymentOption;

	@ApiModelProperty(value = "支付说明")
	private String paymentDetail;

	@ApiModelProperty(value = "卖家要求 true 允许所有买家购买 false 禁止以下买家购买我的商品")
	private Boolean disableBuyerRequirements;

	@ApiModelProperty(value = "发货时间天数")
	private Integer dispatchTimeMax;

	@ApiModelProperty(value = "物品所在地")
	private String local;

	@ApiModelProperty(value = "物品所在国家")
	private String country;

	@ApiModelProperty(value = "邮编")
	private String zipCode;

	@ApiModelProperty(value = "刊登帐号")
	private String publishAccount;

	@ApiModelProperty(value = "产品描述")
	private String listingDesc;

	@ApiModelProperty(value = "产品描述原来的描述")
	private String listingDescOriginal;

	@ApiModelProperty(value = "授权id")
	private Long empowerId;

	@ApiModelProperty(value = "卖家")
	private String seller;

	@ApiModelProperty(value = "刊登站点所用的币种")
	private String currency;

	@ApiModelProperty(value = "多属性刊登变体信息字符串集合")
	private List<EbayPublishListingVariant> variant = Lists.newArrayList();

	@ApiModelProperty(value = "组合商品")
	private List<EbayPublishListingVariantSkus> listVariantSkus = Lists.newArrayList();

	@ApiModelProperty(value = "多属性刊登变体信息图片集合")
	private List<EbayPublishListingVariantPicture> variantPicture = Lists.newArrayList();

	@ApiModelProperty(value = "商品属性值 ")
	private List<EbayPublishListingAttribute> attributeValue = Lists.newArrayList();

	@ApiModelProperty(value = "当disableBuyerRequirements=false 此字段为所需的要求")
	private EbayPublishBuyerRequirements disableBuyerRequirementsValue;

	@ApiModelProperty(value = "退货政策")
	private EbayPublishListingReturnPolicy returnPolicy;

	@ApiModelProperty(value = "运输政策")
	private EbayPublishListingShipping shippingService;

	@ApiModelProperty(value = "橱窗展示 None Featured Gallery Plus CustomCode")
	private String galleryTypeCode;

	@ApiModelProperty(value = "立即付款 ture false")
	private Boolean autoPay;

	@ApiModelProperty(value = "风格id")
	private Long styleId;

	@ApiModelProperty(value = "风格名称")
	private String styleName;

	@ApiModelProperty(value = "模板ID{\"template1\":1,\"template2\":2}")
	private String templateIds;

	@ApiModelProperty(value = "地标币种")
	private String switchCountry;

	@ApiModelProperty(value = "地标汇率")
	private BigDecimal exchangeRates;

	@ApiModelProperty(value = "是否是平台listing 0是历史刊登数据 1是新刊登 2Ebay平台")
	private Integer platformListing;//

	@ApiModelProperty(value = "收货国家")
	private String shipCountry;

	@ApiModelProperty(value = "物流类型")
	private String logisticsType;

	@ApiModelProperty(value = "发货仓库")
	private String warehouseCode;

	@ApiModelProperty(value = "物流时效")
	private String logisticsAging;

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

	@ApiModelProperty(value = "productReferenceID")
	private String productReferenceID;

	public Integer getPlatformListing() {
		return platformListing;
	}

	public void setPlatformListing(Integer platformListing) {
		this.platformListing = platformListing;
	}

	public List<EbayPublishListingAttribute> getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(List<EbayPublishListingAttribute> attributeValue) {
		this.attributeValue = attributeValue;
	}

	public EbayPublishBuyerRequirements getDisableBuyerRequirementsValue() {
		return disableBuyerRequirementsValue;
	}

	public void setDisableBuyerRequirementsValue(EbayPublishBuyerRequirements disableBuyerRequirementsValue) {
		this.disableBuyerRequirementsValue = disableBuyerRequirementsValue;
	}

	public EbayPublishListingReturnPolicy getReturnPolicy() {
		return returnPolicy;
	}

	public void setReturnPolicy(EbayPublishListingReturnPolicy returnPolicy) {
		this.returnPolicy = returnPolicy;
	}

	public EbayPublishListingShipping getShippingService() {
		return shippingService;
	}

	public void setShippingService(EbayPublishListingShipping shippingService) {
		this.shippingService = shippingService;
	}

	public List<EbayPublishListingVariant> getVariant() {
		return variant;
	}

	public void setVariant(List<EbayPublishListingVariant> variant) {
		this.variant = variant;
	}

	public List<EbayPublishListingVariantPicture> getVariantPicture() {
		return variantPicture;
	}

	public void setVariantPicture(List<EbayPublishListingVariantPicture> variantPicture) {
		this.variantPicture = variantPicture;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getProductCategory1() {
		return productCategory1;
	}

	public void setProductCategory1(String productCategory1) {
		this.productCategory1 = productCategory1;
	}

	public String getProductCategory2() {
		return productCategory2;
	}

	public void setProductCategory2(String productCategory2) {
		this.productCategory2 = productCategory2;
	}

	public String getStoreCategory1() {
		return storeCategory1;
	}

	public void setStoreCategory1(String storeCategory1) {
		this.storeCategory1 = storeCategory1;
	}

	public String getStoreCategory2() {
		return storeCategory2;
	}

	public void setStoreCategory2(String storeCategory2) {
		this.storeCategory2 = storeCategory2;
	}

	public Integer getListingType() {
		return listingType;
	}

	public void setListingType(Integer listingType) {
		this.listingType = listingType;
	}

	public String getListingDuration() {
		return listingDuration;
	}

	public void setListingDuration(String listingDuration) {
		this.listingDuration = listingDuration;
	}

	public EbayPublishListingVariant getProductListingDetails() {
		return productListingDetails;
	}

	public void setProductListingDetails(EbayPublishListingVariant productListingDetails) {
		this.productListingDetails = productListingDetails;
	}

	public String getPlatformSku() {
		return platformSku;
	}

	public void setPlatformSku(String platformSku) {
		this.platformSku = platformSku;
	}

	public String getPlSku() {
		return plSku;
	}

	public void setPlSku(String plSku) {
		this.plSku = plSku;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(BigDecimal startPrice) {
		this.startPrice = startPrice;
	}

	public Boolean getBestOfferEnabled() {
		return bestOfferEnabled;
	}

	public void setBestOfferEnabled(Boolean bestOfferEnabled) {
		this.bestOfferEnabled = bestOfferEnabled;
	}

	public BigDecimal getAutoAcceptPrice() {
		return autoAcceptPrice;
	}

	public void setAutoAcceptPrice(BigDecimal autoAcceptPrice) {
		this.autoAcceptPrice = autoAcceptPrice;
	}

	public BigDecimal getMinimumBestOfferPrice() {
		return minimumBestOfferPrice;
	}

	public void setMinimumBestOfferPrice(BigDecimal minimumBestOfferPrice) {
		this.minimumBestOfferPrice = minimumBestOfferPrice;
	}

	public String getConditionId() {
		return conditionId;
	}

	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}

	public String getConditionDescription() {
		return conditionDescription;
	}

	public void setConditionDescription(String conditionDescription) {
		this.conditionDescription = conditionDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getBuyItNowPrice() {
		return buyItNowPrice;
	}

	public void setBuyItNowPrice(BigDecimal buyItNowPrice) {
		this.buyItNowPrice = buyItNowPrice;
	}

	public BigDecimal getFloorPrice() {
		return floorPrice;
	}

	public void setFloorPrice(BigDecimal floorPrice) {
		this.floorPrice = floorPrice;
	}

	public String getPaypal() {
		return paypal;
	}

	public void setPaypal(String paypal) {
		this.paypal = paypal;
	}

	public String getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}

	public String getPaymentDetail() {
		return paymentDetail;
	}

	public void setPaymentDetail(String paymentDetail) {
		this.paymentDetail = paymentDetail;
	}

	public Boolean getDisableBuyerRequirements() {
		return disableBuyerRequirements;
	}

	public void setDisableBuyerRequirements(Boolean disableBuyerRequirements) {
		this.disableBuyerRequirements = disableBuyerRequirements;
	}

	public Integer getDispatchTimeMax() {
		return dispatchTimeMax;
	}

	public void setDispatchTimeMax(Integer dispatchTimeMax) {
		this.dispatchTimeMax = dispatchTimeMax;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPublishAccount() {
		return publishAccount;
	}

	public void setPublishAccount(String publishAccount) {
		this.publishAccount = publishAccount;
	}

	public String getListingDesc() {
		return listingDesc;
	}

	public void setListingDesc(String listingDesc) {
		this.listingDesc = listingDesc;
	}

	public Long getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Long empowerId) {
		this.empowerId = empowerId;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getGalleryTypeCode() {
		return galleryTypeCode;
	}

	public void setGalleryTypeCode(String galleryTypeCode) {
		this.galleryTypeCode = galleryTypeCode;
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
		this.templateIds = templateIds;
	}

	public String getSwitchCountry() {
		return switchCountry;
	}

	public void setSwitchCountry(String switchCountry) {
		this.switchCountry = switchCountry;
	}

	public BigDecimal getExchangeRates() {
		return exchangeRates;
	}

	public void setExchangeRates(BigDecimal exchangeRates) {
		this.exchangeRates = exchangeRates;
	}

	public String getListingDescOriginal() {
		return listingDescOriginal;
	}

	public void setListingDescOriginal(String listingDescOriginal) {
		this.listingDescOriginal = listingDescOriginal;
	}

	public String getPlSpu() {
		return plSpu;
	}

	public void setPlSpu(String plSpu) {
		this.plSpu = plSpu;
	}

	public String getStyleName() {
		return styleName;
	}

	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}

	public String getShipCountry() {
		return shipCountry;
	}

	public void setShipCountry(String shipCountry) {
		this.shipCountry = shipCountry;
	}

	public String getLogisticsType() {
		return logisticsType;
	}

	public void setLogisticsType(String logisticsType) {
		this.logisticsType = logisticsType;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getLogisticsAging() {
		return logisticsAging;
	}

	public void setLogisticsAging(String logisticsAging) {
		this.logisticsAging = logisticsAging;
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
