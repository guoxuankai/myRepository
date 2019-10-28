package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;

/**
* @Description:通途货品属性
* @author:范津 
* @date:2019年9月12日 下午4:48:17
 */
public class TongToolGoodsVariation implements Serializable{

	private static final long serialVersionUID = 1L;

	//规格名称
	private String variationName;
	
	//规格值
	private String variationValue;
	

	public String getVariationName() {
		return variationName;
	}

	public void setVariationName(String variationName) {
		this.variationName = variationName;
	}

	public String getVariationValue() {
		return variationValue;
	}

	public void setVariationValue(String variationValue) {
		this.variationValue = variationValue;
	}
	
}
