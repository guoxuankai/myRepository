package com.rondaful.cloud.seller.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

/**
 * 返回前端的dto对象信息
 * @author songjie
 *
 */
public class EbayPublishListingAPPDTO {
	
	private Integer id;    //主键
	private String itemId;  //ebay返回的产品id
	private String picture; // 图片
	private String listingDuration; //刊登天数
	private Integer listingType; //刊登类型(1=单属性一口价 2=多属性一口价 3=拍卖)")
	private String storeName;    // 店名
	private Integer status;     //3=已下线,4=在线
	private String title; //标题
	private Date onlineTime; //上线时间
	
	public Date getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
