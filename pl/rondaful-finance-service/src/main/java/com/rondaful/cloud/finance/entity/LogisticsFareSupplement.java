package com.rondaful.cloud.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class LogisticsFareSupplement implements Serializable {
    private Integer supplementId;

    private String serialNo;

    private String orderNo;

    private Date createTime;

    private BigDecimal supplementAmount;

    private Date executeTime;

    private Integer sellerId;

    private String tbStatus;

    private static final long serialVersionUID = 1L;
    
    

    public LogisticsFareSupplement() {
	}

	public LogisticsFareSupplement(String serialNo, String orderNo, BigDecimal supplementAmount, Integer sellerId) {
		this.serialNo = serialNo;
		this.orderNo = orderNo;
		this.supplementAmount = supplementAmount;
		this.sellerId = sellerId;
	}

	public Integer getSupplementId() {
        return supplementId;
    }

    public void setSupplementId(Integer supplementId) {
        this.supplementId = supplementId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo == null ? null : serialNo.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getSupplementAmount() {
        return supplementAmount;
    }

    public void setSupplementAmount(BigDecimal supplementAmount) {
        this.supplementAmount = supplementAmount;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getTbStatus() {
        return tbStatus;
    }

    public void setTbStatus(String tbStatus) {
        this.tbStatus = tbStatus == null ? null : tbStatus.trim();
    }
}