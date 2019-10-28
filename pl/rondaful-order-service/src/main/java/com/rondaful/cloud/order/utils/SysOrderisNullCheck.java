package com.rondaful.cloud.order.utils;

import com.rondaful.cloud.order.entity.SysOrder;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * 作者: wujiachuang
 * 时间: 2019-01-08 10:32
 * 包名: com.rondaful.cloud.order.utils
 * 描述:
 */
public class SysOrderisNullCheck {
    public static SysOrder orderSetNull(SysOrder sysOrder) {
        sysOrder.setSysOrderId("");
        sysOrder.setRecordNumber("");
        sysOrder.setSourceOrderId("");
        sysOrder.setConverSysStatus((byte) 1);
        sysOrder.setOrderSource((byte) 1);
        sysOrder.setOrderDeliveryStatus((byte) 1);
        sysOrder.setMainOrderId("");
        sysOrder.setSplittedOrMerged((byte) 0);
        sysOrder.setChildIds("");
        sysOrder.setIsValid((byte) 0);
        sysOrder.setPlatformSellerAccount("");
        sysOrder.setBuyerUserId("");
        sysOrder.setBuyerName("");
        sysOrder.setPayId("");
        sysOrder.setTotal(BigDecimal.valueOf(0.0000));
        sysOrder.setOrderAmount(BigDecimal.valueOf(0.0000));
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");
        sysOrder.setPayId("");

        return sysOrder;
    }
}
