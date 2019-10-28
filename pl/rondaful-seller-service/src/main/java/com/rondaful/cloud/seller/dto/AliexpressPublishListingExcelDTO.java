package com.rondaful.cloud.seller.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.google.common.collect.Lists;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingAttribute;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProduct;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author
 *
 */
public class AliexpressPublishListingExcelDTO {

	private Long id;


	@Excel(name = "创建时间[creationTime]",format = "yyyy-MM-dd HH:mm:ss",width = 18)
	private Date createTime;

	@ApiModelProperty(value = "")
	//@Excel(name = "上线时间[onlineTime]",format = "yyyy-MM-dd HH:mm:ss",width = 18)
	private Date onlineTime;

	@ApiModelProperty(value = "")
	@Excel(name = "上线时间[successTime]",format = "yyyy-MM-dd HH:mm:ss",width = 18)
	private Date successTime;

	@Excel(name = "商品ID[id]",format = "yyyy-MM-dd HH:mm:ss",width = 18)
	private Long itemId;

	@ApiModelProperty(value = "刊登状态 1: 草稿  2: 刊登中 3: 刊登失败 4:审核中  5: 审核失败,6:正在销售 7 已下架")
	private Integer publishStatus;

	@Excel(name = "刊登状态[publishStatus]",width = 18)
	private String publishStatusName;

	@ApiModelProperty(value = "更新状态 1更新中2更新成功3更新失败")
	private Integer updateStatus;

	@Excel(name = "更新状态[updateStatus]",width = 18)
	private String updateStatusName;

	@ApiModelProperty(value = "卖家账号(自定义账号)")
	@Excel(name = "刊登账号[publishAccount]",width = 18)
	private String publishAccount;

	@ApiModelProperty(value = "商品分类Id")
	private Long categoryId;

	@Excel(name = "商品分类[publishAccount]",width = 18)
	private String categoryName;

	@ApiModelProperty(value = "品连spu")
	@Excel(name = "品连spu[spu]",width = 18)
	private String plSpu;

	@ApiModelProperty(value = "产品标题")
	@Excel(name = "商品标题[title]",width = 18)
	private String title;

	@Excel(name = "商品属性[commodityProperty]",width = 18)
	private String commodityProperty;

	@Excel(name = "品连sku[sku]",width = 18)
	private String plSku;

	@ApiModelProperty(value = "平台sku")
	@Excel(name = "平台sku[platformSku]",width = 18)
	private String platformSku;

	@ApiModelProperty(value = "库存")
	@Excel(name = "库存[inventory]",width = 18)
	private String inventory;

	@Excel(name = "sku属性[skuProperty]",width = 18)
	private String skuProperty;

	@ApiModelProperty(value = "零售价")
	@Excel(name = "零售价(USD)[retailPrice]",width = 18)
	private String retailPrice;

	@ApiModelProperty(value = "sku图片")
	@Excel(name = "sku图片[skuImage]",width = 18)
	private String skuImage;

	@ApiModelProperty(value = "库存扣减策略，总共有2种：下单减库存(place_order_withhold)和支付减库存(payment_success_deduct)。")
	@Excel(name = "库存扣减方式[reduceStrategy]",width = 18)
	private String reduceStrategy;

	@ApiModelProperty(value = "发货期(取值范围:1-60;单位:天)")
	@Excel(name = "发货期[deliveryTime]",width = 18)
	private Integer deliveryTime;

	@ApiModelProperty(value = "最小计量单位")
	private Integer unit;

	@Excel(name = "最小计量单位[unit]",width = 18)
	@ApiModelProperty(value = "最小计量单位")
	private String unitName;

	@ApiModelProperty(value = "图片")
	@Excel(name = "商品图片[productImage]",width = 18)
	private String productImage;

	@ApiModelProperty(value = "商品详情")
	@Excel(name = "商品描述[productDetails]",width = 18)
	private String productDetails;

	@ApiModelProperty(value = "商品包装后的重量(公斤/袋)")
	@Excel(name = "商品包装后重量（公斤/斤）[productWeight]",width = 18)
	private BigDecimal packagingWeight;

	@ApiModelProperty(value = "长")
	private Integer packageLength;

	@ApiModelProperty(value = "宽")
	private Integer packageWidth;

	@ApiModelProperty(value = "高")
	private Integer packageHeight;

	@Excel(name = "商品包装后的尺寸(cm)[packageVolume]",width = 18)
	private String packageVolume;

	@ApiModelProperty(value = "运费模板")
	private Long freightTemplateId;

	@ApiModelProperty(value = "服务模板")
	private Long promiseTemplateId;

