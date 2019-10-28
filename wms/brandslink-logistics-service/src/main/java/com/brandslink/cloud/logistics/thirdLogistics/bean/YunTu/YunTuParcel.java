package com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "YunTuParcel")
public class YunTuParcel implements Serializable {

    @NotBlank(message = "包裹申报名称(英文)必填")
    @Size(max = 200, message = "包裹申报名称，字符个数必须小于200")
    @JsonProperty("eName")
    @ApiModelProperty(value = "包裹申报名称(英文)必填")
    private String EName;

    @NotBlank(message = "包裹申报名称(中文)必填")
    @Size(max = 500, message = "包裹申报名称(中文)，字符个数必须小于500")
    @JsonProperty("cName")
    @ApiModelProperty(value = "包裹申报名称(中文)")
    private String CName;

    @Size(max = 50, message = "海关编码，字符个数必须小于50")
    @JsonProperty("hSCode")
    @ApiModelProperty(value = "海关编码")
    private String HSCode;

    @NotNull(message = "申报数量,必填")
    @JsonProperty("quantity")
    @ApiModelProperty(value = "申报数量,必填")
    private Integer Quantity;

//    @Digits(integer = 18, fraction = 2, message = "申报价格价限制为18位整数2位小数")
    @NotNull(message = "申报价格价,单位USD,必填")
    @JsonProperty("unitPrice")
    @ApiModelProperty(value = "申报价格价,单位USD,必填")
    private BigDecimal UnitPrice;

    @Digits(integer = 18, fraction = 3, message = "申报重量(单重)限制为18位整数3位小数")
    @NotNull(message = "申报重量(单重)，单位kg")
    @JsonProperty("unitWeight")
    @ApiModelProperty(value = "申报重量(单重)，单位kg")
    private BigDecimal UnitWeight;

    @Size(max = 500, message = "订单备注，字符个数必须小于500")
    @JsonProperty("remark")
    @ApiModelProperty(value = "订单备注，用于打印配货单")
    private String Remark;

    @Size(max = 200, message = "产品销售链接地址，字符个数必须小于200")
    @JsonProperty("productUrl")
    @ApiModelProperty(value = "产品销售链接地址")
    private String ProductUrl;

    @Size(max = 50, message = "商品SKU，字符个数必须小于50")
    @JsonProperty("sKU")
    @ApiModelProperty(value = "用于填写商品SKU，FBA订单必填")
    private String SKU;

    @Size(max = 50, message = "配货信息，字符个数必须小于50")
    @JsonProperty("invoiceRemark")
    @ApiModelProperty(value = "配货信息")
    private String InvoiceRemark;

    @NotBlank(message = "申报币种不能为空")
    @Size(max = 50, message = "申报币种，字符个数必须小于50")
    @JsonProperty("currencyCode")
    @ApiModelProperty(value = "申报币种，默认：USD")
    private String CurrencyCode;

    /**
     * 以下为查询运单返回字段
     */

    @JsonProperty("sku")
    @ApiModelProperty(value = "包裹中货品 商品SKU")
    private String Sku;

    @JsonProperty(value = "classCode")
    @ApiModelProperty(value = "品类编码")
    private String ClassCode;

    @JsonProperty(value = "brand")
    @ApiModelProperty(value = "品牌")
    private String Brand;

    @JsonProperty(value = "modelType")
    @ApiModelProperty(value = "型号规格")
    private String ModelType;

    @JsonProperty(value = "unit")
    @ApiModelProperty(value = "单位")
    private String Unit;
}
