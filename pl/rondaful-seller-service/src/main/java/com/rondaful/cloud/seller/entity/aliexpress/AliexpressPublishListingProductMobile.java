package com.rondaful.cloud.seller.entity.aliexpress;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(description = "速卖通刊登移动端sku详情类")
public class AliexpressPublishListingProductMobile {

    @ApiModelProperty(value = "")
    private Long id;


    @ApiModelProperty(value = "品连sku")
    private String plSku;

    @ApiModelProperty(value = "平台sku")
    private String platformSku;



    @ApiModelProperty(value = "颜色")
    private String colourIdName;

    @ApiModelProperty(value = "颜色自定义名称")
    private String colourName;


    @ApiModelProperty(value = "尺寸")
    private String sizeIdName;

    @ApiModelProperty(value = "尺寸自定义名称")
    private String sizeName;

    @ApiModelProperty(value = "发货地")
    private String placeDispatchIdName;

    @ApiModelProperty(value = "发货地自定义名称")
    private String placeDispatchName;

    @ApiModelProperty(value = "库存")
    private Integer inventory;

    @ApiModelProperty(value = "零售价")
    private BigDecimal retailPrice;

    @ApiModelProperty(value = "图片")
    private String productImage;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlSku() {
        return plSku;
    }

    public void setPlSku(String plSku) {
        this.plSku = plSku;
    }

    public String getPlatformSku() {
        return platformSku;
    }

    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku;
    }

    public String getColourIdName() {
        return colourIdName;
    }

    public void setColourIdName(String colourIdName) {
        this.colourIdName = colourIdName;
    }

    public String getColourName() {
        return colourName;
    }

    public void setColourName(String colourName) {
        this.colourName = colourName;
    }

    public String getSizeIdName() {
        return sizeIdName;
    }

    public void setSizeIdName(String sizeIdName) {
        this.sizeIdName = sizeIdName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getPlaceDispatchIdName() {
        return placeDispatchIdName;
    }

    public void setPlaceDispatchIdName(String placeDispatchIdName) {
        this.placeDispatchIdName = placeDispatchIdName;
    }

    public String getPlaceDispatchName() {
        return placeDispatchName;
    }

    public void setPlaceDispatchName(String placeDispatchName) {
        this.placeDispatchName = placeDispatchName;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
