package com.rondaful.cloud.transorder.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author guoxuankai
 * @date 2019/10/18 14:53
 */
public class ConvertOrderVO {

    @ApiModelProperty(value = "来源订单ID")
    private String platformOrderId;

    @ApiModelProperty(value = "订单转入状态:1转入成功,2转入失败")
    private Byte converSysStatus;

    @ApiModelProperty(value = "不可转入原因")
    private String failureReason;

    public ConvertOrderVO(String platformOrderId, Byte converSysStatus, String failureReason) {
        this.platformOrderId = platformOrderId;
        this.converSysStatus = converSysStatus;
        this.failureReason = failureReason;
    }

    public String getPlatformOrderId() {
        return platformOrderId;
    }

    public void setPlatformOrderId(String platformOrderId) {
        this.platformOrderId = platformOrderId;
    }

    public Byte getConverSysStatus() {
        return converSysStatus;
    }

    public void setConverSysStatus(Byte converSysStatus) {
        this.converSysStatus = converSysStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
