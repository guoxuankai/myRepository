package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
* @Description:通途变参货品列表
* @author:范津 
* @date:2019年9月12日 下午4:50:16
 */
public class TongToolGoods implements Serializable{
	private static final long serialVersionUID = 1L;

	//货品成本(最新成本)
	private BigDecimal goodsCurrentCost;
	
	//货号(SKU)
	private String goodsSku;
	
	//货品属性列表
	private List<TongToolGoodsVariation> goodsVariation;
	
	//货品重量(克)
	private Integer goodsWeight;

	
	public BigDecimal getGoodsCurrentCost() {
		return goodsCurrentCost;
	}

	public void setGoodsCurrentCost(BigDecimal goodsCurrentCost) {
		this.goodsCurrentCost = goodsCurrentCost;
	}

	public String getGoodsSku() {
		return goodsSku;
	}

	public void setGoodsSku(String goodsSku) {
		this.goodsSku = goodsSku;
	}

	public List<TongToolGoodsVariation> getGoodsVariation() {
		return goodsVariation;
	}

	public void setGoodsVariation(List<TongToolGoodsVariation> goodsVariation) {
		this.goodsVariation = goodsVariation;
	}

	public Integer getGoodsWeight() {
		return goodsWeight;
	}

	public void setGoodsWeight(Integer goodsWeight) {
		this.goodsWeight = goodsWeight;
	}
	
}
