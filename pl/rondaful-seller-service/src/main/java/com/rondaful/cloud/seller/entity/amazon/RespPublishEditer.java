package com.rondaful.cloud.seller.entity.amazon;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;

/**
 * 编辑时返回的数据
 * @author ouxiangfeng
 *
 */
public class RespPublishEditer {
	
	@ApiModelProperty(value="id")
	private Long id;
	
	@ApiModelProperty(value="ext,封装的数据")
	private String ext;
	
	@ApiModelProperty(value="批次号")
	private String batchNo;
	
	@ApiModelProperty(value="刊登状态, 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线")
	private Integer status;
	
	@ApiModelProperty(value="提交时的数据源")
	private String publishMessage;

	private Integer warehouseId;
	
	private Integer logisticsType;
	
	
	public Integer getLogisticsType() {
		return logisticsType;
	}

	public void setLogisticsType(Integer logisticsType) {
		this.logisticsType = logisticsType;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getPublishMessage() {
		return publishMessage;
	}

	public void setPublishMessage(String publishMessage) {
		this.publishMessage = publishMessage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
	
	
}
