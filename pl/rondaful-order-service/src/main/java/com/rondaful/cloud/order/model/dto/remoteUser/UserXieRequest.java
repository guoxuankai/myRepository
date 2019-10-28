package com.rondaful.cloud.order.model.dto.remoteUser;

import java.io.Serializable;
import java.util.List;

/**
 * 根据平台类型获取用户信息实体类
 * @author Administrator
 *
 */
public class UserXieRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2473742317950776526L;

	private Integer userId;
	
	private String userName;
	
	private Integer platformType;

	private List<UserXieRequest> childs;


	public UserXieRequest(Integer userId, String userName, Integer platformType) {
		this.userId = userId;
		this.userName = userName;
		this.platformType = platformType;
	}

	public UserXieRequest() {

	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}

	public List<UserXieRequest> getChilds() {
		return childs;
	}

	public void setChilds(List<UserXieRequest> childs) {
		this.childs = childs;
	}
}
