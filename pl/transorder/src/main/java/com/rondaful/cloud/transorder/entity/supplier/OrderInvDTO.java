package com.rondaful.cloud.transorder.entity.supplier;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: wujiachuang
 * @Date: 2019/7/11
 * @Description:
 */
public class OrderInvDTO implements Serializable {
    private static final long serialVersionUID = -4624842277053350727L;

    @ApiModelProperty(value = "服务商code")
    private String serviceCode;

    @ApiModelProperty(value = "appKey")
    private String appKey;

    @ApiModelProperty(value = "appToken")
    private String appToken;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "自定义标识")
    private String name;

    @ApiModelProperty(value = "仓库下各sku库存数")
    private List<OrderInvNumberDTO> item;


    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<OrderInvNumberDTO> getItem() {
        return item;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItem(List<OrderInvNumberDTO> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "OrderInvDTO{" +
                "serviceCode='" + serviceCode + '\'' +
                ", appKey='" + appKey + '\'' +
                ", appToken='" + appToken + '\'' +
                ", warehouseName='" + warehouseName + '\'' +
                ", warehouseId=" + warehouseId +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", name='" + name + '\'' +
                ", item=" + item +
                '}';
    }
}
