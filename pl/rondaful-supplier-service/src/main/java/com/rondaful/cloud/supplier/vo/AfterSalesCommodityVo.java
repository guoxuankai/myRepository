package com.rondaful.cloud.supplier.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后商品VO")
public class AfterSalesCommodityVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "图片[xxx,xxx]多个用逗号隔开")
	private String commodityImage;

	@ApiModelProperty(value = "名称")
	private String commodityName;

	@ApiModelProperty(value = "商品属性")
	private String commodityAttribute;

	@ApiModelProperty(value = "商品退款金额")
	private String commodityRefundMoney;

	@ApiModelProperty(value = "金额")
	private String commodityMoney;

	@ApiModelProperty(value = "商品SKU")
	private String commoditySku;

	@ApiModelProperty(value = "供应商SKU")
	private String supplierSku;

	@ApiModelProperty(value = "商品供应商")
	private String commoditySupplier;

	@ApiModelProperty(value = "退货数量")
	private Long commodityNumber;

	@ApiModelProperty(value = "订单商品数量")
	private Long orderCommodityNumber;

	@ApiModelProperty(value = "商品信息存放")
	private String commodityDetails;

	public String getCommoditySku() {
		return commoditySku;
	}

	public void setCommoditySku(String commoditySku) {
		this.commoditySku = commoditySku;
	}

	public String getCommodityImage() {
		return commodityImage;
	}

	public void setCommodityImage(String commodityImage) {
		this.commodityImage = commodityImage;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCommodityAttribute() {
		return commodityAttribute;
	}

	public void setCommodityAttribute(String commodityAttribute) {
		this.commodityAttribute = commodityAttribute;
	}

	public String getCommodityRefundMoney() {
		return commodityRefundMoney;
	}

	public void setCommodityRefundMoney(String commodityRefundMoney) {
		this.commodityRefundMoney = commodityRefundMoney;
	}

	public String getCommodityMoney() {
		return commodityMoney;
	}

	public void setCommodityMoney(String commodityMoney) {
		this.commodityMoney = commodityMoney;
	}

	public Long getCommodityNumber() {
		return commodityNumber;
	}

	public void setCommodityNumber(Long commodityNumber) {
		this.commodityNumber = commodityNumber;
	}

	public String getCommoditySupplier() {
		return commoditySupplier;
	}

	public void setCommoditySupplier(String commoditySupplier) {
		this.commoditySupplier = commoditySupplier;
	}

	public String getSupplierSku() {
		return supplierSku;
	}

	public void setSupplierSku(String supplierSku) {
		this.supplierSku = supplierSku;
	}

	public Long getOrderCommodityNumber() {
		return orderCommodityNumber;
	}

	public void setOrderCommodityNumber(Long orderCommodityNumber) {
		this.orderCommodityNumber = orderCommodityNumber;
	}

	public String getCommodityDetails() {
		return commodityDetails;
	}

	public void setCommodityDetails(String commodityDetails) {
		this.commodityDetails = commodityDetails;
	}

}
