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
 * 平台账户
 * 实体类对应的数据表为：  tf_sys_account
 * @author yangzefei
 * @date 2019-08-26 09:34:21
 */
@ApiModel(value ="SysAccount")
@Data
public class SysAccount extends BaseObject {
    @ApiModelProperty(value = "平台账户名称")
    private String nickName;

    @ApiModelProperty(value = "平台总收入(可用+支出)")
    private BigDecimal incomeMoney;

    @ApiModelProperty(value = "支出")
    private BigDecimal expendMoney;

    @ApiModelProperty(value = "可用余额")
    private BigDecimal usableMoney;

}