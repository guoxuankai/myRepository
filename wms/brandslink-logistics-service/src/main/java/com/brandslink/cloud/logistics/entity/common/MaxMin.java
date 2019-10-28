package com.brandslink.cloud.logistics.entity.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "MaxMin")
public class MaxMin implements Serializable {
    @NotNull(message = "最大值不能为空")
    @ApiModelProperty(value = "最大值")
    private Integer max;

    @NotNull(message = "最小值不能为空")
    @ApiModelProperty(value = "最小值")
    private Integer min;
}