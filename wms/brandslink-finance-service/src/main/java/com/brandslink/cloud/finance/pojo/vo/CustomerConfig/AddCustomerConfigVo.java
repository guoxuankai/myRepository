package com.brandslink.cloud.finance.pojo.vo.CustomerConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @Author: zhangjinhua
 * @Date: 2019/8/24 11:47
 */
@ApiModel(value = "AddCustomerConfigOutDto")
@Data
public class AddCustomerConfigVo {

    @ApiModelProperty(value = "用户余额阈值")
    private BigDecimal thresholdMoney;
    @ApiModelProperty(value = "客户Code，关联tf_customer")
    private String customerCode;
    @ApiModelProperty(value = "客户名称")
    private String customerName;
    @ApiModelProperty(value = "版本号", hidden = true)
    private String version;
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
    @ApiModelProperty(value = "创建人", hidden = true )
    private String createBy;

}
