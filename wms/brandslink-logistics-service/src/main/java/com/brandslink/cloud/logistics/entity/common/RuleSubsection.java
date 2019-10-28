package com.brandslink.cloud.logistics.entity.common;

import com.brandslink.cloud.logistics.model.CountryRemoteFeeModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "RuleSubsection")
public class RuleSubsection implements Serializable {

    @Min(value = 1, message = "分段序号最小值为1")
    @NotNull(message = "分段序号不能为空")
    @ApiModelProperty(value = "分段序号")
    private Integer sectionNumber;

    @Min(value = 0, message = "分段开始重量（g）最小值为0")
    @NotNull(message = "分段开始重量（g）不能为空")
    @ApiModelProperty(value = "分段开始重量（g）")
    private Integer beginWeight;

    @Min(value = 0, message = "分段结束重量（g）最小值为0")
    @NotNull(message = "分段结束重量（g）不能为空")
    @ApiModelProperty(value = "分段结束重量（g）")
    private Integer endWeight;

    @NotNull(message = "分段计费类型不能为空")
    @ApiModelProperty(value = "分段计费类型（0：分段内按单位重量收费，1：分段内按首重+续重收费，2：分段内固定收费）")
    private Byte segmentFeeType;

    @ApiModelProperty(value = "每%克%元")
    private YuanPerGram yuanPerGram;

    @ApiModelProperty(value = "按首重+续重计费数据对象")
    private CountryRemoteFeeModel remoteFee;

    @Digits(integer = 18, fraction = 3, message = "固定费用限制为18位整数3位小数")
    @ApiModelProperty(value = "固定费用")
    private BigDecimal fixedCharge;

    @NotNull(message = "运费折扣类型不能为空")
    @ApiModelProperty(value = "运费折扣类型（0：无折扣，1：全部折扣，2：部分折扣）")
    private Byte freightRebateType;

    @DecimalMax(value = "1.000", message = "运费折扣最大为1")
    @NotNull(message = "运费折扣不能为空")
    @Digits(integer = 5, fraction = 3, message = "运费折扣限制为1位整数3位小数，最大值为1")
    @ApiModelProperty(value = "运费折扣")
    private BigDecimal freightRebate;

    @Digits(integer = 18, fraction = 3, message = "处理费限制为18位整数3位小数")
    @ApiModelProperty(value = "处理费")
    private BigDecimal handlingCharge;
}