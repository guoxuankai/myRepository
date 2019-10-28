package com.rondaful.cloud.order.entity.aliexpress;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TradeListDTO implements Serializable {
    private static final long serialVersionUID = 2342384118482098892L;

    /**
     * 声明发货类型，all表示全部发货，part表示部分声明发货。
     */
    private String sendType;
    /**
     * 子订单序号，从1开始
     */
    private Integer orderIndex;
    /**
     * 物流列表
     */
    List<ShipmentDTO> shipmentList;
}
