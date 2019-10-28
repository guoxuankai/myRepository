package com.rondaful.cloud.order.model.dto.remoteCommodity;

import java.io.Serializable;
import java.util.List;

/**
 * @author Blade
 * @date 2019-06-26 17:21:40
 **/
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
