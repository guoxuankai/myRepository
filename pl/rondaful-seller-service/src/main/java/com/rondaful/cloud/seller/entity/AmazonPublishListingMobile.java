package com.rondaful.cloud.seller.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(description = "亚马逊刊登移动端列表查询类")
public class AmazonPublishListingMobile {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "刊登天数")
    private Integer publishDays;

    @ApiModelProperty(value = "刊登成功时间")
    private Date successTime;

    @ApiModelProperty(value = "刊登图片")
    private String productImage;

    @ApiModelProperty(value = "刊登状态 1: 草稿  2: 刊登中 3: 在线 4: 刊登失败 5: 已下线")
    private Integer publishStatus;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPublishDays() {
        return publishDays;
    }

    public void setPublishDays(Integer publishDays) {
        this.publishDays = publishDays;
    }

    public Date getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(Date successTime) {
        this.successTime = successTime;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
