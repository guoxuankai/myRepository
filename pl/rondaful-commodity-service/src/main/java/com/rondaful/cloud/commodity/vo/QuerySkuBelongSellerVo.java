package com.rondaful.cloud.commodity.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class QuerySkuBelongSellerVo {

	@ApiModelProperty(value = "品连sku",required=true)
	private List<String> systemSkuList;
	
	@ApiModelProperty(value = "卖家ID",required=true)
	private Long sellerId;

	public List<String> getSystemSkuList() {
		return systemSkuList;
	}

	public void setSystemSkuList(List<String> systemSkuList) {
		this.systemSkuList = systemSkuList;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	
	
}
