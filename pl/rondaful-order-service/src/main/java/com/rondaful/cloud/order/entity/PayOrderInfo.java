package com.rondaful.cloud.order.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.entity
 * @ClassName: otherMergeOrderInfo
 * @Author: Superhero
 * @Description:
 * @Date: 2019/7/29 15:35
 */
@Data
public class PayOrderInfo {
    private String sysOrderId;
    private BigDecimal orderAmount;
    private BigDecimal total;
    private BigDecimal estimateShipCost;
}
