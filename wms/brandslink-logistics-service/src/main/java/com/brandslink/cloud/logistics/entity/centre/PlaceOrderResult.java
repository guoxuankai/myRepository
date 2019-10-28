package com.brandslink.cloud.logistics.entity.centre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author guoxuankai
 * @date 2019/7/29 15:43
 */
@Data
@ApiModel(value = "下单返回结果")
public class PlaceOrderResult {


    @ApiModelProperty(value = "运单号")
    private String wayBillNumber;

    @ApiModelProperty(value = "面单信息url")
    private String faceSheetUrl;


}
