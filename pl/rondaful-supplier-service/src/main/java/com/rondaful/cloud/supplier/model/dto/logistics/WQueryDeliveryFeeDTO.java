package com.rondaful.cloud.supplier.model.dto.logistics;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/10/18
 * @Description:
 */
public class WQueryDeliveryFeeDTO implements Serializable {
    private static final long serialVersionUID = 2276559291396798792L;

    private String warehouse;
    private String country;
    private String city;
    private String method;
    private String platform;
    private Integer searchType;
    private Double length;
    private Double wide;
    private Double height;
    private Double weight;
    private List<Map<String,Object>> skuQuantityList;

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWide() {
        return wide;
    }

    public void setWide(Double wide) {
        this.wide = wide;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<Map<String, Object>> getSkuQuantityList() {
        return skuQuantityList;
    }

    public void setSkuQuantityList(List<Map<String, Object>> skuQuantityList) {
        this.skuQuantityList = skuQuantityList;
    }
}

