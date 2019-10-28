package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModelProperty;

public class GoodCangSnItem {

    @ApiModelProperty(value = "\tS/N码")
    private String sn;

    @ApiModelProperty(value = "IMEI")
    private String imei;


    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
