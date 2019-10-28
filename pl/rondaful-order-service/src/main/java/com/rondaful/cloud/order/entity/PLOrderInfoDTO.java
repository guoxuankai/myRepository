package com.rondaful.cloud.order.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ProjectName: Rondaful
 * @Package: com.rondaful.cloud.order.entity
 * @ClassName: PLOrderInfoDTO
 * @Author: Superhero
 * @Description: 内部供财务系统调用
 * @Date: 2019/7/30 10:29
 */
@ApiModel(value = "内部供财务系统调用类")
@Data
public class PLOrderInfoDTO {
    @ApiModelProperty(value = "供应商费用")
    private BigDecimal supplierShipFee;
    @ApiModelProperty(value = "是否包邮： 0,不包邮 1,包邮  ")
    private Integer freeFreight;
    @ApiModelProperty(value = "品连采购订单编号")
    private String sysOrderId;
    @ApiModelProperty(value = "包裹号")
    private String orderTrackId;
    @ApiModelProperty(value = "品连SKU")
    private String sku;
    @ApiModelProperty(value = "供应商SKU")
    private String supplierSku;
    @ApiModelProperty(value = "品连采购商品中文名称")
    private String itemName;
    @ApiModelProperty(value = "品连采购商品英文名称")
    private String itemNameEn;
    @ApiModelProperty(value = "品连采购商品单价")
    private BigDecimal itemPrice;
    @ApiModelProperty(value = "品连采购商品数量")
    private Integer skuQuantity;
    @ApiModelProperty(value = "品连采购商品总额")
    private BigDecimal totalPrice;
    @ApiModelProperty(value = "品连采购物流费")
    private BigDecimal sellerShipFee;
    @ApiModelProperty(value = "品连采购退款金额")
    private BigDecimal refundMoney;
    @ApiModelProperty(value = "品连采购订单总额")
    private BigDecimal orderTotal;
}
