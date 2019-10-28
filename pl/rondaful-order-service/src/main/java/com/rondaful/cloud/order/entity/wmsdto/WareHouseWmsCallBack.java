package com.rondaful.cloud.order.entity.wmsdto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value ="OrderWareHouseDeliverCallBack")
public class WareHouseWmsCallBack implements Serializable {
    @ApiModelProperty(value = "仓库类型：ERP/GC")
    private String warehouseType;

    @ApiModelProperty(value = "WMS包裹号，对应系统订单表中order_track_id",required = true)
    private String packageNum;

    @ApiModelProperty(value = "发货状态[4:发货完成 5:发货失败]")
    private String orderStatus;

    @ApiModelProperty(value = "当前进度:order_push - 入库通知，distribution - 分配库存，package_upload_status –物流下单，" +
            "package_confirm_status - 物流交运，packing_time – 包裹包装，shipping_time – 发货, received_good - 已收货")
    private String speed;

    @ApiModelProperty(value = "进度时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:sss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:sss")
    private String deliveryTime;

    @ApiModelProperty(value = "跟踪单号")
    private String trackingNum;

    @ApiModelProperty(value = "物流商单号")
    private String waybillNum;

    @ApiModelProperty(value = "实际物流费")
    private BigDecimal actualShipCost = BigDecimal.ZERO;

    @ApiModelProperty(value = "仓库发货异常信息，附带异常发生原因")
    private String receiverMark;
}
