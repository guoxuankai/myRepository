package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

public class QuerySkuMapForOrderVo implements Serializable{
 
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "来源订单ID")
	private String sourceOrderId;

	@ApiModelProperty(value = "来源订单项ID")
	private String sourceOrderLineItemId;
	
	 @ApiModelProperty(value = "平台：amazon, eBay, wish, aliexpress,other")
	 private String platform;
	 
	 @ApiModelProperty(value = "授权id（店铺id）")
	 private String authorizationId;
	 
	 @ApiModelProperty(value = "平台sku")
	 private String platformSku;
	 
	 @ApiModelProperty(value = "卖家ID")
	 private String sellerId;
	 
	 private List<SkuMapBind> skuBinds;

	 
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public QuerySkuMapForOrderVo() {
	}

	public QuerySkuMapForOrderVo(String sourceOrderId, String sourceOrderLineItemId, String platform, String authorizationId, String platformSku, String sellerId, List<SkuMapBind> skuBinds) {
		this.sourceOrderId = sourceOrderId;
		this.sourceOrderLineItemId = sourceOrderLineItemId;
		this.platform = platform;
		this.authorizationId = authorizationId;
		this.platformSku = platformSku;
		this.sellerId = sellerId;
		this.skuBinds = skuBinds;
	}

	@Override
	public String toString() {
		return "QuerySkuMapForOrderVo{" +
				"sourceOrderId='" + sourceOrderId + '\'' +
				", sourceOrderLineItemId='" + sourceOrderLineItemId + '\'' +
				", platform='" + platform + '\'' +
				", authorizationId='" + authorizationId + '\'' +
				", platformSku='" + platformSku + '\'' +
				", sellerId='" + sellerId + '\'' +
				", skuBinds=" + skuBinds +
				'}';
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
}
