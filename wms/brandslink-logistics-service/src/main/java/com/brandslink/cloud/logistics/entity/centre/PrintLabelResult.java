package com.brandslink.cloud.logistics.entity.centre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author guoxuankai
 * @date 2019/7/31 16:05
 */
@Data
@ApiModel(value = "打印标签返回结果")
public class PrintLabelResult {

    @ApiModelProperty(value = "运单号")
    private String orderNumber;

    @ApiModelProperty(value = "面单信息url")
    private String printUrl;


}