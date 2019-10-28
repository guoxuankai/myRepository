package com.rondaful.cloud.finance.entity;

import java.io.Serializable;
import java.util.Date;

public class SettlementRegistInfo implements Serializable {
    private Integer registerId;

    private Integer supplierId;

    private String supplierName;

    private Date createTime;

    private String settlementCycle;

    private Date lastSettlementTime;

    private Integer version;

    private String tbStatus;
    
    private Date nextSettlementTime;
    
    

    public SettlementRegistInfo() {
	}

	public SettlementRegistInfo(Integer supplierId, String supplierName, String settlementCycle) {
		super();
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.settlementCycle = settlementCycle;
		
	}

	public Date getNextSettlementTime() {
		return nextSettlementTime;
	}

	public void setNextSettlementTime(Date nextSettlementTime) {
		this.nextSettlementTime = nextSettlementTime;
	}

	private static final long serialVersionUID = 1L;

    public Integer getRegisterId() {
        return registerId;
    }

    public void setRegisterId(Integer registerId) {
        this.registerId = registerId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSettlementCycle() {
        return settlementCycle;
    }

    public void setSettlementCycle(String settlementCycle) {
        this.settlementCycle = settlementCycle == null ? null : settlementCycle.trim();
    }

    public Date getLastSettlementTime() {
        return lastSettlementTime;
    }

    public void setLastSettlementTime(Date lastSettlementTime) {
        this.lastSettlementTime = lastSettlementTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTbStatus() {
        return tbStatus;
    }

    public void setTbStatus(String tbStatus) {
        this.tbStatus = tbStatus == null ? null : tbStatus.trim();
    }
}