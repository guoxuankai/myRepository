package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "多店铺刊登AliexpressMoreVO")
public class AliexpressMoreVO implements Serializable {


	private static final long serialVersionUID = -8378239504983353606L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "刊登状态 1: 草稿  2: 刊登中")
	private Integer publishStatus;

	@ApiModelProperty(value = "授权店铺")
	private List<Long> empowerIds;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}

	public List<Long> getEmpowerIds() {
		return empowerIds;
	}

	public void setEmpowerIds(List<Long> empowerIds) {
		this.empowerIds = empowerIds;
	}
}
