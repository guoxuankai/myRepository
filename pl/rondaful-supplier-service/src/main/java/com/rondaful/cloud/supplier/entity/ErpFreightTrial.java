package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * erp运费试算返回实体
 * @author xieyanbin
 *
 * @2019年1月26日 
 * @version v1.0
 */
public class ErpFreightTrial implements Serializable {

	private static final long serialVersionUID = -66228904799077470L;

	//渠道简称
	private String shipping_name;
	
	//渠道code
	private String shipping_code;
	
	//折扣
	private Double shipping_fee_discount;
	
	//国家简码
	private String currency_code;
	
	//打折前费用（未加燃油费和附加费）  运输服务费
	private Double before_amount;
	
	//挂号费金额
	private Double registered_fee;
	
	//燃油附加费
	private Double oli_additional_fee;
	
	//最快天数（不设置0） 预计运输时效
	private Integer earliest_days;
	
	//最忙天数（不设置0）
	private Integer latest_days;
	
	//最大限重
	private Double max_weight;
	
	//物流费用  总运费
	private BigDecimal amount;
	
	//打折后物流费用折后运费
	private Double after_discount_amount;
	
	//打折后物流费用(人民币)   折后运费
	private BigDecimal cny_amount;
	
	//处理费   操作费
	private Double Handle_fee;

	public ErpFreightTrial(){}

	public ErpFreightTrial(Integer earliest_days, BigDecimal amount) {
		this.earliest_days = earliest_days;
		this.amount = amount;
	}

	public String getShipping_name() {
		return shipping_name;
	}

	public void setShipping_name(String shipping_name) {
		this.shipping_name = shipping_name;
	}

	public String getShipping_code() {
		return shipping_code;
	}

	public void setShipping_code(String shipping_code) {
		this.shipping_code = shipping_code;
	}

	public Double getShipping_fee_discount() {
		return shipping_fee_discount;
	}

	public void setShipping_fee_discount(Double shipping_fee_discount) {
		this.shipping_fee_discount = shipping_fee_discount;
	}

	public String getCurrency_code() {
		return currency_code;
	}

	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}

	public Double getBefore_amount() {
		return before_amount;
	}

	public void setBefore_amount(Double before_amount) {
		this.before_amount = before_amount;
	}

	public Double getRegistered_fee() {
		return registered_fee;
	}

	public void setRegistered_fee(Double registered_fee) {
		this.registered_fee = registered_fee;
	}

	public Double getOli_additional_fee() {
		return oli_additional_fee;
	}

	public void setOli_additional_fee(Double oli_additional_fee) {
		this.oli_additional_fee = oli_additional_fee;
	}

	public Integer getEarliest_days() {
		return earliest_days;
	}

	public void setEarliest_days(Integer earliest_days) {
		this.earliest_days = earliest_days;
	}

	public Integer getLatest_days() {
		return latest_days;
	}

	public void setLatest_days(Integer latest_days) {
		this.latest_days = latest_days;
	}

	public Double getMax_weight() {
		return max_weight;
	}

	public void setMax_weight(Double max_weight) {
		this.max_weight = max_weight;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Double getAfter_discount_amount() {
		return after_discount_amount;
	}

	public void setAfter_discount_amount(Double after_discount_amount) {
		this.after_discount_amount = after_discount_amount;
	}

	public BigDecimal getCny_amount() {
		return cny_amount;
	}

	public void setCny_amount(BigDecimal cny_amount) {
		this.cny_amount = cny_amount;
	}

	public Double getHandle_fee() {
		return Handle_fee;
	}

	public void setHandle_fee(Double handle_fee) {
		Handle_fee = handle_fee;
	}

	@Override
	public String toString() {
		return "ErpFreightTrial{" +
				"shipping_name='" + shipping_name + '\'' +
				", shipping_code='" + shipping_code + '\'' +
				", shipping_fee_discount=" + shipping_fee_discount +
				", currency_code='" + currency_code + '\'' +
				", before_amount=" + before_amount +
				", registered_fee=" + registered_fee +
				", oli_additional_fee=" + oli_additional_fee +
				", earliest_days=" + earliest_days +
				", latest_days=" + latest_days +
				", max_weight=" + max_weight +
				", amount=" + amount +
				", after_discount_amount=" + after_discount_amount +
				", cny_amount=" + cny_amount +
				", Handle_fee=" + Handle_fee +
				'}';
	}
}
