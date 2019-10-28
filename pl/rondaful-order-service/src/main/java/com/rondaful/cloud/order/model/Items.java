package com.rondaful.cloud.order.model;

/**
 * @author wh
 * @description 谷仓测试新建订单参数对象
 * @date 2019/4/26
 */
public class Items {
    String product_sku;
    int quantity;

    public Items(String product_sku, int quantity) {
        this.product_sku = product_sku;
        this.quantity = quantity;
    }

    public String getProduct_sku() {
        return product_sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProduct_sku(String product_sku) {
        this.product_sku = product_sku;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Items(String product_sku) {
        this.product_sku = product_sku;
    }
}
