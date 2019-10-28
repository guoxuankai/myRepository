package com.rondaful.cloud.order.enums;

/**
 * 系统订单来源类型
 *
 * @author Blade
 * @date 2019-06-25 17:09:16
 **/
public enum OrderSourceEnum {

    MANUAL_CREATE("手工创建", (byte) 1),
    BULK_IMPORT("批量导入", (byte) 2),
    THIRD_API_PUSH("第三方平台API推送", (byte) 3),
    CONVER_FROM_EBAY("eBay平台订单转入", (byte) 4),
    CONVER_FROM_AMAZON("Amazon平台订单转入", (byte) 5),
    CONVER_FROM_ALIEXPRESS("aliexpress平台订单转入", (byte) 6),
    CONVER_FROM_WISH("wish平台订单转入", (byte) 7),
    XINGSHANG_API_PUSH("星商API推送", (byte) 8);

    private String msg;
    private byte value;

    OrderSourceEnum(String msg, byte value) {
        this.msg = msg;
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public byte getValue() {
        return value;
    }
}
