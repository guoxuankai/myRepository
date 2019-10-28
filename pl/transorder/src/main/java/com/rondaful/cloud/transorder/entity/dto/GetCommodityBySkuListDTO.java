package com.rondaful.cloud.transorder.entity.dto;

import java.io.Serializable;
import java.util.List;


public class GetCommodityBySkuListDTO implements Serializable {

    private static final long serialVersionUID = -1413946242228515369L;

    private Integer sellerId;

    private List<String> systemSkuList;

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public List<String> getSystemSkuList() {
        return systemSkuList;
    }

    public void setSystemSkuList(List<String> systemSkuList) {
        this.systemSkuList = systemSkuList;
    }
}
