package com.rondaful.cloud.order.enums;

/**
 * 规则事件类型
 * @author liusiying
 */
public enum RuleEventType {
    MATCH_SHIP_WAREHOUSE("匹配发货仓库",1),
    MATCH_LOGISTICS_TYPE("匹配物流类型",2);
    String name;
    Integer value;

    RuleEventType(String name, Integer value) {
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
