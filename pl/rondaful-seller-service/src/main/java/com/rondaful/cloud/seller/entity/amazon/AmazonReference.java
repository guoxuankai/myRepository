package com.rondaful.cloud.seller.entity.amazon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel(description = "亚马逊引用")
public class AmazonReference {

    @ApiModelProperty(value = "刊登ID")
    private Long id;

    @ApiModelProperty(value = "刊登账号（店铺名称）")
    private String publishAccount;

    @ApiModelProperty(value = "平台sku")
    private String sku;

    @ApiModelProperty(value = "站点")
    private String publishSite;

    @ApiModelProperty(value = "站点ID")
    private String marketplaceId;

    @ApiModelProperty(value="标准的商品编码"/*,required=true*/)
    // @NotBlank(message="商品编码不能为空")
    private String standardProductID;

    @ApiModelProperty(value="标准的商品编码对应的类型 : ISBN,  UPC, ASIN, GTIN ,GCID ,PZN", allowableValues ="ISBN,  UPC, ASIN, GTIN ,GCID ,PZN", required=true)
    private String standardProductType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPublishAccount() {
        return publishAccount;
    }

    public void setPublishAccount(String publishAccount) {
        this.publishAccount = publishAccount;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPublishSite() {
        return publishSite;
    }

    public void setPublishSite(String publishSite) {
        this.publishSite = publishSite;
    }

    public String getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public String getStandardProductID() {
        return standardProductID;
    }

    public void setStandardProductID(String standardProductID) {
        this.standardProductID = standardProductID;
    }

    public String getStandardProductType() {
        return standardProductType;
    }

    public void setStandardProductType(String standardProductType) {
        this.standardProductType = standardProductType;
    }
}
