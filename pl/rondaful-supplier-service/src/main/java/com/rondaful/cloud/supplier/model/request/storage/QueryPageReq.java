package com.rondaful.cloud.supplier.model.request.storage;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public class QueryPageReq implements Serializable {
    private static final long serialVersionUID = -552018154624805765L;

    @ApiModelProperty(value = "入库单号")
    private String receivingCode;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;
    @ApiModelProperty(value = "当前页")
    private Integer currentPage;
    @ApiModelProperty(value = "展示条数")
    private Integer pageSize;
    @ApiModelProperty(value = "供应商id")
    private Integer userId;

    public String getReceivingCode() {
        return receivingCode;
    }

    public void setReceivingCode(String receivingCode) {
        this.receivingCode = receivingCode;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
