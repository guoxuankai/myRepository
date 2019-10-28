package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value ="MessageSearchTerm")
public class MessageSearchTerm implements Serializable {

	private static final long serialVersionUID = -4839920552617580078L;

	@ApiModelProperty(value = "开始时间")
	private String startDate;
	
	@ApiModelProperty(value = "结束时间")
	private String endDate;
	
	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "类型")
	private String type;
	
	@ApiModelProperty(value = "状态 0未读 1已读 9删除 默认0未读 ")
	private String status;
	
	@ApiModelProperty(value = "用户id")
	private String userId;
	
	@ApiModelProperty(value = "所属系统 0管理后台 1供应商 2卖家")
	private String belongSys;
	
	@ApiModelProperty(value = "消息id数组")
	private String[] idList;
	
	@ApiModelProperty(value = "用户名")
	private String userName;
	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBelongSys() {
		return belongSys;
	}

	public void setBelongSys(String belongSys) {
		this.belongSys = belongSys;
	}
	
	public String[] getIdList() {
		return idList;
	}

	public void setIdList(String[] idList) {
		this.idList = idList;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "MessageSearchTerm [startDate=" + startDate + ", endDate=" + endDate + ", title=" + title + ", type="
				+ type + ", status=" + status + ", userId=" + userId + ", belongSys=" + belongSys + ", idList="
				+ Arrays.toString(idList) + ", userName=" + userName + "]";
	}

}
