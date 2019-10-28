package com.rondaful.cloud.order.entity.syncorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 待转单的订单数据
 *
 * @author Blade
 * @date 2019-07-09 14:21:34
 **/
public class PreConvertOrderDTO {

    @ApiModelProperty(value = "来源订单ID")
    private String sourceOrderId;

    @ApiModelProperty(value = "平台订单总价")
    private String platformTotal;

    @ApiModelProperty(value = "下单时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String orderTime;

    @ApiModelProperty(value = "卖家平台店铺ID")
    private Integer platformShopId;

    @ApiModelProperty(value = "卖家平台店铺名")
    private String platformSellerAccount;

    @ApiModelProperty(value = "卖家平台订单销售记录号")
    private String recordNumber;

    @ApiModelProperty(value = "买家ID")
    private String buyerUserId;

    @ApiModelProperty(value = "收货人email")
    private String shipToEmail;

    @ApiModelProperty(value = "卖家填的物流费")
    private String shippingServiceCostStr;

    @ApiModelProperty(value = "仓库返回订单号")
    private String referenceId;

    @ApiModelProperty(value = "买家留言")
    private String buyerCheckoutMessage;

    @ApiModelProperty(value = "收货人姓名")
    private String shipToName;

    @ApiModelProperty(value = "收货人电话")
    private String shipToPhone;

    @ApiModelProperty(value = "收货目的地/国家代码")
    private String shipToCountry;

    @ApiModelProperty(value = "收货目的地/国家名称")
    private String shipToCountryName;

    @ApiModelProperty(value = "收货省/州名")
    private String shipToState;

    @ApiModelProperty(value = "收货城市")
    private String shipToCity;

    @ApiModelProperty(value = "收货地址1")
    private String shipToAddrStreet1;

    @ApiModelProperty(value = "收货地址2")
    private String shipToAddrStreet2;

    @ApiModelProperty(value = "收货邮编")
    private String shipToPostalCode;

    @ApiModelProperty(value = "卖家品连ID")
    private Integer sellerPlId;

    @ApiModelProperty(value = "卖家品连账号")
    private String sellerPlAccount;

    @ApiModelProperty(value = "最迟发货时间(取所有子单最早时间)")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private String deliverDeadline;
}
