package com.rondaful.cloud.finance.vo.conditions;

import io.swagger.annotations.ApiModelProperty;

public class OrderConditionVo extends BaseConditionVo {
	@ApiModelProperty(name = "卖家账户")
	private String sellerAccount;

	@ApiModelProperty(name = "订单号")
	private String orderNo;

	@ApiModelProperty(name = "结算单号")
	private Integer settlementId;

	public String getSellerAccount() {
		return sellerAccount;
	}

	public void setSellerAccount(String sellerAccount) {
		this.sellerAccount = sellerAccount;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getSettlementId() {
		return settlementId;
	}

	public void setSettlementId(Integer settlementId) {
		this.settlementId = settlementId;
	}

}
