package com.rondaful.cloud.seller.entity.ebay.store;

import java.io.Serializable;
import java.util.List;

/**
 * ebay 店铺对象  按需求补充字段
 * @author songjie
 *
 */
public class Store implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<CustomCategory> customCategories;

	public List<CustomCategory> getCustomCategories() {
		return customCategories;
	}

	public void setCustomCategories(List<CustomCategory> customCategories) {
		this.customCategories = customCategories;
	}
	
}
