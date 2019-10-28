package com.rondaful.cloud.order.enums;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.enums
 * @ClassName: SkuBindEnum
 * @Author: Superhero
 * @Description: 平台SKU绑定状态枚举
 * @Date: 2019/9/4 14:22
 */
public enum SkuBindEnum {

    BIND("已绑定","bind"),
    UNBIND("未绑定","unbind"),
    REMOVE("已移除","remove");
    private String name;
    private String value;

    SkuBindEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
