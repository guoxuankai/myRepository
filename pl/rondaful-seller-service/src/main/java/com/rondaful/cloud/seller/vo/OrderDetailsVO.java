package com.rondaful.cloud.seller.vo;

public class OrderDetailsVO {
	private String deliverId;
	private String id;
	private String itemAttr;
	private String itemName;
	private String itemPrice;
	private String itemUrl;
	private String sku;
	private String skuQuantity;
	private String supplierName;
	public String getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(String deliverId) {
		this.deliverId = deliverId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getItemAttr() {
		return itemAttr;
	}
	public void setItemAttr(String itemAttr) {
		this.itemAttr = itemAttr;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}
	public String getItemUrl() {
		return itemUrl;
	}
	public void setItemUrl(String itemUrl) {
		this.itemUrl = itemUrl;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getSkuQuantity() {
		return skuQuantity;
	}
	public void setSkuQuantity(String skuQuantity) {
		this.skuQuantity = skuQuantity;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
}
