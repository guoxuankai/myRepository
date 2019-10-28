package com.rondaful.cloud.seller.entity.aliexpress;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 参考文档 aliexpress.postproduct.redefining.findaeproductbyid( 获取单个产品信息 )
 * https://developers.aliexpress.com/doc.htm?docId=30172&docType=2&source=search
 * @author chenhan
 *
 */
public class AliexpressListingModel implements Serializable {

    private static final long serialVersionUID = 8255919174147732459L;

    private Long addUnit;

    private String addWeight;

    private AliexpressAeopAeMultimedia aeopAEMultimedia;

    private List<AliexpressAeopAeProductProperty> aeopAeProductPropertys;

    private List<AliexpressAeopAeProductSku> aeopAeProductSKUs;

    private AliexpressAeopNationalQuoteConfiguration aeopNationalQuoteConfiguration;

    private Long baseUnit;

    private Long bulkDiscount;

    private Long bulkOrder;

    private Long categoryId;

    private Date couponEndDate;

    private Date couponStartDate;

    private String currencyCode;

    private Long deliveryTime;

    private String detail;

    private Long errorCode;

    private String errorMessage;

    private Long freightTemplateId;

    private Date gmtCreate;

    private Date gmtModified;

    private String grossWeight;

    private Long groupId;

    private List<Long> groupIds;

    private String imageURLs;

    private Boolean isImageDynamic;

    private Boolean isPackSell;

    private String keyword;

    private Long lotNum;

    private String mobileDetail;

    private String ownerMemberId;

    private Long ownerMemberSeq;

    private Long packageHeight;

    private Long packageLength;

    private Boolean packageType;

    private Long packageWidth;

    private Long productId;

    private String productMoreKeywords1;

    private String productMoreKeywords2;

    private String productPrice;

    private String productStatusType;

    private Long productUnit;

    private Long promiseTemplateId;

    private String reduceStrategy;

    private Long sizechartId;

    private String src;

    private String subject;

    private Boolean success;

    private String summary;

    private String wsDisplay;

    private Date wsOfflineDate;

    private Long wsValidNum;



    public Long getAddUnit() {
        return addUnit;
    }

    public void setAddUnit(Long addUnit) {
        this.addUnit = addUnit;
    }

    public String getAddWeight() {
        return addWeight;
    }

    public void setAddWeight(String addWeight) {
        this.addWeight = addWeight;
    }

    public AliexpressAeopAeMultimedia getAeopAEMultimedia() {
        return aeopAEMultimedia;
    }

    public void setAeopAEMultimedia(AliexpressAeopAeMultimedia aeopAEMultimedia) {
        this.aeopAEMultimedia = aeopAEMultimedia;
    }

    public List<AliexpressAeopAeProductProperty> getAeopAeProductPropertys() {
        return aeopAeProductPropertys;
    }

    public void setAeopAeProductPropertys(List<AliexpressAeopAeProductProperty> aeopAeProductPropertys) {
        this.aeopAeProductPropertys = aeopAeProductPropertys;
    }

    public List<AliexpressAeopAeProductSku> getAeopAeProductSKUs() {
        return aeopAeProductSKUs;
    }

    public void setAeopAeProductSKUs(List<AliexpressAeopAeProductSku> aeopAeProductSKUs) {
        this.aeopAeProductSKUs = aeopAeProductSKUs;
    }

    public AliexpressAeopNationalQuoteConfiguration getAeopNationalQuoteConfiguration() {
        return aeopNationalQuoteConfiguration;
    }

    public void setAeopNationalQuoteConfiguration(AliexpressAeopNationalQuoteConfiguration aeopNationalQuoteConfiguration) {
        this.aeopNationalQuoteConfiguration = aeopNationalQuoteConfiguration;
    }

    public Long getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(Long baseUnit) {
        this.baseUnit = baseUnit;
    }

    public Long getBulkDiscount() {
        return bulkDiscount;
    }

    public void setBulkDiscount(Long bulkDiscount) {
        this.bulkDiscount = bulkDiscount;
    }

    public Long getBulkOrder() {
        return bulkOrder;
    }

    public void setBulkOrder(Long bulkOrder) {
        this.bulkOrder = bulkOrder;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

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

    public Long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Long errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public String getImageURLs() {
        return imageURLs;
    }

    public void setImageURLs(String imageURLs) {
        this.imageURLs = imageURLs;
    }

    public Boolean getImageDynamic() {
        return isImageDynamic;
    }

    public void setImageDynamic(Boolean imageDynamic) {
        isImageDynamic = imageDynamic;
    }

    public Boolean getPackSell() {
        return isPackSell;
    }

    public void setPackSell(Boolean packSell) {
        isPackSell = packSell;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getLotNum() {
        return lotNum;
    }

    public void setLotNum(Long lotNum) {
        this.lotNum = lotNum;
    }

    public String getMobileDetail() {
        return mobileDetail;
    }

    public void setMobileDetail(String mobileDetail) {
        this.mobileDetail = mobileDetail;
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

    public Long getPackageHeight() {
        return packageHeight;
    }

    public void setPackageHeight(Long packageHeight) {
        this.packageHeight = packageHeight;
    }

    public Long getPackageLength() {
        return packageLength;
    }

    public void setPackageLength(Long packageLength) {
        this.packageLength = packageLength;
    }

    public Boolean getPackageType() {
        return packageType;
    }

    public void setPackageType(Boolean packageType) {
        this.packageType = packageType;
    }

    public Long getPackageWidth() {
        return packageWidth;
    }

    public void setPackageWidth(Long packageWidth) {
        this.packageWidth = packageWidth;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductMoreKeywords1() {
        return productMoreKeywords1;
    }

    public void setProductMoreKeywords1(String productMoreKeywords1) {
        this.productMoreKeywords1 = productMoreKeywords1;
    }

    public String getProductMoreKeywords2() {
        return productMoreKeywords2;
    }

    public void setProductMoreKeywords2(String productMoreKeywords2) {
        this.productMoreKeywords2 = productMoreKeywords2;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductStatusType() {
        return productStatusType;
    }

    public void setProductStatusType(String productStatusType) {
        this.productStatusType = productStatusType;
    }

    public Long getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(Long productUnit) {
        this.productUnit = productUnit;
    }

    public Long getPromiseTemplateId() {
        return promiseTemplateId;
    }

    public void setPromiseTemplateId(Long promiseTemplateId) {
        this.promiseTemplateId = promiseTemplateId;
    }

    public String getReduceStrategy() {
        return reduceStrategy;
    }

    public void setReduceStrategy(String reduceStrategy) {
        this.reduceStrategy = reduceStrategy;
    }

    public Long getSizechartId() {
        return sizechartId;
    }

    public void setSizechartId(Long sizechartId) {
        this.sizechartId = sizechartId;
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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public Long getWsValidNum() {
        return wsValidNum;
    }

    public void setWsValidNum(Long wsValidNum) {
        this.wsValidNum = wsValidNum;
    }
}
