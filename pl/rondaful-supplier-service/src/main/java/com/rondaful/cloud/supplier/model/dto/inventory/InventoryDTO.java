package com.rondaful.cloud.supplier.model.dto.inventory;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/6/17
 * @Description:
 */
public class InventoryDTO implements Serializable {
    private static final long serialVersionUID = -2804400861579285800L;

    @ApiModelProperty(value = "")
    private String pictureUrl;

    @ApiModelProperty(value = "品连sku")
    private String pinlianSku;

    @ApiModelProperty(value = "供应商sku")
    private String supplierSku;

    @ApiModelProperty(value = "商品名称")
    private String commodityName;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "在途数量")
    private Integer instransitQty;

    @ApiModelProperty(value = "待上架数量")
    private Integer pendingQty;

    @ApiModelProperty(value = "可用数量")
    private Integer availableQty;

    @ApiModelProperty(value = "故障品数量")
    private Integer defectsQty;

    @ApiModelProperty(value = "待出库")
    private Integer waitingShippingQty;

    @ApiModelProperty(value = "待调入")
    private Integer tuneInQty;

    @ApiModelProperty(value = "待调出")
    private Integer tuneOutQty;

    @ApiModelProperty(value = "预警值")
    private Integer warnVal;

    @ApiModelProperty(value = "备货数量")
    private Integer stockingQty;

    @ApiModelProperty(value = "缺货数量")
    private Integer piNoStockQty;

    @ApiModelProperty(value = "最后更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @ApiModelProperty(value = "本地待出库")
    private Integer localWaitingShippingQty;

    @ApiModelProperty(value = "本地可用")
    private Integer localAvailableQty;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
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

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
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

    public Integer getInstransitQty() {
        return instransitQty;
    }

    public void setInstransitQty(Integer instransitQty) {
        this.instransitQty = instransitQty;
    }

    public Integer getPendingQty() {
        return pendingQty;
    }

    public void setPendingQty(Integer pendingQty) {
        this.pendingQty = pendingQty;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

    public Integer getDefectsQty() {
        return defectsQty;
    }

    public void setDefectsQty(Integer defectsQty) {
        this.defectsQty = defectsQty;
    }

    public Integer getWaitingShippingQty() {
        return waitingShippingQty;
    }

    public void setWaitingShippingQty(Integer waitingShippingQty) {
        this.waitingShippingQty = waitingShippingQty;
    }

    public Integer getTuneInQty() {
        return tuneInQty;
    }

    public void setTuneInQty(Integer tuneInQty) {
        this.tuneInQty = tuneInQty;
    }

    public Integer getTuneOutQty() {
        return tuneOutQty;
    }

    public void setTuneOutQty(Integer tuneOutQty) {
        this.tuneOutQty = tuneOutQty;
    }

    public Integer getWarnVal() {
        return warnVal;
    }

    public void setWarnVal(Integer warnVal) {
        this.warnVal = warnVal;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getStockingQty() {
        return stockingQty;
    }

    public void setStockingQty(Integer stockingQty) {
        this.stockingQty = stockingQty;
    }

    public Integer getPiNoStockQty() {
        return piNoStockQty;
    }

    public void setPiNoStockQty(Integer piNoStockQty) {
        this.piNoStockQty = piNoStockQty;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getLocalWaitingShippingQty() {
        return localWaitingShippingQty;
    }

    public void setLocalWaitingShippingQty(Integer localWaitingShippingQty) {
        this.localWaitingShippingQty = localWaitingShippingQty;
    }

    public Integer getLocalAvailableQty() {
        return (this.localWaitingShippingQty==null||this.getLocalWaitingShippingQty()<1)?this.availableQty:(this.availableQty-this.getLocalWaitingShippingQty());
    }

    public void setLocalAvailableQty(Integer localAvailableQty) {
        this.localAvailableQty = localAvailableQty;
    }
}
