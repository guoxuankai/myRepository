package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.entity
 * @ClassName: WarehouseShipExceptionVo
 * @Author: Superhero
 * @Description:
 * @Date: 2019/7/23 16:35
 */
@Data
@ApiModel
public class WarehouseShipExceptionVo {

    @ApiModelProperty(value = "订单包裹号")
    private String orderTrackId;

    @ApiModelProperty(value = "发货异常信息")
    private String warehouseShipException;

}
