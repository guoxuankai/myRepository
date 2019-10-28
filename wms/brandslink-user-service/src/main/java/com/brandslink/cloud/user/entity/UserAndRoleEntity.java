package com.brandslink.cloud.user.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 账号角色关联实体类
 *
 * @author tianye
 * @date 2019-06-11 15:45:53
 */
public class UserAndRoleEntity implements Serializable {

    @ApiModelProperty(value = "账户id")
    private Integer userId;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    private static final long serialVersionUID = 1L;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}