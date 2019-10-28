package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value ="MessageDetailDTO")
public class MessageDetailDTO implements Serializable {

	private static final long serialVersionUID = -1837795347480044513L;

	@ApiModelProperty(value = "标题")
	private String title;
	
	@ApiModelProperty(value = "发送人")
	private String sender;
	
	@ApiModelProperty(value = "发送时间")
	private Date sendTime;
	
	@ApiModelProperty(value = "消息类型 0客服 1售后 2商品 3订单 4库存 5财务 9系统公告")
	private String type;
	
	@ApiModelProperty(value = "内容")
	private String content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "MessageDetail [title=" + title + ", sender=" + sender + ", sendTime=" + sendTime + ", type=" + type
				+ ", content=" + content + "]";
	}
	
}
