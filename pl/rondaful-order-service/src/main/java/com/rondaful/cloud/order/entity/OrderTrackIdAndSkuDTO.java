package com.rondaful.cloud.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderTrackIdAndSkuDTO implements Serializable {

    private String orderTrackId;

    private String commoditySku;

}