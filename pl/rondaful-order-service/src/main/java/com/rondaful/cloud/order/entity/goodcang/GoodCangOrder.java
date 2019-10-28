package com.rondaful.cloud.order.entity.goodcang;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "GoodCangOrder")
public class GoodCangOrder {

    private String warehouseId;//TODO 仓库ID 内部自用

    @ApiModelProperty(value = "订单参考号(建议使用平台单号)", required = true)
    private String reference_no;//string     Require      订单参考号(建议使用平台单号)
    @ApiModelProperty(value = "平台，ALIEXPRESS,AMAZON,WISH,EBAY,OTHER默认OTHER")
    private String platform;//string     Option      平台，ALIEXPRESS,AMAZON,WISH,EBAY,OTHER默认OTHER
    @ApiModelProperty(value = "配送方式，参考getShippingMethod,FBA 类型订单必填", required = true)
    private String shipping_method;//string     Require      配送方式，参考getShippingMethod,FBA 类型订单必填
    @ApiModelProperty(value = "配送仓库，参考getWarehouse", required = true)
    private String warehouse_code;//string     Require      配送仓库，参考getWarehouse
    @ApiModelProperty(value = "收件人国家，参考getCountry", required = true)
    private String country_code;//string     Require      收件人国家，参考getCountry
    @ApiModelProperty(value = "省", required = true)
    private String province;//string     Require      省
    @ApiModelProperty(value = "城市", required = true)
    private String city;//string     Require      城市
    @ApiModelProperty(value = "公司名称")
    private String company;//string     Option      公司名称
    @ApiModelProperty(value = "地址1", required = true)
    private String address1;//string     Require      地址1
    @ApiModelProperty(value = "地址2")
    private String address2;//string     Option      地址2
    @ApiModelProperty(value = "邮编", required = true)
    private String zipcode;//string     Require      邮编
    @ApiModelProperty(value = "门牌号")
    private String doorplate;//string     Option      门牌号
    @ApiModelProperty(value = "收件人姓名", required = true)
    private String name;//string     Require      收件人姓名
    @ApiModelProperty(value = "分机号")
    private String cell_phone;//string     Option      分机号
    @ApiModelProperty(value = "收件人联系方式", required = true)
    private String phone;//string     Require      收件人联系方式
    @ApiModelProperty(value = "收件人邮箱")
    private String email;//string     Option      收件人邮箱
    @ApiModelProperty(value = "订单备注")
    private String order_desc;//string     Option      订单备注
    @ApiModelProperty(value = "是否直接审核,0新建不审核(草稿状态)，1新建并审核，默认为0， 审核通过之后，不可编辑")
    private Integer verify;//int     Option      是否直接审核,0新建不审核(草稿状态)，1新建并审核，默认为0， 审核通过之后，不可编辑
    @ApiModelProperty(value = "派送方式不允许修改 1：不允许修改 ；0：可以修改 默认1 ：不允许修改 注：时效刷选时是否允许修改")
    private Integer is_shipping_method_not_allow_update;//int      Option      派送方式不允许修改 1：不允许修改 ；0：可以修改 默认1 ：不允许修改 注：时效刷选时是否允许修改
    @ApiModelProperty(value = "签名服务 1：签名服务 0:b不选择签名服务, 不填默认为0")
    private Integer is_signature;//int      Option      签名服务 1：签名服务 0:b不选择签名服务, 不填默认为0
    @ApiModelProperty(value = "保险服务, 0：不需要，1：需要，不填默认为0")
    private Integer is_insurance;//int      Option      保险服务, 0：不需要，1：需要，不填默认为0
    @ApiModelProperty(value = "保额, 不填为0")
    private Integer insurance_value;//int      Option      保额, 不填为0
    @ApiModelProperty(value = "FBA Shipment ID FBA 类型订单必填")
    private String fba_shipment_id;//string      Option      FBA Shipment ID FBA 类型订单必填
    @ApiModelProperty(value = "FBA Shipment ID创建时间 FBA 类型订单必填")
    private String fba_shipment_id_create_time;//string      Option      FBA Shipment ID创建时间 FBA 类型订单必填
    @ApiModelProperty(value = "FBA换标服务 1换标，0不换标(不填，默认为1)")
    private Integer is_change_label;//int      Option      FBA换标服务 1换标，0不换标(不填，默认为1)
    @ApiModelProperty(value = "年龄检测服务 0不检测，如需要该服务只可填16或18，其他值默认为0(不填，默认为0)")
    private Integer age_detection;//int      Option      年龄检测服务 0不检测，如需要该服务只可填16或18，其他值默认为0(不填，默认为0)
    @ApiModelProperty(value = "订单明细", required = true)
    private List<GoodCangOrderItem> items;//Object      Require      订单明细
}
