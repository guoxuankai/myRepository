package com.rondaful.cloud.order.enums;

/**
 * 订单转入状态
 *
 * @author Blade
 * @date 2019-07-17 10:28:01
 **/
public enum ConvertSysStatusEnum {

    PENDING("待处理", 0),
    CONVERT_SUCCESS("转入成功", 1),
    CONVERT_FAILURE("转入失败", 2),
    CONVERT_PORTION_SUCCESS("部分转入成功", 3);

    private String msg;
    private int value;

    ConvertSysStatusEnum(String msg, int value) {
        this.msg = msg;
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public int getValue() {
        return value;
    }
}
