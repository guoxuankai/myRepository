package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;

/**
 * 仓库与物流的关联表
 * 
 * @author xieyanbin
 *
 * @2019年5月5日 
 * @version v2.2
 */
public class LogisticsWarehouseRelation implements Serializable {

	private Long id;
	//仓库名称
	private String warehouseName;
	//仓库code
	private String warehouseCode;
	//物流方式code
	private String logisticsId;
	//仓库状态 默认0 0停用 1启用
	private String warehouseStatus;
	//物流方式状态 默认0 0停用 1启用
	private String logisticsStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getLogisticsId() {
		return logisticsId;
	}

	public void setLogisticsId(String logisticsId) {
		this.logisticsId = logisticsId;
	}

	public String getWarehouseStatus() {
		return warehouseStatus;
	}

	public void setWarehouseStatus(String warehouseStatus) {
		this.warehouseStatus = warehouseStatus;
	}

	public String getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(String logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

	@Override
	public String toString() {
		return "LogisticsWarehouseRelation [id=" + id + ", warehouseName=" + warehouseName + ", warehouseCode="
				+ warehouseCode + ", logisticsId=" + logisticsId + ", warehouseStatus=" + warehouseStatus
				+ ", logisticsStatus=" + logisticsStatus + "]";
	}

}
