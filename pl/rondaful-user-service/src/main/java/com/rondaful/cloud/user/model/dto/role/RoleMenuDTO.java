package com.rondaful.cloud.user.model.dto.role;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
public class RoleMenuDTO implements Serializable {
    private static final long serialVersionUID = -5157039912782400088L;

    @ApiModelProperty(value = "菜单id")
    private Integer menuId;

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

}
