package com.rondaful.cloud.supplier.model.enums;

/**
 * @Author: xqq
 * @Date: 2019/6/30
 * @Description:
 */
public enum StorageEnums {

    /**
     * 草稿
     */
    DRAFT(0),
    /**
     * 提交审核
     */
    NO_AUDIT(1),

    /**
     * 等待
     */
    WAIT(2),
    /**
     * 上架入库
     */
    PUT_AWAY(3),

    /**
     * 取消删除
     */
    DEL(4),

    /**
     * 异常
     */
    ERROR(5);


    private Integer verify;

    StorageEnums(Integer verify) {
        this.verify = verify;
    }

    public Integer getVerify() {
        return verify;
    }

    public void setVerify(Integer verify) {
        this.verify = verify;
    }}
