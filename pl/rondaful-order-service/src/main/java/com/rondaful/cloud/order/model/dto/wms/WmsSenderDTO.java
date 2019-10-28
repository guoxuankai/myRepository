package com.rondaful.cloud.order.model.dto.wms;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 寄件人信息
 *
 * @author Blade
 * @date 2019-08-12 14:08:51
 **/
public class WmsSenderDTO implements Serializable {
    private static final long serialVersionUID = 5193032213340649406L;

    @ApiModelProperty(value = "国家二字码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "省/州")
    private String state;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "详情地址1")
    private String addressOne;

    @ApiModelProperty(value = "详情地址2")
    private String addressTwo;

    @ApiModelProperty(value = "移动电话[与固定电话二选一]")
    private String mobilePhone;

    @ApiModelProperty(value = "固定电话[与移动电话二选一]")
    private String fixationPhone;

    @ApiModelProperty(value = "姓")
    private String sur;

    @ApiModelProperty(value = "名")
    private String name;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "买家id")
    private String buyerId;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "邮编")
    private String postCode;
}
