package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;

/**
* @Description:sku仓库相关信息
* @author:范津 
* @date:2019年8月13日 下午4:30:34
 */
public class SkuInventoryVo implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer warehouseId;
	
	private String warehouseName;
	
	private int inventory;
	
	private String warehousePrice;

	
	
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public String getWarehousePrice() {
		return warehousePrice;
	}

	public void setWarehousePrice(String warehousePrice) {
		this.warehousePrice = warehousePrice;
	}
	
}
