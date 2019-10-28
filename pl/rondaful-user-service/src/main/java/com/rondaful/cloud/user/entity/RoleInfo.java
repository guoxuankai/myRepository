package com.rondaful.cloud.user.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class RoleInfo implements Serializable {

	@ApiModelProperty(value="角色id",required = false)
    private Integer roleId;

	@ApiModelProperty(value="角色名称",required = true)
    private String roleName;

    private static final long serialVersionUID = 1L;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}