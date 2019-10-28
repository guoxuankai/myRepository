package com.rondaful.cloud.supplier.model.dto.basics;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/6/17
 * @Description:
 */
public class WarehouseDTO implements Serializable {
    private static final long serialVersionUID = -2809936233673866830L;

    @ApiModelProperty(value = "仓库服务商名称code:RONDAFUL:利郎达 GOODCANG:谷仓")
    private String firmCode;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "归属供应商id：0(通用)  ")
    private Integer supplierId;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "供应商名称")
    private Integer supplierName;

    @ApiModelProperty(value = "供应链公司id")
    private Integer supplyId;
    @ApiModelProperty(value = "供应链公司名称")
    private String supplyName;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "国家编码")
    private String countryCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "自定义标识")
    private String name;

    @ApiModelProperty(value = "应用标识")
    private String appToken;

    @ApiModelProperty(value = "密钥")
    private String appKey;

    @ApiModelProperty(value = "仓储账号id")
    private Integer logisticsUserId;


    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getFirmCode() {
        return firmCode;
    }

    public void setFirmCode(String firmCode) {
        this.firmCode = firmCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(Integer supplierName) {
        this.supplierName = supplierName;
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

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }

    public Integer getLogisticsUserId() {
        return logisticsUserId;
    }

    public void setLogisticsUserId(Integer logisticsUserId) {
        this.logisticsUserId = logisticsUserId;
    }

    public String getSupplyName() {
        return supplyName;
    }

    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName;
    }
}
