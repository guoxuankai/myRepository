package com.brandslink.cloud.finance.pojo.dto;

import lombok.Data;

/**
 * @author yangzefei
 * @Classname ProductDto
 * @Description 商品模型
 * @Date 2019/9/4 16:26
 */
@Data
public class ProductDto {
    /**
     * 商品SKU
     */
    private String productSku;
    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品数量
     */
    private Integer productNumber;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 商品长度 mm
     */
    private Double productLength;
    /**
     * 商品宽度 mm
     */
    private Double productWidth;
    /**
     * 商品高度 mm
     */
    private Double productHeight;
    /**
     * 商品体积 mm3
     */
    private Double productVolume;
    /**
     * 商品重量 g
     */
    private Double productWeight;

    /**
     * 包装长度 mm
     */
    private Double packageLength;
    /**
     * 包装宽度 mm
     */
    private Double packageWidth;
    /**
     * 包装高度 mm
     */
    private Double packageHeight;
    /**
     * 包装重量 g
     */
    private Double packageWeight;
}
