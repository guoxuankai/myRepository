package com.rondaful.cloud.user.model.dto.download;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/9/4
 * @Description:
 */
public class QueryDownloadDTO implements Serializable {
    private static final long serialVersionUID = 9148801781464452367L;

    private Integer status;

    private Date startTime;

    private Date endTime;

    private Integer userId;

    private Integer platformType;

    @ApiModelProperty(value = "展示条数",name = "totalCount",dataType = "Integer")
    private Integer pageSize;

    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Integer")
    private Integer currentPage;

    private String languageType;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Integer platformType) {
        this.platformType = platformType;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }
}
