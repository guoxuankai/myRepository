package com.rondaful.cloud.user.entity;

import com.rondaful.cloud.common.entity.user.MenuCommon;
import com.rondaful.cloud.common.entity.user.RoleCommon;
import com.rondaful.cloud.common.entity.user.UserCommon;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

public class UserAll implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -72278226233652906L;

	private UserCommon user;

	private List<RoleCommon> roles;

	private List<MenuCommon> menus;

	private HashSet<String> urls;   // 用户所能访问的url列表

	public Integer getUserId() {
		return user.getUserid();
	}

	public UserCommon getUser() {
		return user;
	}

	public String getLoginName() {
		return user.getLoginName();
	}
	
	public void setUser(UserCommon user) {
		this.user = user;
	}

	public List<RoleCommon> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleCommon> roles) {
		this.roles = roles;
	}

	public List<MenuCommon> getMenus() {
		return menus;
	}

	public void setMenus(List<MenuCommon> menus) {
		this.menus = menus;
	}

	public HashSet<String> getUrls() {
		return urls;
	}

	public void setUrls(HashSet<String> urls) {
		this.urls = urls;
	}
}
