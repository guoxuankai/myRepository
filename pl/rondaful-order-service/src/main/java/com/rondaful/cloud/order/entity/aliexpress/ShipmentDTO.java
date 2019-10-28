package com.rondaful.cloud.order.entity.aliexpress;

import lombok.Data;

import java.io.Serializable;
@Data
public class ShipmentDTO implements Serializable {
    private static final long serialVersionUID = -7111856003409135677L;
    /**
     * 国际运单号
     */
    private String logisticsNo;
    /**
     * 跟踪网址
     */
    private String trackingWebSite;
    /**
     * 物流服务名称
     */
    private String serviceName;
}