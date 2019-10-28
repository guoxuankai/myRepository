package com.rondaful.cloud.finance.vo.conditions;

public class WithdrawConditionVo extends BaseConditionVo{
	
	private String withdrawalType;
	
	private String withdrawalAccount;

	public String getWithdrawalType() {
		return withdrawalType;
	}

	public void setWithdrawalType(String withdrawalType) {
		this.withdrawalType = withdrawalType;
	}

	public String getWithdrawalAccount() {
		return withdrawalAccount;
	}

	public void setWithdrawalAccount(String withdrawalAccount) {
		this.withdrawalAccount = withdrawalAccount;
	}

	
	
	
}
