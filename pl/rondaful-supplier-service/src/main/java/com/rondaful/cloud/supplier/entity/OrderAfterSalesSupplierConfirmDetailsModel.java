package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后供应商确认详情结果展示")
public class OrderAfterSalesSupplierConfirmDetailsModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "图片[xxx,xxx]多个用逗号隔开")
	private String image;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "退款原因")
	private String refundReason;

	private List<AfterSalesCommodityModel> orderCommodity;

	private List<AfterSalesApprovalModel> afterSalesApproval;

	private List<OrderAfterSalesReceiptMessageModel> afterSalesReceiptMessage;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public List<AfterSalesCommodityModel> getOrderCommodity() {
		return orderCommodity;
	}

	public void setOrderCommodity(List<AfterSalesCommodityModel> orderCommodity) {
		this.orderCommodity = orderCommodity;
	}

	public List<AfterSalesApprovalModel> getAfterSalesApproval() {
		return afterSalesApproval;
	}

	public void setAfterSalesApproval(List<AfterSalesApprovalModel> afterSalesApproval) {
		this.afterSalesApproval = afterSalesApproval;
	}

	public List<OrderAfterSalesReceiptMessageModel> getAfterSalesReceiptMessage() {
		return afterSalesReceiptMessage;
	}

	public void setAfterSalesReceiptMessage(List<OrderAfterSalesReceiptMessageModel> afterSalesReceiptMessage) {
		this.afterSalesReceiptMessage = afterSalesReceiptMessage;
	}

}
