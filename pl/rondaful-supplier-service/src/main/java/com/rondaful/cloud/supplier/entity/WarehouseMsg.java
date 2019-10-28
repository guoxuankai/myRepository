package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;

public class WarehouseMsg implements Serializable {

	private static final long serialVersionUID = 3684332239888234328L;

	private String wareHouseName;

	private String wareHouseNameEn;
	
	private String wareHouseCode;

	private String warehouseId;

	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}

	public String getWareHouseCode() {
		return wareHouseCode;
	}

	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWareHouseNameEn() {
		return wareHouseNameEn;
	}

	public void setWareHouseNameEn(String wareHouseNameEn) {
		this.wareHouseNameEn = wareHouseNameEn;
	}

	@Override
	public String toString() {
		return "WarehouseMsg{" +
				"wareHouseName='" + wareHouseName + '\'' +
				", wareHouseNameEn='" + wareHouseNameEn + '\'' +
				", wareHouseCode='" + wareHouseCode + '\'' +
				", warehouseId='" + warehouseId + '\'' +
				'}';
	}
}
