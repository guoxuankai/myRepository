package com.rondaful.cloud.supplier.model.response.third;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/7/29
 * @Description:
 */
public class WarehouseInventoryReq implements Serializable {

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    private String pinlianSku;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseName;

    @ApiModelProperty(value = "英文仓库名称")
    private String warehouseNameEn;

    @ApiModelProperty(value = "在途库存")
    private Integer instransitQty;

    @ApiModelProperty(value = "可用库存")
    private Integer availableQty;

    @ApiModelProperty(value = "数量")
    private Integer qty;

    @ApiModelProperty(value = "待出库")
    private Integer waitingShippingQty;

    @ApiModelProperty(value = "待入库")
    private Integer allocatingQty;

    @ApiModelProperty(value = "故障品库存")
    private Integer defectsQty;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getPinlianSku() {
        return pinlianSku;
    }

    public void setPinlianSku(String pinlianSku) {
        this.pinlianSku = pinlianSku;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseNameEn() {
        return warehouseNameEn;
    }

    public void setWarehouseNameEn(String warehouseNameEn) {
        this.warehouseNameEn = warehouseNameEn;
    }

    public Integer getInstransitQty() {
        return instransitQty;
    }

    public void setInstransitQty(Integer instransitQty) {
        this.instransitQty = instransitQty;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getWaitingShippingQty() {
        return waitingShippingQty;
    }

    public void setWaitingShippingQty(Integer waitingShippingQty) {
        this.waitingShippingQty = waitingShippingQty;
    }

    public Integer getAllocatingQty() {
        return allocatingQty;
    }

    public void setAllocatingQty(Integer allocatingQty) {
        this.allocatingQty = allocatingQty;
    }

    public Integer getDefectsQty() {
        return defectsQty;
    }

    public void setDefectsQty(Integer defectsQty) {
        this.defectsQty = defectsQty;
    }
}
