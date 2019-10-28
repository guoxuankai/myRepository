package com.rondaful.cloud.commodity.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@ApiModel(description = "平台sku映射")
public class SellerSkuMap implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    @ApiModelProperty(value = "sku映射的id 主键 自增")
    private Long id;

    @ApiModelProperty(value = "平台：amazon, eBay, wish, aliexpress,other")
    private String platform;

    @ApiModelProperty(value = "授权id（店铺id）")
    private String authorizationId;
    
    @ApiModelProperty(value = "卖家账号")
    private String sellerPlAccount;

    @ApiModelProperty(value = "卖家ID")
    private String sellerPlId;

    @ApiModelProperty(value = "授权id列表", hidden = true)
    private List<Integer> authorizationIds;

    @ApiModelProperty(value = "品连sku")
    private String plSku;

    @ApiModelProperty(value = "平台sku")
    private String platformSku;

    @ApiModelProperty(value = "映射状态[1:启用  0:停用 默认启用 ]")
    private Integer status;

    @ApiModelProperty(value = "version")
    private Long version;

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

    @ApiModelProperty(value = "店铺名称")
    private String sellerSelfAccount;

    @ApiModelProperty(value = "卖家品连ID列表（部分查询可用）")
    private List<Integer> pinlianIds;

    @ApiModelProperty(value = "sku数组")
    private List<String> plSkus;

    @ApiModelProperty(value = "品连sku及数量组合，sku1:2|sku2:3")
    private String skuGroup;
    
    private List<SkuMapBind> skuBinds;


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

    public List<Integer> getAuthorizationIds() {
        return authorizationIds;
    }

    public void setAuthorizationIds(List<Integer> authorizationIds) {
        this.authorizationIds = authorizationIds;
    }

    public List<Integer> getPinlianIds() {
        return pinlianIds;
    }

    public void setPinlianIds(List<Integer> pinlianIds) {
        this.pinlianIds = pinlianIds;
    }

    public String getSellerPlId() {
        return sellerPlId;
    }

    public void setSellerPlId(String sellerPlId) {
        this.sellerPlId = sellerPlId;
    }

    public List<String> getPlSkus() {
        return plSkus;
    }

    public void setPlSkus(List<String> plSkus) {
        this.plSkus = plSkus;
    }

	public String getSkuGroup() {
		return skuGroup;
	}

	public void setSkuGroup(String skuGroup) {
		this.skuGroup = skuGroup;
	}

	public List<SkuMapBind> getSkuBinds() {
		return skuBinds;
	}

	public void setSkuBinds(List<SkuMapBind> skuBinds) {
		this.skuBinds = skuBinds;
	}
    
}