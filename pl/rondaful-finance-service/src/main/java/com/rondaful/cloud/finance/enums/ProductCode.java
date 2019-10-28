package com.rondaful.cloud.finance.enums;

public enum ProductCode {
	LOGISTICS_PAGE_PAY("物流补扣费用支付"), // 物流费用页面支付
	ORDER_PAGE_PAY("订单支付"), // 订单支付页面支付
	RECHARGE_PAGE_PAY("充值支付")// 充值页面支付
	;

	private String subject;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	private ProductCode(String subject) {
		this.subject = subject;
	}

}
