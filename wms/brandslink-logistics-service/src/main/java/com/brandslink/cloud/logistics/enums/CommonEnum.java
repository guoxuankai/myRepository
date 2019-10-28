package com.brandslink.cloud.logistics.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommonEnum {

    @AllArgsConstructor
    public enum EnabledDisable{
        Enabled(1),
        Disable(2);
        @Getter
        private int code;
    }

    @AllArgsConstructor
    public enum IMG_TYPE{
        JPG ("jpg"),
        PNG ("png");
        @Getter
        private String tpye;
    }

    public static void main(String[] args) {
        System.out.println(EnabledDisable.Disable.code);
    }
}
