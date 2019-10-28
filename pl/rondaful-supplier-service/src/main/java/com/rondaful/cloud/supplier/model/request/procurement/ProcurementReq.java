package com.rondaful.cloud.supplier.model.request.procurement;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/20
 * @Description:
 */
public class ProcurementReq implements Serializable {
    private static final long serialVersionUID = 5413763534380700424L;

    @ApiModelProperty(value = "采购单id")
    private Long id;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "供货商id")
    private Integer providerId;

    @ApiModelProperty(value = "采购人")
    private String buyer;

    @ApiModelProperty(value = "json字符串")
    private String items;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
