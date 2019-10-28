package com.rondaful.cloud.order.enums;

    /**
     * 订单包裹状态枚举类
     */
    public enum OrderPackageStatusEnum {
        WAIT_PUSH("待推送","wait_push"),
        WAIT_DELIVER("待发货","wait_deliver"),
        DELIVERED("已发货","delivered"),
        PUSH_FAIL("推送失败","push_fail"),
        ;

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        OrderPackageStatusEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

