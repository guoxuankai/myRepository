package com.rondaful.cloud.supplier.model.dto.logistics;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/10/18
 * @Description:
 */
public class EQueryDeliveryFeeDTO implements Serializable {
    private static final long serialVersionUID = 2276559291396798792L;

    private String warehouse_code;
    private String country_code;
    private String search_type;
    private List<SkuNum> skus;
    private List<String> shipping_code_arr;

    private Integer channel_id;
    private String city;
    private String zip;
    private Double length;
    private Double width;
    private Double height;
    private Double weight;

    public String getWarehouse_code() {
        return warehouse_code;
    }

    public void setWarehouse_code(String warehouse_code) {
        this.warehouse_code = warehouse_code;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getSearch_type() {
        return search_type;
    }

    public void setSearch_type(String search_type) {
        this.search_type = search_type;
    }

    public List<SkuNum> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuNum> skus) {
        this.skus = skus;
    }

    public List<String> getShipping_code_arr() {
        return shipping_code_arr;
    }

    public void setShipping_code_arr(List<String> shipping_code_arr) {
        this.shipping_code_arr = shipping_code_arr;
    }

    public Integer getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(Integer channel_id) {
        this.channel_id = channel_id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
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
}

