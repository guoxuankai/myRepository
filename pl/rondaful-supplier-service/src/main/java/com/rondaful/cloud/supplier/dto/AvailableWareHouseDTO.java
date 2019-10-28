package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;

public class AvailableWareHouseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String wareHouseProvider;
	
	private String wareHouserName;

	public String getWareHouseProvider() {
		return wareHouseProvider;
	}

	public void setWareHouseProvider(String wareHouseProvider) {
		this.wareHouseProvider = wareHouseProvider;
	}

	public String getWareHouserName() {
		return wareHouserName;
	}

	public void setWareHouserName(String wareHouserName) {
		this.wareHouserName = wareHouserName;
	}

}
