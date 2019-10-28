package com.brandslink.cloud.logistics.entity.centre;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
@ApiModel(value = "订单项信息")
public class BaseOrderChild {

    @NotBlank(message = "包裹申报名称(英文)必填")
    @ApiModelProperty(value = "包裹申报名称(英文)必填")
    private String enName;

    @NotBlank(message = "包裹申报名称(中文)必填")
    @ApiModelProperty(value = "包裹申报名称(中文)，选填")
    private String cnName;

    @Size(max = 50, message = "产品 SKU字符长度最大为50")
    @NotBlank(message = "产品 SKU必填")
    @ApiModelProperty(value = "产品 SKU")
    private String sku;

    @NotNull(message = "申报数量,必填")
    @ApiModelProperty(value = "申报数量,必填")
    private Integer quantity;

    @NotNull(message = "申报价格单价,单位USD,必填")
    @ApiModelProperty(value = "申报价格单价,单位USD,必填")
    private BigDecimal unitPrice;

    @NotNull(message = "申报重量不能为空")
    @ApiModelProperty(value = "申报重量(单重)，单位kg")
    private BigDecimal unitWeight;

    @Size(max = 11, message = "海关编码字符长度最大为11")
    @ApiModelProperty(value = "海关编码")
    private String hsCode;


}
