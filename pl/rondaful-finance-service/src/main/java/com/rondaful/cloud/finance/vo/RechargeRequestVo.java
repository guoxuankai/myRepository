package com.rondaful.cloud.finance.vo;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class RechargeRequestVo {
	@ApiModelProperty(name = "充值账号", required = true)
	private String rechargeAccount;
	@ApiModelProperty(name = "充值方式", required = true)
	private String rechargeType;
	@ApiModelProperty(name = "卖家名称", required = true)
	private String sellerName;
	@ApiModelProperty(name = "卖家ID", required = true)
	private Integer sellerId;
	@ApiModelProperty(name = "转账回执", required = true)
	private String transferReceiptUrl;
	@ApiModelProperty(name = "充值金额", required = true)
	private BigDecimal rechargeAmount;
	@ApiModelProperty(name = "转账流水号", required = true)
	private String transSerialNo;
	@ApiModelProperty(name = "备注", required = false)
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRechargeAccount() {
		return rechargeAccount;
	}

	public void setRechargeAccount(String rechargeAccount) {
		this.rechargeAccount = rechargeAccount;
	}

	public String getRechargeType() {
		return rechargeType;
	}

	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public String getTransferReceiptUrl() {
		return transferReceiptUrl;
	}

	public void setTransferReceiptUrl(String transferReceiptUrl) {
		this.transferReceiptUrl = transferReceiptUrl;
	}

	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getTransSerialNo() {
		return transSerialNo;
	}

	public void setTransSerialNo(String transSerialNo) {
		this.transSerialNo = transSerialNo;
	}

}
