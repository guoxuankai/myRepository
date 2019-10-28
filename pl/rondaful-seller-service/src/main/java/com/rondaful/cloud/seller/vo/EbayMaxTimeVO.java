package com.rondaful.cloud.seller.vo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *
 * @author chenhan
 *
 */
public class EbayMaxTimeVO {
	@ApiModelProperty(value = "刊登商品id")
	List<String> itemIds = Lists.newArrayList();
	@ApiModelProperty(value = "站点")
	private String site;
	@ApiModelProperty(value = "刊登账号id")
	private Integer empowerId;

	public List<String> getItemIds() {
		return itemIds;
	}

	public void setItemIds(List<String> itemIds) {
		this.itemIds = itemIds;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Integer getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Integer empowerId) {
		this.empowerId = empowerId;
	}
}
