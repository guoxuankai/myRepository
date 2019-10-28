package com.brandslink.cloud.logistics.entity.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "RuleFirstRenew")
public class RuleFirstRenew implements Serializable {

    @Max(value = 200000, message = "首重（g）最大值为200000")
    @Min(value = 0, message = "首重（g）最小值为0")
    @NotNull(message = "首重（g）不能为空，默认为0")
    @ApiModelProperty(value = "首重（g）")
    private Integer firstWeight;

    @DecimalMax(value = "20000.000", message = "首重收费（元）最高为20000元")
    @DecimalMin(value = "0.000", message = "首重收费（元）最低0元")
    @NotNull(message = "首重收费（元）不可为空")
    @Digits(integer = 18, fraction = 3, message = "首重收费（元）限制为18位整数3位小数")
    @ApiModelProperty(value = "首重收费（元）")
    private BigDecimal firstWeightCharge;

    @Max(value = 200000, message = "续重（g）最大值为200000")
    @Min(value = 0, message = "续重（g）最小值为0")
    @NotNull(message = "续重（g）不能为空，默认为0")
    @ApiModelProperty(value = "续重（g）")
    private Integer renewWeight;

    @DecimalMax(value = "20000.000", message = "续重收费（元）最高为20000元")
    @DecimalMin(value = "0.000", message = "续重收费（元）最低0元")
    @NotNull(message = "续重收费（元）不可为空")
    @Digits(integer = 18, fraction = 3, message = "续重收费（元）限制为18位整数3位小数")
    @ApiModelProperty(value = "续重收费（元）")
    private BigDecimal renewWeightCharge;

    @NotNull(message = "处理费不能为空")
    @DecimalMax(value = "20000.000", message = "处理费最高为20000元")
    @DecimalMin(value = "0.000", message = "处理费最低0元")
    @Digits(integer = 18, fraction = 3, message = "处理费限制为18位整数3位小数")
    @ApiModelProperty(value = "处理费")
    private BigDecimal handlingCharge;

    @NotNull(message = "运费折扣类型不能为空")
    @ApiModelProperty(value = "运费折扣类型（0：无折扣，1：全部折扣，2：部分折扣）")
    private Byte freightRebateType;

    @NotNull(message = "运费折扣不能为空")
    @DecimalMax(value = "1.000", message = "运费折扣最大为1")
    @Digits(integer = 5, fraction = 3, message = "运费折扣限制为1位整数3位小数，最大值为1")
    @ApiModelProperty(value = "运费折扣")
    private BigDecimal freightRebate;
}