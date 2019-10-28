package com.rondaful.cloud.user.entity;

import java.io.Serializable;

public class RoleMenuKey implements Serializable{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2217039283243407307L;

	private Integer roleId;
	
	private Integer menuId;

	

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
	
	
}
