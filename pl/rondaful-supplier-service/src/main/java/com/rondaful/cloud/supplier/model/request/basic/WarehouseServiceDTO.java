package com.rondaful.cloud.supplier.model.request.basic;


import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public class WarehouseServiceDTO implements Serializable {
    private static final long serialVersionUID = 7636924038519222567L;

    private Integer id;
    private String serviceCode;

    private String serviceName;

    private String companyName;

    private Integer supplyId;

    public WarehouseServiceDTO(){}

    public WarehouseServiceDTO(String serviceCode, String serviceName) {
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }
}
