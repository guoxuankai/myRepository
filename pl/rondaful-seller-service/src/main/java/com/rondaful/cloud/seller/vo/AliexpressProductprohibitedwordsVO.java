package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "商品关键字查询")
public class AliexpressProductprohibitedwordsVO implements Serializable {


	private static final long serialVersionUID = 8818966154771103502L;
	@ApiModelProperty(value = "刊登账号id")
	private Long empowerId;
	@ApiModelProperty(value = "分类id")
	private Long categoryId;
	@ApiModelProperty(value = "标题")
	private String title;
	@ApiModelProperty(value = "关键字")
	private String keywords;
	@ApiModelProperty(value = "商品类目属性")
	private String productProperties;
	@ApiModelProperty(value = "商品的详细描述")
	private String detail;

	public Long getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Long empowerId) {
		this.empowerId = empowerId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getProductProperties() {
		return productProperties;
	}

	public void setProductProperties(String productProperties) {
		this.productProperties = productProperties;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
