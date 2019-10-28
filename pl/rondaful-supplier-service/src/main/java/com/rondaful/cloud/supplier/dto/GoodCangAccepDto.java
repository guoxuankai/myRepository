package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;

public class GoodCangAccepDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private String AppToken;

    private String Sign;

    private String MessageType;

    private String Message;

    private String MessageId;

    private String SendTime;

	public String getAppToken() {
		return AppToken;
	}

	public void setAppToken(String appToken) {
		AppToken = appToken;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getMessageType() {
		return MessageType;
	}

	public void setMessageType(String messageType) {
		MessageType = messageType;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getMessageId() {
		return MessageId;
	}

	public void setMessageId(String messageId) {
		MessageId = messageId;
	}

	public String getSendTime() {
		return SendTime;
	}

	public void setSendTime(String sendTime) {
		SendTime = sendTime;
	}
    
    
}
