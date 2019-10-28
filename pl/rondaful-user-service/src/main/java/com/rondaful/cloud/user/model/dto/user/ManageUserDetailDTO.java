package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/29
 * @Description:
 */
public class ManageUserDetailDTO extends ManageUserDTO{
    private static final long serialVersionUID = 830206339781329894L;


    @ApiModelProperty(value = "职位")
    private List<String> jobs;

    @ApiModelProperty(value = "角色")
    private List<BindAccountDetailDTO> roleAll;

    @ApiModelProperty(value = "部门")
    private List<BindAccountDetailDTO> department;

    @ApiModelProperty(value = "地区")
    private List<BindAccountDetailDTO> area;


    public List<String> getJobs() {
        return jobs;
    }

    public void setJobs(List<String> jobs) {
        this.jobs = jobs;
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

    public List<BindAccountDetailDTO> getRoleAll() {
        return roleAll;
    }

    public void setRoleAll(List<BindAccountDetailDTO> roleAll) {
        this.roleAll = roleAll;
    }
}
