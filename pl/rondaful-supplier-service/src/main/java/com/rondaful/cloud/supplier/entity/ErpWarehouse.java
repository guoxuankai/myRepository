package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;

public class ErpWarehouse implements Serializable {

	private static final long serialVersionUID = -2864474420082596106L;

	//仓库名称
	private String name;

	//仓库code
	private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "ErpWarehouse [name=" + name + ", code=" + code + "]";
	}

}
