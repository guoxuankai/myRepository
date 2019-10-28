package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.entity
 * @ClassName: OrderInfoVO
 * @Author: Superhero
 * @Description: 供供应商服务调用
 * @Date: 2019/8/23 16:20
 */
@Data
@ApiModel(value = "供供应商服务调用对象")
public class OrderInfoVO {
    @ApiModelProperty(value = "来源订单号集合")
    private List<String> platformOrderIdList;
    @ApiModelProperty(value = "订单发货仓库名称")
    private String deliveryWarehouse;
    @ApiModelProperty(value = "卖家平台店铺ID")
    private Integer platformShopId;
}
