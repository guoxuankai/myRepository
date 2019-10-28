package com.rondaful.cloud.order.model.xingShang.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author: 闵功伟
 * @description:
 * @date: 2019/06/03
 */
@Data
@ApiModel(value = "SysOrderXS")
public class SysOrderXS implements Serializable {

    private static final long serialVersionUID = 3018505487654067377L;

    @ApiModelProperty(value = "系统订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "订单发货状态:1-待付款,2-缺货,3-待发货,4-已拦截,5-已发货,6-部分发货,7-已作废,8-已完成")
    private Byte orderDeliveryStatus;

    @ApiModelProperty(value = "该单是否有效:0有效,1无效")
    private Byte isValid;

    @ApiModelProperty(value = "卖家平台店铺账号")
    private String platformShopAccount;

    @ApiModelProperty(value = "订单总售价:预估物流费+系统商品总金额")
    private BigDecimal total;

    @ApiModelProperty(value = "系统商品总价(商品单价X数量)")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "支付状态:0待支付,10冻结失败,11冻结成功,20付款中,21付款成功,22付款失败,30待补款,40已取消")
    private Byte payStatus;

    @ApiModelProperty(value = "付款时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String payTime;

    @ApiModelProperty(value = "订单创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @ApiModelProperty(value = "发货时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String deliveryTime;

    @ApiModelProperty(value = "系统订单项实体类")
    private List<OrderDetailXS> sysOrderDetails;

    @ApiModelProperty(value = "收货信息")
    private SysOrderReceiveAddressXS sysOrderReceiveAddress;

}
