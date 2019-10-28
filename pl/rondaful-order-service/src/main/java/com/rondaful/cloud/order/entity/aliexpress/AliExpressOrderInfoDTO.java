package com.rondaful.cloud.order.entity.aliexpress;

import java.io.Serializable;
import java.util.List;

/**
 * @author Blade
 * @date 2019-06-21 15:05:45
 **/
public class AliExpressOrderInfoDTO implements Serializable {

    private AliexpressOrder aliexpressOrder;

    private AliexpressOrderReceipt aliexpressOrderReceipt;

    private AliexpressOrderMoney aliexpressOrderMoney;

    private List<AliexpressOrderChild> aliexpressOrderChildList;

    private static final long serialVersionUID = 4614969938100541760L;

    public AliexpressOrder getAliexpressOrder() {
        return aliexpressOrder;
    }

    public void setAliexpressOrder(AliexpressOrder aliexpressOrder) {
        this.aliexpressOrder = aliexpressOrder;
    }

    public AliexpressOrderReceipt getAliexpressOrderReceipt() {
        return aliexpressOrderReceipt;
    }

    public void setAliexpressOrderReceipt(AliexpressOrderReceipt aliexpressOrderReceipt) {
        this.aliexpressOrderReceipt = aliexpressOrderReceipt;
    }

    public AliexpressOrderMoney getAliexpressOrderMoney() {
        return aliexpressOrderMoney;
    }

    public void setAliexpressOrderMoney(AliexpressOrderMoney aliexpressOrderMoney) {
        this.aliexpressOrderMoney = aliexpressOrderMoney;
    }

    public List<AliexpressOrderChild> getAliexpressOrderChildList() {
        return aliexpressOrderChildList;
    }

    public void setAliexpressOrderChildList(List<AliexpressOrderChild> aliexpressOrderChildList) {
        this.aliexpressOrderChildList = aliexpressOrderChildList;
    }
}
