package com.rondaful.cloud.order.entity.goodcang.GoodCangSubscibe;

import lombok.Data;

import java.util.List;

@Data
public class GoodCangAcceptItem {

    private String product_barcode	;//string(32)	Require	商品编码	必填,唯一
    private String product_sku	    ;//string	Require	客户商品编码
    private Integer qty	            ;//Int	Require	商品数量
    private List<GoodCangAccepSnItem> snItem	        ;//Object	Option	S/N码明细
}
