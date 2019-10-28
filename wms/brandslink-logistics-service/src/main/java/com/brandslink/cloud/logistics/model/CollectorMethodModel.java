package com.brandslink.cloud.logistics.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "CollectorMethodModel")
public class CollectorMethodModel implements Serializable {
    @ApiModelProperty(value = "揽收商ID")
    private Long collectorId;

    @ApiModelProperty(value = "物流商ID")
    private Long providerId;

    @ApiModelProperty(value = "物流商简称")
    private String logisticsShortened;

    @ApiModelProperty(value = "物流商编码")
    private String logisticsCode;

    @ApiModelProperty(value = "邮寄方式ID")
    private Long methodId;

    @ApiModelProperty(value = "邮寄方式名称")
    private String logisticsMethodName;

    @ApiModelProperty(value = "邮寄方式编码")
    private String logisticsMethodCode;

    @ApiModelProperty(value = "邮寄方式状态是否有效")
    private Byte isValidMethod;
}
