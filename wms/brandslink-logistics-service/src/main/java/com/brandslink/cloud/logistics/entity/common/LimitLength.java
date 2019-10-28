package com.brandslink.cloud.logistics.entity.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "LimitLength")
public class LimitLength implements Serializable {

    @Max(value = 6000, message = "长最大值为6000mm")
    @Min(value = 0, message = "长最小值为0mm")
    @NotNull(message = "长不能为空")
    @Digits(integer = 18, fraction = 1, message = "长限制为4位整数，最大值为6000mm，最小值为0mm")
    @ApiModelProperty(value = "长")
    private Integer length;

    @Max(value = 6000, message = "宽最大值为6000mm")
    @Min(value = 0, message = "宽最小值为0mm")
    @NotNull(message = "宽不能为空")
    @Digits(integer = 18, fraction = 1, message = "宽限制为4位整数，最大值为6000mm，最小值为0mm")
    @ApiModelProperty(value = "宽")
    private Integer wide;

    @Max(value = 6000, message = "高最大值为6000mm")
    @Min(value = 0, message = "高最小值为0mm")
    @NotNull(message = "高不能为空")
    @Digits(integer = 18, fraction = 1, message = "高限制为4位整数，最大值为6000mm，最小值为0mm")
    @ApiModelProperty(value = "高")
    private Integer high;

    @Max(value = 6000, message = "三边长最大值为6000mm")
    @Min(value = 0, message = "三边长最小值为0mm")
    @NotNull(message = "三边长不能为空")
    @Digits(integer = 18, fraction = 1, message = "三边长限制为4位整数，最大值为6000mm，最小值为0mm")
    @ApiModelProperty(value = "三边长")
    private Integer threeLength;
}