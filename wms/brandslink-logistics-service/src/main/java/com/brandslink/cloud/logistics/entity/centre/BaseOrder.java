package com.brandslink.cloud.logistics.entity.centre;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
@ApiModel(value = "物流商公共运单对象")
public class  BaseOrder {

    @NotBlank(message = "运输方式不能为空")
    @ApiModelProperty(value = "运输方式编码")
    private String logisticsMethodCode;

    @NotBlank(message = "物流商编码不能为空")
    @ApiModelProperty(value = "物流商编码")
    private String logisticsCode;

    @NotBlank(message = "客户订单号不能为空")
    @ApiModelProperty(value = "客户订单号")
    private String customerOrderNumber;

    @Valid
    @NotEmpty(message = "申报信息不能为空")
    @ApiModelProperty(value = "申报信息")
    List<BaseOrderChild> childs;

    /**
     * 收件人信息
     */
    @NotBlank(message = "收件人国家不能为空")
    @ApiModelProperty(value = "收件人所在国家，填写国际通用标准2位简码，可通过国家查询服务查询")
    private String consigneeCountryCode;

    @NotBlank(message = "收件人姓名不能为空")
    @ApiModelProperty(value = "收件人姓")
    private String consigneeFirstName;

    @NotBlank(message = "收件人姓名不能为空")
    @ApiModelProperty(value = "收件人名")
    private String consigneeLastName;

    @ApiModelProperty(value = "收件人公司名称，选填")
    private String consigneeCompany;

    @NotBlank(message = "收件人详细地址不能为空")
    @ApiModelProperty(value = "收件人详细地址")
    private String consigneeStreet;

    @NotBlank(message = "收件人所在城市不能为空")
    @ApiModelProperty(value = "收件人所在城市")
    private String consigneeCity;

    @NotBlank(message = "收件人省/州不能为空")
    @ApiModelProperty(value = "收件人省/州")
    private String consigneeState;

    @NotBlank(message = "收件人编码不能为空")
    @ApiModelProperty(value = "收件人邮编")
    private String consigneeZip;

    @NotBlank(message = "收件人电话不能为空")
    @ApiModelProperty(value = "收件人电话")
    private String consigneePhone;

    /**
     * 发件人信息(选填)
     */
    @ApiModelProperty(value = "发件人所在国家，填写国际通用标准2位简码，可通过国家查询服务查询")
    private String consignorCountryCode;

    @ApiModelProperty(value = "发件人姓")
    private String consignorFirstName;

    @ApiModelProperty(value = "发件人名")
    private String consignorLastName;

    @ApiModelProperty(value = "发件人公司名称")
    private String consignorCompany;

    @ApiModelProperty(value = "发件人详细地址")
    private String consignorStreet;

    @ApiModelProperty(value = "发件人所在城市")
    private String consignorCity;

    @ApiModelProperty(value = "发件人省/州")
    private String consignorState;

    @ApiModelProperty(value = "发件人邮编")
    private String consignorZip;

    @ApiModelProperty(value = "发件人电话")
    private String consignorPhone;


}
