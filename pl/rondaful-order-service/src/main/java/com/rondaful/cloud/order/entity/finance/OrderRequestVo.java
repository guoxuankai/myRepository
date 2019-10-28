package com.rondaful.cloud.order.entity.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
@Data
public class OrderRequestVo implements Serializable {
	@ApiModelProperty(value = "卖家名称   [没有则传SellerAccount]", required = true)
	private String sellerName;
	@ApiModelProperty(value = "商品金额", required = true)
	private BigDecimal productAmount;
	@ApiModelProperty(value = "预估物流费用", required = true)
	private BigDecimal logisticsFare;
	@ApiModelProperty(value = "卖家ID", required = true)
	private Integer sellerId;
	@ApiModelProperty(value = "订单号   [唯一]", required = true)
	private String orderNo;
	@ApiModelProperty(value = "卖家账户", required = true)
	private String sellerAccount;
	@ApiModelProperty(value = "物流商ID", required = true)
	private String logisticsId;
	@ApiModelProperty(value = "物流商名称")
	private String logisticsName;
	@ApiModelProperty(value = "仓库ID", required = true)
	private String storageId;
	@ApiModelProperty(value = "仓库名称")
	private String storageName;
	@ApiModelProperty(value = "订单项内容", required = true)
	private List<OrderItemVo> orderItems;
	@ApiModelProperty(value = "供应链公司ID V2.0新增", required = true)
	private Integer supplyCompanyId;
	@ApiModelProperty(value = "供应链公司名称  V2.0新增", required = true)
	private String supplyCompanyName;

	@ApiModelProperty(value = "店铺ID")
	private Integer shopId;
	@ApiModelProperty(value = "币种")
	private String currency;
	@ApiModelProperty(value = "平台订单总额")
	private BigDecimal platformTotal;
	@ApiModelProperty(value = "店铺类型")
	private String shopType;
	@ApiModelProperty(value = "包邮类型", required = true)
	private Boolean freeShipping;
	@ApiModelProperty(value = "用户id， 对该订单进行发货操作的人", required = false)
	private Integer operateUserId;
}
