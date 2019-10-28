package com.rondaful.cloud.order.enums;

import java.text.MessageFormat;

public enum GCDeliverStatus {

    PENDING_CHECK("pending_check", "待发货审核"),
    PENDING_DELIVER("pending_deliver", "待发货"),
    DELIVERED("delivered", "已发货"),
    TEMPORARILY_SAVE("temporarily_save", "暂存"),
    ABNORMAL_ORDER("abnormal_order", "异常订单"),
    PROBLEM_SHIPMENT("problem_shipment", "问题件"),
    DISCARD("discard", "废弃"),
    PACKAGE_UPLOAD_STATUS("package_upload_status", "物流下单");

    public String process;
    public String msg;

    GCDeliverStatus(String process, String msg) {
        this.msg = msg;
        this.process = process;
    }
    public String getProcess() {
        return process;
    }
    public String getMsg() {
        return msg;
    }


    public String pendingCheck(String parm1, String parm2) {
        String pattern = "订单【{0}】,包裹【{1}】谷仓仓库状态为：待发货审核。";
        Object[] params = new Object[]{parm1,parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String pendingDeliver(String parm1, String parm2) {
        String pattern = "订单【{0}】,包裹【{1}】谷仓仓库状态为：待发货。";
        Object[] params = new Object[]{parm1, parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String delivered(String parm1, String parm2) {
        String pattern = "订单【{0}】,谷仓仓库状态为：已发货，跟踪号【{1}】。";
        Object[] params = new Object[]{parm1, parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String temporarilySave(String parm1, String parm2) {
        String pattern = "订单【{0}】,包裹【{1}】谷仓仓库状态为：暂存。";
        Object[] params = new Object[]{parm1, parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String abnormalOrder(String parm1, String parm2) {
        String pattern = "订单【{0}】,包裹【{1}】谷仓仓库状态为：异常订单。";
        Object[] params = new Object[]{parm1, parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String problemShipment(String parm, String parm2) {
        String pattern = "订单【{0}】,包裹【{1}】谷仓仓库状态为：问题件。";
        Object[] params = new Object[]{parm, parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String discard(String parm, String parm2) {
        String pattern = "订单【{0}】,包裹【{1}】谷仓仓库状态为：废弃。";
        Object[] params = new Object[]{parm, parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String packageUploadStatus(String parm1, String parm2, String parm3) {
        String pattern = "订单【{0}】,包裹【{2}】谷仓仓库：物流下单，跟踪号【{1}】。";
        Object[] params = new Object[]{parm1, parm2, parm3};
        String value = MessageFormat.format(pattern, params);
        return value;
    }
}
