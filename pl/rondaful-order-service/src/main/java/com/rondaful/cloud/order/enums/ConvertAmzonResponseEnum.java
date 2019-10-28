package com.rondaful.cloud.order.enums;

/**
 * 订单转入状态
 *
 * @author Blade
 * @date 2019-07-17 10:28:01
 **/
public enum ConvertAmzonResponseEnum {

    CONVERT_SUCCESS("全部转入成功", "1"),
    CONVERT_PORTION_SUCCESS("部分转入成功", "2"),
    CONVERT_FAILURE("全部转入失败", "3");

    private String msg;
    private String value;

    ConvertAmzonResponseEnum(String msg, String value) {
        this.msg = msg;
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public String getValue() {
        return value;
    }
}
