package com.rondaful.cloud.supplier.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 * 	运费试算返回实体
 * @author xieyanbin
 *
 * @2019年4月28日 
 * @version v2.2
 */
public class FreightTrialDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "总费用")
	private BigDecimal totalCost;
	
	@ApiModelProperty(value = "费用明细")
	private List costDetail;
	
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

	@ApiModelProperty(value = "sku的具体费用")
	private Map<String,Object> skuCost;

	private String  searchId;

	@ApiModelProperty(value = "物流方式英文名称")
	private String logisticsNameEn;
	
	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public List getCostDetail() {
		return costDetail;
	}

	public void setCostDetail(List income) {
		this.costDetail = income;
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

	public Map<String, Object> getSkuCost() {
		return skuCost;
	}

	public void setSkuCost(Map<String, Object> skuCost) {
		this.skuCost = skuCost;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public String getLogisticsNameEn() {
		return logisticsNameEn;
	}

	public void setLogisticsNameEn(String logisticsNameEn) {
		this.logisticsNameEn = logisticsNameEn;
	}

	@Override
	public String toString() {
		return "FreightTrialDTO{" +
				"totalCost=" + totalCost +
				", costDetail=" + costDetail +
				", logisticsName='" + logisticsName + '\'' +
				", logisticsCode='" + logisticsCode + '\'' +
				", minDeliveryTime=" + minDeliveryTime +
				", maxDeliveryTime=" + maxDeliveryTime +
				", currency='" + currency + '\'' +
				", discount=" + discount +
				", maxWeight=" + maxWeight +
				", afterDiscountAmount=" + afterDiscountAmount +
				", skuCost=" + skuCost +
				", searchId='" + searchId + '\'' +
				", logisticsNameEn='" + logisticsNameEn + '\'' +
				'}';
	}
}
