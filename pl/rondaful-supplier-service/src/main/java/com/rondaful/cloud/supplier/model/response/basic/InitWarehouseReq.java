package com.rondaful.cloud.supplier.model.response.basic;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: xqq
 * @Date: 2019/10/12
 * @Description:
 */
public class InitWarehouseReq implements Serializable {
    private static final long serialVersionUID = 4626134393691177336L;
    private String appKey;

    private Integer id;

    private String appToken;

    private String name;

    private String code;

    private Integer supplyId;

    private String countryCode;


    private Map<String,Integer> items;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }
}
