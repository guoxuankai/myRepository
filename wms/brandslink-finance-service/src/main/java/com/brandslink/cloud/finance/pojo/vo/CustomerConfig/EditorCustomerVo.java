package com.brandslink.cloud.finance.pojo.vo.CustomerConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: zhangjinhua
 * @Date: 2019/8/28 11:23
 */
@Data
@ApiModel(value = "EditorCustomerVo")
public class EditorCustomerVo {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "存储费(百分比)")
    private Double storageFee;

    @ApiModelProperty(value = "卸货费(百分比)")
    private Double uploadFee;

    @ApiModelProperty(value = "入库费(百分比)")
    private Double instockFee;

    @ApiModelProperty(value = "出库费")
    private Double outstockFee;

    @ApiModelProperty(value = "盘点费")
    private Double checkFee;

    @ApiModelProperty(value = "增值费")
    private Double incrementFee;
    @ApiModelProperty(value = "物流费")
    private Double logisticsFee;

    @ApiModelProperty(value = "用户余额阈值")
    private BigDecimal thresholdMoney;
}
