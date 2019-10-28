package com.rondaful.cloud.supplier.model.response.provide.third;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/7/29
 * @Description:
 */
public class WarehouseReq implements Serializable {
    private static final long serialVersionUID = 2822214803222768945L;

    private String warehouseName;

    private String warehouseCode;

    private String countryCode;

    private String warehouseNameEn;

    public String getWarehouseNameEn() {
        return warehouseNameEn;
    }

    public void setWarehouseNameEn(String warehouseNameEn) {
        this.warehouseNameEn = warehouseNameEn;
    }

    public WarehouseReq(){}

    public WarehouseReq(String warehouseName, String warehouseCode, String countryCode) {
        this.warehouseName = warehouseName;
        this.warehouseCode = warehouseCode;
        this.countryCode = countryCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
