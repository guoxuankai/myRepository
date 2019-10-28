package com.rondaful.cloud.seller.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "补货订单VO")
public class ReplenishmentVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "订单号", required = true)
	private String orderId;

	@ApiModelProperty(value = "卖家品连账号", required = true)
	private String seller;

	@ApiModelProperty(value = "图片[xxx,xxx]多个用逗号隔开")
	private String image;

	@ApiModelProperty(value = "订单详情[根据订单号查询得到的所有数据转为json]", required = true)
	private String orderDetails;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "")
	private Long status;

	@ApiModelProperty(value = "店铺信息", required = true)
	private String shop;

	@ApiModelProperty(value = "", required = true)
	private String refundReason;

	@ApiModelProperty(value = "仓库返回的订单号")
	private String referenceId;

	@ApiModelProperty(value = "仓库类型0自营仓库 2谷仓", required = true)
	private String warehouseType;


	private ReceivingAddressVO addressVO;

	private List<AfterSalesCommodityVo> orderCommodity;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getWarehouseType() {
		return warehouseType;
	}

	public void setWarehouseType(String warehouseType) {
		this.warehouseType = warehouseType;
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

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public List<AfterSalesCommodityVo> getOrderCommodity() {
		return orderCommodity;
	}

	public void setOrderCommodity(List<AfterSalesCommodityVo> orderCommodity) {
		this.orderCommodity = orderCommodity;
	}

	public ReceivingAddressVO getAddressVO() {
		return addressVO;
	}

	public void setAddressVO(ReceivingAddressVO addressVO) {
		this.addressVO = addressVO;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

}

