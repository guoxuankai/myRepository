package com.rondaful.cloud.seller.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 卖家在平台上的sku和品连sku的映射
 */
@ApiModel(description = "卖家在平台上的sku和品连sku的映射对象")
public class SellerSkuMap implements Serializable {
    /**
     * sku映射的id 主键 自增
     */
    @ApiModelProperty(value = "sku映射的id 主键 自增")
    private Long id;

    @ApiModelProperty(value = "卖家所属平台名称[amazon, eBay, wish, aliexpress]")
    private String platform;

    @ApiModelProperty(value = "授权id（品连平台的授权表格的id）")
    private String authorizationId;

    @ApiModelProperty(value = "品连sku及数量组合，sku1*2|sku2*3")
    private String skuGroup;



    /**
     * 在品连上的sku
     */
    @ApiModelProperty(value = "在品连上的sku")
    private String plSku;

    /**
     * 其他平台的sku
     */
    @ApiModelProperty(value = "其他平台的sku")
    private String platformSku;

    /**
     * 映射状态[1:启用  2:停用 ]
     */
    @ApiModelProperty(value = "映射状态[1:启用  2:停用 默认启用 ]")
    private Integer status;

    @ApiModelProperty(value = "version")
    private Long version;

    /*    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")*/
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "开始创建时间，查询参数")
    private String startCreateTime;

    @ApiModelProperty(value = "结束创建时间，查询参数")
    private String endCreateTime;

    @ApiModelProperty(value = "卖家品连账号，查询参数")
    private String sellerPlAccount;

    @ApiModelProperty(value = "卖家账号(授权时的自定义名称)，查询参数")
    private String sellerSelfAccount;

    private static final long serialVersionUID = 1L;


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
        this.plSku = plSku == null ? null : plSku.trim();
    }

    public String getPlatformSku() {
        return platformSku;
    }

    public void setPlatformSku(String platformSku) {
        this.platformSku = platformSku == null ? null : platformSku.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getStartCreateTime() {
        return startCreateTime;
    }

    public void setStartCreateTime(String startCreateTime) {
        this.startCreateTime = startCreateTime;
    }

    public String getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(String endCreateTime) {
        this.endCreateTime = endCreateTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }


    public String getAuthorizationId() {
        return authorizationId;
    }

    public void setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId == null ? null : authorizationId.trim();
    }

    public String getSellerPlAccount() {
        return sellerPlAccount;
    }

    public void setSellerPlAccount(String sellerPlAccount) {
        this.sellerPlAccount = sellerPlAccount == null? null:sellerPlAccount.trim();
    }

    public String getSellerSelfAccount() {
        return sellerSelfAccount;
    }

    public void setSellerSelfAccount(String sellerSelfAccount) {
        this.sellerSelfAccount = sellerSelfAccount == null? null:sellerSelfAccount.trim();
    }

    public String getSkuGroup() {
        return skuGroup;
    }

    public void setSkuGroup(String skuGroup) {
        this.skuGroup = skuGroup;
    }
}