package com.rondaful.cloud.order.enums;

/**
 * Wms的发货回调状态转换为系统状态
 *
 * @author Blade
 * @date 2019-07-25 14:39:09
 **/
public enum WmsEnum {

    SHIPPING_FAILED("5", "shipping_failed"),
    SHIPPING_TIME("4", "shipping_time");

    private String deliveryStatus;

    private String deliverProcess;


    WmsEnum(String deliveryStatus, String deliverProcess) {
        this.deliveryStatus = deliveryStatus;
        this.deliverProcess = deliverProcess;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getDeliverProcess() {
        return deliverProcess;
    }

    public static String getSpeedCode(String orderSource) {
        WmsEnum[] orderSourceCovertToGoodCandPlatformEnums = WmsEnum.values();
        for (WmsEnum orderSourceCovertToGoodCandPlatformEnum : orderSourceCovertToGoodCandPlatformEnums) {
            if (orderSourceCovertToGoodCandPlatformEnum.getDeliveryStatus().equals(orderSource)) {
                return orderSourceCovertToGoodCandPlatformEnum.getDeliverProcess();
            }
        }
        return SHIPPING_TIME.deliverProcess;
    }
}
