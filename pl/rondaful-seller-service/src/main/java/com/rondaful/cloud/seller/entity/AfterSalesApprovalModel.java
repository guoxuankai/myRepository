package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后审批")
public class AfterSalesApprovalModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "物流商 | 退款+退货补单时需要")
	private String logisticsCompany;

	@ApiModelProperty(value = "退货跟踪单号 | 退款+退货补单时需要")
	private String logisticsOrder;

	@ApiModelProperty(value = "退款条件[1-丢弃商品、2-返回仓库]  |  审批时根据需求使用")
	private Long conditions;

	@ApiModelProperty(value = "退款范围[1-商品费用、2-物流费用、3-商品+物流费用]  |  审批时根据需求使用")
	private Long scope;

	@ApiModelProperty(value = "仓库  |  审批时根据需求使用")
	private String warehouse;

	@ApiModelProperty(value = "收货人  |  审批时根据需求使用")
	private String consignee;

	@ApiModelProperty(value = "收货人电话  |  审批时根据需求使用")
	private String consigneePhone;

	@ApiModelProperty(value = "退货地址  |  审批时根据需求使用")
	private String refundAddress;

	@ApiModelProperty(value = "[审核描述 备注]  |  审批时根据需求使用")
	private String remark;

	@ApiModelProperty(value = "售后订单ID  |  审批时根据需求使用")
	private String orderAfterSalesId;

	@ApiModelProperty(value = "商品费用[1-供应商、2-平台]  |  审批时根据需求使用")
	private Long commodityCost;

	@ApiModelProperty(value = "物流费用[1-供应商、2-平台]  |  审批时根据需求使用")
	private Long logisticsCost;

	@ApiModelProperty(value = "仓库对应的国家  |  审批时根据需求使用")
	private String countries;

	@ApiModelProperty(value = "仓库对应的编码  |  退款退货.审批时根据需求使用")
	private String warehouseCode;

	@ApiModelProperty(value = "仅后台退款传入收货信息数据,其他不需要传入  |  退款退货.审批时根据需求使用")
	private List<OrderAfterSalesReceiptMessageModel> listReceiptMessage;

	public AfterSalesApprovalModel(String logisticsCompany, String logisticsOrder, String orderAfterSalesId) {
		this.logisticsCompany = logisticsCompany;
		this.logisticsOrder = logisticsOrder;
		this.orderAfterSalesId = orderAfterSalesId;
	}

	public AfterSalesApprovalModel(Long scope, String remark, String orderAfterSalesId) {
		this.scope = scope;
		this.remark = remark;
		this.orderAfterSalesId = orderAfterSalesId;
	}

	public AfterSalesApprovalModel() {

	}

	public Long getConditions() {
		return conditions;
	}

	public void setConditions(Long conditions) {
		this.conditions = conditions;
	}

	public Long getScope() {
		return scope;
	}

	public void setScope(Long scope) {
		this.scope = scope;
	}

	public Long getCommodityCost() {
		return commodityCost;
	}

	public void setCommodityCost(Long commodityCost) {
		this.commodityCost = commodityCost;
	}

	public Long getLogisticsCost() {
		return logisticsCost;
	}

	public void setLogisticsCost(Long logisticsCost) {
		this.logisticsCost = logisticsCost;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public String getConsigneePhone() {
		return consigneePhone;
	}

	public void setConsigneePhone(String consigneePhone) {
		this.consigneePhone = consigneePhone;
	}

	public String getRefundAddress() {
		return refundAddress;
	}

	public void setRefundAddress(String refundAddress) {
		this.refundAddress = refundAddress;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@JsonIgnore
	@JSONField
	public String getOrderAfterSalesId() {
		return orderAfterSalesId;
	}

	public void setOrderAfterSalesId(String orderAfterSalesId) {
		this.orderAfterSalesId = orderAfterSalesId;
	}

	public String getLogisticsCompany() {
		return logisticsCompany;
	}

	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}

	public String getLogisticsOrder() {
		return logisticsOrder;
	}

	public void setLogisticsOrder(String logisticsOrder) {
		this.logisticsOrder = logisticsOrder;
	}

	public String getCountries() {
		return countries;
	}

	public void setCountries(String countries) {
		this.countries = countries;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public List<OrderAfterSalesReceiptMessageModel> getListReceiptMessage() {
		return listReceiptMessage;
	}

	public void setListReceiptMessage(List<OrderAfterSalesReceiptMessageModel> listReceiptMessage) {
		this.listReceiptMessage = listReceiptMessage;
	}

}
