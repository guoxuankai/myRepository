package com.rondaful.cloud.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.rondaful.cloud.finance.utils.OrderUtils;

public class SettlementRecord implements Serializable {
	private Integer settlementId;

	private String settlementNo;

	private Date createTime;

	private String settlementCycle;

	private BigDecimal settlementAmount;

	private BigDecimal restBalance;

	private String tbStatus;

	private String supplierName;

	private Integer supplierId;

	
	
	public SettlementRecord() {
	}

	public SettlementRecord(String settlementCycle, BigDecimal settlementAmount,
			BigDecimal restBalance, String supplierName, Integer supplierId) {
		this.settlementNo = "JS" + OrderUtils.getOrderSn();
		this.settlementCycle = settlementCycle;
		this.settlementAmount = settlementAmount;
		this.restBalance = restBalance;
		this.supplierName = supplierName;
		this.supplierId = supplierId;
	}

	private static final long serialVersionUID = 1L;

	public Integer getSettlementId() {
		return settlementId;
	}

	public void setSettlementId(Integer settlementId) {
		this.settlementId = settlementId;
	}

	public String getSettlementNo() {
		return settlementNo;
	}

	public void setSettlementNo(String settlementNo) {
		this.settlementNo = settlementNo == null ? null : settlementNo.trim();
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

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public BigDecimal getRestBalance() {
		return restBalance;
	}

	public void setRestBalance(BigDecimal restBalance) {
		this.restBalance = restBalance;
	}

	public String getTbStatus() {
		return tbStatus;
	}

	public void setTbStatus(String tbStatus) {
		this.tbStatus = tbStatus == null ? null : tbStatus.trim();
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName == null ? null : supplierName.trim();
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
}