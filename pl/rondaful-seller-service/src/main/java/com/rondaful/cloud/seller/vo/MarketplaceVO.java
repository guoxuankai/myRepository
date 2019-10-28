package com.rondaful.cloud.seller.vo;

import io.swagger.annotations.ApiModelProperty;

public class MarketplaceVO {

    @ApiModelProperty(value = "国家名称")
    private String countryName;
    @ApiModelProperty(value = "国家code")
    private String countryCode;
    @ApiModelProperty(value = "uri")
    private String uri;
    @ApiModelProperty(value = "站点code")
    private String marketplaceId;


    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

}
