package com.rondaful.cloud.order.enums;

/**
 * 订单服务的订单来源转换为谷仓的平台编码
 *
 * @author Blade
 * @date 2019-07-25 14:39:09
 **/
public enum OrderSourceCovertToGoodCandPlatformEnum {

    EBAY(4, "EBAY"),

    AMAZON(5, "AMAZON"),

    ALIEXPRESS(6, "ALIEXPRESS"),

    WISH(7, "WISH"),
    // 不属于上面的平台信息
    OTHER(1, "OTHER");

    private int orderSource;

    private int logisticsPlatform;
    private String goodCangPlatform;

    private static int logisticsOtherPlatform = 6;

    OrderSourceCovertToGoodCandPlatformEnum(int orderSource, String goodCangPlatform) {
        this.orderSource = orderSource;
        this.goodCangPlatform = goodCangPlatform;
    }

    public String getGoodCangPlatform() {
        return goodCangPlatform;
    }

    public int getOrderSource() {
        return orderSource;
    }

    public int getLogisticsPlatform() {
        return logisticsPlatform;
    }

    public static String getGoodCangPlatformCode(int orderSource) {
        OrderSourceCovertToGoodCandPlatformEnum[] orderSourceCovertToGoodCandPlatformEnums = OrderSourceCovertToGoodCandPlatformEnum.values();
        for (OrderSourceCovertToGoodCandPlatformEnum orderSourceCovertToGoodCandPlatformEnum : orderSourceCovertToGoodCandPlatformEnums) {
            if (orderSourceCovertToGoodCandPlatformEnum.getOrderSource() == orderSource) {
                return orderSourceCovertToGoodCandPlatformEnum.getGoodCangPlatform();
            }
        }
        return OTHER.goodCangPlatform;
    }

    public static int getOrderSource(String platform) {
        OrderSourceCovertToGoodCandPlatformEnum[] orderSourceCovertToGoodCandPlatformEnums = OrderSourceCovertToGoodCandPlatformEnum.values();
        for (OrderSourceCovertToGoodCandPlatformEnum orderSourceCovertToGoodCandPlatformEnum : orderSourceCovertToGoodCandPlatformEnums) {
            if (orderSourceCovertToGoodCandPlatformEnum.getGoodCangPlatform().equalsIgnoreCase(platform)) {
                return orderSourceCovertToGoodCandPlatformEnum.getOrderSource();
            }
        }
        return OTHER.orderSource;
    }
}
