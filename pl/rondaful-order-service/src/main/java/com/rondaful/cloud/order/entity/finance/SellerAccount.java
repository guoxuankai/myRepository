package com.rondaful.cloud.order.entity.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("SellerAccount")
public class SellerAccount implements Serializable {
    @ApiModelProperty(value = "ID")
    private Integer sellerAccountId;
    @ApiModelProperty(value = "已充值金额")
    private BigDecimal rechargeAmount;
    @ApiModelProperty(value = "已支付金额")
    private BigDecimal consumedAmount;
    @ApiModelProperty(value = "冻结金额")
    private BigDecimal frozenAmount;
    @ApiModelProperty(value = "可用金额")
    private BigDecimal freeAmount;
    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "卖家ID")
    private Integer sellerId;
//    @ApiModelProperty(value = "创建时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date createTime;
//    @ApiModelProperty(value = "修改时间")
//    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date modifyTime;
    @ApiModelProperty(value = "版本号")
    private Integer version;
    @ApiModelProperty(value = "数据状态")
    private String tbStatus;
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;
    @ApiModelProperty(value = "币种")
    private String coinType;
}