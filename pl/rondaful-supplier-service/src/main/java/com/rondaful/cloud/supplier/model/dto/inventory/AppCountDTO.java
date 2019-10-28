package com.rondaful.cloud.supplier.model.dto.inventory;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/9/11
 * @Description:
 */
public class AppCountDTO implements Serializable {
    private static final long serialVersionUID = -3511388007902288368L;

    @ApiModelProperty(value = "品连sku")
    private Integer skuCount;

    @ApiModelProperty(value = "有库存数")
    private Integer availableCount;

    @ApiModelProperty(value = "低于预警值数")
    private Integer warnCount;

    public AppCountDTO(Integer skuCount, Integer availableCount, Integer warnCount) {
        this.skuCount = skuCount;
        this.availableCount = availableCount;
        this.warnCount = warnCount;
    }

    public AppCountDTO(){}

    public Integer getSkuCount() {
        return skuCount;
    }

    public void setSkuCount(Integer skuCount) {
        this.skuCount = skuCount;
    }

    public Integer getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }

    public Integer getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(Integer warnCount) {
        this.warnCount = warnCount;
    }
}
