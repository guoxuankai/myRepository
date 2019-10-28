package com.rondaful.cloud.seller.entity.aliexpress;

import com.taobao.api.internal.mapping.ApiField;

import java.io.Serializable;

public class AliexpressAeopSkuProperty implements Serializable {

    private String propertyValueDefinitionName;

    private Long propertyValueId;

    private Long skuPropertyId;

    private String skuImage;

    public String getPropertyValueDefinitionName() {
        return propertyValueDefinitionName;
    }

    public void setPropertyValueDefinitionName(String propertyValueDefinitionName) {
        this.propertyValueDefinitionName = propertyValueDefinitionName;
    }

    public Long getPropertyValueId() {
        return propertyValueId;
    }

    public void setPropertyValueId(Long propertyValueId) {
        this.propertyValueId = propertyValueId;
    }

    public Long getSkuPropertyId() {
        return skuPropertyId;
    }

    public void setSkuPropertyId(Long skuPropertyId) {
        this.skuPropertyId = skuPropertyId;
    }

    public String getSkuImage() {
        return skuImage;
    }

    public void setSkuImage(String skuImage) {
        this.skuImage = skuImage;
    }
}
