package com.brandslink.cloud.logistics.entity.centre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author guoxuankai
 * @date 2019/7/31 16:05
 */
@Data
@ApiModel(value = "获取跟踪号返回结果")
public class TrackingNumberResult {

    @ApiModelProperty(value = "客户订单号")
    private String orderNumber;

    @ApiModelProperty(value = "跟踪单号")
    private String trackingNumber;


}