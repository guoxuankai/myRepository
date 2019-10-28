package com.brandslink.cloud.logistics.thirdLogistics.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TraderType {

    MIAOXIN("淼信", "miaoxin"),
    YUNTU("云途", "yuntu");

    @Getter
    private String name;
    @Getter
    private String code;

}