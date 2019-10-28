package com.rondaful.cloud.commodity.vo;

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
	private Double amount;
	
	public CostDetail() {}

	public CostDetail(String name, Double amount) {
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "CostDetail [name=" + name + ", amount=" + amount + "]";
	}
	
	
	
	
}
