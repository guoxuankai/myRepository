package com.rondaful.cloud.commodity.vo;

import com.rondaful.cloud.commodity.entity.CommodityBase;

public class CommoditySearchVo {

	private String commodityId;
	
	private String searchKeyWords;
	
	private CommodityBase commodityBase;
	
	public String getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(String commodityId) {
		this.commodityId = commodityId;
	}

	public String getSearchKeyWords() {
		return searchKeyWords;
	}

	public void setSearchKeyWords(String searchKeyWords) {
		this.searchKeyWords = searchKeyWords;
	}

	public CommodityBase getCommodityBase() {
		return commodityBase;
	}

	public void setCommodityBase(CommodityBase commodityBase) {
		this.commodityBase = commodityBase;
	}
	
}
