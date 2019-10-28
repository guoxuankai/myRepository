package com.brandslink.cloud.logistics.thirdLogistics.bean.YunTu;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "YunTuOrder")
public class YunTuOrder implements Serializable {

    @NotBlank(message = "客户订单号不能为空")
    @Size(min = 1, max = 50, message = "客户订单号字符个数必须在1和50之间")
    @JsonProperty(value = "customerOrderNumber")
    @ApiModelProperty(value = "客户订单号")
    private String CustomerOrderNumber;

    @NotBlank(message = "运输方式代码不能为空")
    @Size(min = 1, max = 50, message = "运输方式代码字符个数必须在1和50之间")
    @JsonProperty(value = "shippingMethodCode")
    @ApiModelProperty(value = "运输方式代码")
    private String ShippingMethodCode;

    @Size(max = 50, message = "包裹跟踪号字符个数必须小于50个")
    @JsonProperty(value = "trackingNumber")
    @ApiModelProperty(value = "包裹跟踪号")
    private String TrackingNumber;

    @Size(max = 50, message = "包裹跟踪号字符个数必须小于50个")
    @JsonProperty(value = "transactionNumber")
    @ApiModelProperty(value = "平台交易号（wish邮）")
    private String TransactionNumber;

    @JsonProperty(value = "length")
    @ApiModelProperty(value = "预估包裹单边长，单位cm，非必填，默认1")
    private Integer Length;

    @JsonProperty(value = "width")
    @ApiModelProperty(value = "预估包裹单边宽，单位cm，非必填，默认1")
    private Integer Width;

    @JsonProperty(value = "height")
    @ApiModelProperty(value = "预估包裹单边高，单位cm，非必填，默认1")
    private Integer Height;

    @NotNull(message = "运单包裹的件数不能为空，必须大于0的整数")
    @Min(value = 1,message = "运单包裹的件数，必须大于0的整数")
    @JsonProperty(value = "packageCount")
    @ApiModelProperty(value = "运单包裹的件数，必须大于0的整数")
    private Integer PackageCount;

    @Digits(integer = 18, fraction = 3, message = "预估包裹总重量限制为18位整数3位小数")
    @NotNull(message = "预估包裹总重量不能为空，单位kg,最多3位小数")
    @JsonProperty(value = "weight")
    @ApiModelProperty(value = "预估包裹总重量，单位kg,最多3位小数")
    private BigDecimal Weight;

    @Valid
    @NotNull(message = "收件人信息不能为空")
    @JsonProperty(value = "receiver")
    @ApiModelProperty(value = "收件人信息")
    private YunTuReceiver Receiver;

    @Valid
    @JsonProperty(value = "sender")
    @ApiModelProperty(value = "发件人信息")
    private YunTuSender Sender;

    @JsonProperty(value = "applicationType")
    @ApiModelProperty(value = "申报类型,用于打印CN22，1-Gift,2-Sameple,3-Documents,4-Others,默认4-Other")
    private Integer ApplicationType;

    @JsonProperty(value = "returnOption")
    @ApiModelProperty(value = "是否退回,包裹无人签收时是否退回，1-退回，0-不退回，默认0")
    private Boolean ReturnOption;

    @JsonProperty(value = "tariffPrepay")
    @ApiModelProperty(value = "关税预付服务费，1-参加关税预付，0-不参加关税预付，默认0 (渠道需开通关税预付服务)")
    private Boolean TariffPrepay;

    @JsonProperty(value = "insuranceOption")
    @ApiModelProperty(value = "包裹投保类型，0-不参保，1-按件，2-按比例，默认0，表示不参加运输保险，具体参考包裹运输")
    private Integer InsuranceOption;

    @JsonProperty(value = "coverage")
    @ApiModelProperty(value = "保险的最高额度，单位RMB")
    private BigDecimal Coverage;

    @JsonProperty(value = "sensitiveTypeID")
    @ApiModelProperty(value = "包裹中特殊货品类型，可调用货品类型查询服务查询，可以不填写，表示普通货品")
    private Integer SensitiveTypeID;

    @Valid
    @NotEmpty(message = "申报信息不能为空")
    @JsonProperty(value = "parcels")
    @ApiModelProperty(value = "申报信息")
    private List<YunTuParcel> Parcels = new ArrayList<>();

    @Size(max = 3, message = "订单来源代码字符个数最大为3")
    @JsonProperty(value = "sourceCode")
    @ApiModelProperty(value = "订单来源代码")
    private String SourceCode;

    @Valid
    @JsonProperty(value = "childOrders")
    @ApiModelProperty(value = "箱子明细信息，FBA订单必填")
    private List<YunTuChildOrder> ChildOrders = new ArrayList<>();

    /**
     * 以下为运单返回字段
     */

    @JsonProperty(value = "trackType")
    @ApiModelProperty(value = "1-已产生跟踪号，2-等待后续更新跟踪号,3-不需要跟踪号")
    private String TrackType;

    @JsonProperty(value = "requireSenderAddress")
    @ApiModelProperty(value = "0-不需要分配地址，1-需要分配地址")
    private Integer RequireSenderAddress;

    @JsonProperty(value = "agentNumber")
    @ApiModelProperty(value = "代理单号")
    private String AgentNumber;

    @JsonProperty(value = "wayBillNumber")
    @ApiModelProperty(value = "物流系统运单号")
    private String WayBillNumber;

    @JsonProperty(value = "ShipperBox")
    @ApiModelProperty(value = "箱子信息")
    private List<YunTuShipperBox> ShipperBoxs = new ArrayList<>();


    @JsonProperty(value = "orderNumber")
    @ApiModelProperty(value = "客户订单号")
    private String OrderNumber;

    @JsonProperty(value = "status")
    @ApiModelProperty(value = "订单状态：3-已提交，4-已收货，5-发货运输中，6-已删除，7-已退回，8-待转单，9-退货在仓，10-已签收")
    private Integer Status;

    @JsonProperty(value = "packageNumber")
    @ApiModelProperty(value = "运单的包裹件数")
    private Integer PackageNumber;

    @JsonProperty(value = "insuranceType")
    @ApiModelProperty(value = "包裹投保类型，0-不参保，1-按件，2-按比例")
    private Integer InsuranceType;

    @Digits(integer = 18, fraction = 2)
    @JsonProperty(value = "insureAmount")
    @ApiModelProperty(value = "保险的最高额度，单位RMB")
    private BigDecimal InsureAmount;
}
