package com.rondaful.cloud.supplier.model.enums;

/**
 * @Author: xqq
 * @Date: 2019/6/18
 * @Description:
 */
public enum TransitEnum {

    /**
     * 入库单类型：0:标准入库单 3-中转入库单 5-FBA入库单
     */
    STANDARD(0),TRANSFER(3),FBA(5);

    private Integer type;

    TransitEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }}
