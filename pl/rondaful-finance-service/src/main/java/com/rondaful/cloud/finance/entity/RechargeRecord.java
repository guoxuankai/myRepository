package com.rondaful.cloud.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.rondaful.cloud.finance.enums.ExamineStatus;
import com.rondaful.cloud.finance.utils.OrderUtils;
import com.rondaful.cloud.finance.vo.ExamineRequestVo;
import com.rondaful.cloud.finance.vo.RechargeRequestVo;

import io.swagger.annotations.ApiModelProperty;

public class RechargeRecord implements Serializable {
	@ApiModelProperty(name = "ID", required = true)
	private Integer rechargeId;
	@ApiModelProperty(name = "充值单号", required = true)
	private String rechargeNo;
	@ApiModelProperty(name = "充值账号", required = true)
	private String rechargeAccount;
	@ApiModelProperty(name = "充值金额", required = true)
	private BigDecimal rechargeAmount;
	@ApiModelProperty(name = "充值方式", required = true)
	private String rechargeType;
	@ApiModelProperty(name = "创建时间", required = false)//外部不可修改
	private Date createTime;
	@ApiModelProperty(name = "转账回执", required = true)
	private String transferReceiptUrl;
	@ApiModelProperty(name = "更改时间", required = false)//外部不可修改
	private Date modifyTime;
	@ApiModelProperty(name = "审核状态", required = true)
	private String examineStatus;
	@ApiModelProperty(name = "状态", required = false)//外部不可修改
	private String tbStatus;
	@ApiModelProperty(name = "说明", required = true)
	private String remark;
	@ApiModelProperty(name = "版本号", required = true)
	private Integer version;
	@ApiModelProperty(name = "卖家名称", required = true)
	private String sellerName;
	@ApiModelProperty(name = "卖家ID", required = true)
	private Integer sellerId;
	@ApiModelProperty(name = "转账流水号", required = true)
	private String transSerialNo;
	@ApiModelProperty(name = "审核批注", required = true)
	private String annotation;

	private static final long serialVersionUID = 1L;

	public RechargeRecord() {
	}

	public RechargeRecord(RechargeRequestVo rechargeRequestVo) {
		BeanUtils.copyProperties(rechargeRequestVo, this);
		this.rechargeNo = "CZ" + OrderUtils.getOrderSn();// 充值流水号
		this.examineStatus = ExamineStatus.待审核.name();
	}

	public RechargeRecord(ExamineRequestVo examineRequestVo) {
		this.examineStatus = examineRequestVo.getExamineStatus();
		this.rechargeId = examineRequestVo.getId();
		this.annotation = examineRequestVo.getAnnotation();
		this.version = examineRequestVo.getVersion();
	}

	public Integer getRechargeId() {
		return rechargeId;
	}

	public void setRechargeId(Integer rechargeId) {
		this.rechargeId = rechargeId;
	}

	public String getRechargeNo() {
		return rechargeNo;
	}

	public void setRechargeNo(String rechargeNo) {
		this.rechargeNo = rechargeNo == null ? null : rechargeNo.trim();
	}

	public String getRechargeAccount() {
		return rechargeAccount;
	}

	public void setRechargeAccount(String rechargeAccount) {
		this.rechargeAccount = rechargeAccount == null ? null : rechargeAccount.trim();
	}

	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getRechargeType() {
		return rechargeType;
	}

	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType == null ? null : rechargeType.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getTransferReceiptUrl() {
		return transferReceiptUrl;
	}

	public void setTransferReceiptUrl(String transferReceiptUrl) {
		this.transferReceiptUrl = transferReceiptUrl == null ? null : transferReceiptUrl.trim();
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
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

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName == null ? null : sellerName.trim();
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
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