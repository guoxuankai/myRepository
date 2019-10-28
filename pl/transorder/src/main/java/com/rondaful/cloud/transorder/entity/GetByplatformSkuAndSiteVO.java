package com.rondaful.cloud.transorder.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author Blade
 * @date 2019-07-29 18:20:45
 **/
public class GetByplatformSkuAndSiteVO implements Serializable {
    private static final long serialVersionUID = -2958854132450000024L;

    @ApiModelProperty(value = "发货仓库")
    private Integer warehouseId;

    @ApiModelProperty(value = "1价格最低  2综合排序  3物流速度")
    private Integer logisticsType;

    @ApiModelProperty(value = "物流方式code")
    private String logisticsCode;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getLogisticsType() {
        return logisticsType;
    }

    public void setLogisticsType(Integer logisticsType) {
        this.logisticsType = logisticsType;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }
}