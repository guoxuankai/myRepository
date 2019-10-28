package com.rondaful.cloud.seller.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

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

	private String url;
	
	@ApiModelProperty(value = "内容")
	private String content;

	private String fileName;

	private String fileNameEn;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileNameEn() {
		return fileNameEn;
	}

	public void setFileNameEn(String fileNameEn) {
		this.fileNameEn = fileNameEn;
	}

	@Override
	public String toString() {
		return "MessageDetailDTO{" +
				"title='" + title + '\'' +
				", sender='" + sender + '\'' +
				", sendTime=" + sendTime +
				", type='" + type + '\'' +
				", url='" + url + '\'' +
				", content='" + content + '\'' +
				", fileName='" + fileName + '\'' +
				", fileNameEn='" + fileNameEn + '\'' +
				'}';
	}
}
