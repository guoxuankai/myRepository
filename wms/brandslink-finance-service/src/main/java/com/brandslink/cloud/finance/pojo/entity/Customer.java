package com.brandslink.cloud.finance.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.brandslink.cloud.finance.pojo.base.BaseObject;
import com.brandslink.cloud.finance.pojo.base.DomainObject;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 客户信息表
 * 实体类对应的数据表为：  tf_customer
 * @author yangzefei
 * @date 2019-08-29 17:34:41
 */
@Data
@ApiModel(value ="Customer")
public class Customer extends DomainObject {
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