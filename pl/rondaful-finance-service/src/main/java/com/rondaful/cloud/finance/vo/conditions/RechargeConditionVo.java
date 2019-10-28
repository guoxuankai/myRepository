package com.rondaful.cloud.finance.vo.conditions;

import io.swagger.annotations.ApiModelProperty;

public class RechargeConditionVo extends BaseConditionVo {

	@ApiModelProperty(name = "转账流水号")
	private String transSerialNo;
	@ApiModelProperty(name = "充值账号")
	private String rechargeAccount;
	@ApiModelProperty(name = "充值方式")
	private String rechargeType;

	public String getTransSerialNo() {
		return transSerialNo;
	}

	public void setTransSerialNo(String transSerialNo) {
		this.transSerialNo = transSerialNo;
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

}
