package com.rondaful.cloud.supplier.model.request.procurement;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/6/19
 * @Description:
 */
public class QueryPageProviderReq implements Serializable {
    private static final long serialVersionUID = 6838072732585348593L;

    @ApiModelProperty(value="供货商id",name = "id",dataType = "Integer")
    private Integer id;

    @ApiModelProperty(value="开始时间: yyyy-MM-dd",name = "startTime",dataType = "string")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @ApiModelProperty(value="结束时间: yyyy-MM-dd",name = "endTime",dataType = "string")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @ApiModelProperty(value="状态",name = "status",dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value="当前页",name = "currentPage",dataType = "Integer",required = true)
    private Integer currentPage;

    @ApiModelProperty(value="展示条数",name = "pageSize",dataType = "Integer",required = true)
    private Integer pageSize;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
