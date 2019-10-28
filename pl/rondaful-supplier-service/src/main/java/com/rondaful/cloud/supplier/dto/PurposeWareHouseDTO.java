package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;

public class PurposeWareHouseDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String wareHouseName;
	private String wareHouseCode;
	private String countryCode;
	private String countryName;
	private String wareHouseType;
	public String getWareHouseName() {
		return wareHouseName;
	}
	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}
	public String getWareHouseCode() {
		return wareHouseCode;
	}
	public void setWareHouseCode(String wareHouseCode) {
		this.wareHouseCode = wareHouseCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getWareHouseType() {
		return wareHouseType;
	}
	public void setWareHouseType(String wareHouseType) {
		this.wareHouseType = wareHouseType;
	}
}
