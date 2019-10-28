package com.rondaful.cloud.seller.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rondaful.cloud.common.utils.DateUtils;
import com.rondaful.cloud.seller.vo.OrderAfterSalesSerchVo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后订单")
public class OrderAfterSalesModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "售后编号")
	private String numberingId;

	@ApiModelProperty(value = "订单号")
	private String orderId;

	@ApiModelProperty(value = "卖家品连账号")
	private String seller;

	@ApiModelProperty(value = "退款金额")
	private String refundMoney;

	@ApiModelProperty(value = "图片[xxx,xxx]多个用逗号隔开")
	private String image;

	@ApiModelProperty(value = "订单详情[根据订单号查询得到的所有数据转为json]")
	private String orderDetails;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "邮费")
	private String postage;

	@ApiModelProperty(value = "状态[ 1、待审核; 2、查看; 3、审核; 4、待确认; 5、已确认; 6、(已确认)系统超时自动确认; 7、已关闭; 8、(已关闭)系统超时已关闭; 9、全部确认; 10、退款完成; 11、已取消; 12、(已取消)系统超时关闭，没有编辑; 13、审核失败; 14、(审核失败)系统超时关闭，没有编辑; 15、编辑; 16、待退货; 17、提交物流信息; 18、退货中; 19、已收货; 20、收货完成协商退款; 21、补发配货中; 22、补发已发货; 23、售后结案; 24、修改物流信息; 25、自动关闭; 26、拒绝退款; 27、退货完成等待退款; 28、已收货; 29、自动收货; 30、重发; 31、同意退款; 32、取消; 33、新建提交; 34、修改提交; 35、退款中; ]")
	private Long status;

	@ApiModelProperty(value = "退款原因来源于公共参数接口")
	private String refundReason;

	@JSONField(format = DateUtils.FORMAT_2)
	@ApiModelProperty(value = "申请时间")
	private Date createTime;

	@JSONField(format = DateUtils.FORMAT_2)
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	@ApiModelProperty(value = "售后类型[1-仅退款、2-退款+退货、3-补货]")
	private Long afterSalesType;

	@ApiModelProperty(value = "开始时间")
	private String startTime;

	@ApiModelProperty(value = "结束时间")
	private String endTime;

	@ApiModelProperty(value = "补货跟踪号")
	private String trackingId;

	@ApiModelProperty(value = "店铺")
	private String shop;

	@ApiModelProperty(value = "供应商")
	private String supplier;

	@ApiModelProperty(value = "补发货异常原因(订单系统中写入)")
	private String warehouseShipException;

	@ApiModelProperty(value = "仓库返回的订单号")
	private String referenceId;
	@ApiModelProperty(value = "谷仓返回的退件单号")
	private String gcAsroCode;
	@ApiModelProperty(value = "退货到的仓库类型 0erp 2谷仓")
	private String warehouseType;

	private String shipNumber;

	private List<AfterSalesCommodityModel> orderCommodity;

	private List<OrderOperationLogModel> orderOperationLog;

	private List<AfterSalesApprovalModel> afterSalesApproval;

	private List<OrderAfterSalesReceiptMessageModel> afterSalesReceiptMessage;

	private List<OrderAfterSalesNodeModel> afterNode;

	public OrderAfterSalesModel() {

	}

	public OrderAfterSalesModel(String numberingId, Long status) {
		this.numberingId = numberingId;
		this.status = status;
	}

	public OrderAfterSalesModel(String numberingId, Long status, String refundMoney) {
		this.numberingId = numberingId;
		this.status = status;
		this.refundMoney = refundMoney;
	}

	/**
	 * 
	 * @param oassv
	 * @param supplier 供应商
	 */
	public OrderAfterSalesModel(OrderAfterSalesSerchVo oassv, String supplier) {
		this.numberingId = oassv.getNumberingId();
		this.status = oassv.getStatus();
		this.orderId = oassv.getOrderId();
		this.seller = oassv.getSeller();
		this.startTime = oassv.getStartTime();
		this.endTime = oassv.getEndTime();
		this.afterSalesType = oassv.getAfterSalesType();
		this.supplier = supplier;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getGcAsroCode() {
		return gcAsroCode;
	}

	public void setGcAsroCode(String gcAsroCode) {
		this.gcAsroCode = gcAsroCode;
	}

	public String getWarehouseType() {
		return warehouseType;
	}

	public void setWarehouseType(String warehouseType) {
		this.warehouseType = warehouseType;
	}

	public String getNumberingId() {
		return numberingId;
	}

	public void setNumberingId(String numberingId) {
		this.numberingId = numberingId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(String refundMoney) {
		this.refundMoney = refundMoney;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(String orderDetails) {
		this.orderDetails = orderDetails;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getAfterSalesType() {
		return afterSalesType;
	}

	public void setAfterSalesType(Long afterSalesType) {
		this.afterSalesType = afterSalesType;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public List<AfterSalesCommodityModel> getOrderCommodity() {
		return orderCommodity;
	}

	public void setOrderCommodity(List<AfterSalesCommodityModel> orderCommodity) {
		this.orderCommodity = orderCommodity;
	}

	public List<OrderOperationLogModel> getOrderOperationLog() {
		return orderOperationLog;
	}

	public void setOrderOperationLog(List<OrderOperationLogModel> orderOperationLog) {
		this.orderOperationLog = orderOperationLog;
	}

	public List<AfterSalesApprovalModel> getAfterSalesApproval() {
		return afterSalesApproval;
	}

	public void setAfterSalesApproval(List<AfterSalesApprovalModel> afterSalesApproval) {
		this.afterSalesApproval = afterSalesApproval;
	}

	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}

	@JsonIgnore
	@JSONField
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@JsonIgnore
	@JSONField
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getWarehouseShipException() {
		return warehouseShipException;
	}

	public void setWarehouseShipException(String warehouseShipException) {
		this.warehouseShipException = warehouseShipException;
	}

	public String getShipNumber() {
		return shipNumber;
	}

	public void setShipNumber(String shipNumber) {
		this.shipNumber = shipNumber;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	public List<OrderAfterSalesReceiptMessageModel> getAfterSalesReceiptMessage() {
		return afterSalesReceiptMessage;
	}

	public void setAfterSalesReceiptMessage(List<OrderAfterSalesReceiptMessageModel> afterSalesReceiptMessage) {
		this.afterSalesReceiptMessage = afterSalesReceiptMessage;
	}

	public List<OrderAfterSalesNodeModel> getAfterNode() {
		return afterNode;
	}

	public void setAfterNode(List<OrderAfterSalesNodeModel> afterNode) {
		this.afterNode = afterNode;
	}

	@JsonIgnore
	@JSONField
	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
}
