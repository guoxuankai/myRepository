package com.rondaful.cloud.order.entity.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Data
@ApiModel(value ="FinanceOrderRecord")
public class FinanceOrderRecord implements Serializable {
	@ApiModelProperty(value = "ID")
	private Integer orderId;
	@ApiModelProperty(value = "交易流水号")//支付ID-pay_id
	private String serialNo;
	@ApiModelProperty(value = "订单号", required = true)
	private String orderNo;
	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	@ApiModelProperty(value = "修改时间")
	private Date modifyTime;
	@ApiModelProperty(value = "卖家名称", required = true)
	private String sellerName;
	@ApiModelProperty(value = "商品金额", required = true)
	private BigDecimal productAmount;
	@ApiModelProperty(value = "物流费用", required = true)
	private BigDecimal logisticsFare;
	@ApiModelProperty(value = "应付金额", required = true)
	private BigDecimal payableAmount;
	@ApiModelProperty(value = "实付金额", required = true)
	private BigDecimal actualAmount;
	@ApiModelProperty(value = "补扣物流费用")
	private BigDecimal fillLogisticsFare;
	@ApiModelProperty(value = "审核状态")
	private String examineStatus;
	@ApiModelProperty(value = "说明")
	private String remark;
	@ApiModelProperty(value = "版本号")
	private Integer version;
	@ApiModelProperty(value = "状态")
	private String tbStatus;
	@ApiModelProperty(value = "卖家ID", required = true)
	private Integer sellerId;
	@ApiModelProperty(value = "卖家账户", required = true)
	private String sellerAccount;
	@ApiModelProperty(value = "真实物流费用")
	private BigDecimal actualLogisticsFare;
	@ApiModelProperty(value = "物流商ID", required = true)
	private Integer logisticsId;
	@ApiModelProperty(value = "物流商名称", required = true)
	private String logisticsName;
	@ApiModelProperty(value = "仓库ID", required = true)
	private Integer storageId;
	@ApiModelProperty(value = "仓库名称", required = true)
	private String storageName;
	@ApiModelProperty(value = "结算ID")
	private Integer settlementId;
	@ApiModelProperty(value = "真实支付时间(发货时间)")
	private Date payTime;
}