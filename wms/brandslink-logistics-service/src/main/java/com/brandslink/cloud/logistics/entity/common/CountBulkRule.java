package com.brandslink.cloud.logistics.entity.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "CountBulkRule")
public class CountBulkRule implements Serializable {

    @Max(value = 20000, message = "起重（g）最大值为20000")
    @ApiModelProperty(value = "起重（g）")
    private Integer initWeight;

    @DecimalMax(value = "10.0", message = "材积与实重倍数最大值为10")
    @Digits(integer = 5, fraction = 1, message = "材积与实重倍数限制为2位整数1位小数")
    @ApiModelProperty(value = "材积与实重倍数")
    private BigDecimal volumeWeightTimes;

    @Max(value = 5000, message = "单边长（mm）最大值为5000")
    @ApiModelProperty(value = "单边长（mm）")
    private Integer unilateralLong;

    @NotNull(message = "抛系不能为空")
    @Min(value = 1, message = "抛系最小值为1")
    @ApiModelProperty(value = "抛系")
    private Integer throwDepartment;

    @Max(value = 4, message = "计抛方式最大值为4")
    @Min(value = 1, message = "计抛方式最小值为1")
    @ApiModelProperty(value = "计抛方式（1：默认，2：计半抛，3：免三分之一抛，4：超过实重的）")
    private Byte meterBehindWay;
}