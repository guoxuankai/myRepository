package com.rondaful.cloud.commodity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

/**
* @Description:供应商服务物流运费DTO
* @author:范津 
* @date:2019年8月6日 下午3:49:44
 */
public class LogisticFreight implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "总费用")
	private BigDecimal totalCost;
	
	@ApiModelProperty(value = "物流方式名称")
	private String logisticsName;
	
	@ApiModelProperty(value = "物流方式代码")
	private String logisticsCode;
	
	@ApiModelProperty(value = "最快时效")
	private Integer minDeliveryTime;
	
	@ApiModelProperty(value = "最慢时效")
	private Integer maxDeliveryTime;
	
	@ApiModelProperty(value = "费用币种")
	private String currency;
	
	@ApiModelProperty(value = "折扣")
	private Double discount;
	
	@ApiModelProperty(value = "最大限重")
	private Double maxWeight;

	@ApiModelProperty(value = "折后运费")
	private Double afterDiscountAmount;

	
	
	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public String getLogisticsName() {
		return logisticsName;
	}

	public void setLogisticsName(String logisticsName) {
		this.logisticsName = logisticsName;
	}

	public String getLogisticsCode() {
		return logisticsCode;
	}

	public void setLogisticsCode(String logisticsCode) {
		this.logisticsCode = logisticsCode;
	}

	public Integer getMinDeliveryTime() {
		return minDeliveryTime;
	}

	public void setMinDeliveryTime(Integer minDeliveryTime) {
		this.minDeliveryTime = minDeliveryTime;
	}

	public Integer getMaxDeliveryTime() {
		return maxDeliveryTime;
	}

	public void setMaxDeliveryTime(Integer maxDeliveryTime) {
		this.maxDeliveryTime = maxDeliveryTime;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(Double maxWeight) {
		this.maxWeight = maxWeight;
	}

	public Double getAfterDiscountAmount() {
		return afterDiscountAmount;
	}

	public void setAfterDiscountAmount(Double afterDiscountAmount) {
		this.afterDiscountAmount = afterDiscountAmount;
	}
}
