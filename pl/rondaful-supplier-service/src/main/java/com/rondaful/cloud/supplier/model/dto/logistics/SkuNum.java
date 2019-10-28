package com.rondaful.cloud.supplier.model.dto.logistics;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/10/18
 * @Description:
 */
public class SkuNum implements Serializable {
    private static final long serialVersionUID = -3620810103690061577L;

    private String sku;

    private Integer num;

    public SkuNum(String sku, Integer num) {
        this.sku = sku;
        this.num = num;
    }

    public SkuNum(){}

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
