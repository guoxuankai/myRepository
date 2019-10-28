package com.brandslink.cloud.logistics.entity.centre;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "LogisticsFreightCallBack")
public class LogisticsFreightCallBack implements Serializable {

//    @ApiModelProperty(value = "顺序号")
//    private Long id;

//    @ApiModelProperty(value = "邮寄方式ID")
//    private Long methodId;

    @ApiModelProperty(value = "物流商简称")
    private String logisticsShortened;

    @ApiModelProperty(value = "物流商编码")
    private String logisticsCode;

    @ApiModelProperty(value = "邮寄方式名称")
    private String methodName;

    @ApiModelProperty(value = "邮寄方式编码")
    private String methodCode;

    @ApiModelProperty(value = "可发货平台[{'code': '平台编码', 'name': '平台名称', 'type': '是否有效（1是2否）'}],JSONArray类型")
    private JSONArray supportPlatform;

//    @ApiModelProperty(value = "国家ID")
//    private Long countryId;

    @ApiModelProperty(value = "国家编码")
    private String country;

//    @ApiModelProperty(value = "是否收取偏远费")
//    private Byte isRemoteFee;

    @ApiModelProperty(value = "不可发货物")
    private JSONArray unsupportCargo;

    @ApiModelProperty(value = "承诺到达天数")
    private JSONObject promiseDays;

    @ApiModelProperty(value = "重量范围")
    private JSONObject weightRange;

    @ApiModelProperty(value = "长度限制")
    private JSONObject limitLength;

//    @ApiModelProperty(value = "计抛否")
//    private Byte isCountBulk;
//
//    @ApiModelProperty(value = "计抛规则")
//    private JSONObject countBulkRule;
//
//    @ApiModelProperty(value = "计费方式（1：首重+续重，0：分段收费）")
//    private Byte chargeMode;
//
//    @ApiModelProperty(value = "首重+续重计费规则（JSON）")
//    private JSONObject ruleFirstRenew;
//
//    @ApiModelProperty(value = "分段计费规则（JSON）")
//    private JSONArray ruleSubsection;
//
//    @ApiModelProperty(value = "附加费率")
//    private BigDecimal afterCharge;

    @ApiModelProperty(value = "折扣")
    private BigDecimal discount;

    @ApiModelProperty(value = "最快天数")
    private Integer minDay;

    @ApiModelProperty(value = "最慢天数")
    private Integer maxDay;

    @ApiModelProperty(value = "最小限重")
    private Integer minWeight;

    @ApiModelProperty(value = "最大限重")
    private Integer maxWeight;

    @ApiModelProperty(value = "打折前费用")
    private BigDecimal prediscountFee;

    @ApiModelProperty(value = "打折后费用")
    private BigDecimal discountedCharge;

    @ApiModelProperty(value = "处理费")
    private BigDecimal handlingCharge;

    @ApiModelProperty(value = "总运费(物流费用)")
    private BigDecimal totalFee;

    @ApiModelProperty(value = "附加费")
    private BigDecimal afterChargeFee;

    @ApiModelProperty(value = "偏远费")
    private BigDecimal remoteFee;

    @ApiModelProperty(value = "费用总和")
    private BigDecimal allFee;
}
