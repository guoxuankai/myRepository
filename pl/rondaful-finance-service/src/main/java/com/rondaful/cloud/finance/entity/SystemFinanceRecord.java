package com.rondaful.cloud.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SystemFinanceRecord implements Serializable {
    private Integer recordId;

    private Date createTime;

    private String tradeType;

    private String serialNo;

    private BigDecimal actualAmount;

    private String type;

    private BigDecimal restBalance;

    private String tbStatus;

    private static final long serialVersionUID = 1L;
    
    

    public SystemFinanceRecord() {
	}

	public SystemFinanceRecord(String tradeType, String serialNo, BigDecimal actualAmount, String type,
			BigDecimal restBalance) {
		this.tradeType = tradeType;
		this.serialNo = serialNo;
		this.actualAmount = actualAmount;
		this.type = type;
		this.restBalance = restBalance;
	}

	public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType == null ? null : tradeType.trim();
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo == null ? null : serialNo.trim();
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
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
}