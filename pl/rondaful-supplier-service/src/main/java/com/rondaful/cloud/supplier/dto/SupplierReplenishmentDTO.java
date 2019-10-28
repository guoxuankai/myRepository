package com.rondaful.cloud.supplier.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.rondaful.cloud.supplier.entity.AfterSalesCommodityModel;

import io.swagger.annotations.ApiModelProperty;

public class SupplierReplenishmentDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "售后编号")
	private String numberingId;

	@ApiModelProperty(value = "订单号")
	private String orderId;

	@ApiModelProperty(value = "备注")
	private String remark;
	
	@ApiModelProperty(value = "图片")
	private String image;

	@ApiModelProperty(value = "退款原因来源于公共参数接口")
	private String refundReason;
	
	@ApiModelProperty(value = "支付状态")
	private Integer paymentStatus;
	
	@ApiModelProperty(value = "商品总价")
	private BigDecimal totalCommodityPrice;
	
	@ApiModelProperty(value = "实际物流费用")
	private BigDecimal actualLogisticsCost;
	
	@ApiModelProperty(value = "预估物流费用")
	private BigDecimal logisticsFare;
	
	@ApiModelProperty(value = "订单总额")
	private BigDecimal totalOrder;
	
	@ApiModelProperty(value = "待付金额")
	private BigDecimal amountDue;
	
	@ApiModelProperty(value = "冻结金额")
	private BigDecimal frozenAmount;
	
	@ApiModelProperty(value = "已付金额")
	private BigDecimal paidAmount;
	
	@ApiModelProperty(value = "退回金额/补款金额")
	private BigDecimal backReplenishmentAmount;
	
	@ApiModelProperty(value = "待补款")
	private BigDecimal balanceToBeMade;
	
	@ApiModelProperty(value = "物流商id")
	private String logisticsId;
	
	@ApiModelProperty(value = "物流商名称")
	private String logisticsName;
	
	@ApiModelProperty(value = "仓库id")
	private String storageId;
	
	@ApiModelProperty(value = "仓库名称")
	private String storageName;

	@ApiModelProperty(value = "补发货状态")
	private String status;
	
	private List<AfterSalesCommodityModel> orderCommodity;

	private List<OrderOperationLogModel> orderStatusLog;


	public List<OrderOperationLogModel> getOrderStatusLog() {
		return orderStatusLog;
	}

	public void setOrderStatusLog(List<OrderOperationLogModel> orderStatusLog) {
		this.orderStatusLog = orderStatusLog;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNumberingId() {
		return numberingId;
	}

	public void setNumberingId(String numberingId) {
		this.numberingId = numberingId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public BigDecimal getTotalCommodityPrice() {
		return totalCommodityPrice;
	}

	public void setTotalCommodityPrice(BigDecimal totalCommodityPrice) {
		this.totalCommodityPrice = totalCommodityPrice;
	}

	public BigDecimal getActualLogisticsCost() {
		return actualLogisticsCost;
	}

	public void setActualLogisticsCost(BigDecimal actualLogisticsCost) {
		this.actualLogisticsCost = actualLogisticsCost;
	}

	public BigDecimal getLogisticsFare() {
		return logisticsFare;
	}

	public void setLogisticsFare(BigDecimal logisticsFare) {
		this.logisticsFare = logisticsFare;
	}

	public String getLogisticsId() {
		return logisticsId;
	}

	public void setLogisticsId(String logisticsId) {
		this.logisticsId = logisticsId;
	}

	public String getLogisticsName() {
		return logisticsName;
	}

	public void setLogisticsName(String logisticsName) {
		this.logisticsName = logisticsName;
	}

	public String getStorageId() {
		return storageId;
	}

	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public BigDecimal getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(BigDecimal totalOrder) {
		this.totalOrder = totalOrder;
	}

	public BigDecimal getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(BigDecimal amountDue) {
		this.amountDue = amountDue;
	}

	public BigDecimal getFrozenAmount() {
		return frozenAmount;
	}

	public void setFrozenAmount(BigDecimal frozenAmount) {
		this.frozenAmount = frozenAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getBalanceToBeMade() {
		return balanceToBeMade;
	}

	public void setBalanceToBeMade(BigDecimal balanceToBeMade) {
		this.balanceToBeMade = balanceToBeMade;
	}

	public BigDecimal getBackReplenishmentAmount() {
		return backReplenishmentAmount;
	}

	public void setBackReplenishmentAmount(BigDecimal backReplenishmentAmount) {
		this.backReplenishmentAmount = backReplenishmentAmount;
	}
}
