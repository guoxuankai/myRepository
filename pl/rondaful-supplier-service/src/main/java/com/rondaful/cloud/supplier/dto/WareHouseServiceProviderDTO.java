package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;
import java.util.List;

public class WareHouseServiceProviderDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer serviceId;
	private String  serviceProviderName;
	private Integer supplyChainId;
	private String supplyChainCompany;
	

	private List<WareHouseAuthorizeDTO> authorizeList;

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceProviderName() {
		return serviceProviderName;
	}

	public Integer getSupplyChainId() {
		return supplyChainId;
	}

	public void setSupplyChainId(Integer supplyChainId) {
		this.supplyChainId = supplyChainId;
	}

	public String getSupplyChainCompany() {
		return supplyChainCompany;
	}

	public void setSupplyChainCompany(String supplyChainCompany) {
		this.supplyChainCompany = supplyChainCompany;
	}
	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}

	public List<WareHouseAuthorizeDTO> getAuthorizeList() {
		return authorizeList;
	}

	public void setAuthorizeList(List<WareHouseAuthorizeDTO> authorizeList) {
		this.authorizeList = authorizeList;
	}

}
