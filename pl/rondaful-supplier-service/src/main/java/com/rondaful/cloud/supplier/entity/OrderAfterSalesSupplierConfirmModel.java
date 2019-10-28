package com.rondaful.cloud.supplier.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后供应商确认")
public class OrderAfterSalesSupplierConfirmModel implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "[1-仅退款、2-退款+退货、3-补货]")
	private Long type;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	@ApiModelProperty(value = "售后ID")
	private String orderAfterSalesId;

	@ApiModelProperty(value = "供应商")
	private String supplier;

	@ApiModelProperty(value = "后台统计任务名称")
	private String taskName;

	@ApiModelProperty(value = "状态")
	private Long status;

	@ApiModelProperty(value = "供应商商品")
	private List<AfterSalesCommodityModel> commodityData;

	/**
	 * 
	 * @param type              [1-仅退款、2-退款+退货、3-补货]
	 * @param remark            备注
	 * @param orderAfterSalesId 售后ID
	 * @param supplier          供应商
	 * @param taskName          后台统计任务名称
	 * @param status            状态
	 */
	public OrderAfterSalesSupplierConfirmModel(Long type, String remark, String orderAfterSalesId, String supplier, String taskName, Long status) {
		this.type = type;
		this.remark = remark;
		this.orderAfterSalesId = orderAfterSalesId;
		this.supplier = supplier;
		this.taskName = taskName;
		this.status = status;
	}

	public OrderAfterSalesSupplierConfirmModel(String remark, String orderAfterSalesId, String taskName, Long status) {
		this.remark = remark;
		this.orderAfterSalesId = orderAfterSalesId;
		this.taskName = taskName;
		this.status = status;
	}

	public OrderAfterSalesSupplierConfirmModel() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
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

	public String getOrderAfterSalesId() {
		return orderAfterSalesId;
	}

	public void setOrderAfterSalesId(String orderAfterSalesId) {
		this.orderAfterSalesId = orderAfterSalesId;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	@JsonIgnore
	@JSONField
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public List<AfterSalesCommodityModel> getCommodityData() {
		return commodityData;
	}

	public void setCommodityData(List<AfterSalesCommodityModel> commodityData) {
		this.commodityData = commodityData;
	}

}
