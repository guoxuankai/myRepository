package com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("YunTuShipperBox")
public class YunTuShipperBox implements Serializable {

    @JsonProperty(value = "boxNumber")
    @ApiModelProperty(value = "箱子号码")
    private String BoxNumber;

    @JsonProperty(value = "shipperHawbcode")
    @ApiModelProperty(value = "物流运单子单号")
    private String ShipperHawbcode;
}
