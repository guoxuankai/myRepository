package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;

public class OrderTransferVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "平台，ebay,amazon,aliexpress")
	private String platform;

	@ApiModelProperty(value = "平台sku和品连sku组合，{platformSKU:pinlianSKU}")
	private Map<String, String> skuRelationMap;

	@ApiModelProperty(value = "卖家店铺ID（授权ID）")
	private Integer empowerID;

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Map<String, String> getSkuRelationMap() {
		return skuRelationMap;
	}

	public void setSkuRelationMap(Map<String, String> skuRelationMap) {
		this.skuRelationMap = skuRelationMap;
	}

	public Integer getEmpowerID() {
		return empowerID;
	}

	public void setEmpowerID(Integer empowerID) {
		this.empowerID = empowerID;
	}

}
