package com.rondaful.cloud.supplier.model.dto.logistics;

import java.util.List;

public class AliexpressAddressDTO {

    private List<LogisticsAddress> sendAddress;

    private List<LogisticsAddress> refundAddress;

    private List<LogisticsAddress> pickupAddress;

    public List<LogisticsAddress> getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(List<LogisticsAddress> sendAddress) {
        this.sendAddress = sendAddress;
    }

    public List<LogisticsAddress> getRefundAddress() {
        return refundAddress;
    }

    public void setRefundAddress(List<LogisticsAddress> refundAddress) {
        this.refundAddress = refundAddress;
    }

    public List<LogisticsAddress> getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(List<LogisticsAddress> pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    @Override
    public String toString() {
        return "AliexpressAddressDTO{" +
                "sendAddress=" + sendAddress +
                ", refundAddress=" + refundAddress +
                ", pickupAddress=" + pickupAddress +
                '}';
    }
}
