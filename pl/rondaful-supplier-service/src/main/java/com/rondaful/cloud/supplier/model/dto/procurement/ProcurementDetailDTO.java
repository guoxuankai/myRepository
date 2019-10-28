package com.rondaful.cloud.supplier.model.dto.procurement;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/7/2
 * @Description:
 */
public class ProcurementDetailDTO implements Serializable {
    private static final long serialVersionUID = -2747898083012972492L;


    @ApiModelProperty(value = "")
    private String id;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "采购仓库")
    private String warehouseName;

    @ApiModelProperty(value = "供货商id")
    private Integer providerId;

    @ApiModelProperty(value = "供货商名称")
    private String providerName;

    @ApiModelProperty(value = "采购人")
    private String buyer;

    @ApiModelProperty(value = "采购单商品明细")
    private List<ProcurementListDeatilDTO> list;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public List<ProcurementListDeatilDTO> getList() {
        return list;
    }

    public void setList(List<ProcurementListDeatilDTO> list) {
        this.list = list;
    }
}
