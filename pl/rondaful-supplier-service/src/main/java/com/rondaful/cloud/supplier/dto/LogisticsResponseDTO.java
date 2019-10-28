package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;
import java.util.List;

public class LogisticsResponseDTO implements Serializable {

	private String warehouseName;
	
	private String warehouseCode;

	private String warehouseId;
	
	private String foreignWarehouseName; 
	
	private List<LogisticsDTO> list;

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

	public List<LogisticsDTO> getList() {
		return list;
	}

	public void setList(List<LogisticsDTO> list) {
		this.list = list;
	}
	
	public String getForeignWarehouseName() {
		return foreignWarehouseName;
	}

	public void setForeignWarehouseName(String foreignWarehouseName) {
		this.foreignWarehouseName = foreignWarehouseName;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Override
	public String toString() {
		return "LogisticsResponseDTO{" +
				"warehouseName='" + warehouseName + '\'' +
				", warehouseCode='" + warehouseCode + '\'' +
				", warehouseId='" + warehouseId + '\'' +
				", foreignWarehouseName='" + foreignWarehouseName + '\'' +
				", list=" + list +
				'}';
	}
}
