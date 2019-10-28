package com.rondaful.cloud.supplier.model.dto.logistics;

import java.util.List;

public class WmsLogisticsDTO {

    //物流商编码
    private String providerCode;

    private List supportPlatform;

    private List warehouseList;

    private String providerId;

    private String isValid;
    //邮寄方式名称
    private String methodCnName;

    private String id;
    //邮寄方式编码
    private String methodCode;
    //物流商简称
    private String providerShortened;

    private String methodEnName;

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public List getSupportPlatform() {
        return supportPlatform;
    }

    public void setSupportPlatform(List supportPlatform) {
        this.supportPlatform = supportPlatform;
    }

    public List getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List warehouseList) {
        this.warehouseList = warehouseList;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getMethodCnName() {
        return methodCnName;
    }

    public void setMethodCnName(String methodCnName) {
        this.methodCnName = methodCnName;
    }

    public String getMethodEnName() {
        return methodEnName;
    }

    public void setMethodEnName(String methodEnName) {
        this.methodEnName = methodEnName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getProviderShortened() {
        return providerShortened;
    }

    public void setProviderShortened(String providerShortened) {
        this.providerShortened = providerShortened;
    }

    @Override
    public String toString() {
        return "WmsLogisticsDTO{" +
                "providerCode='" + providerCode + '\'' +
                ", supportPlatform=" + supportPlatform +
                ", warehouseList=" + warehouseList +
                ", providerId='" + providerId + '\'' +
                ", isValid='" + isValid + '\'' +
                ", methodCnName='" + methodCnName + '\'' +
                ", id='" + id + '\'' +
                ", methodCode='" + methodCode + '\'' +
                ", providerShortened='" + providerShortened + '\'' +
                ", methodEnName='" + methodEnName + '\'' +
                '}';
    }
}
