package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 速卖通物品刊登详情表
 * 实体类对应的数据表为：  aliexpress_publish_listing_detail
 * @author ch
 * @date 2019-03-22 10:52:22
 */
@ApiModel(value ="AliexpressPublishListingDetail")
public class AliexpressPublishListingDetail implements Serializable {
    @ApiModelProperty(value = "主键id，自增")
    private Long id;

    @ApiModelProperty(value = "刊登id")
    private Long publishListingId;

    @ApiModelProperty(value = "商品详情")
    private String productDetails;

    @ApiModelProperty(value = "手机端描述")
    private Boolean mobileTerminal;

    @ApiModelProperty(value = "手机备注信息")
    private String mobileRemark;

    @ApiModelProperty(value = "选择属性")
    private String selectAttributes;

    @ApiModelProperty(value = "区域调价 percentage：按比例 relative：按金额 absolute：直接报价")
    private String regionalPricing;

    @ApiModelProperty(value = "批发价")
    private Boolean wholesale;

    @ApiModelProperty(value = "购买数量")
    private Integer bulkOrder;

    @ApiModelProperty(value = "价格基础上减免折扣")
    private Integer bulkDiscount;

    @ApiModelProperty(value = "库存扣减策略，总共有2种：下单减库存(place_order_withhold)和支付减库存(payment_success_deduct)。")
    private String reduceStrategy;

    @ApiModelProperty(value = "发货期(取值范围:1-60;单位:天)")
    private Integer deliveryTime;

    @ApiModelProperty(value = "最小计量单位")
    private Integer unit;

    @ApiModelProperty(value = "销售方式 1按件出售 2打包出售（价格按照包计算）")
    private Integer salesMethod;

    @ApiModelProperty(value = "每包件数。 打包销售情况，lotNum>1,非打包销售情况,lotNum=1")
    private Integer lotNum;

    @ApiModelProperty(value = "商品包装后的重量(公斤/袋)")
    private BigDecimal packagingWeight;

    @ApiModelProperty(value = "是否自定义计重.true为自定义计重,false反之")
    private Boolean isPackSell;

    @ApiModelProperty(value = "买家购买")
    private Integer buyersPurchase;

    @ApiModelProperty(value = "买家每多买")
    private Integer buyersMore;

    @ApiModelProperty(value = "重量增加")
    private BigDecimal increaseWeight;

    @ApiModelProperty(value = "长")
    private Integer packageLength;

    @ApiModelProperty(value = "宽")
    private Integer packageWidth;

    @ApiModelProperty(value = "高")
    private Integer packageHeight;

    @ApiModelProperty(value = "运费模板")
    private Long freightTemplateId;

    @ApiModelProperty(value = "服务模板")
    private Long promiseTemplateId;

    @ApiModelProperty(value = "商品分组")
    private Long groupId;

    @ApiModelProperty(value = "商品有效天数")
    private Integer wsValidNum;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "收货国家")
    private String shipCountry;

    @ApiModelProperty(value = "物流类型")
    private String logisticsType;

    @ApiModelProperty(value = "发货仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "物流时效")
    private String logisticsAging;

    @ApiModelProperty(value = "模板id(json格式数据)")
    private String templateIds;

    @ApiModelProperty(value = "区域价格是否显示")
    private Boolean pricingShow;


    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table aliexpress_publish_listing_detail
     *
     * @mbg.generated 2019-03-22 10:52:22
     */
    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPublishListingId() {
        return publishListingId;
    }

    public void setPublishListingId(Long publishListingId) {
        this.publishListingId = publishListingId;
    }

    public String getSelectAttributes() {
        return selectAttributes;
    }

    public void setSelectAttributes(String selectAttributes) {
        this.selectAttributes = selectAttributes == null ? null : selectAttributes.trim();
    }

    public String getRegionalPricing() {
        return regionalPricing;
    }

    public void setRegionalPricing(String regionalPricing) {
        this.regionalPricing = regionalPricing == null ? null : regionalPricing.trim();
    }

    public Boolean getWholesale() {
        return wholesale;
    }

    public void setWholesale(Boolean wholesale) {
        this.wholesale = wholesale;
    }

    public Integer getBulkOrder() {
        return bulkOrder;
    }

    public void setBulkOrder(Integer bulkOrder) {
        this.bulkOrder = bulkOrder;
    }

    public Integer getBulkDiscount() {
        return bulkDiscount;
    }

    public void setBulkDiscount(Integer bulkDiscount) {
        this.bulkDiscount = bulkDiscount;
    }

    public String getReduceStrategy() {
        return reduceStrategy;
    }

    public void setReduceStrategy(String reduceStrategy) {
        this.reduceStrategy = reduceStrategy == null ? null : reduceStrategy.trim();
    }

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public Integer getSalesMethod() {
        return salesMethod;
    }

    public void setSalesMethod(Integer salesMethod) {
        this.salesMethod = salesMethod;
    }

    public Integer getLotNum() {
        return lotNum;
    }

    public void setLotNum(Integer lotNum) {
        this.lotNum = lotNum;
    }

    public Boolean getIsPackSell() {
        return isPackSell;
    }

    public void setIsPackSell(Boolean isPackSell) {
        this.isPackSell = isPackSell;
    }

    public Integer getBuyersPurchase() {
        return buyersPurchase;
    }

    public void setBuyersPurchase(Integer buyersPurchase) {
        this.buyersPurchase = buyersPurchase;
    }

    public Integer getBuyersMore() {
        return buyersMore;
    }

    public void setBuyersMore(Integer buyersMore) {
        this.buyersMore = buyersMore;
    }

    public BigDecimal getIncreaseWeight() {
        return increaseWeight;
    }

    public void setIncreaseWeight(BigDecimal increaseWeight) {
        this.increaseWeight = increaseWeight;
    }

    public Integer getPackageLength() {
        return packageLength;
    }

    public void setPackageLength(Integer packageLength) {
        this.packageLength = packageLength;
    }

    public Integer getPackageWidth() {
        return packageWidth;
    }

    public void setPackageWidth(Integer packageWidth) {
        this.packageWidth = packageWidth;
    }

    public Integer getPackageHeight() {
        return packageHeight;
    }

    public void setPackageHeight(Integer packageHeight) {
        this.packageHeight = packageHeight;
    }

    public Long getFreightTemplateId() {
        return freightTemplateId;
    }

    public void setFreightTemplateId(Long freightTemplateId) {
        this.freightTemplateId = freightTemplateId;
    }

    public Long getPromiseTemplateId() {
        return promiseTemplateId;
    }

    public void setPromiseTemplateId(Long promiseTemplateId) {
        this.promiseTemplateId = promiseTemplateId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Integer getWsValidNum() {
        return wsValidNum;
    }

    public void setWsValidNum(Integer wsValidNum) {
        this.wsValidNum = wsValidNum;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigDecimal getPackagingWeight() {
        return packagingWeight;
    }

    public void setPackagingWeight(BigDecimal packagingWeight) {
        this.packagingWeight = packagingWeight;
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

    public String getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(String templateIds) {
        this.templateIds = templateIds;
    }

    public Boolean getPricingShow() {
        return pricingShow;
    }

    public void setPricingShow(Boolean pricingShow) {
        this.pricingShow = pricingShow;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public Boolean getMobileTerminal() {
        return mobileTerminal;
    }

    public void setMobileTerminal(Boolean mobileTerminal) {
        this.mobileTerminal = mobileTerminal;
    }

    public String getMobileRemark() {
        return mobileRemark;
    }

    public void setMobileRemark(String mobileRemark) {
        this.mobileRemark = mobileRemark;
    }
}