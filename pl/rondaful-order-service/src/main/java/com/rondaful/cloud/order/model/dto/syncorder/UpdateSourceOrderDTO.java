package com.rondaful.cloud.order.model.dto.syncorder;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Blade
 * @date 2019-07-31 11:18:55
 **/
public class UpdateSourceOrderDTO implements Serializable {
    private static final long serialVersionUID = 1193024632228312008L;

    @ApiModelProperty("平台订单ID")
    private String orderId;

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

    public Integer getConverSysStatus() {
        return converSysStatus;
    }

    public void setConverSysStatus(Integer converSysStatus) {
        this.converSysStatus = converSysStatus;
    }
}
