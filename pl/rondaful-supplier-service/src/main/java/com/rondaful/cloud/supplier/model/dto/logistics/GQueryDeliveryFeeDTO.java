package com.rondaful.cloud.supplier.model.dto.logistics;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/10/18
 * @Description:
 */
public class GQueryDeliveryFeeDTO implements Serializable {
    private static final long serialVersionUID = 3841625969873342586L;

    /**
     * 发货仓库代码 Required
     */
    private String warehouse_code;

    /**
     * 目的国家代码 Required
     */
    private String country_code;

    /**
     * 邮政编码 Required
     */
    private String postcode;

    /**
     * 配送方式，未填写配送方式，试算所有可用物流产品的费用
     */
    private String sm_code;

    /**
     * [商品编码与包裹重量必须填写一个,商品编码与包裹重量都填写时,以商品编码为主] 格式如:["sku1","sku2:1","sku3:1"],多个商品隔开,冒号后接商品数量,不填商品数量,默认1件,最多不超过100种sku
     */
    private List<String> sku;

    /**
     * 包裹重量单位KG Optional
     */
    private Double weight;

    /**
     * 包裹长 单位CM Optional
     */
    private Double length;

    /**
     * 包裹宽 单位CM Optional
     */
    private Double width;

    /**
     * 包裹宽 单位CM Optional
     */
    private Double height;

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

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getSm_code() {
        return sm_code;
    }

    public void setSm_code(String sm_code) {
        this.sm_code = sm_code;
    }

    public List<String> getSku() {
        return sku;
    }

    public void setSku(List<String> sku) {
        this.sku = sku;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
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
}
