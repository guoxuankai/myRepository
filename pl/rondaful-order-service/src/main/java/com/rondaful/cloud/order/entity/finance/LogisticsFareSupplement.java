package com.rondaful.cloud.order.entity.finance;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("LogisticsFareSupplement")
public class LogisticsFareSupplement {
    @ApiModelProperty(value = "订单实付金额")
    private BigDecimal actualAmount;

    @ApiModelProperty(value = "订单实际物流金额")
    private BigDecimal actualLogisticsFare;

    @ApiModelProperty(value="创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "订单冻结金额")
    private BigDecimal freezeAmount;

    @ApiModelProperty(value = "订单预估物流金额")
    private BigDecimal logisticsFare;

    @ApiModelProperty(value="补扣订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单应付金额")
    private BigDecimal payableAmount;

    @ApiModelProperty(value="卖家ID")
    private Integer sellerId;

    @ApiModelProperty(value="补扣订单序列号")
    private String serialNo;

    @ApiModelProperty(value="补扣金额")
    private BigDecimal supplementAmount;

    @ApiModelProperty(value="ID")
    private Integer supplementId;

    @ApiModelProperty(value="数据状态")
    private String tbStatus;

}
