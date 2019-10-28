package com.rondaful.cloud.user.enums;

/**
 * @Author: xqq
 * @Date: 2019/5/9
 * @Description:
 */
public enum UserStatusEnum {

    /**
     * 不同业务场景  此值含义不同
     *
     */
    CHANGE(-1),
    /**
     * 1：启用
     */
    ACTIVATE(1),
    /**
     * 4：禁用
     */
    DISABLE(4),
    /**
     * 0：待激活
     */
    NO_ACTIVATE(0),
    /**
     * 2：审核中
     */
    NO_AUDIT(2),
    /**
     * 3：审核失败
     */
    AUDIT_FILE(3);

    private Integer status;

    UserStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
