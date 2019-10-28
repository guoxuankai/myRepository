package com.rondaful.cloud.order.enums;

/**
 * 订单规则类型
 * @author liusiying
 * @see java.lang.Enum
 */
public enum RuleTypeEnum {

    PUBLIC_RULE("公共规则",1),
    SELLER_RULE("卖家规则",2);
    private String name;
    private Integer value;

    RuleTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
