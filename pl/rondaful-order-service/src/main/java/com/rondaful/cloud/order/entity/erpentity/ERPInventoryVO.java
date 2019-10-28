package com.rondaful.cloud.order.entity.erpentity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ERPInventoryVO {

    @ApiModelProperty(value = "预警库存")
    private Integer alert_quantity;
    @ApiModelProperty(value = "调拨在途库存")
    private Integer allocating_quantity;
    @ApiModelProperty(value = "可用库存")
    private Integer available_quantity;
    @ApiModelProperty(value = "故障品库存")
    private Integer defects_quantity;
    @ApiModelProperty(value = "在途库存")
    private Integer instransit_quantity;
    @ApiModelProperty(value = "库存数")
    private Integer quantity;
    @ApiModelProperty(value = "SKU")
    private String sku;
    @ApiModelProperty(value = "图片URL")
    private String thumb;
    @ApiModelProperty(value = "更新时间")
    private Integer updated_time;
    @ApiModelProperty(value = "待发库存")
    private Integer waiting_shipping_quantity;
    @ApiModelProperty(value = "仓库代码")
    private String warehouse_code;
    @ApiModelProperty(value = "仓库名称")
    private String warehouse_name;
}
