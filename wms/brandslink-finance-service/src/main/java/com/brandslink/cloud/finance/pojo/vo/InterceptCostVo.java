package com.brandslink.cloud.finance.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname InterceptCostVo
 * @Description 拦截费计算模型
 * @Date 2019/9/6 9:56
 */
@Data
public class InterceptCostVo extends BaseCostVo {

    @ApiModelProperty(value = "客户编码")
    private String customerCode;

    @ApiModelProperty(value = "拦截类型 50:B2C未拣货-拦截,51:B2C未复核-拦截,52:B2C未集包-拦截,53:B2C已集包-拦截")
    private Integer interceptType;

    @ApiModelProperty(value = "平台订单号")
    private String platformOrderNo;
}
