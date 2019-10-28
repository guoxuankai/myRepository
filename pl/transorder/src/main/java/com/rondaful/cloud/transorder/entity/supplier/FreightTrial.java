package com.rondaful.cloud.transorder.entity.supplier;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class FreightTrial implements Serializable {

    private static final long serialVersionUID = -7598824742338409737L;

    @ApiModelProperty(value = "仓库id", required = true)
    private Integer warehouseId;

    @ApiModelProperty(value = "国家简码", required = true)
    private String countryCode;

    @ApiModelProperty(value = "邮政编码 | 谷仓必传")
    private String postCode;

    @ApiModelProperty(value = "物流方式code | 计算预估物流费必传")
    private String logisticsCode;

    @ApiModelProperty(value = "所属平台 1(eBay) 2(Amazon) 3(Wish) 4(AliExpress)")
    private String platformType;

    @ApiModelProperty(value = "sku集合", required = true)
    private List<Map<String, Object>> skuList;

    @ApiModelProperty(value = "搜索条件 1 价格最低  2 综合排序   3 物流速度最快 ", required = true)
    private Integer searchType;

    @ApiModelProperty(value = "城市")
    private String city;

    private String warehouseCode;

    private String appKey;

    private String appToken;

    private List list;

    public FreightTrial() {
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

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public List<Map<String, Object>> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Map<String, Object>> skuList) {
        this.skuList = skuList;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}