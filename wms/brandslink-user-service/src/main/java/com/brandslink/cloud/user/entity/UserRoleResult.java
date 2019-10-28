package com.brandslink.cloud.user.entity;

import java.io.Serializable;

/**
 * 用于根据角色名称查询所有包含该角色的用户的所有角色名称
 *
 * @ClassName UserRoleResult
 * @Author tianye
 * @Date 2019/6/14 18:06
 * @Version 1.0
 */
public class UserRoleResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String roleName;

    private String warehouseName;

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

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
