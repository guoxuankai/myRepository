package com.rondaful.cloud.seller.entity.ebay;

public class ShippingServiceOptions {
	private boolean freeShipping;  //是否免运费
	private String shippingService; //运输服务方式
	private String shippingServiceAdditionalCost; //运费附加
	private String shippingServiceCost; //运费
	private Integer shippingServicePriority; //运输服务优先级
	
	public boolean isFreeShipping() {
		return freeShipping;
	}
	public void setFreeShipping(boolean freeShipping) {
		this.freeShipping = freeShipping;
	}
	public String getShippingService() {
		return shippingService;
	}
	public void setShippingService(String shippingService) {
		this.shippingService = shippingService;
	}
	public String getShippingServiceAdditionalCost() {
		return shippingServiceAdditionalCost;
	}
	public void setShippingServiceAdditionalCost(String shippingServiceAdditionalCost) {
		this.shippingServiceAdditionalCost = shippingServiceAdditionalCost;
	}
	public String getShippingServiceCost() {
		return shippingServiceCost;
	}
	public void setShippingServiceCost(String shippingServiceCost) {
		this.shippingServiceCost = shippingServiceCost;
	}
	public Integer getShippingServicePriority() {
		return shippingServicePriority;
	}
	public void setShippingServicePriority(Integer shippingServicePriority) {
		this.shippingServicePriority = shippingServicePriority;
	}
}
