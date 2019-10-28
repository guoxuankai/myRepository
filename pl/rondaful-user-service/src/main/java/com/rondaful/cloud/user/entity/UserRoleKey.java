package com.rondaful.cloud.user.entity;

import java.io.Serializable;

public class UserRoleKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3193466011390184896L;

	private Integer userId;
	
	private Integer roleId;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	
	
}
