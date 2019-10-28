package com.rondaful.cloud.supplier.vo;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "售后订单VO")
public class OrderAfterSalesVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "订单号", required = true)
	private String orderId;

	@ApiModelProperty(value = "卖家品连账号", required = true)
	private String seller;

	@ApiModelProperty(value = "订单参考号")
	private String trackingId;

	// @ApiModelProperty(value = "退款金额", required = true)
	// private String refundMoney;

	@ApiModelProperty(value = "图片[xxx,xxx]多个用逗号隔开")
	private String image;

	@ApiModelProperty(value = "邮费", required = true)
	private String postage;

	@ApiModelProperty(value = "订单详情[根据订单号查询得到的所有数据转为json]", required = true)
	private String orderDetails;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "状态[ 1、待审核; 2、查看; 3、审核; 4、待确认; 5、已确认; 6、(已确认)系统超时自动确认; 7、已关闭; 8、(已关闭)系统超时已关闭; 9、全部确认; 10、退款完成; 11、已取消; 12、(已取消)系统超时关闭，没有编辑; 13、审核失败; 14、(审核失败)系统超时关闭，没有编辑; 15、编辑; 16、待退货; 17、提交物流信息; 18、退货中; 19、已收货; 20、收货完成协商退款; 21、补发配货中; 22、补发已发货; 23、售后结案; 24、修改物流信息; 25、自动关闭; 26、拒绝退款; 27、退货完成等待退款; 28、已收货; 29、自动收货; 30、重发; 31、同意退款; 32、取消; 33、新建提交; 34、修改提交; 35、退款中; ]")
	private Long status;

	@ApiModelProperty(value = "退款原因来源于公共参数接口", required = true)
	private String refundReason;
	
	@ApiModelProperty(value = "店铺")
	private String shop;

	private List<AfterSalesCommodityVo> orderCommodity;

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

	// public String getRefundMoney() {
	// return refundMoney;
	// }

	// public void setRefundMoney(String refundMoney) {
	// this.refundMoney = refundMoney;
	// }

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

	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

}
