package com.rondaful.cloud.order.model.vo.sysorder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Blade
 * @date 2019-07-26 19:58:00
 **/
public class CalculateLogisticsSupplierVO implements Serializable {
    private static final long serialVersionUID = 1113327393487888480L;

    /**
     * 供应商ID
     */
    private Long supplierId = -1L;

    /**
     * 物流费
     */
    private BigDecimal logisticsFee;

    /**
     * sku 列表
     */
    private List<CalculateLogisticsSkuVO> skuList;

    public List<CalculateLogisticsSkuVO> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<CalculateLogisticsSkuVO> skuList) {
        this.skuList = skuList;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getLogisticsFee() {
        return logisticsFee;
    }

    public void setLogisticsFee(BigDecimal logisticsFee) {
        this.logisticsFee = logisticsFee;
    }
}
