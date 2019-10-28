package com.rondaful.cloud.finance.vo;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class OrderRequestVo {
	@ApiModelProperty(name = "卖家名称", required = true)
	private String sellerName;
	@ApiModelProperty(name = "商品金额", required = true)
	private BigDecimal productAmount;
	@ApiModelProperty(name = "物流费用", required = true)
	private BigDecimal logisticsFare;
	@ApiModelProperty(name = "应付金额", required = true)
	private BigDecimal payableAmount;
	@ApiModelProperty(name = "实付金额", required = true)
	private BigDecimal actualAmount;
	@ApiModelProperty(name = "卖家ID", required = true)
	private Integer sellerId;
	@ApiModelProperty(name = "订单号", required = true)
	private String orderNo;
	@ApiModelProperty(name = "卖家账户", required = true)
	private String sellerAccount;
	@ApiModelProperty(name = "供应商ID", required = true)
	private String supplierId;
	@ApiModelProperty(name = "供应商名称", required = true)
	private String supplierName;

	public String getSellerAccount() {
		return sellerAccount;
	}

	public void setSellerAccount(String sellerAccount) {
		this.sellerAccount = sellerAccount;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public BigDecimal getProductAmount() {
		return productAmount;
	}

	public void setProductAmount(BigDecimal productAmount) {
		this.productAmount = productAmount;
	}

	public BigDecimal getLogisticsFare() {
		return logisticsFare;
	}

	public void setLogisticsFare(BigDecimal logisticsFare) {
		this.logisticsFare = logisticsFare;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
