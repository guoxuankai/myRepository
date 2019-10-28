package com.rondaful.cloud.commodity.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.servlet.http.HttpServletRequest;

import com.rondaful.cloud.commodity.entity.Trial;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FreightTrialDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "仓库id", required = true)
	private Integer warehouseId;

	@ApiModelProperty(value = "国家简码", required = true)
	private String countryCode;

	@ApiModelProperty(value = "邮政编码 | 谷仓必传")
	private String postCode;

	@ApiModelProperty(value = "物流方式code")
	private String logisticsCode;

	@ApiModelProperty(value = "所属平台 1(eBay) 2(Amazon) 3(Wish) 4(AliExpress)")
	private String platformType;

	@ApiModelProperty(value = "sku集合")
	private List<Trial.Skus> skuList;


	

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getLogisticsCode() {
		return logisticsCode;
	}

	public void setLogisticsCode(String logisticsCode) {
		this.logisticsCode = logisticsCode;
	}

	public String getPlatformType() {
		return platformType;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}

	public List<Trial.Skus> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<Trial.Skus> skuList) {
		this.skuList = skuList;
	}
	
	
}
