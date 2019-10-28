package com.brandslink.cloud.logistics.thirdLogistics.bean.MiaoXin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "MiaoXinOrder")
public class MiaoXinOrder implements Serializable {
    /**
     * 重量单位是kg
     * 金额单位是USD
     */

    @Valid
    @NotEmpty(message = "运单详情不能为空")
    @ApiModelProperty(value = "运单详情")
    private List<OrderInvoice> orderInvoiceParam = new ArrayList<>();

    @ApiModelProperty(value = "买家ID")
    private String buyerid;

    @NotBlank(message = "收件地址街道，必填")
    @ApiModelProperty(value = "收件地址街道，必填")
    private String consignee_address;

    @ApiModelProperty(value = "城市")
    private String consignee_city;

    @ApiModelProperty(value = "邮箱，选填")
    private String consignee_email;

    @NotBlank(message = "手机号,必填")
    @ApiModelProperty(value = "手机号,选填。为方便派送最好填写")
    private String consignee_mobile;

    @NotBlank(message = "收件人,必填")
    @ApiModelProperty(value = "收件人,必填")
    private String consignee_name;

    @NotBlank(message = "邮编，有邮编的国家必填")
    @ApiModelProperty(value = "邮编，有邮编的国家必填")
    private String consignee_postcode;

    @ApiModelProperty(value = "州/省")
    private String consignee_state;

    @NotBlank(message = "收件电话，必填")
    @ApiModelProperty(value = "收件电话，必填")
    private String consignee_telephone;

    @NotBlank(message = "收件国家二字代码，必填")
    @ApiModelProperty(value = "收件国家二字代码，必填")
    private String country;

    @NotBlank(message = "客户ID，必填")
    @ApiModelProperty(value = "客户ID，必填")
    private String customer_id;

    @NotBlank(message = "登录人ID，必填")
    @ApiModelProperty(value = "登录人ID，必填")
    private String customer_userid;

    @NotBlank(message = "原单号，必填")
    @ApiModelProperty(value = "原单号，必填")
    private String order_customerinvoicecode;

    @ApiModelProperty(value = "退回标志，默认N表示不退回，Y标表示退回。中邮可以忽略该属性")
    private String order_returnsign;

    @ApiModelProperty(value = "产品销售地址")
    private String order_transactionurl;

    @NotBlank(message = "运输方式ID，必填")
    @ApiModelProperty(value = "运输方式ID，必填")
    private String product_id;

    @ApiModelProperty(value = "图片地址，多图片地址用分号隔开")
    private String product_imagepath;

    @ApiModelProperty(value = "发件人地址1，选填")
    private String shipper_address1;

    @ApiModelProperty(value = "发件人地址2,选填")
    private String shipper_address2;

    @ApiModelProperty(value = "发件人地址3,选填")
    private String shipper_address3;

    @ApiModelProperty(value = "发件人城市，选填")
    private String shipper_city;

    @ApiModelProperty(value = "发件人公司名,选填")
    private String shipper_companyname;

    @ApiModelProperty(value = "发件人国家，选填")
    private String shipper_country;

    @ApiModelProperty(value = "发件人姓名,选填")
    private String shipper_name;

    @ApiModelProperty(value = "发件人邮编,选填")
    private String shipper_postcode;

    @ApiModelProperty(value = "发件人州，选填")
    private String shipper_state;

    @ApiModelProperty(value = "发件人电话，选填")
    private String shipper_telephone;

    @NotBlank(message = "交易类型，必填")
    @ApiModelProperty(value = "Trade_type可选值：SUMAI 速脉ERP、QQZS 全球助手、WDJL 网店精灵、IEBAY365 IEBAY365、STOMS 赛兔OMS、TTERP 通途ERP、" +
            "MGDZ 芒果店长、LRERP 懒人erp、SUMOOL 速猫ERP、GLBPAY 上海九赢、DXM 店小秘、ZYXT 客户自用系统/其他不在列表中的均使用该代码")
    private String trade_type;

    @ApiModelProperty(value = "总重(kg)，选填，如果sku上有单重可不填该项")
    private String weight;

}
