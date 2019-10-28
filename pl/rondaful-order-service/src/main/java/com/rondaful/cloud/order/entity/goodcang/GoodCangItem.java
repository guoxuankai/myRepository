package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class GoodCangItem {

    @ApiModelProperty(value = "商品编码")
    private String productBarcode;

    @ApiModelProperty(value = "客户商品编码")
    private String productSku;

    @ApiModelProperty(value = "商品数量")
    private Integer qty;

    @ApiModelProperty(value = "S/N码明细")
    private List<GoodCangSnItem> snItem;

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public List<GoodCangSnItem> getSnItem() {
        return snItem;
    }

    public void setSnItem(List<GoodCangSnItem> snItem) {
        this.snItem = snItem;
    }
}
