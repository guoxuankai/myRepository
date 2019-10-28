package com.rondaful.cloud.order.entity.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItemVo implements Serializable {
	@ApiModelProperty(value = "订单号 [唯一]", required = true)
	private String orderNo;
	@ApiModelProperty(value = "订单项号  [唯一]", required = true)
	private String itemNo;
	@ApiModelProperty(value = "卖家ID", required = true)
	private Integer sellerId;
	@ApiModelProperty(value = "供应商ID", required = true)
	private Integer supplierId;
	@ApiModelProperty(value = "供应商名称", required = true)
	private String supplierName;
	@ApiModelProperty(value = "订单项总价", required = true)
	private BigDecimal productAmount;
	@ApiModelProperty(value = "供应链公司ID V2.0新增", required = true)
	private Integer supplyCompanyId;
	@ApiModelProperty(value = "供应链公司名称  V2.0新增", required = true)
	private String supplyCompanyName;
	@ApiModelProperty(value = "商品名称 V2.0新增", required = true)
	private String productName;
	@ApiModelProperty(value = "服务费", required = true)
	private BigDecimal fare;
	@ApiModelProperty(value = "服务费扣取类型", required = true)
	private String fareType;
	@ApiModelProperty(value = "购买此SKU总数量")
	private Integer skuQuantity;
	@ApiModelProperty(value = "商品系统单价")
	private BigDecimal itemPrice;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public BigDecimal getProductAmount() {
		return productAmount;
	}

	public void setProductAmount(BigDecimal productAmount) {
		this.productAmount = productAmount;
	}

	public Integer getSupplyCompanyId() {
		return supplyCompanyId;
	}

	public void setSupplyCompanyId(Integer supplyCompanyId) {
		this.supplyCompanyId = supplyCompanyId;
	}

	public String getSupplyCompanyName() {
		return supplyCompanyName;
	}

	public void setSupplyCompanyName(String supplyCompanyName) {
		this.supplyCompanyName = supplyCompanyName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getFare() {
		return fare;
	}

	public void setFare(BigDecimal fare) {
		this.fare = fare;
	}

	public String getFareType() {
		return fareType;
	}

	public void setFareType(String fareType) {
		this.fareType = fareType;
	}

	public Integer getSkuQuantity() {
		return skuQuantity;
	}

	public void setSkuQuantity(Integer skuQuantity) {
		this.skuQuantity = skuQuantity;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}
}