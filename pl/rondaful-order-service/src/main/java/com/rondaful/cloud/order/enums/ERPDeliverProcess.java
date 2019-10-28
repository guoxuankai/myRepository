package com.rondaful.cloud.order.enums;

import java.text.MessageFormat;

public enum ERPDeliverProcess {

    ORDER_PUSH("order_push", "入库通知"),
    DISTRIBUTION("distribution", "分配库存"),
    PACKAGE_UPLOAD_STATUS("package_upload_status", "物流下单"),
    PACKAGE_CONFIRM_STATUS("package_confirm_status", "物流交运"),
    PACKING_TIME("packing_time", "包裹包装"),
    SHIPPING_TIME("shipping_time", "发货");

    private String process;
    private String msg;

    ERPDeliverProcess(String process, String msg) {
        this.msg = msg;
        this.process = process;
    }
    public String getProcess() {
        return process;
    }
    public String getMsg() {
        return msg;
    }


    public String orderPush(String parm1) {
        String pattern = "订单【{0}】,仓库处理步骤：入库通知";
        Object[] params = new Object[]{parm1};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String distribution(String parm1) {
        String pattern = "订单【{0}】,仓库处理步骤：分配库存";
        Object[] params = new Object[]{parm1};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String packageUploadStatus(String parm1, String parm2, String parm3) {
        String pattern = "订单【{0}】,包裹【{2}】,仓库处理步骤：物流下单，跟踪号【{1}】。";
        Object[] params = new Object[]{parm1, parm2, parm3};
        String value = MessageFormat.format(pattern, params);
        return value;
    }
    public String packageUploadStatus2(String parm1, String parm2, String parm3) {
        String pattern = "订单【{0}】,包裹【{2}】,仓库处理步骤：物流下单，物流商单号【{1}】。";
        Object[] params = new Object[]{parm1, parm2, parm3};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String packageConfirmStatus(String parm1, String parm2, String parm3) {
        String pattern = "订单【{0}】,包裹【{2}】,仓库处理步骤：物流交运，跟踪号【{1}】。";
        Object[] params = new Object[]{parm1, parm2, parm3};
        String value = MessageFormat.format(pattern, params);
        return value;
    }
    public String packageConfirmStatus2(String parm1, String parm2, String parm3) {
        String pattern = "订单【{0}】,包裹【{2}】,仓库处理步骤：物流交运，物流商单号【{1}】。";
        Object[] params = new Object[]{parm1, parm2, parm3};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String packingTime(String parm1, String parm2, String parm3) {
        String pattern = "订单【{0}】,仓库处理步骤：包裹【{2}】包装，跟踪号【{1}】。";
        Object[] params = new Object[]{parm1, parm2, parm3};
        String value = MessageFormat.format(pattern, params);
        return value;
    }
    public String packingTime2(String parm1, String parm2, String parm3) {
        String pattern = "订单【{0}】,仓库处理步骤：包裹【{2}】包装，物流商单号【{1}】。";
        Object[] params = new Object[]{parm1, parm2, parm3};
        String value = MessageFormat.format(pattern, params);
        return value;
    }

    public String shippingTime(String parm1, String parm2) {
        String pattern = "订单【{0}】,仓库处理步骤：发货，跟踪号【{1}】。";
        Object[] params = new Object[]{parm1, parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }
    public String shippingTime2(String parm1, String parm2) {
        String pattern = "订单【{0}】,仓库处理步骤：发货，物流商单号【{1}】。";
        Object[] params = new Object[]{parm1, parm2};
        String value = MessageFormat.format(pattern, params);
        return value;
    }
}
