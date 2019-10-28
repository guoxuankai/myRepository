package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;
import java.util.List;



public class ApiCategoryResponseVo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
    private Long id;
    
    private Integer categoryLevel;

    private String categoryName;

    private String categoryNameEn;

    private Long categoryParentId;

    private List<ApiCategoryResponseVo> children;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCategoryLevel() {
		return categoryLevel;
	}

	public void setCategoryLevel(Integer categoryLevel) {
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

	public Long getCategoryParentId() {
		return categoryParentId;
	}

	public void setCategoryParentId(Long categoryParentId) {
		this.categoryParentId = categoryParentId;
	}

	public List<ApiCategoryResponseVo> getChildren() {
		return children;
	}

	public void setChildren(List<ApiCategoryResponseVo> children) {
		this.children = children;
	}

}
