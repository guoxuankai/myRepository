package com.rondaful.cloud.supplier.model.enums;

/**
 * @Author: xqq
 * @Date: 2019/6/19
 * @Description:
 */
public enum StatusEnums {

    /**
     * 1：启用
     */
    ACTIVATE(1),
    /**
     * 4：禁用
     */
    DISABLE(4),
    /**
     * 2：审核中
     */
    NO_AUDIT(2),
    /**
     * 3：审核失败
     */
    AUDIT_FILE(3),
    /**
     * 入库
     */
    PUTWAY(5),
    /**
     * 未入库
     */
    NOT_PUTWAY(6),
    /**
     * 草稿
     */
    DRAFT(7),
    /**
     * 部分入库
     */
    PART_PUTWAY(8);


    private Integer status;

    StatusEnums(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }}
