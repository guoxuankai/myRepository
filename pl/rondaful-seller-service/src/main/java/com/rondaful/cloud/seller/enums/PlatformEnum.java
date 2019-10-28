package com.rondaful.cloud.seller.enums;

public enum PlatformEnum {

AMAZON("AMAZON","亚马逊"),
ALIEXPRESS("ALIEXPRESS","速卖通"),
EBAY("EBAY","EBAY");

/** 枚举码. */
private final String code;

/** 描述信息. */
private final String desc;

private PlatformEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
}

public String getCode() {
	return code;
}

public String getDesc() {
	return desc;
}
}