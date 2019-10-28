package com.rondaful.cloud.order.model.aliexpress.response;

import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderMoney;
import com.rondaful.cloud.order.entity.aliexpress.AliexpressOrderReceipt;

import java.io.Serializable;

/**
 * @Author: xqq
 * @Date: 2019/4/4
 * @Description:
 */
public class OrderOtherDTO implements Serializable {

    private AliexpressOrderMoney orderMoney;

    private AliexpressOrderReceipt orderReceipt;

    public AliexpressOrderMoney getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(AliexpressOrderMoney orderMoney) {
        this.orderMoney = orderMoney;
    }

    public AliexpressOrderReceipt getOrderReceipt() {
        return orderReceipt;
    }

    public void setOrderReceipt(AliexpressOrderReceipt orderReceipt) {
        this.orderReceipt = orderReceipt;
    }


}
