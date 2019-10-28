package com.brandslink.cloud.finance.pojo.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.brandslink.cloud.finance.pojo.base.BaseObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 客户资金流水
 * 实体类对应的数据表为：  tf_customer_flow
 * @author yangzefei
 * @date 2019-08-26 09:51:15
 */
@Data
@ApiModel(value ="CustomerFlow")
public class CustomerFlow extends BaseObject {

    @ApiModelProperty(value = "流水单号")
    private String orderNo;

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "费用类型 1:存储费,2:入库费,3:销退费,4:出库费,5:订单拦截费,6:物流费,7:充值费")
    private Integer costType;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "来源单号")
    private String sourceNo;

    @ApiModelProperty(value = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "件数")
    private Integer number;

    @ApiModelProperty(value = "变动前余额")
    private BigDecimal beforeMoney;

    @ApiModelProperty(value = "应扣金额")
    private BigDecimal originalCost;

    @ApiModelProperty(value = "折扣")
    private Double discount;

    @ApiModelProperty(value = "变动金额")
    private BigDecimal discountCost;

    @ApiModelProperty(value = "变动后余额")
    private BigDecimal afterMoney;

    @ApiModelProperty(value = "可用余额")
    private BigDecimal usableMoney;

    @ApiModelProperty(value = "冻结金额")
    private BigDecimal freezeMoney;

    @ApiModelProperty(value = "是否展示 1:展示,2:不展示")
    private Integer isShow;

    @ApiModelProperty(value = "收支类型。1:支出,2:收入")
    private Integer orderType;

    @ApiModelProperty(value = "存储不同费用类型的特性字段(json格式)",hidden = true)
    private String featureJson;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "计费日期")
    private Date billTime;

}