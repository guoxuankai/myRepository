package com.rondaful.cloud.supplier.model.dto.basics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/8/5
 * @Description:
 */
public class WarehouseQueryDTO implements Serializable {
    private static final long serialVersionUID = 4685350503529408261L;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "自定义账号名")
    private String name;

    private Integer id;

    @ApiModelProperty(value = "服务商编码")
    private String firmCode;

    @ApiModelProperty(value = "供应商id")
    private List<Integer> supplierIds;

    @ApiModelProperty(value = "当前页")
    private Integer currentPage;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

    @ApiModelProperty(value = "仓库id")
    private List<Integer> warehouseIds;

    private String languageType;

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

    public List<Integer> getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(List<Integer> supplierIds) {
        this.supplierIds = supplierIds;
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

    public List<Integer> getWarehouseIds() {
        return warehouseIds;
    }

    public void setWarehouseIds(List<Integer> warehouseIds) {
        this.warehouseIds = warehouseIds;
    }

    public String getLanguageType() {

        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
