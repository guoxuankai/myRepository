package com.rondaful.cloud.supplier.model.dto;


import java.io.Serializable;

/**
 * 返回消息实体类
 * */
public class FeignResult<T> implements Serializable{

	private static final long serialVersionUID = 5033274552965677865L;
	private Boolean success;
	private String errorCode;
	private String msg;
	private T data;

	public FeignResult(boolean success, String errorCode, String msg) {
		this.success = success;
		this.errorCode = errorCode;
		this.msg = msg;
	}

	public FeignResult(){}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}