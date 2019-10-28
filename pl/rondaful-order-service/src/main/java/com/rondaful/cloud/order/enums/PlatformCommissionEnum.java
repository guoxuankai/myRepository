package com.rondaful.cloud.order.enums;

import java.math.BigDecimal;

/**
 * 系统订单来源类型
 *
 * @author wujiachuang
 * @date 2019-06-25 17:09:16
 **/
public enum PlatformCommissionEnum {

    AMAZON("亚马逊",new BigDecimal("0.15")),
    EBAY("易趣",new BigDecimal("0.15")),
    WISH("速卖通", new BigDecimal("0.10"));
    private String msg;
    private BigDecimal value;

    PlatformCommissionEnum(String msg, BigDecimal value) {
        this.msg = msg;
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public BigDecimal getValue() {
        return value;
    }
}
