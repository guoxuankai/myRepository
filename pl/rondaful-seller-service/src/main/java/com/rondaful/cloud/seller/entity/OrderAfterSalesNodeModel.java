package com.rondaful.cloud.seller.entity;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后节点备注信息")
public class OrderAfterSalesNodeModel {

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "售后ID")
	private String orderAfterSalesId;

	@ApiModelProperty(value = "节点名")
	private String nodeName;

	@ApiModelProperty(value = "节点备注")
	private String remark;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	public OrderAfterSalesNodeModel(String orderAfterSalesId, String nodeName, String remark) {
		this.orderAfterSalesId = orderAfterSalesId;
		this.nodeName = nodeName;
		this.remark = remark;
	}

	public OrderAfterSalesNodeModel() {

	}

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

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
