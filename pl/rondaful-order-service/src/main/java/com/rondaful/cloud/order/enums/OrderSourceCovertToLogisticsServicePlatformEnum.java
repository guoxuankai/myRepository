package com.rondaful.cloud.order.enums;

/**
 * 订单服务的订单来源转换为对应其他服务的平台
 *
 * @author Blade
 * @date 2019-06-25 17:19:09
 **/
public enum OrderSourceCovertToLogisticsServicePlatformEnum {
//    MANUAL_CREATE( 1, 4),
//
//    BULK_IMPORT(2, 4),
//
//    THIRD_API_PUSH(3, 4),

    CONVER_FROM_CREATE(1, 0),

    CONVER_FROM_EBAY(4, 1),

    CONVER_FROM_AMAZON(5, 2),

    CONVER_FROM_ALIEXPRESS(6, 4),

    CONVER_FROM_WISH(7, 3),

    XINGSHANG_API_PUSH(8, 5);

    private int orderSource;

    private int logisticsPlatform;

    private static int logisticsOtherPlatform = 6;

    OrderSourceCovertToLogisticsServicePlatformEnum(int orderSource, int logisticsPlatform) {
        this.logisticsPlatform = logisticsPlatform;
        this.orderSource = orderSource;
    }

    public int getOrderSource() {
        return orderSource;
    }

    public int getLogisticsPlatform() {
        return logisticsPlatform;
    }

    public static int getLogisticsPlatformCode(int orderSource) {
        OrderSourceCovertToLogisticsServicePlatformEnum[] orderSourceCovertToUserServicePlatformEnums = OrderSourceCovertToLogisticsServicePlatformEnum.values();
        for (OrderSourceCovertToLogisticsServicePlatformEnum orderSourceCovertToUserServicePlatformEnum : orderSourceCovertToUserServicePlatformEnums) {
            if (orderSourceCovertToUserServicePlatformEnum.getOrderSource() == orderSource) {
                return orderSourceCovertToUserServicePlatformEnum.getLogisticsPlatform();
            }
        }
        return logisticsOtherPlatform;
    }
}
