package com.rondaful.cloud.order.model.xingShang.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单收货地址表
 * 实体类对应的数据表为：  tb_sys_order_receive_address
 * @author chenjiangxin
 * @date 2019-07-18 17:20:20
 */
@ApiModel(value ="SysOrderReceiveAddress")
@Data
public class SysOrderReceiveAddressXS implements Serializable {

    private static final long serialVersionUID = -7825296836392743447L;
    @ApiModelProperty(value = "系统订单ID")
    private String sysOrderId;

    @ApiModelProperty(value = "收货人姓名")
    private String shipToName;

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

    @ApiModelProperty(value = "收货地址3")
    private String shipToAddrStreet3;

    @ApiModelProperty(value = "收货邮编")
    private String shipToPostalCode;

    @ApiModelProperty(value = "收货人电话")
    private String shipToPhone;

    @ApiModelProperty(value = "收货人email")
    private String shipToEmail;
}