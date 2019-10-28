package com.rondaful.cloud.commodity.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

/**
* @Description:sku仓库价格等信息
* @author:范津 
* @date:2019年10月8日 下午2:16:46
 */
public class SkuWarehouseInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;

	@ApiModelProperty(value = "系统sku")
    private String systemSku;

	@ApiModelProperty(value = "仓库ID")
    private Long warehouseId;

	@ApiModelProperty(value = "备货天数")
    private Integer stockDay;

	@ApiModelProperty(value = "附加费用,USD")
    private BigDecimal additionalCost;

	@ApiModelProperty(value = "仓库价格,USD")
    private BigDecimal warehousePriceUs;

	@ApiModelProperty(value = "仓库价格,RMB")
    private BigDecimal warehousePrice;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSystemSku() {
        return systemSku;
    }

    public void setSystemSku(String systemSku) {
        this.systemSku = systemSku == null ? null : systemSku.trim();
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getStockDay() {
        return stockDay;
    }

    public void setStockDay(Integer stockDay) {
        this.stockDay = stockDay;
    }

    public BigDecimal getAdditionalCost() {
        return additionalCost;
    }

    public void setAdditionalCost(BigDecimal additionalCost) {
        this.additionalCost = additionalCost;
    }

    public BigDecimal getWarehousePriceUs() {
        return warehousePriceUs;
    }

    public void setWarehousePriceUs(BigDecimal warehousePriceUs) {
        this.warehousePriceUs = warehousePriceUs;
    }

    public BigDecimal getWarehousePrice() {
        return warehousePrice;
    }

    public void setWarehousePrice(BigDecimal warehousePrice) {
        this.warehousePrice = warehousePrice;
    }
}