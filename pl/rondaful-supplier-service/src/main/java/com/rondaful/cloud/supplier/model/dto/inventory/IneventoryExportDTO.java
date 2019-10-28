package com.rondaful.cloud.supplier.model.dto.inventory;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xqq
 * @Date: 2019/7/25
 * @Description:
 */
public class IneventoryExportDTO implements Serializable {
    private static final long serialVersionUID = 6993326669658202234L;
    @Excel(name  = "commodity_picture",width = 20.22D)
    private String pictureUrl;

    @Excel(name  = "pinlian_sku",width = 20.22D)
    private String pinlianSku;

    @Excel(name = "supplier_sku",width = 20.22D)
    private String supplierSku;

    @Excel(name = "commodity_name",width = 20.22D)
    private String commodityName;

    @Excel(name = "warehouse_name/code",width =20.22D)
    private String warehouseName;

    @Excel(name  = "localWaitingShippingQty",width = 20.22D)
    private Integer localWaitingShippingQty;

    @Excel(name  = "localAvailableQty",width = 20.22D)
    private Integer localAvailableQty;

    @Excel(name = "instransit/pending",width = 10.22D)
    private String instransitQty;

    @Excel(name  = "available/defects",width = 10.22D)
    private String availableQty;

    @Excel(name = "waiting_shipping",width = 10.22D)
    private Integer waitingShippingQty;

    @Excel(name = "tune_in/out",width = 10.22D)
    private String tune;

    @Excel(name = "stocking/no_stock",width = 10.22D)
    private String stockingQty;

    @Excel(name = "warn_val",width = 9D,replace = {"--_-1"})
    private Integer warnVal;

    @Excel(name  = "status",width = 7D)
    private String status;

    @Excel(name  = "update_time", format = "yyyy-MM-dd HH:mm:ss",width = 20.22D)
    private Date syncTime;

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

    public String getInstransitQty() {
        return instransitQty;
    }

    public void setInstransitQty(String instransitQty) {
        this.instransitQty = instransitQty;
    }

    public String getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(String availableQty) {
        this.availableQty = availableQty;
    }

    public Integer getWaitingShippingQty() {
        return waitingShippingQty;
    }

    public void setWaitingShippingQty(Integer waitingShippingQty) {
        this.waitingShippingQty = waitingShippingQty;
    }

    public String getTune() {
        return tune;
    }

    public void setTune(String tune) {
        this.tune = tune;
    }

    public String getStockingQty() {
        return stockingQty;
    }

    public void setStockingQty(String stockingQty) {
        this.stockingQty = stockingQty;
    }

    public Integer getWarnVal() {
        return warnVal;
    }

    public void setWarnVal(Integer warnVal) {
        this.warnVal = warnVal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public Integer getLocalWaitingShippingQty() {
        return localWaitingShippingQty;
    }

    public void setLocalWaitingShippingQty(Integer localWaitingShippingQty) {
        this.localWaitingShippingQty = localWaitingShippingQty;
    }

    public Integer getLocalAvailableQty() {
        return localAvailableQty;
    }

    public void setLocalAvailableQty(Integer localAvailableQty) {
        this.localAvailableQty = localAvailableQty;
    }
}
