package com.rondaful.cloud.supplier.model.dto.basics;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/7/10
 * @Description:
 */
public class InitWarehouseDTO implements Serializable {
    private static final long serialVersionUID = 3213210076384392468L;

    private String appKey;

    private Integer id;

    private String appToken;

    private String name;

    private String code;

    private String countryCode;

    private List<InitWarehouseDTO> item;

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

    public List<InitWarehouseDTO> getItem() {
        return item;
    }

    public void setItem(List<InitWarehouseDTO> item) {
        this.item = item;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
