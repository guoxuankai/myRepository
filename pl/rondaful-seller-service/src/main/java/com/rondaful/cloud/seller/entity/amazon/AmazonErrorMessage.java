package com.rondaful.cloud.seller.entity.amazon;

/**
 * Amazon请求返回消息
 * @author ouxiangfeng
 *
 */
public class AmazonErrorMessage {
	private boolean isSuccess = Boolean.FALSE;
	private String errorCode;
	private String errorType;
	private String errorMsg;
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
}
