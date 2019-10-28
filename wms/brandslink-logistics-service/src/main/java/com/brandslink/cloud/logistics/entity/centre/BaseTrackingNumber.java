package com.brandslink.cloud.logistics.entity.centre;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@ApiModel(value = "物流商公共跟踪号对象")
public class
BaseTrackingNumber {

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "客户订单号")
    private String orderNumber;


    @NotBlank(message = "物流商编码不能为空")
    @ApiModelProperty(value = "物流商编码")
    private String logisticsCode;


}
