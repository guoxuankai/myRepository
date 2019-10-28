package com.rondaful.cloud.order.model.dto.syncorder;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Blade
 * @date 2019-07-31 11:19:08
 **/
public class UpdateSourceOrderDetailDTO implements Serializable {
    private static final long serialVersionUID = -5153701495057919026L;

    @ApiModelProperty("平台订单ID")
    private String orderId;

    @ApiModelProperty("平台订单项ID")
    private String orderItemId;

    @ApiModelProperty("转换状态")
    private Integer converSysStatus;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private Date updateDate;

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getConverSysStatus() {
        return converSysStatus;
    }

    public void setConverSysStatus(Integer converSysStatus) {
        this.converSysStatus = converSysStatus;
    }
}
