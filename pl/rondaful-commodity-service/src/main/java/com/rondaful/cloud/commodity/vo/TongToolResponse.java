package com.rondaful.cloud.commodity.vo;

import java.io.Serializable;

public class TongToolResponse implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer code;
	
	private String datas;
	
	private String message;
	
	private Object others;

	
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDatas() {
		return datas;
	}

	public void setDatas(String datas) {
		this.datas = datas;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getOthers() {
		return others;
	}

	public void setOthers(Object others) {
		this.others = others;
	}
	
}
