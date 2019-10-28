package com.rondaful.cloud.seller.entity;

import java.io.Serializable;

public class WarehouseMsg implements Serializable {

	private static final long serialVersionUID = 3684332239888234328L;

	private String warehouseName;
	
	private String warehouseCode;

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	@Override
	public String toString() {
		return "WarehouseMsg [warehouseName=" + warehouseName + ", warehouseCode=" + warehouseCode + "]";
	}
	
	
}
