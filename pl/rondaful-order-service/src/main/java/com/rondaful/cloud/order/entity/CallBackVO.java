package com.rondaful.cloud.order.entity;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "售后订单发货回调对象")
public class CallBackVO implements Serializable{

	private String trackingId;
	private String status;
	private String warehouseShipException;
	private String logisticsCost;
	private String shipNumber;
}
