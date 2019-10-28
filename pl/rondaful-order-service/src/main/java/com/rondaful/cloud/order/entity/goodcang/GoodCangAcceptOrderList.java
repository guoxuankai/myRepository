package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class GoodCangAcceptOrderList {

    @ApiModelProperty(value = "订单号")
    private String orderCode;

    @ApiModelProperty(value = "参考号")
    private String referenceNo;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNumber;

    @ApiModelProperty(value = "物流产品Code")
    private String smCode;

    @ApiModelProperty(value = "创建时间")
    private String addTime;

    @ApiModelProperty(value = "渠道id")
    private Integer scId;

    @ApiModelProperty(value = "仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "签出时间")
    private String outStock_time;

    @ApiModelProperty(value = "预估重量")
    private Long soWeight;

    @ApiModelProperty(value = "运输费")
    private Long soShippingFee;

    @ApiModelProperty(value = "订单明细")
    private List<GoodCangItem> item;

    @ApiModelProperty(value = "费用明细")
    private List<GoodCangFeeDetails>  feeDetails;

    @ApiModelProperty(value = "箱子明细")
    private List<GoodCangOrderBoxInfo> orderBoxInfo;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getSmCode() {
        return smCode;
    }

    public void setSmCode(String smCode) {
        this.smCode = smCode;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public Integer getScId() {
        return scId;
    }

    public void setScId(Integer scId) {
        this.scId = scId;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getOutStock_time() {
        return outStock_time;
    }

    public void setOutStock_time(String outStock_time) {
        this.outStock_time = outStock_time;
    }

    public Long getSoWeight() {
        return soWeight;
    }

    public void setSoWeight(Long soWeight) {
        this.soWeight = soWeight;
    }

    public Long getSoShippingFee() {
        return soShippingFee;
    }

    public void setSoShippingFee(Long soShippingFee) {
        this.soShippingFee = soShippingFee;
    }

    public List<GoodCangItem> getItem() {
        return item;
    }

    public void setItem(List<GoodCangItem> item) {
        this.item = item;
    }

    public List<GoodCangFeeDetails> getFeeDetails() {
        return feeDetails;
    }

    public void setFeeDetails(List<GoodCangFeeDetails> feeDetails) {
        this.feeDetails = feeDetails;
    }

    public List<GoodCangOrderBoxInfo> getOrderBoxInfo() {
        return orderBoxInfo;
    }

    public void setOrderBoxInfo(List<GoodCangOrderBoxInfo> orderBoxInfo) {
        this.orderBoxInfo = orderBoxInfo;
    }


}
