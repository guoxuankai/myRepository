package com.rondaful.cloud.supplier.model.dto.inventory;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/7/11
 * @Description:
 */
public class CombineSelectDTO implements Serializable {
    private static final long serialVersionUID = -1783241553937432252L;

    private Integer warehouseId;

    private String warehouseName;

    private Boolean isAmple;

    private String serviceCode;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Boolean getIsAmple() {
        return isAmple;
    }

    public void setIsAmple(Boolean isAmple) {
        this.isAmple = isAmple;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}


