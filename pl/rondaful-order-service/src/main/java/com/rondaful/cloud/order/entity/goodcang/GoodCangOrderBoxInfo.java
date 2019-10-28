package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModelProperty;

public class GoodCangOrderBoxInfo {

    @ApiModelProperty(value = "箱号")
    private String boxNo;

    @ApiModelProperty(value = "数量")
    private Integer obQty;

    @ApiModelProperty(value = "长")
    private Long obLength;

    @ApiModelProperty(value = "宽")
    private Long obWidth;

    @ApiModelProperty(value = "高")
    private Long obHeight;

    @ApiModelProperty(value = "重量")
    private Long obWeight;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNumber;


    public String getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(String boxNo) {
        this.boxNo = boxNo;
    }

    public Integer getObQty() {
        return obQty;
    }

    public void setObQty(Integer obQty) {
        this.obQty = obQty;
    }

    public Long getObLength() {
        return obLength;
    }

    public void setObLength(Long obLength) {
        this.obLength = obLength;
    }

    public Long getObWidth() {
        return obWidth;
    }

    public void setObWidth(Long obWidth) {
        this.obWidth = obWidth;
    }

    public Long getObHeight() {
        return obHeight;
    }

    public void setObHeight(Long obHeight) {
        this.obHeight = obHeight;
    }

    public Long getObWeight() {
        return obWeight;
    }

    public void setObWeight(Long obWeight) {
        this.obWeight = obWeight;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
