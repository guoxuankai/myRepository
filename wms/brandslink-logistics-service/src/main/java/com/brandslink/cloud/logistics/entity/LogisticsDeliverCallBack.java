package com.brandslink.cloud.logistics.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LogisticsDeliverCallBack implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String customerOrderNumber;

    @ApiModelProperty(value = "运单号")
    private String wayBillNumber;

    @ApiModelProperty(value = "跟踪号")
    private String trackNumber;

    @ApiModelProperty(value = "面单信息url")
    private String faceSheetUrl;
}
