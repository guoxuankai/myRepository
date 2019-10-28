package com.rondaful.cloud.seller.dto;

import com.rondaful.cloud.seller.entity.ebay.ListingVariant;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 返回前端的dto对象信息
 * @author
 *
 */
public class EbayHistoryDTO {


	private String plSpu;
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPlSpu() {
		return plSpu;
	}

	public void setPlSpu(String plSpu) {
		this.plSpu = plSpu;
	}
}