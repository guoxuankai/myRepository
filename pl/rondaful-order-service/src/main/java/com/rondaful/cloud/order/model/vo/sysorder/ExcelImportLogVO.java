package com.rondaful.cloud.order.model.vo.sysorder;

public class ExcelImportLogVO {
    //excel中的序号
    private String sn;
    private String orderID;
    private String operator;
    private boolean status;
    private String remark;

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public boolean isStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
