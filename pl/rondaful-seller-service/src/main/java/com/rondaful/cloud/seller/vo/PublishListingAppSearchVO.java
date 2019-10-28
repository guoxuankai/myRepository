package com.rondaful.cloud.seller.vo;

import java.io.Serializable;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "EbayPublishListingSearch")
public class PublishListingAppSearchVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "卖家")
    private String seller;

	List<Integer> empowerIds;//数据权限
    
	@ApiModelProperty(value = "分页页数")
	private String page;

	@ApiModelProperty(value = "每页条数")
	private String row;


	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

	public List<Integer> getEmpowerIds() {
		return empowerIds;
	}

	public void setEmpowerIds(List<Integer> empowerIds) {
		this.empowerIds = empowerIds;
	}
}
