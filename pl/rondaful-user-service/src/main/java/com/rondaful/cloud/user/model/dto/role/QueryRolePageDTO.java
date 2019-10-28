package com.rondaful.cloud.user.model.dto.role;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/4/25
 * @Description:
 */
public class QueryRolePageDTO implements Serializable {
    private static final long serialVersionUID = 4326049403756975916L;

    @ApiModelProperty(value = "展示条数",name = "totalCount",dataType = "Long")
    private Integer pageSize;
    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Long")
    private Integer currentPage;
    @ApiModelProperty(value = "平台类型   0供应商平台  1卖家平台  2管理平台")
    private Integer platform;
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    @ApiModelProperty(value = "截至时间")
    private Date endTime;
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    @ApiModelProperty(value = "归属账号")
    private Integer attribution;

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

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getAttribution() {
        return attribution;
    }

    public void setAttribution(Integer attribution) {
        this.attribution = attribution;
    }
}
