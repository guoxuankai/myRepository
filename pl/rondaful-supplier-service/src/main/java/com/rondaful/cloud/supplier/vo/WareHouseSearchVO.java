package com.rondaful.cloud.supplier.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "仓库查询VO")
public class WareHouseSearchVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "仓库服务商Id")
	private Integer wareHouseServiceProvider;
	
	@ApiModelProperty(value = "仓库状态  [0=停用  1=启用 ] ")
	private Integer wareHouseStatus;
	
	@ApiModelProperty(value = "仓库国家")
	private String wareHouseCountry;
	
	@ApiModelProperty(value = "仓库名称")
	private String wareHouseName;
	
	@ApiModelProperty(value = "仓库code")
	private List<String> wareHouseCode;
	
	public Integer getWareHouseServiceProvider() {
		return wareHouseServiceProvider;
	}

	public void setWareHouseServiceProvider(Integer wareHouseServiceProvider) {
		this.wareHouseServiceProvider = wareHouseServiceProvider;
	}

	public Integer getWareHouseStatus() {
		return wareHouseStatus;
	}

	public void setWareHouseStatus(Integer wareHouseStatus) {
		this.wareHouseStatus = wareHouseStatus;
	}

	public String getWareHouseCountry() {
		return wareHouseCountry;
	}

	public void setWareHouseCountry(String wareHouseCountry) {
		this.wareHouseCountry = wareHouseCountry;
	}

	public String getWareHouseName() {
		return wareHouseName;
	}

	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}

	public List<String> getWareHouseCode() {
		return wareHouseCode;
	}

	public void setWareHouseCode(List<String> wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}

}

