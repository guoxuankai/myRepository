package com.rondaful.cloud.order.entity.erpentity;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value ="OrderWareHouseDeliverCallBack")
public class WareHouseDeliverCallBack implements Serializable {
    @ApiModelProperty(value = "仓库类型：ERP/GC")
    private String warehouseType;

    @ApiModelProperty(value = "ERP发货订单跟踪号，用于重复推单和跟踪订单,此sysOrderId对应系统订单表中order_track_id",required = true)
    private String orderTrackId;

    @ApiModelProperty(value = "当前进度:order_push - 入库通知，distribution - 分配库存，package_upload_status –物流下单，" +
            "package_confirm_status - 物流交运，packing_time – 包裹包装，shipping_time – 发货, received_good - 已收货",required = true)
    private String speed;

    @ApiModelProperty(value = "进度时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:sss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:sss")
    private String updateTime;

    @ApiModelProperty(value = "跟踪单号")
    private String shipTrackNumber;

    @ApiModelProperty(value = "物流商单号")
    private String shipOrderId;

    @ApiModelProperty(value = "实际物流费")
    private BigDecimal actualShipCost;

    @ApiModelProperty(value = "仓库发货异常信息，附带异常发生原因")
    private String warehouseShipException;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus = "0";
}
