package com.rondaful.cloud.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.rondaful.cloud.finance.enums.ExamineStatus;
import com.rondaful.cloud.finance.utils.OrderUtils;
import com.rondaful.cloud.finance.vo.OrderRequestVo;

import io.swagger.annotations.ApiModelProperty;

public class OrderRecord implements Serializable {
	@ApiModelProperty(name = "ID")
	private Integer orderId;
	@ApiModelProperty(name = "交易流水号")
	private String serialNo;
	@ApiModelProperty(name = "订单号", required = true)
	private String orderNo;
	@ApiModelProperty(name = "创建时间")
	private Date createTime;
	@ApiModelProperty(name = "修改时间")
	private Date modifyTime;
	@ApiModelProperty(name = "卖家名称", required = true)
	private String sellerName;
	@ApiModelProperty(name = "商品金额", required = true)
	private BigDecimal productAmount;
	@ApiModelProperty(name = "物流费用", required = true)
	private BigDecimal logisticsFare;
	@ApiModelProperty(name = "应付金额", required = true)
	private BigDecimal payableAmount;
	@ApiModelProperty(name = "实付金额", required = true)
	private BigDecimal actualAmount;
	@ApiModelProperty(name = "补扣物流费用")
	private BigDecimal fillLogisticsFare;
	@ApiModelProperty(name = "审核状态")
	private String examineStatus;
	@ApiModelProperty(name = "说明")
	private String remark;
	@ApiModelProperty(name = "版本号")
	private Integer version;
	@ApiModelProperty(name = "状态")
	private String tbStatus;
	@ApiModelProperty(name = "卖家ID", required = true)
	private Integer sellerId;
	@ApiModelProperty(name = "卖家账户", required = true)
	private String sellerAccount;
	@ApiModelProperty(name = "真实物流费用")
	private BigDecimal actualLogisticsFare;
	@ApiModelProperty(name = "供应商ID")
	private Integer supplierId;
	@ApiModelProperty(name = "供应商名称")
	private String supplierName;
	@ApiModelProperty(name = "结算ID")
	private Integer settlementId;
	

	public BigDecimal getActualLogisticsFare() {
		return actualLogisticsFare;
	}

	public void setActualLogisticsFare(BigDecimal actualLogisticsFare) {
		this.actualLogisticsFare = actualLogisticsFare;
	}

	public String getSellerAccount() {
		return sellerAccount;
	}

	public void setSellerAccount(String sellerAccount) {
		this.sellerAccount = sellerAccount;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	private static final long serialVersionUID = 1L;

	public OrderRecord(OrderRequestVo orderRequestVo) {
		BeanUtils.copyProperties(orderRequestVo, this);
		this.serialNo = "JYB" + OrderUtils.getOrderSn();
		this.examineStatus = ExamineStatus.冻结中.name();
	}
	

	public OrderRecord() {
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo == null ? null : serialNo.trim();
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo == null ? null : orderNo.trim();
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName == null ? null : sellerName.trim();
	}

	public BigDecimal getProductAmount() {
		return productAmount;
	}

	public void setProductAmount(BigDecimal productAmount) {
		this.productAmount = productAmount;
	}

	public BigDecimal getLogisticsFare() {
		return logisticsFare;
	}

	public void setLogisticsFare(BigDecimal logisticsFare) {
		this.logisticsFare = logisticsFare;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public BigDecimal getFillLogisticsFare() {
		return fillLogisticsFare;
	}

	public void setFillLogisticsFare(BigDecimal fillLogisticsFare) {
		this.fillLogisticsFare = fillLogisticsFare;
	}

	public String getExamineStatus() {
		return examineStatus;
	}

	public void setExamineStatus(String examineStatus) {
		this.examineStatus = examineStatus == null ? null : examineStatus.trim();
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

	public String getTbStatus() {
		return tbStatus;
	}

	public void setTbStatus(String tbStatus) {
		this.tbStatus = tbStatus == null ? null : tbStatus.trim();
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}
}