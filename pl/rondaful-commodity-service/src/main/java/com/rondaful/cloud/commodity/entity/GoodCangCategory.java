package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.util.List;


/**
* @Description:谷仓商品分类
* @author:范津 
* @date:2019年4月25日 下午2:02:06
 */
public class GoodCangCategory implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	//品类ID
	private Integer category_id;
	//品类父ID
	private Integer parent_category_id;
	//品类中文名
	private String category_name;
	//品类英文名
	private String category_name_en;
	//品类级别
	private Integer category_level;
	
	private String sortKey;

    private String sort;
    
	private List<GoodCangCategory> children;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}

	public Integer getParent_category_id() {
		return parent_category_id;
	}

	public void setParent_category_id(Integer parent_category_id) {
		this.parent_category_id = parent_category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getCategory_name_en() {
		return category_name_en;
	}

	public void setCategory_name_en(String category_name_en) {
		this.category_name_en = category_name_en;
	}

	public Integer getCategory_level() {
		return category_level;
	}

	public void setCategory_level(Integer category_level) {
		this.category_level = category_level;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<GoodCangCategory> getChildren() {
		return children;
	}

	public void setChildren(List<GoodCangCategory> children) {
		this.children = children;
	}
}
