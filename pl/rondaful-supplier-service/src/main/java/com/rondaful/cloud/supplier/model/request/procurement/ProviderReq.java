package com.rondaful.cloud.supplier.model.request.procurement;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/6/19
 * @Description:
 */
public class ProviderReq implements Serializable {
    private static final long serialVersionUID = 8287756992174177288L;

    @ApiModelProperty(value = "修改时必传,新增不管",dataType = "Integer",name = "id")
    private Integer id;

    @ApiModelProperty(value = "供货商名称",dataType = "String",name = "providerName")
    private String providerName;

    @ApiModelProperty(value = "社会信用代码",dataType = "String",name = "creditCode")
    private String creditCode;

    @ApiModelProperty(value = "供货商类型:1-工厂,2-公司,3-商户,4-企业",dataType = "Integer",name = "type")
    private Integer type;

    @ApiModelProperty(value = "店铺url",dataType = "String",name = "url")
    private String url;

    @ApiModelProperty(value = "是否支持退货",dataType = "Boolean",name = "isBack")
    private Boolean isBack;

    @ApiModelProperty(value = "一级分类",dataType = "String",name = "levelOne")
    private String levelOne;

    @ApiModelProperty(value = "二级分类",dataType = "String",name = "levelTwo")
    private String levelTwo;

    @ApiModelProperty(value = "三级分类",dataType = "String",name = "levelThree")
    private String levelThree;

    @ApiModelProperty(value = "交付天数",dataType = "Integer",name = "deliveryTime")
    private Integer deliveryTime;

    @ApiModelProperty(value = "采购员",dataType = "String",name = "buyer")
    private String buyer;

    @ApiModelProperty(value = "公司地址",dataType = "String",name = "companyAddress")
    private String companyAddress;

    @ApiModelProperty(value = "经营范围",dataType = "String",name = "businessScope")
    private String businessScope;

    @ApiModelProperty(value = "联系人",dataType = "String",name = "name")
    private String name;

    @ApiModelProperty(value = "法人代表",dataType = "String",name = "representative")
    private String representative;

    @ApiModelProperty(value = "联系电话",dataType = "String",name = "phone")
    private String phone;

    @ApiModelProperty(value = "发货地址",dataType = "String",name = "sendAddress")
    private String sendAddress;

    @ApiModelProperty(value = "旺旺账号",dataType = "String",name = "wangWang")
    private String wangWang;

    @ApiModelProperty(value = "营业执照",dataType = "String",name = "businessLicense")
    private String businessLicense;

    @ApiModelProperty(value = "其他证件",dataType = "String",name = "otherPapers")
    private String otherPapers;

    @ApiModelProperty(value = "备注",dataType = "String",name = "remake")
    private String remake;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsBack() {
        return isBack;
    }

    public void setIsBack(Boolean isBack) {
        isBack = isBack;
    }

    public String getLevelOne() {
        return levelOne;
    }

    public void setLevelOne(String levelOne) {
        this.levelOne = levelOne;
    }

    public String getLevelTwo() {
        return levelTwo;
    }

    public void setLevelTwo(String levelTwo) {
        this.levelTwo = levelTwo;
    }

    public String getLevelThree() {
        return levelThree;
    }

    public void setLevelThree(String levelThree) {
        this.levelThree = levelThree;
    }

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public String getWangWang() {
        return wangWang;
    }

    public void setWangWang(String wangWang) {
        this.wangWang = wangWang;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getOtherPapers() {
        return otherPapers;
    }

    public void setOtherPapers(String otherPapers) {
        this.otherPapers = otherPapers;
    }

    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }
}
