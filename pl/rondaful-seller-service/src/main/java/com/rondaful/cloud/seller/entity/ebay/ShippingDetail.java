package com.rondaful.cloud.seller.entity.ebay;

public class ShippingDetail {

	private String shippingCategory;
	private Integer shippingServiceId;
	private Integer shippingtimeMin;
	private String shippingService;
	private String desc;
	private Integer shippingTimeMax;
	public String getShippingCategory() {
		return shippingCategory;
	}
	public void setShippingCategory(String shippingCategory) {
		this.shippingCategory = shippingCategory;
	}
	public Integer getShippingServiceId() {
		return shippingServiceId;
	}
	public void setShippingServiceId(Integer shippingServiceId) {
		this.shippingServiceId = shippingServiceId;
	}
	public Integer getShippingtimeMin() {
		return shippingtimeMin;
	}
	public void setShippingtimeMin(Integer shippingtimeMin) {
		this.shippingtimeMin = shippingtimeMin;
	}
	public String getShippingService() {
		return shippingService;
	}
	public void setShippingService(String shippingService) {
		this.shippingService = shippingService;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Integer getShippingTimeMax() {
		return shippingTimeMax;
	}
	public void setShippingTimeMax(Integer shippingTimeMax) {
		this.shippingTimeMax = shippingTimeMax;
	}
}
