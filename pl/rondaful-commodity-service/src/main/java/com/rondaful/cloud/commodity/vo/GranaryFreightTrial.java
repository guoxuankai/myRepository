package com.rondaful.cloud.commodity.vo;

import java.util.List;

/**
 * 谷仓物流运费试算接收实体
 * 
 * @author xieyanbin
 *
 * @2019年4月27日 
 * @version v1.0
 */
public class GranaryFreightTrial {

	//配送方式代码
	private String sm_code;
	
	//配送方式的英文名称
	private String sm_name;
	
	//配送方式的中文名称
	private String sm_name_cn;
	
	//最快时效
	private Integer sm_delivery_time_min;
	
	//最慢时效
	private Integer sm_delivery_time_max;
	
	//总费用
	private Double total;
	
	//明细
	private List<CostDetail> income;
	
	//币种
	private String currency;

	public String getSm_code() {
		return sm_code;
	}

	public void setSm_code(String sm_code) {
		this.sm_code = sm_code;
	}

	public String getSm_name() {
		return sm_name;
	}

	public void setSm_name(String sm_name) {
		this.sm_name = sm_name;
	}

	public String getSm_name_cn() {
		return sm_name_cn;
	}

	public void setSm_name_cn(String sm_name_cn) {
		this.sm_name_cn = sm_name_cn;
	}

	public Integer getSm_delivery_time_min() {
		return sm_delivery_time_min;
	}

	public void setSm_delivery_time_min(Integer sm_delivery_time_min) {
		this.sm_delivery_time_min = sm_delivery_time_min;
	}

	public Integer getSm_delivery_time_max() {
		return sm_delivery_time_max;
	}

	public void setSm_delivery_time_max(Integer sm_delivery_time_max) {
		this.sm_delivery_time_max = sm_delivery_time_max;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public List<CostDetail> getIncome() {
		return income;
	}

	public void setIncome(List<CostDetail> income) {
		this.income = income;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "GranaryFreightTrial [sm_code=" + sm_code + ", sm_name=" + sm_name + ", sm_name_cn=" + sm_name_cn
				+ ", sm_delivery_time_min=" + sm_delivery_time_min + ", sm_delivery_time_max=" + sm_delivery_time_max
				+ ", total=" + total + ", income=" + income + ", currency=" + currency + "]";
	}

}
