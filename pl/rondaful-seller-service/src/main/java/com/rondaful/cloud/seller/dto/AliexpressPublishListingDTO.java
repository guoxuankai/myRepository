package com.rondaful.cloud.seller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.rondaful.cloud.seller.entity.AliexpressPublishListingProduct;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 返回前端的dto对象信息
 * @author
 *
 */
public class AliexpressPublishListingDTO {

	private Long id;

	@ApiModelProperty(value = "刊登状态 1: 草稿  2: 刊登中 3: 刊登失败 4:审核中  5: 审核失败,6:正在销售 7 已下架")
	private Integer publishStatus;

	@ApiModelProperty(value = "卖家账号(自定义账号)")
	private String publishAccount;

	@ApiModelProperty(value = "产品标题")
	private String title;

	@ApiModelProperty(value = "品连spu或sku")
	private String plSpuSku;

	@ApiModelProperty(value = "品连spu")
	private String plSpu;

	@ApiModelProperty(value = "刊登成功速卖通商品id")
	private Long itemId;

	@ApiModelProperty(value = "创建时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@ApiModelProperty(value = "刊登成功的时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date successTime;

	@ApiModelProperty(value = "备注信息")
	private String remark;

	@ApiModelProperty(value = "品连sku")
	private String plSku;

	@ApiModelProperty(value = "平台sku")
	private String platformSku;

	@ApiModelProperty(value = "库存")
	private Integer inventory;

	@ApiModelProperty(value = "零售价")
	private String retailPrice;

	@ApiModelProperty(value = "图片")
	private String productImage;

	@ApiModelProperty(value = "刊登类型 2：单属性 1：多属性")
	private Integer publishType;
	@ApiModelProperty(value = "是否是平台listing 0是历史刊登数据 1是新刊登 2速卖通平台")
	private Integer platformListing;
	@ApiModelProperty(value = "更新状态 1更新中2更新成功3更新失败")
	private Integer updateStatus;

	@ApiModelProperty(value = "发货仓库")
	private String warehouseCode;

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

	public String getPlSpuSku() {
		return plSpuSku;
	}

	public void setPlSpuSku(String plSpuSku) {
		this.plSpuSku = plSpuSku;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSuccessTime() {
		return successTime;
	}

	public void setSuccessTime(Date successTime) {
		this.successTime = successTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Integer getInventory() {
		return inventory;
	}

	public void setInventory(Integer inventory) {
		this.inventory = inventory;
	}

	public String getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
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

	public Integer getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(Integer updateStatus) {
		this.updateStatus = updateStatus;
	}

	public String getPlSpu() {
		return plSpu;
	}

	public void setPlSpu(String plSpu) {
		this.plSpu = plSpu;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}
}
