package com.rondaful.cloud.order.enums;

public enum ERPOrderStatusEnum {

    CANCELLED(4294967295l, "作废"),
    STAY_DISTRIBUTION(196608l, "待配货"),
    ALOCATED_SYOCK(196609l, "已分配库存"),
    SHIPPED(983044l, "已发货"),
    RECEIVED(983048l, "已签收"),
    STOCKOUT(2097153l, "缺货"),
    HAVE_RECEIVED_RETURN(983296l, "已收到退换货"),
    NO_TRACE_NUMBER_UPLOAD(462848l, "已上传无跟踪号"),
    TRACE_NUMBER_BEEN_UPLOADED_AND_TAKEN(471040l, "已上传已取跟踪号"),
    TRACE_NUMBER_BEEN_UPLOADED_NOT_TAKEN(466944l, "已上传未取跟踪号");

    public Long process;
    public String msg;

    ERPOrderStatusEnum(Long process, String msg) {
        this.msg = msg;
        this.process = process;
    }
    public Long getProcess() {
        return process;
    }
    public String getMsg() {
        return msg;
    }

}
