package com.rondaful.cloud.supplier.model.request.basic;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/8/5
 * @Description:
 */
public class WarehousePageReq implements Serializable {
    private static final long serialVersionUID = 2089342063993075987L;

    @ApiModelProperty(value = "状态:1-ok  4-no",dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value = "自定义账号名",dataType = "String")
    private String name;

    @ApiModelProperty(value = "服务商编码",dataType = "String")
    private String firmCode;

    @ApiModelProperty(value = "供应商id",dataType = "Integer")
    private Integer supplierId;

    @ApiModelProperty(value = "当前页",dataType = "Integer")
    private Integer currentPage;

    @ApiModelProperty(value = "每页条数",dataType = "Integer")
    private Integer pageSize;

    @ApiModelProperty(value = "1-共有仓,2-私有仓",dataType = "Integer")
    private Integer type;

    @ApiModelProperty(value = "仓库id",dataType = "Integer")
    private Integer warehouseId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirmCode() {
        return firmCode;
    }

    public void setFirmCode(String firmCode) {
        this.firmCode = firmCode;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
