package com.rondaful.cloud.order.enums;

/**
 * 订单服务的订单来源转换为对应其他服务的平台
 *
 * @author Blade
 * @date 2019-06-25 17:19:09
 **/
public enum OrderSourceCovertToUserServicePlatformEnum {
//    MANUAL_CREATE((byte) 1, 4),
//
//    BULK_IMPORT((byte) 2, 4),
//
    THIRD_API_PUSH((byte) 3, 4),
//
    CONVER_FROM_EBAY((byte) 4, 1),

    CONVER_FROM_AMAZON((byte) 5, 2),

    CONVER_FROM_ALIEXPRESS((byte) 6, 3),

    CONVER_FROM_WISH((byte) 7, 4),

    XINGSHANG_API_PUSH((byte) 8, 4);

    private byte orderSource;
    //1 ebay   2 Amazon 3 aliexpress 4 其他平台
    private Integer otherPlatform;

    OrderSourceCovertToUserServicePlatformEnum(byte orderSource, Integer otherPlatform) {
        this.otherPlatform = otherPlatform;
        this.orderSource = orderSource;
    }

    public byte getOrderSource() {
        return orderSource;
    }

    public Integer getOtherPlatform() {
        return otherPlatform;
    }

    public static Integer getOtherPlatformCode(byte orderSource) {
        OrderSourceCovertToUserServicePlatformEnum[] orderSourceCovertToUserServicePlatformEnums = OrderSourceCovertToUserServicePlatformEnum.values();
        for (OrderSourceCovertToUserServicePlatformEnum orderSourceCovertToUserServicePlatformEnum : orderSourceCovertToUserServicePlatformEnums) {
            if (orderSourceCovertToUserServicePlatformEnum.getOrderSource() == orderSource) {
                return orderSourceCovertToUserServicePlatformEnum.getOtherPlatform();
            }
        }
        return null;
    }
}
