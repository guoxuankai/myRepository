package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value ="GoodCangOrderItem")
public class GoodCangOrderItem {

    @ApiModelProperty(value = "SKU", required = true)
    private String product_sku;//string      Require      SKU
    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;//int      Require      数量
    @ApiModelProperty(value = "订单交易号")
    private String transaction_id;//string      Option      ebay订单交易号
    @ApiModelProperty(value = "订单商品编码")
    private String item_id;//string      Option      ebay订单商品编码
    @ApiModelProperty(value = "FBA商品编码FBA 类型订单必填")
    private String fba_product_code;//string      Option      FBA商品编码FBA 类型订单必填
}
