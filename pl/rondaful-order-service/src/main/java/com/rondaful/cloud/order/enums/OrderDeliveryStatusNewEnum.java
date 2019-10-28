package com.rondaful.cloud.order.enums;

    /**
     * 订单发货状态枚举类
     */
    public enum OrderDeliveryStatusNewEnum {
        WAIT_PAY("待付款",(byte)1),
        STOCKOUT("缺货",(byte)2),
        WAIT_SHIP("待发货",(byte)3),
        INTERCEPTED("已拦截",(byte)4),
        DELIVERED("已发货",(byte)5),
        PARTIALLYSHIPPED("部分发货",(byte)6),
        CANCELLED("已作废",(byte)7),
        COMPLETED("已完成",(byte)8);

        private String msg;
        private byte value;
        OrderDeliveryStatusNewEnum(String msg, byte value) {
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

