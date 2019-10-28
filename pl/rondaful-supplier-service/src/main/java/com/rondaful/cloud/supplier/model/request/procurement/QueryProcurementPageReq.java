package com.rondaful.cloud.supplier.model.request.procurement;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/6/20
 * @Description:
 */
public class QueryProcurementPageReq implements Serializable {
    private static final long serialVersionUID = -4990100074994040609L;

    @ApiModelProperty(value="供货商id",name = "providerId",dataType = "Integer")
    private Integer providerId;

    @ApiModelProperty(value="查询开始时间:yyyy-MM-dd HH:mm:ss",name = "startTime",dataType = "String")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value="查询结束时间:yyyy-MM-dd HH:mm:ss",name = "startTime",dataType = "String")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value="状态",name = "status",dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value="仓库id",name = "warehouseId",dataType = "Integer")
    private Integer warehouseId;

    @ApiModelProperty(value="当前页",name = "currentPage",dataType = "Integer",required = true)
    private Integer currentPage;

    @ApiModelProperty(value="每页条数",name = "pageSize",dataType = "Integer",required = true)
    private Integer pageSize;

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
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

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
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
