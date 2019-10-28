package com.rondaful.cloud.supplier.model.dto.basics;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/6/11
 * @Description:
 */
public class WarehouseFirmTreeDTO implements Serializable {
    private static final long serialVersionUID = -522359431468666818L;

    private Integer id;

    private String name;

    private String code;

    private String supplierName;

    private Integer supplierId;

    private String country;

    private Integer status;

    private String companyName;

    List<WarehouseFirmTreeDTO> childs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<WarehouseFirmTreeDTO> getChilds() {
        return childs;
    }

    public void setChilds(List<WarehouseFirmTreeDTO> childs) {
        this.childs = childs;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
}
