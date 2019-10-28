package com.rondaful.cloud.user.controller.model.provider;

import java.io.Serializable;

public class GetSupplyChinByUserIdOrUsername implements Serializable {
    private static final long serialVersionUID = -4506084534664173126L;

    private Integer supplyId;

    private String supplyUsername;

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }

    public String getSupplyUsername() {
        return supplyUsername;
    }

    public void setSupplyUsername(String supplyUsername) {
        this.supplyUsername = supplyUsername;
    }
}
