package com.rondaful.cloud.order.model.vo.sysorder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Blade
 * @date 2019-07-26 19:36:27
 **/
public class CalculateLogisticsSkuVO implements Serializable {
    private static final long serialVersionUID = -1358046808932371290L;

    /**
     * 品连sku
     */
    private String sku;

    // 计算之后 每个sku的单个预估物流费
    /**
     * 计算之后，每个sku的单个费用
     * 如果是 卖家 或 供应商， 则是单个预估物流费
     * 如果是 物流商  则是 单个实际物流费
     */
    private BigDecimal skuPerCost;

    public CalculateLogisticsSkuVO() {
        this.skuPerCost = new BigDecimal("0");
    }

    /**
     * sku数量
     */
    private Integer skuNumber;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getSkuPerCost() {
        return skuPerCost;
    }

    public void setSkuPerCost(BigDecimal skuPerCost) {
        this.skuPerCost = skuPerCost;
    }

    public Integer getSkuNumber() {
        return skuNumber;
    }

    public void setSkuNumber(Integer skuNumber) {
        this.skuNumber = skuNumber;
    }
}
