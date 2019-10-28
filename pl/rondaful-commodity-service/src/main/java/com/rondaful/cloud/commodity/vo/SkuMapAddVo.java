package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class SkuMapAddVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	 @ApiModelProperty(value = "平台：amazon, eBay, wish, aliexpress,other")
	 private String platform;
	 
	 @ApiModelProperty(value = "平台sku")
	 private String platformSku;
	 
	 @ApiModelProperty(value = "授权id（店铺id）")
	 private String authorizationId;
	 
	 @ApiModelProperty(value = "品连sku及数量组合，sku1:2|sku2:3")
	 private String skuGroup;

	 
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPlatformSku() {
		return platformSku;
	}

	public void setPlatformSku(String platformSku) {
		this.platformSku = platformSku;
	}

	public String getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(String authorizationId) {
		this.authorizationId = authorizationId;
	}

	public String getSkuGroup() {
		return skuGroup;
	}

	public void setSkuGroup(String skuGroup) {
		this.skuGroup = skuGroup;
	}
	 
}
