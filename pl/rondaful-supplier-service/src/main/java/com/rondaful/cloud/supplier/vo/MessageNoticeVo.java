package com.rondaful.cloud.supplier.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "MessageNotice")
public class MessageNoticeVo {

	@ApiModelProperty(value = "消息种类[ 客服消息CUSTOMER_MESSAGE、售后消息AFTER_MESSAGE、商品消息COMMODITY_MESSAGE、订单消息ORDER_MESSAGE、库存通知INVENTORY_NOTICE、财务通知FINANCIAL_NOTICE ]", required = true)
	private String messageCategory;

	@ApiModelProperty(value = "消息类型[ 工单回复WORK_ORDER_REPLY、仅退款退款成功REFUND_SUCCESSFUL、仅退款申请失败REFUND_FAILED、退货单申请成功RETURN_ORDER_APPLY_SUCCESSFUL、退货单退款成功RETURN_ORDER_SUCCESSFUL、退货单退款失败RETURN_ORDER_FAILED、售后退货超时RETURN_ORDER_INVALID、补发货申请成功REPLENISHMENT_APPLY_SUCCESSFUL、补发货申请失败REPLENISHMENT_APPLY_FAILED、补发货仓库发货REPLENISHMENT_SHIPPED、商品审核成功COMMODITY_AUDITING_SUCCESSFUL、商品审核失败COMMODITY_AUDITING_FAILED、品牌审核成功BRAND_AUDITING_SUCCESSFUL、品牌审核失败BRAND_AUDITING_FAILED、订单发货通知ORDER_DELIVERY_NOTICE、订单新建通知ORDER_NEW_NOTICE、订单异常通知ORDER_EXCEPTION_NOTICE、订单缺货通知ORDER_OUT_OF_STOCK_NOTICE、订单作废通知ORDER_INVALID_NOTICE、订单拦截通知ORDER_INTERCEPT_NOTICE、库存预警通知INVENTORY_EARLY_WARNING_NOTICE、结算通知SETTLEMENT_NOTICE ]", required = true)
	private String messageType;

	@ApiModelProperty(value = "消息内容可能会有多个值情况,每个值用 # 隔开", required = true)
	private String messageContent;

	@ApiModelProperty(value = "消息接收人userName，如有多个userName每个值用 #*# 隔开 ", required = true)
	private String messageScceptUserName;
	
	@ApiModelProperty(value = "消息接收平台 [ 0全部 1PC 2App ]", required = true)
	private String messagePlatform;
	
	@ApiModelProperty(value = "接收平台 0供应商 1卖家 , 2管理后台  ", required = true)
	private String receiveSys;

	public MessageNoticeVo() {

	}

	public MessageNoticeVo(String messageCategory, String messageType, String messageContent,
			String messageScceptUserName, String messagePlatform, String receiveSys) {
		super();
		this.messageCategory = messageCategory;
		this.messageType = messageType;
		this.messageContent = messageContent;
		this.messageScceptUserName = messageScceptUserName;
		this.messagePlatform = messagePlatform;
		this.receiveSys = receiveSys;
	}




	public String getMessageCategory() {
		return messageCategory;
	}

	public void setMessageCategory(String messageCategory) {
		this.messageCategory = messageCategory;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getMessageScceptUserName() {
		return messageScceptUserName;
	}

	public void setMessageScceptUserName(String messageScceptUserName) {
		this.messageScceptUserName = messageScceptUserName;
	}

	public String getMessagePlatform() {
		return messagePlatform;
	}

	public void setMessagePlatform(String messagePlatform) {
		this.messagePlatform = messagePlatform;
	}

	public String getReceiveSys() {
		return receiveSys;
	}

	public void setReceiveSys(String receiveSys) {
		this.receiveSys = receiveSys;
	}

	@Override
	public String toString() {
		return "MessageNoticeModel [messageCategory=" + messageCategory + ", messageType=" + messageType
				+ ", messageContent=" + messageContent + ", messageScceptUserName=" + messageScceptUserName
				+ ", messagePlatform=" + messagePlatform + ", receiveSys=" + receiveSys + "]";
	}

}
