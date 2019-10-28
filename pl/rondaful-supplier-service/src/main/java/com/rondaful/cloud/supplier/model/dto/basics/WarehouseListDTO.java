package com.rondaful.cloud.supplier.model.dto.basics;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/14
 * @Description:
 */
public class WarehouseListDTO implements Serializable {
    private static final long serialVersionUID = -3853983362637626980L;

    private Integer id;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    private String warehouseName;

    public WarehouseListDTO(){}

    public WarehouseListDTO(Integer id, String warehouseCode) {
        this.id = id;
        this.warehouseCode = warehouseCode;
    }

    public WarehouseListDTO(Integer id, String warehouseCode, String warehouseName) {
        this.id = id;
        this.warehouseCode = warehouseCode;
        this.warehouseName = warehouseName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}
