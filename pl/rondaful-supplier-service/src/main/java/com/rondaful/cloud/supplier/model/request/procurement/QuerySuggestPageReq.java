package com.rondaful.cloud.supplier.model.request.procurement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/20
 * @Description:
 */
@ApiModel(value = "采购建议查询实体类")
public class QuerySuggestPageReq implements Serializable {
    private static final long serialVersionUID = 3303905951477499858L;

    @ApiModelProperty(value="开始时间: yyyy-MM-dd HH:mm:ss",name = "startTime",dataType = "string")
    private String startTime;

    @ApiModelProperty(value="结束时间: yyyy-MM-dd HH:mm:ss",name = "endTime",dataType = "string")
    private String endTime;

    @ApiModelProperty(value="仓库id",name = "warehouseId",dataType = "Integer")
    private Integer warehouseId;

    @ApiModelProperty(value="仓库id",name = "status",dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value="当前页",name = "currentPage",dataType = "Integer",required = true)
    private Integer currentPage;

    @ApiModelProperty(value="每页条数",name = "pageSize",dataType = "Integer",required = true)
    private Integer pageSize;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
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
