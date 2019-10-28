package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.entity
 * @ClassName: CancelOrderDTO
 * @Author: Superhero
 * @Description: 第三方调取消订单接口接受参数对象
 * @Date: 2019/8/10 11:38
 */
@ApiModel(description = "第三方调取消订单接口接受参数对象")
public class CancelOrderDTO {
    @ApiModelProperty(value = "店铺ID")
    private Integer platformShopId;
    @ApiModelProperty(value = "订单ID")
    private String sysOrderId;
    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;
    @ApiModelProperty(value = "取消订单原因")
    private String cancelReason;

    @Override
    public String toString() {
        return "CancelOrderDTO{" +
                "platformShopId=" + platformShopId +
                ", sysOrderId='" + sysOrderId + '\'' +
                ", sourceOrderId='" + sourceOrderId + '\'' +
                ", cancelReason='" + cancelReason + '\'' +
                '}';
    }

    public CancelOrderDTO() {
    }

    public CancelOrderDTO(Integer platformShopId, String sysOrderId, String sourceOrderId, String cancelReason) {
        this.platformShopId = platformShopId;
        this.sysOrderId = sysOrderId;
        this.sourceOrderId = sourceOrderId;
        this.cancelReason = cancelReason;
    }

    public Integer getPlatformShopId() {
        return platformShopId;
    }

    public void setPlatformShopId(Integer platformShopId) {
        this.platformShopId = platformShopId;
    }

    public String getSysOrderId() {
        return sysOrderId;
    }

    public void setSysOrderId(String sysOrderId) {
        this.sysOrderId = sysOrderId;
    }

    public String getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(String sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
