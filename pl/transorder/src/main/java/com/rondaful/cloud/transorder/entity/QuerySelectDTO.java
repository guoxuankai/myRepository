package com.rondaful.cloud.transorder.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: xqq
 * @Date: 2019/10/18
 * @Description:
 */
public class QuerySelectDTO implements Serializable {
    private static final long serialVersionUID = 2258131207204360230L;

    @ApiModelProperty(value = "1 价格   2 速度   3 综合")
    private Integer queryType;
    @ApiModelProperty(value = "发货仓库id")
    private Integer warehouseId;
    @ApiModelProperty(value = "目的国家编码")
    private String countryCode;
    @ApiModelProperty(value = "sku数量列表")
    private List<SkuNum> skus;
    @ApiModelProperty(value = "物流方式列表")
    private List<String> shippingArr;
    @ApiModelProperty(value = "1-ebay 2-亚马逊 3-wish 4-速卖通")
    private Integer channelId;
    @ApiModelProperty(value = "城市名")
    private String city;
    @ApiModelProperty(value = "邮编")
    private String zip;
    @ApiModelProperty(value = "长/CM")
    private Double length;
    @ApiModelProperty(value = "宽/CM")
    private Double width;
    @ApiModelProperty(value = "高/CM")
    private Double height;
    @ApiModelProperty(value = "重/kg")
    private Double weight;
    @ApiModelProperty(value = "展示多少条")
    private Integer index;
    @ApiModelProperty(value = "是否是手工单")
    private Boolean isHandOrder;

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<SkuNum> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuNum> skus) {
        this.skus = skus;
    }

    public List<String> getShippingArr() {
        return shippingArr;
    }

    public void setShippingArr(List<String> shippingArr) {
        this.shippingArr = shippingArr;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
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

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getIsHandOrder() {
        return isHandOrder;
    }

    public void setIsHandOrder(Boolean isHandOrder) {
        this.isHandOrder = isHandOrder;
    }
}
