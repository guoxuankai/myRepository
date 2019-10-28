package com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "OrderInvoice")
public class OrderInvoice implements Serializable {

    @ApiModelProperty(value = "海关编码")
    private String hs_code;

    @NotBlank
    @ApiModelProperty(value = "申报总价值，必填")
    private String invoice_amount;

    @NotBlank
    @ApiModelProperty(value = "件数，必填")
    private String invoice_pcs;

    @NotBlank
    @ApiModelProperty(value = "英文品名，必填")
    private String invoice_title;

    @ApiModelProperty(value = "单件重（kg）")
    private String invoice_weight;

    @ApiModelProperty(value = "商品ID")
    private String item_id;

    @ApiModelProperty(value = "商品交易ID")
    private String item_transactionid;

    @ApiModelProperty(value = "中文品名")
    private String sku;

    @ApiModelProperty(value = "配货信息")
    private String sku_code;

    /**
     * 自定义字段，用于计算[申报总价值]
     */
    @NotNull(message = "申报价格单价,单位USD,必填")
    @ApiModelProperty(value = "申报价格单价,单位USD,必填")
    private BigDecimal unitPrice;
}
