package com.rondaful.cloud.user.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/4/28
 * @Description:
 */
public class QuerManagePageDTO implements Serializable {
    private static final long serialVersionUID = 5245321279975635529L;

    @ApiModelProperty(value = "登录账号名",name = "loginName",dataType = "String")
    private String loginName;
    @ApiModelProperty(value = "用户名",name = "userName",dataType = "String")
    private String userName;
    @ApiModelProperty(value = "职位",name = "jobs",dataType = "String")
    private String jobs;
    @ApiModelProperty(value = "部门id",name = "departmentId",dataType = "String")
    private Integer departmentId;
    @ApiModelProperty(value = "角色id",name = "roleId",dataType = "String")
    private Integer roleId;
    @ApiModelProperty(value = "开始时间yyyy-MM-dd HH:mm:ss",name = "startTime",dataType = "String")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @ApiModelProperty(value = "结束时间yyyy-MM-dd HH:mm:ss",name = "endTime",dataType = "String")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "展示条数",name = "totalCount",dataType = "Long")
    private Integer pageSize;
    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Long")
    private Integer currentPage;

    private List<Integer> userIds;

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
        this.jobs = jobs;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
