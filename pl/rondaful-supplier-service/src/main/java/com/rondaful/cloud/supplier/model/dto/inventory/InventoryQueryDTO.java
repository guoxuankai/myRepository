package com.rondaful.cloud.supplier.model.dto.inventory;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/17
 * @Description:
 */
public class InventoryQueryDTO implements Serializable {
    private static final long serialVersionUID = 8076883506265311693L;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "查询状态")
    private Integer status;

    @ApiModelProperty(value = "品连sku")
    private String pinlianSku;

    @ApiModelProperty(value = "供应商sku")
    private String supplierSku;

    private String languageType;

    @ApiModelProperty(value = "当前页",name = "currentPage",dataType = "Integer")
    private Integer currentPage;

    @ApiModelProperty(value = "每页展示条数",name = "currentPage",dataType = "Integer")
    private Integer pageSize;

    @ApiModelProperty(value = "商品名称",name = "commodityName",dataType = "String")
    private String commodityName;

    private Integer sellerId;

    private List<Integer> supplierIds;

    private List<String> pinlianSkus;

    public List<Integer> getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(List<Integer> supplierIds) {
        this.supplierIds = supplierIds;
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

    public String getPinlianSku() {
        return pinlianSku;
    }

    public void setPinlianSku(String pinlianSku) {
        this.pinlianSku = pinlianSku;
    }

    public String getSupplierSku() {
        return supplierSku;
    }

    public void setSupplierSku(String supplierSku) {
        this.supplierSku = supplierSku;
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

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public List<String> getPinlianSkus() {
        return pinlianSkus;
    }

    public void setPinlianSkus(List<String> pinlianSkus) {
        this.pinlianSkus = pinlianSkus;
    }
}
