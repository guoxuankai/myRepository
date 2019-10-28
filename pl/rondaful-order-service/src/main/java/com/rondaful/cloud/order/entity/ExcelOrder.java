package com.rondaful.cloud.order.entity;

import com.rondaful.cloud.order.seller.Empower;

import java.util.concurrent.atomic.AtomicInteger;

public class ExcelOrder extends SysOrder{
    private String sn;
    private AtomicInteger lines = new AtomicInteger(0);
    private AtomicInteger success = new AtomicInteger(0);
    private AtomicInteger fail = new AtomicInteger(0);
    private String platform;
    private String userName;
    private Empower empower;
    private String errorMsg;

    public Empower getEmpower() {
        return empower;
    }

    public void setEmpower(Empower empower) {
        this.empower = empower;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    private Integer successCount;
    private Integer failCount;
    private Boolean isFill = false;

    private Boolean isSuccess=false;

    public Boolean getFill() {
        return isFill;
    }

    public void setFill(Boolean fill) {
        isFill = fill;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getRowCount() {
        return lines.get();
    }

    public void addCount() {
        this.lines.addAndGet(1);
    }

    public void addSuccess() {
        this.success.addAndGet(1);
    }

    public void addFail() {
        this.fail.addAndGet(1);
    }

    public Integer getSuccessCount() {
        return success.get();
    }

    public Integer getFailCount() {
        return fail.get();
    }

    public Boolean isSuccess(){
        return (getRowCount()-success.get())==0;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public void setIsSuccess(Boolean status){
        this.isSuccess=status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
