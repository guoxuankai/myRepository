package com.rondaful.cloud.order.enums;
public class OrderPackageHandleEnum {
    public enum OperateStatusEnum {

        GENERAL("普通","general"),
        SPLIT("拆分","split"),
        MERGED("合并","merged"),
        ;

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        OperateStatusEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public enum IsShowEnum {

        SHOW("展示","show"),
        NO_SHOW("不展示","no_show"),
        ;

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        IsShowEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}