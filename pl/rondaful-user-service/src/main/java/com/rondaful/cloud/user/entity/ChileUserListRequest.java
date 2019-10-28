package com.rondaful.cloud.user.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 根据平台类型获取用户信息实体类
 * @author Administrator
 *
 */
public class ChileUserListRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2473742317950776526L;

	private Integer userId;
	
	private String userName;
	
	private Integer platformType;

	private String loginName;

	private List<ChileUserListRequest> childs;


	public ChileUserListRequest(Integer userId, String userName, Integer platformType) {
		this.userId = userId;
		this.userName = userName;
		this.platformType = platformType;
	}

	public ChileUserListRequest() {

	}

	public ChileUserListRequest(Integer userId, String userName, Integer platformType, String loginName) {
		this.userId = userId;
		this.userName = userName;
		this.platformType = platformType;
		this.loginName = loginName;
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

	public List<ChileUserListRequest> getChilds() {
		return childs;
	}

	public void setChilds(List<ChileUserListRequest> childs) {
		this.childs = childs;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
}
