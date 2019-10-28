package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "亚马逊站点")
public class AmazonSiteVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "区域")
	private String areaCode;
	@ApiModelProperty(value = "站点")
	private List<MarketplaceVO> marketplaceVOList;

	public List<MarketplaceVO> getMarketplaceVOList() {
		return marketplaceVOList;
	}

	public void setMarketplaceVOList(List<MarketplaceVO> marketplaceVOList) {
		this.marketplaceVOList = marketplaceVOList;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
}
