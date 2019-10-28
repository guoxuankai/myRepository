package com.rondaful.cloud.supplier.entity;

import java.math.BigDecimal;

/**
 * 费用明细
 * 
 * @author xieyanbin
 *
 * @2019年4月27日 
 * @version v2.2
 */
public class CostDetail {
	
	//名称
	private String name;
	
	//费用
	private BigDecimal amount;
	
	public CostDetail() {}

	public CostDetail(String name, BigDecimal amount) {
		super();
		this.name = name;
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "CostDetail [name=" + name + ", amount=" + amount + "]";
	}
	
	
	
	
}
