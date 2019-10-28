package com.rondaful.cloud.order.enums;

/**
 * 平台规则转换成用户服务平台类型
 *
 * @author Blade
 * @date 2019-06-25 17:19:09
 **/
public enum PlatformRuleCovertToUserServicePlatformEnum {
    E_BAY("eBay", 1),

    AMAZON("amazon", 2),

    ALIEXPRESS("aliexpress", 3),

    WISH("wish", 4),

    OTHER("other", 4);

    private String platform;
    //1 ebay   2 Amazon 3 aliexpress 4 其他平台
    private Integer userServicePlatform;

    PlatformRuleCovertToUserServicePlatformEnum(String platform, Integer userServicePlatform) {
        this.userServicePlatform = userServicePlatform;
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public Integer getUserServicePlatform() {
        return userServicePlatform;
    }

    public static Integer getOtherPlatformCode(String platform) {
        PlatformRuleCovertToUserServicePlatformEnum[] orderSourceCovertToUserServicePlatformEnums = PlatformRuleCovertToUserServicePlatformEnum.values();
        for (PlatformRuleCovertToUserServicePlatformEnum orderSourceCovertToUserServicePlatformEnum : orderSourceCovertToUserServicePlatformEnums) {
            if (platform.equalsIgnoreCase(orderSourceCovertToUserServicePlatformEnum.getPlatform())) {
                return orderSourceCovertToUserServicePlatformEnum.getUserServicePlatform();
            }
        }
        return null;
    }
}
