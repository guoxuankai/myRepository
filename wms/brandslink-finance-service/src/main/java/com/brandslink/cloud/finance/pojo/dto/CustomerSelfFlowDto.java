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
 * @Classname CustomerSelfFlowDto
 * @Description 客户系统列表展示模型
 * @Date 2019/8/28 10:36
 */
@Data
@ExcelTarget("customerSelfFlowDto")
public class CustomerSelfFlowDto {

    @ApiModelProperty(value = "主键标识")
    private Integer id;
    /**
     * 序号(该字段供前端展示)
     */
    @Excel(name="序号")
    @ApiModelProperty(value = "序号")
    private Integer serialNo;

    @Excel(name="流水单号",orderNum = "2")
    @ApiModelProperty(value = "流水单号")
    private String orderNo;

    @Excel(name="费用类型",replace = {"存储费_1","入库费_2","销退费_3","出库费_4","订单拦截费_5","物流费_6","充值费_7"},orderNum = "3")
    @ApiModelProperty(value = "费用类型 1:存储费,2:入库费,3:销退费,4:出库费,5:订单拦截费,6:物流费,7:充值费")
    private Integer costType;

    @Excel(name="仓库名称",orderNum = "4")
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @Excel(name="计费日期",orderNum = "5")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计费日期")
    private String billTime;

    @Excel(name="来源单号",orderNum = "6")
    @ApiModelProperty(value = "来源单号")
    private String sourceNo;

    @Excel(name="件数",orderNum = "7")
    @ApiModelProperty(value = "件数")
    private Integer number;

    @Excel(name="变动金额",orderNum = "10")
    @ApiModelProperty(value = "变动金额")
    private BigDecimal discountCost;

    @Excel(name="收支类型",replace ={"支出_1","收入_2"} ,orderNum = "11")
    @ApiModelProperty(value = "收支类型。1:支出,2:收入")
    private Integer orderType;

    @Excel(name="创建时间",orderNum = "12")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
