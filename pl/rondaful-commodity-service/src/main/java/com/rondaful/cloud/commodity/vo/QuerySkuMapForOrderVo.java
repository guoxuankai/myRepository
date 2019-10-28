package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.util.List;

import com.rondaful.cloud.commodity.entity.SkuMapBind;

import io.swagger.annotations.ApiModelProperty;

public class QuerySkuMapForOrderVo implements Serializable{
 
	private static final long serialVersionUID = 1L;
	
	 @ApiModelProperty(value = "平台：amazon, eBay, wish, aliexpress,other")
	 private String platform;
	 
	 @ApiModelProperty(value = "授权id（店铺id）")
	 private String authorizationId;
	 
	 @ApiModelProperty(value = "平台sku")
	 private String platformSku;
	 
	 @ApiModelProperty(value = "卖家ID")
	 private String sellerId;
	 
	 @ApiModelProperty(value = "来源订单ID")
	 private String sourceOrderId;

	 @ApiModelProperty(value = "来源订单项ID")
	 private String sourceOrderLineItemId;
	 
	 private List<SkuMapBind> skuBinds;

	 
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getAuthorizationId() {
		return authorizationId;
	}

	public void setAuthorizationId(String authorizationId) {
		this.authorizationId = authorizationId;
	}

	public String getPlatformSku() {
		return platformSku;
	}

	public void setPlatformSku(String platformSku) {
		this.platformSku = platformSku;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public List<SkuMapBind> getSkuBinds() {
		return skuBinds;
	}

	public void setSkuBinds(List<SkuMapBind> skuBinds) {
		this.skuBinds = skuBinds;
	}

	public String getSourceOrderId() {
		return sourceOrderId;
	}

	public void setSourceOrderId(String sourceOrderId) {
		this.sourceOrderId = sourceOrderId;
	}

	public String getSourceOrderLineItemId() {
		return sourceOrderLineItemId;
	}

	public void setSourceOrderLineItemId(String sourceOrderLineItemId) {
		this.sourceOrderLineItemId = sourceOrderLineItemId;
	}
	
}
