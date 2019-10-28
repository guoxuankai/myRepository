package com.rondaful.cloud.supplier.model.dto.logistics;


import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/10/21
 * @Description:
 */
public class LogisticsPublishDTO extends LogisticsSelectDTO {
    private static final long serialVersionUID = 8105968598339129168L;

    /**
     * sku与物流费的对应关系
     */
    Map<String,BigDecimal> skuCost;

    public Map<String, BigDecimal> getSkuCost() {
        return skuCost;
    }

    public void setSkuCost(Map<String, BigDecimal> skuCost) {
        this.skuCost = skuCost;
    }
}

