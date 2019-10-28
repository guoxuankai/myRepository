package com.rondaful.cloud.seller.dto;

import java.util.Date;
import java.util.List;

import com.rondaful.cloud.seller.entity.EbayPublishListingVariant;
import com.rondaful.cloud.seller.entity.ebay.ListingVariant;
import io.swagger.annotations.ApiModelProperty;

/**
 * 返回前端的dto对象信息
 * @author songjie
 *
 */
public class EbayPublishListingDTO {
	
	private Integer id;
	private String itemId;
	private String picture;
	private String listingDuration;
	private Integer listingType;
	private String site;
	private String publishAccount;
	private String title;
	private String remarks;
	private String statusValue;
	@ApiModelProperty(value = "1=草稿,2=刊登中,3=已下线,4=在线,5=刊登失败 8平台删除")
	private Integer status;
	private String plSku;
	private String plSpu;
	private String platformSku;
	private String startPrice;
	private Integer quantity;
	private Date creationTime;
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	private Date publishTime;
	private Date endTime;
	private Date onlineTime;
	@ApiModelProperty(value = "创建人刊登人")
	private String createName;//创建人名称
	private Long createId;
	@ApiModelProperty(value = "发货仓库")
	private String warehouseCode;

	@ApiModelProperty(value = "更新状态 1更新中2更新成功3更新失败")
	private Integer updateStatus;//1更新中2更新成功3更新失败
	@ApiModelProperty(value = "是否是平台listing 0是历史刊登数据 1是新刊登 2ebay平台")
	private Integer platformListing;

	private List<EbayPublishListingVariant> variantList;
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}
	public Integer getListingType() {
		return listingType;
	}
	public void setListingType(Integer listingType) {
		this.listingType = listingType;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getListingDuration() {
		return listingDuration;
	}
	public void setListingDuration(String listingDuration) {
		this.listingDuration = listingDuration;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
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
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getStatusValue() {
		return statusValue;
	}
	public void setStatusValue(String statusValue) {
		this.statusValue = statusValue;
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
	public String getStartPrice() {
		return startPrice;
	}
	public void setStartPrice(String startPrice) {
		this.startPrice = startPrice;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public Date getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public List<EbayPublishListingVariant> getVariantList() {
		return variantList;
	}
	public void setVariantList(List<EbayPublishListingVariant> variantList) {
		this.variantList = variantList;
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

	public Integer getPlatformListing() {
		return platformListing;
	}

	public void setPlatformListing(Integer platformListing) {
		this.platformListing = platformListing;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Long getCreateId() {
		return createId;
	}

	public void setCreateId(Long createId) {
		this.createId = createId;
	}
}
