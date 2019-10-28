package com.brandslink.cloud.logistics.thirdLogistics.bean.SunYou;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("SunYouProduct")
public class SunYouProduct implements Serializable {

    @Size(max = 50, message = "产品 SKU字符长度最大为50")
    @NotBlank(message = "产品 SKU必填")
    @ApiModelProperty(value = "产品 SKU")
    private String productSku;

    @Size(max = 100, message = "申报英文名称字符长度最大为100")
    @NotBlank(message = "申报英文名称必填")
    @ApiModelProperty(value = "申报英文名称")
    private String declareEnName;

    @Size(max = 100, message = "申报中文名称字符长度最大为100")
    @ApiModelProperty(value = "申报中文名称")
    private String declareCnName;

    @NotNull(message = "产品数量必填")
    @ApiModelProperty(value = "产品数量")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "海关申报单价（币种：USD），数值必须大 于 0")
    @NotNull(message = "海关申报单价（币种：USD）必填")
    @Digits(integer = 18, fraction = 2, message = "海关申报单价（币种：USD）限制为18位整数2位小数")
    @ApiModelProperty(value = "海关申报单价（币种：USD），数值必须大 于 0 最终申报总价值为Σ(单价*数量)")
    private BigDecimal declarePrice;

    @Size(max = 11, message = "海关编码字符长度最大为11")
    @ApiModelProperty(value = "海关编码")
    private String hsCode;

    @Size(max = 50, message = "产品材质字符长度最大为50")
    @ApiModelProperty(value = "产品材质")
    private String productMaterial;

    @Size(max = 50, message = "产品用途字符长度最大为50")
    @ApiModelProperty(value = "产品用途")
    private String productPurpose;

    /**
     * 自定义字段，用于计算[包裹总重量（单位：kg）]
     */
    @NotNull(message = "申报重量(单重)必填")
    @ApiModelProperty(value = "申报重量(单重)，单位kg")
    private BigDecimal unitWeight;
}
