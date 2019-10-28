package com.rondaful.cloud.order.model.dto.sysorder;


import java.io.Serializable;

/**
 * 返回消息实体类
 * */
public class FeignResult<T> implements Serializable{

	private static final long serialVersionUID = 5033274552965677865L;
	private boolean success;
	private String errorCode;
	private String msg;
	private T data;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
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