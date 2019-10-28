package com.brandslink.cloud.finance.pojo.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname CustomerFlowDto
 * @Description 运营平台客户流水展示模型
 * @Date 2019/8/28 10:36
 */
@Data
@ExcelTarget("customerFlowDto")
public class CustomerFlowDto extends CustomerSelfFlowDto{

    @Excel(name="客户",orderNum = "1")
    @ApiModelProperty(value = "客户")
    private String customerName;

    @Excel(name="应扣金额",orderNum = "8")
    @ApiModelProperty(value = "应扣金额")
    private BigDecimal originalCost;

    @Excel(name="折扣",orderNum = "9")
    @ApiModelProperty(value = "折扣")
    private Double discount;

    @ApiModelProperty(value = "费用特性",dataType = "com.brandslink.cloud.finance.pojo.base.BaseFeature")
    private String feature;
}
