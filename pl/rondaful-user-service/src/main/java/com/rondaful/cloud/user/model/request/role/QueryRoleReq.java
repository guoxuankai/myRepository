package com.rondaful.cloud.user.model.request.role;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
public class QueryRoleReq implements Serializable {
    private static final long serialVersionUID = 1690740477616495138L;

    @ApiModelProperty(value = "展示条数",name = "totalCount",dataType = "Integer",required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Integer",required = true)
    private Integer currentPage;
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)",name = "startTime",dataType = "Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @ApiModelProperty(value = "截至时间(yyyy-MM-dd HH:mm:ss)",name = "endTime",dataType = "Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @ApiModelProperty(value = "角色名称",name = "roleName",dataType = "String")
    private String roleName;
    @ApiModelProperty(value = "用户id",name = "userId",dataType = "Integer")
    private Integer userId;
    @ApiModelProperty(value = "平台类型:0供应商;1卖家",name = "platformType",dataType = "Integer")
    private Integer platformType;

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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }
}
