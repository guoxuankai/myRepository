package com.brandslink.cloud.finance.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.brandslink.cloud.finance.pojo.base.BaseObject;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * 平台账号流水
 * 实体类对应的数据表为：  tf_sys_account_flow
 * @author yangzefei
 * @date 2019-08-26 09:34:21
 */
@Data
@ApiModel(value ="SysAccountFlow")
public class SysAccountFlow extends BaseObject {

    @ApiModelProperty(value = "平台账户ID")
    private Integer sysAccountId;

    @ApiModelProperty(value = "流水号")
    private String orderNo;

    @ApiModelProperty(value = "来源单号")
    private String sourceNo;

    @ApiModelProperty(value = "费用类型 6:物流费,7:充值费")
    private Integer costType;

    @ApiModelProperty(value = "变动前余额")
    private BigDecimal beforeMoney;

    @ApiModelProperty(value = "账单金额")
    private BigDecimal billMoney;

    @ApiModelProperty(value = "变动后余额")
    private BigDecimal afterMoney;

    @ApiModelProperty(value = "收支类型。1:支出,2:收入")
    private Integer orderType;


}