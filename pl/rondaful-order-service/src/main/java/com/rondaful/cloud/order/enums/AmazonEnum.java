package com.rondaful.cloud.order.enums;

public class AmazonEnum {
    public enum OrderStatus {

        PENDING_AVAILABILITY("PendingAvailability", "预订"),
        PENDING("Pending", "待支付"),
        UNSHIPPED("Unshipped", "待发货"),
        PARTIALLY_SHIPPED("PartiallyShipped", "部分发货"),
        SHIPPED("Shipped", "均已发货"),
        INVOICE_UNCONFIRMED("InvoiceUnconfirmed", "均已发货、未寄发票"),
        CANCELED("Canceled", "已取消"),
        UNFULFILLABLE("Unfulfillable", "无法配送");

        public String msg;
        public String value;

        OrderStatus(String msg, String value) {
            this.msg = msg;
            this.value = value;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}