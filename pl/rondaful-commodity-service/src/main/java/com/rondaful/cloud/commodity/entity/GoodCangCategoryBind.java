package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;

/**
* @Description:谷仓商品分类和品连商品分类绑定
* @author:范津 
* @date:2019年4月25日 下午5:05:41
 */
public class GoodCangCategoryBind implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	//品连三级分类ID
	private Integer pinlianCategoty3Id;
	
	//谷仓三级分类ID
	private Integer granaryCategoty3Id;
	
	private Integer version;
	
	private Integer categoryId3;
	
	private Integer categoryId2;
	
	private Integer categoryId1;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPinlianCategoty3Id() {
		return pinlianCategoty3Id;
	}

	public void setPinlianCategoty3Id(Integer pinlianCategoty3Id) {
		this.pinlianCategoty3Id = pinlianCategoty3Id;
	}

	public Integer getGranaryCategoty3Id() {
		return granaryCategoty3Id;
	}

	public void setGranaryCategoty3Id(Integer granaryCategoty3Id) {
		this.granaryCategoty3Id = granaryCategoty3Id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getCategoryId3() {
		return categoryId3;
	}

	public void setCategoryId3(Integer categoryId3) {
		this.categoryId3 = categoryId3;
	}

	public Integer getCategoryId2() {
		return categoryId2;
	}

	public void setCategoryId2(Integer categoryId2) {
		this.categoryId2 = categoryId2;
	}

	public Integer getCategoryId1() {
		return categoryId1;
	}

	public void setCategoryId1(Integer categoryId1) {
		this.categoryId1 = categoryId1;
	}
	
}
