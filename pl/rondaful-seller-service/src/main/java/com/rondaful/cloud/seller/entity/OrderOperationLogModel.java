package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "退款退货操作日志")
public class OrderOperationLogModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "售后订单ID")
	private String orderAfterSalesId;

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	@ApiModelProperty(value = "是否客服Y/N C")
	private String isKF;

	@ApiModelProperty(value = "卖家ID")
	private String sellerId;

	@ApiModelProperty(value = "操作人ID")
	private String operationUserId;

	@ApiModelProperty(value = "处理")
	private String handleNode;

	@ApiModelProperty(value = "处理后的状态")
	private String status;

	@ApiModelProperty(value = "备注")
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderAfterSalesId() {
		return orderAfterSalesId;
	}

	public void setOrderAfterSalesId(String orderAfterSalesId) {
		this.orderAfterSalesId = orderAfterSalesId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getIsKF() {
		return isKF;
	}

	public void setIsKF(String isKF) {
		this.isKF = isKF;
	}


	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getOperationUserId() {
		return operationUserId;
	}

	public void setOperationUserId(String operationUserId) {
		this.operationUserId = operationUserId;
	}

	public String getHandleNode() {
		return handleNode;
	}

	public void setHandleNode(String handleNode) {
		this.handleNode = handleNode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
