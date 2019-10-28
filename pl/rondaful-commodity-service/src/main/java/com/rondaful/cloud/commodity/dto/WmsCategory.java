package com.rondaful.cloud.commodity.dto;

import java.io.Serializable;

/**
* @Description:wms分类
* @author:范津 
* @date:2019年7月25日 下午3:24:50
 */
public class WmsCategory implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//分类唯一编码
	private String categoryCode;
	
	//分类等级
	private int categoryLevel;
	
	//分类名称（中文）
	private String categoryName;
	
	//分类名称（英文）
	private String categoryNameEn;
	
	//父级分类编码（如果是第一级，填写0）
	private String parentCode;
	
	//数据来源 (01-品连优选 02-利郎达ERP)
	private String dataSources;

	
	
	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public int getCategoryLevel() {
		return categoryLevel;
	}

	public void setCategoryLevel(int categoryLevel) {
		this.categoryLevel = categoryLevel;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryNameEn() {
		return categoryNameEn;
	}

	public void setCategoryNameEn(String categoryNameEn) {
		this.categoryNameEn = categoryNameEn;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getDataSources() {
		return dataSources;
	}

	public void setDataSources(String dataSources) {
		this.dataSources = dataSources;
	}


}
