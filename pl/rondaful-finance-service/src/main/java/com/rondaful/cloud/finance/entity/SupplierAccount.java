package com.rondaful.cloud.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SupplierAccount implements Serializable {
    private Integer supplierAccountId;

    private BigDecimal unsettledAmount;

    private BigDecimal settledAmount;

    private BigDecimal withdrawalsAmount;

    private BigDecimal withdrawingAmount;

    private BigDecimal frozenAmount;

    private BigDecimal freeAmount;

    private BigDecimal totalAmount;

    private Integer supplierId;

    private String supplierName;

    private Integer version;

    private Date modifyTime;

    private Date createTime;

    private String tbStatus;

    private static final long serialVersionUID = 1L;
    
    
    
    public SupplierAccount() {
	}

	public SupplierAccount(Integer supplierId, String supplierName) {
		this.supplierId = supplierId;
		this.supplierName = supplierName;
	}

	public Integer getSupplierAccountId() {
        return supplierAccountId;
    }

    public void setSupplierAccountId(Integer supplierAccountId) {
        this.supplierAccountId = supplierAccountId;
    }

    public BigDecimal getUnsettledAmount() {
        return unsettledAmount;
    }

    public void setUnsettledAmount(BigDecimal unsettledAmount) {
        this.unsettledAmount = unsettledAmount;
    }

    public BigDecimal getSettledAmount() {
        return settledAmount;
    }

    public void setSettledAmount(BigDecimal settledAmount) {
        this.settledAmount = settledAmount;
    }

    public BigDecimal getWithdrawalsAmount() {
        return withdrawalsAmount;
    }

    public void setWithdrawalsAmount(BigDecimal withdrawalsAmount) {
        this.withdrawalsAmount = withdrawalsAmount;
    }

    public BigDecimal getWithdrawingAmount() {
        return withdrawingAmount;
    }

    public void setWithdrawingAmount(BigDecimal withdrawingAmount) {
        this.withdrawingAmount = withdrawingAmount;
    }

    public BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public BigDecimal getFreeAmount() {
        return freeAmount;
    }

    public void setFreeAmount(BigDecimal freeAmount) {
        this.freeAmount = freeAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTbStatus() {
        return tbStatus;
    }

    public void setTbStatus(String tbStatus) {
        this.tbStatus = tbStatus == null ? null : tbStatus.trim();
    }
}