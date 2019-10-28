package com.brandslink.cloud.finance.pojo.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yangzefei
 * @Classname SysAccountFlowDto
 * @Description TODO
 * @Date 2019/9/3 14:19
 */
@Data
@ExcelTarget("sysAccountFlow")
public class SysAccountFlowDto {
    @ApiModelProperty(value = "主键标识")
    private Integer id;
    /**
     * 序号(该字段供前端展示)
     */
    @Excel(name="序号")
    @ApiModelProperty(value = "序号")
    private Integer serialNo;

    @Excel(name="单号")
    @ApiModelProperty(value = "单号")
    private String orderNo;

    @Excel(name="来源单号")
    @ApiModelProperty(value = "来源单号")
    private String sourceNo;

    @Excel(name="账单金额")
    @ApiModelProperty(value = "账单金额")
    private BigDecimal billMoney;

    @Excel(name="平台余额")
    @ApiModelProperty(value = "平台余额")
    private BigDecimal afterMoney;

    @Excel(name="费用类型",replace = {"物流费_6","充值费_7"})
    @ApiModelProperty(value = "费用类型 6:物流费,7:充值费")
    private Integer costType;

    @Excel(name="收支类型",replace = {"支出_1","收入_2"})
    @ApiModelProperty(value = "收支类型。1:支出,2:收入")
    private Integer orderType;

    @Excel(name="创建时间",databaseFormat = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
