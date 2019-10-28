package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;

import java.util.Date;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import io.swagger.annotations.ApiModelProperty;
/**
 * 
 *  数据库返回对像
 * @author lxx
 * @date 2018-12-04 15:53:16
 */
@ExcelTarget("WarehouseInventoryExportDTO")
public class WarehouseInventoryExportDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Excel(name  = "图片")
	private String pictureUrl;
	
	@ApiModelProperty(value = "供应商")
    private String supplier;
	
	@Excel(name  = "品连sku")
    private String pinlianSku;
    
    @Excel(name = "供应商sku")
    private String supplierSku;
    
    @Excel(name = "商品名称")
    private String commodityName;
    
    @Excel(name = "仓库名称/代码")
    private String warehouseName;
    
    private String warehouseCode;
    
    @Excel(name = "在途/待上架")
    private String instransitQty;
    
	private Integer pendingQty;

	@Excel(name  = "可售/不可售")
    private String availableQty;
	
	private Integer defectsQty;
    
	@Excel(name = "待出库")
    private Integer waitingShippingQty;
    
	@Excel(name = "待调入/待调出")
    private String tuneOutQty;
	
	@Excel(name = "备货数量/缺货数量")
    private String stockingQty;
	
	private Integer tuneInQty;
    
    @Excel(name = "预警值")
    private Integer warnVal;

    @Excel(name  = "库存状态",replace = {"低于预警_0","正常_1"})
    private String status;

    
    @Excel(name  = "数据更新时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;
    
    public String getPictureUrl() {
		return pictureUrl;
	}


	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getSupplier() {
		return supplier;
	}


	public void setSupplier(String supplier) {
		this.supplier = supplier;
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


	public String getInstransitQty() {
		return instransitQty;
	}


	public void setInstransitQty(String instransitQty) {
		this.instransitQty = instransitQty;
	}


	public Integer getPendingQty() {
		return pendingQty;
	}


	public void setPendingQty(Integer pendingQty) {
		this.pendingQty = pendingQty;
	}


	public String getAvailableQty() {
		return availableQty;
	}


	public void setAvailableQty(String availableQty) {
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


	public String getTuneOutQty() {
		return tuneOutQty;
	}


	public void setTuneOutQty(String tuneOutQty) {
		this.tuneOutQty = tuneOutQty;
	}


	public Integer getTuneInQty() {
		return tuneInQty;
	}

	public String getStockingQty() {
		return stockingQty;
	}


	public void setStockingQty(String stockingQty) {
		this.stockingQty = stockingQty;
	}

	public void setTuneInQty(Integer tuneInQty) {
		this.tuneInQty = tuneInQty;
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
   
    
}