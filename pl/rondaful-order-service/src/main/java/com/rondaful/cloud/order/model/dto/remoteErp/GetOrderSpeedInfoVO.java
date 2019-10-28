package com.rondaful.cloud.order.model.dto.remoteErp;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Blade
 * @date 2019-08-04 10:27:33
 **/
public class GetOrderSpeedInfoVO implements Serializable {
    private static final long serialVersionUID = -3985764745850449051L;

    private String speed;
    private String error;
    private String channel_order_number;
    private String shipping_number;
    private String process_code;
    private BigDecimal shipping_fee;
    private Long order_status;

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getChannel_order_number() {
        return channel_order_number;
    }

    public void setChannel_order_number(String channel_order_number) {
        this.channel_order_number = channel_order_number;
    }

    public String getShipping_number() {
        return shipping_number;
    }

    public void setShipping_number(String shipping_number) {
        this.shipping_number = shipping_number;
    }

    public String getProcess_code() {
        return process_code;
    }

    public void setProcess_code(String process_code) {
        this.process_code = process_code;
    }

    public BigDecimal getShipping_fee() {
        return shipping_fee;
    }

    public void setShipping_fee(BigDecimal shipping_fee) {
        this.shipping_fee = shipping_fee;
    }

    public Long getOrder_status() {
        return order_status;
    }

    public void setOrder_status(Long order_status) {
        this.order_status = order_status;
    }
}
