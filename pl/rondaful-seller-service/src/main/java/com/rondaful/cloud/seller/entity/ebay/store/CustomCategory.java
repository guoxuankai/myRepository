package com.rondaful.cloud.seller.entity.ebay.store;

import java.io.Serializable;
import java.util.List;

/**
 * 店铺子对象
 * @author Administrator
 *
 */
public class CustomCategory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Integer order;
	private Long categoryID;
	private List<CustomCategory> childCategory;  
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public Long getCategoryID() {
		return categoryID;
	}
	public void setCategoryID(Long categoryID) {
		this.categoryID = categoryID;
	}
	public List<CustomCategory> getChildCategory() {
		return childCategory;
	}
	public void setChildCategory(List<CustomCategory> childCategory) {
		this.childCategory = childCategory;
	}
}
