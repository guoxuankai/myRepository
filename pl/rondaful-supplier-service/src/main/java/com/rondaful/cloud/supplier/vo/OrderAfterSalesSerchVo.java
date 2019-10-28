package com.rondaful.cloud.supplier.vo;

import com.rondaful.cloud.common.utils.DateUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后搜索VO")
public class OrderAfterSalesSerchVo {

	@ApiModelProperty(value = "页码 >>   导出报表不需要")
	private String page;

	@ApiModelProperty(value = "每页显示行数 >> 导出报表不需要")
	private String row;

	@ApiModelProperty(value = "状态")
	private Long status;

	@ApiModelProperty(value = "订单号")
	private String orderId;

	@ApiModelProperty(value = "开始时间,格式[yyyy-MM-dd HH:mm:ss]开始与结束一起使用")
	private String startTime;

	@ApiModelProperty(value = "结束时间,格式[yyyy-MM-dd HH:mm:ss]开始与结束一起使用")
	private String endTime;

	@ApiModelProperty(value = "默认0查询全部、售后类型[1-仅退款、2-退款+退货、3-补货]")
	private Long afterSalesType = 0L;

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = DateUtils.time(startTime, "start");
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = DateUtils.time(endTime, "end");
	}

	public Long getAfterSalesType() {
		return afterSalesType;
	}

	public void setAfterSalesType(Long afterSalesType) {
		this.afterSalesType = afterSalesType;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row;
	}

}
