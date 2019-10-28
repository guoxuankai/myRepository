package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;


public class ApiSkuWarehouseInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer warehouseId;
	
	private String warehousePrice;

	
	
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehousePrice() {
		return warehousePrice;
	}

	public void setWarehousePrice(String warehousePrice) {
		this.warehousePrice = warehousePrice;
	}
	
}
