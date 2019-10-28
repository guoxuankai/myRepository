package com.brandslink.cloud.finance.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangzefei
 * @Classname StockCostDetailVo
 * @Description 入库、销退、出库参数模型
 * @Date 2019/9/4 10:20
 */
@Data
public class StockCostDetailVo {
    @ApiModelProperty(value = "sku")
    private String sku;
    @ApiModelProperty(value = "商品数量")
    private Integer skuNumber;
}
