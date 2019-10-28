package com.rondaful.cloud.order.entity;

import java.io.Serializable;

/**
* @Description:平台sku映射sku数量表
* @author:范津 
* @date:2019年9月2日 上午10:00:05
 */
public class SkuMapBind implements Serializable{ 
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long mapId;

    private String systemSku;

    private Integer skuNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMapId() {
        return mapId;
    }

    public void setMapId(Long mapId) {
        this.mapId = mapId;
    }

    public String getSystemSku() {
        return systemSku;
    }

    public void setSystemSku(String systemSku) {
        this.systemSku = systemSku == null ? null : systemSku.trim();
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }
}