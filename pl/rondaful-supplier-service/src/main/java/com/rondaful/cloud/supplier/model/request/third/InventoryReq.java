package com.rondaful.cloud.supplier.model.request.third;

import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/7/29
 * @Description:
 */
public class InventoryReq {
    private static final long serialVersionUID = -3210507057113815332L;

    private Integer page;

    private Integer pageSize;

    private String pinlianSku;

    private String warehouseCode;

    private List<String> pinlianSkus;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getPinlianSku() {
        return pinlianSku;
    }

    public void setPinlianSku(String pinlianSku) {
        this.pinlianSku = pinlianSku;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public List<String> getPinlianSkus() {
        return pinlianSkus;
    }

    public void setPinlianSkus(List<String> pinlianSkus) {
        this.pinlianSkus = pinlianSkus;
    }

}
