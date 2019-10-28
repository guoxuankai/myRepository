package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;


public class EmpowerRequestVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer empowerId;
	
	private String account;
	
	private Integer platform;
	
	private String dataType;

	
	public Integer getEmpowerId() {
		return empowerId;
	}

	public void setEmpowerId(Integer empowerId) {
		this.empowerId = empowerId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
}
