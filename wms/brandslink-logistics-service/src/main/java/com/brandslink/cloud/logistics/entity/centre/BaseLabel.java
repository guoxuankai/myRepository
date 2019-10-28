package com.brandslink.cloud.logistics.entity.centre;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@ApiModel(value = "物流商公共打印标签对象")
public class BaseLabel {


    @NotBlank(message = "运单号不能为空")
    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    @NotBlank(message = "运单号不能为空")
    @ApiModelProperty(value = "运单号")
    private String waybillNumber;

    @NotBlank(message = "物流商编码不能为空")
    @ApiModelProperty(value = "物流商编码")
    private String logisticsCode;


}
