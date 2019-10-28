package com.rondaful.cloud.order.entity.supplier;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FreightTrialDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "总费用")
    private Double totalCost;

    @ApiModelProperty(value = "费用明细")
    private List costDetail;

    @ApiModelProperty(value = "配送方式名称")
    private String deliveryName;

    @ApiModelProperty(value = "配送方式代码")
    private String deliveryCode;

    @ApiModelProperty(value = "最快时效")
    private Integer minDeliveryTime;

    @ApiModelProperty(value = "最慢时效")
    private Integer maxDeliveryTime;

    @ApiModelProperty(value = "费用币种")
    private String currency;

    @ApiModelProperty(value = "折扣")
    private Double discount;

    @ApiModelProperty(value = "最大限重")
    private Double maxWeight;

    @ApiModelProperty(value = "折后运费")
    private Double afterDiscountAmount;
}