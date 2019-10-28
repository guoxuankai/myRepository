package com.brandslink.cloud.finance.pojo.feature;

import com.brandslink.cloud.finance.pojo.base.BaseFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yangzefei
 * @Classname LogisticsCostFeature
 * @Description 物流费特性
 * @Date 2019/8/29 9:34
 */
@Data
@ApiModel(value = "物流费特性")
public class LogisticsCostFeature extends BaseFeature {

    @ApiModelProperty(value = "物流跟踪单号")
    private String trackNo;
    @ApiModelProperty(value = "发货平台")
    private String platform;
    @ApiModelProperty(value = "物流名称")
    private String logName;
    @ApiModelProperty(value = "物流运单")
    private String logNo;
    @ApiModelProperty(value = "邮寄方式")
    private String postType;
    @ApiModelProperty(value = "目的国家")
    private String country;
    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "实际重量")
    private BigDecimal actualWeight;
    @ApiModelProperty(value = "计费重量")
    private BigDecimal chargedWeight;
    @ApiModelProperty(value = "物流商实重")
    private BigDecimal logActualWeight;
    @ApiModelProperty(value = "物流商计重")
    private BigDecimal logChargedWeight;

    @ApiModelProperty(value = "仓库运费")
    private BigDecimal freightCost;
    @ApiModelProperty(value = "物流商运费")
    private BigDecimal logFreightCost;
    @ApiModelProperty(value = "冻结金额")
    private BigDecimal freezeMoney;

}
