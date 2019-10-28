package com.rondaful.cloud.seller.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后退货信息")
public class OrderAfterSalesReceiptMessageModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id[后台退款需要带id]")
	private Long id;

	@ApiModelProperty(value = "品连SKU[后台退款需要]")
	private String sku;

	@ApiModelProperty(value = "商品价")
	private String commodityMoney;

	@ApiModelProperty(value = "订购数量")
	private Long quantityOrdered;

	@ApiModelProperty(value = "退货数量")
	private Long returnGoodsNumber;

	@ApiModelProperty(value = "退款数量")
	private Long returnNumber;

	@ApiModelProperty(value = "实收数量")
	private Long paidInNumber;

	@ApiModelProperty(value = "良品数量")
	private Long goodNumber;

	@ApiModelProperty(value = "退款金额[后台退款需要带退款金额]")
	private String refundMoney;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "售后ID")
	private String orderAfterSalesId;

	@ApiModelProperty(value = "商品ID")
	private String commodityId;

	/**
	 * 商品新增
	 * 
	 * @param sku
	 * @param commodityMoney
	 * @param quantityOrdered
	 * @param returnGoodsNumber
	 * @param orderAfterSalesId
	 */
	public OrderAfterSalesReceiptMessageModel(String sku, String commodityMoney, Long quantityOrdered, Long returnGoodsNumber, String orderAfterSalesId, String commodityId) {
		this.sku = sku;
		this.commodityMoney = commodityMoney;
		this.quantityOrdered = quantityOrdered;
		this.returnGoodsNumber = returnGoodsNumber;
		this.orderAfterSalesId = orderAfterSalesId;
		this.commodityId = commodityId;
	}

	/**
	 * 后台修改金额
	 * 
	 * @param id
	 * @param refundMoney
	 * @param orderAfterSalesId
	 */
	public OrderAfterSalesReceiptMessageModel(Long id, String refundMoney, String orderAfterSalesId) {
		this.id = id;
		this.refundMoney = refundMoney;
		this.orderAfterSalesId = orderAfterSalesId;
	}

	public OrderAfterSalesReceiptMessageModel() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getCommodityMoney() {
		return commodityMoney;
	}

	public void setCommodityMoney(String commodityMoney) {
		this.commodityMoney = commodityMoney;
	}

	public Long getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(Long quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public Long getPaidInNumber() {
		return paidInNumber;
	}

	public void setPaidInNumber(Long paidInNumber) {
		this.paidInNumber = paidInNumber;
	}

	public Long getGoodNumber() {
		return goodNumber;
	}

	public void setGoodNumber(Long goodNumber) {
		this.goodNumber = goodNumber;
	}

	public Long getReturnNumber() {
		return returnNumber;
	}

	public void setReturnNumber(Long returnNumber) {
		this.returnNumber = returnNumber;
	}

	public String getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(String refundMoney) {
		this.refundMoney = refundMoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOrderAfterSalesId() {
		return orderAfterSalesId;
	}

	public void setOrderAfterSalesId(String orderAfterSalesId) {
		this.orderAfterSalesId = orderAfterSalesId;
	}

	public Long getReturnGoodsNumber() {
		return returnGoodsNumber;
	}

	public void setReturnGoodsNumber(Long returnGoodsNumber) {
		this.returnGoodsNumber = returnGoodsNumber;
	}

	public String getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(String commodityId) {
		this.commodityId = commodityId;
	}

}
