package com.rondaful.cloud.user.entity;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class JsonUtil implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2175107431541208526L;

	@ApiModelProperty(value = "用户id列表", required = false)
	private List<Integer> userIds;

	public List<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}
}

