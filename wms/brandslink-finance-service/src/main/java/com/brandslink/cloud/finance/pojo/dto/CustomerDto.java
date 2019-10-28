package com.brandslink.cloud.finance.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname CustomerDto
 * @Description 客户列表模型
 * @Date 2019/8/30 10:00
 */
@Data
public class CustomerDto {
    @ApiModelProperty(value = "序号")
    private Integer serialNo=0;

    @ApiModelProperty(value = "主键标识")
    private Integer id;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "总收入")
    private BigDecimal totalIncome;

    @ApiModelProperty(value = "总充值")
    private BigDecimal totalRecharge;

    @ApiModelProperty(value = "总支出")
    private BigDecimal totalExpend;

    @ApiModelProperty(value = "账号余额(可用+冻结)")
    private BigDecimal balanceMoney;

    @ApiModelProperty(value = "可用余额")
    private BigDecimal usableMoney;

    @ApiModelProperty(value = "冻结金额")
    private BigDecimal freezeMoney;

}
