package com.rondaful.cloud.user.model.request.role;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
public class AddRoleReq implements Serializable {
    private static final long serialVersionUID = -6060943020153384834L;

    @ApiModelProperty(value = "角色名称",name = "roleName",dataType = "String")
    private String roleName;
    @ApiModelProperty(value = "备注",name = "remark",dataType = "String")
    private String remark;
    @ApiModelProperty(value = "功能权限数组json字符串",name = "menus",dataType = "String")
    private String menus;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMenus() {
        return menus;
    }

    public void setMenus(String menus) {
        this.menus = menus;
    }
}
