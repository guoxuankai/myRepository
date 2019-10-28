package com.rondaful.cloud.seller.entity.aliexpress;


import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.EAN;

import java.util.Date;
import java.util.List;

@ApiModel(description = "速卖通刊登移动端列表查询类")
public class AliexpressPublishListingMobile {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "刊登天数")
    private Integer publishDays;

    @ApiModelProperty(value = "刊登成功时间")
    private Date successTime;

    @ApiModelProperty(value = "刊登图片")
    private String productImage;

    @ApiModelProperty(value = "刊登状态 1: 草稿  2: 刊登中 3: 刊登失败 4:审核中  5: 审核失败,6:正在销售 7 已下架")
    private Integer publishStatus;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "卖家账号(自定义账号)")
    private String publishAccount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "商品描述")
    private String productDetails;

    private List<AliexpressPublishListingProductMobile> listProductMobile = Lists.newArrayList();

    public List<AliexpressPublishListingProductMobile> getListProductMobile() {
        return listProductMobile;
    }

    public void setListProductMobile(List<AliexpressPublishListingProductMobile> listProductMobile) {
        this.listProductMobile = listProductMobile;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

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

    public String getPublishAccount() {
        return publishAccount;
    }

    public void setPublishAccount(String publishAccount) {
        this.publishAccount = publishAccount;
    }
}
