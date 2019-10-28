package com.brandslink.cloud.logistics.thirdLogistics.bean.SunYou;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("SunYouPackage")
public class SunYouPackage implements Serializable {

    @NotEmpty(message = "包裹商品数据对象集合不能为空")
    @ApiModelProperty(value = "包裹商品数据对象集合")
    List<SunYouProduct> productList = new ArrayList<>();

    @Size(max = 50, message = "客户订单号字符长度最大为50")
    @NotBlank(message = "客户订单号必填")
    @ApiModelProperty(value = "客户订单号，由客户自定义，同一客户不 允许重复。")
    private String customerOrderNo;

    @Size(max = 50, message = "客户参考号字符长度最大为50")
    @ApiModelProperty(value = "客户参考号，由客户定义，允许重复。")
    private String customerReferenceNo;

    @Size(max = 30, message = "承运人追踪号码字符长度最大为30")
    @ApiModelProperty(value = "承运人追踪号码，通常由顺友提供，已从 顺友获取批量备用追踪号的客户可以直 接提供，顺友系统会校验其有效性，无效 将返回错误信息。")
    private String trackingNumber;

    @Size(max = 20, message = "邮寄方式编码字符长度最大为20")
    @NotBlank(message = "邮寄方式编码必填")
    @ApiModelProperty(value = "邮寄方式编码 可通过 findShippingMethods 接口获取。")
    private String shippingMethodCode;

    @Digits(integer = 18, fraction = 2, message = "包裹总价值（单位：USD）限制为18位整数2位小数")
    @ApiModelProperty(value = "包裹总价值（单位：USD） 系统接收后自动四舍五入至 2 位小数")
    private BigDecimal packageSalesAmount;

    @Digits(integer = 18, fraction = 2, message = "包裹长度（单位：cm）限制为18位整数2位小数")
    @ApiModelProperty(value = "包裹长度（单位：cm） 系统接收后自动四舍五入至 2 位小数")
    private BigDecimal packageLength;

    @Digits(integer = 18, fraction = 2, message = "包裹宽度（单位：cm）限制为18位整数2位小数")
    @ApiModelProperty(value = "包裹宽度（单位：cm） 系统接收后自动四舍五入至 2 位小数")
    private BigDecimal packageWidth;

    @Digits(integer = 18, fraction = 2, message = "包裹高度（单位：cm）限制为18位整数2位小数")
    @ApiModelProperty(value = "包裹高度（单位：cm） 系统接收后自动四舍五入至 2 位小数")
    private BigDecimal packageHeight;

    @NotNull(message = "包裹总重量（单位：kg）不能为空")
    @Digits(integer = 18, fraction = 3, message = "包裹总重量（单位：kg）限制为18位整数3位小数")
    @ApiModelProperty(value = "包裹总重量（单位：kg） 系统接收后自动四舍五入至 3 位小数")
    private BigDecimal predictionWeight;

    @Size(max = 64, message = "收件人姓名字符长度最大为64")
    @NotBlank(message = "收件人姓名必填")
    @ApiModelProperty(value = "收件人姓名")
    private String recipientName;

    @Size(max = 2, message = "收件人国家二字代码字符长度最大为2")
    @NotBlank(message = "收件人国家二字代码必填")
    @ApiModelProperty(value = "收件人国家二字代码")
    private String recipientCountryCode;

    @Size(max = 32, message = "收件人邮编字符长度最大为32")
    @NotBlank(message = "收件人邮编必填")
    @ApiModelProperty(value = "收件人邮编")
    private String recipientPostCode;

    @Size(max = 64, message = "收件人省州字符长度最大为64")
    @ApiModelProperty(value = "收件人省州")
    private String recipientState;

    @Size(max = 64, message = "收件人城市字符长度最大为64")
    @ApiModelProperty(value = "收件人城市")
    private String recipientCity;

    @Size(max = 200, message = "收件人街道字符长度最大为200")
    @NotBlank(message = "收件人街道必填")
    @ApiModelProperty(value = "收件人街道")
    private String recipientStreet;

    @Size(max = 32, message = "收件人电话字符长度最大为32")
    @ApiModelProperty(value = "收件人电话")
    private String recipientPhone;

    @Size(max = 32, message = "收件人手机字符长度最大为32")
    @ApiModelProperty(value = "收件人手机")
    private String recipientMobile;

    @Size(max = 128, message = "收件人邮箱字符长度最大为128")
    @ApiModelProperty(value = "收件人邮箱")
    private String recipientEmail;

    @Size(max = 64, message = "发件人姓名字符长度最大为64")
    @ApiModelProperty(value = "发件人姓名")
    private String senderName;

    @Size(max = 32, message = "发件人电话字符长度最大为32")
    @ApiModelProperty(value = "发件人电话")
    private String senderPhone;

    @Size(max = 32, message = "发件人邮编字符长度最大为32")
    @ApiModelProperty(value = "发件人邮编")
    private String senderPostCode;

    @Size(max = 200, message = "发件人完整地址字符长度最大为200")
    @ApiModelProperty(value = "发件人完整地址")
    private String senderFullAddress;

    @Size(max = 200, message = "发件人街道地址字符长度最大为200")
    @ApiModelProperty(value = "发件人街道地址")
    private String senderAddress;

    @Size(max = 2, message = "发件人国家代码字符长度最大为2")
    @ApiModelProperty(value = "发件人国家代码")
    private String senderCountryCode;

    @Size(max = 64, message = "发件人省州字符长度最大为64")
    @ApiModelProperty(value = "发件人省州")
    private String senderState;

    @Size(max = 64, message = "发件人城市字符长度最大为64")
    @ApiModelProperty(value = "发件人城市")
    private String senderCity;

    @Size(max = 64, message = "发件人区/县字符长度最大为64")
    @ApiModelProperty(value = "发件人区/县")
    private String senderDistrict;

    @Size(max = 128, message = "发件人邮编字符长度最大为128")
    @ApiModelProperty(value = "发件人邮编")
    private String senderEmail;

    @Size(max = 1, message = "是否投保字符长度最大为1")
    @NotBlank(message = "是否投保必填")
    @ApiModelProperty(value = "是否投保 0：不投保 1：投保 如果选择了投保，那么投保总价值为海关 申报总价值。 如果当前邮寄方式不支持投保，本参数将 被忽略。")
    private String insuranceFlag;

    @Size(max = 3, message = "包裹属性字符长度最大为3")
    @ApiModelProperty(value = "包裹属性，例如：“011”、“210”。如果包 裹没有任何属性请填入 000 或者不填。 第一位 0：不含电池 1：含电池 2：纯电池 第二位 0：不含液体及粉末 1：含液体或粉末 第三位 0：不是食品 1：是食品")
    private String packageAttributes;
}
