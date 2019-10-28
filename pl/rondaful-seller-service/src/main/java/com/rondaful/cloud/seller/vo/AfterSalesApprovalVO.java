package com.rondaful.cloud.seller.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "补货售后审批")
public class AfterSalesApprovalVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "[审核描述 备注]  |  审批时根据需求使用")
	private String remark;

	@ApiModelProperty(value = "商品费用[1-供应商、2-平台]  |  审批时根据需求使用")
	private Long commodityCost;

	@ApiModelProperty(value = "物流费用[1-供应商、2-平台]  |  审批时根据需求使用")
	private Long logisticsCost;

	public AfterSalesApprovalVO() {

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
