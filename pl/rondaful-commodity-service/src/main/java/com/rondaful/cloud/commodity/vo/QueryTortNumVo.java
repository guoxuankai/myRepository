package com.rondaful.cloud.commodity.vo;

import io.swagger.annotations.ApiModelProperty;

public class QueryTortNumVo {

	@ApiModelProperty(value = "平台，1：eBay，2：Amazon，3：wish，4：AliExpress")
	private Integer platform;
	
	@ApiModelProperty(value = "站点编码")
	private String siteCode;
	
	@ApiModelProperty(value = "品连spu")
	private String spu;
	
	@ApiModelProperty(value = "系统sku")
	private String systemSku;
	
	

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
	
	public String getSpu() {
		return spu;
	}

	public void setSpu(String spu) {
		this.spu = spu;
	}

	public String getSystemSku() {
		return systemSku;
	}

	public void setSystemSku(String systemSku) {
		this.systemSku = systemSku;
	}
}
