package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;

public class SkuMapUpdateStatusVo implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private Integer status;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}
