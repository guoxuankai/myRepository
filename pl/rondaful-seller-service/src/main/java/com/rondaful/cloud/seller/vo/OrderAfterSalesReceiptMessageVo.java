package com.rondaful.cloud.seller.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后确认信息")
public class OrderAfterSalesReceiptMessageVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "品连SKU")
	private String sku;

	@ApiModelProperty(value = "退款数量")
	private Long returnNumber;

	@ApiModelProperty(value = "实收数量")
	private Long paidInNumber;

	@ApiModelProperty(value = "良品数量")
	private Long goodNumber;

	@ApiModelProperty(value = "退款金额")
	private String refundMoney;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "售后ID")
	private String orderAfterSalesId;

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

	public Long getReturnNumber() {
		return returnNumber;
	}

	public void setReturnNumber(Long returnNumber) {
		this.returnNumber = returnNumber;
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

}
