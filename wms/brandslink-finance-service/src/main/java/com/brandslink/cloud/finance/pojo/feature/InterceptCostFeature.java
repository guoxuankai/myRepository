package com.brandslink.cloud.finance.pojo.feature;

import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname InterceptCostFeature
 * @Description 订单拦截费特性
 * @Date 2019/8/29 9:26
 */
@Data
@ApiModel(value = "订单拦截费特性")
public class InterceptCostFeature extends BaseFeature {
    @ApiModelProperty(value = "平台订单号")
    private String platformOrderNo;
    @ApiModelProperty(value = "拦截费类型")
    private Integer interceptType;
    @ApiModelProperty(value = "拦截费")
    private BigDecimal interceptCost;

    public InterceptCostFeature(String platformOrderNo,Integer interceptType,BigDecimal interceptCost){
        this.platformOrderNo=platformOrderNo;
        this.interceptType=interceptType;
        this.interceptCost=interceptCost;
    }
}
