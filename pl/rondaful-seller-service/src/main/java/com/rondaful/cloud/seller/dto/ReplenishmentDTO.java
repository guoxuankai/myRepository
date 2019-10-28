package com.rondaful.cloud.seller.dto;

import java.util.Date;
import java.util.List;

import com.rondaful.cloud.seller.entity.AfterSalesApprovalModel;
import com.rondaful.cloud.seller.entity.OrderOperationLogModel;
import com.rondaful.cloud.seller.vo.ReplenishmentVO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "补发货订单返回VO")
public class ReplenishmentDTO  extends ReplenishmentVO{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	
	@ApiModelProperty(value = "试运算费用")
	private String postage;
	
	@ApiModelProperty(value = "商品总额")
	private String totalCommodities;
	
	@ApiModelProperty(value = "订单总额")
	private String totalOrder;
	
	@ApiModelProperty(value = "实际运算费用")
	private String logisticsCost;
	
	private List<AfterSalesApprovalModel> afterSalesApproval;
	
	private List<OrderOperationLogModel> operationLog;

	private String trackingId;
	
	public List<OrderOperationLogModel> getOperationLog() {
		return operationLog;
	}

	public void setOperationLog(List<OrderOperationLogModel> operationLog) {
		this.operationLog = operationLog;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}

	public String getTotalCommodities() {
		return totalCommodities;
	}

	public void setTotalCommodities(String totalCommodities) {
		this.totalCommodities = totalCommodities;
	}

	public String getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(String totalOrder) {
		this.totalOrder = totalOrder;
	}

	public String getLogisticsCost() {
		return logisticsCost;
	}

	public void setLogisticsCost(String logisticsCost) {
		this.logisticsCost = logisticsCost;
	}

	public List<AfterSalesApprovalModel> getAfterSalesApproval() {
		return afterSalesApproval;
	}

	public void setAfterSalesApproval(List<AfterSalesApprovalModel> afterSalesApproval) {
		this.afterSalesApproval = afterSalesApproval;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
}
