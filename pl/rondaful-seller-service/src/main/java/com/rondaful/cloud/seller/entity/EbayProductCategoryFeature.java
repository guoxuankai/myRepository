package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 
 * 实体类对应的数据表为：  ebay_product_category_feature
 * @author songjie
 * @date 2018-12-04 10:31:23
 */
@ApiModel(value ="EbayProductCategoryFeature")
public class EbayProductCategoryFeature implements Serializable {
    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "分类id")
    private String categoryId;

    @ApiModelProperty(value = "启用广告")
    private String adFormatEnabled;

    @ApiModelProperty(value = "启用广告兼容性")
    private Boolean adDitionalCompatibilityEnabled;

    @ApiModelProperty(value = "最价报价自动接收启用")
    private Boolean bestOfferAutoAcceptEnabled;

    @ApiModelProperty(value = "最佳报价自动下降启用")
    private Boolean bestOfferAutoDeclineEnabled;

    @ApiModelProperty(value = "最佳报盘启用")
    private Boolean bestOfferCounterEnabled;

    @ApiModelProperty(value = "最佳报价启用")
    private Boolean bestOfferEnabled;

    @ApiModelProperty(value = "启用品牌mpn标识")
    private Boolean brandMpnIdentifierEnabled;

    @ApiModelProperty(value = "买方担保启用")
    private Boolean buyerGuaranteeEnabled;

    @ApiModelProperty(value = "条件启用")
    private String conditionEnabled;

    @ApiModelProperty(value = "条件值")
    private String conditionValues;

    @ApiModelProperty(value = "")
    private Boolean crossBorderTradeAustraliaEnabled;

    @ApiModelProperty(value = "")
    private Boolean crossBorderTradeGbEnabled;

    @ApiModelProperty(value = "")
    private Boolean crossBorderTradeNorthAmericaEnabled;

    @ApiModelProperty(value = "")
    private Boolean depositSupported;

    @ApiModelProperty(value = "")
    private Boolean digitalGoodDeliveryEnabled;

    @ApiModelProperty(value = "")
    private String eanEnabled;

    @ApiModelProperty(value = "免费画廊")
    private Boolean freeGallerPlusEnabled;

    @ApiModelProperty(value = "免费图片包启用")
    private Boolean freePicturePackEnabled;

    @ApiModelProperty(value = "画廊持续时间")
    private String galleryFeaturedDurations;

    @ApiModelProperty(value = "开启全球货运")
    private Boolean globalShippingEnabled;

    @ApiModelProperty(value = "")
    private BigDecimal group1MaxFlatShippingCost;

    @ApiModelProperty(value = "")
    private BigDecimal group2MaxFlatShippingCost;

    @ApiModelProperty(value = "")
    private BigDecimal group3MaxFlatShippingCost;

    @ApiModelProperty(value = "处理时间启用")
    private Boolean handlingTimeEnabled;

    @ApiModelProperty(value = "主页功能启用")
    private Boolean homePageFeaturedEnabled;

    @ApiModelProperty(value = "工作流时间线")
    private String inEscrowWorkflowTimeline;

    @ApiModelProperty(value = "")
    private String isbnEnabled;

    @ApiModelProperty(value = "项目兼容性")
    private String itemCompatibilityEnabled;

    @ApiModelProperty(value = "")
    private String itemCompatibilityType;

    @ApiModelProperty(value = "")
    private String itemSpecificsEnabled;

    @ApiModelProperty(value = "列表持续时间")
    private String listingDuration;

    @ApiModelProperty(value = "")
    private BigDecimal maxFlatShippingCost;

    @ApiModelProperty(value = "")
    private String maxGranularFitmentCount;

    @ApiModelProperty(value = "")
    private Integer minItemCompatibility;

    @ApiModelProperty(value = "")
    private Integer maxItemCompatibility;

    @ApiModelProperty(value = "")
    private BigDecimal minimumReservePrice;

    @ApiModelProperty(value = "")
    private String nonSubscription;

    @ApiModelProperty(value = "")
    private Boolean paisaPayFullEscrowEnabled;

    @ApiModelProperty(value = "")
    private Boolean paypalBuyerProtectionEnabled;

    @ApiModelProperty(value = "")
    private Boolean paypalRequired;

    @ApiModelProperty(value = "")
    private String paymentMethoed;

    @ApiModelProperty(value = "")
    private String paymentProfileCategoryGroup;

    @ApiModelProperty(value = "")
    private Boolean pickupDropOffEnabled;

    @ApiModelProperty(value = "")
    private String premiumSubscription;

    @ApiModelProperty(value = "")
    private Boolean proPackEnabled;

    @ApiModelProperty(value = "")
    private Boolean proPackPlusEnabled;

    @ApiModelProperty(value = "")
    private String productCreationEnabled;

    @ApiModelProperty(value = "")
    private String regularSubscription;

    @ApiModelProperty(value = "货运政策")
    private Boolean returnPolicyEnabled;

    @ApiModelProperty(value = "")
    private String returnPolicyProfileCategoryGroup;

    @ApiModelProperty(value = "允许修订价格")
    private Boolean revisePriceAllowed;

    @ApiModelProperty(value = "允许修订数量")
    private Boolean reviseQuantityAllowed;

    @ApiModelProperty(value = "买家安全保证")
    private Boolean safePaymentRequired;

    @ApiModelProperty(value = "卖方联系人详细信息启用")
    private Boolean sellerContactDetailsEnabled;

    @ApiModelProperty(value = "卖方提供的标题支持")
    private Boolean sellerProvidedTitleSupported;

    @ApiModelProperty(value = "")
    private String shippingProfileCategoryGroup;

    @ApiModelProperty(value = "所需装运条款")
    private Boolean shippingTermsRequired;

    @ApiModelProperty(value = "站点名称")
    private String site;

    @ApiModelProperty(value = "")
    private Boolean skypeMeNonTransactionalEnabled;

    @ApiModelProperty(value = "")
    private Boolean skypeMeTransactionalEnabled;

    @ApiModelProperty(value = "")
    private String specialitySubscription;

    @ApiModelProperty(value = "店主延长上市期限")
    private String storeOwnerExtendedListingDurations;

    @ApiModelProperty(value = "事务确认请求启用")
    private Boolean transactionConfirmationRequestEnabled;

    @ApiModelProperty(value = "")
    private String upcEnabled;

    @ApiModelProperty(value = "用户同意要求")
    private Boolean userConsentRequired;

    @ApiModelProperty(value = "")
    private Boolean valueCategory;

    @ApiModelProperty(value = "")
    private Boolean valuePackEnabled;

    @ApiModelProperty(value = "启用变体")
    private Boolean variationsEnabled;

    @ApiModelProperty(value = "")
    private String version;

    @ApiModelProperty(value = "")
    private Boolean vinSupported;

    @ApiModelProperty(value = "")
    private Boolean vrmSupported;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table ebay_product_category_feature
     *
     * @mbg.generated 2018-12-04 10:31:23
     */
    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId == null ? null : categoryId.trim();
    }

    public String getAdFormatEnabled() {
        return adFormatEnabled;
    }

    public void setAdFormatEnabled(String adFormatEnabled) {
        this.adFormatEnabled = adFormatEnabled == null ? null : adFormatEnabled.trim();
    }

    public Boolean getAdDitionalCompatibilityEnabled() {
        return adDitionalCompatibilityEnabled;
    }

    public void setAdDitionalCompatibilityEnabled(Boolean adDitionalCompatibilityEnabled) {
        this.adDitionalCompatibilityEnabled = adDitionalCompatibilityEnabled;
    }

    public Boolean getBestOfferAutoAcceptEnabled() {
        return bestOfferAutoAcceptEnabled;
    }

    public void setBestOfferAutoAcceptEnabled(Boolean bestOfferAutoAcceptEnabled) {
        this.bestOfferAutoAcceptEnabled = bestOfferAutoAcceptEnabled;
    }

    public Boolean getBestOfferAutoDeclineEnabled() {
        return bestOfferAutoDeclineEnabled;
    }

    public void setBestOfferAutoDeclineEnabled(Boolean bestOfferAutoDeclineEnabled) {
        this.bestOfferAutoDeclineEnabled = bestOfferAutoDeclineEnabled;
    }

    public Boolean getBestOfferCounterEnabled() {
        return bestOfferCounterEnabled;
    }

    public void setBestOfferCounterEnabled(Boolean bestOfferCounterEnabled) {
        this.bestOfferCounterEnabled = bestOfferCounterEnabled;
    }

    public Boolean getBestOfferEnabled() {
        return bestOfferEnabled;
    }

    public void setBestOfferEnabled(Boolean bestOfferEnabled) {
        this.bestOfferEnabled = bestOfferEnabled;
    }

    public Boolean getBrandMpnIdentifierEnabled() {
        return brandMpnIdentifierEnabled;
    }

    public void setBrandMpnIdentifierEnabled(Boolean brandMpnIdentifierEnabled) {
        this.brandMpnIdentifierEnabled = brandMpnIdentifierEnabled;
    }

    public Boolean getBuyerGuaranteeEnabled() {
        return buyerGuaranteeEnabled;
    }

    public void setBuyerGuaranteeEnabled(Boolean buyerGuaranteeEnabled) {
        this.buyerGuaranteeEnabled = buyerGuaranteeEnabled;
    }

    public String getConditionEnabled() {
        return conditionEnabled;
    }

    public void setConditionEnabled(String conditionEnabled) {
        this.conditionEnabled = conditionEnabled == null ? null : conditionEnabled.trim();
    }

    public String getConditionValues() {
        return conditionValues;
    }

    public void setConditionValues(String conditionValues) {
        this.conditionValues = conditionValues == null ? null : conditionValues.trim();
    }

    public Boolean getCrossBorderTradeAustraliaEnabled() {
        return crossBorderTradeAustraliaEnabled;
    }

    public void setCrossBorderTradeAustraliaEnabled(Boolean crossBorderTradeAustraliaEnabled) {
        this.crossBorderTradeAustraliaEnabled = crossBorderTradeAustraliaEnabled;
    }

    public Boolean getCrossBorderTradeGbEnabled() {
        return crossBorderTradeGbEnabled;
    }

    public void setCrossBorderTradeGbEnabled(Boolean crossBorderTradeGbEnabled) {
        this.crossBorderTradeGbEnabled = crossBorderTradeGbEnabled;
    }

    public Boolean getCrossBorderTradeNorthAmericaEnabled() {
        return crossBorderTradeNorthAmericaEnabled;
    }

    public void setCrossBorderTradeNorthAmericaEnabled(Boolean crossBorderTradeNorthAmericaEnabled) {
        this.crossBorderTradeNorthAmericaEnabled = crossBorderTradeNorthAmericaEnabled;
    }

    public Boolean getDepositSupported() {
        return depositSupported;
    }

    public void setDepositSupported(Boolean depositSupported) {
        this.depositSupported = depositSupported;
    }

    public Boolean getDigitalGoodDeliveryEnabled() {
        return digitalGoodDeliveryEnabled;
    }

    public void setDigitalGoodDeliveryEnabled(Boolean digitalGoodDeliveryEnabled) {
        this.digitalGoodDeliveryEnabled = digitalGoodDeliveryEnabled;
    }

    public String getEanEnabled() {
        return eanEnabled;
    }

    public void setEanEnabled(String eanEnabled) {
        this.eanEnabled = eanEnabled == null ? null : eanEnabled.trim();
    }

    public Boolean getFreeGallerPlusEnabled() {
        return freeGallerPlusEnabled;
    }

    public void setFreeGallerPlusEnabled(Boolean freeGallerPlusEnabled) {
        this.freeGallerPlusEnabled = freeGallerPlusEnabled;
    }

    public Boolean getFreePicturePackEnabled() {
        return freePicturePackEnabled;
    }

    public void setFreePicturePackEnabled(Boolean freePicturePackEnabled) {
        this.freePicturePackEnabled = freePicturePackEnabled;
    }

    public String getGalleryFeaturedDurations() {
        return galleryFeaturedDurations;
    }

    public void setGalleryFeaturedDurations(String galleryFeaturedDurations) {
        this.galleryFeaturedDurations = galleryFeaturedDurations == null ? null : galleryFeaturedDurations.trim();
    }

    public Boolean getGlobalShippingEnabled() {
        return globalShippingEnabled;
    }

    public void setGlobalShippingEnabled(Boolean globalShippingEnabled) {
        this.globalShippingEnabled = globalShippingEnabled;
    }

    public BigDecimal getGroup1MaxFlatShippingCost() {
        return group1MaxFlatShippingCost;
    }

    public void setGroup1MaxFlatShippingCost(BigDecimal group1MaxFlatShippingCost) {
        this.group1MaxFlatShippingCost = group1MaxFlatShippingCost;
    }

    public BigDecimal getGroup2MaxFlatShippingCost() {
        return group2MaxFlatShippingCost;
    }

    public void setGroup2MaxFlatShippingCost(BigDecimal group2MaxFlatShippingCost) {
        this.group2MaxFlatShippingCost = group2MaxFlatShippingCost;
    }

    public BigDecimal getGroup3MaxFlatShippingCost() {
        return group3MaxFlatShippingCost;
    }

    public void setGroup3MaxFlatShippingCost(BigDecimal group3MaxFlatShippingCost) {
        this.group3MaxFlatShippingCost = group3MaxFlatShippingCost;
    }

    public Boolean getHandlingTimeEnabled() {
        return handlingTimeEnabled;
    }

    public void setHandlingTimeEnabled(Boolean handlingTimeEnabled) {
        this.handlingTimeEnabled = handlingTimeEnabled;
    }

    public Boolean getHomePageFeaturedEnabled() {
        return homePageFeaturedEnabled;
    }

    public void setHomePageFeaturedEnabled(Boolean homePageFeaturedEnabled) {
        this.homePageFeaturedEnabled = homePageFeaturedEnabled;
    }

    public String getInEscrowWorkflowTimeline() {
        return inEscrowWorkflowTimeline;
    }

    public void setInEscrowWorkflowTimeline(String inEscrowWorkflowTimeline) {
        this.inEscrowWorkflowTimeline = inEscrowWorkflowTimeline == null ? null : inEscrowWorkflowTimeline.trim();
    }

    public String getIsbnEnabled() {
        return isbnEnabled;
    }

    public void setIsbnEnabled(String isbnEnabled) {
        this.isbnEnabled = isbnEnabled == null ? null : isbnEnabled.trim();
    }

    public String getItemCompatibilityEnabled() {
        return itemCompatibilityEnabled;
    }

    public void setItemCompatibilityEnabled(String itemCompatibilityEnabled) {
        this.itemCompatibilityEnabled = itemCompatibilityEnabled == null ? null : itemCompatibilityEnabled.trim();
    }

    public String getItemCompatibilityType() {
        return itemCompatibilityType;
    }

    public void setItemCompatibilityType(String itemCompatibilityType) {
        this.itemCompatibilityType = itemCompatibilityType == null ? null : itemCompatibilityType.trim();
    }

    public String getItemSpecificsEnabled() {
        return itemSpecificsEnabled;
    }

    public void setItemSpecificsEnabled(String itemSpecificsEnabled) {
        this.itemSpecificsEnabled = itemSpecificsEnabled == null ? null : itemSpecificsEnabled.trim();
    }

    public String getListingDuration() {
        return listingDuration;
    }

    public void setListingDuration(String listingDuration) {
        this.listingDuration = listingDuration == null ? null : listingDuration.trim();
    }

    public BigDecimal getMaxFlatShippingCost() {
        return maxFlatShippingCost;
    }

    public void setMaxFlatShippingCost(BigDecimal maxFlatShippingCost) {
        this.maxFlatShippingCost = maxFlatShippingCost;
    }

    public String getMaxGranularFitmentCount() {
        return maxGranularFitmentCount;
    }

    public void setMaxGranularFitmentCount(String maxGranularFitmentCount) {
        this.maxGranularFitmentCount = maxGranularFitmentCount == null ? null : maxGranularFitmentCount.trim();
    }

    public Integer getMinItemCompatibility() {
        return minItemCompatibility;
    }

    public void setMinItemCompatibility(Integer minItemCompatibility) {
        this.minItemCompatibility = minItemCompatibility;
    }

    public Integer getMaxItemCompatibility() {
        return maxItemCompatibility;
    }

    public void setMaxItemCompatibility(Integer maxItemCompatibility) {
        this.maxItemCompatibility = maxItemCompatibility;
    }

    public BigDecimal getMinimumReservePrice() {
        return minimumReservePrice;
    }

    public void setMinimumReservePrice(BigDecimal minimumReservePrice) {
        this.minimumReservePrice = minimumReservePrice;
    }

    public String getNonSubscription() {
        return nonSubscription;
    }

    public void setNonSubscription(String nonSubscription) {
        this.nonSubscription = nonSubscription == null ? null : nonSubscription.trim();
    }

    public Boolean getPaisaPayFullEscrowEnabled() {
        return paisaPayFullEscrowEnabled;
    }

    public void setPaisaPayFullEscrowEnabled(Boolean paisaPayFullEscrowEnabled) {
        this.paisaPayFullEscrowEnabled = paisaPayFullEscrowEnabled;
    }

    public Boolean getPaypalBuyerProtectionEnabled() {
        return paypalBuyerProtectionEnabled;
    }

    public void setPaypalBuyerProtectionEnabled(Boolean paypalBuyerProtectionEnabled) {
        this.paypalBuyerProtectionEnabled = paypalBuyerProtectionEnabled;
    }

    public Boolean getPaypalRequired() {
        return paypalRequired;
    }

    public void setPaypalRequired(Boolean paypalRequired) {
        this.paypalRequired = paypalRequired;
    }

    public String getPaymentMethoed() {
        return paymentMethoed;
    }

    public void setPaymentMethoed(String paymentMethoed) {
        this.paymentMethoed = paymentMethoed == null ? null : paymentMethoed.trim();
    }

    public String getPaymentProfileCategoryGroup() {
        return paymentProfileCategoryGroup;
    }

    public void setPaymentProfileCategoryGroup(String paymentProfileCategoryGroup) {
        this.paymentProfileCategoryGroup = paymentProfileCategoryGroup == null ? null : paymentProfileCategoryGroup.trim();
    }

    public Boolean getPickupDropOffEnabled() {
        return pickupDropOffEnabled;
    }

    public void setPickupDropOffEnabled(Boolean pickupDropOffEnabled) {
        this.pickupDropOffEnabled = pickupDropOffEnabled;
    }

    public String getPremiumSubscription() {
        return premiumSubscription;
    }

    public void setPremiumSubscription(String premiumSubscription) {
        this.premiumSubscription = premiumSubscription == null ? null : premiumSubscription.trim();
    }

    public Boolean getProPackEnabled() {
        return proPackEnabled;
    }

    public void setProPackEnabled(Boolean proPackEnabled) {
        this.proPackEnabled = proPackEnabled;
    }

    public Boolean getProPackPlusEnabled() {
        return proPackPlusEnabled;
    }

    public void setProPackPlusEnabled(Boolean proPackPlusEnabled) {
        this.proPackPlusEnabled = proPackPlusEnabled;
    }

    public String getProductCreationEnabled() {
        return productCreationEnabled;
    }

    public void setProductCreationEnabled(String productCreationEnabled) {
        this.productCreationEnabled = productCreationEnabled == null ? null : productCreationEnabled.trim();
    }

    public String getRegularSubscription() {
        return regularSubscription;
    }

    public void setRegularSubscription(String regularSubscription) {
        this.regularSubscription = regularSubscription == null ? null : regularSubscription.trim();
    }

    public Boolean getReturnPolicyEnabled() {
        return returnPolicyEnabled;
    }

    public void setReturnPolicyEnabled(Boolean returnPolicyEnabled) {
        this.returnPolicyEnabled = returnPolicyEnabled;
    }

    public String getReturnPolicyProfileCategoryGroup() {
        return returnPolicyProfileCategoryGroup;
    }

    public void setReturnPolicyProfileCategoryGroup(String returnPolicyProfileCategoryGroup) {
        this.returnPolicyProfileCategoryGroup = returnPolicyProfileCategoryGroup == null ? null : returnPolicyProfileCategoryGroup.trim();
    }

    public Boolean getRevisePriceAllowed() {
        return revisePriceAllowed;
    }

    public void setRevisePriceAllowed(Boolean revisePriceAllowed) {
        this.revisePriceAllowed = revisePriceAllowed;
    }

    public Boolean getReviseQuantityAllowed() {
        return reviseQuantityAllowed;
    }

    public void setReviseQuantityAllowed(Boolean reviseQuantityAllowed) {
        this.reviseQuantityAllowed = reviseQuantityAllowed;
    }

    public Boolean getSafePaymentRequired() {
        return safePaymentRequired;
    }

    public void setSafePaymentRequired(Boolean safePaymentRequired) {
        this.safePaymentRequired = safePaymentRequired;
    }

    public Boolean getSellerContactDetailsEnabled() {
        return sellerContactDetailsEnabled;
    }

    public void setSellerContactDetailsEnabled(Boolean sellerContactDetailsEnabled) {
        this.sellerContactDetailsEnabled = sellerContactDetailsEnabled;
    }

    public Boolean getSellerProvidedTitleSupported() {
        return sellerProvidedTitleSupported;
    }

    public void setSellerProvidedTitleSupported(Boolean sellerProvidedTitleSupported) {
        this.sellerProvidedTitleSupported = sellerProvidedTitleSupported;
    }

    public String getShippingProfileCategoryGroup() {
        return shippingProfileCategoryGroup;
    }

    public void setShippingProfileCategoryGroup(String shippingProfileCategoryGroup) {
        this.shippingProfileCategoryGroup = shippingProfileCategoryGroup == null ? null : shippingProfileCategoryGroup.trim();
    }

    public Boolean getShippingTermsRequired() {
        return shippingTermsRequired;
    }

    public void setShippingTermsRequired(Boolean shippingTermsRequired) {
        this.shippingTermsRequired = shippingTermsRequired;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site == null ? null : site.trim();
    }

    public Boolean getSkypeMeNonTransactionalEnabled() {
        return skypeMeNonTransactionalEnabled;
    }

    public void setSkypeMeNonTransactionalEnabled(Boolean skypeMeNonTransactionalEnabled) {
        this.skypeMeNonTransactionalEnabled = skypeMeNonTransactionalEnabled;
    }

    public Boolean getSkypeMeTransactionalEnabled() {
        return skypeMeTransactionalEnabled;
    }

    public void setSkypeMeTransactionalEnabled(Boolean skypeMeTransactionalEnabled) {
        this.skypeMeTransactionalEnabled = skypeMeTransactionalEnabled;
    }

    public String getSpecialitySubscription() {
        return specialitySubscription;
    }

    public void setSpecialitySubscription(String specialitySubscription) {
        this.specialitySubscription = specialitySubscription == null ? null : specialitySubscription.trim();
    }

    public String getStoreOwnerExtendedListingDurations() {
        return storeOwnerExtendedListingDurations;
    }

    public void setStoreOwnerExtendedListingDurations(String storeOwnerExtendedListingDurations) {
        this.storeOwnerExtendedListingDurations = storeOwnerExtendedListingDurations == null ? null : storeOwnerExtendedListingDurations.trim();
    }

    public Boolean getTransactionConfirmationRequestEnabled() {
        return transactionConfirmationRequestEnabled;
    }

    public void setTransactionConfirmationRequestEnabled(Boolean transactionConfirmationRequestEnabled) {
        this.transactionConfirmationRequestEnabled = transactionConfirmationRequestEnabled;
    }

    public String getUpcEnabled() {
        return upcEnabled;
    }

    public void setUpcEnabled(String upcEnabled) {
        this.upcEnabled = upcEnabled == null ? null : upcEnabled.trim();
    }

    public Boolean getUserConsentRequired() {
        return userConsentRequired;
    }

    public void setUserConsentRequired(Boolean userConsentRequired) {
        this.userConsentRequired = userConsentRequired;
    }

    public Boolean getValueCategory() {
        return valueCategory;
    }

    public void setValueCategory(Boolean valueCategory) {
        this.valueCategory = valueCategory;
    }

    public Boolean getValuePackEnabled() {
        return valuePackEnabled;
    }

    public void setValuePackEnabled(Boolean valuePackEnabled) {
        this.valuePackEnabled = valuePackEnabled;
    }

    public Boolean getVariationsEnabled() {
        return variationsEnabled;
    }

    public void setVariationsEnabled(Boolean variationsEnabled) {
        this.variationsEnabled = variationsEnabled;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Boolean getVinSupported() {
        return vinSupported;
    }

    public void setVinSupported(Boolean vinSupported) {
        this.vinSupported = vinSupported;
    }

    public Boolean getVrmSupported() {
        return vrmSupported;
    }

    public void setVrmSupported(Boolean vrmSupported) {
        this.vrmSupported = vrmSupported;
    }
}