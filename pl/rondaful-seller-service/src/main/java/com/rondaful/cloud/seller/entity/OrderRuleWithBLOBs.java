package com.rondaful.cloud.seller.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "订单规则邮寄方式完备对象")
public class OrderRuleWithBLOBs extends OrderRule implements Serializable {

    @ApiModelProperty(value = "发货仓库id列表json字符串(分配邮寄方式时存在此条件)",hidden = true)
    private String deliveryWarehouseCodeList;

    @ApiModelProperty(value = "发货仓库id列表")
    private List<String> deliveryWarehouseIds;

    @ApiModelProperty(value = "卖家账户id列表json字符串",hidden = true)
    private String sellerAccountList;

    @ApiModelProperty(value = "卖家平台账户列表")
    private List<PlatformAccount> platformAccounts;

    @ApiModelProperty(value = "收货国家列表json字符串" ,hidden = true)
    private String receiveGoodsCountryList;

    @ApiModelProperty(value = "收货国家列表")
    private List<String> receiveGoodsCountrys;

    @ApiModelProperty(value = "收货邮编列表json字符串",hidden = true)
    private String receiveGoodsZipCodeList;

    @ApiModelProperty(value = "收货邮编列表")
    private List<String> receiveGoodsZipCodes;

    @ApiModelProperty(value = "收货品连sku列表json字符串" ,hidden = true)
    private String plSkuList;

    @ApiModelProperty(value = "规则对应的品连sku列表")
    private List<String> plSkus;

    @ApiModelProperty(value = "发货仓库 ID 列表")
    private String deliveryWarehouseIdList;

    @ApiModelProperty(value = "发货仓库 ID 列表")
    private List<String> deliveryWarehouseIdLists;

    private static final long serialVersionUID = 1L;

    public List<String> getDeliveryWarehouseIdLists() {
        return deliveryWarehouseIdLists;
    }

    public void setDeliveryWarehouseIdLists(List<String> deliveryWarehouseIdLists) {
        this.deliveryWarehouseIdLists = deliveryWarehouseIdLists;
    }

    public String getDeliveryWarehouseIdList() {
        return deliveryWarehouseIdList;
    }

    public void setDeliveryWarehouseIdList(String deliveryWarehouseIdList) {
        this.deliveryWarehouseIdList = deliveryWarehouseIdList;
    }

    public String getDeliveryWarehouseCodeList() {
        return deliveryWarehouseCodeList;
    }

    public void setDeliveryWarehouseCodeList(String deliveryWarehouseCodeList) {
        this.deliveryWarehouseCodeList = deliveryWarehouseCodeList == null?null:deliveryWarehouseCodeList.trim();
    }

    public String getSellerAccountList() {
        return sellerAccountList;
    }

    public void setSellerAccountList(String sellerAccountList) {
        this.sellerAccountList = sellerAccountList == null?null:sellerAccountList.trim();
    }

    public String getReceiveGoodsCountryList() {
        return receiveGoodsCountryList;
    }

    public void setReceiveGoodsCountryList(String receiveGoodsCountryList) {
        this.receiveGoodsCountryList = receiveGoodsCountryList == null ? null : receiveGoodsCountryList.trim();
    }

    public String getReceiveGoodsZipCodeList() {
        return receiveGoodsZipCodeList;
    }

    public void setReceiveGoodsZipCodeList(String receiveGoodsZipCodeList) {
        this.receiveGoodsZipCodeList = receiveGoodsZipCodeList == null ? null : receiveGoodsZipCodeList.trim();
    }

    public String getPlSkuList() {
        return plSkuList;
    }

    public void setPlSkuList(String plSkuList) {
        this.plSkuList = plSkuList == null ? null : plSkuList.trim();
    }

    public List<String> getReceiveGoodsCountrys() {
        return receiveGoodsCountrys;
    }

    public void setReceiveGoodsCountrys(List<String> receiveGoodsCountrys) {
        this.receiveGoodsCountrys = receiveGoodsCountrys;
    }

    public List<String> getReceiveGoodsZipCodes() {
        return receiveGoodsZipCodes;
    }

    public void setReceiveGoodsZipCodes(List<String> receiveGoodsZipCodes) {
        this.receiveGoodsZipCodes = receiveGoodsZipCodes;
    }

    public List<String> getPlSkus() {
        return plSkus;
    }

    public void setPlSkus(List<String> plSkus) {
        this.plSkus = plSkus;
    }

    public List<String> getDeliveryWarehouseIds() {
        return deliveryWarehouseIds;
    }

    public void setDeliveryWarehouseIds(List<String> deliveryWarehouseIds) {
        this.deliveryWarehouseIds = deliveryWarehouseIds;
    }

    public List<PlatformAccount> getPlatformAccounts() {
        return platformAccounts;
    }

    public void setPlatformAccounts(List<PlatformAccount> platformAccounts) {
        this.platformAccounts = platformAccounts;
    }
}