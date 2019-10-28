package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value ="SkuPushVo")
public class SkuPushVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "账号ID")
	private Integer accountId;
	
	@ApiModelProperty(value = "接口类型，1：新增，2：修改")
	private Integer type;
	
	@ApiModelProperty(value = "sku数组")
	private List<String> skuList;
	
	@ApiModelProperty(value = "sku状态")
	private Integer status;


	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<String> skuList) {
		this.skuList = skuList;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
