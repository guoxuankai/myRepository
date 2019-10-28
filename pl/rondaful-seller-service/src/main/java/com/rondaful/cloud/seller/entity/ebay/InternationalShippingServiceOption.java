package com.rondaful.cloud.seller.entity.ebay;

public class InternationalShippingServiceOption {
	private String shippingService;  //运输服务方式
	private String shippingServiceAdditionalCost;  //增加费用
	private String shippingServiceCost; //附加费
	private String shipToLocation; //运输到以下地区
	private Integer shippingServicePriority; //运输服务优先级
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
	public String getShipToLocation() {
		return shipToLocation;
	}
	public void setShipToLocation(String shipToLocation) {
		this.shipToLocation = shipToLocation;
	}
	public Integer getShippingServicePriority() {
		return shippingServicePriority;
	}
	public void setShippingServicePriority(Integer shippingServicePriority) {
		this.shippingServicePriority = shippingServicePriority;
	}
}
