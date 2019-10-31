package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/5/6
 * @Description:
 */
public class SellerUserChildDetail extends SellerUserDTO {
    private static final long serialVersionUID = 6104399673383382978L;

    @ApiModelProperty(value = "角色")
    private List<BindAccountDetailDTO> roleAll;

    @ApiModelProperty(value = "部门")
    private List<BindAccountDetailDTO> department;

    @ApiModelProperty(value = "地区")
    private List<BindAccountDetailDTO> area;



    public List<BindAccountDetailDTO> getRoleAll() {
        return roleAll;
    }

    public void setRoleAll(List<BindAccountDetailDTO> roleAll) {
        this.roleAll = roleAll;
    }

    public List<BindAccountDetailDTO> getDepartment() {
        return department;
    }

    public void setDepartment(List<BindAccountDetailDTO> department) {
        this.department = department;
    }

    public List<BindAccountDetailDTO> getArea() {
        return area;
    }

    public void setArea(List<BindAccountDetailDTO> area) {
        this.area = area;
    }
}