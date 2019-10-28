package com.rondaful.cloud.finance.vo.conditions;

import io.swagger.annotations.ApiModelProperty;

public class BaseConditionVo {
	@ApiModelProperty(name = "开始时间")
	private String beginDate;
	@ApiModelProperty(name = "结束时间")
	private String endDate;
	@ApiModelProperty(name = "页数", required = true)
	private Integer pageNum;
	@ApiModelProperty(name = "页码", required = true)
	private Integer pageSize;
	@ApiModelProperty(name = "数据ID")
	private Integer id;
	@ApiModelProperty(name = "审核状态")
	private String examineStatus;
	@ApiModelProperty(name = "单号")
	private String serialNo;

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExamineStatus() {
		return examineStatus;
	}

	public void setExamineStatus(String examineStatus) {
		this.examineStatus = examineStatus;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
