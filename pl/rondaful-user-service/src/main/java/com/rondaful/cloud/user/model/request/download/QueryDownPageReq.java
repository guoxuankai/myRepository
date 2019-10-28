package com.rondaful.cloud.user.model.request.download;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/9/5
 * @Description:
 */
public class QueryDownPageReq implements Serializable {
    private static final long serialVersionUID = -5199808220725993820L;

    @ApiModelProperty(value = "0:下载中  1:完成  4:失败",name = "startTime",dataType = "string")
    private Integer status;

    @ApiModelProperty(value = "起始创建时间:yyyy-MM-dd HH:mm:ss",name = "startTime",dataType = "string")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "起始结束时间:yyyy-MM-dd HH:mm:ss",name = "startTime",dataType = "string")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "展示条数",name = "totalCount",dataType = "Integer")
    private Integer pageSize;

    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Integer")
    private Integer currentPage;

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
