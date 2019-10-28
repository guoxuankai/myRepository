package com.rondaful.cloud.supplier.model.dto.storage;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public class StorageQueryPageDTO implements Serializable {
    private static final long serialVersionUID = -4370707540362016725L;

    private String receivingCode;

    private Integer status;

    private List<Integer> warehouseId;

    private List<Integer> supplierId;

    private Integer currentPage;

    private Integer pageSize;

    private String languageType;

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getReceivingCode() {
        return receivingCode;
    }

    public void setReceivingCode(String receivingCode) {
        this.receivingCode = receivingCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(List<Integer> warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<Integer> getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(List<Integer> supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
