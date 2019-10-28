package com.brandslink.cloud.user.entity;

import java.io.Serializable;

/**
 * 用于根据角色查询所有该角色下的仓库信息
 *
 * @ClassName UserRoleResult
 * @Author tianye
 * @Date 2019/6/14 18:06
 * @Version 1.0
 */
public class RoleWarehouseResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer roleId;

    private String warehouseName;

    private String warehouseCode;

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
}
