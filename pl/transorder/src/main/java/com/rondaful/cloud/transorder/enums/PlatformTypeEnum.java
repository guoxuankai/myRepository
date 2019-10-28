package com.rondaful.cloud.transorder.enums;

public enum PlatformTypeEnum {

    /**
     * 定义返回码
     */

    AMAZON("亚马逊", 1),
    EBAY("易贝", 2),
    APLIEXPRESS("速卖通", 3);


    private String name;
    private int code;

    PlatformTypeEnum(String name, int code) {
        this.name = name;
        this.code = code;

    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
