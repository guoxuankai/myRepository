package com.rondaful.cloud.seller.vo;

import java.util.List;

public class PublishListingParamsVO {

	private List<String> platformSku;
	//private String site;
	private String type;
	private Integer empowerId;
	
	public List<String> getPlatformSku() {
		return platformSku;
	}
	public void setPlatformSku(List<String> platformSku) {
		this.platformSku = platformSku;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getEmpowerId() {
		return empowerId;
	}
	public void setEmpowerId(Integer empowerId) {
		this.empowerId = empowerId;
	}
	
	
	
}