	@ApiModelProperty(value = "商品分组")
	private Long groupId;

	@ApiModelProperty(value = "运费模板")
	@Excel(name = "运费模板[freightTemplateName]",width = 18)
	private String freightTemplateName;

	@ApiModelProperty(value = "服务模板")
	@Excel(name = "服务模板[promiseTemplateName]",width = 18)
	private String promiseTemplateName;

	@ApiModelProperty(value = "商品分组")
	@Excel(name = "商品分组[groupName]",width = 18)
	private String groupName;



	@ApiModelProperty(value = "刊登类型 2：单属性 1：多属性")
	private Integer publishType;
	@ApiModelProperty(value = "是否是平台listing 0是历史刊登数据 1是新刊登 2速卖通平台")
	private Integer platformListing;


	@ApiModelProperty(value = "AliexpressPublishListingAttribute")
	private List<AliexpressPublishListingAttribute> listAttribute = Lists.newArrayList();

	@ApiModelProperty(value = "AliexpressPublishListingProduct")
	private List<AliexpressPublishListingProduct> listProduct = Lists.newArrayList();

	public List<AliexpressPublishListingProduct> getListProduct() {
		return listProduct;
	}

	public void setListProduct(List<AliexpressPublishListingProduct> listProduct) {
		this.listProduct = listProduct;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}

	public String getPublishAccount() {
		return publishAccount;
	}

	public void setPublishAccount(String publishAccount) {
		this.publishAccount = publishAccount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Integer getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(Integer updateStatus) {
		this.updateStatus = updateStatus;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getPlSpu() {
		return plSpu;
	}

	public void setPlSpu(String plSpu) {
		this.plSpu = plSpu;
	}

	public String getReduceStrategy() {
		return reduceStrategy;
	}

	public void setReduceStrategy(String reduceStrategy) {
		this.reduceStrategy = reduceStrategy;
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

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public String getProductDetails() {
		return productDetails;
	}

	public void setProductDetails(String productDetails) {
		this.productDetails = productDetails;
	}

	public BigDecimal getPackagingWeight() {
		return packagingWeight;
	}

	public void setPackagingWeight(BigDecimal packagingWeight) {
		this.packagingWeight = packagingWeight;
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

	public String getFreightTemplateName() {
		return freightTemplateName;
	}

	public void setFreightTemplateName(String freightTemplateName) {
		this.freightTemplateName = freightTemplateName;
	}

	public String getPromiseTemplateName() {
		return promiseTemplateName;
	}

	public void setPromiseTemplateName(String promiseTemplateName) {
		this.promiseTemplateName = promiseTemplateName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getPublishType() {
		return publishType;
	}

	public void setPublishType(Integer publishType) {
		this.publishType = publishType;
	}

	public Integer getPlatformListing() {
		return platformListing;
	}

	public void setPlatformListing(Integer platformListing) {
		this.platformListing = platformListing;
	}

	public List<AliexpressPublishListingAttribute> getListAttribute() {
		return listAttribute;
	}

	public void setListAttribute(List<AliexpressPublishListingAttribute> listAttribute) {
		this.listAttribute = listAttribute;
	}

	public String getPublishStatusName() {
		return publishStatusName;
	}

	public void setPublishStatusName(String publishStatusName) {
		this.publishStatusName = publishStatusName;
	}

	public String getUpdateStatusName() {
		return updateStatusName;
	}

	public void setUpdateStatusName(String updateStatusName) {
		this.updateStatusName = updateStatusName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCommodityProperty() {
		return commodityProperty;
	}

	public void setCommodityProperty(String commodityProperty) {
		this.commodityProperty = commodityProperty;
	}

	public String getPlSku() {
		return plSku;
	}

	public void setPlSku(String plSku) {
		this.plSku = plSku;
	}

	public String getPlatformSku() {
		return platformSku;
	}

	public void setPlatformSku(String platformSku) {
		this.platformSku = platformSku;
	}

	public String getInventory() {
		return inventory;
	}

	public void setInventory(String inventory) {
		this.inventory = inventory;
	}

	public String getSkuProperty() {
		return skuProperty;
	}

	public void setSkuProperty(String skuProperty) {
		this.skuProperty = skuProperty;
	}

	public String getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}

	public String getSkuImage() {
		return skuImage;
	}

	public void setSkuImage(String skuImage) {
		this.skuImage = skuImage;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getPackageVolume() {
		return packageVolume;
	}

	public void setPackageVolume(String packageVolume) {
		this.packageVolume = packageVolume;
	}

	public Date getSuccessTime() {
		return successTime;
	}

	public void setSuccessTime(Date successTime) {
		this.successTime = successTime;
	}
}
