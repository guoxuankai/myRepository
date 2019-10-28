package com.rondaful.cloud.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.rondaful.cloud.finance.enums.ExamineStatus;
import com.rondaful.cloud.finance.utils.OrderUtils;
import com.rondaful.cloud.finance.vo.ExamineRequestVo;
import com.rondaful.cloud.finance.vo.WithdrawRequestVo;

import io.swagger.annotations.ApiModelProperty;

public class WithdrawalRecord implements Serializable {
	@ApiModelProperty(name = "ID", required = true)
	private Integer withdrawalId;
	@ApiModelProperty(name = "提现单号", required = true)
	private String withdrawalNo;
	@ApiModelProperty(name = "创建时间", required = false) // 外部不可修改
	private Date createTime;
	@ApiModelProperty(name = "打款时间", required = true)
	private Date payTime;
	@ApiModelProperty(name = "更改时间", required = false) // 外部不可修改
	private Date modifyTime;
	@ApiModelProperty(name = "供应商名称", required = true)
	private String supplierName;
	@ApiModelProperty(name = "提现金额", required = true)
	private BigDecimal withdrawalAmount;
	@ApiModelProperty(name = "提现方式", required = true)
	private String withdrawalType;
	@ApiModelProperty(name = "发票URL", required = true)
	private String billUrl;
	@ApiModelProperty(name = "转账回执URL", required = true)
	private String transferReceiptUrl;
	@ApiModelProperty(name = "审核状态", required = true)
	private String examineStatus;
	@ApiModelProperty(name = "状态", required = false) // 外部不可修改
	private String tbStatus;
	@ApiModelProperty(name = "说明", required = true)
	private String remark;
	@ApiModelProperty(name = "版本号", required = true)
	private Integer version;
	@ApiModelProperty(name = "供应商ID", required = true)
	private Integer supplierId;
	@ApiModelProperty(name = "转账流水号", required = true)
	private String transSerialNo;
	@ApiModelProperty(name = "审核批注", required = true)
	private String annotation;
	@ApiModelProperty(name = "提现账户", required = true)
	private String withdrawalAccount;

	private static final long serialVersionUID = 1L;

	public WithdrawalRecord() {
	}

	public WithdrawalRecord(WithdrawRequestVo withdrawRequestVo) {
		BeanUtils.copyProperties(withdrawRequestVo, this);
		this.withdrawalNo = "TX" + OrderUtils.getOrderSn();// 流水号
		this.examineStatus = ExamineStatus.待审核.name();
	}

	public WithdrawalRecord(ExamineRequestVo examineRequestVo) {
		this.examineStatus = examineRequestVo.getExamineStatus();
		this.withdrawalId = examineRequestVo.getId();
		this.annotation = examineRequestVo.getAnnotation();
		this.version = examineRequestVo.getVersion();
	}

	public WithdrawalRecord(Integer id, Date payTime, String transferReceiptUrl, String transSerialNo,
			Integer version) {
		this.withdrawalId = id;
		this.payTime = payTime;
		this.transferReceiptUrl = transferReceiptUrl;
		this.transSerialNo = transSerialNo;
		this.version = version;
		this.examineStatus = ExamineStatus.打款成功.name();
	}

	public String getWithdrawalAccount() {
		return withdrawalAccount;
	}

	public void setWithdrawalAccount(String withdrawalAccount) {
		this.withdrawalAccount = withdrawalAccount;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Integer getWithdrawalId() {
		return withdrawalId;
	}

	public void setWithdrawalId(Integer withdrawalId) {
		this.withdrawalId = withdrawalId;
	}

	public String getWithdrawalNo() {
		return withdrawalNo;
	}

	public void setWithdrawalNo(String withdrawalNo) {
		this.withdrawalNo = withdrawalNo == null ? null : withdrawalNo.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName == null ? null : supplierName.trim();
	}

	public BigDecimal getWithdrawalAmount() {
		return withdrawalAmount;
	}

	public void setWithdrawalAmount(BigDecimal withdrawalAmount) {
		this.withdrawalAmount = withdrawalAmount;
	}

	public String getWithdrawalType() {
		return withdrawalType;
	}

	public void setWithdrawalType(String withdrawalType) {
		this.withdrawalType = withdrawalType == null ? null : withdrawalType.trim();
	}

	public String getBillUrl() {
		return billUrl;
	}

	public void setBillUrl(String billUrl) {
		this.billUrl = billUrl == null ? null : billUrl.trim();
	}

	public String getTransferReceiptUrl() {
		return transferReceiptUrl;
	}

	public void setTransferReceiptUrl(String transferReceiptUrl) {
		this.transferReceiptUrl = transferReceiptUrl == null ? null : transferReceiptUrl.trim();
	}

	public String getExamineStatus() {
		return examineStatus;
	}

	public void setExamineStatus(String examineStatus) {
		this.examineStatus = examineStatus == null ? null : examineStatus.trim();
	}

	public String getTbStatus() {
		return tbStatus;
	}

	public void setTbStatus(String tbStatus) {
		this.tbStatus = tbStatus == null ? null : tbStatus.trim();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getTransSerialNo() {
		return transSerialNo;
	}

	public void setTransSerialNo(String transSerialNo) {
		this.transSerialNo = transSerialNo == null ? null : transSerialNo.trim();
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation == null ? null : annotation.trim();
	}
}